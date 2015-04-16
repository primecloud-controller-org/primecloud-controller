<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "apserver::tomcat":
        tomcat_service_name     => "${component.componentName}",
      <#comment>TODO CLOUD BRANCHING</#comment>
      <#if awsVolume ??>
        tomcat_vgdisk           => "${awsVolume.device}",
        tomcat_visor            => "aws",
      <#elseif cloudstackVolume ??>
        tomcat_vgdisk           => "${cloudstackVolume.deviceid}",
        tomcat_visor            => "${cloudstackVolume.hypervisor}",
      <#elseif vmwareDisk ??>
        tomcat_vgdisk           => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        tomcat_visor            => "vmware",
      <#elseif vcloudDisk ??>
        tomcat_vgdisk           => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        tomcat_visor            => "vcloud",
      <#elseif niftyVolume ??>
        tomcat_vgdisk           => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${niftyVolume.scsiId}:0",
        tomcat_visor            => "nifty",
      <#elseif azureDisk ??>
        tomcat_vgdisk           => "${azureDisk.device}",
        tomcat_visor            => "azure",
      <#elseif openstackVolume ??>
        tomcat_vgdisk           => "${openstackVolume.device}",
        tomcat_visor            => "openstack",
      <#else>
        tomcat_vgdisk           => "",
        tomcat_visor            => "",
      </#if>
        tomcat_admin_username   => "admin",
        tomcat_admin_password   => "admin${instance.instanceNo}",
        tomcat_user_username    => "${user.username}",
        tomcat_user_password    => "${user.password}",
      <#if (unDetachVolume == true)>
        tomcat_unmount          => "false",
      </#if>
      <#if (customParam1 ??)>
        tomcat_custom_param_1   => "${customParam1}",
      <#else>
        tomcat_custom_param_1   => "",
      </#if>
      <#if (customParam2 ??)>
        tomcat_custom_param_2   => "${customParam2}",
      <#else>
        tomcat_custom_param_2   => "",
      </#if>
      <#if (customParam3 ??)>
        tomcat_custom_param_3   => "${customParam3}",
      <#else>
        tomcat_custom_param_3   => "",
      </#if>
    }

<#if (start == true)>
  <#if databases ??>
    # DataSource
    tomcat::createDataSource { "context.xml.default" :
        dbservers => [
          <#list databases as database>
          <#assign name = database.component.componentName>
          <#assign ip = "${database.component.componentName}-host">
          <#assign username = user.username>
          <#assign password = user.password>
            "${name},${ip},${username},${password}",
          </#list>
        ],
        ap_home   => "<#noparse>${apserver::tomcat::ap_home}</#noparse>"
    }

    # Host
    <#list databases as database>
    host { "${database.component.componentName}-host" :
      <#if database.instance ??>
        ip      => "${accessIps[database.instance.instanceNo?string]}",
        ensure  => "present",
      <#else>
        ensure  => "absent",
      </#if>
    }
    </#list>
  </#if>

  <#if sampleDbInstance ??>
    # for arvicio Sample
    tomcat::dbhost { "AppDS-master":
        ip => "${accessIps[sampleDbInstance.instanceNo?string]}"
    }
  </#if>

    class { "apserver::tomcat::mount": }
    class { "apserver::tomcat::resource": require => Class[ "Apserver::Tomcat::Mount" ] }
<#else>
  <#if databases ??>
    # Host
    <#list databases as database>
    host { "${database.component.componentName}-host" :
        ensure => "absent",
    }
    </#list>
  </#if>

    class { "apserver::tomcat::stop": }
    class { "apserver::tomcat::unmount": require => Class[ "Apserver::Tomcat::Stop" ] }
</#if>

}
