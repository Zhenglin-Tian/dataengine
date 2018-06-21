package com.tcredit.engine.util.xml;

import com.tcredit.engine.util.JsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-02 14:10
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-02 14:10
 * @updatedRemark:
 * @version:
 */
public class Xml2Pojo {

    /**
     *
     */
    public Xml2Pojo() {
    }

    public static String xml2Json(String xmlContent) {
        return JsonUtil.xml2Json(xmlContent);
    }

    public static Map single(File file) throws IOException {
        String json = JsonUtil.xml2Json(FileUtils.readFileToString(file, "utf-8"));
        return (Map)JsonUtil.json2Object(json, Map.class);
    }
}