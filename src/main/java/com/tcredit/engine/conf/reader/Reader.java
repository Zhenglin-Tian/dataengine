package com.tcredit.engine.conf.reader;

import com.tcredit.engine.context.ProcessContextV2;

import java.io.Serializable;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 15:52
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 15:52
 * @updatedRemark:
 * @version:
 */
public interface Reader extends Serializable {
    void read(ProcessContextV2 cxt);
}
