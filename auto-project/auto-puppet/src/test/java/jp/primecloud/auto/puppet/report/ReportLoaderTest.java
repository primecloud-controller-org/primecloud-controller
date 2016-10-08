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
package jp.primecloud.auto.puppet.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ReportLoaderTest {

    private ReportLoader reportLoader;

    @Before
    public void setUp() throws Exception {
        reportLoader = new ReportLoader();
        reportLoader.setReportDir(new File("src/test/resources/yaml"));
    }

    @Test
    @Ignore
    public void testListReportFiles() {
        List<String> files = reportLoader.listReportFiles("test");

        assertEquals(3, files.size());
        assertEquals(true, files.contains("test.yaml"));
    }

    @Test
    @Ignore
    public void testLoadReport100() throws Exception {
        final List<Exception> exceptions = new ArrayList<Exception>();

        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        reportLoader.loadReport("test", "test.yaml");
                    } catch (Exception e) {
                        e.printStackTrace();
                        exceptions.add(e);
                    }
                }
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        assertEquals(0, exceptions.size());
    }
}
