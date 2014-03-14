class apache {
    exec { "reload-apache" : 
        command     => "service httpd reload",
        refreshonly => true,
        onlyif      => "sleep 1 && service httpd status",
    }
}

define apache::stop(
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

    $web_home=$name    
    $rsyncFlag = "${web_home}/${fact_rsyncflagfile}"
    $rsyncFlag_contents = "${web_home}/${fact_rsyncflagfile}_CONTENTS"

    service { "httpd":
        before  => [ Mount["${name}"], Exec["killProc-${name}"] ],
        enable  => false,
        ensure  => "stopped",
        hasstatus => true,
        stop      => "/sbin/service httpd stop && sleep 5",
    }

    if $template_path != "" {
        file { "${rsyncFlag}":
            before  => Service["httpd"],
            ensure  => "absent",
        }

        file { "${rsyncFlag_contents}":
            before  => Service["httpd"],
            ensure  => "absent",
        }
    }
}

define apache::config (
    $rsync_server  = "",
    $rsync_path    = "${rsync_default_path}/apache",
    $template_path = "apache",
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

    $web_home=$name
    $rsyncFlag = "${web_home}/${fact_rsyncflagfile}"
    $rsyncFlag_contents = "${web_home}/${fact_rsyncflagfile}_CONTENTS"

    file { "/etc/rc.d/init.d/httpd" :
        before  => Service[ "httpd" ],
        mode   => "755" ,
        owner  => "root" ,
        group  => "root" ,
        source => "puppet:///apache/httpd",
    }

    file { "${web_home}/logs" : 
        ensure  => directory,
        owner   => "root",
        group   => "root",
        mode    => "755",
        require => Mount["${web_home}"],
    }

    file { "/var/log/httpd" :
        before  => Service[ "httpd" ],
        ensure  => "${web_home}/logs",
        require => File["${web_home}/logs"],
        force   => true,
    }   

    file { "/etc/httpd/logs" :
        before  => Service[ "httpd" ],
        ensure  => "${web_home}/logs",
        require => File["${web_home}/logs"],
    }

    file { "/etc/httpd/conf" :
        before  => Service[ "httpd" ],
        ensure  => "${web_home}/conf",
        require => [ Exec["rsync-conf"], Exec["mv-httpd-conf"] ],
    }
    
    file { "/etc/httpd/conf.d" :
        before  => Service[ "httpd" ],
        ensure  => "${web_home}/conf.d",
        require => [ Exec["rsync-conf"], Exec["mv-httpd-conf.d"] ],
    }

    exec { "mv-httpd-conf": 
        before  => Service["httpd"] ,
        onlyif  => "test -d /etc/httpd/conf",
        command => "mv -f /etc/httpd/conf /tmp/ ",
        unless  => "test -L /etc/httpd/conf",
    }

    exec { "mv-httpd-conf.d":
        before  => Service["httpd"] ,
        onlyif  => "test -d /etc/httpd/conf.d",
        command => "mv -f /etc/httpd/conf.d /tmp/ ",
        unless  => "test -L /etc/httpd/conf.d",
    }

    service { "httpd":
        enable     => false,
        ensure     => "running",
        hasstatus  => true,
    }

    if ( $template_path == "apache" ) {
        #Default Value
        $replaceoption = false
    }else{
        #check rsync flag file exists
        $rsyncvariable="fact_rsyncstatus_${web_home}"
        $replaceoption=inline_template("<%= scope.lookupvar(rsyncvariable) %>") ? {
            "true"  => false,
            default => true,
        }
    }

    file { "/etc/httpd/conf/httpd.conf" :
        mode    =>  "644" ,
        owner   => "root" ,
        group   => "root" ,
        content => template("${template_path}/httpd.conf.erb"),
        require => Exec["rsync-conf"] ,
        replace => "${replaceoption}",
    }
    
    if $rsync_server != "" {
        exec { "rsync-html" :
            before  => Service["httpd"] ,
            command => "rsync -auqz rsync://${rsync_server}${rsync_path}/www ${web_home}  && touch ${rsyncFlag_contents}",
            unless  => "test -f ${rsyncFlag_contents}",
            require => Mount["${web_home}"],
        }
        exec { "rsync-conf" :
            before  => Service["httpd"] ,
            command => "rsync -auq rsync://${rsync_server}${rsync_path}/httpd/ ${web_home} && touch ${rsyncFlag}",
            unless  => "test -f ${rsyncFlag}",
            require => Mount["${web_home}"],
        }
    }else{
        exec { "rsync-html" : }
        exec { "rsync-conf" : }
    }

}

define apache::proxy_ajp( 
    $location , 
    $target , 
    $aphost = "localhost", 
    $apport = "8009", 
    $ensure = "enabled" )
{
    include apache

    $file = "/etc/httpd/conf.d/${name}.conf"

    file { $file :
        ensure   => $ensure ? {
        enabled  => present,
        disabled => absent },
        content  => template("apache/proxy_ajp.erb"),
        notify   => Exec["reload-apache"],
   }
}

define apache::proxy_ajp_balancer(
    $location,
    $target,
    $apservers,
    $ensure = "enabled" )
{
    include apache

    $file = "/etc/httpd/conf.d/${name}.conf"

    file { $file : 
        ensure   => $ensure ? {
                enabled  => present,
                disabled => absent
            },
        content  => template("apache/proxy_ajp_balancer.erb"),
        notify   => Exec["reload-apache"],
    }
}

