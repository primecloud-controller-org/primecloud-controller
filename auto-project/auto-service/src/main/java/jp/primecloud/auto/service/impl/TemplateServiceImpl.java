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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TemplateDto> getTemplates(Long userNo) {
        List<TemplateDto> dtos = new ArrayList<TemplateDto>();

        //プラットフォーム取得(プラットフォームの認証情報チェックもここで行う)
        Map<Long, Platform> platformMap = getPlatformMap(userNo);

        //イメージ取得
        List<Long> imageNos = getEnabledImageNos();

        //コンポーネントタイプ取得
        List<Long> componentTypeNos = getEnabledComponentTypeNos();

        //テンプレート取得
        List<Template> templates = templateDao.readAll();
        for (Template template : templates) {
            boolean available = true;
            // 全てのインスタンスのプラットフォームを利用できるかどうかのチェック
            Platform platform = null;
            List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(template.getTemplateNo());
            for (TemplateInstance templateInstance: templateInstances) {
                platform = platformMap.get(templateInstance.getPlatformNo());
                if (platform == null) {
                    //認証情報が存在しない or 使用不可プラットフォーム
                    available = false;
                    break;
                }
                if (!imageNos.contains(templateInstance.getImageNo())) {
                    //使用不可イメージ
                    available = false;
                    break;
                }
            }

            if (!available) {
                //インスタンスのプラットフォーム、イメージが使用不可の場合、テンプレート非表示
                continue;
            }

            List<TemplateComponent> templateComponents = templateComponentDao.readByTemplateNo(template.getTemplateNo());
            for (TemplateComponent templateComponent: templateComponents) {
                if (!componentTypeNos.contains(templateComponent.getComponentTypeNo())) {
                    //使用不可コンポーネントタイプ
                    available = false;
                    break;
                }
            }

            if (!available) {
                //インスタンスのプラットフォーム、イメージが使用不可の場合、テンプレート非表示
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
        Map<Long, Platform> platformMap = getPlatformMap(farm.getUserNo());

        // インスタンスを作成
        Map<String, Long> instanceNoMap = new HashMap<String, Long>();

        List<TemplateInstance> templateInstances = templateInstanceDao.readByTemplateNo(templateNo);
        for (TemplateInstance templateInstance: templateInstances) {
            Long instanceNo = null;
            Platform platform = platformMap.get(templateInstance.getPlatformNo());
            if ("aws".equals(platform.getPlatformType()) || "cloudstack".equals(platform.getPlatformType())) {
                //Iaas(AWS or CloudStack)
                instanceNo = instanceService.createIaasInstance(
                        farmNo, templateInstance.getTemplateInstanceName(),templateInstance.getPlatformNo(),
                        templateInstance.getComment(), templateInstance.getImageNo(), templateInstance.getInstanceType());
            } else if ("vmware".equals(platform.getPlatformType())) {
                //VMware
                instanceNo = instanceService.createVmwareInstance(
                        farmNo, templateInstance.getTemplateInstanceName(), templateInstance.getPlatformNo(),
                        templateInstance.getComment(), templateInstance.getImageNo(), templateInstance.getInstanceType());

            } else if ("nifty".equals(platform.getPlatformType())) {
                //Nifty
                instanceNo = instanceService.createNiftyInstance(
                        farmNo, templateInstance.getTemplateInstanceName(),templateInstance.getPlatformNo(),
                        templateInstance.getComment(), templateInstance.getImageNo(), templateInstance.getInstanceType());
            } else {
                continue;
            }
            instanceNoMap.put(templateInstance.getTemplateInstanceName(), instanceNo);
        }

        // コンポーネントを作成
        List<TemplateComponent> templateComponents = templateComponentDao.readByTemplateNo(templateNo);
        for (TemplateComponent templateComponent: templateComponents) {
            Long componentNo = componentService.createComponent(farmNo, templateComponent.getTemplateComponentName(),
                    templateComponent.getComponentTypeNo(), templateComponent.getComment(), templateComponent.getDiskSize());

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
     *
     * プラットフォーム情報をマップで取得
     *
     *
     * @param userNo
     * @return プラットフォーム情報のマップ(認証情報がないプラットフォームはマップに含まれない)
     */
    private Map<Long, Platform> getPlatformMap(Long userNo) {
        // プラットフォームを取得
        Map<Long, Platform> platformMap = new HashMap<Long, Platform>();
        List<Platform> platforms = platformDao.readAll();
        for (Platform platform : platforms) {
            if (BooleanUtils.isNotTrue(platform.getSelectable())) {
                // 使用不可プラットフォームの場合スキップ
                continue;
            }

            if ("aws".equals(platform.getPlatformType())) {
                // 認証情報がない場合はスキップ
                if (awsCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
            } else if ("vmware".equals(platform.getPlatformType())) {
                // キーペアがない場合はスキップ
                if (vmwareKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
            } else if ("nifty".equals(platform.getPlatformType())) {
                // 認証情報とキーペアがない場合はスキップ
                if (niftyCertificateDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
                if (niftyKeyPairDao.countByUserNoAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
            } else if ("cloudstack".equals(platform.getPlatformType())) {
                // 認証情報がない場合はスキップ
                if (cloudstackCertificateDao.countByAccountAndPlatformNo(userNo, platform.getPlatformNo()) == 0) {
                    continue;
                }
            }

            platformMap.put(platform.getPlatformNo(), platform);
        }

        return platformMap;
    }

    /**
     *
     * 使用可能なイメージ番号のリストを取得する
     *
     * @return 使用可能なイメージ番号のリスト
     */
    private List<Long> getEnabledImageNos() {
        List<Long> imageNos = new ArrayList<Long>();
        List<Image> images = imageDao.readAll();
        for (Image image: images) {
            if (BooleanUtils.isNotTrue(image.getSelectable())) {
                //有効イメージではない場合、ロードバランサーイメージの場合はリストに含めない
                continue;
            }
            imageNos.add(image.getImageNo());
        }
        return imageNos;
    }

    /**
     *
     * 使用可能なコンポーネントタイプ番号のリストを取得する
     *
     * @return 使用可能なコンポーネントタイプ番号のリスト
     */
    private List<Long> getEnabledComponentTypeNos() {
        List<Long> componentTypeNos = new ArrayList<Long>();
        List<ComponentType> componentTypes = componentTypeDao.readAll();
        for (ComponentType componentType: componentTypes) {
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
}
