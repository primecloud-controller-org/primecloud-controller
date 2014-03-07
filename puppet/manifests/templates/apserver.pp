class apserver {
    $ap_home = "/mnt/ap"
}

class apserver::geronimo (
    $geronimo_service_name = "", #pcc service name
    $geronimo_vgdisk       = "/dev/sdf",
    $geronimo_lvsize       = "",
    $geronimo_unmount      = "true", #detach volume or not
    $geronimo_visor        = "", #hyper visor for mount or unmount volume

    $geronimo_admin_username = "system",  #for geronimo.erb, groups.properties.erb, users.properties.erb
    $geronimo_admin_password = "manager", #for geronimo.erb, users.properties.erb
    $geronimo_user_username  = "system",  #for groups.properties.erb, users.properties.erb
    $geronimo_user_password  = "manager", #for users.properties.erb

    $geronimo_custom_param_1 = "",
    $geronimo_custom_param_2 = "",
    $geronimo_custom_param_3 = ""
)inherits apserver{
    $geronimo_version      = "geronimo-tomcat6-javaee5-2.2"
    $geronimo_vgname       = "VGap1"
    $geronimo_lvname       = "LVap01"
    $geronimo_session_type = "sticky"  #for server.xml.erb
    $geronimo_rsync_server = "${servername}" #puppet master fqdn

    #Check user-custom directory
    $custom_rsync_path = "${rsync_userdata_dir}/${user_name}/${cloud_name}/${geronimo_service_name}"
    $use_custom_rsync = generate("/etc/puppet/checkDir.sh","${custom_rsync_path}")
    if ($use_custom_rsync =~ /true/ ) {
        $geronimo_rsync_path = "/userdata/${user_name}/${cloud_name}/${geronimo_service_name}"
        $geronimo_template_path = "${custom_rsync_path}/templates"
        notify{ "using geronimo user-custom directory= ${custom_rsync_path}": }
    }
}

class apserver::geronimo::resource (
    $ap_home                 = "${apserver::geronimo::ap_home}",
    $use_custom_rsync        = "${apserver::geronimo::use_custom_rsync}",
    $geronimo_version        = "${apserver::geronimo::geronimo_version}",
    $geronimo_rsync_server   = "${apserver::geronimo::geronimo_rsync_server}",
    $geronimo_rsync_path     = "${apserver::geronimo::geronimo_rsync_path}",
    $geronimo_template_path  = "${apserver::geronimo::geronimo_template_path}",
    $geronimo_session_type   = "${apserver::geronimo::geronimo_session_type}",
    $geronimo_admin_username = "${apserver::geronimo::geronimo_admin_username}",
    $geronimo_admin_password = "${apserver::geronimo::geronimo_admin_password}",
    $geronimo_user_username  = "${apserver::geronimo::geronimo_user_username}",
    $geronimo_user_password  = "${apserver::geronimo::geronimo_user_password}",
    $geronimo_custom_param_1 = "${apserver::geronimo::geronimo_custom_param_1}",
    $geronimo_custom_param_2 = "${apserver::geronimo::geronimo_custom_param_2}",
    $geronimo_custom_param_3 = "${apserver::geronimo::geronimo_custom_param_3}"
){

    if ($use_custom_rsync =~ /true/ ) {
        geronimo22::config { "${ap_home}":
            version                 => "${geronimo_version}",
            rsync_server            => "${geronimo_rsync_server}",
            rsync_path              => "${geronimo_rsync_path}",
            template_path           => "${geronimo_template_path}",
            geronimo_session_type   => "${geronimo_session_type}",
            geronimo_admin_username => "${geronimo_admin_username}",
            geronimo_admin_password => "${geronimo_admin_password}",
            geronimo_user_username  => "${geronimo_user_username}",
            geronimo_user_password  => "${geronimo_user_password}",
            custom_param_1          => "${geronimo_custom_param_1}",
            custom_param_2          => "${geronimo_custom_param_2}",
            custom_param_3          => "${geronimo_custom_param_3}",
        }
    }else{
        geronimo22::config { "${ap_home}":
            version                 => "${geronimo_version}",
            rsync_server            => "${geronimo_rsync_server}",
            geronimo_session_type   => "${geronimo_session_type}",
            geronimo_admin_username => "${geronimo_admin_username}",
            geronimo_admin_password => "${geronimo_admin_password}",
            geronimo_user_username  => "${geronimo_user_username}",
            geronimo_user_password  => "${geronimo_user_password}",
            custom_param_1          => "${geronimo_custom_param_1}",
            custom_param_2          => "${geronimo_custom_param_2}",
            custom_param_3          => "${geronimo_custom_param_3}",
        }
    }
}

