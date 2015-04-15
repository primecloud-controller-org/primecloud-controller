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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.service.FarmService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.ui.mock.XmlDataLoader;
import jp.primecloud.auto.util.MessageUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockFarmService implements FarmService {

    private Log log = LogFactory.getLog(getClass());

    @Override
    public FarmDto getFarm(Long farmNo) {
        List<FarmDto> dtos = getFarms(null, null);
        for (FarmDto dto : dtos) {
            if (farmNo.equals(dto.getFarm().getFarmNo())) {
                return dto;
            }
        }
        return null;
    }

    @Override
    public List<FarmDto> getFarms(Long userNo, Long loginUserNo) {
        List<FarmDto> dtos = new ArrayList<FarmDto>();
        List<Farm> farms = XmlDataLoader.getData("farm.xml", Farm.class);
        for (Farm farm : farms) {
            FarmDto dto = new FarmDto();
            dto.setFarm(farm);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public Long createFarm(Long userNo, String farmName, String comment) {
        Long farmNo = (long) (Math.random() * 100);
        log.info(MessageUtils.format("Create farm.(farmNo={0})", farmNo));
        return farmNo;
    }

    @Override
    public void updateFarm(Long farmNo, String comment, String domainName) {
        log.info(MessageUtils
                .format("updateFarm: farmNo={0}, comment={1}, domainName={2}", farmNo, comment, domainName));
    }

    @Override
    public void deleteFarm(Long farmNo) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
