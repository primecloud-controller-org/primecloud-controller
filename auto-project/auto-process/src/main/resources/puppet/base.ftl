<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_base {

<#if (start == true)>
        class {"basenode":
            zabbix_server      => "${zabbixServer}",
            rsyslog_log_server => "${rsyslogServer}",
            puppet_ssh_user    => "puppetmaster",
            puppet_ssh_pass    => "${puppetInstance.password}",
            ssh_usr_username   => "${user.username}",
            ssh_usr_password   => "${user.password}"
        }

    if <#noparse>"${operatingsystem}"</#noparse> == "CentOS" { include basenode::resource }
    elsif <#noparse>"${operatingsystem}"</#noparse> == "windows" { include basenode::resource-windows }

  <#if awsVolumes ??>
  <#list awsVolumes as awsVolume>
  <#if !(awsVolume.componentNo ??)>
    lvm::config { "/mnt/base":
        lvname  => "LVbase01",
        lvsize  => "",
        vgname  => "LGbase01",
        vgdisk  => "${awsVolume.device}",
        visor   => "aws",
    }
  <#break>
  </#if>
  </#list>
  <#elseif cloudstackVolumes ??>
  <#list cloudstackVolumes as cloudstackVolume>
  <#if !(cloudstackVolume.componentNo ??)>
    lvm::config { "/mnt/base":
        lvname  => "LVbase01",
        lvsize  => "",
        vgname  => "LGbase01",
        vgdisk  => "${cloudstackVolume.deviceid}",
        visor   => "${cloudstackVolume.hypervisor}",
    }
  <#break>
  </#if>
  </#list>
  <#elseif vmwareDisks ??>
  <#list vmwareDisks as vmwareDisk>
  <#if !(vmwareDisk.componentNo ??)>
    lvm::config { "/mnt/base":
        lvname  => "LVbase01",
        lvsize  => "",
        vgname  => "LGbase01",
        vgdisk  => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0"",
        visor   => "vmware",
    }
  <#break>
  </#if>
  </#list>
  </#if>
<#else>
    if <#noparse>"${operatingsystem}"</#noparse> == "CentOS" { include basenode::stop }
    elsif <#noparse>"${operatingsystem}"</#noparse> == "windows" { include basenode::stop-windows }

  <#if awsVolumes ??>
  <#list awsVolumes as awsVolume>
  <#if !(awsVolume.componentNo ??)>
    lvm::detach{ "/mnt/base":
        vgname => "LGbase01",
        visor  => "aws",
    }
  <#break>
  </#if>
  </#list>
  <#elseif cloudstackVolumes ??>
  <#list cloudstackVolumes as cloudstackVolume>
  <#if !(cloudstackVolume.componentNo ??)>
    lvm::detach{ "/mnt/base":
        vgname => "LGbase01",
        visor  => "${cloudstackVolume.hypervisor}",
    }
  <#break>
  </#if>
  </#list>
  <#elseif vmwareDisks ??>
  <#list vmwareDisks as vmwareDisk>
  <#if !(vmwareDisk.componentNo ??)>
    lvm::detach{ "/mnt/base":
        vgname => "LGbase01",
        visor  => "vmware",
    }
  <#break>
  </#if>
  </#list>
  </#if>
</#if>

}
