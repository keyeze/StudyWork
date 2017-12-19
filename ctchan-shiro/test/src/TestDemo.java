import com.chan.shiro.token.CtChanChildToken;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by keyez on 2017/11/14.
 */
public class TestDemo {
    private static final Logger logger = LogManager.getLogger(TestDemo.class);

    @Test
    public void testHelloWorld() throws IOException {
        logger.info("hahaha");
        //创建SecurityManager工厂,此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-config.ini");
        //得到SecurityManager示例,并绑定SecrityUtils
        //默认为单例.
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Object[][] objs = {{"chan", "0000"}, {"ctchan", "0000"}, {"root", "0000"}};
        Subject subject = SecurityUtils.getSubject();
        for (Object[] item : objs) {
            UsernamePasswordToken token = getToken(item);

            System.out.println(token);
            try {
                subject.login(token);
                Assert.assertEquals(true, subject.isAuthenticated());
                System.out.println(subject);
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }


    }

    @Test
    public void testHelloWorld2() throws IOException {
        //创建SecurityManager工厂,此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-config.ini");
        //得到SecurityManager示例,并绑定SecrityUtils
        //默认为单例.
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
//        CtChanShiroToken ctChanToken = new CtChanShiroToken();
//        ctChanToken.setPassword("111111");
//        ctChanToken.setUsername("xiaoming");
//        subject.login(ctChanToken);
//        System.out.println(subject.isAuthenticated());
        CtChanChildToken token = new CtChanChildToken();
        token.setPassword("0000");
        token.setUsername("CtChan");
        subject.login(token);
        System.out.println(subject.isAuthenticated());
    }

    private UsernamePasswordToken getToken(Object[] objs) {
        UsernamePasswordToken token = new UsernamePasswordToken(objs[0].toString(), objs[1].toString());
        return token;
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        String a = "哈哈哈";
        System.out.println(new String(a.getBytes("utf-8"), "GBK"));
        System.out.println(new String(new String(a.getBytes("utf-8"), "utf-8").getBytes("gbk"), "gbk"));
    }

    @Test
    public void test2() throws InterruptedException {
        while (true) {
            double infoA = 3000 * Math.random();
            double infoB = Math.random() * 100;
            for (int i = (int) (Math.random() * infoB); i >= 0; i++) {
                logger.info("em....");
                Thread.sleep((long) (Math.random() * infoA));
            }
        }
    }

    @Test
    public void test3() throws InterruptedException, IOException {
        ServerSocket server = new ServerSocket(4567);
        Socket socket = server.accept();
        //跟客户端建立好连接之后，我们就可以获取socket的InputStream，并从中读取客户端发过来的信息了。
        Reader reader = new InputStreamReader(socket.getInputStream());
        char chars[] = new char[64];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = reader.read(chars)) != -1) {
            sb.append(new String(chars, 0, len));
        }
        System.out.println("from client: " + sb);

        reader.close();
        socket.close();
        server.close();
    }


    @Test
    public void test4() throws InterruptedException, IOException {
        logger.info("OK..");
        logger.debug("OK...");
        logger.error("OK....");
        logger.warn("OK.....");
    }

    public static void main(String[] args) throws IOException {
        while (true) {
            Socket socket = new Socket("127.0.0.1", 4560);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String str = "{item:12,it:13}";
            writer.write(str);
            writer.flush();
            writer.close();
        }
    }

    @Test
    public void test5() throws IOException {
        //创建SecurityManager工厂,此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-config.ini");
        //得到SecurityManager示例,并绑定SecrityUtils
        //默认为单例.
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        CtChanChildToken token = new CtChanChildToken();
        token.setPassword("Abc123_&");
        token.setUsername("OsbornChen");
        subject.login(token);
        System.out.println(subject.isAuthenticated());
        subject.checkRole("aa");
    }


}
