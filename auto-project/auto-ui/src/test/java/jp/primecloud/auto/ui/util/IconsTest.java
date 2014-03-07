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
package jp.primecloud.auto.ui.util;

import java.io.File;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class IconsTest {

    @Test
    @Ignore
    public void listup() {
        File dir = new File("src/main/webapp/VAADIN/themes/classy/icons");
        for (File f : dir.listFiles()) {
            if (!f.isFile()) {
                continue;
            }

            String file = f.getName();
            String name = file.substring(0, file.lastIndexOf("."));
            System.out.println(name.toUpperCase(Locale.ENGLISH) + "(\"" + file + "\"),");
            System.out.println();
        }
    }

}
