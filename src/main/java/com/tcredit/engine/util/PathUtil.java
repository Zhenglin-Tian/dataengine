package com.tcredit.engine.util;

import java.io.File;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-14 9:27
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-14 9:27
 * @updatedRemark:
 * @version:
 */
public class PathUtil {
    /**
     *
     * @return
     */
    public static String fetchProjectRootDir(){
        String path = PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path;
    }

    public static String fetchParent(String path, int level) {
        for (int i = 0; i < level; i++) {
            path = fetchParent(path);
        }
        return path;
    }

    public static String fetchParent(String path) {
        File file = new File(path);
        return file.getParent();
    }

    public static void main(String[] args) {
        System.out.println(fetchProjectRootDir());
        System.out.println(fetchParent(fetchProjectRootDir(),2));
    }
}
