#!/bin/bash

#delete garbage
rm -f /root/.bash_history
rm -f /root/.ssh/known_hosts

PLATFORM="EC2"
if [ $# -gt 0 ]; then
    PLATFORM=$1
fi

if [ "$PLATFORM" = "EC2" ]; then
    INSTANCEDATA=`curl -s -f http://169.254.169.254/latest/user-data`
elif [ "$PLATFORM" = "VMWARE" ]; then
    INSTANCEDATA=`vmware-guestd --cmd 'info-get guestinfo.userdata'`
elif [ "$PLATFORM" = "CS" ]; then
    ROUTERIP=`cat /var/lib/dhclient/dhclient-eth0.leases | grep dhcp-server-identifier | tail -1 | awk '{print $3}' | sed -e "s/;//"`
    INSTANCEDATA=`curl -s -f http://$ROUTERIP/latest/user-data`
elif [ "$PLATFORM" = "VCLOUD" ]; then
    INSTANCEDATA=`vmware-guestd --cmd "info-get guestinfo.ovfEnv" | awk -F"\"" ' /guestinfo.userdata/ {print $4}'`
    if [ -z "$INSTANCEDATA" ]; then
        INSTANCEDATA=`vmtoolsd --cmd "info-get guestinfo.ovfEnv" | awk -F"\"" ' /guestinfo.userdata/ {print $4}'`
    fi
fi

FILE_INSTANCEDATA=/root/instancedata
if [ -z "$INSTANCEDATA" ]; then
    if [ -e $FILE_INSTANCEDATA ]; then
        INSTANCEDATA=`cat $FILE_INSTANCEDATA`

        if [ "$PLATFORM" = "VMWARE" ]; then
            PUPPET_ONCE=1
        fi
    fi
fi

if [ -z "$INSTANCEDATA" ]; then
    exit 1
fi


PUPPETMASTER=""
DNS=""
DNS2=""
DNSDOMAIN="localhost"

count=0
USERDATA=$INSTANCEDATA
while [ -n "$USERDATA" ]
do
    DATALINES[$count]=`echo "$USERDATA" | cut -d ";" -f 1`
    USERDATA=`echo "$USERDATA" | cut -d ";" -f 2-`
    if [ "${DATALINES[$count]}" = "$USERDATA" ]; then
        break
    fi
    count=`expr $count + 1`
done

max=$count
count=0
while [ $count -le $max ]
do
#    echo $count ${DATALINES[$count]}
    DATALINE=${DATALINES[$count]}
    OPTION_NAME=`echo "$DATALINE" | cut -d "=" -f 1`
    OPTION_VAL=`echo "$DATALINE" | cut -d "=" -f 2-`

    case "$OPTION_NAME" in
        "hostname")
            HOSTNAME=$OPTION_VAL
            ;;
        "scriptserver")
            SCRIPTSERVER=$OPTION_VAL
            ;;
        "vpnuser")
            VPNUSER=$OPTION_VAL
            ;;
        "puppetmaster")
            PUPPETMASTER=$OPTION_VAL
            ;;
        "dns")
            DNS=$OPTION_VAL
                ;;
        "dns2")
            DNS2=$OPTION_VAL
            ;;
        "dnsdomain")
            DNSDOMAIN=$OPTION_VAL
            ;;
        "sshpubkey")
            SSHPUBKEY=$OPTION_VAL
            ;;
    esac
    count=`expr $count + 1`
done


#set hostname
${LOG_FILE}
if [ -n "$HOSTNAME" ]; then
    /bin/hostname $HOSTNAME
fi

#set edit hosts file
if [ "$PLATFORM" = "VCLOUD" ]; then
    /usr/bin/perl -p -i.bak -e "s/^.*# NIC <eth.*\n//g" /etc/hosts
fi

#set puppet server
if [ -n "$PUPPETMASTER" ]; then
    export PUPPETMASTER
    /usr/bin/perl -p -i.bak -e 's/server *\= *(.*)/server \= $ENV{PUPPETMASTER}/i' /etc/puppet/puppet.conf
    /usr/bin/perl -p -i.bak -e 's/^[^#].*allow *(.*)/ allow $ENV{PUPPETMASTER}/i'  /etc/puppet/namespaceauth.conf
    #set postrun_command
    grep -q -e "^\s*postrun_command" /etc/puppet/puppet.conf
    [ $? != 0 ] && echo -e '    postrun_command = "/sbin/service puppet reload"\n' >> /etc/puppet/puppet.conf
    #set configure timeout
    grep -q -e "^\s*configtimeout" /etc/puppet/puppet.conf
    [ $? != 0 ] && echo -e '    configtimeout = 120\n' >> /etc/puppet/puppet.conf
fi


