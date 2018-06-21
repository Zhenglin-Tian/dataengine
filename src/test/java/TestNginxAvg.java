import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.util.FileUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PathUtil;
import jodd.util.URLDecoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-07 09:45
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-07 09:45
 * @updatedRemark:
 * @version:
 */
public class TestNginxAvg {
    private static String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 1);

    private static String approveNginx = projectDir + "/test-classes/nginx/beesmell.log";
    private static String dataaccessNginx = projectDir + "/test-classes/nginx/access.log";
    private static String databoosterNginx = projectDir + "/test-classes/nginx/databooster.access.log.15";
    private static String dataengineNginx = projectDir + "/test-classes/nginx/dataengine.access.log.15";
    private static String split = "@@@";
    private static String pattern = "dd/MMM/YYYY:HH:mm:ss +0800";
    private static String startTime = "21/May/2018:00:00:00 +0800";
    private static String endTime = "21/May/2018:23:59:59 +0800";
    private static SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    private static long startTimeInMills = 0;
    private static long endTimeInMills = 0;

    static {
        try {
            startTimeInMills = sdf.parse(startTime).getTime();
            endTimeInMills = sdf.parse(endTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getData() throws IOException {
        String dataFile = projectDir + "/test-classes/nginx/beesmell.log";
        List<String> strings = FileUtils.readLines(new File(dataFile), "utf-8");
        List<Double> datas = Lists.newArrayList();
        for (String s : strings) {
            int i = s.lastIndexOf(":");
            String sub = s.substring(i + 1, s.length() - 2);
            if (StringUtils.isNotBlank(sub) && !"null".equalsIgnoreCase(sub)) {
                double v = Double.valueOf(sub) / 1000;
                datas.add(v);
            }

        }

        long count = datas.stream().filter(f -> f > 10).count();
        System.out.println(datas.size());
        System.out.println("val>10 count:" + count);

    }


    public static void main(String[] args) throws Exception {
        String file = "/Users/zlT/Documents/tcreditProj/dataProcessEngine/src/test/resources/nginx/result.log";
       // FileUtils.forceDeleteOnExit(new File(file));
        List<String> results = Lists.newArrayList();

        List<String> approve = FileUtils.readLines(new File(approveNginx), "utf-8");
        List<String> dataaccess = FileUtils.readLines(new File(dataaccessNginx), "utf-8");
//        List<String> databooster = FileUtils.readLines(new File(databoosterNginx), "utf-8");
//        List<String> dataengine = FileUtils.readLines(new File(dataengineNginx), "utf-8");




        List<Float> approveTimes = getData(approve);
        System.out.println("----------approve-------------");
        results.add("----------approve-------------");
        DoubleSummaryStatistics approveStat = approveTimes.stream().mapToDouble(f -> Double.valueOf(f)).summaryStatistics();
        System.out.println("sum:" + approveStat.getSum());
        results.add("sum:" + approveStat.getSum());
        System.out.println("size:" + approveStat.getCount());
        results.add("size:" + approveStat.getCount());
        System.out.println("max:" + approveStat.getMax());
        results.add("max:" + approveStat.getMax());
        System.out.println("min:" + approveStat.getMin());
        results.add("min:" + approveStat.getMin());
        System.out.println("avg:" + approveStat.getAverage());
        results.add("avg:" + approveStat.getAverage());
        long approveCount = approveTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 10).count();
        System.out.println("time > 10 count:" + approveCount);
        results.add("time > 10 count:" + approveCount);
        long approveCount12 = approveTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 12).count();
        System.out.println("time > 12 count:" + approveCount12);
        results.add("time > 12 count:" + approveCount12);
        long approveCount15 = approveTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 15).count();
        System.out.println("time > 15 count:" + approveCount15);
        results.add("time > 15 count:" + approveCount15);
        List<String> approveFilterByVal = getDataFilterByVal(dataaccess, 15);
        results.addAll(approveFilterByVal);
        for (String s : approveFilterByVal) {
            System.out.println(s);
        }

        System.out.println("-----------------------------------------------------");
        results.add("-----------------------------------------------------");


        List<Float> dataaccessTimes = getData(dataaccess);
        System.out.println("----------dataaccess-------------");
        results.add("----------dataaccess-------------");
        DoubleSummaryStatistics accessStat = dataaccessTimes.stream().mapToDouble(f -> Double.valueOf(f)).summaryStatistics();
        System.out.println("sum:" + accessStat.getSum());
        results.add("sum:" + accessStat.getSum());
        System.out.println("size:" + accessStat.getCount());
        results.add("size:" + accessStat.getCount());
        System.out.println("max:" + accessStat.getMax());
        results.add("max:" + accessStat.getMax());
        System.out.println("min:" + accessStat.getMin());
        results.add("min:" + accessStat.getMin());
        System.out.println("avg:" + accessStat.getAverage());
        results.add("avg:" + accessStat.getAverage());
        long accessCount = dataaccessTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 10).count();
        System.out.println("time > 10 count:" + accessCount);
       results.add("time > 10 count:" + accessCount);
        long accessCount12 = dataaccessTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 12).count();
        System.out.println("time > 12 count:" + accessCount12);
        results.add("time > 12 count:" + accessCount12);
        long accessCount15 = dataaccessTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 15).count();
        System.out.println("time > 15 count:" + accessCount15);
        results.add("time > 15 count:" + accessCount15);
        List<String> dataFilterByVal = getDataFilterByVal(dataaccess, 15);
        results.addAll(dataFilterByVal);
        for (String s : dataFilterByVal) {
            System.out.println(s);
        }

        FileUtils.writeLines(new File(file),results);

