#!/bin/bash
docker run --privileged -i -t -p 80:80 -p 3306:3306 --name pcc-zabbix-3.0.5 -d pcc-zabbix:3.0.5 /sbin/init
sleep 5
docker exec -i -t pcc-zabbix-3.0.5 /entrypoint.sh
