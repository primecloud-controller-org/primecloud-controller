<#comment><#include "/debug.ftl"></#comment>

class ${instance.fqdn?replace('.','_')}_${component.componentName} {

    class { "prjserver":
        prjserver_service_name => "${component.componentName}",
      <#if awsVolume ??>
        prjserver_vgdisk       => "${awsVolume.device}",
        prjserver_visor        => "aws",
      <#elseif cloudstackVolume ??>
        prjserver_vgdisk       => "${cloudstackVolume.deviceid}",
        prjserver_visor        => "${cloudstackVolume.hypervisor}",
      <#elseif vmwareDisk ??>
        prjserver_vgdisk       => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vmwareDisk.scsiId}:0",
        prjserver_visor        => "vmware",
      <#elseif vcloudDisk ??>
        prjserver_vgdisk       => "/dev/disk/by-path/pci-0000:00:10.0-scsi-0:0:${vcloudDisk.unitNo}:0",
        prjserver_visor        => "vcloud",
      <#elseif azureDisk ??>
        geronimo_vgdisk         => "${azureDisk.lun}",
        geronimo_visor          => "azure",
      <#else>
        prjserver_vgdisk       => "",
        prjserver_visor        => "",
      </#if>
      <#if (unDetachVolume == true)>
        prjserver_unmount      => "false",
      </#if>
    }

<#if (start == true)>
    include prjserver::resource
<#else>
    include prjserver::stop
</#if>

}
