package com.tcredit.engine.data_process.hbaseDataProcessUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.data_process.DataStorage;
import com.tcredit.engine.data_process.RecordColumnsUtil;
import com.tcredit.engine.data_process.mongoDataProcessUtil.MongoDataProcessUtil;
import com.tcredit.engine.dbEntity.AntifraudRetrieveEntity;
import com.tcredit.engine.dbEntity.RetrieveEntity;
import com.tcredit.engine.processService.impl.BlackListTreServiceImpl;
import com.tcredit.engine.response.TableData;
import com.tcredit.engine.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.bson.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-03 17:26
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-03 17:26
 * @updatedRemark:
 * @version:
 */
public class HBaseDataProcessUtil {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(HBaseDataProcessUtil.class);
    private static Configuration configuration;
    private static String hbaseMaster = PropertiesUtil.getString("HBASE_MASTER");
    private static String hbaseZk = PropertiesUtil.getString("HBASE_ZOOKEEPER");
    private static String FAMILY_NAME = DataStorage.HBASE_DEFAULT_FAMILY_NAME;
    private static HBaseAdmin admin;

    static {
        configuration = HBaseConfiguration.create();
//        configuration.set("hbase.master", hbaseMaster);
        configuration.set("hbase.zookeeper.quorum", hbaseZk);
        try {
            admin = new HBaseAdmin(configuration);
        } catch (IOException e) {
            throw new RuntimeException("初始化hbase连接时出错");
        }
    }


    public static void createTable(String tableName, String[] family) throws IOException {
        if (StringUtils.isBlank(tableName) || family == null || family.length == 0) return;
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        for (String s : family) {
            desc.addFamily(new HColumnDescriptor(s));
        }

        if (admin.tableExists(tableName)) {
            LOGGER.info(String.format("hbase创建表:%s,列族:%s,存在", tableName, family));
        } else {
            admin.createTable(desc);
            LOGGER.info(String.format("hbase创建表:%s,列族:%s,创建成功", tableName, family));
        }
    }


    public static void storeTableData(String tableName, String familyName, String rowKeyBase, TableData tableData, Map<String, Object> otherData) throws IOException {
        HTable table = new HTable(configuration, Bytes.toBytes(tableName));
        table.setAutoFlush(false, true);
        table.setWriteBufferSize(64 * 1024 * 1024);
        try {
            HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();
            for (HColumnDescriptor hcd : columnFamilies) {
                String tblFamilyName = hcd.getNameAsString();
                if (tblFamilyName.equalsIgnoreCase(familyName)) {
                    List<String> columns = RecordColumnsUtil.getRecordColumns(tableData);
                    /**
                     * rowKey后缀初始值
                     */
                    int i = 10000;
                    List<Put> puts = Lists.newArrayList();
                    for (Map<String, Object> data : tableData.getData()) {
                        /**
                         * 一行记录 rowkeybase+4为数字从0000-9999
                         */
                        //生成rowKey
                        String rowKey = generateRowKey(rowKeyBase, i);

                        Put put = new Put(Bytes.toBytes(rowKey));
                        for (String column : columns) {
                            Object val = data.get(column);
                            for (String tm : PropertiesUtil.getTm_columns()) {
                                if (tm.equalsIgnoreCase(column) && val == null) {
                                    val = DateUtil.formatDate2StrFromDate(new Date(), DateUtil.DATE_FORMAT_yMdHmsSSS);
                                }
                            }
                            if (val == null || StringUtils.isBlank(val.toString())) {
                                val = otherData.get(column);
                            }
                            if (val == null) {
                                //column.trim().toLowerCase()变为column.trim()
                                put.add(Bytes.toBytes(familyName), Bytes.toBytes(column.trim()), Bytes.toBytes(""));
                            } else {
                                if (val instanceof List) {
                                    String json = JsonUtil.toJson(val);
                                    put.add(Bytes.toBytes(familyName), Bytes.toBytes(column.trim()), Bytes.toBytes(json));
                                } else if (val instanceof Map) {
                                    String json = JsonUtil.toJson(val);
                                    put.add(Bytes.toBytes(familyName), Bytes.toBytes(column.trim()), Bytes.toBytes(json));
                                } else {
                                    put.add(Bytes.toBytes(familyName), Bytes.toBytes(column.trim()), Bytes.toBytes(val.toString()));

                                }
                                //column.trim().toLowerCase()变为column.trim()
                            }

                        }
                        puts.add(put);
                        i++;
                    }
                    table.put(puts);
                    table.flushCommits();
                }
            }
        } finally {
            table.close();
        }

    }


