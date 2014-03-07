<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    #$ultramonkey_service_name = "${component.componentName}"

<#if (loadBalancer.enabled == true)>
  <#list listeners as listener>
  <#list listenIps as listenIp>
    ultramonkey::virtual_server { "${listenIp}:${listener.loadBalancerPort}":
        real_servers => [
          <#list targetInstances as targetInstance>
            "${accessIps[targetInstance.instanceNo?string]}:${listener.servicePort} masq 1",
          </#list>
        ],
        checktimeout     => "${healthCheck.checkTimeout}",
        negotiatetimeout => "${healthCheck.checkTimeout}",
        checkinterval    => "${healthCheck.checkInterval}",
        retryinterval    => "${healthCheck.checkInterval}",
        checkcount       => "${healthCheck.unhealthyThreshold}",
        checkport => "${healthCheck.checkPort}",
      <#if (healthCheck.checkProtocol == "TCP")>
        checktype => "connect",
        service   => "none",
      <#elseif (healthCheck.checkProtocol == "HTTP")>
        checktype => "negotiate",
        service   => "http",
        request   => "${healthCheck.checkPath}",
        receive   => "html",
      </#if>
    }

  </#list>
  </#list>

    include lbserver::ultramonkey::resource
<#else>
    include lbserver::ultramonkey::stop
</#if>

}
