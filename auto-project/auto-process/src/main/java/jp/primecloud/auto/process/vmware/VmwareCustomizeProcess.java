package jp.primecloud.auto.process.vmware;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.VmwareInstance;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.vmware.VmwareClient;

import org.apache.commons.lang.StringUtils;

import com.vmware.vim25.CustomizationAdapterMapping;
import com.vmware.vim25.CustomizationDhcpIpGenerator;
import com.vmware.vim25.CustomizationFixedName;
import com.vmware.vim25.CustomizationGlobalIPSettings;
import com.vmware.vim25.CustomizationGuiUnattended;
import com.vmware.vim25.CustomizationIPSettings;
import com.vmware.vim25.CustomizationIdentification;
import com.vmware.vim25.CustomizationLicenseDataMode;
import com.vmware.vim25.CustomizationLicenseFilePrintData;
import com.vmware.vim25.CustomizationPassword;
import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.CustomizationSysprep;
import com.vmware.vim25.CustomizationSysprepRebootOption;
import com.vmware.vim25.CustomizationUserData;
import com.vmware.vim25.CustomizationWinOptions;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.VirtualMachine;

public class VmwareCustomizeProcess extends ServiceSupport {

    protected String windowsFullName = StringUtils.defaultIfEmpty(Config.getProperty("vmware.windowsFullName"), "PCC");

    protected String windowsOrgName = StringUtils.defaultIfEmpty(Config.getProperty("vmware.windowsOrgName"), "PCC");

    protected String windowsWorkgroup = StringUtils.defaultIfEmpty(Config.getProperty("vmware.windowsWorkgroup"),
            "WORKGROUP");

