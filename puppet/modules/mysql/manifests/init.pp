class mysql {

}

define mysql::stop(
    $template_path  = "",
    $custom_param_1 = "",
    $custom_param_2 = "",
    $custom_param_3 = ""
){
    notice ("custom_param_1 = ${custom_param_1}")
    notice ("custom_param_2 = ${custom_param_2}")
    notice ("custom_param_3 = ${custom_param_3}")
    notice ("all_db_servers = ${all_db_servers}")
    notice ("all_ap_servers = ${all_ap_servers}")
    notice ("all_web_servers = ${all_web_servers}")
    notice ("db_servers = ${db_servers}")
    notice ("ap_servers = ${ap_servers}")
    notice ("web_servers = ${web_servers}")
    
    $db_home = $name
    $rsyncFlag = "${db_home}/${fact_rsyncflagfile}"

    service { "mysqld":
       before    => [ Mount["${name}"],Exec["killProc-${name}"] ],
       enable    => "false",
       ensure    => "stopped",
       hasstatus => true,
       stop      => "/sbin/service mysqld stop && sleep 5",
    }

    if $template_path != "" {
        file { "${rsyncFlag}":
            before  => Service["mysqld"],
            ensure  => "absent",
        }
    }
}

define mysql::config(
    $server_type   ,
    $server_id      ="1" ,
    $root_username  ="root",
    $root_password_org = "" ,
    $root_password  = "" ,
    $mng_username   = "mnguser",
    $mng_password   = "",
    $host_address   = "${ipaddress}",
    $reset_slave    = "false",
    $rsync_path    = "$rsync_default_path/mysql",
    $template_path = "mysql",
    $custom_param_1 = "",
    $custom_param_2 = "",
    $custom_param_3 = ""
){
    notice ("custom_param_1 = ${custom_param_1}")
    notice ("custom_param_2 = ${custom_param_2}")
    notice ("custom_param_3 = ${custom_param_3}")
    notice ("all_db_servers = ${all_db_servers}")
    notice ("all_ap_servers = ${all_ap_servers}")
    notice ("all_web_servers = ${all_web_servers}")
    notice ("db_servers = ${db_servers}")
    notice ("ap_servers = ${ap_servers}")
    notice ("web_servers = ${web_servers}")
    
    $db_home = $name
    $rsyncFlag = "${db_home}/${fact_rsyncflagfile}"

    $mysql_dir = [
         "${db_home}/mysql",
         "${db_home}/mysql/log",
         "${db_home}/mysql/data",
         "${db_home}/mysql/data/ilog",
         "${db_home}/mysql/data/blog",
         "${db_home}/mysql/data/idata"
    ]

    file { [$mysql_dir] :
        before  => Service[ "mysqld" ],
        ensure  => directory,
        owner   => "mysql",
        group   => "mysql",
        mode    => "755",
        require => Mount["${db_home}"],
    }

    service { "mysqld":
        enable  => "true",
        ensure  => "running",
        hasstatus => true,
        require => [File["/etc/my.cnf"] , ]
    }

    exec { "rsync-mysqld" :
        before  => Service["mysqld"],
        command => "touch ${rsyncFlag}",
        unless  => "test -f ${rsyncFlag}",
        require => Mount["${db_home}"],
    }

    file { "/etc/my.cnf" :
        ensure  => "${db_home}/my.cnf",
        require => [ File["${db_home}/my.cnf"],Exec["mv-my.cnf"] ],
    }

    exec { "mv-my.cnf":
        command => "mv -f /etc/my.cnf /tmp/",
        unless  => "test -L /etc/my.cnf",
        require => [ Mount["${db_home}"],File["${db_home}/my.cnf"] ],
    }

    if ( $template_path == "mysql" ) {
        #Default Value
        $replaceoption = false
    }else{
        #check rsync flag file exists
        $rsyncvariable="fact_rsyncstatus_${db_home}"
        $replaceoption=inline_template("<%= scope.lookupvar(rsyncvariable) %>") ? {
            "true"  => false,
            default => true,
        }
    }

    file { "${db_home}/my.cnf":
        owner   => "mysql",
        group   => "mysql",
        mode    => "644",
        content => template("${template_path}/my.cnf.erb"),
        replace => "${replaceoption}",
        require => Mount["${db_home}"],
    }

    exec { "SetMySQLpassword": 
        command => "mysqladmin -u${root_username}  --password='${root_password_org}' password '${root_password}'",
        unless  => "mysqladmin -u${root_username} --password='${root_password}' status",
        require => Service["mysqld"],
    }

    exec { "DeleteDefautUsers":
        command => "mysql -u${root_username} --password='${root_password}' -e \"DELETE FROM mysql.user WHERE USER='';\"",
        require => [Service["mysqld"],Exec["SetMySQLpassword"]]
    }

    exec { "DeleteDefautDB": 
        command => "mysql -u${root_username} --password='${root_password}' -e \"DROP DATABASE IF EXISTS TEST;\"",
        require => [Service["mysqld"],Exec["SetMySQLpassword"]]
    }

    case $server_type {
        'MASTER' : { 
            $del_master_info = "true" 
            createMNGUser{ "${mng_username}":
                password       => "${mng_password}",
                host           => "localhost",
                root_username  => "${root_username}",
                root_password  => "${root_password}",
            } 
        }
        'SLAVE'  : { 
            $del_master_info = $reset_slave 
        } 
        default  : {}
    }

    if $del_master_info=="true" {
        exec { "Remove master.info" :
            cwd     => "${db_home}/mysql/data/",
            command => "mv -f master.info master.info.puppet.bak",
            onlyif => "test -f ${db_home}/mysql/data/master.info",
            before  => Service["mysqld"],
            require => [ File["${db_home}/mysql/data"] ],
            notify  => Service["mysqld"],
        }
    }

    file { "/etc/logrotate.d/mysql" :
        owner   => "root",
        group   => "root",
        mode    => "644",
        content => template("${template_path}/logrotate_mysqld.erb"),
        replace => "${replaceoption}",
    }

}

