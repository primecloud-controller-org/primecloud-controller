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
    $zabbix_server      = "pcc.primecloud.jp",
    $rsyslog_log_server = "pcc.primecloud.jp",
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
    $rsyslog_log_server = "${basenode::rsyslog_log_server}",
    $rsyslog_log_msgs   = "${basenode::rsyslog_log_msgs}",
    $rsyslog_log_proto  = "${basenode::rsyslog_log_proto}",
    $rsyslog_log_port   = "${basenode::rsyslog_log_port}",
)
{
    basenode::node_user {"${puppet_ssh_user}" : password => "${puppet_ssh_pass}"}

    ntp::config{ "Default Config" : }

    zabbix-agent::config{  "Default Config" : zabbix_server => "${zabbix_server}"}

    rsyslog::config {"Default Config" :
        log_server => "${rsyslog_log_server}",
        log_msgs   => "${rsyslog_log_msgs}",
        log_proto  => "${rsyslog_log_proto}",
        log_port   => "${rsyslog_log_port}",
    }

    case "${ssh_usr_username}" {
        ""      : {}
        default : {
            basenode::node_user {"${ssh_usr_username}" :
                password => "${ssh_usr_password}",
                groups   => ["webmaster","tomcat"],
            }
        }
    }
}

class basenode::stop {

}

class basenode::resource-windows (
    $zabbix_server      = "${basenode::zabbix_server}",
    $rsyslog_log_server = "${basenode::rsyslog_log_server}"
)
{

    zabbix-agent::config-windows { "Default Config" :
        zabbix_server => "${zabbix_server}",
        require   => [Host["${hostname}"]],
    }
    ntsyslog::config { "Default Config" :
        log_server => "${rsyslog_log_server}",
        require    => [Host["${hostname}"]],
    }
}

class basenode::stop-windows {
#    zabbix-agent::stop-windows{"Stop-Zabbix":}
#    ntsyslog::stop{"Stop-NTsyslog":}
}
