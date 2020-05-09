import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogTests {
    // 创建日志器对象
    private static Logger logger = LoggerFactory.getLogger(LogTests.class);

    public static void main(String[] args) throws IOException {
        //String message1 = "app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" zhengzhou \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        String message2 = "app1 EVALUATE [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        //String message3 = "app1 ABCDEFG [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        logger.info(message2);
        System.in.read();
    }
}
