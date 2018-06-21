package com.tcredit.engine.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.constants.StepConstants;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.KeyUtil;
import com.tcredit.engine.util.RedissonUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-30 10:06
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-30 10:06
 * @updatedRemark:
 * @version:
 */
public class ProcessContext {
    private boolean isSyn = true;
    private ProcessStep currentStep = new ProcessStep();

    private List<ProcessStep> handledSteps = Lists.newArrayList();

    /**
     * 存储数据处理过程中涉及到的所有数据
     */
    private Map<String, Object> processScopeDataHolder = Maps.newConcurrentMap();

    private List<String> dataModuleResultSigns = StepConstants.steps();

    /**
     * 处理过程是否正常标识
     */
    private ExceptionSign exceptionSign = new ExceptionSign();

    public void put(ProcessContextEnum pce, Object val) {
        getProcessScopeDataHolder().put(pce.val, val);
    }

    public void put(String key, Object val) {
        getProcessScopeDataHolder().put(key, val);
    }

    public Object get(ProcessContextEnum pce) {
        return getProcessScopeDataHolder().get(pce.val);
    }


    //getter &&&& setter


    public ProcessStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(ProcessStep currentStep) {
        this.currentStep = currentStep;
    }

    public List<ProcessStep> getHandledSteps() {
        return handledSteps;
    }

    public void setHandledSteps(List<ProcessStep> handledSteps) {
        this.handledSteps = handledSteps;
    }

    public ProcessStep getProcessedStep(String stepName) {
        if (StringUtils.isBlank(stepName)) return null;
        for (ProcessStep step : getHandledSteps()) {
            if (stepName.equalsIgnoreCase(step.getCurrentStep())) {
                return step;
            }
        }
        return null;
    }


    public boolean isSyn() {
        return isSyn;
    }

    public void setSyn(boolean syn) {
        isSyn = syn;
    }

    public Map<String, Object> getProcessScopeDataHolder() {
        return processScopeDataHolder;
    }

    public void setProcessScopeDataHolder(Map<String, Object> processScopeDataHolder) {
        this.processScopeDataHolder = processScopeDataHolder;
    }

    public List<String> getDataModuleResultSigns() {
        return dataModuleResultSigns;
    }

    public void setDataModuleResultSigns(List<String> dataModuleResultSigns) {
        this.dataModuleResultSigns = dataModuleResultSigns;
    }

    public ExceptionSign getExceptionSign() {
        return exceptionSign;
    }

    public void setExceptionSign(ExceptionSign exceptionSign) {
        this.exceptionSign = exceptionSign;
    }

    public void setExceptionSign(boolean flag,String msg){
        ExceptionSign exceptionSign = getExceptionSign();
        if (exceptionSign == null){
            exceptionSign = new ExceptionSign();
            this.setExceptionSign(exceptionSign);
        }

        getExceptionSign().setExceptionSign(flag);
        getExceptionSign().setExceptionMsg(msg);
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public static void main(String[] args) {
        ProcessContext context = new ProcessContext();
        context.put(ProcessContextEnum.START_TIME, new Date());
        context.getCurrentStep().setStartTime(new Date());

        String ss = context.toString();
        System.out.println(ss);

        ProcessContext processContext = JsonUtil.json2Object(ss, ProcessContext.class);
        Object o = processContext.get(ProcessContextEnum.START_TIME);
        System.out.println(o.getClass());

    }


    public static void updateCxt(String gid,String mid,ProcessContext cxt){
        String lockName = KeyUtil.generateRedisKey(gid, mid, "lock");
        String sessionKey = KeyUtil.generateRedisKey(gid, mid);
        RLock rLock = RedissonUtil.getRLock(lockName);
        try {
            String s = RedissonUtil.get(sessionKey);
            if (StringUtils.isNotBlank(s)) {
                ProcessContext processContext = JsonUtil.json2Object(s, ProcessContext.class);
                Map<String, Object> processScopeDataHolder = processContext.getProcessScopeDataHolder();
                cxt.setProcessScopeDataHolder(processScopeDataHolder);
            }
            RedissonUtil.set(sessionKey, cxt.toString(), RedissonUtil.JEDIS_KEY_EXPIRE_IN_SEC);
        } finally {
            rLock.unlock();
        }
    }
}


