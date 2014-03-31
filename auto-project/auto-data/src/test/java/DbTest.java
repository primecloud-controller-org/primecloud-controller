import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DbTest {

    ClassPathXmlApplicationContext init() {
        List<String> locations = new ArrayList<String>();
        locations.add("applicationCore.xml");
        return new ClassPathXmlApplicationContext(locations.toArray(new String[locations.size()]));
    }

    @Test
    @Ignore
    public void test1() throws Exception {
        ClassPathXmlApplicationContext context = init();

        context.destroy();
    }

}