    /**
     * 多条件查询反欺诈变量
     *
     * @param entity
     * @return
     * @throws IOException
     */
    @Deprecated
    public static List<Map<String, Object>> queryAntifraudVariableDataByFilter(AntifraudRetrieveEntity entity) throws IOException {
        if (entity == null) return null;

        /**
         * 生成表名  阶段_库_表名
         */
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(entity.step)) {
            sb.append(entity.step).append(DataStorage.SPLIT_LINE);
        }
        if (StringUtils.isNotBlank(entity.db)) {
            sb.append(entity.db).append(DataStorage.SPLIT_LINE);
        }
        sb.append(entity.tblName);

        String tableName = sb.toString().toLowerCase();


        FilterList filterList = new FilterList();
        if (StringUtils.isNotBlank(entity.gid)) {
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
                    Bytes.toBytes("gid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.gid));
            filter.setFilterIfMissing(true);
            filterList.addFilter(filter);
        }
        if (StringUtils.isNotBlank(entity.bid)) {
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
                    Bytes.toBytes("bid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.bid));
            filter.setFilterIfMissing(true);
            filterList.addFilter(filter);
        }
        if (StringUtils.isNotBlank(entity.nameRevar)) {
            String[] split = entity.nameRevar.split(",");
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String s : split) {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
                        Bytes.toBytes("namerevar"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(s));
                filter.setFilterIfMissing(true);
                list.addFilter(filter);
            }
//            FilterList newList = new FilterList(FilterList.Operator.MUST_PASS_ONE, list);
            filterList.addFilter(list);
        }

        if (StringUtils.isNotBlank(entity.nameInvar)) {
            String[] split = entity.nameInvar.split(",");
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String s : split) {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
                        Bytes.toBytes("nameinvar"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(s));
                filter.setFilterIfMissing(true);
                list.addFilter(filter);
            }
//            FilterList newList = new FilterList(FilterList.Operator.MUST_PASS_ONE, list);
            filterList.addFilter(list);
        }

        /**
         * 查询指定的列
         */
        Filter filter = filterColumns(entity.columns);
        if (filter != null) {
            filterList.addFilter(filter);
        }

        HTable table = new HTable(configuration, tableName);
        try {


            Scan scan = new Scan();
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);

