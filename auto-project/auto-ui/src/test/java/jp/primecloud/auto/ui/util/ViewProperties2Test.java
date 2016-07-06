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
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Can't create /opt/adc/conf/ directory at Travis CI.")
public class ViewProperties2Test {

    private static File file = new File("/opt/adc/conf/view.properties");

    private static File langFile = new File("/opt/adc/conf/view_" + Locale.getDefault().getLanguage() + ".properties");

    private static File countryFile = new File("/opt/adc/conf/view_" + Locale.getDefault().getLanguage() + "_"
            + Locale.getDefault().getCountry() + ".properties");

    private static File variantFile = new File("/opt/adc/conf/view_" + Locale.getDefault().getLanguage() + "_"
            + Locale.getDefault().getCountry() + "_" + Locale.getDefault().getVariant() + ".properties");

    private static File originFile = null;

    private static File originLangFile = null;

    private static File originCountryFile = null;

    private static File originVariantFile = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (file.exists()) {
            originFile = new File(file.getAbsolutePath() + ".origin");
            FileUtils.moveFile(file, originFile);
        }

        if (langFile.exists()) {
            originLangFile = new File(langFile.getAbsolutePath() + ".origin");
            FileUtils.moveFile(langFile, originLangFile);
        }

        if (countryFile.exists()) {
            originCountryFile = new File(countryFile.getAbsoluteFile() + ".origin");
            FileUtils.moveFile(countryFile, originCountryFile);
        }

        if (variantFile.exists()) {
            originVariantFile = new File(variantFile.getAbsoluteFile() + ".origin");
            FileUtils.moveFile(variantFile, originVariantFile);
        }

        file.getParentFile().mkdirs();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(langFile);
        FileUtils.deleteQuietly(countryFile);
        FileUtils.deleteQuietly(variantFile);

        if (originFile != null) {
            FileUtils.moveFile(originFile, file);
        }

        if (originLangFile != null) {
            FileUtils.moveFile(originLangFile, langFile);
        }

        if (originCountryFile != null) {
            FileUtils.moveFile(originCountryFile, countryFile);
        }

        if (originVariantFile != null) {
            FileUtils.moveFile(originVariantFile, variantFile);
        }
    }

    @Test
    public void testBase() throws Exception {
        FileUtils.writeStringToFile(file, "key=base");

        ViewProperties.reload();
        assertEquals("base", ViewProperties.get("key"));

        FileUtils.deleteQuietly(file);
    }

    @Test
    public void testLang() throws Exception {
        if (StringUtils.isEmpty(Locale.getDefault().getLanguage())) {
            return;
        }

        FileUtils.writeStringToFile(file, "key=base");
        FileUtils.writeStringToFile(langFile, "key=lang");

        ViewProperties.reload();
        assertEquals("lang", ViewProperties.get("key"));

        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(langFile);
    }

    @Test
    public void testCountry() throws Exception {
        if (StringUtils.isEmpty(Locale.getDefault().getCountry())) {
            return;
        }

        FileUtils.writeStringToFile(file, "key=base");
        FileUtils.writeStringToFile(langFile, "key=lang");
        FileUtils.writeStringToFile(countryFile, "key=country");

        ViewProperties.reload();
        assertEquals("country", ViewProperties.get("key"));

        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(langFile);
        FileUtils.deleteQuietly(countryFile);
    }

    @Test
    public void testVariant() throws Exception {
        if (StringUtils.isEmpty(Locale.getDefault().getVariant())) {
            return;
        }

        FileUtils.writeStringToFile(file, "key=base");
        FileUtils.writeStringToFile(langFile, "key=lang");
        FileUtils.writeStringToFile(countryFile, "key=country");
        FileUtils.writeStringToFile(variantFile, "key=variant");

        ViewProperties.reload();
        assertEquals("variant", ViewProperties.get("key"));

        FileUtils.deleteQuietly(file);
        FileUtils.deleteQuietly(langFile);
        FileUtils.deleteQuietly(countryFile);
        FileUtils.deleteQuietly(variantFile);
    }

}
