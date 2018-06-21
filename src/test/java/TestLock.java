import com.tcredit.engine.util.RedissonUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-22 16:39
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-22 16:39
 * @updatedRemark:
 * @version:
 */
public class TestLock {
    private static final String lock_key = "xxxxxxxxxxxxxxxxxx";
    private static final String keyVal = "test_test_test_xxx";
    private static final int THREAD_COUNT = 10;
    private static volatile long i = 1;
    public static void increase() {
        /**
         * 获取锁，获取到的锁是已经加过锁的锁
         */
        RLock rLock = RedissonUtil.getRLock(lock_key);
        String s = RedissonUtil.get(keyVal);
        if (StringUtils.isBlank(s)) {
            RedissonUtil.set(keyVal,String.valueOf(0),180);
        } else {
            long add = Long.parseLong(s) + 1;
            RedissonUtil.set(keyVal,String.valueOf(add),180);
        }
        /**
         * 执行完成自动释放锁
         */
        rLock.unlock();

    }


    public static void main(String[] args) throws InterruptedException {
//        RedissonUtil.delete(lock_key);
//        RedissonUtil.set(keyVal,String.valueOf(0),180);
//
//        Thread[] threads = new Thread[THREAD_COUNT];
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            threads[i] = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int j = 0; j < 1000; j++) {
//                        increase();
//                    }
//                }
//            });
//            threads[i].start();
//        }
//
//        while (true){
//            System.out.println(Thread.activeCount()+"================active thread");
//            System.out.println(RedissonUtil.get(keyVal));
//            Thread.sleep(5000);
//        }

        /**
         * 测试锁
         */
        String lockName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        int i = 1;
        while (true) {
            RLock rLock = RedissonUtil.getRLock(lockName);
            System.out.println(rLock.getName()+"----------"+rLock.isLocked()+"----"+i++);

            Thread.sleep(1000);

            rLock.unlock();
        }
    }
}
