package zhangkan;

import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostTest {
    public static void main(String[] args) {
        Map map = new HashMap<>();
        Map inParameter = new HashMap<>();

        map.put("mid", "antifraudquery");
        map.put("cmid", "antifraudquery");
        map.put("uuid", "0518zhangkanuuid0001");
        map.put("gid", "0518zhangkanuuid0001");
        map.put("bid", "3e824e54-0dba-48d2-baad-8e9d9567eb0b");
        map.put("seq_num", "credit0510016");

        Map event = new HashMap<>();
        event.put("ei", "0518zhangkanuuid0001");
        event.put("idCooper", "43d62159-7cc9-4576-9d27-a9a3e9ecb628");
        event.put("equeryAlli", "1");

        List list = new ArrayList();
        Map supplyParams1 = new HashMap();
        supplyParams1.put("val", "UUID_TESTH_158");
        supplyParams1.put("name", "uuid");
        Map supplyParams2 = new HashMap();
        supplyParams2.put("val", "958600158");
        supplyParams2.put("name", "rphone");
        list.add(supplyParams1);
        list.add(supplyParams2);

        event.put("supplyParams", list);


        map.put("event", event);
        System.out.println("参数：" + JsonUtil.toJson(map));
        inParameter.put("params", JsonUtil.toJson(map));


        String originContent = HttpClientUtil.httpPost("http://172.19.160.161/data/access", inParameter, 10000);
        System.out.println("返回结果:" + originContent);

    }
}
