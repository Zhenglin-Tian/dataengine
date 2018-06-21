package com.tcredit.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-27 10:45
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-27 10:45
 * @updatedRemark:
 * @version:
 */
@SpringBootApplication
@ServletComponentScan
//@EnableAutoConfiguration
/*@EnableAutoConfiguration
@Configuration
@ImportResource({"classpath*:applicationContext.xml"})*/
public class ApplicationMain {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class,args);
    }




}
