define basenode::node_user (
    $password,
    $groups = ""
){
      
    if $groups {
        group { [$groups] :
            before  => User["${name}"],
            ensure  => "present",
        }
        
        user { "${name}":
            home       => "/home/${name}",
            groups     => $groups,
            shell      => "/bin/bash",
            managehome => true,
            ensure     => "present",
        }
    } else {
        user { "${name}":
            home       => "/home/${name}",
            shell      => "/bin/bash",
            managehome => true,
            ensure     => "present",
        }
    }

    exec { "setpassword-${name}":
        command => "echo '${name}:${password}' | chpasswd ",
        onlyif  => "grep '^${name}' /etc/passwd && test ! -f /var/prjserver/.forCSKGROUP",
        require => User["${name}"],
    }
}

class basenode (
    $zabbix_server      = "pccdemo.lab.primecloud.jp",
    $zabbix_hostname    = "${fqdn}",
    $rsyslog_log_server = "pccdemo.lab.primecloud.jp",
    $rsyslog_log_msgs   = "*.notice,kern.info,local1.info",
    $rsyslog_log_proto  = "tcp",
    $rsyslog_log_port   = "514",
    $puppet_ssh_user = "puppetmaster",
    $puppet_ssh_pass = "9rUDpSkbn",
    $ssh_usr_username = "",
    $ssh_usr_password = ""
) {

    host {"${hostname}" :
      alias        => "${fqdn}",
      host_aliases => "${fqdn}",
      ip           => "${ipaddress}",
    }
    
    if "${operatingsystem}" == "CentOS" {
        file {"/var/log/messages" :
          owner => "root",
          group => "root",
          mode  => "640",
        }
    }
}

class basenode::resource (
    $puppet_ssh_user    = "${basenode::puppet_ssh_user}",
    $puppet_ssh_pass    = "${basenode::puppet_ssh_pass}",
    $ssh_usr_username   = "${basenode::ssh_usr_username}",
    $ssh_usr_password   = "${basenode::ssh_usr_password}",
    $zabbix_server      = "${basenode::zabbix_server}",
    $zabbix_hostname    = "${basenode::zabbix_hostname}",
    $rsyslog_log_server = "${basenode::rsyslog_log_server}",
    $rsyslog_log_msgs   = "${basenode::rsyslog_log_msgs}",
    $rsyslog_log_proto  = "${basenode::rsyslog_log_proto}",
    $rsyslog_log_port   = "${basenode::rsyslog_log_port}"
)
{
    basenode::node_user {"${puppet_ssh_user}" : 
        password => "${puppet_ssh_pass}",
        require  => [Class["Basenode"]],
    }

    case "${ssh_usr_username}" {
        ""      : {}
        default : {
            basenode::node_user {"${ssh_usr_username}" : 
                password => "${ssh_usr_password}",
                groups   => ["webmaster","tomcat"],
                require  => [Class["Basenode"]],
            }
            }
        }
    
    ntp::config{ "Default Config" : 
        require  => [Class["Basenode"], Basenode::Node_user ["${puppet_ssh_user}"]],
    }

    zabbix-agent::config{ "Default Config" : 
        zabbix_server   => "${zabbix_server}",
        zabbix_hostname => "${zabbix_hostname}",
        require         => [
            Class["Basenode"], 
            Basenode::Node_user ["${puppet_ssh_user}"],
            Ntp::Config ["Default Config"]],
    }

    rsyslog::config {"Default Config" :
        log_server => "${rsyslog_log_server}",
        log_msgs   => "${rsyslog_log_msgs}",
        log_proto  => "${rsyslog_log_proto}",
        log_port   => "${rsyslog_log_port}",
        require    => [
            Class["Basenode"], 
            Basenode::Node_user ["${puppet_ssh_user}"],
            Ntp::Config ["Default Config"],
            Zabbix-agent::Config["Default Config"]],
    }
}

class basenode::stop {

}

class basenode::resource-windows (
    $zabbix_server      = "${basenode::zabbix_server}",
    $zabbix_hostname    = "${basenode::zabbix_hostname}",
    $rsyslog_log_server = "${basenode::rsyslog_log_server}"
)
{

    zabbix-agent::config-windows { "Default Config" : 
        zabbix_server => "${zabbix_server}",
        zabbix_hostname => "${zabbix_hostname}",
        require         => [Class["Basenode"]],
    }
    ntsyslog::config { "Default Config" :
        log_server => "${rsyslog_log_server}",
        require    => [Class["Basenode"], Zabbix-Agent::Config-windows["Default Config"]],
    }
}

class basenode::stop-windows {
#    zabbix-agent::stop-windows{"Stop-Zabbix":}
#    ntsyslog::stop{"Stop-NTsyslog":}
}
