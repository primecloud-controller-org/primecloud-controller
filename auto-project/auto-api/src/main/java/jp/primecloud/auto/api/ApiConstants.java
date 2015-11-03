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


public class ApiConstants {

    //PARAM_NAME
    protected static final String PARAM_NAME_TIMESTAMP = "Timestamp";
    protected static final String PARAM_NAME_SIGNATURE = "Signature";
    protected static final String PARAM_NAME_FARM_NO = "FarmNo";
    protected static final String PARAM_NAME_COMPONENT_NO = "ComponentNo";
    protected static final String PARAM_NAME_COMPONENT_NAME = "ComponentName";
    protected static final String PARAM_NAME_COMPONENT_TYPE_NO = "ComponentTypeNo";
    protected static final String PARAM_NAME_DISK_SIZE = "DiskSize";
    protected static final String PARAM_NAME_COMMENT = "Comment";
    protected static final String PARAM_NAME_PLATFORM_NO = "PlatformNo";
    protected static final String PARAM_NAME_IMAGE_NO = "ImageNo";
    protected static final String PARAM_NAME_INSTANCE_NO = "InstanceNo";
    protected static final String PARAM_NAME_INSTANCE_NAME = "InstanceName";
    protected static final String PARAM_NAME_INSTANCE_TYPE = "InstanceType";
    protected static final String PARAM_NAME_LOAD_BALANCER_PORT = "LoadBalancerPort";
    protected static final String PARAM_NAME_LOAD_BALANCER_NO = "LoadBalancerNo";
    protected static final String PARAM_NAME_SERVICE_PORT = "ServicePort";
    protected static final String PARAM_NAME_PROTOCOL = "Protocol";
    protected static final String PARAM_NAME_IS_STOP_INSTANCE = "IsStopInstance";
    protected static final String PARAM_NAME_IS_START_SERVICE = "IsStartService";
    protected static final String PARAM_NAME_USER = "User";
    protected static final String PARAM_NAME_PASSWORD = "Password";
    protected static final String PARAM_NAME_FARM_NAME = "FarmName";
    protected static final String PARAM_NAME_TEMPLATE_NO = "TemplateNo";
    protected static final String PARAM_NAME_KEY_NAME = "KeyName";
    protected static final String PARAM_NAME_SECURITY_GROUPS = "SecurityGroups";
    protected static final String PARAM_NAME_IP_ADDRESS = "IpAddress";
    protected static final String PARAM_NAME_AVAILABILITY_ZONE = "AvailabilityZone";
    protected static final String PARAM_NAME_LOAD_BALANCER_NAME = "LoadBalancerName";
    protected static final String PARAM_NAME_LOAD_BALANCER_TYPE = "LoadBalancerType";
    protected static final String PARAM_NAME_ENABLED = "Enabled";
    protected static final String PARAM_NAME_NAMING_RULE = "NamingRule";
    protected static final String PARAM_NAME_IDLE_TIME_MAX = "IdleTimeMax";
    protected static final String PARAM_NAME_IDLE_TIME_MIN = "IdleTimeMin";
    protected static final String PARAM_NAME_CONTINUE_LIMIT = "ContinueLimit";
    protected static final String PARAM_NAME_ADD_COUNT = "AddCount";
    protected static final String PARAM_NAME_DEL_COUNT = "DelCount";
    protected static final String PARAM_NAME_CHECK_PROTOCOL = "CheckProtocol";
    protected static final String PARAM_NAME_CHECK_PORT = "CheckPort";
    protected static final String PARAM_NAME_CHECK_PATH = "CheckPath";
    protected static final String PARAM_NAME_CHECK_TIMEOUT = "CheckTimeout";
    protected static final String PARAM_NAME_CHECK_INTERVAL = "CheckInterval";
    protected static final String PARAM_NAME_HEALTHY_THRESHOLD = "HealthyThreshold";
    protected static final String PARAM_NAME_UNHEALTHY_THRESHOLD = "UnhealthyThreshold";
    protected static final String PARAM_NAME_KEY_PAIR_NO = "KeyPairNo";
    protected static final String PARAM_NAME_COMPUTE_RESOURCE = "ComputeResource";
    protected static final String PARAM_NAME_IS_STATIC_IP = "IsStaticIp";
    protected static final String PARAM_NAME_SUBNET_MASK = "SubnetMask";
    protected static final String PARAM_NAME_DEFAULT_GATEWAY = "DefaultGateway";
    protected static final String PARAM_NAME_INSTANCE_NOS = "InstanceNos";
    protected static final String PARAM_NAME_SUBNET = "Subnet";
    protected static final String PARAM_NAME_PRIVATE_IP = "PrivateIpAddress";
    protected static final String PARAM_NAME_IS_FROM_CURRENT = "IsFromCurrent";
    protected static final String PARAM_NAME_FROM_CURRENT = "FromCurrent";
    protected static final String PARAM_NAME_FROM_DATE = "FromDate";
    protected static final String PARAM_NAME_TO_DATE = "ToDate";
    protected static final String PARAM_NAME_LOG_LEVEL = "LogLevel";
    protected static final String PARAM_NAME_ORDER_NAME = "OrderName";
    protected static final String PARAM_NAME_ORDER_ASC_DESC = "OrderAscDesc";
    protected static final String PARAM_NAME_ACCESS_ID = "AccessId";
    protected static final String PARAM_NAME_CUSTOM_PARAM_1 = "CustomParam1";
    protected static final String PARAM_NAME_CUSTOM_PARAM_2 = "CustomParam2";
    protected static final String PARAM_NAME_CUSTOM_PARAM_3 = "CustomParam3";
    protected static final String PARAM_NAME_SSL_KEY_NO = "SslKeyNo";
    protected static final String PARAM_NAME_STORAGE_TYPE = "StorageType";
    protected static final String PARAM_NAME_IS_INTERNAL = "IsInternal";

    //PLATFORM_TYPE
    protected static final String PLATFORM_TYPE_AWS = "aws";
    protected static final String PLATFORM_TYPE_VMWARE = "vmware";
    protected static final String PLATFORM_TYPE_NIFTY = "nifty";
    protected static final String PLATFORM_TYPE_CLOUDSTACK = "cloudstack";
    protected static final String PLATFORM_TYPE_VCLOUD = "vcloud";
    protected static final String PLATFORM_TYPE_AZURE = "azure";
    protected static final String PLATFORM_TYPE_OPENSTACK = "openstack";

    //LOAD_BALANCER_TYPE
    protected static final String LB_TYPE_ELB = "aws";
    protected static final String LB_TYPE_ULTRA_MONKEY = "ultramonkey";
    protected static final String LB_TYPE_CLOUDSTACK = "cloudstack";

    //DATE_FORMAT
    protected final static String DATE_FORMAT_YYYYMMDD_HHMMSS = "yyyy/MM/dd HH:mm:ss";

    //PATTERN(Patten match regex)
    protected final static String PATTERN_IP_ADDRESS =
        "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$";
    protected final static String PATTERN_LB_TYPE = "aws|ultramonkey";

    //PCC-API CERTIFICATE
    protected final static int ACCESS_ID_LENGTH = 30;
    protected final static int SECRET_KEY_LENGTH = 100;
}