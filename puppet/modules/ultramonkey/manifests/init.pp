class ultramonkey {

}

define ultramonkey::stop(){
    service { "l7vsd":
        enable     => false,
        ensure     => "stopped",
    }

    service { "l7directord":
        before => Service["l7vsd"],
        enable     => false,
        ensure     => "stopped",
    }

#   $syslog_pri="local1.info"
#   exec { "Log:l7directord" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : l7directord is stopped'",
#       require    => [Service["l7directord"]]
#   }
}

define ultramonkey::config ( )
{

    $tempdir = "/tmp/ultramonkey"

    service { "l7vsd":
        enable     => false,
        ensure     => "running",
        hasrestart => true,
        hasstatus  => true,
        require    => [File["l7vs.cf"]]
    }

    service { "l7directord":
        enable     => false,
        ensure     => "running",
        hasrestart => true,
        status     => "/usr/sbin/l7directord status",
        require    => [ Service["l7vsd"], File["l7directord.cf"] ],
    }

    file { "l7directord.cf":
        path    => "${tempdir}/l7directord.cf",
        mode    => "644",
        owner   => root,
        group   => root,
        content => template("ultramonkey/l7directord.erb"),
    }

    file { "l7vs.cf" :
        path   => "/etc/l7vs/l7vs.cf",
        mode   => "644",
        owner  => root,
        group  => root,
        source => "puppet:///ultramonkey/l7vs.cf",
        notify => Service["l7vsd"],
    }

    file { "${tempdir}":
        ensure  => directory,
        owner   => root,
        group   => root,
        mode    => "755",
    }

    $lbconf = "/etc/ha.d/conf/l7directord.cf"
    exec { "make_lbconfig" :
        cwd       => "${tempdir}",
        command   => "cat l7directord.cf *.vscf 2>/dev/null > ${lbconf}  || true && rm -f *.vscf ",
        require   => [ File["${tempdir}"], File["l7directord.cf"] ],
        notify    => Exec["reload-l7directord"],
    }
    
    exec { "reload-l7directord" :
        command     => "service l7directord reload",
        refreshonly => true,
        require     => Service["l7directord"],
    }

#   $syslog_pri="local1.info"
#   exec { "Log:l7directord" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : l7directord is running'",
#       require    => [Service["l7directord"],Service["rsyslog"]]
#   }
}

define ultramonkey::virtual_server(
    $moduletype  = "sessionless",
    $scheduler   = "wrr",
    $maxconn     = "0",
    $checktimeout     = "5",
    $negotiatetimeout = "5",
    $checkinterval    = "10",
    $retryinterval    = "5",
    $checkcount       = "3",
    $checkport   = "",
    $checktype   = "connect",
    $service     = "none",
    $request     = "",
    $receive     = "",
    $real_servers
)
{
    #include ultramonkey

    $tempdir = "/tmp/ultramonkey"
    $filename = regsubst($name , ":" , "-port")

    file { "${tempdir}/${filename}.vscf" :
       before  => Exec["make_lbconfig"],
       ensure  => "present",
       content => template("ultramonkey/virtual_servers.erb"),
    }
}
