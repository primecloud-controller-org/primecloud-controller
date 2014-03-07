<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_base_coordinate {

<#if (start == true)>
  <#assign count = 0>
  <#list stopInstances as stopInstance>
  <#if (instance.instanceNo != stopInstance.instanceNo)>
    <#if (count != 0)><#noparse>-></#noparse></#if>
    host { "${stopInstance.fqdn}.ipSelectTable" :
        ensure => "absent",
    }<#assign count = "${count} + 1"?eval>
  </#if>
  </#list>
  <#list startInstances as startInstance>
  <#if (instance.instanceNo != startInstance.instanceNo)>
    <#if (count != 0)><#noparse>-></#noparse></#if>
    host { "${startInstance.fqdn}.ipSelectTable" :
        ensure       => "present",
        ip           => "${accessIps[startInstance.instanceNo?string]}",
        host_aliases => [ "${startInstance.instanceName}","${startInstance.fqdn}" ],
    }<#assign count = "${count} + 1"?eval>
  </#if>
  </#list>

  <#if platform.platformType == "nifty">
   <#assign ipList = "">
   <#list startInstances as startInstance>
    <#if (instance.platformNo == startInstance.platformNo)>
     <#if (ipList != "")>
      <#assign ipList = ipList + ",">
     </#if>
     <#assign ipList = ipList + startInstance.privateIp>
    </#if>
   </#list>
    iptables::config { "configIptables" :
        ip_list => "${ipList}",
    }
  </#if>

<#else>
  <#assign count = 0>
  <#list stopInstances as stopInstance>
  <#if (instance.instanceNo != stopInstance.instanceNo)>
    <#if (count != 0)><#noparse>-></#noparse></#if>
    host { "${stopInstance.fqdn}.ipSelectTable" :
        ensure => "absent",
    }<#assign count = "${count} + 1"?eval>
  </#if>
  </#list>
  <#list startInstances as startInstance>
  <#if (instance.instanceNo != startInstance.instanceNo)>
    <#if (count != 0)><#noparse>-></#noparse></#if>
    host { "${startInstance.fqdn}.ipSelectTable" :
        ensure => "absent",
    }<#assign count = "${count} + 1"?eval>
  </#if>
  </#list>

  <#if platform.platformType == "nifty">
    iptables::config { "configIptables" :
        ip_list => "",
    }
  </#if>

</#if>

}
