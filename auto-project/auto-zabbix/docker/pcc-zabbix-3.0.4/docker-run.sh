#!/bin/bash
docker run -i -t -p 80:80 -p 3306:3306 --name pcc-zabbix-3.0.4 -d pcc-zabbix:3.0.4
#docker run --privileged -p 80:80 -p 3306:3306 --name pcc-zabbix-3.0.4 -d pcc-zabbix:3.0.4
#docker exec -it zabbix /bin/bash
