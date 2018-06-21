package com.tcredit.engine.engine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.*;
import com.tcredit.engine.constants.StrategyConstants;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.BaseResponse;
import com.tcredit.engine.response.ResponseCodeEnum;
import com.tcredit.engine.strategy.DPUSelectStrategy;
import com.tcredit.engine.strategy.impl.OrderDPUSelectStrategy;
import com.tcredit.engine.strategy.impl.PercentDPUSelectStrategy;
import com.tcredit.engine.strategy.impl.RandomDPUSelectStrategy;
import com.tcredit.engine.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-30 11:55
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-30 11:55
 * @updatedRemark:
 * @version:
 */
public class ProcessEngine {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ProcessEngine.class);


    private static final String DPU_FAIL_POLICY = "next";
    private static final int ATTEMP_COUNT = Integer.parseInt(PropertiesUtil.getString("RESULT_QUERY_ATTEMP_COUNT"));
    private static final int ATTEMP_INTERVAL_IN_MILLI = Integer.parseInt(PropertiesUtil.getString("ATTEMP_INTERVAL_IN_MILLI"));

    private static final String RLT_CODE = "code";
    private static final String RLT_MESSAGE = "message";
    private static final String RLT_GID = "gid";
    private static final String RLT_RID = "rid";

    public static String start(ProcessContextV2 cxt) throws Exception {
        String gid = String.valueOf(cxt.get(ProcessContextEnum.GID));

        LOGGER.info(String.format("gid:%s,开始查找数据模块", gid));
        String mId = String.valueOf(cxt.get(ProcessContextEnum.MODULE_ID));
        /**
         * 查找数据模块
         */
        final DataModule dataModule = ConfigManagerV2.chooseDataModule(mId);
        /**
         * 将要返回的结果配置放进上下文
         */
        Results results = dataModule.getResults();
        if (results != null) {
            cxt.put(ProcessContextEnum.RESULT, results);
        }

        /**
         * 未匹配数据模块
         */
        if (null == dataModule) {
            LOGGER.info(String.format("gid:%s,没有匹配任何数据模块，退出", gid));
            return JsonUtil.toJson(MessageUtil.convertBase2Out(new BaseResponse<>(ResponseCodeEnum.noMathedDataContainer())));
        }


        /**
         * 成功匹配数据模块，执行数据处理流程
         */
        LOGGER.info(String.format("gid:%s,成功匹配到可用的数据模块,mid:%s", gid, dataModule.getId()));

        return startDataProcessing(dataModule, cxt);

    }

    /**
     * 开始数据处理
     *
     * @param dataModule
     * @param cxt
     * @return
     */
    private static String startDataProcessing(DataModule dataModule, ProcessContextV2 cxt) {
        Map<String, Object> responseMap = new HashMap();
        try {

            if (dataModule != null && cxt != null) {
                String gid = String.valueOf(cxt.get(ProcessContextEnum.GID));
                LOGGER.info(String.format("gid:%s,开始数据处理步骤", gid));

                /**
                 * 根据策略选择处理单元
                 */
                DataProcessingUnits dpunit = dataModule.getDataProcessingUnits();
                Set<DataProcessingUnit> unitList = dpunit.getDataProcessingUnit();
                String strategy = dpunit.getStrategy();
                /**
                 * 创建选择策略
                 */
                DPUSelectStrategy selectStrategy = getStrategy(unitList, strategy);
                DataProcessingUnit unit = null;
                unit = selectStrategy.select(unit);

                boolean processUnitSuccessed = true;
                do {
                    if (dataModule != null && unit != null) {
                        processUnitSuccessed = processUnit(dataModule, unit, cxt);
                    }
                    if (!processUnitSuccessed) {
                        unit = selectStrategy.select(unit);
                    }
                }
                while (dataModule != null && unit != null && !processUnitSuccessed && DPU_FAIL_POLICY.equalsIgnoreCase(dpunit.getStrategy()));


            }
        } catch (Exception e) {
            LOGGER.error("gid:{},开启数据处理异常，异常信息:{}", cxt.get(ProcessContextEnum.GID), e);
            cxt.setExceptionFlag(true);
        }


        if (!cxt.isExceptionFlag()) {
            if (cxt.isFinished()) {
                responseMap = responseMapCheckReturn(dataModule, cxt);

            } else {
                responseMap.put(RLT_CODE, String.valueOf(ResponseCodeEnum.RESULT_FAIL));
                responseMap.put(RLT_GID, cxt.get(ProcessContextEnum.GID).toString());
                responseMap.put(RLT_MESSAGE, "失败");
            }
        } else {
            if (cxt.get(ProcessContextEnum.ERROR) != null) {
                responseMap.put(RLT_MESSAGE, "数据处理错误:" + cxt.get(ProcessContextEnum.ERROR));

            }
            responseMap.put(RLT_CODE, String.valueOf(ResponseCodeEnum.INNER_FAIL));
            responseMap.put(RLT_GID, cxt.get(ProcessContextEnum.GID).toString());
        }

        return JsonUtil.toJson(responseMap);
    }


    /**
     * 处理某个数据单元
     *
     * @param dataModule
     * @param unit
     * @param cxt
     */
    private static boolean processUnit(DataModule dataModule, DataProcessingUnit unit, ProcessContextV2 cxt) {

        unit.setMid(dataModule.getId());
        /**
         * 将cmid放进cxt
         */
        cxt.put(ProcessContextEnum.CHILD_MODULE_ID, unit.getId());
        /**
         * 将正在处理的数据处理单元信息放进cxt
         */
        Map map = JsonUtil.json2Object(JsonUtil.toJson(unit), Map.class);
        map.remove("step");
        cxt.put(ProcessContextEnum.UNIT_INFO, map);


        /**
         * 将unit里配置的所有处理步骤替换
         */
        convertUnit(unit);

        //将dataModule结果信息置入到某个step，steps为Set
        handleResultInfo2Steps(cxt, unit.getStep());


        /**
         * 对unit配置的所有step确定执行优先级
         */
        List<Set<String>> stepPriority = StepPriorityUtil.getStepPriority(unit.getStep());
        cxt.setPriority(stepPriority);

        /**
         * 将该次数据处理在哪一层返回 置入到上下文中cxt
         */
        handleWhen2BackResult(stepPriority, dataModule, cxt);

        /**
         * 开始数据处理
         */
        if (!stepPriority.isEmpty()) {
            Set<Step> steps = fetchSteps(stepPriority.get(0));

            /**
             *将dataModule结果信息置入到某个step
             */
            handleResultInfo2Steps(cxt, steps);
            ExecutorService es = cxt.getExecutorService();


            Map<String, Future<String>> rnt = Maps.newHashMap();
            for (Step step : steps) {
                Future<String> submit = es.submit(new FutureStepExcutor(step, cxt));
                rnt.put(step.getId(), submit);
            }


        } else {
            LOGGER.info("dataModule:{},unit:{},未配置任何处理步骤", dataModule.getId(), unit.getId());
        }

        /**
         * 查看数据处理是否完成
         */
        int i = 0;
        while (checkoutResultFinished(unit, cxt)) {
            if (i < ATTEMP_COUNT) {
                try {
                    Thread.sleep(ATTEMP_INTERVAL_IN_MILLI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
            i++;
        }
        if (checkoutProcessingSuccess(unit, cxt)) {
            return false;
        } else {
            //上下文中希望返回某层不为空
            if (cxt.getBackResultLayer() != null && cxt.getBackResultLayer().size() != 0) {

                //开启线程处理
                new DataProcessingExecutorThread(dataModule, unit, cxt).start();
            } else {
                //打印结果信息rid
                printProcessingInfo(cxt);
            }
            cxt.setFinished(true);
            return true;
        }

    }

    /**
     * @param unit
     * @param cxt
     * @return
     */
    private static boolean checkoutResultFinished(DataProcessingUnit unit, ProcessContextV2 cxt) {
        int allStepsCount = unit.getStep().size();
        Set<String> backResultLayer = cxt.getBackResultLayer();
        if (backResultLayer == null || backResultLayer.size() == 0) {
            return !cxt.isExceptionFlag() && allStepsCount != 0 && allStepsCount > cxt.getFinishedStepIds().size();
        } else {
            return !cxt.isExceptionFlag() && !cxt.getFinishedStepIds().containsAll(backResultLayer);
        }
    }

    /**
     * @param unit
     * @param cxt
     * @return
     */
    private static boolean checkoutProcessingSuccess(DataProcessingUnit unit, ProcessContextV2 cxt) {
        int allStepsCount = unit.getStep().size();
        Set<String> backResultLayer = cxt.getBackResultLayer();
        if (backResultLayer == null || backResultLayer.size() == 0) {
            return cxt.isExceptionFlag() || allStepsCount > cxt.getFinishedStepIds().size();
        } else {
            return cxt.isExceptionFlag() || !cxt.getFinishedStepIds().containsAll(backResultLayer);
        }
    }


    /**
     * 将该次数据处理在哪一层返回 置入到上下文中cxt
     *
     * @param stepPriority
     * @param dataModule
     * @param cxt
     */
    private static void handleWhen2BackResult(List<Set<String>> stepPriority, DataModule dataModule, ProcessContextV2 cxt) {
        if (stepPriority == null || stepPriority.size() == 0
                || dataModule == null || dataModule.getResults() == null
                || dataModule.getResults().getResults().size() == 0 || cxt == null) {
            return;
        }
        Set<String> backResultLayer = Sets.newHashSet();

//        Map<Integer, Set<String>> map = new HashMap<>();
        /**是否判断出在何处返回**/
        boolean flag = false;

        List<Result> results = dataModule.getResults().getResults();
        for (int i = stepPriority.size() - 1; i >= 0 && flag == false; i--) {
            Set<String> stepIds = stepPriority.get(i);
            for (Result result : results) {
                String stepId = result.getStepId().trim();
                if (stepIds.contains(stepId)) {

                    //map.put(i, stepIds);
                    backResultLayer.addAll(stepIds);
                    flag =true;
                    break;
                }
            }
        }
//        List<Integer> list = new ArrayList<>();
//        for (Map.Entry<Integer, Set<String>> entry : map.entrySet()) {
//            list.add(entry.getKey());
//        }

        //cxt.setBackResultLayer(map.get(MaxNubUtil.getMaxNum(list)));
         cxt.setBackResultLayer(backResultLayer);
    }

    /**
     * 将dataModule结果信息置入到某个step
     *
     * @param cxt
     * @param step
     */
    public static void handleResultInfo2Step(ProcessContextV2 cxt, Step step) {
        if (cxt == null || step == null) {
            throw new RuntimeException("dataModule或者step为空");
        }
        Object obj = cxt.get(ProcessContextEnum.RESULT);
        if (obj != null) {

            Results results = (Results) obj;
            if (results == null || results.getResults().size() == 0) {
                return;
            }
            for (Result result : results.getResults()) {
                String stepId = result.getStepId();
                if (step.getId().equals(stepId)) {
                    List<String> fields = result.getField();
                    step.setField(fields);
                }
            }
        }

    }

    /**
     * 将dataModule结果信息置入到某个step，steps为Set
     *
     * @param ctx
     * @param steps
     */
    public static void handleResultInfo2Steps(ProcessContextV2 ctx, Set<Step> steps) {
        if (steps != null && steps.size() != 0) {
            for (Step step : steps) {
                //将dataModule结果信息置入到某个step
                handleResultInfo2Step(ctx, step);
            }
        }
    }


    /**
     * 获取所有的步骤
     *
     * @param stepIds
     * @return
     */
    private static Set<Step> fetchSteps(Set<String> stepIds) {
        Set<Step> steps = Sets.newHashSet();
        if (stepIds != null && !stepIds.isEmpty()) {
            for (String id : stepIds) {
                Step step = ConfigManagerV2.getStep(id);
                if (step != null && step.getStatus().equalsIgnoreCase("able")) {
                    steps.add(step);
                }
            }
        }
        return steps;
    }


    /**
     * 将unit里配置的所有步骤替换
     *
     * @param unit
     */
    private static void convertUnit(DataProcessingUnit unit) {
        Set<Step> steps = unit.getStep();
        Set<Step> stepInUsed = Sets.newHashSet();
        for (Step step : steps) {
            Step stepCache = ConfigManagerV2.getStep(step.getId());

            if (stepCache != null && "able".equalsIgnoreCase(stepCache.getStatus())) {
                stepInUsed.add(stepCache);
            } else {
                throw new RuntimeException("step:" + step.getId() + "异常，配置的step不存在或状态为disable");
            }
        }
        unit.setStep(stepInUsed);
    }

    /**
     * 根据配置的策略选取第一可执行的数据处理单元
     *
     * @param processingUnitList
     * @param strategy
     * @return
     */
    private static DPUSelectStrategy getStrategy(Set<DataProcessingUnit> processingUnitList, String strategy) {
        /**
         * 默认按顺序选择
         */
        DPUSelectStrategy dpuSelectStrategy = null;
        if (strategy.equalsIgnoreCase(StrategyConstants.RANDOM)) {
            dpuSelectStrategy = new RandomDPUSelectStrategy(processingUnitList);
        } else if (strategy.equalsIgnoreCase(StrategyConstants.PERCENT)) {
            dpuSelectStrategy = new PercentDPUSelectStrategy(processingUnitList);
        } else {
            dpuSelectStrategy = new OrderDPUSelectStrategy(processingUnitList);
        }
        return dpuSelectStrategy;
    }


    /**
     * 获取并执行数据处理步骤
     *
     * @param currentStepId
     * @param cxt
     */
    public static void fetchNextStepAndExcute(String currentStepId, ProcessContextV2 cxt) {
        String mid = null;
        String cmid = null;
        Object midObj = cxt.get(ProcessContextEnum.MODULE_ID);
        Object cmidObj = cxt.get(ProcessContextEnum.CHILD_MODULE_ID);
        if (midObj != null) {
            mid = String.valueOf(midObj);
        }
        if (cmidObj != null) {
            cmid = String.valueOf(cmidObj);
        }

        if (StringUtils.isNotBlank(mid) && StringUtils.isNotBlank(cmid) && StringUtils.isNotBlank(currentStepId) && cxt != null) {
            try {
                DataProcessingUnit unit = ConfigManagerV2.getDPUByCmid(mid, cmid);
                ProcessEngine.convertUnit(unit);

                //将dataModule结果信息置入到某个step，steps为Set
                handleResultInfo2Steps(cxt, unit.getStep());

                /**
                 * stepId成功处理，根据stepId获取所有依赖stepId的数据处理步骤，并判断是否可以开启依赖的步骤
                 */
                if (unit != null) {
                    Map<String, Set<String>> relyOn = StepPriorityUtil.getRelyOn(unit.getStep());
                    Set<String> stepIds = relyOn.get(currentStepId);
                    for (String sid : stepIds) {
                        Step step = ConfigManagerV2.getStep(sid);

                        //将dataModule结果信息置入到某个step
                        handleResultInfo2Step(cxt, step);

                        /**
                         * 当前步骤不为空，且当前步骤未被执行
                         */
                        if (step != null && !cxt.getFinishedStepIds().contains(step.getId())) {
                            startProcessStep(cxt, step);
                        }
                    }
                } else {

                }
            } catch (Exception e) {
                LOGGER.error("gid:{},数据处理异常，异常信息:{}", cxt.get(ProcessContextEnum.GID), e);
            }
        }
    }

    /**
     * 处理数据加工步骤
     *
     * @param cxt
     * @param step
     */
    private static void startProcessStep(ProcessContextV2 cxt, Step step) {
        if (step != null) {
            List<String> finishedStepIds = cxt.getFinishedStepIds();
            String relyon = step.getRelyon();
            if (StringUtils.isNotBlank(relyon)) {
                String[] split = relyon.split(",");
                boolean allOK = checkAllRelyOn(finishedStepIds, split);
                if (allOK) {
                    cxt.getExecutorService().submit(new FutureStepExcutor(step, cxt));
                }
            }
        }
    }

    /**
     * 检查所有的依赖
     *
     * @param finishedStepIds
     * @param relyOnIds
     * @return
     */
    private static boolean checkAllRelyOn(List<String> finishedStepIds, String[] relyOnIds) {
        boolean flag = true;
        if (relyOnIds != null && relyOnIds.length != 0) {
            for (String id : relyOnIds) {
                if (!finishedStepIds.contains(id)) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }


    //开启线程处理（需要处理过程中返回的情况）
    public static class DataProcessingExecutorThread extends Thread {
        private DataModule dataModule;
        private DataProcessingUnit unit;
        private ProcessContextV2 cxt;

        public DataProcessingExecutorThread(DataModule dataModule, DataProcessingUnit unit, ProcessContextV2 cxt) {
            super();
            this.dataModule = dataModule;
            this.unit = unit;
            this.cxt = cxt;
        }

        @Override
        public void run() {
            Set<String> backResultLayer = cxt.getBackResultLayer();
            if (backResultLayer != null || backResultLayer.size() != 0) {
                for (String stepId : backResultLayer) {
                    //下一步
                    fetchNextStepAndExcute(stepId, cxt);
                }

                int allStepsCount = unit.getStep().size();

                int i = 0;
                while (!cxt.isExceptionFlag() && allStepsCount != 0 && allStepsCount > cxt.getFinishedStepIds().size()) {
                    if (i < ATTEMP_COUNT) {
                        try {
                            Thread.sleep(ATTEMP_INTERVAL_IN_MILLI);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                    i++;
                }
                //打印结果rid信息
                printProcessingInfo(cxt);
            }

        }
    }

    /**是否继续数据流程
     * results是否配置，返回层是否执行完成
     * @param cxt
     * @param stepId
     * @return
     */
    public static boolean canStartNextStep(ProcessContextV2 cxt, String stepId) {
        Set<String> backResultLayer = cxt.getBackResultLayer();
        if (backResultLayer == null || backResultLayer.size() == 0) {
            return true;
        }
        if (backResultLayer.contains(stepId)) {
            return false;
        }
        return true;
    }

    /**
     * 打印结果rid信息
     *
     * @param cxt
     */
    public static void printProcessingInfo(ProcessContextV2 cxt) {
        cxt.getExecutorService().shutdownNow();
        cxt.setExecutorService(null);
        List<String> finishedStepIds = cxt.getFinishedStepIds();
        List<String> stepId2Rids = Lists.newArrayList();
        if (finishedStepIds != null && !finishedStepIds.isEmpty()) {
            for (String id : finishedStepIds) {
                stepId2Rids.add(id + "->" + cxt.getProcessScopeDataHolder().get(id));
            }
        }
        LOGGER.info("->>gid:{},数据模块:{},数据单元:{},数据处理优先级:{}", cxt.get(ProcessContextEnum.GID), cxt.get(ProcessContextEnum.MODULE_ID), cxt.get(ProcessContextEnum.CHILD_MODULE_ID), cxt.getPriority());
        LOGGER.info("->>gid:{},数据模块:{},数据单元:{},数据处理开启顺序:{}", cxt.get(ProcessContextEnum.GID), cxt.get(ProcessContextEnum.MODULE_ID), cxt.get(ProcessContextEnum.CHILD_MODULE_ID), cxt.getToBeProcessStepIds());
        LOGGER.info("->>gid:{},数据模块:{},数据单元:{},数据处理完成顺序:{}", cxt.get(ProcessContextEnum.GID), cxt.get(ProcessContextEnum.MODULE_ID), cxt.get(ProcessContextEnum.CHILD_MODULE_ID), cxt.getFinishedStepIds());
        LOGGER.info("->>gid:{},数据模块:{},数据单元:{},数据处理报告rid:{}", cxt.get(ProcessContextEnum.GID), cxt.get(ProcessContextEnum.MODULE_ID), cxt.get(ProcessContextEnum.CHILD_MODULE_ID), stepId2Rids);
    }


    private static Map<String, Object> responseMapCheckReturn(DataModule dataModule, ProcessContextV2 cxt) {
        Map<String, Object> responseMap = new HashMap();

        responseMap.put(RLT_CODE, String.valueOf(ResponseCodeEnum.OUT_SUCCESS));
        responseMap.put(RLT_GID, cxt.get(ProcessContextEnum.GID).toString());
        responseMap.put(RLT_MESSAGE, "成功");

        //获取上下文中不在有效期内的step
        List<String> noValidStepIdList = cxt.getNoValidStepId();

        //Results不为空，说明希望在某一个阶段返回处理结果
        if (dataModule.getResults() != null && dataModule.getResults().getResults().size() != 0) {
            List<Result> results = dataModule.getResults().getResults();

            if (results.size() > 1) {
                for (Result result : results) {
                    String stepId = result.getStepId();
                    List<String> fields = results.get(0).getField();
                    Map<String, Object> rltMap = cxt.getStoreResults().get(stepId);
                    if (rltMap != null) {
                        responseMap.put(stepId, rltMap);
                    } else {
                        Map<String, String> rltMapNoVal = new HashMap<>();
                        for (String field : fields) {
                            rltMapNoVal.put(field, String.valueOf(cxt.getProcessScopeDataHolder().get(stepId)));
                        }
                        responseMap.put(stepId, rltMapNoVal);

                    }


                }
            } else {
                String stepId = results.get(0).getStepId();
                List<String> fields = results.get(0).getField();


                //上下文中存储的处理结果
                Map<String, Object> rltMap = cxt.getStoreResults().get(stepId);
                if (noValidStepIdList.size() != 0) {
                    if (noValidStepIdList.contains(stepId)) {
                        if (rltMap != null) {
                            for (String field : fields) {
                                responseMap.put(field, rltMap.get(field));
                            }
                        } else {
                            responseMap.put(RLT_CODE, String.valueOf(ResponseCodeEnum.NO_DATA));
                            responseMap.put(RLT_MESSAGE, "提取结果异常");
                        }
                    }
                } else {
                    responseMap.put(RLT_RID, String.valueOf(cxt.getProcessScopeDataHolder().get(stepId)));
                }



                /*if (noValidStepIdList.size() != 0) {
                    if (noValidStepIdList.contains(stepId)) {
                        List<String> fields = results.get(0).getField();
                        Map<String, Object> rltMap = cxt.getStoreResults().get(stepId);
                        if (rltMap != null) {
                            for (String field : fields) {
                                responseMap.put(field, rltMap.get(field));
                            }
                        } else {
                            responseMap.put(RLT_CODE, String.valueOf(ResponseCodeEnum.NO_DATA));
                            responseMap.put(RLT_MESSAGE, "提取结果异常");
                        }
                    } else {
                        String lastStepId = cxt.getFinishedStepIds().get(cxt.getFinishedStepIds().size() - 1);
                        Object ridObj = cxt.getProcessScopeDataHolder().get(lastStepId);




                        responseMap.put(RLT_RID, ridObj);
                    }


                } else {
                    String lastStepId = cxt.getFinishedStepIds().get(cxt.getFinishedStepIds().size() - 1);
                    Object ridObj = cxt.getProcessScopeDataHolder().get(lastStepId);
                    responseMap.put(RLT_RID, ridObj);
                }*/


            }

        } else {
            String lastStepId = cxt.getFinishedStepIds().get(cxt.getFinishedStepIds().size() - 1);
            Object ridObj = cxt.getProcessScopeDataHolder().get(lastStepId);
            responseMap.put("rid", ridObj);
        }
        return responseMap;
    }
}
