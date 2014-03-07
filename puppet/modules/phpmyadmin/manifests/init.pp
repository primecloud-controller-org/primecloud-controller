class phpmyadmin {

}

define phpmyadmin::stop(){
    service { "lighttpd":
        enable  => "false",
        ensure  => "stopped",
        hasstatus => true,
        stop      => "/sbin/service lighttpd stop && sleep 5",
    }

#    $syslog_pri="local1.info"
#    exec { "Log:phpmyadmin" :
#        command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : phpmyadmin(lighttpd) is stopped'",
#        require    => [Service["lighttpd"]]
#    }


}

define phpmyadmin::config(

){

    file { "/etc/lighttpd/lighttpd.conf" :
        mode => "640",
        owner => "root",
        group => "root",
        content => template("phpmyadmin/lighttpd.conf.erb"),
        notify => Service["lighttpd"],
    }

    file { "/usr/share/phpmyadmin/config.inc.php" :
        mode => "640",
        owner => "root",
        group => "lighttpd",
        content => template("phpmyadmin/config.inc.php.erb"),
        notify => Service["lighttpd"],
    }

    service { "lighttpd":
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => [File["/usr/share/phpmyadmin/config.inc.php"],File["/etc/lighttpd/lighttpd.conf"]],
    }

#   $syslog_pri="local1.info"
#   exec { "Log:phpmyadmin" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : phpmyadmin(lighttpd) is running'",
#       require    => [Service["lighttpd"],Service["rsyslog"]]
#   }
}

