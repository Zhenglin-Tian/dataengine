package com.tcredit.engine.dbEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-20 11:50
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-20 11:50
 * @updatedRemark:
 * @version:
 */
public class RetrieveEntity {
    /**
     * 查询步骤
     */
    public String step;

    /**
     * 查询库
     */
    public String db;
    /**
     * 查询表
     */
    public String tblName;
    /**
     * 查询列
     */
    public List<String> columns = Lists.newArrayList();


    /**
     * 根据gid查询
     */
    public String gid;

    /**
     * 根据数据报告id来获取数据，报告id来源：信用百科的sequenceNo, 爬虫的tid, 自己生成的id
     */
    @JsonProperty("rid")
    public String rid;

    /**
     * 根据modelID
     */
    @JsonProperty("modelID")
    public String modelId;



}
