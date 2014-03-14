class prjserver (
    $prjserver_service_name    = "",
    $prjserver_vgdisk          = "/dev/sdd",
    $prjserver_lvsize          = "",
    $prjserver_unmount         = "true", #unmount volume or not
    $prjserver_visor           = "" #hyper visor for mount or unmount volume
){
    $prjserver_home            = "/mnt/data01"
    $prjserver_rsync_server    = "${servername}" #puppet master fqdn
    $prjserver_vgname          = "VGprj1"
    $prjserver_lvname          = "LVprj01"
    
    $prjserver_backup_server   = ""
    $prjserver_backup_password = "manager"
    $prjserver_backup_sshkey   = "" 
}

#class prjserver::default inherits prjserver {
#    include prjserver-module
#    
#    $prjserver_home         = "/mnt/data01"
#    $prjserver_rsync_server = "${servername}"
#    $prjserver_lvsize       = ""
#    $prjserver_vgname       = "VGprj1"
#    $prjserver_lvname       = "LVprj01"
##    $prjserver_vgdisk       = "/dev/sdd1"
#    $prjserver_vgdisk       = "/dev/sdd"
#   
#    $prjserver_backup_server   = ""
#    $prjserver_backup_password = "manager"
#    $prjserver_backup_sshkey   = "" 
#}

class prjserver::resource {

    if "${prjserver::prjserver_vgdisk}" == "" {
        file { "${prjserver::prjserver_home}":
            ensure => directory,
            owner  => "root",
            group  => "root",
            mode   => 755,
        }
        mount { "${prjserver::prjserver_home}" :
            ensure  => "absent",
            require => File["${prjserver::prjserver_home}"],
        }
    } else {
        lvm::config { "${prjserver::prjserver_home}":
            lvname  => "${prjserver::prjserver_lvname}",
            lvsize  => "${prjserver::prjserver_lvsize}",
            vgname  => "${prjserver::prjserver_vgname}",
            vgdisk  => "${prjserver::prjserver_vgdisk}",
            visor   => "${prjserver::prjserver_visor}",
        }
    }

    $path = "${rsync_userdata_dir}/${user_name}/${cloud_name}/${prjserver::prjserver_service_name}"
    $ret = generate("/etc/puppet/checkDir.sh","${path}")
    if ($ret =~ /true/ ) {
        $prjserver_rsync_path = "/userdata/${user_name}/${cloud_name}/${prjserver::prjserver_service_name}"
        $prjserver_template_path = "${path}/templates"
        notify{ "using prjserver user-custom directory = ${path}": }

        prjserver-module::config { "${prjserver::prjserver_home}":
            backup_server   => "${prjserver::prjserver_backup_server}",
            backup_password => "${prjserver::prjserver_backup_password}",
            backup_sshkey   => "${prjserver::prjserver_backup_sshkey}",
            rsync_server    => "${prjserver::prjserver_rsync_server}",
            rsync_path      => "${prjserver_rsync_path}",
            template_path   => "${prjserver_template_path}",
        }
    }else{
        prjserver-module::config { "${prjserver::prjserver_home}":
            backup_server   => "${prjserver::prjserver_backup_server}",
            backup_password => "${prjserver::prjserver_backup_password}",
            backup_sshkey   => "${prjserver::prjserver_backup_sshkey}",
            rsync_server    => "${prjserver::prjserver_rsync_server}",
        }
    }
}


class prjserver::stop {
    if "${prjserver::prjserver_vgdisk}" == "" or "${prjserver::prjserver_unmount}" == "false"{
        mount { "${prjserver::prjserver_home}" :
            noop    => "true",
            ensure  => "absent",
            require => Exec["killProc-${prjserver::prjserver_home}"],
        }
        exec { "killProc-${prjserver::prjserver_home}":
            command => "true",
        }
    } else {
        lvm::detach{ "${prjserver::prjserver_home}":
            vgname => "${prjserver::prjserver_vgname}",
            lvname => "${prjserver::prjserver_lvname}",
            vgdisk => "${prjserver::prjserver_vgdisk}",
            visor  => "${prjserver::prjserver_visor}",
        }
    }

    prjserver-module::stop{ "${prjserver::prjserver_home}": 
        backup_server   => "${prjserver::prjserver_backup_server}",
        backup_password => "${prjserver::prjserver_backup_password}",
    }
}