            return fetchColumns(scanner);
        } finally {
            table.close();
        }

    }


    /**
     * 根据rowkey的startRow和stopRow来查询数据
     *
     * @param rowKeyBase
     * @param tableName
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> queryDataByRowKey(String rowKeyBase, String tableName) throws IOException {
        long startTime = System.currentTimeMillis();
        HTable table = new HTable(configuration, tableName);
        Scan scan = new Scan();
        String startRow = rowKeyBase + "-10000";
        String endRow = rowKeyBase + "-99999";
        scan.setStartRow(startRow.getBytes());
        scan.setStopRow(endRow.getBytes());
        ResultScanner scanner = table.getScanner(scan);
        long endTime = System.currentTimeMillis();
        LOGGER.info("gid:{},rowkeyBase:{},查询表:{}，用时:{}ms", rowKeyBase, rowKeyBase, tableName, endTime - startTime);
        return fetchColumns(scanner);
    }

    /**
     * 根据rowKey查询数据
     *
     * @param rowKey
     * @param tableName
     * @return
     * @throws IOException
     */
    public static Map<String, Object> queryDataByRowKey(String gid, String rowKey, String tableName) throws IOException {
        if (StringUtils.isBlank(rowKey) || StringUtils.isBlank(tableName))
            throw new RuntimeException("rowKey|tableName为空");
        long startTime = System.currentTimeMillis();
        HTable table = new HTable(configuration, tableName);
        try {
            Result result = table.get(new Get(rowKey.getBytes()));
            Map<String, Object> map = fetchColumns(result, null);
            long endTime = System.currentTimeMillis();
            LOGGER.info("gid:{},rowKey:{},查询表:{}，用时:{}ms", gid, rowKey, tableName, endTime - startTime);
            return map;
        } finally {
            table.close();
        }
    }

    public static Map<String, Object> queryMapByRowKey(String gid, String rowKey, String tableName) throws IOException {
        if (StringUtils.isBlank(rowKey) || StringUtils.isBlank(tableName))
            throw new RuntimeException("rowKey|tableName为空");
        long startTime = System.currentTimeMillis();
        HTable table = new HTable(configuration, tableName);
        try {
            Result result = table.get(new Get(rowKey.getBytes()));
            Map<String, Object> map = fetchColumns(result, null);
            if (map.size() != 0) {
                return map;
            }
            long endTime = System.currentTimeMillis();
            LOGGER.info("gid:{},rowKey:{},查询表:{}，用时:{}ms", gid, rowKey, tableName, endTime - startTime);
            return null;
        } finally {
            table.close();
        }
    }


    /**
     * 根据rowKey查询数据
     *
     * @param rowKeyPrefix
     * @param tableName
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> queryDataContainsRowkey(String gid, String rowKeyPrefix, String tableName) throws IOException {
        if (StringUtils.isBlank(rowKeyPrefix) || StringUtils.isBlank(tableName))
            throw new RuntimeException("rowKey|tableName为空");
        long startTime = System.currentTimeMillis();
        HTable table = new HTable(configuration, tableName);
        try {
//            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyPrefix));
            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(rowKeyPrefix));

            Scan scan = new Scan();
            scan.setFilter(filter);
            ResultScanner result = table.getScanner(scan);
            List<Map<String, Object>> list = fetchColumns(result);
            long endTime = System.currentTimeMillis();
            LOGGER.info("gid:{},rowKey:{},查询表:{}，用时:{}ms", gid, rowKeyPrefix, tableName, endTime - startTime);
            return list;
        } finally {
            table.close();
        }
    }


    /**
     * 根据filter过滤rowkey
     *
     * @param gid
     * @param tableName
     * @return
     * @throws IOException
     */
    @Deprecated
    public static List<Map<String, Object>> queryDataByGid2(String gid, String tableName) throws IOException {
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(gid.getBytes()));
        HTable table = new HTable(configuration, tableName);
        try {
            Scan scan = new Scan();
            scan.setFilter(filter);
            ResultScanner scanner = table.getScanner(scan);
            return fetchColumns(scanner);
        } finally {
            table.close();
        }

    }


    /**
     * @param entity
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> queryDataNormal(RetrieveEntity entity) throws IOException {
        if (entity == null) return null;

        /**
         * 生成表名
         */
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(entity.step)) {
            sb.append(entity.step).append(DataStorage.SPLIT_LINE);
        }
        if (StringUtils.isNotBlank(entity.db)) {
            sb.append(entity.db).append(DataStorage.SPLIT_LINE);
        }
        sb.append(entity.tblName);

        String tableName = sb.toString().toLowerCase();