    public void customize(VmwareProcessClient vmwareProcessClient, Long instanceNo) {
        VmwareInstance vmwareInstance = vmwareInstanceDao.read(instanceNo);
        VmwareClient vmwareClient = vmwareProcessClient.getVmwareClient();

        // VirtualMachine
        VirtualMachine machine = vmwareClient.search(VirtualMachine.class, vmwareInstance.getMachineName());
        if (machine == null) {
            // 仮想マシンが見つからない場合
            throw new AutoException("EPROCESS-000501", vmwareInstance.getMachineName());
        }

        // Windowsで始まるOSはカスタマイズする
        Instance instance = instanceDao.read(instanceNo);
        Image image = imageDao.read(instance.getImageNo());
        if (StringUtils.startsWith(image.getOs(), "windows")) {
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100464", vmwareInstance.getMachineName()));
            }
            customizeWindows(vmwareProcessClient, vmwareInstance, machine);

            vmwareProcessClient.powerOnVM(vmwareInstance.getMachineName());

            vmwareProcessClient.waitForStopped(vmwareInstance.getMachineName());

            vmwareProcessClient.powerOnVM(vmwareInstance.getMachineName());

            vmwareProcessClient.waitForRunning(vmwareInstance.getMachineName());

            vmwareProcessClient.shutdownGuest(vmwareInstance.getMachineName());

            if (log.isInfoEnabled()) {
                log.info(MessageUtils.getMessage("IPROCESS-100465", vmwareInstance.getMachineName()));
            }
        }

    }

    protected void customizeWindows(VmwareProcessClient vmwareProcessClient, VmwareInstance vmwareInstance,
            VirtualMachine machine) {
        CustomizationSpec customSpec = new CustomizationSpec();

        // Windows設定
        CustomizationSysprep identity = new CustomizationSysprep();

        CustomizationGuiUnattended guiUnattended = new CustomizationGuiUnattended();
        guiUnattended.setAutoLogon(false);
        guiUnattended.setAutoLogonCount(1);
        guiUnattended.setTimeZone(235);
        CustomizationPassword password = new CustomizationPassword();
        password.setPlainText(true);

        // Adminパスワードのデフォルトはユーザのパスワードをセットする
        Instance instance = instanceDao.read(vmwareInstance.getInstanceNo());
        Farm farm = farmDao.read(instance.getFarmNo());
        User user = userDao.read(farm.getUserNo());
        PccSystemInfo pccSystemInfo= pccSystemInfoDao.read();
        if(pccSystemInfo == null) {
            // PCC_SYSTEM_INFOのレコードが存在しない場合
            log.error(MessageUtils.getMessage("EPROCESS-000532"));
            throw new AutoException("EPROCESS-000532");
        }
        PasswordEncryptor encryptor = new PasswordEncryptor();
        String decryptPassword = encryptor.decrypt(user.getPassword(), pccSystemInfo.getSecretKey());
        password.setValue(decryptPassword);
        guiUnattended.setPassword(password);
        identity.setGuiUnattended(guiUnattended);

        CustomizationUserData userData = new CustomizationUserData();
        userData.setProductId("");
        userData.setFullName(windowsFullName);
        userData.setOrgName(windowsOrgName);
        CustomizationFixedName computerName = new CustomizationFixedName();
        computerName.setName(instance.getInstanceName());
        userData.setComputerName(computerName);
        identity.setUserData(userData);

        CustomizationIdentification identification = new CustomizationIdentification();
        identification.setJoinWorkgroup(windowsWorkgroup);
        identity.setIdentification(identification);

        // Windows Server 2000, 2003のみ必要
        CustomizationLicenseFilePrintData printData = new CustomizationLicenseFilePrintData();
        printData.setAutoMode(CustomizationLicenseDataMode.perSeat);
        identity.setLicenseFilePrintData(printData);

        customSpec.setIdentity(identity);

        // Windowsオプション設定
        CustomizationWinOptions options = new CustomizationWinOptions();
        options.setChangeSID(true);
        options.setDeleteAccounts(false);
        options.setReboot(CustomizationSysprepRebootOption.shutdown);
        customSpec.setOptions(options);

        // グローバル設定
        CustomizationGlobalIPSettings globalIpSettings = new CustomizationGlobalIPSettings();

        // DNSサーバ設定
        List<String> dnsServerList = new ArrayList<String>();
        // Primary DNSサーバ
        dnsServerList.add(Config.getProperty("dns.server"));
        // Secondry DNSサーバ
        String dns2 = Config.getProperty("dns.server2");
        if (dns2 != null && dns2.length() > 0) {
            dnsServerList.add(Config.getProperty("dns.server2"));
        }

        globalIpSettings.setDnsServerList(dnsServerList.toArray(new String[dnsServerList.size()]));
        List<String> dnsSuffixList = new ArrayList<String>();
        dnsSuffixList.add(farm.getDomainName());
        globalIpSettings.setDnsSuffixList(dnsSuffixList.toArray(new String[dnsSuffixList.size()]));
        customSpec.setGlobalIPSettings(globalIpSettings);

        // NIC設定
        List<CustomizationAdapterMapping> nicSettingMap = createCustomizationAdapterMappings(vmwareProcessClient,
                machine);
        customSpec.setNicSettingMap(nicSettingMap.toArray(new CustomizationAdapterMapping[nicSettingMap.size()]));

        vmwareProcessClient.customize(vmwareInstance.getMachineName(), customSpec);

    }

    protected List<CustomizationAdapterMapping> createCustomizationAdapterMappings(
            VmwareProcessClient vmwareProcessClient, VirtualMachine machine) {

        List<CustomizationAdapterMapping> nicSettingMap = new ArrayList<CustomizationAdapterMapping>();

        // VirtualEthernetCardを取得
        VirtualMachineConfigInfo configInfo = machine.getConfig();
        for (VirtualDevice device : configInfo.getHardware().getDevice()) {
            if (device instanceof VirtualEthernetCard) {
                VirtualEthernetCard virtualEthernetCard = VirtualEthernetCard.class.cast(device);
                CustomizationAdapterMapping mapping = new CustomizationAdapterMapping();
                CustomizationIPSettings settings = new CustomizationIPSettings();

                // すべてのNICをDHCPにする
                CustomizationDhcpIpGenerator dhcpIp = new CustomizationDhcpIpGenerator();
                settings.setIp(dhcpIp);

                mapping.setMacAddress(virtualEthernetCard.getMacAddress());
                mapping.setAdapter(settings);
                nicSettingMap.add(mapping);
            }
        }

        return nicSettingMap;
    }

}
