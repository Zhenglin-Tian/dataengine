package com.tcredit.engine.data_process.mongoDataProcessUtil;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.util.AverageListUtil;
import com.tcredit.engine.util.DateUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;


/**
 * Created by nick on 2016/11/2.
 * 主备模式,增加联合索引： db.collection.ensureIndex({id_coop:1,no_bus:1},{unique:true})
 */
public class MongoDataProcessUtil {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(MongoDataProcessUtil.class);
    private static List<ServerAddress> addresses;
    private static MongoClient client = null;
    public final static String APPLICATION_DB = "rc_dataEngine";
    public final static int OFFSET = 10;

    //private static String APPLICATION_COL = "std_std_ip_beesmell_hitapp_result";

    static {
        logger.info("mongodb client init start ");
        MongoDataProcessUtil.initClient();
        logger.info("mongodb client init end ");
    }

    private static void initClient() {
        if (null == PropertiesUtil.getMap()) {
            PropertiesUtil.init();
        }


        String servers = PropertiesUtil.getString("MONGO_SERVERS");
        //String servers = "172.19.160.182";
        logger.info("mongodb servers地址为: " + servers);
        if (null == client) {
            servers.replaceAll("\\s", "");
            addresses = new ArrayList<>();
            String[] sa = servers.split(",");
            for (String s : sa) {
                String[] ssa = s.split(":");
                String host = ssa[0];
                int port = 27017;
                if (ssa.length > 1) {
                    port = Integer.valueOf(ssa[1]);
                }
                addresses.add(new ServerAddress(host, port));
            }

            MongoClientOptions.Builder builder = MongoClientOptions.builder();
            MongoClientOptions mongoOptions = MongoClientOptions.builder().build();

            client = new MongoClient(addresses, mongoOptions);
            logger.info("mongodb servers 初始化完成 ");
        }
    }


