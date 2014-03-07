#!/bin/sh
ls  -1 /sys/class/scsi_host | awk '{system("echo \"- - -\" > /sys/class/scsi_host/" $1 "/scan && sleep 3")}'
