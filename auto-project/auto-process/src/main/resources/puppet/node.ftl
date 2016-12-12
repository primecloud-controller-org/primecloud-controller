<#comment><#include "/debug.ftl"></#comment>

node '${instance.fqdn}' inherits default {

    $user_name   = "${user.username}"
    $cloud_name  = "${farm.farmName}"
    $server_name = "${instance.instanceName}"

  <#if platform.platformType == "aws">
    $platform_name  = "aws"
  <#elseif platform.platformType == "cloudstack">
    $platform_name  = "cloudstack"
  <#elseif platform.platformType == "vmware">
    $platform_name  = "vmware"
  <#elseif platform.platformType == "nifty">
    $platform_name  = "nifty"
  <#elseif platform.platformType == "vcloud">
    $platform_name  = "vcloud"
  <#elseif platform.platformType == "azure">
    $platform_name  = "azure"
  </#if>

    $component_names = "<#list associatedComponents as associatedComponents>${associatedComponents.componentName}<#if associatedComponents_has_next>,</#if></#list>"
    $component_types = "<#list associatedComponentTypes as associatedComponentTypes>${associatedComponentTypes.componentTypeName}<#if associatedComponentTypes_has_next>,</#if></#list>"

    $all_db_servers = [
      <#if (allDbInstances ??)>
       <#list allDbInstances?sort_by("instanceNo") as allDbInstance>
        "${allDbInstance.fqdn}",
       </#list>
      </#if>
    ]
    $all_ap_servers = [
      <#if (allApInstances ??)>
       <#list allApInstances?sort_by("instanceNo") as allApInstance>
        "${allApInstance.fqdn}",
       </#list>
      </#if>
    ]
    $all_web_servers = [
      <#if (allWebInstances ??)>
       <#list allWebInstances?sort_by("instanceNo") as allWebInstance>
        "${allWebInstance.fqdn}",
       </#list>
      </#if>
    ]
    $db_servers = [
      <#if (dbInstances ??)>
       <#list dbInstances?sort_by("instanceNo") as dbInstance>
        "${dbInstance.fqdn}",
       </#list>
      </#if>
    ]
    $ap_servers = [
      <#if (apInstances ??)>
       <#list apInstances?sort_by("instanceNo") as apInstance>
        "${apInstance.fqdn}",
       </#list>
      </#if>
    ]
    $web_servers = [
      <#if (webInstances ??)>
       <#list webInstances?sort_by("instanceNo") as webInstance>
        "${webInstance.fqdn}",
       </#list>
      </#if>
    ]

    if defined("${instance.fqdn?replace('.','_')}_base") {
        include ${instance.fqdn?replace('.','_')}_base
    }

    if defined("${instance.fqdn?replace('.','_')}_base_coordinate") {
        include ${instance.fqdn?replace('.','_')}_base_coordinate
    }

  <#list components as component>
    if defined("${instance.fqdn?replace('.','_')}_${component.componentName}") {
        include ${instance.fqdn?replace('.','_')}_${component.componentName}
    }

  </#list>

}
