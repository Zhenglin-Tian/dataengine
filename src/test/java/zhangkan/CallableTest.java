package zhangkan;

import java.util.Random;
import java.util.concurrent.*;

public class CallableTest {
    private static final int NUMBER_INIT=100;

    public static void main(String[] args) {
        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(5);
        //ExecutorService threadPool = Executors.newSingleThreadExecutor();
        Future<Integer> s= threadPool.submit(new StepCallable(NUMBER_INIT));


            for (int i=0;i<10;i++){
                System.out.println("|"+i);
            }

            System.out.println("2、"+s.get());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    static class StepCallable implements Callable<Integer>{
        private int i;

        public StepCallable(int i) {
            this.i = i;
        }

        @Override
        public Integer call()  {
            int s = 0;
            try {
            Random random = new Random();
             s=random.nextInt(i);
            System.out.println("1、"+s);
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }
    }
}
