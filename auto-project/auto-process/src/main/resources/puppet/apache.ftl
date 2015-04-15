<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "webserver::apache":
        apache_service_name => "${component.componentName}",
      <#comment>TODO CLOUD BRANCHING</#comment>
      <#if awsVolume ??>
        apache_vgdisk       => "${awsVolume.device}",
        apache_visor        => "aws",
      <#elseif cloudstackVolume ??>
        apache_vgdisk       => "${cloudstackVolume.deviceid}",
        apache_visor          => "${cloudstackVolume.hypervisor}",
      <#elseif vmwareDisk ??>
        apache_vgdisk       => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        apache_visor          => "vmware",
      <#elseif vcloudDisk ??>
        apache_vgdisk         => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        apache_visor          => "vcloud",
      <#elseif niftyVolume ??>
        apache_vgdisk         => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${niftyVolume.scsiId}:0",
        apache_visor          => "nifty",
      <#elseif azureDisk ??>
        apache_vgdisk         => "${azureDisk.device}",
        apache_visor          => "azure",
      <#elseif openstackVolume ??>
        apache_vgdisk         => "${openstackVolume.device}",
        apache_visor          => "openstack",
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

  <#if (sampleGeronimoInstances ??) && (sampleGeronimoInstances?size > 0)>
    # for arvicio Sample
    apache::proxy_ajp_balancer { "arvicio-sample-ibatis" :
        location => "/arvicio-sample-ibatis/",
        target   => "arvicio-sample-ibatis/",
        apservers => [
          <#list sampleGeronimoInstances as sampleGeronimoInstance>
          <#assign host = sampleGeronimoInstance.instanceName>
          <#assign ip = accessIps[sampleGeronimoInstance.instanceNo?string]>
          <#assign port = "8009">
            "${host},${ip},${port}",
          </#list>
        ],

      <#if (start == true)>
        ensure => "enabled",
      <#else>
        ensure => "disabled",
      </#if>
    }
  </#if>

  <#if (sampleTomcatInstances ??) && (sampleTomcatInstances?size > 0)>
    # for arvicio Sample
    apache::proxy_ajp_balancer { "arvicio-sample-ibatis" :
        location => "/arvicio-sample-ibatis/",
        target   => "arvicio-sample-ibatis/",
        apservers => [
          <#list sampleTomcatInstances as sampleTomcatInstance>
          <#assign host = sampleTomcatInstance.instanceName>
          <#assign ip = accessIps[sampleTomcatInstance.instanceNo?string]>
          <#assign port = "8009">
            "${host},${ip},${port}",
          </#list>
        ],

      <#if (start == true)>
        ensure => "enabled",
      <#else>
        ensure => "disabled",
      </#if>
    }
  </#if>

  <#if appNames ??>
  <#list appNames?split(",") as appName>
  </#list>
  </#if>
    class { "webserver::apache::mount": }
    class { "webserver::apache::resource": require => Class[ "Webserver::Apache::Mount" ] }
<#else>
    class { "webserver::apache::stop": }
    class { "webserver::apache::unmount": require => Class[ "Webserver::Apache::Stop" ] }
</#if>

}
