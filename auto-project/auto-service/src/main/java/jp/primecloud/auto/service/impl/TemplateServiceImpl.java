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
package jp.primecloud.auto.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.ComponentType;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.Template;
import jp.primecloud.auto.entity.crud.TemplateComponent;
import jp.primecloud.auto.entity.crud.TemplateInstance;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ComponentService;
import jp.primecloud.auto.service.InstanceService;
import jp.primecloud.auto.service.PlatformService;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.TemplateService;
import jp.primecloud.auto.service.dto.TemplateDto;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class TemplateServiceImpl extends ServiceSupport implements TemplateService {

    protected ComponentService componentService;

    protected InstanceService instanceService;

    protected PlatformService platformService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TemplateDto> getTemplates(Long userNo) {
        List<TemplateDto> dtos = new ArrayList<TemplateDto>();

        Map<Long, Platform> usablePlatformMap = getUsablePlatformMap(userNo);
        List<Long> enabledImageNos = getEnabledImageNos();
        List<Long> componentTypeNos = getEnabledComponentTypeNos();

        List<Template> templates = templateDao.readAll();
        for (Template template : templates) {
            boolean available = true;

            // インスタンスのプラットフォームやイメージが利用できるかどうかのチェック
            List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(template.getTemplateNo());
            for (TemplateInstance templateInstance : templateInstances) {
                Platform platform = usablePlatformMap.get(templateInstance.getPlatformNo());
                if (platform == null) {
                    // 利用できないプラットフォームの場合
                    available = false;
                    break;
                }

                if (!enabledImageNos.contains(templateInstance.getImageNo())) {
                    // 利用できないイメージの場合
                    available = false;
                    break;
                }
            }

            if (!available) {
                // プラットフォームまたはイメージが利用できないインスタンスがある場合、このテンプレートを含めない
                continue;
            }

            // コンポーネントのタイプが利用できるかどうかのチェック
            List<TemplateComponent> templateComponents = templateComponentDao
                    .readByTemplateNo(template.getTemplateNo());
            for (TemplateComponent templateComponent : templateComponents) {
                if (!componentTypeNos.contains(templateComponent.getComponentTypeNo())) {
                    // 利用できないコンポーネントタイプの場合
                    available = false;
                    break;
                }
            }

            if (!available) {
                // コンポーネントタイプが利用できないコンポーネントがある場合、このテンプレートを含めない
                continue;
            }

            TemplateDto dto = new TemplateDto();
            dto.setTemplate(template);
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyTemplate(Long farmNo, Long templateNo) {
        // 引数チェック
        if (farmNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "farmNo");
        }
        if (templateNo == null) {
            throw new AutoApplicationException("ECOMMON-000003", "templateNo");
        }

        // テンプレートの存在チェック
        Template template = templateDao.read(templateNo);
        if (template == null) {
            throw new AutoApplicationException("ESERVICE-000501", templateNo);
        }

        // ファームの存在チェック
        Farm farm = farmDao.read(farmNo);
        if (farm == null) {
            throw new AutoApplicationException("ESERVICE-000502", farmNo);
        }

        // ファームの空チェック
        List<Component> components = componentDao.readByFarmNo(farmNo);
        if (!components.isEmpty()) {
            throw new AutoApplicationException("ESERVICE-000503", farm.getFarmName());
        }

        List<Instance> instances = instanceDao.readByFarmNo(farmNo);
        if (!instances.isEmpty()) {
            throw new AutoApplicationException("ESERVICE-000503", farm.getFarmName());
        }

        // プラットフォーム情報を取得
        Map<Long, Platform> usablePlatformMap = getUsablePlatformMap(farm.getUserNo());

        // インスタンスを作成
        Map<String, Long> instanceNoMap = new HashMap<String, Long>();

        List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(templateNo);
        for (TemplateInstance templateInstance : templateInstances) {
            Long instanceNo = null;

            Platform platform = usablePlatformMap.get(templateInstance.getPlatformNo());
            // TODO: CLOUD BRANCHING
            if (PCCConstant.PLATFORM_TYPE_AWS.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_CLOUDSTACK.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_VCLOUD.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_AZURE.equals(platform.getPlatformType())
                    || PCCConstant.PLATFORM_TYPE_OPENSTACK.equals(platform.getPlatformType())) {
                instanceNo = instanceService.createIaasInstance(farmNo, templateInstance.getTemplateInstanceName(),
                        templateInstance.getPlatformNo(), templateInstance.getComment(), templateInstance.getImageNo(),
                        templateInstance.getInstanceType());
            } else if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
                instanceNo = instanceService.createVmwareInstance(farmNo, templateInstance.getTemplateInstanceName(),
                        templateInstance.getPlatformNo(), templateInstance.getComment(), templateInstance.getImageNo(),
                        templateInstance.getInstanceType());

            } else if (PCCConstant.PLATFORM_TYPE_NIFTY.equals(platform.getPlatformType())) {
                instanceNo = instanceService.createNiftyInstance(farmNo, templateInstance.getTemplateInstanceName(),
                        templateInstance.getPlatformNo(), templateInstance.getComment(), templateInstance.getImageNo(),
                        templateInstance.getInstanceType());
            } else {
                continue;
            }

            instanceNoMap.put(templateInstance.getTemplateInstanceName(), instanceNo);
        }

        // コンポーネントを作成
        List<TemplateComponent> templateComponents = templateComponentDao.readByTemplateNo(templateNo);
        for (TemplateComponent templateComponent : templateComponents) {
            Long componentNo = componentService.createComponent(farmNo, templateComponent.getTemplateComponentName(),
                    templateComponent.getComponentTypeNo(), templateComponent.getComment(),
                    templateComponent.getDiskSize());

            // コンポーネントにインスタンスを関連付け
            List<Long> instanceNos = new ArrayList<Long>();
            if (StringUtils.isNotEmpty(templateComponent.getAssociate())) {
                String[] instanceNames = StringUtils.split(templateComponent.getAssociate(), ",");
                for (String instanceName : instanceNames) {
                    Long instanceNo = instanceNoMap.get(instanceName.trim());
                    if (instanceNo != null) {
                        instanceNos.add(instanceNo);
                    }
                }
            }
            if (!instanceNos.isEmpty()) {
                componentService.associateInstances(componentNo, instanceNos);
            }
        }
    }

    /**
     * プラットフォーム情報をマップで取得
     *
     * @param userNo
     * @return プラットフォーム情報のマップ(認証情報がないプラットフォームはマップに含まれない)
     */
    protected Map<Long, Platform> getUsablePlatformMap(Long userNo) {
        Map<Long, Platform> usablePlatformMap = new HashMap<Long, Platform>();

        List<Platform> platforms = platformDao.readAll();
        for (Platform platform : platforms) {
            if (BooleanUtils.isNotTrue(platform.getSelectable())) {
                // 使用不可プラットフォームの場合スキップ
                continue;
            }

            boolean usable = platformService.isUsablePlatform(userNo, platform);
            if (usable) {
                usablePlatformMap.put(platform.getPlatformNo(), platform);
            }
        }

        return usablePlatformMap;
    }

    /**
     * 使用可能なイメージ番号のリストを取得する
     *
     * @return 使用可能なイメージ番号のリスト
     */
    protected List<Long> getEnabledImageNos() {
        List<Long> imageNos = new ArrayList<Long>();

        List<Image> images = imageDao.readAll();
        for (Image image : images) {
            if (BooleanUtils.isNotTrue(image.getSelectable())) {
                //有効イメージではない場合、ロードバランサーイメージの場合はリストに含めない
                continue;
            }
            imageNos.add(image.getImageNo());
        }

        return imageNos;
    }

    /**
     * 使用可能なコンポーネントタイプ番号のリストを取得する
     *
     * @return 使用可能なコンポーネントタイプ番号のリスト
     */
    protected List<Long> getEnabledComponentTypeNos() {
        List<Long> componentTypeNos = new ArrayList<Long>();

        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType componentType : componentTypes) {
            if (BooleanUtils.isNotTrue(componentType.getSelectable())) {
                //有効コンポーネントタイプではない場合、ロードバランサーイメージの場合はリストに含めない
                continue;
            }
            componentTypeNos.add(componentType.getComponentTypeNo());
        }

        return componentTypeNos;
    }

    /**
     * componentServiceを設定します。
     *
     * @param componentService componentService
     */
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * instanceServiceを設定します。
     *
     * @param instanceService instanceService
     */
    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

}
