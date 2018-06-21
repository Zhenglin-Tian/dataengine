package com.tcredit.engine.conf;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:33
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:33
 * @updatedRemark:
 * @version:
 */
public class Mapping implements Serializable{
    private String source;
    private String target;
    private String type;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
