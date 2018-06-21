import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-01-10 15:07
 * @updatedUser: zl.T
 * @updatedDate: 2018-01-10 15:07
 * @updatedRemark:
 * @version:
 */
public class TestPrintLinux {

    public static void main(String[] args) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        cmd("sudo sh /home/poc/application/pressureExecutor/access/access.sh");
    }



    public static String cmd(String cmd){
        String result = "";
//        LOGGER.info("执行命令:{}",cmd);
        String[] cmds = {"/bin/sh","-c",cmd};
        try {
            Process pro = Runtime.getRuntime().exec(cmds);
            pro.waitFor();
            InputStream in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = read.readLine())!=null){
                System.out.println("命令执行结果:"+line);
                result = line;
            }
        }catch (Exception e){
            System.out.println("执行系统命令:"+cmd+"出错，错误信息:"+e);
        }

        return result;
    }
}
