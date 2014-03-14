class webserver {
    $web_home            = "/mnt/web"
}

class webserver::apache (
    $apache_service_name = "",
    $apache_vgdisk       = "/dev/sdj",
    $apache_lvsize       = "",
    $apache_unmount      = "true", #detach volume or not
    $apache_visor        = "", #hyper visor for mount or unmount volume
    $apache_custom_param_1 = "",
    $apache_custom_param_2 = "",
    $apache_custom_param_3 = ""
) inherits webserver {
#    $web_home            = "/mnt/web"
    $apache_vgname       = "VGweb1"
    $apache_lvname       = "LVweb01"
    $apache_rsync_server = "${servername}"
    
    #Check user-custom directory    
    $custom_rsync_path = "${rsync_userdata_dir}/${user_name}/${cloud_name}/${apache_service_name}" 
    $use_custom_rsync = generate("/etc/puppet/checkDir.sh","${custom_rsync_path}")
    if ($use_custom_rsync =~ /true/ ) {
        $apache_rsync_path = "/userdata/${user_name}/${cloud_name}/${apache_service_name}"
        $apache_template_path = "${custom_rsync_path}/templates"
        notify{ "using apache user-custom directory= ${custom_rsync_path}": }
    }

    include apache
    
}

class webserver::apache::resource (
    $web_home              = "${webserver::apache::web_home}",
    $apache_rsync_server   = "${webserver::apache::apache_rsync_server}",
    $use_custom_rsync      = "${webserver::apache::use_custom_rsync}",
    $apache_rsync_path      = "${webserver::apache::apache_rsync_path}",
    $apache_template_path  = "${webserver::apache::apache_template_path}",
    $apache_custom_param_1 = "${webserver::apache::apache_custom_param_1}",
    $apache_custom_param_2 = "${webserver::apache::apache_custom_param_2}",
    $apache_custom_param_3 = "${webserver::apache::apache_custom_param_3}"
) {

    if ($use_custom_rsync =~ /true/ ) {
        apache::config{ "${web_home}":
            rsync_server  => "${apache_rsync_server}",
            rsync_path    => "${apache_rsync_path}",
            template_path => "${apache_template_path}",
            custom_param_1 => "${apache_custom_param_1}",
            custom_param_2 => "${apache_custom_param_2}",
            custom_param_3 => "${apache_custom_param_3}",
        }
    }else{
        apache::config{ "${web_home}":
            rsync_server  => "${apache_rsync_server}",
            custom_param_1 => "${apache_custom_param_1}",
            custom_param_2 => "${apache_custom_param_2}",
            custom_param_3 => "${apache_custom_param_3}",
        }
    }
}

class webserver::apache::stop (
    $web_home              = "${webserver::apache::web_home}",
    $use_custom_rsync  = "${webserver::apache::use_custom_rsync}",
    $apache_template_path  = "${webserver::apache::apache_template_path}",
    $apache_custom_param_1 = "${webserver::apache::apache_custom_param_1}",
    $apache_custom_param_2 = "${webserver::apache::apache_custom_param_2}",
    $apache_custom_param_3 = "${webserver::apache::apache_custom_param_3}"
) {
#    notice ("apache_custom_param_1 = ${apache_custom_param_1}")
#    notice ("apache_custom_param_2 = ${apache_custom_param_2}")
#    notice ("apache_custom_param_3 = ${apache_custom_param_3}")
#    notice ("web_home = ${web_home}")
#    notice ("use_custom_rsync = ${use_custom_rsync}")
#    notice ("apache_template_path = ${apache_template_path}")
#    
#    notice ("webserver::apache::custom_rsync_path = ${webserver::apache::custom_rsync_path}")
#    notice ("webserver::apache::apache_rsync_server = ${webserver::apache::apache_rsync_server}")
#    notice ("webserver::apache::apache_rsync_path = ${webserver::apache::apache_rsync_path}")
    
    if ($use_custom_rsync =~ /true/ ) {
        apache::stop{ "${web_home}": 
            template_path  => "${apache_template_path}",
            custom_param_1 => "${apache_custom_param_1}",
            custom_param_2 => "${apache_custom_param_2}",
            custom_param_3 => "${apache_custom_param_3}",
        }
    } else {
        apache::stop{ "${web_home}":
            custom_param_1 => "${apache_custom_param_1}",
            custom_param_2 => "${apache_custom_param_2}",
            custom_param_3 => "${apache_custom_param_3}",
        }
    }
}

