package com.tcredit.engine.processService.impl.crawlercall;

import com.tcredit.engine.processService.DataProcessingService;
import com.tcredit.engine.processService.crawlercall.CrawlerCallBackService;
import com.tcredit.engine.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Service
public class CrawlerCallBackServiceImpl implements CrawlerCallBackService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(CrawlerCallBackServiceImpl.class);
    @Autowired
    DataProcessingService dataProcessingService;

    //公积金、社保、信用卡邮箱账单爬虫回调
    @Override
    public BaseResponse callBack(HttpServletRequest request, String mid) {
        LOGGER.info(mid + "爬虫回调进入-----");
        BaseResponse callBackResponse = new BaseResponse();

        Map<String, String> param = null;

        String gid = null;
        String tid = null;

        try {
            param = getParamFromRequest(request);
            String msg = checkParam(param, mid);
            gid = param.get("gid");
            tid = param.get("tid");
            if (!StringUtils.isNotBlank(msg)) {

                LOGGER.info("gid:" + gid + ",tid:" + tid + "," + mid);

                start(gid,tid,"tid",mid,"");

            } else {
                callBackResponse.setCode("-1");
                callBackResponse.setMessage("失败，参数错误:" + msg);
                LOGGER.error("gid:" + gid + ",tid:" + tid + "," + mid + ",参数错误:" + msg);
            }

        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",tid:" + tid + "," + mid + ",处理异常,异常信息:", e);
        }


        return callBackResponse;
    }

    //网银爬虫回调
    @Override
    public BaseResponse ebankCall(HttpServletRequest request) {
        LOGGER.info("网银爬虫回调进入-----");
        BaseResponse callBackResponse = new BaseResponse();

        Map<String, String> param = null;

        String gid = null;
        String tid = null;
        try {
            param = getEbankParamFromRequest(request);
            String msg = checkEbankParam(param);

            gid = param.get("gid");
            tid = param.get("tid");
            if (!StringUtils.isNotBlank(msg)) {

                LOGGER.info("gid:" + gid + ",tid:" + tid + ",bankcardreportcontentdetail");

                start(gid,tid,"ebank_tid","bankcardreportcontentdetail","");

            } else {
                callBackResponse.setCode("-1");
                callBackResponse.setMessage("失败，参数错误:" + msg);
                LOGGER.error("gid:" + gid + ",tid:" + tid + ",bankcardreportcontentdetail,参数错误:" + msg);

            }
        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",tid:" + tid + ",bankcardreportcontentdetail,处理异常,异常信息:", e);

        }


        return callBackResponse;
    }

    //运营商爬虫回调
    @Override
    public BaseResponse userreportCll(HttpServletRequest request) {
        LOGGER.info("运营商爬虫回调进入-----");
        BaseResponse callBackResponse = new BaseResponse();

        Map<String, String> param = null;

        String gid = null;
        String tid = null;
        String phoneNum = null;
        try {
            param = getUserreportParamFromRequest(request);
            String msg = checkUserreportParam(param);

            gid = param.get("gid");
            tid = param.get("tid");
            phoneNum = param.get("phoneNum");
            if (!StringUtils.isNotBlank(msg)) {

                LOGGER.info("gid:" + gid + ",tid:" + tid + ",phoneNum:" + phoneNum + ",userreportstd");

                start(gid,tid,"userreport_tid","userreportstd",phoneNum);

            } else {
                callBackResponse.setCode("-1");
                callBackResponse.setMessage("失败，参数错误:" + msg);
                LOGGER.error("gid:" + gid + ",tid:" + tid + ",phoneNum:" + phoneNum + ",userreportstd,参数错误:" + msg);
            }

        } catch (Exception e) {
            LOGGER.error("gid:" + gid + ",tid:" + tid + ",phoneNum:" + phoneNum + ",userreportstd,处理异常,异常信息:", e);
        }


        return callBackResponse;
    }


    //公积金、社保、信用卡邮箱账单回调参数获取（tid、status、passParam）
    private Map<String, String> getParamFromRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String status = request.getParameter("status");
        String tid = request.getParameter("tid");
        String passParam = request.getParameter("passParam");
        map.put("tid", tid);
        map.put("status", status);
        map.put("gid", passParam);
        return map;
    }

    //网银回调参数获取（phase、phase_status、task_id、passParam）
    private Map<String, String> getEbankParamFromRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String phase = request.getParameter("phase");
        String phaseStatus = request.getParameter("phase_status");
        String tid = request.getParameter("task_id");
        String passParam = request.getParameter("passParam");
        map.put("tid", tid);
        map.put("phase", phase);
        map.put("phaseStatus", phaseStatus);
        map.put("gid", passParam);
        return map;
    }

    //运营商回调参数获取（phoneNum、tid、status、passbackparams）
    private Map<String, String> getUserreportParamFromRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String phoneNum = request.getParameter("phoneNum");
        String tid = request.getParameter("tid");
        String status = request.getParameter("status");
        String passBackParams = request.getParameter("passbackparams");
        map.put("phoneNum", phoneNum);
        map.put("tid", tid);
        map.put("status", status);
        map.put("gid", passBackParams);
        return map;
    }

    //公积金、社保、信用卡邮箱账单回调参数效验
    private String checkParam(Map<String, String> map, String mid) {
        if (!StringUtils.isNotBlank(map.get("status"))) {
            return "status为空";
        }

        //公积金社保状态码为4开头表示爬取成功、信用卡状态码为0则表示爬取成功
        String status = map.get("status");
        if (mid.equals("creditcardgetdata")) {
            if (!status.equals("0")) {
                //信用卡
                return "status标识状态未成功";
            }
        } else {
            if (!status.substring(0, 1).equals("4")) {
                //公积金、社保
                return "status标识状态未成功";
            }
        }

        if (!StringUtils.isNotBlank(map.get("tid"))) {
            return "tid为空";
        }

        if (!StringUtils.isNotBlank(map.get("gid"))) {
            return "passParam为空";
        }
        return "";
    }

    //网银回调参数效验
    private String checkEbankParam(Map<String, String> map) {
        if (!StringUtils.isNotBlank(map.get("phase")) || !StringUtils.isNotBlank(map.get("phaseStatus"))) {
            return "phase或phaseStatus为空";
        }
        if (!map.get("phase").equals("DONE") || !map.get("phaseStatus").equals("DONE_SUCC")) {
            return "phase_status标识状态未成功";
        }

        if (!StringUtils.isNotBlank(map.get("tid"))) {
            return "tid为空";
        }

        if (!StringUtils.isNotBlank(map.get("gid"))) {
            return "passParam为空";
        }
        return "";
    }

    //运营商回调参数效验
    private String checkUserreportParam(Map<String, String> map) {
        if (!StringUtils.isNotBlank(map.get("status"))) {
            return "status为空";
        }

        if (!map.get("status").equals("10009")) {
            return "status标识状态未成功";
        }

        if (!StringUtils.isNotBlank(map.get("phoneNum"))) {
            return "phoneNum为空";
        }
        if (!StringUtils.isNotBlank(map.get("tid"))) {
            return "tid";
        }

        if (!StringUtils.isNotBlank(map.get("gid"))) {
            return "passbackparams为空";
        }
        return "";
    }


    private void start(String gid,String tid,String tidKey,String mid,String mobile){
        CrawlerCallBackHandle handle=new CrawlerCallBackHandle();
        handle.gid=gid;
        handle.mid=mid;
        handle.tid=tid;
        handle.tidKey=tidKey;
        handle.mobile=mobile;
        Thread thread=new Thread(handle);
        thread.start();
    }


    class CrawlerCallBackHandle implements Runnable{
         String gid;
         String tid;
         String tidKey;
         String mobile;
         String mid;

        @Override
        public void run() {
            Map<String, Object> params = new HashMap<>();
            long startTime = System.currentTimeMillis();
            params.put(tidKey, tid);
            params.put("mobile", mobile);
            params.put("req_time", startTime);
            params.put("gid", gid);
            params.put("mid", mid);
            dataProcessingService.process(params);

        }
    }

}
