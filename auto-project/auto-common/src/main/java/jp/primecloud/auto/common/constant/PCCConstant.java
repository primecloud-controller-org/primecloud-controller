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
package jp.primecloud.auto.common.constant;

/**
 * <p>
 * PCCで利用する各種クラウドに設定されている定数を定義するクラス
 * </p>
 *
 */
public class PCCConstant {

    /** OS識別 Windows  */
    public static final String OS_NAME_WIN = "windows";
    /** OS識別 CENTOS  */
    public static final String OS_NAME_CENTOS = "centos";
    /** OS識別 REDHAT  */
    public static final String OS_NAME_REDHAT = "redhat";
    /** OS識別 Ubuntu  */
    public static final String OS_NAME_UBUNTU = "ubuntu";

    /** PLATFORM識別 AWS  */
    public static final String PLATFORM_TYPE_AWS = "aws";
    /** PLATFORM識別 VMWARE  */
    public static final String PLATFORM_TYPE_VMWARE = "vmware";
    /** PLATFORM識別 NIFTY  */
    public static final String PLATFORM_TYPE_NIFTY = "nifty";
    /** PLATFORM識別 CLOUDSTACK  */
    public static final String PLATFORM_TYPE_CLOUDSTACK = "cloudstack";
    /** PLATFORM識別 VCLOUD  */
    public static final String PLATFORM_TYPE_VCLOUD = "vcloud";
    /** PLATFORM識別 AZURE  */
    public static final String PLATFORM_TYPE_AZURE = "azure";
    /** PLATFORM識別 OPENSTACK  */
    public static final String PLATFORM_TYPE_OPENSTACK = "openstack";

    /** MAGE識別 ultramonkey  */
    public static final String IMAGE_NAME_ULTRAMONKEY = "ultramonkey";
    /** MAGE識別 application  */
    public static final String IMAGE_NAME_APPLICATION = "application";
    /** MAGE識別 prjserver  */
    public static final String IMAGE_NAME_PRJSERVER = "prjserver";
    /** MAGE識別 windows  */
    public static final String IMAGE_NAME_WINDOWS = "windows";
    /** MAGE識別 aws（ELB）  */
    public static final String IMAGE_NAME_ELB = "aws";


    /** LB種別識別 ultramonkey  */
    public static final String LOAD_BALANCER_ULTRAMONKEY = "ultramonkey";
    /** LB種別識別 aws（ELB）  */
    public static final String LOAD_BALANCER_ELB = "aws";
    /** LB種別識別 cloudstack  */
    public static final String LOAD_BALANCER_CLOUDSTACK = "cloudstack";

    /** NIFTYCLIENT識別 server  */
    public static final String NIFTYCLIENT_TYPE_SERVER = "server";
    /** NIFTYCLIENT識別 disk  */
    public static final String NIFTYCLIENT_TYPE_DISK = "disk";


}
