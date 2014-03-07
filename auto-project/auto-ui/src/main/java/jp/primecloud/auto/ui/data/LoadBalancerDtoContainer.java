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
package jp.primecloud.auto.ui.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.service.LoadBalancerService;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.LoadBalancerTable;
import jp.primecloud.auto.ui.util.BeanContext;
import jp.primecloud.auto.ui.util.ViewContext;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDtoContainer extends BeanItemContainer<LoadBalancerDto> implements Serializable {

    public LoadBalancerDtoContainer() {
        super(LoadBalancerDto.class);
        refresh();
    }

    public LoadBalancerDtoContainer(Collection<LoadBalancerDto> dtos) {
        super(LoadBalancerDto.class);
        for (LoadBalancerDto dto : dtos) {
            addItem(dto);
        }
    }

    public void refresh() {
        // ロジックを実行
        removeAllItems();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
            for (LoadBalancerDto dto : loadBalancerService.getLoadBalancers(ViewContext.getFarmNo())) {
                addItem(dto);
            }
        }

    }

    public void refresh2(LoadBalancerTable table) {
        // ロジックを実行
        Collection<LoadBalancerDto> collection = this.getItemIds();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            LoadBalancerService loadBalancerService = BeanContext.getBean(LoadBalancerService.class);
            Object[] o = collection.toArray(); //現在のitem
            List<LoadBalancerDto> dtos = loadBalancerService.getLoadBalancers(farmNo); //取得したデータ
            for (int i = 0; i < o.length; i++) {
                LoadBalancerDto oldDto = (LoadBalancerDto) o[i];
                for (int j = 0; j < dtos.size(); j++) {
                    LoadBalancerDto newDto = dtos.get(j);
                    if (oldDto.getLoadBalancer().getLoadBalancerNo().equals(
                            newDto.getLoadBalancer().getLoadBalancerNo())) {
                        final BeanItem<LoadBalancerDto> dto = this.getItem(o[i]);
                        dto.getItemProperty("loadBalancer").setValue(newDto.getLoadBalancer());
                        dto.getItemProperty("platform").setValue(newDto.getPlatform());
                        dto.getItemProperty("awsLoadBalancer").setValue(newDto.getAwsLoadBalancer());
                        dto.getItemProperty("componentLoadBalancerDto").setValue(newDto.getComponentLoadBalancerDto());
                        dto.getItemProperty("loadBalancerListeners").setValue(newDto.getLoadBalancerListeners());
                        dto.getItemProperty("loadBalancerHealthCheck").setValue(newDto.getLoadBalancerHealthCheck());
                        dto.getItemProperty("loadBalancerInstances").setValue(newDto.getLoadBalancerInstances());
                        dto.getItemProperty("autoScalingConf").setValue(newDto.getAutoScalingConf());
                        dtos.remove(newDto);
                        break;
                    } else {
                        if (dtos.size() == j + 1) {
                            removeItem(oldDto);
                        }
                    }
                }
            }
            for (LoadBalancerDto dto : dtos) {
                addItem(dto);
            }
        }

        final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
            public Container getContainer() {
                return LoadBalancerDtoContainer.this;
            }
        };

        table.containerItemSetChange(event);
        table.refreshDesc();
    }

}