////
//
//        List<Float> databoosterTimes = getData(databooster);
//        System.out.println("----------databooster-------------");
//        DoubleSummaryStatistics boosterStat = databoosterTimes.stream().mapToDouble(f -> Double.valueOf(f)).summaryStatistics();
//        System.out.println("sum:" + boosterStat.getSum());
//        System.out.println("size:" + boosterStat.getCount());
//        System.out.println("max:" + boosterStat.getMax());
//        System.out.println("min:" + boosterStat.getMin());
//        System.out.println("avg:" + boosterStat.getAverage());
//        long boosterCount = databoosterTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 1).count();
//        System.out.println("value > 1 count:" + boosterCount);
////
//
//
//        List<Float> dataengineTimes = getData(dataengine);
//        System.out.println("----------dataengine-------------");
//        DoubleSummaryStatistics engineStat = dataengineTimes.stream().mapToDouble(f -> Double.valueOf(f)).summaryStatistics();
//        System.out.println("sum:" + engineStat.getSum());
//        System.out.println("size:" + engineStat.getCount());
//        System.out.println("max:" + engineStat.getMax());
//        System.out.println("min:" + engineStat.getMin());
//        System.out.println("avg:" + engineStat.getAverage());
//        long engineCount = dataengineTimes.stream().mapToDouble(f -> Double.valueOf(f)).filter(f -> f > 10).count();
//        System.out.println("value > 10 count:" + engineCount);


    }


    public static List<Float> getData(List<String> rawData) throws ParseException {
        List<Float> approveTimes = Lists.newArrayList();
        for (String s : rawData) {
            if (StringUtils.isNotBlank(s)) {

                if (s.contains("tblName") || s.contains("antifraud/count")) {
                    continue;
                }
                String[] split = s.split(TestNginxAvg.split);
                String rts = split[3];


                String rt = rts.substring(1, rts.length() - 1);
                long time1 = sdf.parse(rt).getTime();
                if (time1 >= startTimeInMills && time1 <= endTimeInMills) {
                    String times = split[10];
                    String time = times.substring(1, times.length() - 1);
                    approveTimes.add(Float.valueOf(time));
                }
            }
        }

        return approveTimes;
    }


    public static List<String> getDataFilterByVal(List<String> rawData, float refer) throws ParseException {
        List<String> needResult = Lists.newArrayList();
        for (String s : rawData) {
            if (StringUtils.isNotBlank(s)) {

                if (s.contains("tblName") || s.contains("antifraud/count")) {
                    continue;
                }
                String[] split = s.split(TestNginxAvg.split);
                String rts = split[3];


                String rt = rts.substring(1, rts.length() - 1);
                long time1 = sdf.parse(rt).getTime();
                if (time1 >= startTimeInMills && time1 <= endTimeInMills) {
                    String times = split[10];
                    String time = times.substring(1, times.length() - 1);
                    if (time != null && Float.valueOf(time) >= refer) {
                        if (split[12] != null && StringUtils.isNotBlank(split[12]) && split[12].split("\\s+").length == 2) {
                            String substring = split[12].split("\\s+")[0].substring(1, split[12].split("\\s+")[0].length() - 1);
                            String subStr = URLDecoder.decode(substring.substring(substring.indexOf("=")+1));
                            Map<String,String> map = JsonUtil.json2Object(subStr, Map.class);
                            map.put("invokeIp",split[12].split("\\s+")[1]);
                            map.put("time",time);
                            needResult.add(JsonUtil.toJson(map));
                        }
                    }
                }
            }
        }

        return needResult;
    }
}
