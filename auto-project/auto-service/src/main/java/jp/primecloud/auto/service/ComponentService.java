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

import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface ComponentService {

    public List<ComponentDto> getComponents(Long farmNo);

    public Long createComponent(Long farmNo, String componentName, Long componentTypeNo, String comment,
            Integer diskSize);

    public void associateInstances(Long componentNo, List<Long> instanceNos);

    public void updateComponent(Long componentNo, String comment, Integer diskSize,
            String customParam1, String customParam2, String customParam3);

    public void deleteComponent(Long componentNo);

    public List<ComponentTypeDto> getComponentTypes(Long farmNo);

    public ComponentTypeDto getComponentType(Long componentNo);

    public Collection<Object> checkAttachDisk(Long farmNo, Long componentNo, String instanceName,
            String notSelectedItem, Collection<Object> moveList);

}
