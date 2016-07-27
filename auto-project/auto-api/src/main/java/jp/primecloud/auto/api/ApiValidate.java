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
package jp.primecloud.auto.api;

import jp.primecloud.auto.api.util.ValidateUtil;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.StringUtils;

public class ApiValidate extends ApiConstants {

    public static void validateTimestamp(String timestamp) {
        ValidateUtil.required(timestamp, "EAPI-000001", new Object[] { PARAM_NAME_TIMESTAMP });
        ValidateUtil.isDate(timestamp, "EAPI-000005", DATE_FORMAT_YYYYMMDD_HHMMSS,
                new Object[] { PARAM_NAME_TIMESTAMP });
    }

    public static void validateSignature(String signature) {
        ValidateUtil.required(signature, "EAPI-000001", new Object[] { PARAM_NAME_SIGNATURE });
    }

    public static void validateFarmNo(String farmNo) {
        ValidateUtil.required(farmNo, "EAPI-000001", new Object[] { PARAM_NAME_FARM_NO });
        ValidateUtil.longInRange(farmNo, new Long(1), Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_FARM_NO,
                new Long(1), Long.MAX_VALUE });
    }

    public static void validateComponentNo(String componentNo) {
        ValidateUtil.required(componentNo, "EAPI-000001", new Object[] { PARAM_NAME_COMPONENT_NO });
        ValidateUtil.longInRange(componentNo, new Long(1), Long.MAX_VALUE, "EAPI-000002", new Object[] {
                PARAM_NAME_COMPONENT_NO, new Long(1), Long.MAX_VALUE });
    }

    public static void validateComponentName(String componentName) {
        ValidateUtil.required(componentName, "EAPI-000001", new Object[] { PARAM_NAME_COMPONENT_NAME });
        ValidateUtil.lengthInRange(componentName, 1, 15, "EAPI-000003", new Object[] { PARAM_NAME_COMPONENT_NAME, 15 });
        ValidateUtil.matchRegex(componentName, "^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", "EAPI-000004",
                new Object[] { PARAM_NAME_COMPONENT_NAME });
    }

    public static void validateComponentTypeNo(String componentTypeNo) {
        ValidateUtil.required(componentTypeNo, "EAPI-000001", new Object[] { PARAM_NAME_COMPONENT_TYPE_NO });
        ValidateUtil.longInRange(componentTypeNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] {
                PARAM_NAME_COMPONENT_TYPE_NO, 1, Long.MAX_VALUE });
    }

    public static void validateDiskSize(String diskSize) {
        ValidateUtil.required(diskSize, "EAPI-000001", new Object[] { PARAM_NAME_DISK_SIZE });
        ValidateUtil.intInRange(diskSize, 1, 1000, "EAPI-000002", new Object[] { PARAM_NAME_DISK_SIZE, 1, 1000 });
    }

    public static void validateComment(String comment) {
        ValidateUtil.lengthInRange(comment, 0, 100, "EAPI-000003", new Object[] { PARAM_NAME_COMMENT, 100 });
    }

    public static void validateIsStopInstance(String isStopInstance) {
        if (StringUtils.isNotEmpty(isStopInstance)) {
            ValidateUtil.isBoolean(isStopInstance, "EAPI-000009", new Object[] { PARAM_NAME_IS_STOP_INSTANCE });
        }
    }

    public static void validatePlatformNo(String platformNo) {
        ValidateUtil.required(platformNo, "EAPI-000001", new Object[] { PARAM_NAME_PLATFORM_NO });
        ValidateUtil.longInRange(platformNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_PLATFORM_NO,
                1, Long.MAX_VALUE });
    }

    public static void validateInstanceNo(String instanceNo) {
        ValidateUtil.required(instanceNo, "EAPI-000001", new Object[] { PARAM_NAME_INSTANCE_NO });
        ValidateUtil.longInRange(instanceNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_INSTANCE_NO,
                1, Long.MAX_VALUE });
    }

    public static void validateImageNo(String imageNo) {
        ValidateUtil.required(imageNo, "EAPI-000001", new Object[] { PARAM_NAME_IMAGE_NO });
        ValidateUtil.longInRange(imageNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_IMAGE_NO, 1,
                Long.MAX_VALUE });
    }

    public static void validateInstanceName(String instanceName) {
        ValidateUtil.required(instanceName, "EAPI-000001", new Object[] { PARAM_NAME_INSTANCE_NAME });
        ValidateUtil.lengthInRange(instanceName, 1, 15, "EAPI-000003", new Object[] { PARAM_NAME_INSTANCE_NAME, 15 });
        ValidateUtil.matchRegex(instanceName, "^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", "EAPI-000004",
                new Object[] { PARAM_NAME_INSTANCE_NAME });
        if (instanceName.startsWith("lb-")) {
            //「lb-」から始まる名称は使用不可
            throw new AutoApplicationException("EAPI-000014", PARAM_NAME_INSTANCE_NAME, "lb-", instanceName);
        }
    }

    public static void validateInstanceType(String instanceType, boolean isRequired) {
        if (isRequired) {
            ValidateUtil.required(instanceType, "EAPI-000001", new Object[] { PARAM_NAME_INSTANCE_TYPE });
        }
        ValidateUtil.lengthInRange(instanceType, 0, 20, "EAPI-000003", new Object[] { PARAM_NAME_INSTANCE_TYPE, 20 });
    }

    public static void validateIsStartService(String isStartService) {
        if (StringUtils.isNotEmpty(isStartService)) {
            ValidateUtil.isBoolean(isStartService, "EAPI-000009", new Object[] { PARAM_NAME_IS_START_SERVICE });
        }
    }

    public static void validateLoadBalancerNo(String loadBalancerNo) {
        ValidateUtil.required(loadBalancerNo, "EAPI-000001", new Object[] { PARAM_NAME_LOAD_BALANCER_NO });
        ValidateUtil.longInRange(loadBalancerNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] {
                PARAM_NAME_LOAD_BALANCER_NO, 1, Long.MAX_VALUE });
    }

    public static void validateLoadBalancerPort(String loadBalancerPort) {
        ValidateUtil.required(loadBalancerPort, "EAPI-000001", new Object[] { PARAM_NAME_LOAD_BALANCER_PORT });
        ValidateUtil.intInRange(loadBalancerPort, 1, 65535, "EAPI-000002", new Object[] {
                PARAM_NAME_LOAD_BALANCER_PORT, 1, 65535 });
    }

    public static void validateServicePort(String servicePort) {
        ValidateUtil.required(servicePort, "EAPI-000001", new Object[] { PARAM_NAME_SERVICE_PORT });
        ValidateUtil.intInRange(servicePort, 1, 65535, "EAPI-000002",
                new Object[] { PARAM_NAME_SERVICE_PORT, 1, 65535 });
    }

    public static void validateProtocol(String protocol) {
        ValidateUtil.required(protocol, "EAPI-000001", new Object[] { PARAM_NAME_PROTOCOL });
        ValidateUtil.matchRegex(protocol, "HTTP|TCP|HTTPS|SSL", "EAPI-000006", new Object[] { PARAM_NAME_PROTOCOL,
                "HTTP or TCP or HTTPS or SSL" });
    }

    public static void validateUser(String user) {
        ValidateUtil.required(user, "EAPI-000001", new Object[] { PARAM_NAME_USER });
    }

    public static void validatePassword(String password) {
        ValidateUtil.required(password, "EAPI-000001", new Object[] { PARAM_NAME_PASSWORD });
        ValidateUtil.lengthInRange(password, 1, 15, "EAPI-000003", new Object[] { PARAM_NAME_PASSWORD, 15 });
    }

    public static void validateFarmName(String farmName) {
        ValidateUtil.required(farmName, "EAPI-000001", new Object[] { PARAM_NAME_FARM_NAME });
        ValidateUtil.lengthInRange(farmName, 1, 15, "EAPI-000003", new Object[] { PARAM_NAME_FARM_NAME, 15 });
        ValidateUtil.matchRegex(farmName, "^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", "EAPI-000004",
                new Object[] { PARAM_NAME_FARM_NAME });
    }

    public static void validateTemplateNo(String templateNo) {
        ValidateUtil.required(templateNo, "EAPI-000001", new Object[] { PARAM_NAME_TEMPLATE_NO });
        ValidateUtil.longInRange(templateNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_TEMPLATE_NO,
                1, Long.MAX_VALUE });
    }

    public static void validateKeyName(String keyName) {
        ValidateUtil.required(keyName, "EAPI-000001", new Object[] { PARAM_NAME_KEY_NAME });
    }

    public static void validateSecurityGroups(String securityGroups) {
        ValidateUtil.required(securityGroups, "EAPI-000001", new Object[] { PARAM_NAME_SECURITY_GROUPS });
    }

    public static void validateIpAddress(String ipAddress, boolean isRequired) {
        if (isRequired) {
            ValidateUtil.required(ipAddress, "EAPI-000001", new Object[] { PARAM_NAME_IP_ADDRESS });
        }
        if (StringUtils.isNotEmpty(ipAddress)) {
            ValidateUtil
                    .matchRegex(
                            ipAddress,
                            "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                            "EAPI-000013", new Object[] { PARAM_NAME_IP_ADDRESS, });
        }
    }

    public static void validateKeyPairNo(String keyPairNo) {
        ValidateUtil.required(keyPairNo, "EAPI-000001", new Object[] { PARAM_NAME_KEY_PAIR_NO });
        ValidateUtil.longInRange(keyPairNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_KEY_PAIR_NO, 1,
                Long.MAX_VALUE });
    }

    public static void validateComputeResource(String computeResource) {
        ValidateUtil.required(computeResource, "EAPI-000001", new Object[] { PARAM_NAME_COMPUTE_RESOURCE });
    }

    public static void validateIsStaticIp(String isStaticIp) {
        ValidateUtil.required(isStaticIp, "EAPI-000001", new Object[] { PARAM_NAME_IS_STATIC_IP });
        ValidateUtil.isBoolean(isStaticIp, "EAPI-000009", new Object[] { PARAM_NAME_IS_STATIC_IP });
    }

    public static void validateSubnetMask(String subnetMask) {
        ValidateUtil.required(subnetMask, "EAPI-000001", new Object[] { PARAM_NAME_SUBNET_MASK });
        ValidateUtil
                .matchRegex(
                        subnetMask,
                        "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                        "EAPI-000013", new Object[] { PARAM_NAME_SUBNET_MASK, });
    }

    public static void validateDefaultGateway(String defaultGateway) {
        ValidateUtil.required(defaultGateway, "EAPI-000001", new Object[] { PARAM_NAME_DEFAULT_GATEWAY });
        ValidateUtil
                .matchRegex(
                        defaultGateway,
                        "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                        "EAPI-000013", new Object[] { PARAM_NAME_DEFAULT_GATEWAY, });
    }

    public static void validateAvailabilityZone(String availabilityZoneName) {
        ValidateUtil.lengthInRange(availabilityZoneName, 0, 100, "EAPI-000003", new Object[] {
                PARAM_NAME_AVAILABILITY_ZONE, 100 });
    }

    public static void validateLoadBalancerName(String loadBalancerName) {
        ValidateUtil.required(loadBalancerName, "EAPI-000001", new Object[] { PARAM_NAME_LOAD_BALANCER_NAME });
        ValidateUtil.lengthInRange(loadBalancerName, 1, 15, "EAPI-000003", new Object[] {
                PARAM_NAME_LOAD_BALANCER_NAME, 15 });
        ValidateUtil.matchRegex(loadBalancerName, "^[0-9a-z]|[0-9a-z][0-9a-z-]*[0-9a-z]$", "EAPI-000004",
                new Object[] { PARAM_NAME_LOAD_BALANCER_NAME });
        if (loadBalancerName.startsWith("lb-")) {
            //「lb-」から始まる名称は使用不可
            throw new AutoApplicationException("EAPI-000014", PARAM_NAME_LOAD_BALANCER_NAME, "lb-", loadBalancerName);
        }
    }

    public static void validateLoadBalancerType(String loadBalancerType) {
        ValidateUtil.required(loadBalancerType, "EAPI-000001", new Object[] { PARAM_NAME_LOAD_BALANCER_TYPE });
        //        ValidateUtil.machRegex(loadBalancerType, "aws|ultramonkey|cloudstack", "EAPI-000006",
        //                new Object[] {PARAM_NAME_LOAD_BALANCER_TYPE, "aws or ultramonkey or cloudstack"});
        ValidateUtil.matchRegex(loadBalancerType, "aws|ultramonkey", "EAPI-000006", new Object[] {
                PARAM_NAME_LOAD_BALANCER_TYPE, "aws or ultramonkey" });
    }

    public static void validateEnabled(String enabled) {
        ValidateUtil.required(enabled, "EAPI-000001", new Object[] { PARAM_NAME_ENABLED });
        ValidateUtil.isBoolean(enabled, "EAPI-000009", new Object[] { PARAM_NAME_ENABLED });
    }

    public static void validateNamingRule(String namingRule) {
        ValidateUtil.required(namingRule, "EAPI-000001", new Object[] { PARAM_NAME_NAMING_RULE });
        ValidateUtil.matchRegex(namingRule, "^[a-z]|[a-z][0-9a-z-]*[0-9a-z]$", "EAPI-000004",
                new Object[] { PARAM_NAME_NAMING_RULE });
        ValidateUtil.lengthInRange(namingRule, 1, 10, "EAPI-000003", new Object[] { PARAM_NAME_NAMING_RULE, 10 });
    }

    public static void validateIdleTimeMax(String idleTimeMax) {
        ValidateUtil.required(idleTimeMax, "EAPI-000001", new Object[] { PARAM_NAME_IDLE_TIME_MAX });
        ValidateUtil.longInRange(idleTimeMax, 0, 100, "EAPI-000002", new Object[] { PARAM_NAME_IDLE_TIME_MAX, 0, 100 });
    }

    public static void validateIdleTimeMin(String idleTimeMin) {
        ValidateUtil.required(idleTimeMin, "EAPI-000001", new Object[] { PARAM_NAME_IDLE_TIME_MIN });
        ValidateUtil.longInRange(idleTimeMin, 0, 100, "EAPI-000002", new Object[] { PARAM_NAME_IDLE_TIME_MIN, 0, 100 });
    }

    public static void validateContinueLimit(String continueLimit) {
        ValidateUtil.required(continueLimit, "EAPI-000001", new Object[] { PARAM_NAME_CONTINUE_LIMIT });
        ValidateUtil.longInRange(continueLimit, 1, 1000000, "EAPI-000002", new Object[] { PARAM_NAME_CONTINUE_LIMIT, 1,
                1000000 });
    }

    public static void validateAddCount(String addCount) {
        ValidateUtil.required(addCount, "EAPI-000001", new Object[] { PARAM_NAME_ADD_COUNT });
        ValidateUtil.longInRange(addCount, 1, 10, "EAPI-000002", new Object[] { PARAM_NAME_ADD_COUNT, 1, 10 });
    }

    public static void validateDelCount(String delCount) {
        ValidateUtil.required(delCount, "EAPI-000001", new Object[] { PARAM_NAME_DEL_COUNT });
        ValidateUtil.longInRange(delCount, 1, 10, "EAPI-000002", new Object[] { PARAM_NAME_DEL_COUNT, 1, 10 });
    }

    public static void validateCheckProtocol(String checkProtocol) {
        ValidateUtil.required(checkProtocol, "EAPI-000001", new Object[] { PARAM_NAME_CHECK_PROTOCOL });
        ValidateUtil.matchRegex(checkProtocol, "HTTP|TCP", "EAPI-000006", new Object[] { PARAM_NAME_PROTOCOL,
                "HTTP or TCP" });
    }

    public static void validateCheckPort(String checkPort) {
        ValidateUtil.required(checkPort, "EAPI-000001", new Object[] { PARAM_NAME_CHECK_PORT });
        ValidateUtil.intInRange(checkPort, 1, 65535, "EAPI-000002", new Object[] { PARAM_NAME_CHECK_PORT, 1, 65535 });
    }

    public static void validateCheckPath(String checkPath, boolean isRequired) {
        if (isRequired) {
            ValidateUtil.required(checkPath, "EAPI-000001", new Object[] { PARAM_NAME_CHECK_PATH });
        }
        ValidateUtil.lengthInRange(checkPath, 0, 100, "EAPI-000003", new Object[] { PARAM_NAME_CHECK_PATH, 100 });
    }

    public static void validateCheckTimeout(String checkTimeout) {
        ValidateUtil.required(checkTimeout, "EAPI-000001", new Object[] { PARAM_NAME_CHECK_TIMEOUT });
        ValidateUtil.intInRange(checkTimeout, 2, 60, "EAPI-000002", new Object[] { PARAM_NAME_CHECK_TIMEOUT, 2, 60 });
    }

    public static void validateCheckInterval(String checkInterval) {
        ValidateUtil.required(checkInterval, "EAPI-000001", new Object[] { PARAM_NAME_CHECK_INTERVAL });
        ValidateUtil.intInRange(checkInterval, 5, 600, "EAPI-000002",
                new Object[] { PARAM_NAME_CHECK_INTERVAL, 5, 600 });
    }

    public static void validateHealthyThreshold(String healthyThreshold) {
        ValidateUtil.required(healthyThreshold, "EAPI-000001", new Object[] { PARAM_NAME_HEALTHY_THRESHOLD });
        ValidateUtil.intInRange(healthyThreshold, 2, 10, "EAPI-000002", new Object[] { PARAM_NAME_HEALTHY_THRESHOLD, 2,
                10 });
    }

    public static void validateUnhealthyThreshold(String unhealthyThreshold) {
        ValidateUtil.required(unhealthyThreshold, "EAPI-000001", new Object[] { PARAM_NAME_UNHEALTHY_THRESHOLD });
        ValidateUtil.intInRange(unhealthyThreshold, 2, 10, "EAPI-000002", new Object[] {
                PARAM_NAME_UNHEALTHY_THRESHOLD, 2, 10 });
    }

    public static void validateInstanceNos(String instanceNos, boolean isLongInRange) {
        ValidateUtil.required(instanceNos, "EAPI-000001", new Object[] { PARAM_NAME_INSTANCE_NOS });
        if (isLongInRange) {
            ValidateUtil.longInRange(instanceNos, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] {
                    PARAM_NAME_INSTANCE_NOS, 1, Long.MAX_VALUE });
        }
    }

    public static void validateSubnet(String subnet) {
        ValidateUtil.required(subnet, "EAPI-000001", new Object[] { PARAM_NAME_SUBNET });
    }

    public static void validatePrivateIpAddress(String privateIpAddress) {
        if (StringUtils.isNotEmpty(privateIpAddress)) {
            ValidateUtil
                    .matchRegex(
                            privateIpAddress,
                            "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$",
                            "EAPI-000013", new Object[] { PARAM_NAME_PRIVATE_IP });
        }
    }

    public static void validateIsFromCurrent(String isFromCurrent) {
        ValidateUtil.required(isFromCurrent, "EAPI-000001", new Object[] { PARAM_NAME_IS_FROM_CURRENT });
        ValidateUtil.isBoolean(isFromCurrent, "EAPI-000009", new Object[] { PARAM_NAME_IS_FROM_CURRENT });
    }

    public static void validateFromCurrent(String fromCurrent) {
        if (StringUtils.isNotEmpty(fromCurrent)) {
            ValidateUtil.matchRegex(fromCurrent, "10m|30m|1H|1d", "EAPI-000006", new Object[] {
                    PARAM_NAME_FROM_CURRENT, "10m or 30m or 1H or 1d" });
        }
    }

    public static void validateFromDate(String fromDate) {
        ValidateUtil.required(fromDate, "EAPI-000001", new Object[] { PARAM_NAME_FROM_DATE });
        ValidateUtil
                .isDate(fromDate, "EAPI-000005", DATE_FORMAT_YYYYMMDD_HHMMSS, new Object[] { PARAM_NAME_FROM_DATE });
    }

    public static void validateToDate(String toDate) {
        if (StringUtils.isNotEmpty(toDate)) {
            ValidateUtil
                    .isDate(toDate, "EAPI-000005", DATE_FORMAT_YYYYMMDD_HHMMSS, new Object[] { PARAM_NAME_TO_DATE });
        }
    }

    public static void validateLogLevel(String logLevel) {
        if (StringUtils.isNotEmpty(logLevel)) {
            ValidateUtil.matchRegex(logLevel, "ERROR|WARN|INFO|DEBUG", "EAPI-000006", new Object[] {
                    PARAM_NAME_LOG_LEVEL, "ERROR or WARN or INFO or DEBUG" });
        }
    }

    public static void validateOrderName(String orderName) {
        ValidateUtil.required(orderName, "EAPI-000001", new Object[] { PARAM_NAME_ORDER_NAME });
        ValidateUtil.matchRegex(orderName, "Date|LogLevel|FarmName|ComponentName|InstanceName|Message", "EAPI-000006",
                new Object[] { PARAM_NAME_ORDER_NAME,
                        "Date or LogLevel or FarmName or ComponentName or InstanceName or Message" });
    }

    public static void validateOrderAscDesc(String orderAscDesc) {
        ValidateUtil.required(orderAscDesc, "EAPI-000001", new Object[] { PARAM_NAME_ORDER_ASC_DESC });
        ValidateUtil.matchRegex(orderAscDesc, "ASC|DESC", "EAPI-000006", new Object[] { PARAM_NAME_ORDER_ASC_DESC,
                "ASC or DESC" });
    }

    public static void validateAccessId(String accessId) {
        ValidateUtil.required(accessId, "EAPI-000001", new Object[] { PARAM_NAME_ACCESS_ID });
    }

    public static void validateCustomParam1(String customParam1) {
        if (StringUtils.isNotEmpty(customParam1)) {
            ValidateUtil.matchRegex(customParam1, "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", "EAPI-000019",
                    new Object[] { PARAM_NAME_CUSTOM_PARAM_1 });
        }
        ValidateUtil
                .lengthInRange(customParam1, 0, 200, "EAPI-000003", new Object[] { PARAM_NAME_CUSTOM_PARAM_1, 200 });
    }

    public static void validateCustomParam2(String customParam2) {
        if (StringUtils.isNotEmpty(customParam2)) {
            ValidateUtil.matchRegex(customParam2, "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", "EAPI-000019",
                    new Object[] { PARAM_NAME_CUSTOM_PARAM_2 });
        }
        ValidateUtil
                .lengthInRange(customParam2, 0, 200, "EAPI-000003", new Object[] { PARAM_NAME_CUSTOM_PARAM_2, 200 });
    }

    public static void validateCustomParam3(String customParam3) {
        if (StringUtils.isNotEmpty(customParam3)) {
            ValidateUtil.matchRegex(customParam3, "^[0-9a-zA-Z-,._][0-9a-zA-Z-,._ ]*[0-9a-zA-Z-,._]$", "EAPI-000019",
                    new Object[] { PARAM_NAME_CUSTOM_PARAM_3 });
        }
        ValidateUtil
                .lengthInRange(customParam3, 0, 200, "EAPI-000003", new Object[] { PARAM_NAME_CUSTOM_PARAM_3, 200 });
    }

    public static void validateSslKeyNo(String sslKeyNo) {
        ValidateUtil.required(sslKeyNo, "EAPI-000001", new Object[] { PARAM_NAME_SSL_KEY_NO });
        ValidateUtil.longInRange(sslKeyNo, new Long(1), Long.MAX_VALUE, "EAPI-000002", new Object[] {
                PARAM_NAME_SSL_KEY_NO, new Long(1), Long.MAX_VALUE });
    }

    public static void validateStrageType(String storageType) {
        ValidateUtil.required(storageType, "EAPI-000001", new Object[] { PARAM_NAME_STORAGE_TYPE });
    }

    public static void validateIsInternal(String isInternal) {
        ValidateUtil.isBoolean(isInternal, "EAPI-000009", new Object[] { PARAM_NAME_IS_INTERNAL });
    }

    public static void validateAddressNo(String addressNo) {
        ValidateUtil.required(addressNo, "EAPI-000001", new Object[] { PARAM_NAME_ADDRESS_NO });
        ValidateUtil.longInRange(addressNo, 1, Long.MAX_VALUE, "EAPI-000002", new Object[] { PARAM_NAME_ADDRESS_NO, 1,
                Long.MAX_VALUE });
    }

}
