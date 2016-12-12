class dbserver {
    $db_home = "/mnt/db"
}

class dbserver::mysql (
    $mysql_service_name  = "", #pcc service name
    $mysql_vgdisk        = "/dev/sdg",
    $mysql_lvsize        = "",
    $mysql_unmount       = "true", #unmount volume or not
    $mysql_visor         = "",  #hyper visor for mount or unmount volume
    
    $mysql_server_type   = "MASTER", 
    $mysql_server_id     = "1", #pcc instance_no
    
    #Master
    $mysql_root_username = "root", 
    $mysql_root_password = "9rUDm1es9L",
    $mysql_mng_username  = "puppet-mng",
    $mysql_mng_password  = "mng-pass",
    
    #Demo
    $mysql_usr_username  = "",
    $mysql_usr_password  = "",
    
    #Slave
    $mysql_repl_username = "puppet-repl",
    $mysql_repl_password = "repl-pass",
    $mysql_dump_username = "puppet-dump",
    $mysql_dump_password = "dump-pass",
    
    $mysql_master_host   = "",
    $mysql_master_port   = "3306",
    $mysql_ssh_username  = "puppetmaster",
    $mysql_reset_slave   = "false",
    
    $mysql_custom_param_1 = "",
    $mysql_custom_param_2 = "",
    $mysql_custom_param_3 = ""
) inherits dbserver {
    $mysql_vgname            = "VGdb1"
    $mysql_lvname            = "LVdb01"
    $mysql_host_address      = "${ipaddress}"
    $mysql_root_password_org = ""
    
    #Check user-custom directory
    $custom_rsync_path = "${rsync_userdata_dir}/${user_name}/${cloud_name}/${mysql_service_name}"
    $use_custom_rsync = generate("/etc/puppet/checkDir.sh","${custom_rsync_path}")
    if ($use_custom_rsync =~ /true/ ) {
        $mysql_rsync_path = "/userdata/${user_name}/${cloud_name}/${mysql_service_name}"
        $mysql_template_path = "${custom_rsync_path}/templates"
        notify{ "using mysql user-custom directory= ${custom_rsync_path}": }
    }
}

class dbserver::mysql::resource (
    $db_home                 = "${dbserver::mysql::db_home}",
    $mysql_server_type       = "${dbserver::mysql::mysql_server_type}",
    $mysql_server_id         = "${dbserver::mysql::mysql_server_id}",
    
    $mysql_root_username     = "${dbserver::mysql::mysql_root_username}",
    $mysql_root_password_org = "${dbserver::mysql::mysql_root_password_org}",
    $mysql_root_password     = "${dbserver::mysql::mysql_root_password}",
    $mysql_mng_username      = "${dbserver::mysql::mysql_mng_username}",
    $mysql_mng_password      = "${dbserver::mysql::mysql_mng_password}",
    
    $mysql_usr_username      = "${dbserver::mysql::mysql_usr_username}",
    $mysql_usr_password      = "${dbserver::mysql::mysql_usr_password}",
    
    $mysql_repl_username     = "${dbserver::mysql::mysql_repl_username}",
    $mysql_repl_password     = "${dbserver::mysql::mysql_repl_password}",
    $mysql_dump_username     = "${dbserver::mysql::mysql_dump_username}",
    $mysql_dump_password     = "${dbserver::mysql::mysql_dump_password}",
    
    $mysql_ssh_username      = "${dbserver::mysql::mysql_ssh_username}",
    $mysql_master_host       = "${dbserver::mysql::mysql_master_host}",
    $mysql_master_port       = "${dbserver::mysql::mysql_master_port}",
    $mysql_host_address      = "${dbserver::mysql::mysql_host_address}",
    $mysql_reset_slave       = "${dbserver::mysql::mysql_reset_slave}",
    
    $use_custom_rsync        = "${dbserver::mysql::use_custom_rsync}",
    $mysql_rsync_path        = "${dbserver::mysql::mysql_rsync_path}",
    $mysql_template_path     = "${dbserver::mysql::mysql_template_path}",
    
    $mysql_custom_param_1    = "${dbserver::mysql::mysql_custom_param_1}",
    $mysql_custom_param_2    = "${dbserver::mysql::mysql_custom_param_2}",
    $mysql_custom_param_3    = "${dbserver::mysql::mysql_custom_param_3}"
){
    
    if ($use_custom_rsync =~ /true/ ) {
        mysql::config{ "${db_home}" :
            server_type       => "${mysql_server_type}",
            server_id         => "${mysql_server_id}",
            root_username     => "${mysql_root_username}",
            root_password_org => "${mysql_root_password_org}",
            root_password     => "${mysql_root_password}",
            mng_username      => "${mysql_mng_username}",
            mng_password      => "${mysql_mng_password}",
            host_address      => "${mysql_host_address}",
            reset_slave       => "${mysql_reset_slave}",
            rsync_path        => "${mysql_rsync_path}",
            template_path     => "${mysql_template_path}",
            custom_param_1    => "${mysql_custom_param_1}",
            custom_param_2    => "${mysql_custom_param_2}",
            custom_param_3    => "${mysql_custom_param_3}",
        }

    }else{
        mysql::config{ "${db_home}" :
            server_type       => "${mysql_server_type}",
            server_id         => "${mysql_server_id}",
            root_username     => "${mysql_root_username}",
            root_password_org => "${mysql_root_password_org}",
            root_password     => "${mysql_root_password}",
            mng_username      => "${mysql_mng_username}",
            mng_password      => "${mysql_mng_password}",
            host_address      => "${mysql_host_address}",
            reset_slave       => "${mysql_reset_slave}",
            custom_param_1    => "${mysql_custom_param_1}",
            custom_param_2    => "${mysql_custom_param_2}",
            custom_param_3    => "${mysql_custom_param_3}",
        }
    }
    
    case $mysql_server_type { 
        "MASTER" : {
            #Create Sample Database 
            mysql::sampleDB{ "${db_home}" :  
                root_password => "${mysql_root_password}" ,
                require       => Mysql::Config[ "${db_home}" ]
            }
            
            case $mysql_usr_username {
                ''      : { }
                default : {
                    mysql::createsysUser{ "${mysql_usr_username}" :
                        password      => "${mysql_usr_password}" ,
                        root_password => "${mysql_root_password}",
                    }
                }
            }

            mysql::createDatabase{ "app" :
                username      => "${mysql_usr_username}",
                password      => "${mysql_usr_password}",
                root_password => "${mysql_root_password}",
            }
        }
        "SLAVE" : {
            if ($use_custom_rsync =~ /true/ ) {
            mysql::startSlave{ "${db_home}" :
                master_host   => "${mysql_master_host}",
                master_port   => "${mysql_master_port}",
                repl_username => "${mysql_repl_username}",
                repl_password => "${mysql_repl_password}",
                dump_username => "${mysql_dump_username}",
                dump_password => "${mysql_dump_password}",
                root_username => "${mysql_root_username}",
                root_password => "${mysql_root_password}",
                ssh_username  => "${mysql_ssh_username}",
                template_path => "${mysql_template_path}",
            }

            }else{
            mysql::startSlave{ "${db_home}" :
                master_host   => "${mysql_master_host}",
                master_port   => "${mysql_master_port}",
                repl_username => "${mysql_repl_username}",
                repl_password => "${mysql_repl_password}",
                dump_username => "${mysql_dump_username}",
                dump_password => "${mysql_dump_password}",
                root_username => "${mysql_root_username}",
                root_password => "${mysql_root_password}",
                ssh_username  => "${mysql_ssh_username}",
            }
            }
        }
        default : {}
    }
}

