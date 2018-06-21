package com.tcredit.engine.constants;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @description: 数据处理涉及的所有步骤，每个步骤对应一个数据库(关系数据库中表现为库标识，其他db中没有库概念的则以步骤名称加下划线标识如std_)
 * 库中的所有表以库标识加下划线标识，如std_baidu_blacklist标识百度黑名单标准库，tidy_baidu_blacklist标识百度黑名单整洁数据库
 * @author: zl.T
 * @since: 2017-12-05 18:55
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-05 18:55
 * @updatedRemark:
 * @version:
 */
public enum  StepConstants {
    STD("std",0),
    TIDY("tidy",1),
    VARCALC("var",2),
    MODEL("model",3);
    public String name;
    public int orderVal;


    StepConstants(String name, int orderVal) {
        this.name = name;
        this.orderVal = orderVal;
    }

    public static List<String> steps(){
        List<String> steps = Lists.newArrayList();
        for (StepConstants sc:StepConstants.values()){
            steps.add(sc.name);
        }
        return steps;
    }
}