define mysql::startSlave(
    $master_host,
    $master_port="3306",
    $repl_username,
    $repl_password,
    $dump_username = "",
    $dump_password = "",
    $root_username,
    $root_password,
    $ssh_username = "",
    $template_path = "mysql"
){

    $db_home = $name
    $mysql_repl_dir = "${db_home}/repl"

    if $master_host {

        $import_from_master_command = "${mysql_repl_dir}/mysqld_importFromMaster.sh ${master_host} ${master_port} ${dump_username} ${dump_password} ${root_username} ${root_password} ${repl_username} ${repl_password}"

        $start_slave_process_command = "${mysql_repl_dir}/mysqld_startslave.sh ${master_host} ${master_port} ${root_username} ${root_password}"

    } else {
        $import_from_master_command = "sleep 0"
        $start_slave_process_command = "sleep 0"
    }

    $importflg="${db_home}/mysql/data/.IMPORT_FROM_MASTER-SUCCESS"
    exec { "importFromMaster":
        command => "${import_from_master_command} && touch ${importflg}",
        unless  => "test -f ${importflg}",
        before  => [ Exec["startSlaveProcess"] ],
        require => [Service["mysqld"], Exec["SetMySQLpassword"] ],
    }

    exec { "startSlaveProcess":
        command => "$start_slave_process_command",
        require => [Service["mysqld"],Exec["SetMySQLpassword"],File["${mysql_repl_dir}/mysqld_startslave.sh"]]
    }

    file { "${mysql_repl_dir}" :
        ensure  => directory,
        owner   => "root",
        group   => "root",
        mode    => "755",
        require => [Mount["${db_home}"]],
    }

    if ( $template_path == "mysql" ) {
        #Default Value
        $replaceoption = false
    }else{
        #check rsync flag file exists
        $rsyncvariable="fact_rsyncstatus_${db_home}"
        $replaceoption=inline_template("<%= scope.lookupvar(rsyncvariable) %>") ? {
            "true"  => false,
            default => true,
        }
    }

    file { "${mysql_repl_dir}/mysqld_importFromMaster.sh" :
        owner   => "root",
        group   => "root",
        mode    => "750",
        content => template("${template_path}/mysqld_importFromMaster.sh.erb"),
        require => [File["${mysql_repl_dir}"]],
        replace => "${replaceoption}",
    }

    file { "${mysql_repl_dir}/mysqld_startslave.sh" :
        owner   => "root",
        group   => "root",
        mode    => "750",
        content => template("${template_path}/mysqld_startslave.sh.erb"),
        require => [File["${mysql_repl_dir}"]],
        replace => "${replaceoption}",
    }

}

define mysql::sampleDB (
    $root_password    
){
    $db_home = $name
    notice ("db_home = ${db_home}")

    file { "${db_home}/sampledb":
       owner   => "root",
       group   => "root",
       mode    => "750",
       recurse => true,
       replace => "false",
       source  => "puppet:///mysql/sampledb",
       require => Mount["${db_home}"],
       notify  => Exec["createSampleDB"]
    }

    exec { "createSampleDB" :
        cwd     => "${db_home}/sampledb",
        command => "${db_home}/sampledb/createSampleDB.sh ${root_password}",
        refreshonly => "true",
        require     => [Exec["SetMySQLpassword"]],
    }
}

define mysql::registerSlave(
    $dump_username,
    $dump_password,
    $repl_username,
    $repl_password,
    $root_username="root",
    $root_password
){
   $host=$name
   mysql::createDumpUser { "${dump_username}@${host}":
        host     => "${host}",
        username => "${dump_username}",
        password => "${dump_password}",
        root_username => "${root_username}", 
        root_password => "${root_password}", 
    }
    mysql::createReplUser { "${repl_username}@${host}":
        host     => "${host}",
        username => "${repl_username}",
        password => "${repl_password}",
        root_username => "${root_username}", 
        root_password => "${root_password}", 
    }
}

