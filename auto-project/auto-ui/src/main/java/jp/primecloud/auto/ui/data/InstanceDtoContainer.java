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

import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.ServerTable;
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
public class InstanceDtoContainer extends BeanItemContainer<InstanceDto> implements Serializable,
        Container.ItemSetChangeNotifier {

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] { "instanceNo", "fqdn", "publicIp", "status" };

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
//    public static final Object[] SERVICE_DESC = new Object[] { "instanceName", "status", "platform" };
    public static final Object[] SERVICE_DESC = new Object[] { "check", "instanceName", "urlIcon","status", "platform" };

    /**
     * Natural property order for Farm bean. Used in tables and forms.
     */
    public static final Object[] SERVER_DESC = new Object[] { "fqdn", "publicIp", "platformNo", "status" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] { "no", "name", "ipaddress", "status" };

    public InstanceDtoContainer() {

        super(InstanceDto.class);
        refresh();
    }

    public InstanceDtoContainer(Collection<InstanceDto> instances) {

        super(InstanceDto.class);

        for (InstanceDto dto : instances) {
            addItem(dto);
        }
    }

    public void refresh() {

        // ロジックを実行
        removeAllItems();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            InstanceService instanceService = BeanContext.getBean(InstanceService.class);
            for (InstanceDto instanceDto : instanceService.getInstances(ViewContext.getFarmNo())) {
                addItem(instanceDto);
            }
        }

    }

    public void refresh2(ServerTable table) {

        // ロジックを実行
        Collection<InstanceDto> collection = this.getItemIds();
        Long farmNo = ViewContext.getFarmNo();
        if (farmNo != null) {
            InstanceService instanceService = BeanContext.getBean(InstanceService.class);
            Object[] o = collection.toArray(); //現在のitem
            List<InstanceDto> dtos = instanceService.getInstances(farmNo); //取得したデータ
            for (int i = 0; i < o.length; i++) {
                InstanceDto oldInstance = (InstanceDto) o[i];
                for (int j = 0; j < dtos.size(); j++) {
                    InstanceDto newInstance = dtos.get(j);
                    if (oldInstance.getInstance().getInstanceNo().equals(newInstance.getInstance().getInstanceNo())) {
                        final BeanItem<InstanceDto> dto = this.getItem(o[i]);
                        dto.getItemProperty("instance").setValue(newInstance.getInstance());
                        dto.getItemProperty("platform").setValue(newInstance.getPlatform());
                        dto.getItemProperty("image").setValue(newInstance.getImage());
                        dto.getItemProperty("instanceConfigs").setValue(newInstance.getInstanceConfigs());
                        dto.getItemProperty("componentInstances").setValue(newInstance.getComponentInstances());
                        dto.getItemProperty("awsInstance").setValue(newInstance.getAwsInstance());
                        dto.getItemProperty("awsAddress").setValue(newInstance.getAwsAddress());
                        dto.getItemProperty("awsVolumes").setValue(newInstance.getAwsVolumes());
                        dto.getItemProperty("vmwareInstance").setValue(newInstance.getVmwareInstance());
                        dto.getItemProperty("vmwareKeyPair").setValue(newInstance.getVmwareKeyPair());
                        dto.getItemProperty("vmwareDisks").setValue(newInstance.getVmwareDisks());
                        dto.getItemProperty("niftyInstance").setValue(newInstance.getNiftyInstance());
                        dto.getItemProperty("niftyKeyPair").setValue(newInstance.getNiftyKeyPair());
                        dto.getItemProperty("cloudstackInstance").setValue(newInstance.getCloudstackInstance());
                        dto.getItemProperty("cloudstackAddress").setValue(newInstance.getCloudstackAddress());
                        dto.getItemProperty("cloudstackVolumes").setValue(newInstance.getCloudstackVolumes());
                        dtos.remove(newInstance);
                        break;
                    } else {
                        if (dtos.size() == j + 1) {
                            removeItem(oldInstance);
                        }
                    }
                }
            }
            for (InstanceDto instance : dtos) {
                addItem(instance);
            }
        }

        final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
            private static final long serialVersionUID = -3002746333251784195L;

            public Container getContainer() {
                return InstanceDtoContainer.this;
            }
        };

        table.containerItemSetChange(event);
        table.refreshDesc();

    }

}