    public static Document get(String db, String col, Bson filter) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> document = dbCol.find(filter).limit(1);
        if (document != null && document.iterator().hasNext()) {
            Document doc = document.iterator().next();
            return doc;
        }
        return null;
    }

    public static Document get(String db, String col) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> document = dbCol.find().limit(1);
        if (document != null && document.iterator().hasNext()) {
            Document doc = document.iterator().next();
            return doc;
        }
        return null;
    }

    public static void put(String db, String col, Document document) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        document.put("tm_isrt", DateUtil.formatDate2StrFromDate(new Date(), DateUtil.DATE_FORMAT_yMdHmsSSS));
        dbCol.insertOne(document);
    }

    public static void putList(String db, String col, List<Document> documents) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        List<List<Document>> lists = AverageListUtil.averageAssign(documents, OFFSET);
        for (int i = 0; i < lists.size(); i++) {
            dbCol.insertMany(lists.get(i));
            logger.info("monog,蜂嗅历史数据入库成功----第" + (i + 1) + "组");
        }
    }

    public static List<Document> query(String db, String col, Bson filter, Bson sort, int limit, int page) {
        List<Document> list = new ArrayList<>();

        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> result = dbCol.find(filter).sort(sort).limit(limit).skip(limit * (page - 1));
        MongoCursor<Document> documents = result.iterator();
        while (documents.hasNext()) {
            list.add(documents.next());
        }
        return list;
    }

    public static List<Document> queryNoLim(String db, String col, Bson filter) {
        List<Document> list = new ArrayList<>();

        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> result = dbCol.find(filter);
        MongoCursor<Document> documents = result.iterator();
        while (documents.hasNext()) {
            list.add(documents.next());
        }
        return list;
    }

    public static List<Document> queryWithNoFilter(String db, String col, Bson sort, int limit, int page) {
        List<Document> list = new ArrayList<>();

        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> result = dbCol.find().sort(sort).limit(limit).skip(limit * (page - 1));
        MongoCursor<Document> documents = result.iterator();
        while (documents.hasNext()) {
            list.add(documents.next());
        }
        return list;
    }

    public static long update(String db, String col, Bson filter, Document document) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        UpdateResult result = dbCol.replaceOne(filter, document);
        return result.getModifiedCount();
    }

    public static long remove(String db, String col, Bson filter) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        DeleteResult result = dbCol.deleteOne(filter);
        return result.getDeletedCount();
    }

    public static long count(String db, String col, Bson filter) {
        return getCollection(db, col).count(filter);
    }

    private static MongoCollection getCollection(String db, String col) {
        return client.getDatabase(db).getCollection(col);
    }

    private static void drop(String db, String col) {
        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        dbCol.drop();
    }

    public static void main(String[] args) {
        /*String noBusMax = "22001102150141994931418";
        String noBusMin = "20161102150141994931418";
        String idCoop = "IDCOOP-0000000000001";

        String firstNoBus=null;*/
       /* ArrayList<Document> list=new ArrayList<>();
        Document document1 = new Document();
        document1.append("gid", "123456");
        document1.append("user", "zhangkan");
        Document document2 = new Document();
        document2.append("gid", "1234567");
        document2.append("user", "zhangkan1");
        list.add(document1);
        list.add(document2);
        MongoDataProcessUtil.putList("rc_dataEngine","test_black_list",list);*/
/*
        Document document = new Document();
        Bson filterOne = and(eq("_id", "12345"));
        document.append("_id", "12345");
        document.append("gid", "123456");
        document.append("user", "zhangkan");
        document.append("pass", "gakki521");
        //MongoDataProcessUtil.update(APPLICATION_DB, "std_std_ip_beesmell_regcanal_result",filterOne, document);
        MongoDataProcessUtil.put("rf_dataEngine", "test", document);*/
//        for (int i = 0; i < 100; i++) {
//            long starTime = System.currentTimeMillis();//当前时间毫秒
//            Bson filterOne = and(eq("gid", "tcgid-2fa9c29567b64aa1b5bd980415f1257d"));
//            Document getResult = MongoDataProcessUtil.get("zhangkan_tc", "std_std_ip_beesmell_regcanal_result", filterOne);
//            long endTime = System.currentTimeMillis();//当前时间毫秒
//            System.out.println((endTime - starTime));
//        }


        PropertiesUtil.getMap();
        long l = System.currentTimeMillis();
        String db="xxxxxxxxxxxx";
        String coll = "xxxxxxxxxxxx";
        String xxx = "{\"regcanal3\":\"0\",\"regcanal4\":\"1\",\"regcanal5\":\"0\",\"regcanal6\":\"0\",\"gid\":\"d4dd7d16b37e4df38b920a7b3bbe685e\",\"regcanal1\":\"1\",\"regcanal2\":\"1\",\"online_report_id\":\"0961526309807945\",\"tid\":\"HIT-053dbb06782b47f8ace28b9edc6d4c07\",\"regcanal12\":\"1\",\"regcanal13\":\"0\",\"regcanal14\":\"0\",\"regcanal15\":\"1\",\"regcanal16\":\"1\",\"regcanal17\":\"0\",\"regcanal18\":\"1\",\"regcanal19\":\"0\",\"tel\":\"14780033750\",\"id\":\"2018051410493772671231322\",\"regcanal7\":\"0\",\"regcanal30\":\"-1\",\"regcanal8\":\"0\",\"regcanal31\":\"\",\"regcanal9\":\"1\",\"regcanal10\":\"1\",\"regcanal11\":\"1\",\"regcanal23\":\"0\",\"regcanal24\":\"0\",\"regcanal25\":\"1\",\"regcanal26\":\"0\",\"regcanal27\":\"0\",\"regcanal28\":\"1\",\"regcanal29\":\"1\",\"regcanal20\":\"0\",\"regcanal21\":\"0\",\"regcanal22\":\"0\",\"tm_isrt\":\"2018-05-14 10:49:30.954\"}\n";
        Document document ;
        for (int i=0; i<100000; i++){
            document = Document.parse(xxx);
            put(db,coll,document);
        }
        long l1 = System.currentTimeMillis();
        System.out.println("------time:"+(l1-l));



       /* Bson filterMany = and(eq("idcard", "321084198907240029"),eq("mobile","13511732441"));
        List<Document> queryResult1 = MongoDataProcessUtil.queryNoLim("rc_dataEngine", "tcredit_black_list", filterMany);
        System.out.println(JsonUtil.toJson(queryResult1));*/
        /*for (int i=0;i<queryResult1.size();i++){
            Document document=queryResult1.get(i);


            System.out.println(JsonUtil.toJson(JsonUtil.json2Object(document.toJson(),Map.class)));



        }*/

        /*Bson filterOne = and(eq("_id", "zhangkan521"));
        Document getResult = MongoDataProcessUtil.get("core_dataEngine", "std_std_ip_beesmell_regcanal_result", filterOne);

        System.out.println(getResult.toJson());*/
    }

    private static void print(List<Document> docs) {
        /*for (Document doc : docs) {
            System.out.println(doc.get("_id") + "\t" + doc.get("id_coop") + "\t" + doc.get("no_bus"));
        }
        System.out.println("\n");*/
    }


    public static List<Document> query(String db, String col, Bson filter) {

        List<Document> list = new ArrayList<>();

        MongoDatabase mdb = client.getDatabase(db);
        MongoCollection<Document> dbCol = mdb.getCollection(col);
        FindIterable<Document> result = dbCol.find(filter);
        MongoCursor<Document> documents = result.iterator();
        while (documents.hasNext()) {
            list.add(documents.next());
        }
        return list;
    }

    public static List<Map<String, Object>> queryDataNormal(RetrieveEntity entity) {

        List<Map<String, Object>> list = new ArrayList<>();

        //构建查询表名
        String tbName = null;
        if (StringUtils.isNotBlank(entity.step) && StringUtils.isNotBlank(entity.db)) {
            tbName = entity.step + "_" + entity.db + "_" + entity.tblName;
        } else {
            tbName = entity.tblName;
        }

        //确定查询id的值（rid不为空则为rid的值否则为gid的值）
        String id = StringUtils.isNotBlank(entity.rid) ? entity.rid : entity.gid;

        //查询
        Bson filterOne = and(eq("_id", id));
        Document getResult = MongoDataProcessUtil.get(MongoDataProcessUtil.APPLICATION_DB, tbName.toLowerCase(), filterOne);
        //将查询结果转化为map
        Map<String, Object> map = JsonUtil.json2Object(JsonUtil.toJson(getResult), Map.class);
        //移除查询结果当中的_id
        if (map != null) {
            map.remove("_id");
            list.add(map);
        }

        return list;
    }


    public static List<Map<String, Object>> queryMapNoLim(String idcard, String mobile, String tbName) {
        List<Map<String, Object>> list = new ArrayList<>();
        Bson filterMany = null;

        if (checkParam(idcard) && checkParam(mobile)) {
            filterMany = and(eq("idcard", idcard), eq("tel", mobile));
        }
        if (checkParam(idcard) && !checkParam(mobile)) {
            filterMany = and(eq("idcard", idcard));
        }
        if (!checkParam(idcard) && checkParam(mobile)) {
            filterMany = and(eq("tel", mobile));
        }


        List<Document> queryResult = MongoDataProcessUtil.queryNoLim(APPLICATION_DB, tbName, filterMany);
        for (int i = 0; i < queryResult.size(); i++) {
            Document document = queryResult.get(i);
            Map<String, Object> map = JsonUtil.json2Object(document.toJson(), Map.class);
            map.remove("_id");
            list.add(map);
        }

        return list;
    }

    public static boolean checkParam(String param) {
        if (param == null || StringUtils.isBlank(param) || param.equals("null")) {
            return false;
        }
        return true;
    }

}
