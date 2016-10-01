FROM debian:jessie

RUN sed -i -e "s/httpredir.debian.org/ftp.jp.debian.org/g" /etc/apt/sources.list

RUN apt-get update

# Install Apache HTTP Server
RUN apt-get install -y apache2

# Install MySQL
RUN echo "mysql-server mysql-server/root_password password password" | debconf-set-selections && \
    echo "mysql-server mysql-server/root_password_again password password" | debconf-set-selections && \
    apt-get install -y mysql-server

COPY asset/mysqld.cnf /etc/mysql/conf.d/mysqld.cnf
RUN chmod 644 /etc/mysql/conf.d/mysqld.cnf
RUN chown root:root /etc/mysql/conf.d/mysqld.cnf

RUN sed -i -e "s/127.0.0.1/0.0.0.0/g" /etc/mysql/my.cnf

# Install Zabbix
RUN apt-get install -y wget
RUN apt-get install -y gdebi

RUN wget http://repo.zabbix.com/zabbix/3.0/debian/pool/main/z/zabbix/zabbix-server-mysql_3.0.4-1+jessie_amd64.deb
RUN gdebi -n zabbix-server-mysql_3.0.4-1+jessie_amd64.deb
RUN rm -f zabbix-server-mysql_3.0.4-1+jessie_amd64.deb

RUN wget http://repo.zabbix.com/zabbix/3.0/debian/pool/main/z/zabbix/zabbix-frontend-php_3.0.4-1+jessie_all.deb
RUN gdebi -n zabbix-frontend-php_3.0.4-1+jessie_all.deb
RUN rm -f zabbix-frontend-php_3.0.4-1+jessie_all.deb

RUN sed -i -e "/php_value date.timezone/c \        php_value date.timezone Asia\/Tokyo" /etc/zabbix/apache.conf

COPY asset/zabbix.conf.php /etc/zabbix/web/zabbix.conf.php
RUN chmod 644 /etc/zabbix/web/zabbix.conf.php
RUN chown www-data:www-data /etc/zabbix/web/zabbix.conf.php

# Initialize database
RUN service mysql start && \
    mysql -e "create database zabbix;" -u root -ppassword && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'localhost' identified by 'password';" -u root -ppassword && \
    mysql -e "grant all privileges on zabbix.* to zabbix@'%' identified by 'password';" -u root -ppassword && \
    zcat /usr/share/doc/zabbix-server-mysql/create.sql.gz | mysql -uzabbix -ppassword zabbix && \
    mysql -e "INSERT INTO users VALUES (3,'api','api','api','5f4dcc3b5aa765d61d8327deb882cf99','',0,0,'en_GB',30,3,'default',0,'',0,50);" -u zabbix -ppassword zabbix && \
    mysql -e "INSERT INTO users_groups VALUES (5,12,3);" -u zabbix -ppassword zabbix && \
    service mysql stop

# Entrypoint
COPY asset/entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh

EXPOSE 80 3306

ENTRYPOINT ["/entrypoint.sh"]
