class ntp {

}

define ntp::config()
{

    $packagelist = ["ntp"]
    package { $packagelist: ensure => "installed"}

    file { "/etc/ntp.conf" :
        mode => "644",
        owner => root,
        group => root,
        source => "puppet:///ntp/ntp.conf",
        notify => Service["ntpd"],
    }

    service { "ntpd":
        enable     => "true",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => [Package[$packagelist],File["/etc/ntp.conf"],]
    }
   
#   $syslog_pri="local1.info"
#   exec { "Log:ntpd" :
#       command    => "logger -t puppet-manifest -p ${syslog_pri} '${fqdn} : ntpd is running'",
#       require    => [Service["ntpd"],Service["rsyslog"]]
#   }

}
