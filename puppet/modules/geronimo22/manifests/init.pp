class geronimo22 {
#    $default_geronimo_version="geronimo-tomcat6-javaee5-2.2"
}

define geronimo22::stop(
    $template_path  = "",
    $custom_param_1 = "",
    $custom_param_2 = "",
    $custom_param_3 = ""
){
    notice ("custom_param_1 = ${custom_param_1}")
    notice ("custom_param_2 = ${custom_param_2}")
    notice ("custom_param_3 = ${custom_param_3}")
    notice ("all_db_servers = ${all_db_servers}")
    notice ("all_ap_servers = ${all_ap_servers}")
    notice ("all_web_servers = ${all_web_servers}")
    notice ("db_servers = ${db_servers}")
    notice ("ap_servers = ${ap_servers}")
    notice ("web_servers = ${web_servers}")
    
    $ap_home = $name
    $rsyncFlag = "${ap_home}/${fact_rsyncflagfile}_geronimo22" #see modules/custom/liv/facter/fact_rsyncstatus.rb

    service { "geronimo":
        before  => [ Mount["${name}"],Exec["killProc-${name}"]],
        enable  => false,
        ensure  => "stopped",
        hasstatus => true,
        stop      => "/sbin/service geronimo stop && sleep 5",
    }


    if $template_path != "" {
        file { "${rsyncFlag}":
            before  => Service["geronimo"],
            ensure  => "absent",
        }
    }
}

define geronimo22::config(
    $version ,
    $rsync_server  = "",
    $rsync_path    = "${rsync_default_path}/geronimo",
    $template_path = "geronimo22",
    $geronimo_session_type,   #for server.xml.erb
    $geronimo_admin_username, #for geronimo.erb, groups.properties.erb, users.properties.erb
    $geronimo_admin_password, #for geronimo.erb, users.properties.erb
    $geronimo_user_username,  #for geronimo.erb, users.properties.erb
    $geronimo_user_password,  #for users.properties.erb
    $custom_param_1 = "",
    $custom_param_2 = "",
    $custom_param_3 = ""
){
    notice ("custom_param_1 = ${custom_param_1}")
    notice ("custom_param_2 = ${custom_param_2}")
    notice ("custom_param_3 = ${custom_param_3}")
    notice ("all_db_servers = ${all_db_servers}")
    notice ("all_ap_servers = ${all_ap_servers}")
    notice ("all_web_servers = ${all_web_servers}")
    notice ("db_servers = ${db_servers}")
    notice ("ap_servers = ${ap_servers}")
    notice ("web_servers = ${web_servers}")
    
    $ap_home = $name
    $geronimo_home="${ap_home}/${version}"
    $rsyncFlag = "${ap_home}/${fact_rsyncflagfile}_geronimo22"

    service { "geronimo":
        enable     => false,
        ensure     => "running",
        hasrestart => true,
        hasstatus  => true,
        require    =>  [ File["${ap_home}/default"] , File["/opt/geronimo" ], File["config-substitutions.properties"] , Exec["rsync-geronimo"] , File["/etc/sysconfig/geronimo"]  ]
    }

    if $rsync_server {
        exec { "rsync-geronimo" :
            command => "rsync -auqz rsync://${rsync_server}${rsync_path}/${version} ${ap_home}/  && touch ${rsyncFlag}",
            unless  => "test -f ${rsyncFlag}",
            require => Mount["${ap_home}"],
        }

        file { "${ap_home}/var" :
            ensure  => "directory",
            owner   => "geronimo",
            group   => "geronimo",
            mode    => "755",
            require => Mount["${ap_home}"],
        }
    } else {
        exec { "rsync-geronimo" : }
    }

    file { "${geronimo_home}" :
        ensure  => directory,
        owner   => "geronimo",
        group   => "geronimo",
        mode    => "755",
        require => [ Mount["${ap_home}"], Exec["rsync-geronimo"] ],
        notify  => Exec["chown-${geronimo_home}"],
    }

    exec { "chown-${geronimo_home}" :
        command     => "chown geronimo:geronimo -R ${geronimo_home}",
        refreshonly => true,
    }

    file { "${ap_home}/default" :
        ensure  => "${geronimo_home}",
        require => File["${geronimo_home}"],
    }
 
    file { "/opt/geronimo" :
        ensure => "${ap_home}",
        require => Mount["${ap_home}"],
    }

    if ( $template_path == "geronimo22" ) {
        #Default Value
        $replaceoption = false
    }else{
        #check rsync flag file exists
        $rsyncvariable="fact_rsyncstatus_${ap_home}"
        $replaceoption=inline_template("<%= scope.lookupvar(rsyncvariable) %>") ? {
            "true"  => false,
            default => true,
        }
    }

    file { "config-substitutions.properties" :
        path    => "${geronimo_home}/var/config/config-substitutions.properties" ,
        content => template("${template_path}/config-substitutions.properties.erb") ,
        owner   => "geronimo",
        group   => "geronimo",
        require => Exec["rsync-geronimo"],
        replace => "${replaceoption}",
    }

    file { "server.xml" :
        before  => Service["geronimo"],
        path    => "${geronimo_home}/var/catalina/server.xml" ,
        content => template("${template_path}/server.xml.erb") ,
        owner   => "geronimo",
        group   => "geronimo",
        require => Exec["rsync-geronimo"],
        replace => "${replaceoption}",
    }

    file { "/etc/sysconfig/geronimo" :
        content => template("${template_path}/geronimo.erb") ,
        owner   => "root",
        group   => "root",
        mode    => "755",
        replace => "${replaceoption}",
    }

    file { "/etc/init.d/geronimo" :
        before  => Service["geronimo"],
        owner   => "root",
        group   => "root",
        mode    => "755",
        source  => "puppet:///geronimo22/geronimo" ,
        require => File["/etc/sysconfig/geronimo"],
        #replace => "${replaceoption}",
    }

    file { "users.properties" :
        before  => Service["geronimo"],
        path    => "${geronimo_home}/var/security/users.properties" ,
        content => template("${template_path}/users.properties.erb") ,
        owner   => "geronimo",
        group   => "geronimo",
        require => Exec["rsync-geronimo"],
        replace => "${replaceoption}",
    }

    file { "groups.properties" :
        before  => Service["geronimo"],
        path    => "${geronimo_home}/var/security/groups.properties" ,
        content => template("${template_path}/groups.properties.erb") ,
        owner   => "geronimo",
        group   => "geronimo",
        require => Exec["rsync-geronimo"],
        replace => "${replaceoption}",
    }

    file { "/etc/logrotate.d/geronimo" :
        owner   => "root",
        group   => "root",
        mode    => "644",
        content => template("${template_path}/logrotate_geronimo.erb"),
        replace => "${replaceoption}",
    }

}

