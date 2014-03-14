class iptables {

}

define iptables::config(
    $ip_list = ""
){
    file { "/tmp/iptables_config.sh" :
        mode   => "755",
        owner  => "root",
        group  => "root",
        source => "puppet:///iptables/iptables_config.sh",
    }

    exec { "configIptables" :
        command => "/tmp/iptables_config.sh ${ip_list}",
        require => File["/tmp/iptables_config.sh"],
        notify  => Service["iptables"],
    }

    service { "iptables" :
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
    }

}
