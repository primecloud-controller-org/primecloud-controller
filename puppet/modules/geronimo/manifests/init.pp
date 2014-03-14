class geronimo {
#    $default_geronimo_version="geronimo-tomcat6-javaee5-2.1.4"
}

define geronimo::stop(){
    service { "geronimo":
        before  => [ Mount["${name}"],Exec["killProc-${name}"]], 
        enable  => "false",
        ensure  => "stopped",
        hasstatus => true,
        stop      => "/sbin/service geronimo stop && sleep 5",
    }
    
#    $syslog_pri="local1.info"
#    exec { "Log:geronimo" :
#        command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : geronimo is stopped'",
#        require    => [Service["geronimo"]]
#    }


}

define geronimo::config(
    $version ,
    $rsync_server = "" 
){
    $ap_home = $name
    $geronimo_home="${ap_home}/${geronimo_version}"

    service { "geronimo":
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    =>  [ File["${ap_home}/default"] , File["/opt/geronimo" ], File["config-substitutions.properties"] , Exec["rsync-geronimo"] , File["/etc/sysconfig/geronimo"]  ]
    }

    case $rsync_server {
        ""      : {
            exec { "rsync-geronimo" : }
        }
        default : {
            exec { "rsync-geronimo" :
                command     => "rsync -auq rsync://${rsync_server}/data/${geronimo_version} ${ap_home}/",
                unless      => "test -d ${geronimo_home}/META-INF",
                require     => Mount["${ap_home}"],
            }
        }
    }

    file { "${geronimo_home}" :
        ensure  => directory,
        owner   => "geronimo",
        group   => "geronimo",
        mode    => "755",
        require => Mount["${ap_home}"],
    }

    file { "${ap_home}/default" :
        ensure  => "${geronimo_home}",
        require => File["${geronimo_home}"],
    }
 
    file { "/opt/geronimo" :
        ensure => "${ap_home}",
        require => Mount["${ap_home}"],
    }

    file { "config-substitutions.properties" :
        name    => "${geronimo_home}/var/config/config-substitutions.properties" ,
        content => template("geronimo/config-substitutions.properties.erb") ,
        require => Exec["rsync-geronimo"],
    }

    file { "/etc/sysconfig/geronimo" :
        content => template("geronimo/geronimo.erb") ,
    }

    file { "/etc/init.d/geronimo" :
        before  => Service["geronimo"],
        owner   => "root",
        group   => "root",
        mode    => "755",
        source => "puppet:///geronimo/geronimo" ,
    }


#   $syslog_pri="local1.info"
#   exec { "Log:geronimo" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : geronimo is running'",
#       require    => [Service["geronimo"],Service["rsyslog"]]
#   }
 
}

define geronimo::dbhost ( $ip , $ap_restart=true ){
    if $ap_restart=="true"{
        host { $name :  
            ip  => $ip , 
            notify => Service["geronimo"] 
        }
    }else{
        host { $name :  ip  => $ip }
    }
}
