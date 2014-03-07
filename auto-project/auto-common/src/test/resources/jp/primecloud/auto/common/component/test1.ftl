<#list entries?keys as key>
${key} = ${entries[key]}
</#list>

${aaa}
${bbb}

<#if (enable ??) && (enable == true)>
TRUE
<#else>
FALSE
</#if>

${model.enabled?string}

${num}
${num?c}
${num?string}
