class zabbix-agent {

}

define zabbix-agent::config(
    $zabbix_server
){
    file { "/etc/zabbix/zabbix_agent.conf" :
        mode => "644",
        owner => root,
        group => root,
        content => template("zabbix-agent/centos/zabbix_agent.conf.erb"),
        notify => Service["zabbix-agent"],
    }

    file { "/etc/zabbix/zabbix_agentd.conf" :
        mode => "644",
        owner => root,
        group => root,
        content => template("zabbix-agent/centos/zabbix_agentd.conf.erb"),
        notify => Service["zabbix-agent"],
    }

    service { "zabbix-agent":
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => [File["/etc/zabbix/zabbix_agent.conf"],File["/etc/zabbix/zabbix_agentd.conf"]],
    }
}

define zabbix-agent::config-windows(
    $zabbix_server
){

    file { "C:/Program Files/ZABBIX Agent/zabbix_agentd.conf" :
#        mode => "0664",
#        owner => Administrator,
#        group => Administrators,
        content => template("zabbix-agent/windows/zabbix_agentd.conf.erb"),
        before => Service["Zabbix Agent"],
        notify => Service["Zabbix Agent"],
    }

    service { "Zabbix Agent":
        enable     => "manual",
        ensure     => "running",
        hasrestart => true,
        hasstatus  => true,
        require    => [File["C:/Program Files/ZABBIX Agent/zabbix_agentd.conf"]],
    }
}

define zabbix-agent::stop-windows {
    service { "Zabbix Agent":
        enable     => "manual",
        ensure     => "stopped",
        hasstatus  => true,
    }
}

