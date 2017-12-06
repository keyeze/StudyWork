import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Created by keyez on 2017/11/23.
 */
public class test {
    private static Logger logger = Logger.getLogger(test.class);

    @Test
    public void test() {
        logger.info("OK");
    }

    public static void main(String[] args) {
        logger.info("OK");
    }
}