class webserver::apache::mount (
    $web_home = "${webserver::apache::web_home}",
    $apache_lvname = "${webserver::apache::apache_lvname}",
    $apache_lvsize = "${webserver::apache::apache_lvsize}",
    $apache_vgname = "${webserver::apache::apache_vgname}",
    $apache_vgdisk = "${webserver::apache::apache_vgdisk}",
    $apache_visor = "${webserver::apache::apache_visor}"
) {
#    notice ("apache_vgname = ${apache_vgname}")
#    notice ("apache_lvname = ${apache_lvname}")    
#    notice ("apache_vgdisk = ${apache_vgdisk}")
#    notice ("apache_lvsize = ${apache_lvsize}")
#    notice ("apache_visor = ${apache_visor}")
#    
#    notice ("webserver::apache::apache_unmount = ${webserver::apache::apache_unmount}")
#    notice ("apache_service_name = ${webserver::apache::apache_service_name}")
#    notice ("custom_rsync_path = ${webserver::apache::custom_rsync_path}")
#    notice ("use_custom_rsync = ${webserver::apache::use_custom_rsync}")
#    notice ("apache_rsync_path = ${webserver::apache::apache_rsync_path}")
#    notice ("apache_template_path = ${webserver::apache::apache_template_path}")
    
   if "${apache_vgdisk}" == "" {
        file { "${web_home}":
            ensure => directory,
            owner  => "root",
            group  => "root",
            mode   => 755,
        }
        mount { "${web_home}" :
            ensure  => "absent",
            require => File["${web_home}"],
        }
    } else {
        lvm::config{ "${web_home}":
            lvname  => "${apache_lvname}",
            lvsize  => "${apache_lvsize}",
            vgname  => "${apache_vgname}",
            vgdisk  => "${apache_vgdisk}",
            visor   => "${apache_visor}",
        }
    }
}

class webserver::apache::unmount (
    $web_home = "${webserver::apache::web_home}",
    $apache_vgname = "${webserver::apache::apache_vgname}",
    $apache_lvname = "${webserver::apache::apache_lvname}",
    $apache_vgdisk = "${webserver::apache::apache_vgdisk}",
    $apache_lvsize = "${webserver::apache::apache_lvsize}",
    $apache_visor = "${webserver::apache::apache_visor}",
    $apache_unmount = "${webserver::apache::apache_unmount}"
){
#    #this class's Argument
#    notice ("apache_vgname = ${apache_vgname}")
#    notice ("apache_lvname = ${apache_lvname}")    
#    notice ("apache_vgdisk = ${apache_vgdisk}")
#    notice ("apache_lvsize = ${apache_lvsize}")
#    notice ("apache_visor = ${apache_visor}")
#    notice ("apache_unmount = ${apache_unmount}")
#    
#    #webserver::apache's Argument
#    notice ("webserver::apache::apache_service_name = ${webserver::apache::apache_service_name}")
#    notice ("webserver::apache::custom_rsync_path = ${webserver::apache::custom_rsync_path}")
#    notice ("webserver::apache::use_custom_rsync = ${webserver::apache::use_custom_rsync}")
#    notice ("webserver::apache::apache_rsync_path = ${webserver::apache::apache_rsync_path}")
#    notice ("webserver::apache::apache_template_path = ${webserver::apache::apache_template_path}")
    
    if  "${apache_vgdisk}" == "" or "${apache_unmount}" == "false" {
        mount { "${web_home}" :
            noop    => true,
            ensure  => "absent",
            require => Exec["killProc-${web_home}"],
        }
        exec { "killProc-${web_home}":
            command => "true",
        }
    } else {
        lvm::detach{ "${web_home}":
            vgname => "${apache_vgname}",
            lvname => "${apache_lvname}",
            vgdisk => "${apache_vgdisk}",
            visor  => "${apache_visor}",
        }
    }
}
