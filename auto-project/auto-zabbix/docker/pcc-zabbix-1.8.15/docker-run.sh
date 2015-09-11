#!/bin/bash
docker run -i -t -p 80:80 -p 3306:3306 --name pcc-zabbix-1.8.15 -d pcc-zabbix:1.8.15
