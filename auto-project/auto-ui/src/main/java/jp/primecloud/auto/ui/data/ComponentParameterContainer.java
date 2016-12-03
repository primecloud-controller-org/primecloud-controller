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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.component.apache.ApacheConstants;
import jp.primecloud.auto.component.geronimo.GeronimoConstants;
import jp.primecloud.auto.component.mysql.MySQLConstants;
import jp.primecloud.auto.component.prjserver.PrjserverConstants;
import jp.primecloud.auto.component.tomcat.TomcatConstants;
import jp.primecloud.auto.entity.crud.ComponentConfig;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.InstanceConfig;
import jp.primecloud.auto.process.ComponentConstants;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.InstanceDto;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.BeanItemContainer;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ComponentParameterContainer extends BeanItemContainer<ComponentParameter> implements Serializable {

    public ComponentParameterContainer(ComponentDto component, Collection<InstanceDto> instances) {
        super(ComponentParameter.class);

        ComponentType componentType = component.getComponentType();
        List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();

        // ディスク
        String diskSize = null;
        for (ComponentConfig config : component.getComponentConfigs()) {
            if (ComponentConstants.CONFIG_NAME_DISK_SIZE.equals(config.getConfigName())) {
                diskSize = config.getConfigValue();
            }
        }
        if (StringUtils.isNotEmpty(diskSize)) {
            String mountPoint = null;
            if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                mountPoint = "/mnt/db";
            } else if (TomcatConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                mountPoint = "/mnt/ap";
            } else if (GeronimoConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                mountPoint = "/mnt/ap";
            } else if (ApacheConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                mountPoint = "/mnt/web";
            } else if (PrjserverConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
                mountPoint = "/mnt/data01";
            }

            String captionDisk = ViewProperties.getCaption("param.disk");
            parameters.add(new ComponentParameter(captionDisk, ViewProperties.getCaption("param.disk.size"), diskSize
                    + ViewProperties.getCaption("param.disk.gb")));
            parameters.add(new ComponentParameter(captionDisk, ViewProperties.getCaption("param.disk.mountpoint"),
                    mountPoint));
        }

        // MySQL
        if (MySQLConstants.COMPONENT_TYPE_NAME.equals(componentType.getComponentTypeName())) {
            // Master
            Long masterInstanceNo = null;
            for (InstanceConfig config : component.getInstanceConfigs()) {
                if (MySQLConstants.CONFIG_NAME_MASTER_INSTANCE_NO.equals(config.getConfigName())) {
                    if (StringUtils.isEmpty(config.getConfigValue())) {
                        masterInstanceNo = config.getInstanceNo();
                        break;
                    }
                }
            }
            InstanceDto masterInstance = null;
            if (masterInstanceNo != null) {
                for (InstanceDto instance : instances) {
                    if (masterInstanceNo.equals(instance.getInstance().getInstanceNo())) {
                        masterInstance = instance;
                        break;
                    }
                }
            }
            String master = masterInstance != null ? masterInstance.getInstance().getFqdn() : "";
            String captionMysql = ViewProperties.getCaption("param.mysql");
            parameters
                    .add(new ComponentParameter(captionMysql, ViewProperties.getCaption("param.mysql.master"), master));

            // phpMyAdmin
            boolean phpMyAdmin = false;
            for (ComponentConfig config : component.getComponentConfigs()) {
                if (MySQLConstants.CONFIG_NAME_PHP_MY_ADMIN.equals(config.getConfigName())) {
                    phpMyAdmin = BooleanUtils.toBoolean(config.getConfigValue());
                }
            }
            String usePhpMyAdmin = phpMyAdmin ? ViewProperties.getCaption("param.phpmyadmin.enable") : ViewProperties
                    .getCaption("param.phpmyadmin.disable");
            parameters.add(new ComponentParameter(captionMysql, ViewProperties.getCaption("param.phpmyadmin"),
                    usePhpMyAdmin));
        }

        for (ComponentParameter parameter : parameters) {
            addItem(parameter);
        }
    }

}
