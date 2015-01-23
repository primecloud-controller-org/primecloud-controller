package jp.primecloud.auto.tool.management;

import org.junit.Ignore;
import org.junit.Test;

import jp.primecloud.auto.tool.management.main.ConfigMain;
import jp.primecloud.auto.tool.management.util.ManagementConfigLoader;

public class ConfigLoadTest {

    @Test
    @Ignore
    public void dummy() {
    }

    @Ignore
    public static void main(String args[]) {

        ManagementConfigLoader.init();

//        System.out.println(Config.getProperty("autoconfigPath"));
//        System.out.println(Config.getPlatforms());

        ConfigMain.showPlatforms();

    }
}
