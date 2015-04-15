class lvm{

}

define lvm::detach (
    $vgname,
    $lvname,
    $vgdisk="",
    $visor =""
){

#    exec { "vgdisable-${vgname}":
#        command     => "vgchange -a n ${vgname} && sleep 1",
#        require   => [ Mount["${name}"] ],
#    }

    
    if $vgdisk =~ /^\/dev\/disk\/by-path\/.*/ {
        #Delete Block Device 
        exec { "deleteDev-${vgdisk}":
            command => "echo 1 > /sys/block/`readlink -f ${vgdisk} | sed 's/^\/.*\///'`/device/delete",
            require => Logical_volume["${lvname}"],
            onlyif  => "test -b `readlink -f ${vgdisk}`",
        } 
    } else {
        if "${visor}" == "VMware" or "${visor}" == "KVM" {
            #Delete Block Device 
            exec { "deleteDev-${vgdisk}":
                command => "echo 1 > /sys/block/`readlink -f ${vgdisk} | sed 's/^\/.*\///'`/device/delete",
                require => Logical_volume["${lvname}"],
                onlyif  => "test -b `readlink -f ${vgdisk}`",
            }
        }
    }

    logical_volume { "${lvname}":
         ensure       => present,
         enable       => false,
         volume_group => "${vgname}",
         require      => [ Mount["${name}"] ],
    }
    
    mount { "${name}":
        ensure    => "absent",
        require => Exec["killProc-${name}"],
    }
    
    exec { "killProc-${name}":
        command => "fuser -muk ${name} || true ",
        onlyif  => "mount | grep ${name}",
    }

#    $syslog_pri="local1.info"
#    exec { "Log:lvm_${name}" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : lvmdetach[${name}] is completed'",
#       require    => [Mount["${name}"]]
#   }

}

define lvm::config (
    $vgdisk ,         
    $vgname ,      # Name of volume group
    $lvname ,      # Name of logical volume
    $lvsize   = "",      # Size of the logical volume as understood by lvcreate
    $visor    = "", 
    $fstype   = "ext3",               # Filesystem type as understood by mkfs
    $owner    = "root",
    $group    = "root",
    $mode     = "755") {

    file { "${name}":
        ensure => directory,
        owner  => "${owner}",
        group  => "${group}",
        mode   => "${mode}",
    }

    #for vmware
    if $vgdisk =~ /^\/dev\/disk\/by-path\/.*/ {
        $vgdev = "${vgdisk}-part1"
        exec { "scan-scsidisk-${vgdev}" :
            before  => [ Exec["fdisk-${vgdev}"] ],
            command => "echo \"- - -\" > /sys/class/scsi_host/host0/scan && sleep 5",
        }
    } else {
        file { "/usr/local/bin/scanscsidev.sh" :
            before  => Exec[ "scanscsidev" ],
            mode   => "755" ,
            owner  => "root" ,
            group  => "root" ,
            source => "puppet:///modules/lvm/scanscsidev.sh",
        }
        
        $vgdev = "${vgdisk}1"
        exec { "scanscsidev":
            before  => [ Exec["fdisk-${vgdev}"] ],
            command => "/usr/local/bin/scanscsidev.sh",
        }
    }

    exec { "fdisk-${vgdev}":
        command     => "fdisk ${vgdisk} << EOF
n
p
1


t
8e
w
EOF
sleep 10",
	creates     => "${vgdev}",
    }

    physical_volume { "${vgdev}":
        ensure  => present,
        require => [ Exec["fdisk-${vgdev}"] ],
    }

    volume_group { "${vgname}":
        ensure           => present,
        physical_volumes => "${vgdev}",
        require          => [ Physical_Volume["${vgdev}"] ],
    }

    case $lvsize {
        ''        : {
            logical_volume { "${lvname}":
                ensure       => present,
                enable       => true,
                volume_group => "${vgname}",
                size         => undef,
                require      => [ Volume_Group["${vgname}"] ],
            }
        }
        'default' : {
            logical_volume { "${lvname}":
                ensure       => present,
                enable       => true,
                volume_group => "${vgname}",
                size         => "${lvsize}",
                require      => [ Volume_Group["${vgname}"] ],
            }
        }
    }
 
#    filesystem { "/dev/$vgname/$lvname":
#        ensure  => "${fstype}",
#        require => [ Logical_Volume["${lvname}"] ],
#    }

    filesystem { "/dev/$vgname/$lvname":
        ensure => present,
        fs_type => "${fstype}",
        require => Logical_volume["${lvname}"]
    }

    mount { "${name}":
        device    => "/dev/$vgname/$lvname",
        ensure    => mounted,
        fstype    => "${fstype}",
        options   => "defaults",
        dump      => "0",
        pass      => "0",
        remounts  => true,
        require   => [ FileSystem["/dev/${vgname}/${lvname}"] , File["${name}"] ],
    }

#   $syslog_pri="local1.info"
#   exec { "Log:lvm_${name}" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : lvmconfig[${name}] is completed'",
#       require    => [Mount["${name}"],Service["rsyslog"]]
#   }
}
