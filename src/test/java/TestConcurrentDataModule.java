import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-04-03 08:53
 * @updatedUser: zl.T
 * @updatedDate: 2018-04-03 08:53
 * @updatedRemark:
 * @version:
 */
public class TestConcurrentDataModule {
    private static final String PATH = "/Users/zlT/Documents/tcreditWork/logs/dataEngine/concurrent.log";
    private static final int CONCURRENT = 20;
    private static final int COUNT = 1000;
    private static List<String> res = Lists.newArrayList();

    public static void main(String[] args) {
        String param = "{\"gid\":\"0403001\", \"mid\":\"unionpaytransactionvariableb\", \"cmid\":\"unionpaytransactionvariableb\", \"uuid\":\"111111xxxxx\",\"bankcard\":\"6212262404005241684\", \"mobile\":\"17612140317\", \"name\":\"吴文建\", \"idcard\":\"52252619950824001X\", \"req_time\":\"1234566789\", \"userreport_tid\":\"d6229f4f-2ebd-46e2-a1fe-5973c6d39838\", \"data\":\"{\\\"gid\\\":\\\"0403001\\\",\\\"name\\\":\\\"吴文建\\\",\\\"uuid\\\":\\\"111111xxxxx\\\"}\"}";
//        String param = "{\"period\":\""2, gid=tcgid-8c409e480e8d4653990e598c83bda281, idcard=52252619950824001X, name=吴文建, mid=verifybankcardinfo3e, bankcard=6212262404005241684, uuid=f1d3396156e7402d855281e72e80a6a2, rltStep=std, req_time=1521734651425}";
//        System.out.println(param);
        String url = "http://localhost:8080/dp/v1/handle";
        Map<String, String> params = Maps.newHashMap();


        ExecutorService es = Executors.newFixedThreadPool(CONCURRENT);
        List<Future<String>> tasks = Lists.newArrayList();
        for (int i = 0; i < COUNT; i++) {
            String no = DateUtil.formatDate2StrFromDate(new Date(), "yyyyMMddHHmmssSSS") + RandomUtils.nextInt(1000, 9999);
            String paramNew = param.replaceAll("0403001", no);
            Future<String> submit = es.submit(new RequestFuture(paramNew, url, no));
            tasks.add(submit);
        }
        es.shutdown();
        System.out.println(tasks.size());

        for (int i = 0; i < COUNT; i++) {
            try {
                String s = tasks.get(i).get(10 * 1000, TimeUnit.SECONDS);
                if (res.size() < 100) {
                    res.add("==" + s);
                } else {
                    List<String> resClone = res;
                    res = Lists.newArrayList();
                    res.add("==" + s);
                    writeRes(resClone);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (i == COUNT - 1) {
                writeRes(res);
            }
        }


    }

    static class RequestFuture implements Callable<String> {
        String param;
        String url;
        String no;

        public RequestFuture(String param, String url, String no) {
            this.param = param;
            this.url = url;
            this.no = no;
        }

        @Override
        public String call() throws Exception {
            Map<String, String> params = Maps.newHashMap();
            params.put("param", param);
            long currentTimeMillis = System.currentTimeMillis();
            String s = HttpClientUtil.httpPost(url, params, 30 * 1000);
            long currentTimeMillis1 = System.currentTimeMillis();
            return no + "--" + s + "--耗时:" + (currentTimeMillis1 - currentTimeMillis);
        }
    }


    public static void writeRes(List<String> res) {
        try {
            FileUtils.writeLines(new File(PATH), res, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
