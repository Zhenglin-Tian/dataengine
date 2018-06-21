import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-08 09:31
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-08 09:31
 * @updatedRemark:
 * @version:
 */
public class TestThread {


    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(5);
        es.shutdown();
    }
}
