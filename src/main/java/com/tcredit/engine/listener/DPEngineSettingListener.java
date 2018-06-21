package com.tcredit.engine.listener;

import com.tcredit.engine.conf.ConfigManagerV2;
import com.tcredit.engine.util.xml.XMLValidate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 15:13
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 15:13
 * @updatedRemark:
 * @version:
 */


//@WebListener
@Deprecated
public class DPEngineSettingListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1 validate xml
        XMLValidate.validate();
        //2 parse xml
        ConfigManagerV2.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}