class dbserver::mysql::mount(
    $db_home      = "${dbserver::mysql::db_home}",
    $mysql_lvname = "${dbserver::mysql::mysql_lvname}",
    $mysql_lvsize = "${dbserver::mysql::mysql_lvsize}",
    $mysql_vgname = "${dbserver::mysql::mysql_vgname}",
    $mysql_vgdisk = "${dbserver::mysql::mysql_vgdisk}", 
    $mysql_visor  = "${dbserver::mysql::mysql_visor}"
) {

    if "${mysql_vgdisk}" == "" {
        file { "${db_home}":
            ensure => directory,
            owner  => "root",
            group  => "root",
            mode   => 755,
        }
        mount { "${db_home}" :
            ensure  => "absent",
            require => File["${db_home}"],
        }
    } else {
        lvm::config{ "${db_home}" :
            lvname  => "${mysql_lvname}",
            lvsize  => "${mysql_lvsize}",
            vgname  => "${mysql_vgname}",
            vgdisk  => "${mysql_vgdisk}",
            visor   => "${mysql_visor}",
        }
    }
}

class dbserver::mysql::stop (
    $db_home                 = "${dbserver::mysql::db_home}",
    $use_custom_rsync        = "${dbserver::mysql::use_custom_rsync}",
    $mysql_template_path     = "${dbserver::mysql::mysql_template_path}",
    $mysql_custom_param_1    = "${dbserver::mysql::mysql_custom_param_1}",
    $mysql_custom_param_2    = "${dbserver::mysql::mysql_custom_param_2}",
    $mysql_custom_param_3    = "${dbserver::mysql::mysql_custom_param_3}"
){
    
    if ($use_custom_rsync =~ /true/ ) {
        mysql::stop{ "${db_home}": 
            template_path => "${mysql_template_path}",
            custom_param_1    => "${mysql_custom_param_1}",
            custom_param_2    => "${mysql_custom_param_2}",
            custom_param_3    => "${mysql_custom_param_3}",
        }
    }else{
        mysql::stop{ "${db_home}": 
            custom_param_1    => "${mysql_custom_param_1}",
            custom_param_2    => "${mysql_custom_param_2}",
            custom_param_3    => "${mysql_custom_param_3}",
        }
    }
}

class dbserver::mysql::unmount (
    $db_home       = "${dbserver::mysql::db_home}",
    $mysql_lvname  = "${dbserver::mysql::mysql_lvname}",
    $mysql_lvsize  = "${dbserver::mysql::mysql_lvsize}",
    $mysql_vgname  = "${dbserver::mysql::mysql_vgname}",
    $mysql_vgdisk  = "${dbserver::mysql::mysql_vgdisk}", 
    $mysql_visor   = "${dbserver::mysql::mysql_visor}",
    $mysql_unmount = "${dbserver::mysql::mysql_unmount}"
) {
    
    if "${mysql_vgdisk}" == "" or "${mysql_unmount}" == "false" {
        exec { "killProc-${db_home}":
            command => "true",
        }
        mount { "${db_home}" :
            noop    => true,
            ensure  => "absent",
            require => Exec["killProc-${db_home}"],
        }
    } else {
        lvm::detach{ "${db_home}": 
            vgname => "${mysql_vgname}",
            lvname => "${mysql_lvname}",
            vgdisk => "${mysql_vgdisk}",
            visor  => "${mysql_visor}",
        }
    }
}
 
class dbserver::mysql::phpmyadmin {
    phpmyadmin::config{ "phpmyadmin" : }

}

class dbserver::mysql::phpmyadmin::stop {
    phpmyadmin::stop{ "phpmyadmin" : }
}

