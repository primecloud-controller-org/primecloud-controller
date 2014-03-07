<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "dbserver::mysql":
        mysql_service_name  => "${component.componentName}",
    <#if awsVolume ??>
        mysql_vgdisk        => "${awsVolume.device}",
        mysql_visor         => "aws",
    <#elseif cloudstackVolume ??>
        mysql_vgdisk        => "${cloudstackVolume.deviceid}",
        mysql_visor         => "${cloudstackVolume.hypervisor}",
    <#elseif vmwareDisk ??>
        mysql_vgdisk        => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        mysql_visor         => "vmware",
    <#else>
        mysql_vgdisk        => "",
        mysql_visor         => "",
    </#if>
    <#if (unDetachVolume == true)>
        mysql_unmount       => "false",
    </#if>
        mysql_server_type   => "${mysqlType}",
        mysql_server_id     => "${instance.instanceNo}",
    <#if mysqlType == "MASTER">
        # Master
        mysql_root_username => "root",
        mysql_root_password => "9rUDm1es9L${instance.instanceNo}",
        mysql_mng_username  => "puppet-mng",
        mysql_mng_password  => "0rUDmLprK${instance.instanceNo}",
        # Demo
        mysql_usr_username  => "${user.username}",
        mysql_usr_password   => "${user.password}",
    <#else>
        # Master
        mysql_root_username => "root",
        mysql_root_password => "9rUDm1es9L${masterInstanceNo}",
        mysql_mng_username  => "puppet-mng",
        mysql_mng_password  => "0rUDmLprK${masterInstanceNo}",
        # Slave
        mysql_repl_username => "puppet-repl",
        mysql_repl_password => "9rUDmLprK${instance.instanceNo}",
        mysql_dump_username => "puppet-dump",
        mysql_dump_password => "8rUDmLprK${instance.instanceNo}",
      <#if masterInstance ??>
        mysql_master_host   => "${accessIps[masterInstance.instanceNo?string]}",
        mysql_master_port   => "3306",
      </#if>
        mysql_ssh_username  => "puppetmaster",
        mysql_reset_slave    => "false",
    </#if>
    <#if (customParam1 ??)>
        mysql_custom_param_1 => "${customParam1}",
    <#else>
        mysql_custom_param_1 => "",
    </#if>
    <#if (customParam2 ??)>
        mysql_custom_param_2 => "${customParam2}",
    <#else>
        mysql_custom_param_2 => "",
    </#if>
    <#if (customParam3 ??)>
        mysql_custom_param_3 => "${customParam3}",
    <#else>
        mysql_custom_param_3 => "",
    </#if>
    }

<#if (start == true)>
  <#if mysqlType == "MASTER">
  <#list slaveInstances as slaveInstance>
    mysql::registerSlave{ "${accessIps[slaveInstance.instanceNo?string]}":
        dump_username => "puppet-dump",
        dump_password => "8rUDmLprK${slaveInstance.instanceNo}",
        repl_username => "puppet-repl",
        repl_password => "9rUDmLprK${slaveInstance.instanceNo}",
        root_username => "<#noparse>${dbserver::mysql::mysql_root_username}</#noparse>",
        root_password => "<#noparse>${dbserver::mysql::mysql_root_password}</#noparse>",
        require       => Class[ "Dbserver::Mysql::Resource" ]
    }

  </#list>
  </#if>

    class { "dbserver::mysql::mount": }
    class { "dbserver::mysql::resource": require => Class[ "Dbserver::Mysql::Mount" ] }

  <#if ((phpMyAdmin ??) && (phpMyAdmin == true))>
    include dbserver::mysql::phpmyadmin
  </#if>
<#else>

    class { "dbserver::mysql::stop": }
    class { "dbserver::mysql::unmount": require => Class[ "Dbserver::Mysql::Stop" ] }

  <#if ((phpMyAdmin ??) && (phpMyAdmin == true))>
    include dbserver::mysql::phpmyadmin::stop
  </#if>
</#if>

}
