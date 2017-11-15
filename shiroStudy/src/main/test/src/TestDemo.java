import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by keyez on 2017/11/14.
 */
public class TestDemo {
    @Test
    public void testHelloWorld() throws IOException {
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

    private UsernamePasswordToken getToken(Object[] objs) {
        UsernamePasswordToken token = new UsernamePasswordToken(objs[0].toString(), objs[1].toString());
        return token;
    }

}
