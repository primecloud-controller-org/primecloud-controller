Param($PLATFORM)
. "C:\Program Files\PCC\script\function.ps1"
Write-Log "PLATFORM = $PLATFORM"
if ($PLATFORM -eq $null -or $PLATFORM.Length -le 0) {
    Write-Log "not set parameter"
    exit 1
}

$INSTANCEDATA = ""
if ($PLATFORM -eq "EC2") {
    $INSTANCEDATA = Download-String "http://169.254.169.254/latest/user-data"
} elseif ($PLATFORM -eq "VMWARE") {
    $INSTANCEDATA = & "C:\Program Files\VMware\VMware Tools\vmtoolsd.exe" --cmd "info-get guestinfo.userdata"
} elseif ($PLATFORM -eq "CS") {
    $ROUTERIP = ""#edit later
    $INSTANCEDATA = Download-String "http://$ROUTERIP/latest/user-data "
}

# get instancedata and write instancedata to file
$FILE_INSTANCEDATA = "C:\Program Files\PCC\instancedata"
if ($INSTANCEDATA.Length -le 0 -and (Test-Path $FILE_INSTANCEDATA)) {
    $INSTANCEDATA = Get-Content $FILE_INSTANCEDATA
}

if ($instanceData.Length -le 0) {
    Write-Log "not exist user-data"
    exit 1
}

# set instancedata values to variables
$HOSTNAME=""
$SCRIPTSERVER=""
$VPNUSER=""
$PUPPETMASTER=""
$DNS=""
$DNS2=""
$DNSDOMAIN=""
$SSHPUBKEY=""
$INSTANCENAME=""
$RSYSLOGSERVER=""
foreach ($DATALINE in echo $INSTANCEDATA.split(";")) {
    $OPTION_NAME = $DATALINE.split("=")[0]
    $OPTION_VAL = $DATALINE.split("=")[1]
    
    Write-Log "OPTION_NAME = $OPTION_NAME"
    Write-Log "OPTION_VAL = $OPTION_VAL"
    
    switch -case ($OPTION_NAME) {
        "hostname" { $HOSTNAME = $OPTION_VAL }
        "scriptserver" { $SCRIPTSERVER = $OPTION_VAL }
        "vpnuser" { $VPNUSER = $OPTION_VAL }
        "puppetmaster" { $PUPPETMASTER = $OPTION_VAL }
        "dns" { $DNS = $OPTION_VAL }
        "dns2" { $DNS2 = $OPTION_VAL }
        "dnsdomain" { $DNSDOMAIN = $OPTION_VAL }
        "sshpubkey" { $SSHPUBKEY = $OPTION_VAL }
        "instanceName" { $INSTANCENAME = $OPTION_VAL }
        "rsyslogserver" { $RSYSLOGSERVER = $OPTION_VAL }
    }
}

Write-Log "HOSTNAME = $HOSTNAME"
Write-Log "SCRIPTSERVER = $SCRIPTSERVER"
Write-Log "VPNUSER = $VPNUSER"
Write-Log "PUPPETMASTER = $PUPPETMASTER"
Write-Log "DNS = $DNS"
Write-Log "DNS2 = $DNS2"
Write-Log "DNSDOMAIN = $DNSDOMAIN"
Write-Log "SSHPUBKEY = $SSHPUBKEY"
Write-Log "INSTANCENAME = $INSTANCENAME"
Write-Log "RSYSLOGSERVER = $RSYSLOGSERVER"
#set hostname

#set puppet server
if ($PUPPETMASTER -ne "") {
    $FILE_PUPPET_CONF = "C:\ProgramData\PuppetLabs\puppet\etc\puppet.conf"
    $FILE_NAME_AUTH_CONF = "C:\ProgramData\PuppetLabs\puppet\etc\namespaceauth.conf"
    
    #set puppetmastar fqdn to puppet.conf
    $PUPPET_CONF = Get-Content $FILE_PUPPET_CONF | ForEach-Object { $_ -replace "server = .*", "server = $PUPPETMASTER" }
    Set-Content $FILE_PUPPET_CONF $PUPPET_CONF
    
    #set puppetmastar fqdn to namespaceauth.conf
    $NAME_AUTH_CONF = Get-Content $FILE_NAME_AUTH_CONF | ForEach-Object { $_ -replace "^.*allow .*", " allow $PUPPETMASTER" }
    Set-Content $FILE_NAME_AUTH_CONF $NAME_AUTH_CONF
    
    #set configure timeout
    if ((Select-String -path $FILE_PUPPET_CONF -pattern "^.*configtimeout") -eq "") {
        Add-Contend $FILE_PUPPET_CONF "configtimeout = 120"
    }
}

#edit dhcpDomain
$SET_DOMAIN = Set-Domain $DNS $INSTANCENAME $HOSTNAME
if (-not $SET_DOMAIN) {
    Write-Log "not update dnsdomain"
    exit 1
}

#Start DNSmasq when Instance Image is EC2
# edit later

