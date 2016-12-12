/*
 * Copyright 2016 by PrimeCloud Controller/OSS Community.
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
package jp.primecloud.auto.ui.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

@Ignore("Can't create /opt/adc/conf/ directory at Travis CI.")
public class ViewPropertiesTest {

    private static File file = new File("/opt/adc/conf/view.properties");

    private static File originFile = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (file.exists()) {
            originFile = new File(file.getAbsolutePath() + ".origin");
            FileUtils.moveFile(file, originFile);
        }

        file.getParentFile().mkdirs();

        FileUtils.copyFile(new ClassPathResource("view.properties", ViewPropertiesTest.class).getFile(), file);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FileUtils.deleteQuietly(file);

        if (originFile != null) {
            FileUtils.moveFile(originFile, file);
        }
    }

    @Test
    public void test() {
        assertEquals("ComponentTypeNameXxx", ViewProperties.getComponentTypeName("xxx"));
        assertEquals("ComponentTypeLayerXxx", ViewProperties.getComponentTypeLayer("xxx"));
        assertEquals("PlatformNameXxx", ViewProperties.getPlatformName("xxx"));
        assertEquals("PlatformSimpleNameXxx", ViewProperties.getPlatformSimpleName("xxx"));
        assertEquals("ImageNameXxx", ViewProperties.getImageName("xxx"));
        assertEquals("ImageOsXxx", ViewProperties.getImageOs("xxx"));
        assertEquals("TemplateNameXxx", ViewProperties.getTemplateName("xxx"));
        assertEquals("TemplateDescriptionXxx", ViewProperties.getTemplateDesc("xxx"));
        assertEquals("CaptionXxx", ViewProperties.getCaption("xxx"));
        assertEquals("LoadBalancerTypeXxx", ViewProperties.getLoadBalancerType("xxx"));
    }

}