class apserver::geronimo::mount (
    $ap_home         = "${apserver::geronimo::ap_home}",
    $geronimo_lvname = "${apserver::geronimo::geronimo_lvname}",
    $geronimo_lvsize = "${apserver::geronimo::geronimo_lvsize}",
    $geronimo_vgname = "${apserver::geronimo::geronimo_vgname}",
    $geronimo_vgdisk = "${apserver::geronimo::geronimo_vgdisk}",
    $geronimo_visor  = "${apserver::geronimo::geronimo_visor}"
){
    if "${geronimo_vgdisk}" == "" {
        file { "${ap_home}":
            ensure => directory,
            owner  => "root",
            group  => "root",
            mode   => 755,
        }
        mount { "${ap_home}" :
            ensure  => "absent",
            require => File["${ap_home}"],
        }
    } else {
        lvm::config { "${ap_home}":
            lvname  => "${geronimo_lvname}",
            lvsize  => "${geronimo_lvsize}",
            vgname  => "${geronimo_vgname}",
            vgdisk  => "${geronimo_vgdisk}",
            visor   => "${geronimo_visor}",
        }
    }
}

class apserver::geronimo::stop (
    $ap_home                 = "${apserver::geronimo::ap_home}",
    $use_custom_rsync        = "${apserver::geronimo::use_custom_rsync}",
    $geronimo_template_path  = "${apserver::geronimo::geronimo_template_path}",
    $geronimo_custom_param_1 = "${apserver::geronimo::geronimo_custom_param_1}",
    $geronimo_custom_param_2 = "${apserver::geronimo::geronimo_custom_param_2}",
    $geronimo_custom_param_3 = "${apserver::geronimo::geronimo_custom_param_3}",
){

    if ($use_custom_rsync =~ /true/ ) {
        geronimo22::stop{ "${ap_home}":
            template_path => "${geronimo_template_path}",
            custom_param_1 => "${geronimo_custom_param_1}",
            custom_param_2 => "${geronimo_custom_param_2}",
            custom_param_3 => "${geronimo_custom_param_3}",
        }
    }else{
        geronimo22::stop{ "${ap_home}":
            custom_param_1 => "${geronimo_custom_param_1}",
            custom_param_2 => "${geronimo_custom_param_2}",
            custom_param_3 => "${geronimo_custom_param_3}",
        }
    }
}

class apserver::geronimo::unmount (
    $ap_home          = "${apserver::geronimo::ap_home}",
    $geronimo_lvname  = "${apserver::geronimo::geronimo_lvname}",
    $geronimo_lvsize  = "${apserver::geronimo::geronimo_lvsize}",
    $geronimo_vgname  = "${apserver::geronimo::geronimo_vgname}",
    $geronimo_vgdisk  = "${apserver::geronimo::geronimo_vgdisk}",
    $geronimo_visor   = "${apserver::geronimo::geronimo_visor}",
    $geronimo_unmount = "${apserver::geronimo::geronimo_unmount}"
) {

    if "${geronimo_vgdisk}" == "" or "${geronimo_unmount}" == "false"{
        exec { "killProc-${ap_home}":
            command => "true",
        }
        mount { "${ap_home}" :
            noop    => true,
            ensure  => "absent",
            require => Exec["killProc-${ap_home}"],
        }
    } else {
        lvm::detach{ "${ap_home}":
            vgname         => "${geronimo_vgname}",
            lvname         => "${geronimo_lvname}",
            vgdisk         => "${geronimo_vgdisk}",
            visor          => "${geronimo_visor}",
        }
    }
}

