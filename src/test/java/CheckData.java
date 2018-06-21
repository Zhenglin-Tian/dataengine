import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-09 17:54
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-09 17:54
 * @updatedRemark:
 * @version:
 */
public class CheckData {
    private static final String dataEngineUrl = "http://172.19.160.162:8080/dp/v1/handle";
    private static final String unionpay_tidy_data_url="http://databooster.tcredit.test/tccpTIDYunionpay";
    private static final String unionpay_var_data_url="http://databooster.tcredit.test/tccpVARunionpay";
    private static final String applyform_unionpay_var_data_url="http://databooster.tcredit.test/tccpVARapplyformUunionpay";
    private static final String applyform_unionpay_model_data_url="http://databooster.tcredit.test/tccpMODELapplyUnionpay001";

    private static final String HBASE_DEFAULT_FAMILY_NAME = "default_column_family";
    private static final String tblName = "std_std_aaaop01_unionpay_var";
    private static final int TIMEOUT=2 * 60 * 1000;
    @Test
    public void checkData() throws Exception {
        List<File> files = files();
        for (File file : files) {
            String s = readData(file);
            Map<String, String> map = JsonUtil.json2Object(s, Map.class);
            String unionpay_list = map.get("unionpay_list");
            map.remove("callrec_list");
            map.remove("sms_list");
            map.remove("contacts_list");
            map.remove("callrec_list");
            map.remove("unionpay_list");
            String data = JsonUtil.toJson(map);
            Map<String, String> param = Maps.newHashMap();
            param.put("mid", "applyform");
            param.put("gid", map.get("gid"));
            param.put("rltStep", "var");
            param.put("data", data);
            String params = JsonUtil.toJson(param);
            System.out.println(params);
            param.clear();
            param.put("param", params);
            System.out.println(JsonUtil.toJson(param));
            String originContent = HttpClientUtil.httpPost(dataEngineUrl, param, TIMEOUT);
            ResponseData response = JsonUtil.json2Object(originContent, ResponseData.class);
            System.out.println(response.getCode()+"----"+response.getMsg());

            /**
             * 入库
             */


            List<Map<String,Object>> list = JsonUtil.json2Object(unionpay_list,List.class);
            List<Map<String,Object>> tblDatas= Lists.newArrayList();
            Map<String,Object> columns_val = Maps.newHashMap();
            for (Map<String,Object> mm:list){

                String key = null;
                String val = "";
                for (Map.Entry<String,Object> cm:mm.entrySet()){
                    if (cm.getKey().equalsIgnoreCase("key")){
                        Object value = cm.getValue();
                        if (value != null){
                            key = value.toString();
                        }
                    }else if (cm.getKey().equalsIgnoreCase("value")){
                        Object value1 = cm.getValue();
                        if (value1 != null){
                            val = value1.toString();
                        }
                    }
                }
                if (key == null) throw new RuntimeException("key 为空");
                columns_val.put(key,val);
            }
            tblDatas.add(columns_val);



            TableData tableData = new TableData();
            tableData.setTableName(tblName);
            tableData.setData(tblDatas);


            HBaseDataProcessUtil.storeTableData(tblName,HBASE_DEFAULT_FAMILY_NAME,map.get("gid").toString(),tableData,Maps.newHashMap());
            System.out.println("----------------------------------"+map.get("gid"));


            param.clear();
            param.put("gid",map.get("gid"));
            param.put("mid","unionpaytransactionvariableb");
            param.put("cmid","unionpaytransactionvariableb");
            originContent = HttpClientUtil.httpPost(unionpay_tidy_data_url, param, TIMEOUT);
            response = JsonUtil.json2Object(originContent, ResponseData.class);
            System.out.println(response.getCode()+"----"+response.getMsg());
            Thread.sleep(1*20*1000);

            originContent = HttpClientUtil.httpPost(unionpay_var_data_url, param, TIMEOUT);
            response = JsonUtil.json2Object(originContent, ResponseData.class);
            System.out.println(response.getCode()+"----"+response.getMsg());

            Thread.sleep(1*20*1000);
            originContent = HttpClientUtil.httpPost(applyform_unionpay_var_data_url, param, TIMEOUT);
            response = JsonUtil.json2Object(originContent, ResponseData.class);
            System.out.println(response.getCode()+"----"+response.getMsg());

            Thread.sleep(1*20*1000);
            originContent = HttpClientUtil.httpPost(applyform_unionpay_model_data_url, param, TIMEOUT);
            response = JsonUtil.json2Object(originContent, ResponseData.class);
            System.out.println(response.getCode()+"----"+response.getMsg());


            Thread.sleep(3000);

        }
    }


    @Test
    public void queryData() throws Exception {
        List<File> files = files();
        List<String> rlt = Lists.newArrayList();
        for (File file : files) {
            String s = readData(file);
            Map<String, String> map = JsonUtil.json2Object(s, Map.class);
            String gid = map.get("gid");
            RetrieveEntity entity = new RetrieveEntity();
            entity.tblName="model_score_score";
            entity.gid = gid;
            List<Map<String, Object>> maps = HBaseDataProcessUtil.queryDataNormal(entity);
            if (!maps.isEmpty()){
                Map<String, Object> map1 = maps.get(0);
                Object gid1 = map1.get("gid");
                Object model_score = map1.get("model_score");
                String s1 = gid1.toString() + "----------->" + model_score.toString();
                rlt.add(s1);
            }
        }

        for (String s:rlt){
            System.out.println(s);
        }
        System.out.println(rlt.size());
    }


    private static String readData(File file) throws Exception {
        if (file == null || !file.exists()) return null;
        FileInputStream in = new FileInputStream(file);
        FileChannel channel = in.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int len = -1;
        StringBuilder sb = new StringBuilder();
        while ((len = channel.read(buf)) != -1) {
            buf.flip();
            byte[] byt = new byte[len];
            buf.get(byt, buf.position(), len);
            String news = new String(byt);
            sb.append(news);
            buf.clear();
        }
        return sb.toString();
    }

    private static List<File> files() {
        List<File> files = Lists.newArrayList();

        String path = CheckData.class.getResource("/").getPath();
        String inputPath = path + "inputdata";
        File file = new File(inputPath);
        if (file.exists()) {
            File[] files1 = file.listFiles();
            files.addAll(Arrays.asList(files1));
        }
        return files;
    }


}
