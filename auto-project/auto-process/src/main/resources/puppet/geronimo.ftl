<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "apserver::geronimo" :
        geronimo_service_name   => "${component.componentName}",
      <#comment>TODO CLOUD BRANCHING</#comment>
      <#if awsVolume ??>
        geronimo_vgdisk         => "${awsVolume.device}",
        geronimo_visor          => "aws",
      <#elseif cloudstackVolume ??>
        geronimo_vgdisk         => "${cloudstackVolume.deviceid}",
        geronimo_visor          => "${cloudstackVolume.hypervisor}",
      <#elseif vmwareDisk ??>
        geronimo_vgdisk         => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        geronimo_visor          => "vmware",
      <#elseif vcloudDisk ??>
        geronimo_vgdisk         => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        geronimo_visor          => "vcloud",
      <#elseif niftyVolume ??>
        geronimo_vgdisk         => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${niftyVolume.scsiId}:0",
        geronimo_visor          => "nifty",
      <#elseif azureDisk ??>
        geronimo_vgdisk         => "${azureDisk.device}",
        geronimo_visor          => "azure",
      <#elseif openstackVolume ??>
        geronimo_vgdisk         => "${openstackVolume.device}",
        geronimo_visor          => "openstack",
      <#else>
        geronimo_vgdisk         => "",
        geronimo_visor          => "",
      </#if>
        geronimo_admin_username => "system",
        geronimo_admin_password => "manager${instance.instanceNo}",
        geronimo_user_username  => "${user.username}",
        geronimo_user_password  => "${user.password}",
      <#if (unDetachVolume == true)>
        geronimo_unmount        => "false",
      </#if>
      <#if (customParam1 ??)>
        geronimo_custom_param_1 => "${customParam1}",
      <#else>
        geronimo_custom_param_1 => "",
      </#if>
      <#if (customParam2 ??)>
        geronimo_custom_param_2 => "${customParam2}",
      <#else>
        geronimo_custom_param_2 => "",
      </#if>
      <#if (customParam3 ??)>
        geronimo_custom_param_3 => "${customParam3}",
      <#else>
        geronimo_custom_param_3 => "",
      </#if>
    }
<#if (start == true)>
  <#if databases ??>
    # DataSource
    <#list databases as database>
    geronimo22::createDataSource { "${database.component.componentName}" :
        ap_home  => "<#noparse>${apserver::geronimo::ap_home}</#noparse>",
        ip       => "${database.component.componentName}-host",
        username => "${user.username}",
        password => "${user.password}",
        admin_username => "<#noparse>${apserver::geronimo::geronimo_admin_username}</#noparse>",
        admin_password => "<#noparse>${apserver::geronimo::geronimo_admin_password}</#noparse>"
    }
    </#list>

    # Host
    <#list databases as database>
    host { "${database.component.componentName}-host" :
      <#if database.instance ??>
        ip => "${accessIps[database.instance.instanceNo?string]}",
        ensure => "present",
      <#else>
        ensure => "absent",
      </#if>
    }
    </#list>
  </#if>

  <#if sampleDbInstance ??>
    # for arvicio Sample
    geronimo22::dbhost { "AppDS-master":
        ip => "${accessIps[sampleDbInstance.instanceNo?string]}"
    }
  </#if>

    class { "apserver::geronimo::mount": }
    class { "apserver::geronimo::resource": require => Class[ "Apserver::Geronimo::Mount" ] }
<#else>
  <#if databases ??>
    # Host
    <#list databases as database>
    host { "${database.component.componentName}-host" :
        ensure => "absent",
    }
    </#list>
  </#if>

    class { "apserver::geronimo::stop": }
    class { "apserver::geronimo::unmount": require => Class[ "Apserver::Geronimo::Stop" ] }
</#if>

}