class apserver::tomcat (
    $tomcat_service_name   = "", #pcc service name
    $tomcat_vgdisk         = "/dev/sdf",
    $tomcat_lvsize         = "",
    $tomcat_unmount        = "true", #undetaching volume when service stopping
    $tomcat_visor          = "",  #hyper visor for mount or unmount volume

    $tomcat_admin_username = "admin", #for tomcat-users.xml.erb
    $tomcat_admin_password = "admin", #for tomcat-users.xml.erb
    $tomcat_user_username  = "admin", #for tomcat-users.xml.erb
    $tomcat_user_password  = "admin", #for tomcat-users.xml.erb

    $tomcat_custom_param_1 ="",
    $tomcat_custom_param_2 ="",
    $tomcat_custom_param_3 =""

) inherits apserver {
    $tomcat_version        = "apache-tomcat-6.0.37"
    $tomcat_vgname         = "VGap1"
    $tomcat_lvname         = "LVap01"
    $tomcat_session_type   = "sticky" #for server.xml.erb
    $tomcat_rsync_server   = "${servername}" #puppet master fqdn

    $custom_rsync_path = "${rsync_userdata_dir}/${user_name}/${cloud_name}/${tomcat_service_name}"
    $use_custom_rsync = generate("/etc/puppet/checkDir.sh","${custom_rsync_path}")
    if ($use_custom_rsync =~ /true/ ) {
        $tomcat_rsync_path = "/userdata/${user_name}/${cloud_name}/${tomcat_service_name}"
        $tomcat_template_path = "${custom_rsync_path}/templates"
        notify{ "using tomcat user-custom directory = ${custom_rsync_path}": }
    }
}

class apserver::tomcat::resource (
    $ap_home = "${apserver::tomcat::ap_home}",
    $tomcat_version        = "${apserver::tomcat::tomcat_version}",
    $tomcat_rsync_server   = "${apserver::tomcat::tomcat_rsync_server}",
    $tomcat_session_type   = "${apserver::tomcat::tomcat_session_type}",
    $tomcat_admin_username = "${apserver::tomcat::tomcat_admin_username}",
    $tomcat_admin_password = "${apserver::tomcat::tomcat_admin_password}",
    $tomcat_user_username  = "${apserver::tomcat::tomcat_user_username}",
    $tomcat_user_password  = "${apserver::tomcat::tomcat_user_password}",
    $use_custom_rsync      = "${apserver::tomcat::use_custom_rsync}",
    $tomcat_rsync_path     = "${apserver::tomcat::tomcat_rsync_path}",
    $tomcat_template_path  = "${apserver::tomcat::tomcat_template_path}",
    $tomcat_custom_param_1 = "${apserver::tomcat::tomcat_custom_param_1}",
    $tomcat_custom_param_2 = "${apserver::tomcat::tomcat_custom_param_2}",
    $tomcat_custom_param_3 = "${apserver::tomcat::tomcat_custom_param_3}"
){

    if ($use_custom_rsync =~ /true/ ) {
        tomcat::config { "${ap_home}":
            version                 => "${tomcat_version}",
            rsync_server            => "${tomcat_rsync_server}",
            rsync_path              => "${tomcat_rsync_path}",
            template_path           => "${tomcat_template_path}",
            tomcat_session_type     => "${tomcat_session_type}",
            tomcat_admin_username   => "${tomcat_admin_username}",
            tomcat_admin_password   => "${tomcat_admin_password}",
            tomcat_user_username    => "${tomcat_user_username}",
            tomcat_user_password    => "${tomcat_user_password}",
            tomcat_version          => "${tomcat_version}",
            custom_param_1          => "${tomcat_custom_param_1}",
            custom_param_2          => "${tomcat_custom_param_2}",
            custom_param_3          => "${tomcat_custom_param_3}",
        }
    }else{
        tomcat::config { "${ap_home}":
            version                 => "${tomcat_version}",
            rsync_server            => "${tomcat_rsync_server}",
            tomcat_session_type     => "${tomcat_session_type}",
            tomcat_admin_username   => "${tomcat_admin_username}",
            tomcat_admin_password   => "${tomcat_admin_password}",
            tomcat_user_username    => "${tomcat_user_username}",
            tomcat_user_password    => "${tomcat_user_password}",
            tomcat_version          => "${tomcat_version}",
            custom_param_1          => "${tomcat_custom_param_1}",
            custom_param_2          => "${tomcat_custom_param_2}",
            custom_param_3          => "${tomcat_custom_param_3}",
        }
    }
}

