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
package jp.primecloud.auto.process.vmware;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.PlatformVmware;
import jp.primecloud.auto.entity.crud.VmwareAddress;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.entity.crud.VmwareKeyPair;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.process.ProcessLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.vmware.vim25.DistributedVirtualPortgroupInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareInitProcess extends ServiceSupport {

    protected EventLogger eventLogger;

    protected ProcessLogger processLogger;

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     */
    public void initialize(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // UserDataの作成
        String userData = createUserData(instanceNo);

        // ExtraConfig
        Map<String, Object> extraConfigs = new LinkedHashMap<String, Object>();
        extraConfigs.put("guestinfo.userdata", userData);

        // インスタンス情報の設定
        vmwareProcessClient.setExtraConfigVM(vmwareInstance.getMachineName(), extraConfigs);
    }

    protected String createUserData(Long instanceNo) {
        Map<String, String> map = createUserDataMap(instanceNo);
        return convertMapToString(map);
    }

    protected String convertMapToString(Map<String, String> map) {
        if (map.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                sb.append(key).append("=").append(value).append(";");
            }
        }
        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    protected Map<String, String> createUserDataMap(Long instanceNo) {
        Instance instance = instanceDao.read(instanceNo);
        Farm farm = farmDao.read(instance.getFarmNo());

        Map<String, String> map = new HashMap<String, String>();

        // DB情報
        map.put("instanceName", instance.getInstanceName());
        map.put("farmName", farm.getFarmName());

        // FQDN
        String fqdn = instance.getFqdn();
        map.put("hostname", fqdn);

        // 初期スクリプト情報
        map.put("scriptserver", Config.getProperty("script.server"));

        // rsyslogサーバ情報
        map.put("rsyslogserver", Config.getProperty("rsyslog.server"));

        // DNS情報
        map.putAll(createDnsUserDataMap(instanceNo));

        // Puppet情報
        map.putAll(createPuppetUserDataMap(instanceNo));

        // VPN情報
        Platform platform = platformDao.read(instance.getPlatformNo());
        if (platform.getInternal() == false) {
            // 外部のプラットフォームの場合、VPN情報を含める
            map.putAll(createVpnUserDataMap(instanceNo));
        }

        // VMware情報
        if (PCCConstant.PLATFORM_TYPE_VMWARE.equals(platform.getPlatformType())) {
            map.putAll(createVmwareUserDataMap(instanceNo));
        }

        return map;
    }

    protected Map<String, String> createDnsUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // Primary DNSサーバ
        map.put("dns", Config.getProperty("dns.server"));

        // Secondry DNSサーバ
        String dns2 = Config.getProperty("dns.server2");
        if (dns2 != null && dns2.length() > 0) {
            map.put("dns2", dns2);
        }

        // DNSドメイン
        map.put("dnsdomain", Config.getProperty("dns.domain"));

        return map;
    }

    protected Map<String, String> createPuppetUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // PuppetMaster情報
        map.put("puppetmaster", Config.getProperty("puppet.masterHost"));

        return map;
    }

    protected Map<String, String> createVpnUserDataMap(Long instanceNo) {
        Map<String, String> map = new HashMap<String, String>();

        // VPN情報のユーザとパスワードをセットする
        Instance instance = instanceDao.read(instanceNo);
        map.put("vpnuser", instance.getFqdn());
        map.put("vpnuserpass", instance.getInstanceCode());

        // VPNサーバ情報
        map.put("vpnserver", Config.getProperty("vpn.server"));
        map.put("vpnport", Config.getProperty("vpn.port"));
        //map.put("vpnuser", Config.getProperty("vpn.user"));
        //map.put("vpnuserpass", Config.getProperty("vpn.userpass"));

        // ZIPパスワード
        map.put("vpnzippass", Config.getProperty("vpn.zippass"));

        // OpenVPNクライアント証明書ダウンロード先URL
        map.put("vpnclienturl", Config.getProperty("vpn.clienturl"));

        return map;
    }

    protected Map<String, String> createVmwareUserDataMap(Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
        VmwareKeyPair vmwareKeyPair = vmwareKeyPairDao.read(vmwareInstance.getKeyPairNo());

        Map<String, String> map = new HashMap<String, String>();

        // 公開鍵
        map.put("sshpubkey", vmwareKeyPair.getKeyPublic());

        return map;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param vmwareProcessClient
     * @param instanceNo
     * @param vmwareNetworkProcess
     */
    public void initializeNetwork(VmwareProcessClient vmwareProcessClient, Long instanceNo,
            VmwareNetworkProcess vmwareNetworkProcess) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);

        // VirtualMachine
        VirtualMachine machine = vmwareProcessClient.getVirtualMachine(vmwareInstance.getMachineName());

        // ExtraConfig
        Map<String, Object> extraConfigs = new LinkedHashMap<String, Object>();

        // パブリックネットワーク名の取得
        PlatformVmware platformVmware = platformVmwareDao.read(vmwareProcessClient.getPlatformNo());
        String publicNetworkName = platformVmware.getPublicNetwork();

        // パブリックネットワークに相応する分散ポートグループの取得
        String publicPortGroup = null;
        if (StringUtils.isNotEmpty(publicNetworkName)) {
            DistributedVirtualPortgroupInfo dvPortgroupInfo = vmwareNetworkProcess.getDVPortgroupInfo(machine,
                    publicNetworkName);
            if (dvPortgroupInfo != null) {
                publicPortGroup = dvPortgroupInfo.getPortgroupKey();
            }
        }

        boolean configPublic = false;

        int i = 1;
        for (VirtualDevice device : machine.getConfig().getHardware().getDevice()) {
            if (device instanceof VirtualEthernetCard) {
                VirtualEthernetCard ethernetCard = (VirtualEthernetCard) device;

                // ネットワーク名の取得
                String networkName = null;
                if (ethernetCard.getBacking() instanceof VirtualEthernetCardNetworkBackingInfo) {
                    VirtualEthernetCardNetworkBackingInfo backingInfo = (VirtualEthernetCardNetworkBackingInfo) ethernetCard
                            .getBacking();
                    networkName = backingInfo.getDeviceName();
                }

                // ポートグループ名の取得
                String portGroup = null;
                if (ethernetCard.getBacking() instanceof VirtualEthernetCardDistributedVirtualPortBackingInfo) {
                    VirtualEthernetCardDistributedVirtualPortBackingInfo backingInfo2 = (VirtualEthernetCardDistributedVirtualPortBackingInfo) ethernetCard
                            .getBacking();
                    portGroup = backingInfo2.getPort().getPortgroupKey();
                }

                Map<String, String> map;
                if ((StringUtils.isNotEmpty(networkName) && StringUtils.equals(networkName, publicNetworkName))
                        || (StringUtils.isNotEmpty(portGroup) && StringUtils.equals(portGroup, publicPortGroup))) {
                    // パブリックのイーサネットカードの場合
                    map = createPublicNetworkData(instanceNo, ethernetCard);
                    configPublic = true;
                } else {
                    // パブリックのイーサネットカードでない場合は動的IP
                    map = createDhcpNetworkData(ethernetCard);
                }

                String networkData = convertMapToString(map);
                extraConfigs.put("guestinfo.network" + i, networkData);
                i++;
            }
        }

        // ネットワーク情報の設定
        vmwareProcessClient.setExtraConfigVM(vmwareInstance.getMachineName(), extraConfigs);

        // データベース更新、ログ出力
        if (configPublic) {
            VmwareAddress vmwareAddress = vmwareAddressDao.readByInstanceNo(instanceNo);
            if (vmwareAddress != null) {
                if (BooleanUtils.isTrue(vmwareAddress.getEnabled())) {
                    // イベントログ出力
                    Instance instance = instanceDao.read(instanceNo);
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                            "VmwareNetworkCustomizeStatic", new Object[] { vmwareInstance.getMachineName(),
                                    vmwareAddress.getIpAddress() });

                    if (BooleanUtils.isNotTrue(vmwareAddress.getAssociated())) {
                        // データベース更新
                        vmwareAddress.setAssociated(true);
                        vmwareAddressDao.update(vmwareAddress);
                    }

                    // ログ出力
                    if (log.isInfoEnabled()) {
                        log.info(MessageUtils.getMessage("IPROCESS-100445", vmwareInstance.getMachineName(),
                                vmwareAddress.getIpAddress()));
                    }
                } else {
                    // イベントログ出力
                    Instance instance = instanceDao.read(instanceNo);
                    processLogger.writeLogSupport(ProcessLogger.LOG_DEBUG, null, instance,
                            "VmwareNetworkCustomizeDhcp", new Object[] { vmwareInstance.getMachineName() });

                    if (BooleanUtils.isTrue(vmwareAddress.getAssociated())) {
                        // データベース更新
                        vmwareAddress.setAssociated(false);
                        vmwareAddressDao.update(vmwareAddress);
                    }

                    // ログ出力
                    if (log.isInfoEnabled()) {
                        log.info(MessageUtils.getMessage("IPROCESS-100446", vmwareInstance.getMachineName()));
                    }
                }
            }
        }
    }

    protected Map<String, String> createPublicNetworkData(Long instanceNo, VirtualEthernetCard ethernetCard) {
        VmwareAddress vmwareAddress = vmwareAddressDao.readByInstanceNo(instanceNo);
        if (vmwareAddress == null) {
            // VmwareAddressがない場合は動的IP
            return createDhcpNetworkData(ethernetCard);
        }

        if (BooleanUtils.isTrue(vmwareAddress.getEnabled())) {
            // VmwareAddressが有効な場合は静的IP
            return createStaticNetworkData(ethernetCard, vmwareAddress);
        } else {
            // VmwareAddressが無効な場合は動的IP
            return createDhcpNetworkData(ethernetCard);
        }
    }

    protected Map<String, String> createStaticNetworkData(VirtualEthernetCard ethernetCard, VmwareAddress vmwareAddress) {
        Map<String, String> map = new HashMap<String, String>();
        String macAddress = ethernetCard.getMacAddress().toUpperCase();
        map.put("BootProto", "static");
        map.put("Mac", macAddress);
        map.put("IP", vmwareAddress.getIpAddress());
        map.put("Netmask", vmwareAddress.getSubnetMask());
        map.put("Gateway", vmwareAddress.getDefaultGateway());
        return map;
    }

    protected Map<String, String> createDhcpNetworkData(VirtualEthernetCard ethernetCard) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("BootProto", "dhcp");
        map.put("Mac", ethernetCard.getMacAddress().toUpperCase());
        return map;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * processLoggerを設定します。
     *
     * @param processLogger processLogger
     */
    public void setProcessLogger(ProcessLogger processLogger) {
        this.processLogger = processLogger;
    }
}
