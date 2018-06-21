import com.tcredit.engine.response.Response;
import com.tcredit.engine.util.MD5_HMC_EncryptUtils;
import com.tcredit.engine.util.httpClient.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-02-08 11:21
 * @updatedUser: zl.T
 * @updatedDate: 2018-02-08 11:21
 * @updatedRemark:
 * @version:
 */
public class TestParseMobileArea {

    private static final String TOKEN = "de67d71e-6e82-4db8-bda6-7bfc644c169b";
    private static final String MOBILE_QUERY_URL = "http://123.57.23.67/mobileArea";


    public static void main(String[] args) throws IOException {
        String mobile = "13718721085";
        String s = searchMobileArea(mobile, TOKEN);
        System.out.println(s);


    }

    public static String searchMobileArea(String mobile, String token)
            throws IOException {
        String url = MOBILE_QUERY_URL;
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        String formatUrl = getFormatUrl(url, map);
        url = formatUrl + "&token=" + getToken(formatUrl, token);

        String s = HttpClientUtil.httpGet(url, null, 50000);
        return s;

    }

    private static String getToken(String formatUrl, String token) {
        if (StringUtils.isNotBlank(formatUrl) && StringUtils.isNotBlank(token)){
            String s = formatUrl + token;
            System.out.println(formatUrl);
            System.out.println(token);
            String md5 = MD5_HMC_EncryptUtils.getMd5(s, 1);

            return md5;
        }
        return null;
    }


    public static String getFormatUrl(String urlPrefix, Map<String, String> map) {
        if (null == map) {
            return urlPrefix;
        }
        StringBuffer url = new StringBuffer();
        url.append(urlPrefix).append("?");
        Iterator<String> iterator = map.keySet().iterator();
        String[] arr = new String[map.size()];
        int i = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            arr[i] = key;
            i++;
        }
//        getSortedArr(arr);
        Arrays.sort(arr);
        for (String s : arr) {
            if (s.equalsIgnoreCase("token")) {
                continue;
            }
            String value = map.get(s);
            url.append(s).append("=").append(value).append("&");
        }
        String urlRet = url.substring(0, url.length() - 1);
        return urlRet;
    }

}
