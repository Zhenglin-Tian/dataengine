import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-01 17:27
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-01 17:27
 * @updatedRemark:
 * @version:
 */
public class HttpTest {
    @Test
    public void testSet() {
        String host = "172.19.160.127:6379";
        System.out.println(System.currentTimeMillis());



    }


    @Test
    public void test2(){
        List<Integer> list = Lists.newArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        int max = list.stream().mapToInt(Integer::intValue).summaryStatistics().getMax();
        System.out.println(max);
    }

    /*public static void main(String[] args) {
        String url = "http://localhost:8080/dp/v1/synProcess";
        Map<String, String> map = new HashMap<String, String>() {{
            put("mid", "111");
            put("gid", "2");
        }};
        long l = System.currentTimeMillis();
        try {

            String s = HttpClientUtil.httpPost(url, map, 5000);
            System.out.println(s);
        } catch (Exception e) {
            if (e instanceof CustomedConnectionException){
                System.out.println("xxxx"+e.getMessage());
            }
        }
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

    }*/
}
