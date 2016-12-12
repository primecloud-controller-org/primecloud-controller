#!/bin/bash
systemctl start mariadb

mysql -e "show databases;" -s -N | grep -q zabbix

if [ $? -ne 0 ]; then
    mysql -e "create database zabbix;"
    mysql -e "grant all privileges on zabbix.* to zabbix@'localhost' identified by 'password';"
    mysql -e "grant all privileges on zabbix.* to zabbix@'%' identified by 'password';"

    zcat /usr/share/doc/zabbix-server-mysql-*/create.sql.gz | mysql -u zabbix -ppassword zabbix

    mysql -e "insert into users values (3,'api','api','api','5f4dcc3b5aa765d61d8327deb882cf99','',0,0,'en_GB',30,3,'default',0,'',0,50);" -u zabbix -ppassword zabbix
    mysql -e "insert into users_groups values (5,12,3);" -u zabbix -ppassword zabbix
fi

systemctl start httpd