#update Dynamic DNS
if ($VPNUSER.Length -gt 0 -and $DNS.Length -gt 0) {
    $VPNIP=Get-VpnIP($DNS)
    if ($VPNIP.Length -le 0) {
        Write-Log "not get vpnIp"
        exit 1
    }
    $FILE_NSUPDATE_CONF="C:\Program Files\PCC\script\nsupdate.txt"
    New-Item $FILE_NSUPDATE_CONF -type file -force
    Set-Content $FILE_NSUPDATE_CONF "server $DNS"
    Add-Content $FILE_NSUPDATE_CONF "update delete $HOSTNAME IN A"
    Add-Content $FILE_NSUPDATE_CONF "update add $HOSTNAME 3600 IN A $VPNIP"
    Add-Content $FILE_NSUPDATE_CONF "send"
    Add-Content $FILE_NSUPDATE_CONF "quit"
    
    & nsupdate $FILE_NSUPDATE_CONF
    Remove-Item $FILE_NSUPDATE_CONF -force
}

if ($SCRIPTSERVER.Length -le 0) {
    Write-Log "scriptserver is empty"
    exit 1
}

#setting rsyslog server address
& "C:\Windows\System32\reg.exe" add HKEY_LOCAL_MACHINE\SOFTWARE\SaberNet /v Syslog /t REG_SZ /d $RSYSLOGSERVER /f
Write-Log "set rsyslog server address"

#update puppet file
$RUBY_HOME = "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet" 
$UPDATE_RB = Download-String "http://$SCRIPTSERVER/script/win/mount.rb"
if ($UPDATE_RB.Length -gt 0) {
    Set-Content "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet\provider\mount.rb" $UPDATE_RB
}
$UPDATE_RB = Download-String "http://$SCRIPTSERVER/script/win/configurer.rb"
if ($UPDATE_RB.Length -gt 0) {
    Set-Content "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet\configurer.rb" $UPDATE_RB
}
$UPDATE_RB = Download-String "http://$SCRIPTSERVER/script/win/agent.rb"
if ($UPDATE_RB.Length -gt 0) {
    Set-Content "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet\agent.rb" $UPDATE_RB
}
$UPDATE_RB = Download-String "http://$SCRIPTSERVER/script/win/webrick.rb"
if ($UPDATE_RB.Length -gt 0) {
    Set-Content "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet\network\http\webrick.rb" $UPDATE_RB
}
$UPDATE_RB = Download-String "http://$SCRIPTSERVER/script/win/rest.rb"
if ($UPDATE_RB.Length -gt 0) {
    Set-Content "C:\Program Files (x86)\Puppet Labs\Puppet\puppet\lib\puppet\indirector\rest.rb" $UPDATE_RB
}

#stop puppet and remove old cert data and puppet start
$EXIST_PUPPET_SERVICE =& sc.exe query state= all | Select-String Puppet-Agent-Listen
if ($EXIST_PUPPET_SERVICE -ne $null -and $EXIST_PUPPET_SERVICE -ne "") {
    $PUPPET_SERVICE = Get-Service Puppet-Agent-Listen
    if ($PUPPET_SERVICE.Status -ne "Stopped") {
        & sc.exe stop Puppet-Agent-Listen
        $count=0
        while($count -le 10) {
            $PUPPET_SERVICE = Get-Service Puppet-Agent-Listen
            if ($PUPPET_SERVICE.Status -eq "Stopped") {
                break
            }
            Start-Sleep -Seconds 5
            $count++
        }
    }
}

if (Test-Path "C:\ProgramData\PuppetLabs\puppet\etc\ssl") {
    Remove-Item "C:\ProgramData\PuppetLabs\puppet\etc\ssl\*" -recurse
}
if (Test-Path "C:\ProgramData\PuppetLabs\puppet\var") {
    Remove-Item "C:\ProgramData\PuppetLabs\puppet\var\*" -recurse
}

if ($EXIST_PUPPET_SERVICE -ne $null -and $EXIST_PUPPET_SERVICE -ne "") {
    & sc.exe delete Puppet-Agent-Listen
}
& "C:\Program Files\PCC\script\nssm.exe" install Puppet-Agent-Listen "C:\Program Files (x86)\Puppet Labs\Puppet\bin\puppet.bat" agent --server $PUPPETMASTER --no-client --verbose --waitforcert=120
#$DEPEND_PUPPET_SERVICE="Dhcp\0Dnscache\0iphlpsvc\0Netman\0NlaSvc"
#& "C:\Windows\System32\reg.exe" add HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\services\Puppet-Agent-Listen /v DependOnService /t REG_MULTI_SZ /d $DEPEND_PUPPET_SERVICE /f
& sc.exe config Puppet-Agent-Listen start= demand depend= iphlpsvc/netprofm
& sc.exe start Puppet-Agent-Listen
$count=0
while($count -le 10) {
    $PUPPET_SERVICE = Get-Service Puppet-Agent-Listen
    if ($PUPPET_SERVICE.Status -eq "Running") {
        break
    } else { 
        Write-Log "can't start puppet, count = $count"
    }
    Start-Sleep -Seconds 5
    $count++
}
Write-Log "restarted puppet"