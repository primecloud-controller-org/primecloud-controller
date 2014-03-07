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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.exception.AutoException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class FreeMarkerGenerator {

    protected Log log = LogFactory.getLog(FreeMarkerGenerator.class);

    protected Configuration configuration;

    public String generate(String templateName, Map<String, Object> rootMap) {
        StringWriter writer = new StringWriter();
        try {
            write(templateName, rootMap, writer);
            return writer.toString();
        } catch (AutoException e) {
            ((AutoException) e).addDetailInfo("content=" + writer.toString());
            throw e;
        }
    }

    public void write(String templateName, Map<String, Object> rootMap, Writer writer) {
        try {
            Template template = configuration.getTemplate(templateName);
            Map<String, Object> copyMap = copyMap(rootMap);
            template.process(copyMap, writer);
        } catch (IOException e) {
            throw new AutoException("ECOMMON-000000", e);
        } catch (TemplateException e) {
            throw new AutoException("ECOMMON-000000", e);
        }
    }

    protected Map<String, Object> copyMap(Map<String, Object> map) {
        Map<String, Object> copyMap = new HashMap<String, Object>(map);

        // デバッグ用
        Map<String, String> entries = new HashMap<String, String>();
        for (Entry<String, Object> entry : map.entrySet()) {
            entries.put(entry.getKey(), ObjectUtils.toString(entry.getValue()));
        }
        copyMap.put("entries", entries);

        return copyMap;
    }

    /**
     * configurationを設定します。
     *
     * @param configuration configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
