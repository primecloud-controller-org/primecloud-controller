#!/bin/sh

VGDISK=$1
OS=`facter operatingsystem`
OSVERSION=`facter operatingsystemrelease`

if [ -L /sys/class/scsi_host/host0 ]; then
    ls -1 /sys/class/scsi_host | awk '{system("echo \"- - -\" > /sys/class/scsi_host/" $1 "/scan && sleep 3")}'
    logger "scsi_host scan"
else
    if [ ${OS} = "CentOS" -a ${OSVERSION%%.*} -ge 6 ]; then
        XVD=`ls -rt /dev/xvd[a-z]|tail -1`
        if [ ! -L $VGDISK ]; then
            ln -s $XVD $VGDISK
            ln -s $XVD"1" $VGDISK"1"
        fi
    fi
fi