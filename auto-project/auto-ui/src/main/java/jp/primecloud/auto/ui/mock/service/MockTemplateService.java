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
package jp.primecloud.auto.ui.mock.service;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.Template;
import jp.primecloud.auto.service.TemplateService;
import jp.primecloud.auto.service.dto.TemplateDto;
import jp.primecloud.auto.service.impl.TemplateServiceImpl;
import jp.primecloud.auto.ui.mock.XmlDataLoader;
import jp.primecloud.auto.util.MessageUtils;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockTemplateService extends TemplateServiceImpl implements TemplateService {

    @Override
    public List<TemplateDto> getTemplates(Long userNo) {
        List<TemplateDto> dtos = new ArrayList<TemplateDto>();

        List<Template> templates = XmlDataLoader.getData("template.xml", Template.class);
        for (Template template : templates) {
            TemplateDto dto = new TemplateDto();
            dto.setTemplate(template);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void applyTemplate(Long farmNo, Long templateNo) {
        // 何もしない
        log.info(MessageUtils.format("Apply template.(farmNo={0}, templateNo={1})", farmNo, templateNo));
    }
}
