#!/bin/bash
docker run --privileged -i -t -p 80:80 -p 3306:3306 --name pcc-zabbix-3.2.4 -d pcc-zabbix:3.2.4 /sbin/init
sleep 5
docker exec -i -t pcc-zabbix-3.2.4 /entrypoint.sh
