import "nodes/*.pp"
import "templates/*.pp"
import "auto/*.pp"

# Default 
$rsync_default_path  = "/data/default"
$rsync_userdata_dir = "/opt/userdata"

#Exec { 
#    path    => "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
#    timeout => 1200,
#}

#filebucket { "main" : server => puppet }
#filebucket { "local" : path => "/var/lib/puppet/clientbucket" }
File { 
    backup => "local",
    ignore => ".svn",
}


