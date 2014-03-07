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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ReportAnalyzer {

    /**
     * TODO: メソッドコメントを記述
     *
     * @param report
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MetricsResource> getMetricsResources(Map<String, Object> report) {
        List<MetricsResource> metricsResources = new ArrayList<MetricsResource>();

        if (report == null) {
            return metricsResources;
        }

        Map<String, Object> metrics = (Map<String, Object>) report.get("metrics");
        if (metrics == null) {
            return metricsResources;
        }

        Map<String, Object> resources = (Map<String, Object>) metrics.get("resources");
        if (resources == null) {
            return metricsResources;
        }

        List<List<Object>> values = (List<List<Object>>) resources.get("values");
        if (values == null) {
            return metricsResources;
        }

        for (List<Object> value : values) {
            String name = value.get(1).toString();
            int count = Integer.parseInt(value.get(2).toString());

            MetricsResource metricsResource = new MetricsResource();
            metricsResource.setName(name);
            metricsResource.setCount(count);
            metricsResources.add(metricsResource);
        }

        return metricsResources;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param report
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MetricsTime> getMetricsTimes(Map<String, Object> report) {
        List<MetricsTime> metricsTimes = new ArrayList<MetricsTime>();

        if (report == null) {
            return metricsTimes;
        }

        Map<String, Object> metrics = (Map<String, Object>) report.get("metrics");
        if (metrics == null) {
            return metricsTimes;
        }

        Map<String, Object> times = (Map<String, Object>) metrics.get("time");
        if (times == null) {
            return metricsTimes;
        }

        List<List<Object>> values = (List<List<Object>>) times.get("values");
        if (values == null) {
            return metricsTimes;
        }

        for (List<Object> value : values) {
            String name = value.get(1).toString();
            double time = Double.parseDouble(value.get(2).toString());

            MetricsTime metricsTime = new MetricsTime();
            metricsTime.setName(name);
            metricsTime.setTime(time);
            metricsTimes.add(metricsTime);
        }

        return metricsTimes;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param report
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MetricsChange> getMetricsChanges(Map<String, Object> report) {
        List<MetricsChange> metricsChanges = new ArrayList<MetricsChange>();

        if (report == null) {
            return metricsChanges;
        }

        Map<String, Object> metrics = (Map<String, Object>) report.get("metrics");
        if (metrics == null) {
            return metricsChanges;
        }

        Map<String, Object> resources = (Map<String, Object>) metrics.get("changes");
        if (resources == null) {
            return metricsChanges;
        }

        List<List<Object>> values = (List<List<Object>>) resources.get("values");
        if (values == null) {
            return metricsChanges;
        }

        for (List<Object> value : values) {
            String name = value.get(1).toString();
            int count = Integer.parseInt(value.get(2).toString());

            MetricsChange metricsChange = new MetricsChange();
            metricsChange.setName(name);
            metricsChange.setCount(count);
            metricsChanges.add(metricsChange);
        }

        return metricsChanges;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param report
     * @return
     */
    public String getStatus(Map<String, Object> report) {
        String status = "";

        if (report == null) {
            return status;
        }

        status = (String) report.get("status");
        if (status == null) {
            return "";
        }

        return status;
    }
}
