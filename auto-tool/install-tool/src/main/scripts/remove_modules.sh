#!/bin/sh

#zabbix
service zabbix-agent stop
service zabbix-server stop
chkconfig --del zabbix-agent
chkconfig --del zabbix-server
rpm -e zabbix-agent-1.6.9-2.el5.JP
rpm -e zabbix-1.6.9-2.el5.JP
rpm -e zabbix-agent-1.8.2-0
rpm -e zabbix-server-1.8.2-0
rpm -e zabbix-server-mysql-1.8.2
rpm -e zabbix-web-1.8.2
rpm -e zabbix-web-mysql-1.8.2
rpm -e zabbix-1.8.2-0
rm -rf /var/log/zabbix
rm -rf /var/run/zabbix
rm -rf /etc/zabbix

#fping iksemel libssh2
yum -y remove fping
yum -y remove iksemel
yum -y remove libssh2
rpm -e libssh2-0.18-10.el5
rpm -e fping-2.4-1.b2.2.el5.rf
rpm -e libssh2-0.18-10.el5

#httpd
service httpd stop
chkconfig --del httpd
yum -y remove httpd mod_ssl
rm -rf /var/run/httpd
rm -rf /etc/httpd
rm -rf /usr/lib64/httpd

#mysqld and php
service mysqld stop
chkconfig --del mysqld
yum -y remove mysql php-cli php-common php-mbstring php-mysql php-pdo
rm -rf /var/run/mysqld
rm -rf /etc/rc.d/init.d/mysqld
rm -rf /usr/libexec/mysqld
rm -rf /var/lib/mysql
rm -rf /usr/share/mysql

#tomcat
service tomcat stop
chkconfig --del tomcat
rm -rf /var/spool/mail/tomcat
rm -rf /var/run/tomcat
rm -rf /etc/rc.d/init.d/tomcat

#java
rpm -e jdk-1.6.0_21-fcs
rpm -e jdk-1.6.0_24-fcs

#puppet and ruby
service puppet stop
service puppetmaster stop
chkconfig --del puppet
chkconfig --del puppetmaster
yum -y remove puppet facter ruby ruby-irb ruby-augeas ruby-libs ruby-rdoc ruby-shadow libselinux-ruby
rm -rf /etc/puppet
rm -rf /etc/sysconfig/puppet
rm -rf /mnt/puppet
rm -rf /var/log/puppet
rm -rf /var/lib/puppet

#lighttpd
service lighttpd stop
chkconfig --del lighttpd
yum -y remove lighttpd
rm -rf /var/lighttpd
rm -rf /var/run/lighttpd
rm -rf /etc/lighttpd

#pcc
rm -rf /opt/adc
rm -rf /opt/data
rm -rf /opt/tomcat
rm -rf /opt/userdata

#cron
rm -f /etc/cron.daily/catalina_rotate.sh 
rm -f /etc/cron.monthly/log_delete_auto-web.sh
rm -f /etc/cron.monthly/log_delete_tomcat.sh
rm -f /etc/cron.monthly/log_delete_iaasgw.sh

#user
userdel tomcat
groupdel tomcat

userdel puppet
groupdel puppet

userdel puppetmaster
groupdel puppetmaster

userdel pcc
groupdel pcc

userdel webmaster
groupdel webmaster

userdel mysql
groupdel mysql

userdel geronimo
groupdel geronimo

userdel zabbix
groupdel zabbix