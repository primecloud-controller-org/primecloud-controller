#!/bin/bash
docker run -i -t -p 80:80 -p 3306:3306 --name pcc-zabbix-2.2.9 -d pcc-zabbix:2.2.9
