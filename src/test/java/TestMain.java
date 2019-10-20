import me.kingtux.simplewebsite.SimpleSite;
import me.kingtux.simplewebsite.SimpleSiteBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

public class TestMain {
    public static void main(String[] args) throws IOException {
        new TestMain().test();
    }
    @Test
    public void test() throws IOException {
        Properties properties = new Properties();
        properties.load(TestMain.class.getResourceAsStream("/site.properties"));
        SimpleSiteBuilder simpleSiteBuilder = new SimpleSiteBuilder(properties);
        // Create the simple site
        SimpleSite simpleSite = simpleSiteBuilder.create();
    }
}
