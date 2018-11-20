package localhost;

import no.nav.syfo.LocalApplication;
import no.nav.syfo.config.ApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class ApplicationConfigTest extends ApplicationConfig {

    @Test
    public void test(){
    }

}
