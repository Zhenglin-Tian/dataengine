package com.tcredit.engine.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.KeyUtil;
import com.tcredit.engine.util.RedissonUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-30 10:06
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-30 10:06
 * @updatedRemark:
 * @version:
 */
public class ProcessContextV2 {

    private boolean isSyn = true;
    /**
     * 存储数据处理过程中涉及到的所有数据
     */
    private volatile Map<String, Object> processScopeDataHolder = Maps.newConcurrentMap();
    /**
     * 已经处理完成并成功处理的步骤标识
     */
    private volatile List<String> finishedStepIds = Lists.newCopyOnWriteArrayList();
    /**
     * 数据处理线程执行器
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    /**
     * 步骤依赖关系
     */
    private Map<String, Set<String>> relyOn = null;
    /**
     * 数据处理步骤开启的执行顺序
     */
    private volatile List<String> toBeProcessStepIds = Lists.newCopyOnWriteArrayList();
    /**
     * 数据处理步骤优先级
     */
    private List<Set<String>> priority = Lists.newArrayList();
    /**
     * 数据处理过程异常
     */
    private volatile boolean exceptionFlag = false;

    /**
     * 数据处理是否完成
     */
    private boolean finished = false;

    /**
     * step中的write数据全部写入map
     */
    private volatile Map<String, Object> stepWriteMap = Maps.newConcurrentMap();


    /**
     * 数据处理单元中所有步骤分优先级层，该层处理完成即可返回结果，剩余步骤开启异步处理
     */
    private Set<String> backResultLayer = Sets.newHashSet();

    /**
     * 存储数据处理step返回的结果根据db当中的配置，key为stepID
     */
    private volatile Map<String, Map<String,Object>> storeResults = Maps.newConcurrentMap();


    /**
     * 记录不在有效期内、已经发送请求的stepid，用于配置dp中的results处理返程后获取处理结果
     */
    private volatile List<String> noValidStepId=Lists.newArrayList();


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

    public List<String> getFinishedStepIds() {
        return finishedStepIds;
    }

    public void setFinishedStepIds(List<String> finishedStepIds) {
        this.finishedStepIds = finishedStepIds;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Map<String, Set<String>> getRelyOn() {
        return relyOn;
    }

    public void setRelyOn(Map<String, Set<String>> relyOn) {
        this.relyOn = relyOn;
    }

    public List<String> getToBeProcessStepIds() {
        return toBeProcessStepIds;
    }

    public void setToBeProcessStepIds(List<String> toBeProcessStepIds) {
        this.toBeProcessStepIds = toBeProcessStepIds;
    }

    public boolean addStep2BeProcessStepIds(String stepId) {
        if (StringUtils.isNotBlank(stepId) && !getToBeProcessStepIds().contains(stepId)) {
            getToBeProcessStepIds().add(stepId);
            return true;
        } else {
            return false;
        }
    }

    public List<Set<String>> getPriority() {
        return priority;
    }

    public void setPriority(List<Set<String>> priority) {
        this.priority = priority;
    }

    public boolean isExceptionFlag() {
        return exceptionFlag;
    }

    public void setExceptionFlag(boolean exceptionFlag) {
        this.exceptionFlag = exceptionFlag;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Map<String, Object> getStepWriteMap() {
        return stepWriteMap;
    }

    public void setStepWriteMap(Map<String, Object> stepWriteMap) {
        this.stepWriteMap = stepWriteMap;
    }

    public Set<String> getBackResultLayer() {
        return backResultLayer;
    }

    public void setBackResultLayer(Set<String> backResultLayer) {
        this.backResultLayer = backResultLayer;
    }

    public Map<String, Map<String, Object>> getStoreResults() {
        return storeResults;
    }

    public void setStoreResults(Map<String, Map<String, Object>> storeResults) {
        this.storeResults = storeResults;
    }

    public List<String> getNoValidStepId() {
        return noValidStepId;
    }

    public void setNoValidStepId(List<String> noValidStepId) {
        this.noValidStepId = noValidStepId;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }


    public static void main(String[] args) {


    }

}


