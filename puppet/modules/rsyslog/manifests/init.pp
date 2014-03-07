class rsyslog {

}

define rsyslog::config (
    $log_server ,
    $log_msgs = "*.*"  ,
    $log_proto = "tcp" ,
    $log_port = "514"
){

    $rserver = $log_server
    $msgs = $log_msgs 
    $proto = $log_proto 
    $rport = $log_port  

    service { "rsyslog":
        enable     => "true",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => File["/etc/rsyslog.conf"],
    }

    if tagged("prjserver"){
        $tempFile = "rsyslog/rsyslog.conf.prjserver.erb"
    } else {
        $tempFile = "rsyslog/rsyslog.conf.erb"
    }

    file { "/etc/rsyslog.conf":
        mode    => "644",
        owner   => root,
        group   => root,
        content => template("${tempFile}"),
        notify  => Service["rsyslog"],
    }

    file { "/etc/sysconfig/rsyslog":
        mode    => "644",
        owner   => root,
        group   => root,
        content => template("rsyslog/sysconfig_rsyslog.erb"),
        notify  => Service["rsyslog"],
    }

#   $syslog_pri="local1.info"
#   exec { "Log:rsyslog" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : rsyslog is running'",
#       require    => [Service["rsyslog"]]
#   }

}

