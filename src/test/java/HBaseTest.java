import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.data_process.DataStorageHbase;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.dbEntity.AntifraudRetrieveEntity;
import com.tcredit.engine.response.TableData;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.apache.hadoop.hbase.client.Connection;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-24 下午4:40
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-24 下午4:40
 * @updatedRemark:
 * @version:
 */
public class HBaseTest {
    private static Configuration configuration;
    // private static Connection conn;
    private static String hbaseMaster1 = "172.19.160.169:60000";
    private static String hbaseMaster2 = "172.19.160.169:60000";
    private static String hbaseZk1 = "172.19.160.209:2181,172.19.160.208:2181,172.19.160.210:2181";
    private static String hbaseZk2 = "172.19.160.167:2181,172.19.160.168:2181,172.19.160.169:2181";

    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.master", hbaseMaster1);
        configuration.set("hbase.zookeeper.quorum", hbaseZk2);


        /*Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "这里设置你们集群的地址");
        //config.set("hbase.master", "10.141.68.47:60000");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.master.port", "60000");
        config.set("hbase.master.info.bindAddress", "hmaster绑定地址");
        config.set("hbase.master.info.port", "60010");
        config.set("zookeeper.znode.parent", "/hbase-unsecure");
        Connection con = ConnectionFactory.createConnection(config);
        Admin admin = con.getAdmin();*/
    }


    public static void main(String[] args) throws Exception {

        String[] fs = {"defaultFamily"};
        String tableName = "testbytzl";


        DataStorageHbase dataStorageHbase = new DataStorageHbase();
        for (int i = 0; i<1000000; i++) {
            String gid = UUID.randomUUID().toString().replaceAll("-","");
            System.out.println(gid);
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("gid", gid);
                put("uuid", "222222222222222222222222");
                put("name", "xxxtest");
                put("age", null);
                put("sex", "f");
            }};
            Map<String, Object> map2 = new HashMap<String, Object>() {{
                put("gid", gid);
                put("uuid", "111111111111111111");
                put("name", "xxxtest");
                put("age", null);
                put("sex", "m");
            }};
            List<Map<String,Object>> datas = Lists.newArrayList();
            datas.add(map);
            datas.add(map2);
            TableData tableData = new TableData();
            tableData.setDbName("tzl");
            tableData.setTableName(tableName);
            tableData.setData(datas);
           dataStorageHbase.storage(gid,"xxx","test",tableData,Maps.newHashMap());
        }


//        createTable(tableName, fs);
//        Map<String, Object> map = new HashMap<String, Object>() {{
//            put("gid", "xxxxxxxxxxxxxxxxxxxxxxxxx3");
//            put("uuid", "222222222222222222222222");
//            put("nameinvar", null);
//            put("namerevar", "xxxx");
//            put("valuerevar", 0);
//        }};
//        // addData(tableName,"defaultFamily",map);
//        Result rlt = getResult(tableName, "xxxxxxxxxxxxxxxxxxxxxxxxx3");
//        List<Cell> cells = rlt.listCells();
//        for (Cell cell : cells) {
//            printCell(cell);
//        }


//        List<String> arr = new ArrayList<String>();
//        arr.add("default_column_family,gid,2");
//        arr.add("default_column_family,namerevar,na_cdmdd406maabww");
//        queryByFilter(tableName, arr);




        long l = System.currentTimeMillis();
        AntifraudRetrieveEntity e = new AntifraudRetrieveEntity();
//        e.gid = "1";
        e.step = "std";
        e.db = "std";
        e.tblName = "antifraud_variable";
        e.bid = "all";
        e.gid = "3";
        e.nameRevar = "na_cpacdalchisaabww,na_cdmdd406maabww";
        e.columns.add("nameinvar");
        e.columns.add("gid");
        e.columns.add("bid");
        e.columns.add("uuid");
//        e.nameInvar = "rphone";
//        List<Map<String, Object>> maps = queryAntifraudVariableDataByFilter(e);
        List<Map<String, Object>> maps = HBaseDataProcessUtil.queryAntifraudVariableDataByFilter(e);
        System.out.println(maps.size());

        long l1 = System.currentTimeMillis();
        System.out.println(l1-l);




        long l2 = System.currentTimeMillis();
        List<Map<String, Object>> map2 = HBaseDataProcessUtil.queryDataByRowKey("0208test14", "std_std_antifraud_variable");
        System.out.println(map2.size());

        long l3 = System.currentTimeMillis();
        System.out.println(l3-l2);

        long l4 = System.currentTimeMillis();
        List<Map<String, Object>> map3 = HBaseDataProcessUtil.queryDataByGid2("0208test14", "std_std_antifraud_variable");
        System.out.println(map3.size());

        long l5 = System.currentTimeMillis();
        System.out.println(l5-l4);

