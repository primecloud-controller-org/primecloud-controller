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
public class InstanceDtoContainer extends BeanItemContainer<InstanceDto>
        implements Serializable, Container.ItemSetChangeNotifier {

    public InstanceDtoContainer() {
        super(InstanceDto.class);
        refresh();
    }

    public InstanceDtoContainer(List<InstanceDto> instances) {
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
            List<InstanceDto> instances = instanceService.getInstances(farmNo); //取得したデータ
            for (int i = 0; i < o.length; i++) {
                InstanceDto oldInstance = (InstanceDto) o[i];
                for (int j = 0; j < instances.size(); j++) {
                    InstanceDto newInstance = instances.get(j);
                    if (oldInstance.getInstance().getInstanceNo().equals(newInstance.getInstance().getInstanceNo())) {
                        final BeanItem<InstanceDto> item = this.getItem(o[i]);
                        item.getItemProperty("instance").setValue(newInstance.getInstance());
                        item.getItemProperty("zabbixInstance").setValue(newInstance.getZabbixInstance());
                        item.getItemProperty("platform").setValue(newInstance.getPlatform());
                        item.getItemProperty("image").setValue(newInstance.getImage());
                        item.getItemProperty("instanceConfigs").setValue(newInstance.getInstanceConfigs());
                        item.getItemProperty("componentInstances").setValue(newInstance.getComponentInstances());

                        //AWS
                        item.getItemProperty("awsInstance").setValue(newInstance.getAwsInstance());
                        item.getItemProperty("awsAddress").setValue(newInstance.getAwsAddress());
                        item.getItemProperty("awsVolumes").setValue(newInstance.getAwsVolumes());

                        //VMWare
                        item.getItemProperty("vmwareInstance").setValue(newInstance.getVmwareInstance());
                        item.getItemProperty("vmwareKeyPair").setValue(newInstance.getVmwareKeyPair());
                        item.getItemProperty("vmwareDisks").setValue(newInstance.getVmwareDisks());

                        //Nifty
                        item.getItemProperty("niftyInstance").setValue(newInstance.getNiftyInstance());
                        item.getItemProperty("niftyKeyPair").setValue(newInstance.getNiftyKeyPair());
                        item.getItemProperty("niftyVolumes").setValue(newInstance.getNiftyVolumes());

                        //CloudStack
                        item.getItemProperty("cloudstackInstance").setValue(newInstance.getCloudstackInstance());
                        item.getItemProperty("cloudstackAddress").setValue(newInstance.getCloudstackAddress());
                        item.getItemProperty("cloudstackVolumes").setValue(newInstance.getCloudstackVolumes());

                        //VCloud
                        item.getItemProperty("vcloudInstance").setValue(newInstance.getVcloudInstance());
                        item.getItemProperty("vcloudKeyPair").setValue(newInstance.getVcloudKeyPair());
                        item.getItemProperty("vcloudDisks").setValue(newInstance.getVcloudDisks());
                        item.getItemProperty("vcloudInstanceNetworks")
                                .setValue(newInstance.getVcloudInstanceNetworks());
                        item.getItemProperty("platformVcloudStorageType")
                                .setValue(newInstance.getPlatformVcloudStorageType());

                        //Azure
                        item.getItemProperty("azureInstance").setValue(newInstance.getAzureInstance());
                        item.getItemProperty("azureCertificate").setValue(newInstance.getAzureCertificate());
                        item.getItemProperty("azureDisks").setValue(newInstance.getAzureDisks());

                        //Openstack
                        item.getItemProperty("openstackInstance").setValue(newInstance.getOpenstackInstance());
                        item.getItemProperty("openstackCertificate").setValue(newInstance.getOpenstackCertificate());
                        item.getItemProperty("openstackVolumes").setValue(newInstance.getOpenstackVolumes());

                        instances.remove(newInstance);
                        break;
                    } else {
                        if (instances.size() == j + 1) {
                            removeItem(oldInstance);
                        }
                    }
                }
            }
            for (InstanceDto instance : instances) {
                addItem(instance);
            }
        }

        final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
            @Override
            public Container getContainer() {
                return InstanceDtoContainer.this;
            }
        };

        table.containerItemSetChange(event);
        table.refreshDesc();
    }

}
