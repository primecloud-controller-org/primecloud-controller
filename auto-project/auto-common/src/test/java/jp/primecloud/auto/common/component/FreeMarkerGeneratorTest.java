/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.common.component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.primecloud.auto.common.component.FreeMarkerGenerator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * {@link FreeMarkerGenerator}のテストクラスです。
 * </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FreeMarkerGeneratorTest {

    @Autowired
    private FreeMarkerGenerator generator;

    @Test
    public void test1() throws Exception {
        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("aaa", "AAA");
        rootMap.put("bbb", "BBB");
        rootMap.put("enable", Boolean.TRUE);
        rootMap.put("num", 12345.6789);

        Model model = new Model();
        model.setEnabled(true);
        rootMap.put("model", model);

        String data = generator.generate("test1.ftl", rootMap);

        File file = new File("target/tmp/test1.txt");
        FileUtils.writeStringToFile(file, data, "UTF-8");
    }

    public static class Model {

        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

}
