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
package jp.primecloud.auto.service;

import java.util.List;

import jp.primecloud.auto.service.dto.FarmDto;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface FarmService {

    public List<FarmDto> getFarms(Long userNo, Long loginUserNo);

    public FarmDto getFarm(Long farmNo);

    public Long createFarm(Long userNo, String farmName, String comment);

    public void updateFarm(Long farmNo, String comment, String domainName);

    public void deleteFarm(Long farmNo);

}
