package com.tcredit.engine.listener;

import com.tcredit.engine.conf.ConfigManagerV2;
import com.tcredit.engine.data_process.DataStorage;
import com.tcredit.engine.data_process.DataStorageHbase;
import com.tcredit.engine.data_process.hbaseDataProcessUtil.HBaseDataProcessUtil;
import com.tcredit.engine.util.PropertiesUtil;
import com.tcredit.engine.util.xml.XMLValidate;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 15:13
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 15:13
 * @updatedRemark:
 * @version:
 */


@WebListener
public class DPEngineSettingListenerV2 implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1 validate xml
        XMLValidate.validate();
        //2 parse xml
        ConfigManagerV2.init();
//        /**
//         * 初始化Redis
//         */
//        String init = RedissonUtil.get("init");
//        System.out.println(init);
        String[] family = {DataStorage.HBASE_DEFAULT_FAMILY_NAME};
        try {

            HBaseDataProcessUtil.createTable(DataStorage.HBASE_3d_DS_PERIOD_TB, family);
        } catch (Exception e) {
            throw new RuntimeException("启动时检测数据源有效期管理表是否存在异常");
        }

        try {
            String tableNamesFileName = PropertiesUtil.getString("TABLE_NAME_ROOT");
            String mongoFileName = PropertiesUtil.getString("TABLE_NAME_ROOT");
            if (tableNamesFileName.lastIndexOf("/") == tableNamesFileName.trim().length() - 1) {
                tableNamesFileName = tableNamesFileName.trim() + "hbasetables";
            } else {
                tableNamesFileName = tableNamesFileName.trim() + "/hbasetables";
            }

            if (mongoFileName.lastIndexOf("/") == mongoFileName.trim().length() - 1) {
                mongoFileName = mongoFileName.trim() + "mongotables";
            } else {
                mongoFileName = mongoFileName.trim() + "/mongotables";
            }


            File file = new File(tableNamesFileName);
            File mongoFile = new File(mongoFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!mongoFile.exists()) {
                mongoFile.createNewFile();
            }
            List<String> tables = FileUtils.readLines(file, "utf-8");
            List<String> mongoTables = FileUtils.readLines(mongoFile, "utf-8");
            DataStorageHbase.setHbaseTables(tables);
            ConfigManagerV2.getDbType().put(ConfigManagerV2.MONGO, mongoTables);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        String tableNamesFileName = PropertiesUtil.getString("TABLE_NAME_ROOT");
        String mongoFileName = PropertiesUtil.getString("TABLE_NAME_ROOT");
        try {
            if (tableNamesFileName.lastIndexOf("/") == tableNamesFileName.trim().length() - 1) {
                tableNamesFileName = tableNamesFileName.trim() + "hbasetables";
            } else {
                tableNamesFileName = tableNamesFileName.trim() + "/hbasetables";
            }

            if (mongoFileName.lastIndexOf("/") == mongoFileName.trim().length() - 1) {
                mongoFileName = mongoFileName.trim() + "mongotables";
            } else {
                mongoFileName = mongoFileName.trim() + "/mongotables";
            }
            File file = new File(tableNamesFileName);
            File mongoFile = new File(mongoFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!mongoFile.exists()) {
                mongoFile.createNewFile();
            }

            FileUtils.writeLines(new File(tableNamesFileName), DataStorageHbase.getHbaseTables());
            FileUtils.writeLines(new File(mongoFileName), ConfigManagerV2.getDbType().get(ConfigManagerV2.MONGO));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
