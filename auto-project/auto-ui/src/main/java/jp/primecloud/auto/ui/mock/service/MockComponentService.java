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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import jp.primecloud.auto.common.status.ComponentInstanceStatus;
import jp.primecloud.auto.common.status.ComponentStatus;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentInstance;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.ComponentInstanceDto;
import jp.primecloud.auto.service.dto.ComponentTypeDto;
import jp.primecloud.auto.ui.mock.XmlDataLoader;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockComponentService implements ComponentService {

    @Override
    public List<ComponentDto> getComponents(Long farmNo) {
        List<ComponentDto> result = new ArrayList<ComponentDto>();
        List<Component> components = XmlDataLoader.getData("component.xml", Component.class);
        LinkedHashMap<Long, List<ComponentInstance>>  componentInstanceMap = getComponentInstanceMap();
        LinkedHashMap<Long, List<ComponentConfig>> componentConfigMap = getComponentConfigMap();
        LinkedHashMap<Long, List<InstanceConfig>> instanceConfigMap = getInstanceConfigMap();
        LinkedHashMap<Long, Instance> instanceMap = getInstanceMap();
        LinkedHashMap<Long, ComponentType> componentTypeMap = getComponentTypeMap();

        for (Component component : components) {
            Long componentNo = component.getComponentNo();

            //ComponentType
            ComponentType componentType = componentTypeMap.get(componentNo);

            // コンポーネントのステータスを求める
            ComponentStatus componentStatus;
            Set<ComponentInstanceStatus> statuses = new HashSet<ComponentInstanceStatus>();

            //ComponentInstance
            List<ComponentInstanceDto> componentInstanceDtos = new ArrayList<ComponentInstanceDto>();
            List<ComponentInstance> componentInstances = componentInstanceMap.get(componentNo);
            if (componentInstances != null) {
                for (ComponentInstance componentInstance: componentInstances) {
                    statuses.add(ComponentInstanceStatus.fromStatus(componentInstance.getStatus()));
                    ComponentInstanceDto componentInstanceDto = new ComponentInstanceDto();
                    componentInstanceDto.setComponentInstance(componentInstance);

                    Instance instance = instanceMap.get(componentInstance.getInstanceNo());
                    String url = createUrl(instance.getPublicIp(), componentType);

                    componentInstanceDto.setUrl(url);
                    componentInstanceDtos.add(componentInstanceDto);
                }
            }

            //ComponentConfig
            List<ComponentConfig> componentConfigs = componentConfigMap.get(componentNo);

            //InstanceConfig
            List<InstanceConfig> instanceConfigs = instanceConfigMap.get(componentNo);

            if (statuses.contains(ComponentInstanceStatus.WARNING)) {
                componentStatus = ComponentStatus.WARNING;
            } else if (statuses.contains(ComponentInstanceStatus.CONFIGURING)) {
                componentStatus = ComponentStatus.CONFIGURING;
            } else if (statuses.contains(ComponentInstanceStatus.RUNNING)) {
                if (statuses.contains(ComponentInstanceStatus.STARTING)) {
                    componentStatus = ComponentStatus.CONFIGURING;
                } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
                    componentStatus = ComponentStatus.CONFIGURING;
                } else {
                    componentStatus = ComponentStatus.RUNNING;
                }
            } else if (statuses.contains(ComponentInstanceStatus.STARTING)) {
                componentStatus = ComponentStatus.STARTING;
            } else if (statuses.contains(ComponentInstanceStatus.STOPPING)) {
                componentStatus = ComponentStatus.STOPPING;
            } else {
                componentStatus = ComponentStatus.STOPPED;
            }

            ComponentDto dto = new ComponentDto();
            dto.setComponent(component);
            dto.setComponentType(componentType);
            dto.setComponentConfigs(componentConfigs);
            dto.setComponentInstances(componentInstanceDtos);
            dto.setInstanceConfigs(instanceConfigs);
            dto.setStatus(componentStatus.toString());
            result.add(dto);
        }
        return result;
    }

    @Override
    public void deleteComponent(Long componentNo) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void associateInstances(Long componentNo, List<Long> instanceNos) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void updateComponent(Long componentNo, String comment, Integer diskSize,
            String customParam1, String customParam2, String customParam3) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public Long createComponent(Long farmNo, String componentName, Long componentTypeNo, String comment,
            Integer diskSize) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<ComponentTypeDto> getComponentTypes(Long farmNo) {
        List<ComponentTypeDto> dtos = new ArrayList<ComponentTypeDto>();

        List<ComponentType> componentTypes = XmlDataLoader.getData("componentType.xml", ComponentType.class);
        for (ComponentType componentType : componentTypes) {
            if ("lb".equals(componentType.getLayer())) {
                continue;
            }

            List<Long> instanceNos = null;
            if ("db".equals(componentType.getLayer())) {
                instanceNos = Arrays.asList(6L, 7L);
            } else if ("ap_java".equals(componentType.getLayer())) {
                instanceNos = Arrays.asList(5L, 7L);
            } else if ("web".equals(componentType.getLayer())) {
                instanceNos = Arrays.asList(5L, 6L);
            } else if ("prj".equals(componentType.getLayer())) {
                instanceNos = Arrays.asList(1L);
            } else {
                instanceNos = new ArrayList<Long>();
            }

            ComponentTypeDto dto = new ComponentTypeDto();
            dto.setComponentType(componentType);
            dto.setInstanceNos(instanceNos);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public ComponentTypeDto getComponentType(Long componentNo) {
        return getComponentTypes(null).get(0);
    }

    protected String createUrl(String ipAddress, ComponentType componentType) {

        String url = "http://";
        if (componentType.getComponentTypeName().equals("apache")) {
            url = url + ipAddress + ":80/";
        } else if (componentType.getComponentTypeName().equals("tomcat")) {
            url = url + ipAddress + ":8080/";
        } else if (componentType.getComponentTypeName().equals("geronimo")) {
            url = url + ipAddress + ":8080/console/";
        } else if (componentType.getComponentTypeName().equals("mysql")) {
            url = url + ipAddress + ":8085/phpmyadmin/";
        } else if (componentType.getComponentTypeName().equals("prjserver")) {
            url = url + ipAddress + "/trac/prj/top/";
        }

        return url;
    }

    private static LinkedHashMap<Long, List<ComponentInstance>> getComponentInstanceMap() {
        List<ComponentInstance> componentInstances =
            XmlDataLoader.getData("componentInstance.xml", ComponentInstance.class);
        LinkedHashMap<Long, List<ComponentInstance>> map = new LinkedHashMap<Long, List<ComponentInstance>>();
        for (ComponentInstance componentInstance: componentInstances) {
            List<ComponentInstance> list = map.get(componentInstance.getComponentNo());
            if (list == null) {
                list = new ArrayList<ComponentInstance>();
            }
            list.add(componentInstance);
            map.put(componentInstance.getComponentNo(), list);
        }
        return map;
    }

    private static LinkedHashMap<Long, List<ComponentConfig>> getComponentConfigMap() {
        List<ComponentConfig> componentConfigs = XmlDataLoader.getData("componentConfig.xml", ComponentConfig.class);
        LinkedHashMap<Long, List<ComponentConfig>> map = new LinkedHashMap<Long, List<ComponentConfig>>();
        for (ComponentConfig componentConfig: componentConfigs) {
            List<ComponentConfig> list = map.get(componentConfig.getComponentNo());
            if (list == null) {
                list = new ArrayList<ComponentConfig>();
            }
            list.add(componentConfig);
            map.put(componentConfig.getComponentNo(), list);
        }
        return map;
    }

    private static LinkedHashMap<Long, List<InstanceConfig>> getInstanceConfigMap() {
        List<InstanceConfig> instanceConfigs = XmlDataLoader.getData("instanceConfig.xml", InstanceConfig.class);
        LinkedHashMap<Long, List<InstanceConfig>> map = new LinkedHashMap<Long, List<InstanceConfig>>();
        for (InstanceConfig instanceConfig: instanceConfigs) {
            List<InstanceConfig> list = map.get(instanceConfig.getComponentNo());
            if (list == null) {
                list = new ArrayList<InstanceConfig>();
            }
            list.add(instanceConfig);
            map.put(instanceConfig.getComponentNo(), list);
        }
        return map;
    }

    private static LinkedHashMap<Long, Instance> getInstanceMap() {
        List<Instance> instances = XmlDataLoader.getData("instance.xml", Instance.class);
        LinkedHashMap<Long, Instance> map = new LinkedHashMap<Long, Instance>();
        for (Instance instance: instances) {
            map.put(instance.getInstanceNo(), instance);
        }
        return map;
    }

    private static LinkedHashMap<Long, ComponentType> getComponentTypeMap() {
        List<ComponentType> componentTypes = XmlDataLoader.getData("componentType.xml", ComponentType.class);
        LinkedHashMap<Long, ComponentType> map = new LinkedHashMap<Long, ComponentType>();
        for (ComponentType componentType: componentTypes) {
            map.put(componentType.getComponentTypeNo(), componentType);
        }
        return map;
    }
}
