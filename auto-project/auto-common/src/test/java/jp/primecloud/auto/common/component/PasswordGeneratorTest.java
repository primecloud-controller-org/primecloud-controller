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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import jp.primecloud.auto.common.component.PasswordGenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class PasswordGeneratorTest {

    private static final Log log = LogFactory.getLog(PasswordGeneratorTest.class);

    @Test
    public void testGenerate() {
        PasswordGenerator generator = new PasswordGenerator();

        String password = generator.generate(50);
        String password2 = generator.generate(50);
        String password3 = generator.generate(50);

        log.debug(password);
        log.debug(password2);
        log.debug(password3);

        assertFalse(password.equals(password2));
        assertFalse(password.equals(password3));
        assertFalse(password2.equals(password3));
    }

    @Test
    public void testGenerate2() {
        PasswordGenerator generator = new PasswordGenerator();

        assertEquals(50, generator.generate(50).length());
        assertEquals(51, generator.generate(51).length());
        assertEquals(52, generator.generate(52).length());
        assertEquals(53, generator.generate(53).length());
    }

}
