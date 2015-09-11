FROM centos:centos6

RUN sed -i -e "s/tsflags=nodocs/#tsflags=nodocs/g" /etc/yum.conf

# Install MySQL
RUN yum install -y mysql-server

COPY asset/my.cnf /etc/my.cnf
RUN chmod 644 /etc/my.cnf
RUN chown root:root /etc/my.cnf

# Install Zabbix
RUN rpm -ivh http://repo.zabbix.jp/relatedpkgs/rhel6/x86_64/zabbix-jp-release-6-6.noarch.rpm
RUN yum install -y zabbix-server-mysql-1.8.15 zabbix-web-mysql-1.8.15

COPY asset/zabbix.conf.php /etc/zabbix/zabbix.conf.php
RUN chmod 600 /etc/zabbix/zabbix.conf.php
RUN chown apache:apache /etc/zabbix/zabbix.conf.php

# Initialize database
RUN service mysqld start && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'localhost' identified by 'password';" && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'%' identified by 'password';" && \
    mysql -e "create database zabbix;" && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-1.8.15/schema/mysql.sql && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-1.8.15/data/data.sql && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-1.8.15/data/images_mysql.sql && \
    mysql -e "INSERT INTO users VALUES (3,'api','api','api','5f4dcc3b5aa765d61d8327deb882cf99','',0,0,'en_gb',30,3,'default.css',0,'',0,50);" -u zabbix -ppassword zabbix && \
    mysql -e "INSERT INTO users_groups VALUES (3,10,3);" -u zabbix -ppassword zabbix && \
    service mysqld stop

# Entrypoint
COPY asset/entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh

EXPOSE 80 3306

ENTRYPOINT ["/entrypoint.sh"]