define geronimo22::dbhost ( $ip , $ap_restart=true ){
    if $ap_restart=="true"{
        host { $name :  
            ip  => $ip , 
#            notify => Service["geronimo"] 
        }
    }else{
        host { $name :  ip  => $ip }
    }
}

define geronimo22::createDataSource (
    $ap_home,
    $ip,
    $username,
    $password,
    $admin_username,
    $admin_password
){
    $file = "/opt/geronimo/default/var/temp/datasource-${name}-plan.xml"
    $gsh  = "/opt/geronimo/default/bin/gsh"
    $rar  = "/opt/geronimo/default/repository/org/tranql/tranql-connector-ra/1.5/tranql-connector-ra-1.5.rar"

    file { $file :
        content => template("geronimo22/datasource-plan.xml.erb"),
        owner   => "geronimo",
        group   => "geronimo",
        mode    => "644",
        require => [ Exec["rsync-geronimo"], File["/opt/geronimo" ], File["${ap_home}/default"] ],
    }

    exec { "deploy-datasource-${name}" :
        command => "sleep 5 && ${gsh} -c \"deploy/deploy -u ${admin_username} -w ${admin_password} ${rar} ${file}\"",
        onlyif  => "test `${gsh} -c \"deploy/list-modules -u ${admin_username} -w ${admin_password}\" | grep console.dbpool/datasource-${name}/1.0/car -c` -eq 0",
        require => Service["geronimo"],
    }
}