define mysql::unregisterSlave(
    $dump_username,
    $repl_username,
    $root_username="root",
    $root_password
){
    $host=$name
    mysql::revokeUser { "${dump_username}@${host}":
        host          => "${host}",
        username      => "${dump_username}",
        root_username => "${root_username}", 
        root_password => "${root_password}", 
    } 

    mysql::revokeUser { "${repl_username}@${host}":
        host => "${host}",
        username      => "${repl_username}",
        root_username => "${root_username}", 
        root_password => "${root_password}",
    }
}

define mysql::createsysUser (
    $host = "localhost",
    $password,
    $root_username="root",
    $root_password
){
    mysql::grantUser{ "${name}@${host}":
        host         => "${host}" ,
        username     => "${name}",
        password     => "${password}" ,
        privileges   => "SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,FILE,INDEX,ALTER,CREATE TEMPORARY TABLES,CREATE VIEW,SHOW VIEW,CREATE ROUTINE,ALTER ROUTINE,CREATE USER,EXECUTE" ,
        objects      => "*.*" ,
        withGrantOpt => "true" ,
        root_username => "${root_username}",
        root_password => "${root_password}",
    }
}

define mysql::createReplUser (
    $host = "localhost",
    $username,
    $password,
    $root_username="root",
    $root_password
){
    mysql::grantUser{ "${name}@${host}":
        host          => "${host}" ,
        username      => "${username}",
        password      => "${password}" ,
        privileges    => "REPLICATION SLAVE, REPLICATION CLIENT, RELOAD" ,
        objects       => "*.*" ,
        root_username => "${root_username}",
        root_password => "${root_password}",
    }
}

define mysql::createDumpUser (
    $host = "localhost",
    $username,
    $password,
    $root_username="root",
    $root_password
){
    mysql::grantUser{ "${name}@${host}":
        host          => "${host}" ,
        username      => "${username}",
        password      => "${password}" ,
        privileges    => "SUPER,REPLICATION CLIENT,RELOAD,SELECT,LOCK TABLES" ,
        objects       => "*.*" ,
        root_username => "${root_username}",
        root_password => "${root_password}",
    }
}

define mysql::createMNGUser (
    $host = "localhost",
    $password,
    $root_username="root",
    $root_password
){
    mysql::grantUser{ "${name}@${host}":
        host          => "${host}" ,
        username      => "${name}",
        password      => "${password}" ,
        privileges    => "RELOAD" ,
        objects       => "*.*" ,
        root_username => "${root_username}",
        root_password => "${root_password}",
    }
}


define mysql::revokeUser (
    $host = "localhost",
    $username,
    $root_username = "root",
    $root_password 
){
    exec { "revokeUser-${name}":
        command => "echo \"REVOKE ALL ON *.* FROM '${username}'@'${host}'; FLUSH PRIVILEGES; \" | mysql -u${root_username} --password='${root_password}'",
        require => [Service["mysqld"],Exec["SetMySQLpassword"],]
    }
}

define mysql::grantUser (
    $host = "localhost",
    $username,
    $password,
    $privileges = "ALL",
    $objects = "*.*",
    $withGrantOpt = false,
    $root_username = "root",
    $root_password 
){
    if $withGrantOpt=="true" { $withoption="WITH GRANT OPTION" } else {  $withoption="" }
    exec { "grantUser-${name}":
        command => "echo \"GRANT ${privileges} ON ${objects} TO '${username}'@'${host}' IDENTIFIED BY '${password}' ${withoption}; FLUSH PRIVILEGES; \" | mysql -u${root_username} --password='${root_password}'",
        require => [Service["mysqld"],Exec["SetMySQLpassword"],]
    }
}

define mysql::createDatabase (
    $username,
    $password,
    $root_username = "root",
    $root_password
){
    $db_name = $name

    exec { "createDatabase_${db_name}" :
        command => "mysql -u${root_username} --password='${root_password}' -e \"CREATE DATABASE IF NOT EXISTS ${db_name};\"",
        require => [Service["mysqld"],Exec["SetMySQLpassword"],]
    }

    mysql::grantUser{ "${username}@'%'@${db_name}":
        host         => "%" ,
        username     => "${username}",
        password     => "${password}" ,
        privileges   => "SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,INDEX,ALTER,CREATE TEMPORARY TABLES,CREATE VIEW,SHOW VIEW,CREATE ROUTINE,ALTER ROUTINE,EXECUTE",
        objects      => "${db_name}.*" ,
        withGrantOpt => "true" ,
        root_username => "${root_username}",
        root_password => "${root_password}",
    }
}
