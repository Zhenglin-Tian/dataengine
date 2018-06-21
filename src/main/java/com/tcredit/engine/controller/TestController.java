package com.tcredit.engine.controller;

import com.google.common.collect.Lists;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.RedissonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-06 10:01
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-06 10:01
 * @updatedRemark:
 * @version:
 */
@Controller
@RequestMapping("/dp/v1")
public class TestController {

//    private JedisUtil jedisUtil = new JedisUtil();
//
//    @RequestMapping("/redis")
//    @ResponseBody
//    public String redis(HttpServletRequest request) {
//        String key = "xxxxx_xxxx";
//        try {
//            long x = System.currentTimeMillis();
//            String s = JedisUtil.get(key);
//            long x2 = System.currentTimeMillis();
//            System.out.println("===============" + s + "  time:" + (x2 - x));
//            return s;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//        return key;
//    }

    @RequestMapping("/redis2")
    @ResponseBody
    public String redis2(HttpServletRequest request) {
        String key = "xxxxx_xxxx";
        try {
            long x = System.currentTimeMillis();
            String s = RedissonUtil.getString(key);
            long x2 = System.currentTimeMillis();
            System.out.println("===============" + s + "  time:" + (x2 - x));
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return key;
    }

    @RequestMapping("/testcallback")
    @ResponseBody
    public String callBackTest(HttpServletRequest request) {
        System.out.println(request.getParameter("result"));
        ResponseData response = new ResponseData();
        response.setSync(null);
        response.setMsg("success");
        response.setCode("0000");
        response.setGid(null);
        response.setCmid(null);
        response.setData(null);
        response.setStep(null);
        response.setMid(null);
        System.out.println(response);
        return response.toString();
    }


    @RequestMapping("/std")
    @ResponseBody
    public String std(HttpServletRequest request) {
        ResponseData obr = new ResponseData();
        obr.setCode("00000");
        obr.setMsg("成功");
        obr.setGid("2");
        obr.setMid("dvd161003");
        obr.setCmid("dvd161003001");

        obr.setStep("std");

        Map<String, Object> map = new HashMap<String, Object>() {{
            put("gid","2");
            put("k1", "v1");
            put("k2", "v2");
            put("k3", "v3");
            put("k4", "v4");
        }};
        Map<String, Object> map2 = new HashMap<String, Object>() {{
            put("gid","2");
            put("k1", null);
            put("k2", "v21");
            put("k3", "v31");
            put("k4", "v41");
        }};
        TableData tableData = new TableData();
        tableData.setTableName("test");
        tableData.setDbName("std");
        tableData.getData().add(map);
        tableData.getData().add(map2);
        List<TableData> list = Lists.newArrayList();
        list.add(tableData);
        obr.setData(list);
        obr.setSync(true);

        String raw = JsonUtil.toJson(obr);


        return raw;
    }

    @RequestMapping("/asyncstd")
    @ResponseBody
    public String asyncStd(HttpServletRequest request) {
        ResponseData obr = new ResponseData();
        obr.setCode("00000");
        obr.setMsg("成功");
        obr.setGid("1");
        obr.setMid("dvd161003");
        obr.setCmid("dvd161003001");
        obr.setStep("std");
        obr.setSync(false);

        String raw = JsonUtil.toJson(obr);


        return raw;
    }


    @RequestMapping("/tidy")
    @ResponseBody
    public String tidy(HttpServletRequest request) {
        ResponseData obr = new ResponseData();
        obr.setCode("00000");
        obr.setMsg("成功");
        obr.setGid("2");
        obr.setMid("dvd161003");
        obr.setCmid("dvd161003001");
        obr.setStep("tidy");

        Map<String, Object> map = new HashMap<String, Object>() {{
            put("gid","2");
            put("k1", "v1");
            put("k2", "v2");
            put("k3", "v3");
            put("k4", "v4");
        }};
        Map<String, Object> map2 = new HashMap<String, Object>() {{
            put("gid","2");
            put("k1", "v11");
            put("k2", null);
            put("k3", "v31");
            put("k4", "v41");
        }};
        TableData tableData = new TableData();
        tableData.setTableName("test");
        tableData.setDbName("tidy");
        tableData.getData().add(map);
        tableData.getData().add(map2);
        List<TableData> list = Lists.newArrayList();
        list.add(tableData);
        obr.setData(list);
        obr.setSync(true);

        String raw = JsonUtil.toJson(obr);


        return raw;
    }

    @RequestMapping("/asynctidy")
    @ResponseBody
    public String asyncTidy(HttpServletRequest request) {
        ResponseData obr = new ResponseData();
        obr.setCode("00000");
        obr.setMsg("成功");
        obr.setGid("2");
        obr.setMid("dvd161003");
        obr.setCmid("dvd161003001");
        obr.setStep("tidy");
        obr.setSync(false);

        String raw = JsonUtil.toJson(obr);


        return raw;
    }


    @RequestMapping("/thread")
    @ResponseBody
    public String threadCount(HttpServletRequest request) {

        return String.valueOf(Thread.activeCount());
    }
}
