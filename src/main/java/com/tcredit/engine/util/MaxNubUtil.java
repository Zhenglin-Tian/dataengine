package com.tcredit.engine.util;

import java.util.List;

public class MaxNubUtil {
    public static int getMaxNum(List<Integer> list) {
        int num = list.get(0);
        for (int i = 0; i < list.size(); i++) {//循环数组
            num = (list.get(i) < num ? num : list.get(i));//三元运算符
        }
        return num;


    }
}
