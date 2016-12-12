<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_base {

<#if (start == true)>
        class {"basenode":
            zabbix_server      => "${zabbixServer}",
        zabbix_hostname    => "${zabbixHostname}",
            rsyslog_log_server => "${rsyslogServer}",
            puppet_ssh_user    => "puppetmaster",
            puppet_ssh_pass    => "${puppetInstance.password}",
            ssh_usr_username   => "${user.username}",
            ssh_usr_password   => "${user.password}"
        }

    if <#noparse>"${operatingsystem}"</#noparse> == "CentOS" { include basenode::resource }
    elsif <#noparse>"${operatingsystem}"</#noparse> == "windows" { include basenode::resource-windows }

  <#if vcloudDisks ??>
  <#list vcloudDisks as vcloudDisk>
  <#if (vcloudDisk.componentNo ??) && (vcloudDisk.attached ??) && (vcloudDisk.attached == true)>
  <#if (componentTypeNameMap[vcloudDisk.componentNo?string] == "mysql")>
    include dbserver::mysql

    lvm::config { <#noparse>"${dbserver::mysql::db_home}"</#noparse>:
        vgname => <#noparse>"${dbserver::mysql::mysql_vgname}"</#noparse>,
        lvname => <#noparse>"${dbserver::mysql::mysql_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "tomcat")>
    include apserver::tomcat

    lvm::config { <#noparse>"${apserver::tomcat::ap_home}"</#noparse>:
        vgname => <#noparse>"${apserver::tomcat::tomcat_vgname}"</#noparse>,
        lvname => <#noparse>"${apserver::tomcat::tomcat_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "geronimo")>
    include apserver::geronimo

    lvm::config { <#noparse>"${apserver::geronimo::ap_home}"</#noparse>:
        vgname => <#noparse>"${apserver::geronimo::geronimo_vgname}"</#noparse>,
        lvname => <#noparse>"${apserver::geronimo::geronimo_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "apache")>
    include webserver::apache

    lvm::config { <#noparse>"${webserver::apache::web_home}"</#noparse>:
        vgname => <#noparse>"${webserver::apache::apache_vgname}"</#noparse>,
        lvname => <#noparse>"${webserver::apache::apache_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "prjserver")>
    include prjserver

    lvm::config { <#noparse>"${prjserver::prjserver_home}"</#noparse>:
        vgname => <#noparse>"${prjserver::prjserver_vgname}"</#noparse>,
        lvname => <#noparse>"${prjserver::prjserver_lvname}"</#noparse>,
  </#if>
        vgdisk => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        visor  => "vcloud",
        require => Class[ "Basenode::Resource" ],
    }
  </#if>
  </#list>
  </#if>
<#else>
  <#if vcloudDisks ??>
  <#list vcloudDisks as vcloudDisk>
  <#if (vcloudDisk.componentNo ??) && (vcloudDisk.attached ??) && (vcloudDisk.attached == true)>
  <#if (componentTypeNameMap[vcloudDisk.componentNo?string] == "mysql")>
    include dbserver::mysql

    lvm::detach{ <#noparse>"${dbserver::mysql::db_home}"</#noparse>:
        vgname => <#noparse>"${dbserver::mysql::mysql_vgname}"</#noparse>,
        lvname => <#noparse>"${dbserver::mysql::mysql_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "tomcat")>
    include apserver::tomcat

    lvm::detach{ <#noparse>"${apserver::tomcat::ap_home}"</#noparse>:
        vgname => <#noparse>"${apserver::tomcat::tomcat_vgname}"</#noparse>,
        lvname => <#noparse>"${apserver::tomcat::tomcat_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "geronimo")>
    include apserver::geronimo

    lvm::detach{ <#noparse>"${apserver::geronimo::ap_home}"</#noparse>:
        vgname => <#noparse>"${apserver::geronimo::geronimo_vgname}"</#noparse>,
        lvname => <#noparse>"${apserver::geronimo::geronimo_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "apache")>
    include webserver::apache

    lvm::detach{ <#noparse>"${webserver::apache::web_home}"</#noparse>:
        vgname => <#noparse>"${webserver::apache::apache_vgname}"</#noparse>,
        lvname => <#noparse>"${webserver::apache::apache_lvname}"</#noparse>,
  <#elseif (componentTypeNameMap[vcloudDisk.componentNo?string] == "prjserver")>
    include prjserver

    lvm::detach{ <#noparse>"${prjserver::prjserver_home}"</#noparse>:
        vgname => <#noparse>"${prjserver::prjserver_vgname}"</#noparse>,
        lvname => <#noparse>"${prjserver::prjserver_lvname}"</#noparse>,
  </#if>
        vgdisk => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        visor  => "vcloud",
        before => Class[ "Basenode::Stop" ],
    }
  </#if>
  </#list>
  </#if>

    if <#noparse>"${operatingsystem}"</#noparse> == "CentOS" { include basenode::stop }
    elsif <#noparse>"${operatingsystem}"</#noparse> == "windows" { include basenode::stop-windows }
</#if>

}
