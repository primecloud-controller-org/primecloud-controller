#!/bin/bash

#wait for getting metadata
ROUTERIP=`cat /var/lib/dhclient/dhclient-eth0.leases | grep dhcp-server-identifier | tail -1 | awk '{print $3}' | sed -e "s/;//"`
PRIVATEIP=`curl -s -f http://$ROUTERIP/latest/local-ipv4`
COUNT=1
while [ $COUNT -le 10 ]; do
    if [ ${#PRIVATEIP} != 0 ]; then
        break
    fi
    COUNT=`expr $COUNT + 1`
    sleep 15
    PRIVATEIP=`curl -s -f http://$ROUTERIP/latest/local-ipv4`
done


INSTANCEDATA=`curl -s -f http://$ROUTERIP/latest/user-data`

FILE_INSTANCEDATA=/root/instancedata
if [ -n "$INSTANCEDATA" ]; then
    echo "$INSTANCEDATA" > "$FILE_INSTANCEDATA"
elif [ -e $FILE_INSTANCEDATA ]; then
    INSTANCEDATA=`cat $FILE_INSTANCEDATA`
fi

if [ -z "$INSTANCEDATA" ]; then
    exit 1
fi

for DATALINE in `echo "$INSTANCEDATA" | tr ";" "\n"`; do
    OPTION_NAME=`echo "$DATALINE" | cut -d "=" -f 1`
    OPTION_VAL=`echo "$DATALINE" | cut -d "=" -f 2-`

    case "$OPTION_NAME" in
        "scriptserver")
            SCRIPTSERVER=$OPTION_VAL
            ;;
        "vpnserver")
            VPNSERVER=$OPTION_VAL
            ;;
        "vpnport")
            VPNPORT=$OPTION_VAL
            ;;
        "vpnuser")
            VPNUSER=$OPTION_VAL
            ;;
        "vpnuserpass")
            VPNUSERPASS=$OPTION_VAL
            ;;
        "vpnzippass")
            VPNZIPPASS=$OPTION_VAL
            ;;
        "vpnclienturl")
            VPNCLIENTURL=$OPTION_VAL
            ;;
    esac
done

#download client.zip form http-server
OPENVPNDIR=/etc/openvpn
mkdir -p $OPENVPNDIR/zip
if [ -n "$VPNCLIENTURL" ]; then
    wget -q --connect-timeout=30 --tries=4 --spider $VPNCLIENTURL --no-check-certificate --http-user=client --http-passwd=$VPNZIPPASS
    if [ $? = 0 ]; then
        wget -q --tries=4 -O $OPENVPNDIR/zip/client.zip $VPNCLIENTURL --no-check-certificate --http-user=client --http-passwd=$VPNZIPPASS
    fi
fi

#connect openvpn server
if [ -n "$VPNUSER" ]; then

    #unzip openvpn certs
    if [ -n "$VPNZIPPASS" ]; then
        unzip -P $VPNZIPPASS -qo -d /etc/openvpn /etc/openvpn/zip/client.zip
    fi

    #create openvpn config
    if [ -n "$VPNSERVER" ]; then
        cat << EOF >> /etc/openvpn/client.conf
remote $VPNSERVER $VPNPORT
EOF
    fi

    #start openvpn
    expect -c "
spawn /etc/init.d/openvpn start
expect Username:
send $VPNUSER\n
expect Password:
send $VPNUSERPASS\n
interact"
    sleep 3

    #wait for getting vpnip
    count=1
    while [ $count -le 10 ];
    do
        VPNIP=`/usr/bin/facter | grep "^ipaddress_tun0" | gawk '{print $3;}'`
        if [ -n "$VPNIP" ]; then
            break
        fi
        count=`expr $count + 1`;

        sleep 3
    done

    if [ -z "$VPNIP" ]; then
        exit 1
    fi
fi


#execute init script
SCRIPTDIR=/root/script

if [ -n "$SCRIPTSERVER" ]; then
    #get script
    wget -q --connect-timeout=30 --tries=4 --spider http://$SCRIPTSERVER/script/init.sh
    if [ $? = 0 ]; then
        wget -q --tries=4 -O $SCRIPTDIR/init.sh http://$SCRIPTSERVER/script/init.sh
    fi
fi

if [ -e $SCRIPTDIR/init.sh ]; then
    chmod a+x $SCRIPTDIR/init.sh
    $SCRIPTDIR/init.sh "CS"
fi