//        FilterList filterList = new FilterList();
//        if (StringUtils.isNotBlank(entity.gid)) {
//            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
//                    Bytes.toBytes("gid"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.gid));
//            filter.setFilterIfMissing(true);
//            filterList.addFilter(filter);
//        }
//
//
//
//        if (StringUtils.isNotBlank(entity.modelId)) {
//            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(FAMILY_NAME),
//                    Bytes.toBytes("model_id"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(entity.modelId));
//            filter.setFilterIfMissing(true);
//            filterList.addFilter(filter);
//        }
//
//        /**
//         * 查询指定的列
//         */
//        Filter filter = filterColumns(entity.columns);
//        if (filter != null) {
//            filterList.addFilter(filter);
//        }


        List<Map<String, Object>> maps = Lists.newArrayList();
        List<Get> queryGets = Lists.newArrayList();
        int startIndex = 10000;
        boolean queryGoOn = false;
        HTable table = new HTable(configuration, tableName);
        try {
            String rowKeyBase = StringUtils.isNotBlank(entity.rid) ? entity.rid : entity.gid;
            if (DataStorage.MODEL_STEP_TABLE_NAME.equalsIgnoreCase(tableName)) {
                rowKeyBase = MD5_HMC_EncryptUtils.getMd5(rowKeyBase + "-" + entity.modelId, 1);
            }

            do {
                startIndex = createQueryGets(rowKeyBase, startIndex, startIndex + 99, queryGets);
                if (queryGets != null && !queryGets.isEmpty()) {
                    Result[] results = table.get(queryGets);
                    queryGoOn = processResults(results, entity, maps);
                }
            } while (queryGoOn);

            return maps;
        } finally {
            table.close();
        }

    }

    /**
     * 根据rid或者gid生成rowkey,每次生成endIndex-startIndex+1个
     *
     * @param rowKeyBase
     * @param startIndex
     * @param endIndex
     * @param queryGets
     * @retur
     */
    private static int createQueryGets(String rowKeyBase, int startIndex, int endIndex, List<Get> queryGets) {
        queryGets.clear();
        for (int i = startIndex; i <= endIndex; i++) {
            String rowKey = rowKeyBase + DataStorage.SPLIT_LINE_MID + i;
            Get get = new Get(rowKey.getBytes());
            queryGets.add(get);
        }
        return endIndex + 1;
    }

    /**
     * 处理结果
     */
    private static boolean processResults(Result[] results, RetrieveEntity entity, List<Map<String, Object>> maps) {
        boolean flag = true;
        if (results != null && maps != null) {
            for (Result r : results) {
                if (r != null && r.listCells() != null && !r.listCells().isEmpty()) {
                    Map<String, Object> map = fetchColumns(r, entity);
                    if (map != null) {
                        maps.add(map);
                    }
                } else {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 遍历结果集
     *
     * @param r
     * @return
     */
    private static Map<String, Object> fetchColumns(Result r, RetrieveEntity entity) {
        Map<String, Object> data = Maps.newHashMap();
        if (r != null && r.listCells() != null && !r.listCells().isEmpty()) {
            try {
                for (Cell cell : r.listCells()) {
                    String columnName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    if (entity != null && entity.columns != null && !entity.columns.isEmpty()) {
                        if (entity.columns.contains(columnName)) {
                            String columnVal = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                            data.put(columnName, columnVal);
                        }
                    } else {
                        String columnVal = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        //value判断是否为map
                        if (JsonUtil.json2ObjectNoException(columnVal, Map.class) != null) {
                            data.put(columnName, JsonUtil.json2Object(columnVal, HashMap.class));
                        } else {
                            data.put(columnName, columnVal);
                        }

                    }

                }

                if (entity != null && StringUtils.isNotBlank(entity.modelId) && (!data.containsKey("model_id") || !entity.modelId.equals(data.get("model_id")))) {
                    data = null;
                }

            } catch (Exception e) {
                LOGGER.error("查询hbase数据库异常，异常信息:", e);
            }

        }
        return data;
    }


    /**
     * 遍历结果集
     *
     * @param scanner
     * @return
     */
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
                LOGGER.error("查询hbase数据库异常，异常信息:", e);
            }

        }
        return datas;
    }


    /**
     * 按指定的列返回数据
     */
    private static Filter filterColumns(List<String> columns) {
        if (columns != null && !columns.isEmpty()) {
            FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
            for (String column : columns) {
                if (StringUtils.isNotBlank(column)) {
                    QualifierFilter qf = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(column)));
                    list.addFilter(qf);
                }
            }
            return list;
        }
        return null;
    }

    /**
     * 生成rowKey  rowKeyBase + 7位的数字 10000 ~ 99999
     * 规则： rowKeyBase + "不定数的0"+ index,保证生成的rowKey
     *
     * @param rowKeyBase
     * @param index
     * @return
     */
    private static String generateRowKey(String rowKeyBase, int index) {
        if (index < 10000) throw new RuntimeException("无法生成hbase 行键rowKey");
        if (StringUtils.isNotBlank(rowKeyBase) && index >= 10000) {
            return rowKeyBase + DataStorage.SPLIT_LINE_MID + String.valueOf(index);
        } else {
            String s = UUID.randomUUID().toString().replaceAll(DataStorage.SPLIT_LINE_MID, "");
            return s + DataStorage.SPLIT_LINE_MID + String.valueOf(index);
        }
    }

    /**
     * 根据给定的信息生成rowKey
     *
     * @param rowKeyBases
     * @return
     */
    public static String generateRowKey(String gid, String... rowKeyBases) {
        for (String str:rowKeyBases){
            if (StringUtils.isBlank(str) || "null".equalsIgnoreCase(str.toLowerCase())){
                return  null;
            }
        }


        Arrays.sort(rowKeyBases);
        String collect = Arrays.asList(rowKeyBases).stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining("-"));
        String rowKey = MD5_HMC_EncryptUtils.getMd5(collect, 1);
        LOGGER.info("gid:{},rowKeyBases:{},生成rowkey:{}", gid, rowKeyBases, rowKey);
        return rowKey;
    }

    /**
     * 将给定的map中的数据存储在hbase对应的表中
     *
     * @param gid
     * @param rowKey
     * @param data
     * @throws Exception
     */
    public static void storeData(String gid, String rowKey, Map<String, Object> data) throws Exception {
        long startTime = System.currentTimeMillis();
        HTable table = new HTable(configuration, Bytes.toBytes(DataStorage.HBASE_3d_DS_PERIOD_TB));
        try {
            if (table != null) {
                HTableDescriptor tableDescriptor = table.getTableDescriptor();

                HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
                for (HColumnDescriptor hcd : columnFamilies) {
                    String tblFamilyName = hcd.getNameAsString();
                    if (tblFamilyName.equalsIgnoreCase(DataStorage.HBASE_DEFAULT_FAMILY_NAME)) {
                        Put put = new Put(Bytes.toBytes(rowKey));
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            String columnName = entry.getKey();
                            Object columnValObj = entry.getValue();
                            String columnVal = columnValObj == null ? "" : columnValObj.toString();

                            put.add(Bytes.toBytes(DataStorage.HBASE_DEFAULT_FAMILY_NAME), Bytes.toBytes(columnName.trim()), Bytes.toBytes(columnVal));
                        }
                        table.put(put);
                    }
                }
            } else {
                LOGGER.error("gid:{},存储数据出错，未获取到hbase连接");
            }
            long endTime = System.currentTimeMillis();
            LOGGER.info("gid:{},储存数据源有效期耗时:{}", gid, endTime - startTime);
        } finally {

            table.close();
        }
    }


    public static void insList(List<Map<String, Object>> list, String familyName, String tbName) throws IOException {
        String[] s = {familyName};
        createTable(tbName, s);
        HTable table = new HTable(configuration, Bytes.toBytes(tbName));
        try {
            List<Put> puts = Lists.newArrayList();
            HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();
            for (HColumnDescriptor hcd : columnFamilies) {
                String tblFamilyName = hcd.getNameAsString();
                if (tblFamilyName.equalsIgnoreCase(familyName)) {
                    for (Map<String, Object> d : list) {
                        String moblie = d.get(ReadCSVUtil.MOBILE).toString();
                        String idcard = d.get(ReadCSVUtil.IDCARD).toString();
                        if (moblie.length() != 0 && idcard.length() == 0) {
                            Put put = new Put(Bytes.toBytes(moblie));
                            put.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.VALID_FROM), Bytes.toBytes(d.get(ReadCSVUtil.MOBILE).toString()));
                            put.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.FLAG_SOURCE), Bytes.toBytes(d.get(ReadCSVUtil.FLAG_SOURCE).toString()));
                            puts.add(put);
                        } else if (moblie.length() == 0 && idcard.length() != 0) {
                            Put put = new Put(Bytes.toBytes(idcard));
                            put.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.VALID_FROM), Bytes.toBytes(d.get(ReadCSVUtil.IDCARD).toString()));
                            put.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.FLAG_SOURCE), Bytes.toBytes(d.get(ReadCSVUtil.FLAG_SOURCE).toString()));
                            puts.add(put);
                        } else if (moblie.length() != 0 && idcard.length() != 0) {
                            Put putm = new Put(Bytes.toBytes(moblie));
                            putm.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.VALID_FROM), Bytes.toBytes(d.get(ReadCSVUtil.MOBILE).toString()));
                            putm.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.FLAG_SOURCE), Bytes.toBytes(d.get(ReadCSVUtil.FLAG_SOURCE).toString()));

                            Put puti = new Put(Bytes.toBytes(idcard));
                            puti.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.VALID_FROM), Bytes.toBytes(d.get(ReadCSVUtil.IDCARD).toString()));
                            puti.add(Bytes.toBytes(familyName), Bytes.toBytes(ReadCSVUtil.FLAG_SOURCE), Bytes.toBytes(d.get(ReadCSVUtil.FLAG_SOURCE).toString()));


                            puts.add(putm);
                            puts.add(puti);

                        }

                    }

                    table.put(puts);
                    table.flushCommits();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            table.close();
        }
    }

    public static List<Map<String, Object>> selList(String idcard, String mobile) throws IOException {
        List<Map<String, Object>> mList = new ArrayList<>();
        Boolean check = MongoDataProcessUtil.checkParam(idcard) && MongoDataProcessUtil.checkParam(mobile);

        if (check) {
            Map mapi = HBaseDataProcessUtil.queryMapByRowKey(null, idcard, BlackListTreServiceImpl.TABLE_NAME);
            Map mapm = HBaseDataProcessUtil.queryMapByRowKey(null, mobile, BlackListTreServiceImpl.TABLE_NAME);
            if (mapi != null) {
                mList.add(mapi);

            }
            if (mapm != null) {
                mList.add(mapm);

            }
        } else if (MongoDataProcessUtil.checkParam(idcard) && !MongoDataProcessUtil.checkParam(mobile)) {
            //idcard不为空
            Map mapi = HBaseDataProcessUtil.queryMapByRowKey(null, idcard, BlackListTreServiceImpl.TABLE_NAME);
            if (mapi != null) {
                mList.add(mapi);

            }
        } else if (!MongoDataProcessUtil.checkParam(idcard) && MongoDataProcessUtil.checkParam(mobile)) {
            //mobile不为空
            Map mapm = HBaseDataProcessUtil.queryMapByRowKey(null, mobile, BlackListTreServiceImpl.TABLE_NAME);
            if (mapm != null) {
                mList.add(mapm);

            }
        }
        return mList;
    }


    public static void insOLDMongoListFORHbase(List<Document> list, String familyName, String tbName) throws IOException {
        String[] s = {familyName};
        //createTable(tbName, s);
        HTable table = new HTable(configuration, Bytes.toBytes(tbName));
        try {
            List<Put> puts = Lists.newArrayList();
            HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();
            for (HColumnDescriptor hcd : columnFamilies) {
                String tblFamilyName = hcd.getNameAsString();
                if (tblFamilyName.equalsIgnoreCase(familyName)) {
                    for (Map<String, Object> d : list) {
                        String rid = d.get("online_report_id").toString();
                        Put put = new Put(Bytes.toBytes(rid + "-10000"));

                        for (Map.Entry<String, Object> entry : d.entrySet()) {
                            put.add(Bytes.toBytes(familyName), Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue().toString()));
                        }

                        puts.add(put);
                    }

                    table.put(puts);
                    table.flushCommits();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            table.close();
        }
    }

    public static void main(String[] args) throws Exception {
        /*String s = generateRowKey("ccxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "aa", "ac");
        System.out.println(s);
        String s1 = generateRowKey("cc", "ac", "ab", "ca");
        System.out.println(s1);

        PropertiesUtil.getMap();
        String key = "321084198907240029";
        String key2 = "53212819851011431X";
        String key3 = "15179975678_360302198305062024";
        String key4 ="13055702305_";

        long l = System.currentTimeMillis();

        List<Map<String, Object>> list = HBaseDataProcessUtil.queryDataContainsRowkey(null, key, "tcredit_black_list");

        System.out.println(list+"----:"+(System.currentTimeMillis()-l));
        l = System.currentTimeMillis();
        List<Map<String, Object>> list2 = HBaseDataProcessUtil.queryDataContainsRowkey(null, key2, "tcredit_black_list");
        System.out.println(list2+"----:"+(System.currentTimeMillis()-l));
        l = System.currentTimeMillis();
        List<Map<String, Object>> list3 = HBaseDataProcessUtil.queryDataContainsRowkey(null, key3, "tcredit_black_list");
        System.out.println(list3+"----:"+(System.currentTimeMillis()-l));
        l = System.currentTimeMillis();
        List<Map<String, Object>> list4 = HBaseDataProcessUtil.queryDataContainsRowkey(null, key4, "tcredit_black_list");
        System.out.println(list4+"----:"+(System.currentTimeMillis()-l));

        l = System.currentTimeMillis();
        List<Map<String, Object>> list5 = HBaseDataProcessUtil.queryListByRowKey(null, key3, "tcredit_black_list");
        System.out.println(list5+"----:"+(System.currentTimeMillis()-l));*/


    }
}
