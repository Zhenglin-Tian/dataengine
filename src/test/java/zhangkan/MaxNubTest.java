package zhangkan;

import java.util.ArrayList;
import java.util.List;

public class MaxNubTest {

    public static int getMaxNum(List<Integer> list) {
        int num = list.get(0);
        for (int i = 0; i < list.size(); i++) {//循环数组
            num = (list.get(i) < num ? num : list.get(i));//三元运算符
        }
        return num;


    }


    public static void main(String[] args) {
        List<Integer> list=new ArrayList<>();
        list.add(11);
        list.add(18);
        list.add(1);
        list.add(9);
        list.add(0);
        System.out.println(getMaxNum(list));

    }
}
