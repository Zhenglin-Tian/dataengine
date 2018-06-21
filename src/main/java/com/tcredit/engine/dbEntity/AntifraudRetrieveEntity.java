package com.tcredit.engine.dbEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-02 17:29
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-02 17:29
 * @updatedRemark:
 * @version:
 */
public class AntifraudRetrieveEntity extends RetrieveEntity {
    /**
     * 根据合作客户id查询
     */
    public String bid;

    /**
     * 根据输出变量名称查询，多个用逗号(,)分割
     */
    @JsonProperty("name_revar")
    public String nameRevar;
    /**
     * 根据输入变量名称查询，多个用逗号(,)分割
     */
    @JsonProperty("name_invar")
    public String nameInvar;

}
