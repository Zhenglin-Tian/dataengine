
import com.tcredit.engine.response.ResponseData;
import com.tcredit.engine.util.JsonUtil;

public class JSonTTT {

    public static void main(String[] args) {

        String s = "{\\\"mid\\\":\\\"applyform\\\",\\\"cmid\\\":\\\"applyform\\\",\\\"gid\\\":\\\"123\\\",\\\"sync\\\":true,\\\"step\\\":\\\"tidy\\\",\\\"data\\\":[{\\\"dbName\\\":\\\"applyform\\\",\\\"tableName\\\":\\\"applyform\\\",\\\"data\\\":[{\\\"gid\\\":\\\"123\\\",\\\"bid\\\":\\\"lll\\\",\\\"uuid\\\":\\\"f1d3396156e7402d855281e72e80a6a2\\\",\\\"name\\\":\\\"吴文建\\\",\\\"idcard\\\":\\\"52252619950824001X\\\",\\\"mobile\\\":\\\"17612140317\\\",\\\"bankcard\\\":\\\"6212262404005241684\\\",\\\"gender\\\":\\\"M\\\",\\\"age\\\":22,\\\"marriage\\\":\\\"\\\",\\\"education_level\\\":\\\"\\\",\\\"house_type\\\":\\\"\\\",\\\"yearly_income\\\":\\\"\\\",\\\"working_seniority\\\":\\\"\\\",\\\"per_addr\\\":\\\"\\\",\\\"app_tm\\\":\\\"2017-09-22 11:06:14\\\",\\\"apply_addr\\\":\\\"\\\",\\\"loan_reason\\\":\\\"\\\",\\\"apply_source\\\":\\\"a\\\",\\\"product_type\\\":\\\"a\\\",\\\"apply_money\\\":\\\"\\\",\\\"refund_periods\\\":\\\"\\\",\\\"reg_addr_longitude_latitude\\\":\\\",\\\",\\\"reg_addr\\\":\\\"\\\",\\\"email\\\":\\\"\\\",\\\"postalcode\\\":\\\"\\\",\\\"oth_addr\\\":\\\"\\\",\\\"contact1_relation\\\":\\\"g\\\",\\\"contact1_cell\\\":\\\"15985315590\\\",\\\"contact1_name\\\":\\\"梁晨\\\",\\\"contact1_addr\\\":\\\"\\\",\\\"contact2_relation\\\":\\\"f\\\",\\\"contact2_cell\\\":\\\"13310733779\\\",\\\"contact2_name\\\":\\\"梁栋\\\",\\\"tel_home\\\":\\\"\\\",\\\"home_addr_longitude_latitude\\\":\\\"\\\",\\\"home_addr\\\":\\\"\\\",\\\"tel_biz\\\":\\\"085138133021\\\",\\\"biz_type\\\":\\\"\\\",\\\"biz_positon\\\":\\\"e\\\",\\\"biz_industry\\\":\\\"\\\",\\\"biz_company\\\":\\\"云都装饰公司\\\",\\\"biz_addr_longitude_latitude\\\":\\\"\\\",\\\"biz_addr\\\":\\\"新厢路169号\\\",\\\"os\\\":\\\"a\\\",\\\"mobile_type\\\":\\\"\\\",\\\"mac\\\":\\\"\\\",\\\"ip\\\":\\\"58.16.106.213\\\",\\\"imsi\\\":\\\"\\\",\\\"imei\\\":\\\"\\\",\\\"netlife\\\":null,\\\"apply_addr_tidy\\\":\\\"\\\",\\\"apply_addr_province\\\":\\\"\\\",\\\"apply_addr_city\\\":null,\\\"tel_biz_tidy\\\":\\\"085138133021\\\",\\\"biz_addr_specific\\\":1,\\\"apply_addr_specific\\\":0,\\\"apply_mobile_acd\\\":\\\"021\\\",\\\"tel_biz_acd\\\":\\\"0851\\\",\\\"provcty_acd\\\":null,\\\"contact1_mobile_acd\\\":\\\"0853\\\",\\\"contact2_mobile_acd\\\":\\\"0853\\\",\\\"apply_mobile_opr\\\":\\\"unicom\\\",\\\"contact1_mobile_opr\\\":\\\"mobile\\\",\\\"contact2_mobile_opr\\\":\\\"telecom\\\"}]}]}";


        ResponseData out = JsonUtil.json2Object(s.replaceAll("\\\\",""), ResponseData.class);

        System.out.println(out);

        System.out.println(JsonUtil.toJson("{\"123\":\"456\"}"));

    }
}
