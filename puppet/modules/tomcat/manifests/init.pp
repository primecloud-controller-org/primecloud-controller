class tomcat {
#    $default_tomcat_version="apache-tomcat-6.0.26"
}

define tomcat::stop(
    $template_path = "",
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
    $rsyncFlag = "${ap_home}/${fact_rsyncflagfile}" #see modules/custom/liv/facter/fact_rsyncstatus.rb
    
    service { "tomcat":
        before  => [ Mount["${name}"],Exec["killProc-${name}"] ],
        enable  => "false",
        ensure  => "stopped",
        hasstatus => true,
        stop      => "/sbin/service tomcat stop && sleep 5",
    }

    if $template_path != "" {
        file { "${rsyncFlag}":
            before  => Mount["${name}"],
            ensure  => "absent",
        }
    }
}

define tomcat::config(
    $version ,
    $rsync_server  = "",
    $rsync_path    = "$rsync_default_path/tomcat",
    $template_path = "tomcat",
    $tomcat_session_type  = "",   #for server.xml.erb
    $tomcat_admin_username = "",  #for tomcat-users.xml.erb
    $tomcat_admin_password = "",  #for tomcat-users.xml.erb
    $tomcat_user_username  = "",  #for tomcat-users.xml.erb
    $tomcat_user_password  = "",  #for tomcat-users.xml.erb
    $tomcat_version = "" ,

    $custom_param_1 ="",
    $custom_param_2 ="",
    $custom_param_3 =""
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
    $tomcat_home="${ap_home}/${tomcat_version}"
    $rsyncFlag = "${ap_home}/${fact_rsyncflagfile}" #see site.pp

    service { "tomcat":
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    =>  [ File["${ap_home}/default"] , File["/opt/tomcat" ], Exec["rsync-tomcat"], File["server.xml"], File["/etc/sysconfig/tomcat"], File["${ap_home}/default/bin/jsvc"]  ]
    }

    if ( $architecture == "i386" ) {
         file { "${ap_home}/default/bin/jsvc" :
                before  => Service["tomcat"],
                owner   => "tomcat",
                group   => "tomcat",
                mode    => "755",
                source  => "puppet:///modules/tomcat/jsvc.i386",
                require => Exec["rsync-tomcat"],
        }
    } else {
        file { "${ap_home}/default/bin/jsvc" :
                before  => Service["tomcat"],
                owner   => "tomcat",
                group   => "tomcat",
                mode    => "755",
                source  => "puppet:///modules/tomcat/jsvc",
                require => Exec["rsync-tomcat"],
        }
    }

    if $rsync_server {
            exec { "rsync-tomcat" :
                command => "rsync -auqz rsync://${rsync_server}${rsync_path}/${tomcat_version} ${ap_home}/ && touch ${rsyncFlag}",
                unless  => "test -f ${rsyncFlag}",
                require => Mount["${ap_home}"],
            }

            file { "${ap_home}/var" :
                ensure  => "directory",
                owner   => "tomcat",
                group   => "tomcat",
                mode    => "755",
                require => Mount["${ap_home}"],
            }
    } else {
        exec { "rsync-tomcat" : }
    }

    file { "${tomcat_home}" :
        ensure  => directory,
        owner   => "tomcat",
        group   => "tomcat",
        mode    => "755",
        require => [ Mount["${ap_home}"], Exec["rsync-tomcat"] ],
#        recurse => true,
        notify  => Exec["chown-${tomcat_home}"],
    }

    exec { "chown-${tomcat_home}" :
        command  => "chown tomcat:tomcat -R ${tomcat_home}",
        require => File["${tomcat_home}"],
        before => File["${ap_home}/default"],
#        refreshonly => true,
    }

    file { "${ap_home}/default" :
        ensure  => "${tomcat_home}",
        require => File["${tomcat_home}"],
    }
 
    file { "/opt/tomcat" :
        ensure => "${ap_home}",
        require => Mount["${ap_home}"],
    }

    if ( $template_path == "tomcat" ) {
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

    file { "server.xml" :
        path    => "${tomcat_home}/conf/server.xml" ,
        content => template("${template_path}/server.xml.erb") ,
        owner   => "tomcat",
        group   => "tomcat",
        require => Exec["rsync-tomcat"],
        replace => "${replaceoption}",
    }

    file { "/etc/sysconfig/tomcat" :
        content => template("${template_path}/tomcat.erb") ,
        replace => "${replaceoption}",
    }

    file { "/etc/init.d/tomcat" :
        before  => Service["tomcat"],
        owner   => "root",
        group   => "root",
        mode    => "755",
        source => "puppet:///modules/tomcat/tomcat" ,
        #replace => "${replaceoption}",
    }

    file { "tomcat-users.xml" :
        before  => Service["tomcat"],
        path    => "${tomcat_home}/conf/tomcat-users.xml" ,
        content => template("${template_path}/tomcat-users.xml.erb") ,
        owner   => "tomcat",
        group   => "tomcat",
        require => Exec["rsync-tomcat"],
        replace => "${replaceoption}",
    }

    file { "/etc/logrotate.d/tomcat" :
        owner   => "root",
        group   => "root",
        mode    => "644",
        content => template("${template_path}/logrotate_tomcat.erb"),
        replace => "${replaceoption}",
    }
}

define tomcat::dbhost ( $ip , $ap_restart=true ){
    if $ap_restart=="true" {
        host { $name :  
            ip  => $ip , 
#            notify => Service["tomcat"] 
        }
    }else{
        host { $name :  ip  => $ip }
    }
}

define tomcat::createDataSource (
    $dbservers,
    $ap_home
){
    $file = "/opt/tomcat/default/conf/Catalina/localhost/context.xml.default"

    file { $file :
        content => template("tomcat/context.xml.default.erb"),
        owner   => "tomcat",
        group   => "tomcat",
        mode    => "644",
        require => [ Exec["rsync-tomcat"], File["/opt/tomcat" ], File["${ap_home}/default"] ],
    }
}

