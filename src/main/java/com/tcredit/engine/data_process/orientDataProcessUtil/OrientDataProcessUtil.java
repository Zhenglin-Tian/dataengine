package com.tcredit.engine.data_process.orientDataProcessUtil;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tcredit.engine.util.PropertiesUtil;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class OrientDataProcessUtil {
    private static final org.apache.log4j.Logger logger = LogManager.getLogger(OrientDataProcessUtil.class);


    private static final int EVENT_CLUSTER_INIT_SIZE_FOR_SHARD = 1;
    public static final String RC_DATAENGINE = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_NAME"));
    public static final String RC_APPLYFORM = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_NAME1"));

    private static final String DB_CON = "/";
    private static final String DB_NODE_SPLIT = ";";
    /**
     * 库名 -- 工厂
     **/
    private static Map<String, OPartitionedDatabasePool> docDBFactories = Maps.newHashMap();

    public static OPartitionedDatabasePool getFactory(String dbName) {


        return docDBFactories.get(dbName);
    }

    static {

        DbSetting setting = new DbSetting();
        setting.maxPoolSize = Integer.valueOf(PropertiesUtil.getString("ORIENTDB_DB_MAXPOOLSIZE"));
        setting.minPoolSize = Integer.valueOf(PropertiesUtil.getString("ORIENTDB_DB_MINPOOLSIZE"));
        setting.remote = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_REMOTE"));
        setting.name = RC_DATAENGINE;
        setting.user = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_USER"));
        setting.passowrd = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_PASSOWRD"));
        setting.loadBalanceStrategy = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_LOADBALANCESTRATEGY"));
        setting.useLightweightEdges = Boolean.valueOf(PropertiesUtil.getString("ORIENTDB_DB_USELIGHTWEIGHTEDGES"));

        /** 初始化数据库工厂  **/
        init(setting);
        DbSetting setting1 = new DbSetting();
        setting1.maxPoolSize = Integer.valueOf(PropertiesUtil.getString("ORIENTDB_DB_MAXPOOLSIZE1"));
        setting1.minPoolSize = Integer.valueOf(PropertiesUtil.getString("ORIENTDB_DB_MINPOOLSIZE1"));
        setting1.remote = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_REMOTE1"));
        setting1.name = RC_APPLYFORM;
        setting1.user = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_USER1"));
        setting1.passowrd = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_PASSOWRD1"));
        setting1.loadBalanceStrategy = String.valueOf(PropertiesUtil.getString("ORIENTDB_DB_LOADBALANCESTRATEGY1"));
        setting1.useLightweightEdges = Boolean.valueOf(PropertiesUtil.getString("ORIENTDB_DB_USELIGHTWEIGHTEDGES1"));

        /** **/
        init(setting1);


    }

    public static void shutdown(String dbName) {
        OPartitionedDatabasePool factory = getFactory(dbName);
        if (factory != null) {
            factory.close();
        }
    }


    private static void createDatabaseIfNeed(String dbUrl) {
        boolean finish = true;
        OServerAdmin serverAdmin = null;
        try {

            serverAdmin = new OServerAdmin(dbUrl).connect(DbSetting.user, DbSetting.passowrd);
            if (!serverAdmin.existsDatabase("plocal")) {
                serverAdmin.createDatabase(DbSetting.name, "document", "plocal");
            }


            /*
            int first = DbSetting.remote.indexOf(DB_REMOTE_SPLIT);
            String[] serverNodes = DbSetting.remote.substring(first + 1).split(DB_NODE_SPLIT);

            for (String serverNode : serverNodes) {
                if (StringUtils.isNotBlank(serverNode)) {
                    serverAdmin = new OServerAdmin("remote:" + serverNode + DB_CON + DbSetting.name).connect(DbSetting.user, DbSetting.passowrd);
                    if (!serverAdmin.existsDatabase()) {
                        serverAdmin.createDatabase(DbSetting.name, "graph", "plocal");
                    }
                    serverAdmin.close();
                }
            }
            */
            /*
            OrientBaseGraph orientGraph = new OrientGraphNoTx(getDBURL());
            orientGraph.command(new OCommandSQL("ALTER DATABASE custom strictSQL=false")).execute();
            orientGraph.shutdown();
            */
        } catch (Exception e) {
            e.printStackTrace();
            finish = false;
            logger.error("<init orient database fail,please check the env and config>");

        } finally {
            if (finish) {
                logger.info("database check: database init success>");
            }
            if (serverAdmin != null) {
                serverAdmin.close(true);
            }
        }

    }

    private static OPartitionedDatabasePool initDbFactory(String dbUrl) {
        return new OPartitionedDatabasePool(dbUrl, DbSetting.user, DbSetting.passowrd, DbSetting.minPoolSize, DbSetting.maxPoolSize);
    }

//    private static synchronized void initDbSchema(String dbUrl) {
//        ODatabaseDocumentTx acquire = factory.acquire();
//        createIndexIfNeed(acquire);
//        acquire.close();
//    }


    /**
     * 创建索引
     *
     * @param acquire
     */
    private static void createIndexIfNeed(ODatabaseDocumentTx acquire) {

        try {

            //1.获取类
            OClass rc_applyform = acquire.getMetadata().getSchema().getClass("rc_applyform");
            if (rc_applyform != null) {

                //2.获取索引
                if (rc_applyform.getClassIndex("date_inst_index") == null) {
                    //创建property
                    rc_applyform.createProperty("date_inst", OType.STRING);
                    rc_applyform.createIndex("date_inst_index", OClass.INDEX_TYPE.NOTUNIQUE, "date_inst");
                }

                if (rc_applyform.getClassIndex("gid_index") == null) {
                    //创建property
                    rc_applyform.createProperty("gid", OType.STRING);
                    rc_applyform.createIndex("gid_index", OClass.INDEX_TYPE.NOTUNIQUE, "gid");
                }

                if (rc_applyform.getClassIndex("seq_num_index") == null) {
                    //创建property
                    rc_applyform.createProperty("seq_num", OType.STRING);
                    rc_applyform.createIndex("seq_num_index", OClass.INDEX_TYPE.NOTUNIQUE, "seq_num");
                }

            }
        } catch (Exception e) {
            logger.error("创建orientDb所以异常", e);
        }


    }

    /**
     * ClusterSetting.EVENT_CLUSTERS + EVENT_CLUSTER_INIT_SIZE_FOR_SHARD = 5  which is not we expected clusters number.
     * find out the cause later.
     *
     * @param graph
     */
    private static void createClusterAndEventIfNeed(OrientBaseGraph graph) {

//        OrientVertexType vtEvent = graph.createVertexType("Event",EVENT_CLUSTER_INIT_SIZE_FOR_SHARD);
//        vtEvent.createProperty("ei", OType.STRING);
//        vtEvent.createProperty("idCooper", OType.STRING);
//        vtEvent.createProperty("ts", OType.INTEGER);
//
//        graph.commit();
//
//        int[] idsSystemCreated = vtEvent.getClusterIds();
//        Assert.isTrue(idsSystemCreated.length == EVENT_CLUSTER_INIT_SIZE_FOR_SHARD);
//
//        for (String cluster : ClusterSetting.EVENT_CLUSTERS) {
//            vtEvent.addCluster(cluster);
//        }
//
//        int[] idsNew = vtEvent.getClusterIds();
//        Assert.isTrue(idsNew.length == EVENT_CLUSTER_INIT_SIZE_FOR_SHARD + ClusterSetting.EVENT_CLUSTERS.size());
//
//        for (int id : idsSystemCreated) {
//            vtEvent.removeClusterId(id);
//        }
//
//        int[] idsFinal = vtEvent.getClusterIds();
//        Assert.isTrue(idsFinal.length == ClusterSetting.EVENT_CLUSTERS.size());


        logger.info("first start service,event cluster and event scheme created success.");
    }


    /**
     * check th db is-online, the first time use the db-pool at the same time
     *
     * @return
     */
//    public static boolean preheatDbPool(){
//        OrientGraphNoTx graphNoTx =  OrientGraphFactoryManager.getNoTx();
//        OCommandRequest request = graphNoTx.command(new OCommandSQL("SELECT * FROM V LIMIT 1"));
//        Object RET =  request.execute();
//        graphNoTx.shutdown();
//        if (RET != null) {
//            logger.info("preheat check : database is online.");
//            return true;
//        } else {
//            logger.info("preheat check : database is offline.");
//            return false;
//        }
//    }
    public static List<String> selectRowKeyListByTel(String tel) {
        ODatabaseDocumentTx database = OrientDataProcessUtil.getFactory(RC_DATAENGINE).acquire();
        List<String> list = new ArrayList<>();
        List<ODocument> result = database.command(
                new OSQLSynchQuery<ODocument>("select rowKey from std_std_ip_beesmell_regcanal_result where tel='" + tel + "'")).execute();


        for (ODocument oDocument : result) {
            list.add(oDocument.field("rowKey").toString());
        }
        database.close();
        return list;
    }

    public static void main(String[] args) {
//        PropertiesUtil.getMap();
//        ODatabaseDocumentTx database = OrientDataProcessUtil.getFactory().acquire();
//        ODocument entries = new ODocument("std_std_ip_beesmell_regcanal_result");
//        Document document1 = new Document();
//        document1.put("rowkey", "1234");
//        document1.put("tel", "15232035034");
//        entries.fromJSON(JsonUtil.toJson(document1));
//        entries.save();
//
//        List<ODocument> result = database.command(
//                new OSQLSynchQuery<ODocument>("select * from std_std_ip_beesmell_regcanal_result where tel='17600903048'")).execute();
//
//
//        for (ODocument oDocument : result) {
//
//            System.out.println(oDocument.field("tel").toString());
//        }
//        database.close();
        /*PropertiesUtil.getMap();
        long l = System.currentTimeMillis();
        String xxx = "{\"_id\":\"0961526309807945\",\"regcanal3\":\"0\",\"regcanal4\":\"1\",\"regcanal5\":\"0\",\"regcanal6\":\"0\",\"gid\":\"d4dd7d16b37e4df38b920a7b3bbe685e\",\"regcanal1\":\"1\",\"regcanal2\":\"1\",\"online_report_id\":\"0961526309807945\",\"tid\":\"HIT-053dbb06782b47f8ace28b9edc6d4c07\",\"regcanal12\":\"1\",\"regcanal13\":\"0\",\"regcanal14\":\"0\",\"regcanal15\":\"1\",\"regcanal16\":\"1\",\"regcanal17\":\"0\",\"regcanal18\":\"1\",\"regcanal19\":\"0\",\"tel\":\"14780033750\",\"id\":\"2018051410493772671231322\",\"regcanal7\":\"0\",\"regcanal30\":\"-1\",\"regcanal8\":\"0\",\"regcanal31\":\"\",\"regcanal9\":\"1\",\"regcanal10\":\"1\",\"regcanal11\":\"1\",\"regcanal23\":\"0\",\"regcanal24\":\"0\",\"regcanal25\":\"1\",\"regcanal26\":\"0\",\"regcanal27\":\"0\",\"regcanal28\":\"1\",\"regcanal29\":\"1\",\"regcanal20\":\"0\",\"regcanal21\":\"0\",\"regcanal22\":\"0\",\"tm_isrt\":\"2018-05-14 10:49:30.954\"}\n";
        ODatabaseDocument acquire = OrientDataProcessUtil.getFactory().acquire();
        ODocument document = new ODocument();
        acquire.declareIntent(new OIntentMassiveInsert());
        for (int i = 0; i < 10; i++) {
            document.reset();
            document.setClassName("xxx517");

            document.fromJSON(xxx);
            document.save();

        }
        acquire.declareIntent(null);
        acquire.close();
        long l1 = System.currentTimeMillis();
        System.out.println("------time:" + (l1 - l));*/

//        ODatabaseDocumentTx database = OrientDataProcessUtil.getFactory().acquire();
//        for (int i = 0; i < 100; i++) {
//            long l = System.currentTimeMillis();
//            List<ODocument> result = database.command(
//                    new OSQLSynchQuery<ODocument>("select rowKey from std_std_ip_beesmell_regcanal_result where tel='17600903048'")).execute();
//            long l1 = System.currentTimeMillis();
//            System.out.println("------time:" + (l1 - l));
//        }
//
//        database.close();


    }

    private static void init(DbSetting settings) {
        String dbUrl = "remote:" + settings.remote + DB_CON + settings.name;
        OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(-1);
        createDatabaseIfNeed(dbUrl);
        OPartitionedDatabasePool factory = initDbFactory(dbUrl);
        docDBFactories.put(settings.name, factory);
    }


}
