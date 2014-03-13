class prjserver-module {

}

define prjserver-module::stop(
    $backup_server   = "",
    $backup_password = ""
){
    $prj_home = $name

    service { "mysqld-prjserver":
        name      => "mysqld",
        before    => [ Mount["${name}"], Exec["disable-auth-ldap"], Exec["killProc-${name}"] ],
        ensure    => "stopped",
        hasstatus => true,
    }

    service { "smb-prjserver":
        name      => "smb",
        before    => [ Mount["${name}"], Exec["disable-auth-ldap"], Exec["killProc-${name}"] ],
        enable    => "false",
        ensure    => "stopped",
        hasstatus => true,
    }

    service { "httpd-prjserver":
        name      => "httpd",
        before    => [ Mount["${name}"], Exec["disable-auth-ldap"], Exec["killProc-${name}"] ],
        enable    => "false",
        ensure    => "stopped",
        hasstatus => true,
    }

    service { "ldap-prjserver":
        name      => "ldap",
        before    => [ Mount["${name}"], Exec["killProc-${name}"] ],
        enable    => "false",
        ensure    => "stopped",
        hasstatus => true,
        require   => Exec["disable-auth-ldap"],
    }

    exec { "disable-auth-ldap" :
        before  => [ Mount["${name}"], Exec["killProc-${name}"]],
        command     => "authconfig --disableldap --disableldapauth --update",
    }

    if $backup_server {

        $backupFlag = "${prj_home}/.BACKUPSETTING-SUCCESS"
        exec { "prjserver-delbackup" :
            before  => [ Mount["${name}"], Exec["killProc-${name}"]],
            command => "${prj_home}/delbackup.sh backup-${fqdn} ${backup_server} ${backup_password} && rm -f ${backupFlag}",
            onlyif  => "test -f ${backupFlag}",
        }
        exec { "nsupdate-del-eth1":
            command => "/usr/bin/nsupdate << EOF
server `grep -m 1 '^nameserver' /etc/resolv.conf | awk '{print \$2};'`
update delete backup-${fqdn} IN A
send
quit
EOF
",
        }
    }


}

define prjserver-module::config(
    $backup_server   = "",
    $backup_password = "",
    $backup_sshkey   = "",
    $rsync_server    = "",
    $rsync_path      = "${rsync_default_path}/prjserver",
    $template_path   = "prjserver"
){
    $prj_home = $name
    service { "ldap-prjserver":
        name       => "ldap",
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => Exec["rsync-prjserver"],
    }

   exec { "mysqld-prjserver":
        command => "service mysqld start || true ",
        timeout => "300",
        require => Exec["enable-auth-ldap"],
    }

   service { "smb-prjserver":
        name       => "smb",
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => Exec["enable-auth-ldap"],
    }

   service { "httpd-prjserver":
        name       => "httpd",
        enable     => "false",
        ensure     => "running",
        hasrestart => "true",
        hasstatus  => "true",
        require    => Exec["enable-auth-ldap"],
    }

    if $rsync_server {
        $rsyncFlag = "${prj_home}/.RSYNC-SUCCESS"
        
        exec { "rsync-prjserver" :
            command => "rsync -auqz rsync://${rsync_server}${rsync_path}/ ${prj_home}/ && touch ${rsyncFlag}",
            unless  => "test -f ${rsyncFlag}",
            require => Mount["${prj_home}"],
        }

    }else{
        exec { "rsync-prjserver" : }
    }

    exec { "enable-auth-ldap" :
        command     => "authconfig --enableldap --enableldapauth --ldapserver=127.0.0.1 --ldapbasedn='dc=csk,dc=com' --update",
        require     => Service["ldap-prjserver"],
    }

    if $backup_server {
        ssh_authorized_key { "root@prjbak99.localdomain":
            ensure => "present",
            type   => "ssh-rsa",
            user   => "root",
            key    => "${backup_sshkey}",
        }

        file {"${prj_home}/addbackup.sh":
            owner   => "root",
            group   => "root",
            mode    => "700",
            source => "puppet:///modules/prjserver/addbackup.sh",
            require => Mount["${prj_home}"],
        }

        file {"${prj_home}/delbackup.sh":
            owner   => "root",
            group   => "root",
            mode    => "700",
            source => "puppet:///modules/prjserver/delbackup.sh",
            require => Mount["${prj_home}"],
        }
       
        $backupFlag = "${prj_home}/.BACKUPSETTING-SUCCESS" 
        exec { "prjserver-addbackup" :
            command => "${prj_home}/addbackup.sh backup-${fqdn} ${backup_server} ${backup_password} && touch ${backupFlag}",
            unless  => "test -f ${backupFlag}",
            require => File["${prj_home}/addbackup.sh"],
        }

        exec { "nsupdate-add-eth1": 
            command => "/usr/bin/nsupdate << EOF
server `grep -m 1 '^nameserver' /etc/resolv.conf | awk '{print \$2};'` 
update delete backup-${fqdn} IN A
update add backup-${fqdn} 3600 IN A ${ipaddress_eth1}
send
quit
EOF
",
        }
    }
}