class apserver::tomcat::mount (
    $ap_home       = "${apserver::tomcat::ap_home}",
    $tomcat_lvname = "${apserver::tomcat::tomcat_lvname}",
    $tomcat_lvsize = "${apserver::tomcat::tomcat_lvsize}",
    $tomcat_vgname = "${apserver::tomcat::tomcat_vgname}",
    $tomcat_vgdisk = "${apserver::tomcat::tomcat_vgdisk}",
    $tomcat_visor  = "${apserver::tomcat::tomcat_visor}"
){

    if "${tomcat_vgdisk}" == "" {
        file { "${ap_home}":
            ensure => directory,
            owner  => "root",
            group  => "root",
            mode   => 755,
        }
        mount { "${ap_home}" :
            ensure  => "absent",
            require => File["${ap_home}"],
        }
    } else {
        lvm::config { "${ap_home}":
            lvname  => "${tomcat_lvname}",
            lvsize  => "${tomcat_lvsize}",
            vgname  => "${tomcat_vgname}",
            vgdisk  => "${tomcat_vgdisk}",
            visor   => "${tomcat_visor}",
        }
    }
}

class apserver::tomcat::stop (
    $ap_home = "${apserver::tomcat::ap_home}",
    $use_custom_rsync = "${apserver::tomcat::use_custom_rsync}",
    $tomcat_rsync_path = "${apserver::tomcat::tomcat_rsync_path}",
    $tomcat_template_path = "${apserver::tomcat::tomcat_template_path}",
    $tomcat_custom_param_1 = "${apserver::tomcat::tomcat_custom_param_1}",
    $tomcat_custom_param_2 = "${apserver::tomcat::tomcat_custom_param_2}",
    $tomcat_custom_param_3 = "${apserver::tomcat::tomcat_custom_param_3}"
){

    if ($use_custom_rsync =~ /true/ ) {
        tomcat::stop{ "${ap_home}":
            template_path  => "${tomcat_template_path}",
            custom_param_1 => "${tomcat_custom_param_1}",
            custom_param_2 => "${tomcat_custom_param_2}",
            custom_param_3 => "${tomcat_custom_param_3}",
        }
    }else{
        tomcat::stop{ "${ap_home}":
            custom_param_1 => "${tomcat_custom_param_1}",
            custom_param_2 => "${tomcat_custom_param_2}",
            custom_param_3 => "${tomcat_custom_param_3}",
        }
    }
}

class apserver::tomcat::unmount (
    $ap_home        = "${apserver::tomcat::ap_home}",
    $tomcat_lvname  = "${apserver::tomcat::tomcat_lvname}",
    $tomcat_lvsize  = "${apserver::tomcat::tomcat_lvsize}",
    $tomcat_vgname  = "${apserver::tomcat::tomcat_vgname}",
    $tomcat_vgdisk  = "${apserver::tomcat::tomcat_vgdisk}",
    $tomcat_visor   = "${apserver::tomcat::tomcat_visor}",
    $tomcat_unmount = "${apserver::tomcat::tomcat_unmount}"
) {

    if "${tomcat_vgdisk}" == "" or "${tomcat_unmount}" == "false" {
        exec { "killProc-${ap_home}":
            command => "true",
        }
        mount { "${ap_home}" :
            noop    => true,
            ensure  => "absent",
            require => Exec["killProc-${ap_home}"],
        }
    } else {
        lvm::detach{ "${ap_home}":
            vgname => "${tomcat_vgname}",
            lvname => "${tomcat_lvname}",
            vgdisk => "${tomcat_vgdisk}",
            visor  => "${tomcat_visor}",
        }
    }
}

