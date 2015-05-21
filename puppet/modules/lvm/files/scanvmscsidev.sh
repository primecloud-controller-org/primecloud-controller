#!/bin/sh
for i in $(ls -1 /sys/class/scsi_host); do echo "- - -" > /sys/class/scsi_host/$i/scan ; done