#!/bin/bash
ETHERNET_LIST=`ifconfig -a| grep Ethernet | awk '{print $1"="$5}'`
COUNT=1
LOG_FILE=/tmp/network-init.log

if [ -e ${LOG_FILE} ]; then
        rm -rf ${LOG_FILE}
fi
touch ${LOG_FILE}

while [ true ]; do
  IPCONFIG=`vmware-guestd --cmd "info-get guestinfo.network${COUNT}" 2>/dev/null`
  if [ -n "$IPCONFIG" ]; then
        for DATALINE in `echo "$IPCONFIG" | tr ";" "\n"`; do
            OPTION_NAME=`echo "$DATALINE" | cut -d "=" -f 1`
            OPTION_VAL=`echo "$DATALINE" | cut -d "=" -f 2-`
            case "$OPTION_NAME" in
                "BootProto")
                    BOOTPROTO=$OPTION_VAL
                    ;;
                "Mac")
                    MAC=$OPTION_VAL
                    ;;
                "IP")
                    IP=$OPTION_VAL
                    ;;
                "Netmask")
                    NETMASK=$OPTION_VAL
                    ;;
                "Gateway")
                    GATEWAY=$OPTION_VAL
                    ;;
            esac
        done
        for ETHERNET in `echo "$ETHERNET_LIST"`; do
            MAC_LOCAL=`echo "$ETHERNET" | cut -d "=" -f 2-`
            if [ "${MAC_LOCAL}" = "${MAC}" ]; then
                INTERFACE=`echo "$ETHERNET" | cut -d "=" -f 1`
                echo `date` [${INTERFACE}] ${IPCONFIG} >> ${LOG_FILE}
                if [ "$BOOTPROTO" = "static" ]; then
                `/usr/sbin/system-config-network-cmd -i >> ${LOG_FILE} 2>&1 << EOF
DeviceList.Ethernet.${INTERFACE}.BootProto=static
DeviceList.Ethernet.${INTERFACE}.Device=${INTERFACE}
DeviceList.Ethernet.${INTERFACE}.DeviceId=${INTERFACE}
DeviceList.Ethernet.${INTERFACE}.OnBoot=true
DeviceList.Ethernet.${INTERFACE}.IP=${IP}
DeviceList.Ethernet.${INTERFACE}.Netmask=${NETMASK}
DeviceList.Ethernet.${INTERFACE}.Gateway=${GATEWAY}
EOF`
                    RETURN_CODE=$?
                    if [ ${RETURN_CODE} -ne 0 ]; then
                        echo `date` [${INTERFACE}] initalization faild return_code=${RETURN_CODE} >> ${LOG_FILE}
                        echo -e `date` [${INTERFACE}] `/usr/sbin/system-config-network-cmd | grep DeviceList.Ethernet.${INTERFACE}` >> ${LOG_FILE}
                    else
                        echo `date` [${INTERFACE}] initalization success >> ${LOG_FILE}
                    fi
                fi
                if [ "$BOOTPROTO" = "dhcp" ]; then
                `/usr/sbin/system-config-network-cmd -i >> ${LOG_FILE} 2>&1 << EOF
DeviceList.Ethernet.${INTERFACE}.BootProto=dhcp
DeviceList.Ethernet.${INTERFACE}.Device=${INTERFACE}
DeviceList.Ethernet.${INTERFACE}.DeviceId=${INTERFACE}
DeviceList.Ethernet.${INTERFACE}.OnBoot=true
EOF`
                    RETURN_CODE=$?
                    if [ ${RETURN_CODE} -ne 0 ]; then
                        echo `date` [${INTERFACE}] initalization faild return_code=${RETURN_CODE} >> ${LOG_FILE}
                        echo -e `date` [${INTERFACE}] `/usr/sbin/system-config-network-cmd | grep DeviceList.Ethernet.${INTERFACE}` >> ${LOG_FILE}
                    else
                        echo `date` [${INTERFACE}] initalization success >> ${LOG_FILE}
                    fi
                 fi
            fi
        done
        COUNT=`expr $COUNT + 1`
    else
        break;
    fi
done
