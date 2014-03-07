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
package jp.primecloud.auto.common.interceptor;

import static org.junit.Assert.assertEquals;
import jp.primecloud.auto.common.interceptor.TraceInterceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * {@link TraceInterceptor}のテストクラスです。
 * </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TraceInterceptorTest {

    @Autowired
    @Qualifier("target")
    private Target target;

    @Test
    public void testInvoke() {
        assertEquals("aaabbb", target.method1("aaa", "bbb"));
        assertEquals("cccddd", target.method1("ccc", "ddd"));
    }

}
