FROM centos:centos6

RUN sed -i -e "s/tsflags=nodocs/#tsflags=nodocs/g" /etc/yum.conf

# Install MySQL
RUN yum install -y mysql-server

COPY asset/my.cnf /etc/my.cnf
RUN chmod 644 /etc/my.cnf
RUN chown root:root /etc/my.cnf

# Install Zabbix
RUN rpm -ivh http://repo.zabbix.com/zabbix/2.2/rhel/6/x86_64/zabbix-release-2.2-1.el6.noarch.rpm
RUN yum install -y zabbix-server-mysql-2.2.9 zabbix-web-mysql-2.2.9 zabbix-web-japanese-2.2.9

RUN localedef -f UTF-8 -i ja_JP ja_JP

RUN sed -i -e "/php_value date.timezone/c \    php_value date.timezone Asia\/Tokyo" /etc/httpd/conf.d/zabbix.conf

COPY asset/zabbix.conf.php /etc/zabbix/web/zabbix.conf.php
RUN chmod 644 /etc/zabbix/web/zabbix.conf.php
RUN chown apache:apache /etc/zabbix/web/zabbix.conf.php

# Initialize database
RUN service mysqld start && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'localhost' identified by 'password';" && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'%' identified by 'password';" && \
    mysql -e "create database zabbix;" && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-mysql-2.2.9/create/schema.sql && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-mysql-2.2.9/create/images.sql && \
    mysql -u zabbix -ppassword zabbix < /usr/share/doc/zabbix-server-mysql-2.2.9/create/data.sql && \
    mysql -e "INSERT INTO users VALUES (3,'api','api','api','5f4dcc3b5aa765d61d8327deb882cf99','',0,900,'en_GB',30,3,'default',0,'',0,50);" -u zabbix -ppassword zabbix && \
    mysql -e "INSERT INTO users_groups VALUES (5,12,3);" -u zabbix -ppassword zabbix && \
    service mysqld stop

# Entrypoint
COPY asset/entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh

EXPOSE 80 3306

ENTRYPOINT ["/entrypoint.sh"]
