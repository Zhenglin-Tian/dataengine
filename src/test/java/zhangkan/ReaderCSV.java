/*
package zhangkan;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderCSV {


    public static void main(String[] args) {
        //源文件
        List<HashMap<String, Object>> list = new ArrayList<>();
        File file = new File("/Users/zhangkan/工作相关/田正林/balck_list.csv");


        //这里要统一编码
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bfr = new BufferedReader(read);

            String line = null;
            while ((line = bfr.readLine()) != null) {
                HashMap<String, Object> m = new HashMap<>();
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                //System.out.println("|"+String.valueOf(item[1])+"|"+String.valueOf(item[2])+"|");

                String idcard = item[1];
                String mobile = item[2];
                if (idcard.equals("NA")) {
                    idcard = "";
                } else {
                    idcard = idcard.substring(1, idcard.length() - 1);
                }
                if (mobile.equals("NA")) {
                    mobile = "";
                } else {
                    mobile = mobile.substring(1, mobile.length() - 1);
                }
                m.put("idcard", idcard);
                m.put("mobile", mobile);
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
*/
