node default {

    if "${operatingsystem}" == "CentOS" {
        filebucket { "local" : path => "/var/lib/puppet/clientbucket" }
        Host { target => "/etc/hosts", }
        Exec { path => "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin", timeout => 1200, }
        Service { path => "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin", }
    } elsif "${operatingsystem}" == "windows" {
        filebucket { "local" : path => "C:/ProgramData/PuppetLabs/puppet/var/clientbucket" }
        Host { target => "C:/Windows/System32/drivers/etc/hosts", }
        Exec { path => $path, timeout => 1200, }
        Service { path => $path, }
    }
}
