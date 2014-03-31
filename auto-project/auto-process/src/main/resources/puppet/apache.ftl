<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "webserver::apache":
        apache_service_name => "${component.componentName}",
      <#if awsVolume ??>
        apache_vgdisk       => "${awsVolume.device}",
        apache_visor        => "aws",
      <#elseif cloudstackVolume ??>
        apache_vgdisk       => "${cloudstackVolume.deviceid}",
        apache_visor          => "${cloudstackVolume.hypervisor}",
      <#elseif vmwareDisk ??>
        apache_vgdisk       => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        apache_visor          => "vmware",
      <#else>
        apache_vgdisk       => "",
        apache_visor        => "",
      </#if>
      <#if (unDetachVolume == true)>
        apache_unmount        => "false",
      </#if>
      <#if (customParam1 ??)>
        apache_custom_param_1 => "${customParam1}",
      <#else>
        apache_custom_param_1 => "",
      </#if>
      <#if (customParam2 ??)>
        apache_custom_param_2 => "${customParam2}",
      <#else>
        apache_custom_param_2 => "",
      </#if>
      <#if (customParam3 ??)>
        apache_custom_param_3 => "${customParam3}",
      <#else>
        apache_custom_param_3 => "",
      </#if>
    }

<#if (start == true)>
    class { "webserver::apache::mount": }
    class { "webserver::apache::resource": require => Class[ "Webserver::Apache::Mount" ] }
<#else>
    class { "webserver::apache::stop": }
    class { "webserver::apache::unmount": require => Class[ "Webserver::Apache::Stop" ] }
</#if>

}
