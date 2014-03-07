<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} inherits lbserver::ultramonkey::default {

    $ultramonkey_service_name = "${component.componentName}"

<#if (start == true)>
    include lbserver::ultramonkey::resource
<#else>
    include lbserver::ultramonkey::stop
</#if>

}