#create resolv.conf
if [ -n "$DNS" ]; then
    /bin/cp -f /etc/resolv.conf /etc/resolv.conf.org
    echo "$DNS" | gawk '{print "nameserver",$1}' > /etc/resolv.conf

    if [ -n "$DNS2" ]; then
        echo "$DNS2" | gawk '{print "nameserver",$1}' >> /etc/resolv.conf
    fi

    if [ -n "$DNSDOMAIN" ]; then
        echo "search $DNSDOMAIN" >> /etc/resolv.conf
    fi
fi


#Start DNSmasq when Instance Image is EC2
if [ "$PLATFORM" = "EC2" ] && [ -f "/etc/init.d/dnsmasq" ]; then
    #update DNSmasq file
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/dhclient-exit-hooks
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /etc/dhclient-exit-hooks http://$SCRIPTSERVER/script/dhclient-exit-hooks
    fi

    PUBLICIP=`curl -s -f http://169.254.169.254/latest/meta-data/public-ipv4`
    AMIID=`curl -s -f http://169.254.169.254/latest/meta-data/ami-id | cut -c 1-3`
    if [ ${#PUBLICIP} != 0 ] && [ $AMIID = ami ]; then
        mv /etc/resolv.conf /etc/resolv.conf.tmp
        echo nameserver 127.0.0.1 > /etc/resolv.conf
        cat /etc/resolv.conf.tmp >> /etc/resolv.conf
        rm -f /etc/resolv.conf.tmp
        /etc/init.d/dnsmasq start
    fi
fi


#update Dynamic DNS
if [ -n "$VPNUSER" ]; then
    if [ -n "$DNS" ]; then
        #PulicIP
        VPNIP=`/usr/bin/facter | grep "^ipaddress_tun0" | gawk '{print $3;}'`
        if [ -z "$VPNIP" ]; then
            exit 1
        fi

       /usr/bin/nsupdate << EOF
server $DNS
update delete $HOSTNAME IN A
update add $HOSTNAME 3600 IN A $VPNIP
send
quit
EOF
    fi
fi

#change  authorized_keys file
if [ -n "$SSHPUBKEY" ]; then
    #if not prjserver image for CSKGroup
    if [ ! -f /var/prjserver/.forCSKGROUP ]; then
        echo `grep "^[^#]" /root/.ssh/authorized_keys | head -n 1` > /root/.ssh/authorized_keys
        echo "$SSHPUBKEY" >> /root/.ssh/authorized_keys
        chmod 400 /root/.ssh/authorized_keys
    fi
fi

if [ -n "$SCRIPTSERVER" ]; then
    #update puppet file
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/mount.rb
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/provider/mount.rb http://$SCRIPTSERVER/script/mount.rb
    fi
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/configurer.rb
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/configurer.rb http://$SCRIPTSERVER/script/configurer.rb
    fi
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/agent.rb
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/agent.rb http://$SCRIPTSERVER/script/agent.rb
    fi
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/webrick.rb
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/network/http/webrick.rb http://$SCRIPTSERVER/script/webrick.rb
    fi
    wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/rest.rb
    if [ $? = 0 ]; then
        wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/indirector/rest.rb http://$SCRIPTSERVER/script/rest.rb
    fi
    #wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/run.rb
    #if [ $? = 0 ]; then
        #wget -q --tries=1 -O /usr/lib/ruby/site_ruby/1.8/puppet/run.rb http://$SCRIPTSERVER/script/run.rb
    #fi
fi

# Puppet
if [ -n "$PUPPET_ONCE" ]; then
    # run puppet once
    puppetd --no-daemon --test
else
    # delete puppet cert
    rm -rf /mnt/puppet
fi

# delete DNS script
wget -q --connect-timeout=10 --tries=1 --spider http://$SCRIPTSERVER/script/deleteDns.sh
if [ $? = 0 ]; then
    if [ "$PLATFORM" != "VCLOUD" ]; then
        wget -q --tries=1 -O /etc/init.d/deleteDns http://$SCRIPTSERVER/script/deleteDns.sh
        chmod 755 /etc/init.d/deleteDns
        /etc/init.d/deleteDns start
    fi
fi

# create swap
if [ "$PLATFORM" = "EC2" ]; then
    #SWAP_SIZE(MB)
    SWAP_SIZE=1024
    SWAP_DIR=/mnt/ephemeral0
    SWAP_FILE=$SWAP_DIR/swap.space

    SWAP_STAT=`swapon -s | wc -l`
    if [ "$SWAP_STAT" -le 1 ]; then
        if [ ! -e "$SWAP_FILE" ]; then
            mkdir -p "$SWAP_DIR"

            count=`expr $SWAP_SIZE \* 1024`
            dd if=/dev/zero of="$SWAP_FILE" bs=1024 count=$count
            mkswap "$SWAP_FILE"
        fi
        swapon "$SWAP_FILE"
    fi
fi


# i18n
if [ ! -e "/etc/sysconfig/i18n" ]; then
    echo 'LANG="ja_JP.UTF-8"' > /etc/sysconfig/i18n
fi

