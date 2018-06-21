package com.tcredit.engine.listener;

import com.tcredit.engine.util.RedissonUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-12 17:10:23
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-12 17:10:23
 * @updatedRemark:
 * @version:
 */



//@WebListener
@Deprecated
public class LoadDataSourceConfigListener implements ServletContextListener {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(LoadDataSourceConfigListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //linux path
        //  String configPath = LoadDataSourceConfigListener.class.getClassLoader().getResource("druid_data_source").getPath();

//        String configPath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"druid_data_source";
        //mac
//        String configPath = LoadDataSourceConfigListener.class.getResource("").getPath()+"druid_data_source";
//        String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 2);
//        String configPath = projectDir + "/config/db";
//
//        LOGGER.info("====================================="+configPath);
//        File filePath = new File(configPath);
//        if (filePath.exists()) {
//            LOGGER.info("------------------------------------------------------");
//            File[] configFiles = new File(configPath).listFiles();
//            for (File file : configFiles) {
//                Properties config = new Properties();
//                try {
//                    InputStream is = new FileInputStream(file.getAbsolutePath());
//                    config.load(is);
//                    DruidDataSourceUtil.createDruidDataSourceAndPutInCache(config);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }

        /**
         * 初始化Redis
         */
        RedissonUtil.get("init");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        /**
         * 关闭Redis客户端
         */
//        RedissonUtil.close();
    }
}