//        String[] cs = {"column1", "column2", "column3"};
//
//
//
//        HTableDescriptor htds = new HTableDescriptor(TableName.valueOf(tableName));
//        Result result = getResult(tableName, "112");
//        System.out.println(result.toString());
//


    }


    //根据rowkey查询
    public static Result getResult(String tableName, String rowkey) throws IOException {
        Get get = new Get(Bytes.toBytes(rowkey));
        HTable hTable = new HTable(configuration, Bytes.toBytes(tableName));
        Result result = hTable.get(get);
        return result;
    }

    public static void printKV(KeyValue keyValue) {
        System.out.println(Bytes.toString(keyValue.getRowArray()) +
                "\t" + Bytes.toString(keyValue.getFamilyArray()) +
                "\t" + Bytes.toString(keyValue.getQualifierArray()) +
                "\t" + Bytes.toString(keyValue.getValueArray()) +
                "\t" + keyValue.getTimestamp()
        );
    }

    public static void printCell(Cell cell) {
        System.out.println(Bytes.toString(cell.getRow()) +
                "\t" + Bytes.toString(cell.getFamily()) +
                "\t" + Bytes.toString(cell.getQualifier()) +
                "\t" + Bytes.toString(cell.getValue()) +
                "\t" + cell.getTimestamp()
        );
    }

    public static void createTable(String tableName, String[] family) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(configuration);
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        for (String s : family) {
            desc.addFamily(new HColumnDescriptor(s));
        }

        if (admin.tableExists(tableName)) {
            System.out.println("table Exists");
        } else {
            admin.createTable(desc);
            System.out.println("create table Success");
        }
    }

    public static void addData(String tableName, String familyName, Map<String, Object> datas) throws IOException {
        Put put = new Put(Bytes.toBytes(datas.get("gid").toString()));
        HTable table = new HTable(configuration, Bytes.toBytes(tableName));
        HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();
        for (HColumnDescriptor hcd : columnFamilies) {
            String tblFamilyName = hcd.getNameAsString();
            if (tblFamilyName.equalsIgnoreCase(familyName)) {
                for (Map.Entry<String, Object> e : datas.entrySet()) {
                    String key = e.getKey();
                    Object value = e.getValue();
                    if (value == null) {
                        value = "";
                    }
                    put.add(Bytes.toBytes(familyName), Bytes.toBytes(key), Bytes.toBytes(value.toString()));
                }
            }
        }
        table.put(put);
        System.out.println("add data Success");


    }


    public static void queryByFilter(String tableName, List<String> aar) throws IOException {
        HTable table = new HTable(configuration, tableName);
        FilterList filterList = new FilterList();
        Scan scan = new Scan();
        for (String s : aar) {
            String[] split = s.split(",");
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(split[0]),
                    Bytes.toBytes(split[1]),
                    CompareFilter.CompareOp.EQUAL,
                    Bytes.toBytes(split[2]));
            filter.setFilterIfMissing(false);
            filterList.addFilter(filter);
        }
        scan.setFilter(filterList);
        ResultScanner scanner = table.getScanner(scan);

        for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            for (KeyValue kv : rr.list()) {
                System.out.print("row : " + new String(kv.getRow()));
                System.out.print(" column : " + new String(kv.getQualifier()));
                System.out.print(" value : " + new String(kv.getValue()));
                System.out.println();
            }
            System.out.println();
        }

    }

    public static List<Map<String, Object>> queryAntifraudVariableDataByFilter(AntifraudRetrieveEntity entity) throws IOException {
        if (entity == null) return null;
        String familyName = "default_column_family";

        String tableName = (entity.step + "_" + entity.db + "_" + entity.tblName).toLowerCase();
        FilterList filterList = new FilterList();
        if (StringUtils.isNotBlank(entity.gid)) {
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(familyName),
                    Bytes.toBytes("gid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.gid));
            filterList.addFilter(filter);
        }
        if (StringUtils.isNotBlank(entity.bid)) {
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(familyName),
                    Bytes.toBytes("bid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.bid));
            filterList.addFilter(filter);
        }
        if (StringUtils.isNotBlank(entity.nameRevar)) {
            String[] split = entity.nameRevar.split(",");
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String s : split) {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(familyName),
                        Bytes.toBytes("namerevar"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(s));
                list.addFilter(filter);
            }
//            FilterList newList = new FilterList(FilterList.Operator.MUST_PASS_ONE, list);
            filterList.addFilter(list);
        }

        if (StringUtils.isNotBlank(entity.nameInvar)) {
            String[] split = entity.nameInvar.split(",");
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String s : split) {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(familyName),
                        Bytes.toBytes("nameinvar"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(s));
                list.addFilter(filter);
            }
//            FilterList newList = new FilterList(FilterList.Operator.MUST_PASS_ONE, list);
            filterList.addFilter(list);
        }

        if (entity.columns != null && !entity.columns.isEmpty()) {
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String column : entity.columns) {
                if (StringUtils.isNotBlank(column)) {
                    QualifierFilter qf = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(column)));
                    list.addFilter(qf);
                }
            }
            filterList.addFilter(list);
        }


        HTable table = new HTable(configuration, tableName);
        Scan scan = new Scan();
        scan.setFilter(filterList);
        ResultScanner scanner = table.getScanner(scan);
        return fetchColumns(scanner);

    }

    private static List<Map<String, Object>> fetchColumns(ResultScanner scanner) {
        List<Map<String, Object>> datas = Lists.newArrayList();
        if (scanner != null) {
            try {
                for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                    Map<String, Object> data = Maps.newHashMap();
                    for (Cell cell : rr.listCells()) {
                        String columnName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                        String columnVal = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        data.put(columnName, columnVal);
                    }
                    datas.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return datas;
    }


}