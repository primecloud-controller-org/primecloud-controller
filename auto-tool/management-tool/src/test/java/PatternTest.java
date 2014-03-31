import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

public class PatternTest {

    @Test
    @Ignore
    public void dummy() {
    }

    public static void main(String args[]) {
        String sql = "INSERT INTO USER SET USERNAME='aaa' ,PASSWORD='bbbbb'";

        Pattern pattern = Pattern.compile("PASSWORD='\\w*'");

        String logSQL = pattern.matcher(sql).replaceAll("PASSWORD='\\*\\*\\*\\*\\*'");
        System.out.println(logSQL);

    }
}
