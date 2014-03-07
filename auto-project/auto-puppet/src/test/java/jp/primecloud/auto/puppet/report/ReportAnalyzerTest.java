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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ReportAnalyzerTest {

    private Log log = LogFactory.getLog(ReportAnalyzerTest.class);

    private ReportLoader reportLoader;

    private ReportAnalyzer reportAnalyzer;

    @Before
    public void setUp() throws Exception {
        reportLoader = new ReportLoader();
        reportLoader.setReportDir(new File("src/test/resources/yaml"));

        reportAnalyzer = new ReportAnalyzer();
    }

    @Test
    @Ignore
    public void testGetMetricsResources() {
        Map<String, Object> report = reportLoader.loadReport("test", "test.yaml");
        List<MetricsResource> metricsResources = reportAnalyzer.getMetricsResources(report);

        for (MetricsResource metricsResource : metricsResources) {
            log.trace(ReflectionToStringBuilder.toString(metricsResource, ToStringStyle.SHORT_PREFIX_STYLE));
        }

        assertEquals(8, metricsResources.size());
    }

}
