class ntsyslog {

}

define ntsyslog::config (
    $log_server ,
){

    $rserver = $log_server

#    service { "NTsyslog":
#        enable     => "manual",
#        ensure     => "running",
#        hasrestart => "true",
#        hasstatus  => "true",
#        require    => Exec["set-conf"],
#    }
    service { "NTsyslog":
        enable     => "manual",
        ensure     => "running",
        hasrestart => true,
        hasstatus  => true,
    }    
#    $file_path = '"C:\Program Files\PCC\script\set-rsyslog-server.ps1"'
#    $shell_path = "${file_path} ${log_server}"
#    exec { "set-conf":
#        path    => "C:/Windows/System32/WindowsPowerShell/v1.0",
#        command => "powershell -executionpolicy remotesigned -file ${shell_path}",
#        before  => Service["NTsyslog"],
#    }
#    $file_path = '"C:\Program Files\PCC\script\set-rsyslog-server.ps1"'
#    $shell_path = "${file_path} ${log_server}"
#    exec { "set-conf":
#        path    => "C:/Windows/System32/WindowsPowerShell/v1.0",
#        command => "powershell.exe -executionpolicy remotesigned -file ${shell_path}",
#        before  => Service["NTsyslog"],
#    }
}

define ntsyslog::stop {
    service { "NTsyslog":
        enable     => "manual",
        ensure     => "stopped",
        hasstatus  => true,
    }
}

