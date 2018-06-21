package com.tcredit.engine.util;

import com.mongodb.MongoBulkWriteException;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ReadCSVUtil {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ReadCSVUtil.class);
    public static final String IDCARD = "idcard";
    public static final String MOBILE = "mobile";
    public static final String FLAG_SOURCE = "flagsource";
    public static final String VALID_FROM = "valid_from";
    public static final String NA = "NA";


    public static List<Map<String, Object>> readCSVReMap(String fileURL) {
        List<Map<String, Object>> list = new ArrayList<>();
        File file = new File(fileURL);
        //这里要统一编码
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bfr = new BufferedReader(read);

            String line = null;
            while ((line = bfr.readLine()) != null) {
                Map<String, Object> document = new Document();
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分

                String idcard = item[1];
                String mobile = item[2];
                String flagsource = item[3];
                if (NA.equals(idcard) || idcard == null) {
                    idcard = "";
                }
                if (NA.equals(mobile) || mobile == null) {
                    mobile = "";
                }


                if (!"".equals(mobile) && !"".equals(idcard)) {
                    if (validateLegalStr(mobile, MOBILE) && validateLegalStr(idcard, IDCARD)) {
                        document.put(IDCARD, idcard);
                        document.put(MOBILE, mobile);
                        document.put(FLAG_SOURCE, flagsource);
                        list.add(document);
                    }
                    if (!validateLegalStr(mobile, MOBILE) && validateLegalStr(idcard, IDCARD)) {
                        document.put(IDCARD, idcard);
                        document.put(MOBILE, "");
                        document.put(FLAG_SOURCE, flagsource);
                        list.add(document);
                    }
                    if (validateLegalStr(mobile, MOBILE) && !validateLegalStr(idcard, IDCARD)) {
                        document.put(IDCARD, "");
                        document.put(MOBILE, mobile);
                        document.put(FLAG_SOURCE, flagsource);
                        list.add(document);
                    }
                } else if (!"".equals(mobile) && "".equals(idcard)) {
                    if (validateLegalStr(mobile, MOBILE)) {
                        document.put(IDCARD, "");
                        document.put(MOBILE, mobile);
                        document.put(FLAG_SOURCE, flagsource);
                        list.add(document);
                    }
                } else if ("".equals(mobile) && !"".equals(idcard)) {
                    if (validateLegalStr(idcard, IDCARD)) {
                        document.put(IDCARD, idcard);
                        document.put(MOBILE, "");
                        document.put(FLAG_SOURCE, flagsource);
                        list.add(document);
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean validateLegalStr(String str, String type) {
        String idCardPattern = "^\\d\\d*[\\dxX]$";
        String mobilePattern = "^\\d*";
        if (IDCARD.equals(type)) {
            Pattern pattern = Pattern.compile(idCardPattern);
            return pattern.matcher(str).matches();
        } else if (MOBILE.equals(type)) {
            Pattern pattern = Pattern.compile(mobilePattern);
            return pattern.matcher(str).matches();
        } else {
            return false;
        }

    }

    public static List<Document> readFileReList(File file) {
        List<Document> list = new ArrayList<>();
        //这里要统一编码
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bfr = new BufferedReader(read);

            String line = null;
            while ((line = bfr.readLine()) != null) {
                Document document = new Document();
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分


                String regcanal3 = item[1];
                String regcanal4 = item[2];
                String regcanal5 = item[3];
                String regcanal6 = item[4];
                String gid = item[5];
                String regcanal1 = item[6];
                String regcanal2 = item[7];
                String online_report_id = item[8];
                String tid = item[9];
                String regcanal12 = item[10];
                String regcanal13 = item[11];
                String regcanal14 = item[12];
                String regcanal15 = item[13];
                String regcanal16 = item[14];
                String regcanal17 = item[15];
                String regcanal18 = item[16];
                String regcanal19 = item[17];
                String tel = item[18];
                String id = item[19];
                String regcanal7 = item[20];
                String regcanal30 = item[21];
                String regcanal8 = item[22];
                String regcanal31 = item[23];
                String regcanal9 = item[24];
                String regcanal10 = item[25];
                String regcanal11 = item[26];
                String regcanal23 = item[27];
                String regcanal24 = item[28];
                String regcanal25 = item[29];
                String regcanal26 = item[30];
                String regcanal27 = item[31];
                String regcanal28 = item[32];
                String regcanal29 = item[33];
                String regcanal20 = item[34];
                String regcanal21 = item[35];
                String regcanal22 = item[36];
                String tm_isrt = item[37];
                document.put("_id", online_report_id);
                document.put("regcanal3", regcanal3);
                document.put("regcanal4", regcanal4);
                document.put("regcanal5", regcanal5);
                document.put("regcanal6", regcanal6);
                document.put("gid", gid);
                document.put("regcanal1", regcanal1);
                document.put("regcanal2", regcanal2);
                document.put("online_report_id", online_report_id);
                document.put("tid", tid);
                document.put("regcanal12", regcanal12);
                document.put("regcanal13", regcanal13);
                document.put("regcanal14", regcanal14);
                document.put("regcanal15", regcanal15);
                document.put("regcanal16", regcanal16);
                document.put("regcanal17", regcanal17);
                document.put("regcanal18", regcanal18);
                document.put("regcanal19", regcanal19);
                document.put("tel", tel);
                document.put("id", id);
                document.put("regcanal7", regcanal7);
                document.put("regcanal30", regcanal30);
                document.put("regcanal8", regcanal8);
                document.put("regcanal31", regcanal31);
                document.put("regcanal9", regcanal9);
                document.put("regcanal10", regcanal10);
                document.put("regcanal11", regcanal11);
                document.put("regcanal23", regcanal23);
                document.put("regcanal24", regcanal24);
                document.put("regcanal25", regcanal25);
                document.put("regcanal26", regcanal26);
                document.put("regcanal27", regcanal27);
                document.put("regcanal28", regcanal28);
                document.put("regcanal29", regcanal29);
                document.put("regcanal20", regcanal20);
                document.put("regcanal21", regcanal21);
                document.put("regcanal22", regcanal22);
                document.put("tm_isrt", tm_isrt);

                list.add(document);
            }
        } catch (MongoBulkWriteException e) {
            LOGGER.info("重复数据----");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void main(String[] args) {
        /*/data/service/tmp_std_std_ip_beesmell_regcanal_result_inside*/
       /* File[] files = FileUtil.listFiles("/Users/zhangkan/Desktop/test/");
        for (File file : files) {
            System.out.println(file.getName());
            System.out.println(readFileReList(file));

        }*/

    }
}
