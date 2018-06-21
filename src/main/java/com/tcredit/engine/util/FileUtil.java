package com.tcredit.engine.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 17:50
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 17:50
 * @updatedRemark:
 * @version:
 */
public class FileUtil {
    /**
     *
     * @param filePath
     * @return
     */
    public static File[] listFiles(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return listFiles(file);
        }
        return null;
    }

    public static File[] listFiles(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return null;
        }
        return file.listFiles();
    }
}
