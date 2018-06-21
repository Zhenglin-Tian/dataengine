package com.tcredit.engine.context;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-18 09:43
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-18 09:43
 * @updatedRemark:
 * @version:
 */
public class ProcessContextHolder {
    //标识该次请求是同步还是异步，根据callbackUrl来判断，如果该值为空，则认为是同步调用，如果该字段有值则任务是异步调用
    public final static String DATETIMEFORM = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String CALLBACK_URL = "callbackUrl";
    public final static String RLT_STEP = "rltStep";
    //标识是否是同步返回，还是异步返回，true 同步，false 异步
    private static InheritableThreadLocal<Boolean> dataHandlerSynchronized = new InheritableThreadLocal<>();
    public static void setDataHandlerSynchronized(boolean syn){
        dataHandlerSynchronized.set(syn);
    }
    public static boolean getDataHandleSync(){
        return dataHandlerSynchronized.get();
    }
}
