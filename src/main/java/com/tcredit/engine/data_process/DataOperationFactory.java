package com.tcredit.engine.data_process;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-04 22:46
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-04 22:46
 * @updatedRemark:
 * @version:
 */
public class DataOperationFactory {
    public static String MYSQL = "mysql";
    public static String HBASE = "hbase";
    public static String MONGO = "mongo";
    public static String ANTIFRAUD_MYSQL = "antifraudMysql";
    public static String ANTIFRAUD_HBASE = "antifraudHbase";
    private static DataStorage dataStorageMysql = new DataStorageMysql();
    private static DataStorage dataStorageHBase = new DataStorageHbase();
    private static DataStorage dataStorageMongo = new DataStorageMongo();
    private static DataRetrieve dataRetrieveMysql = new DataRetrieveMysql();
    private static DataRetrieve dataRetrieveHbase = new DataRetieveHbase();
    private static DataRetrieve dataRetrieveMongo = new DataRetieveMongo();
    private static DataRetrieve antifraudVariableRetrieveMysql = new AntifraudVariableRetrieveMysql();
    private static DataRetrieve antifraudVariableRetrieveHbase = new AntifraudVariableRetrieveHbase();


    public static DataStorage getDataStorage(String dbtype) {
        if (MYSQL.equalsIgnoreCase(dbtype)) {
            return dataStorageMysql;
        } else if (HBASE.equalsIgnoreCase(dbtype)) {
            return dataStorageHBase;
        }else if(MONGO.equalsIgnoreCase(dbtype)){
            return dataStorageMongo;
        } else {
            throw new RuntimeException("该数据库尚未支持");
        }
    }

    public static DataRetrieve getDataRetrieve(String dbtype) {
        if (MYSQL.equalsIgnoreCase(dbtype)) {
            return dataRetrieveMysql;
        } else if (HBASE.equalsIgnoreCase(dbtype)) {
            return dataRetrieveHbase;
        } else if (MONGO.equalsIgnoreCase(dbtype)) {
            return dataRetrieveMongo;
        }else if (ANTIFRAUD_MYSQL.equalsIgnoreCase(dbtype)) {
            return antifraudVariableRetrieveMysql;
        } else if (ANTIFRAUD_HBASE.equalsIgnoreCase(dbtype)) {
            return antifraudVariableRetrieveHbase;
        } else {
            throw new RuntimeException("该数据库尚未支持");
        }
    }

}
