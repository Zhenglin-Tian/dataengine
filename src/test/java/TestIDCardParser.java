import com.tcredit.engine.util.PropertiesUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-02-08 11:41
 * @updatedUser: zl.T
 * @updatedDate: 2018-02-08 11:41
 * @updatedRemark:
 * @version:
 */
public class TestIDCardParser {


    public static void main(String[] args) throws IOException {
        Map<String, String> stringStringMap = PropertiesUtil.readProperties("card.properties");

        String idcardno="130582";
        System.out.println(stringStringMap.get(idcardno));
    }
}
