#!/bin/sh

DIR=`dirname $0`
cd ${DIR}
BASE_DIR=`pwd`
cd ${BASE_DIR}


#Read parameter settings
SET_ENV=${BASE_DIR}/config.sh

if [ ! -f ${SET_ENV} ]; then
  echo "${SET_ENV}: No such file"
  exit 1
fi
. ${SET_ENV}

#Read software versions
SET_SOFTWARE_ENV=${BASE_DIR}/software_config.sh

if [ ! -f ${SET_SOFTWARE_ENV} ]; then
   echo "${SET_SOFTWARE_ENV}: No such file"
   exit 1
fi
. ${SET_SOFTWARE_ENV}

if [ ! -d ${SOFTWARE_DIR} ]; then
    echo "Error: install software not found"
    echo 1
fi

#make backup directory
if [ ! -d ${BACKUP_DIR} ]; then
    mkdir -p ${BACKUP_DIR}
fi

if [ ! -d ${LOG_DIR} ]; then
    mkdir -p ${LOG_DIR}
fi

#Check Download Softwares

# Puppet repository check
PUPPET_REPO=`rpm -qa | grep puppetlabs-release`
if [ ! -n "${PUPPET_REPO}" ]; then
        echo "Puppet yum repository was not installed"
        echo "Puppet yum repository was not installed" >> $LOG_FILE 2>&1
        exit 1
fi

#Tomcat binary file check
TOMCAT_FILE=`find ${SOFTWARE_DIR}/tomcat6/ -maxdepth 1 -name 'apache-tomcat*'`
if [ -n "${TOMXAT_FILE}" ]; then
        echo "Tomcat file was not disposed"
        echo "Tomcat file was not disposed" >> $LOG_FILE 2>&1
        exit 1
fi

# JDK binary file check
JAVA_FILE=`find ${SOFTWARE_DIR}/java/ -maxdepth 1 -name '*.bin'`
if [ ! -n "${JAVA_FILE}" ]; then
        echo "JDK File was not disposed"
        echo "JDK File was not disposed" >> $LOG_FILE 2>&1
        exit 1
fi

#Python source check
PYTHON_FILE=`find ${SOFTWARE_DIR}/python/ -maxdepth 1 -name '*.tgz'`
if [ ! -n "${PYTHON_FILE}" ]; then
        echo "Python file was not disposed"
        echo "Python file was not disposed" >> $LOG_FILE 2>&1
        exit 1
fi

# Zabbix repo check
ZABBIX_REPO=`rpm -qa | grep zabbix-jp-release`
if [ ! -n "${ZABBIX_REPO}" ]; then
        echo "ZABBIX-JP yum repository was not installed"
        echo "ZABBIX-JP yum repository was not installed" >> $LOG_FILE 2>&1
        exit 1
fi

#Install Infomation
echo "Install PCC Version   : ${PCC_VERSION}"
echo "Backup File Directory : ${BACKUP_DIR}"
echo "LOG File Directory    : ${LOG_DIR}"

yum install wget -y >> $LOG_FILE 2>&1

#install base pkg
echo "====START :install base pkg===="
echo "====START :install base pkg====" >> $LOG_FILE 2>&1

yum install -y make gcc zlib unzip patch wget man man-pages man-pages-ja rsync traceroute vixie-cron xinetd sudo \
OpenIPMI-libs.x86_64 php php-gd bcmath php-bcmath php-mysql net-snmp net-snmp-devel.x86_64 openldap \
curl curl-devel.x86_64 mysql-connector-odbc unixODBC.x86_64 php-mbstring php-xml bzip2-devel zlib-devel lvm2 >> $LOG_FILE 2>&1

if [ $? -ne 0 ];then
    echo "Error: base pkg install failed"
    echo "Error: base pkg install failed" >> $LOG_FILE 2>&1
    exit 1
fi

echo "====END :install base pkg====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install base pkg"

#setting ryncd
echo "====START :setting ryncd===="
echo "====START :setting ryncd====" >> $LOG_FILE 2>&1

cp -p /etc/xinetd.d/rsync ${BACKUP_DIR}/rsync.${BACKUP_DATE}  >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: backup failed /etc/xinetd.d/rsync"
    echo "Error: backup failed /etc/xinetd.d/rsync" >> $LOG_FILE 2>&1
    exit 1
fi
sed -i -e 's/\(\s*\)\(disable\)\s=\syes/\1\2 = no/' /etc/xinetd.d/rsync

cat > /etc/rsyncd.conf << RSYNCD_CONF_EOF
uid=root
gid=root
syslog facility = local5
hosts allow = ${CLOUD1_SERVICE}
hosts deny = ${CLOUD1_SERVICE}
dont compress = *.gz *.tgz *.zip *.pdf *.sit *.sitx *.lzh *.bz2 *.jpg *.gif *.png
#
# Module options
#
[data]
comment      = rsync server
path         = /opt/data/
#auth users   = rsync_user
#secrets file = /etc/rsyncd.secrets
read only    = no

[userdata]
comment      = rsync server userdata
path         = /opt/userdata/
#auth users   = rsync_user
#secrets file = /etc/rsyncd.secrets
read only    = no

RSYNCD_CONF_EOF

/etc/init.d/xinetd stop  >> $LOG_FILE 2>&1
/etc/init.d/xinetd start >> $LOG_FILE 2>&1
chkconfig --list xinetd  >> $CONF_LOG_FILE 2>&1

echo "====END :setting ryncd====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:setting ryncd===="

#sudoers
echo "====START :setting sudo====" >> $LOG_FILE 2>&1

mv /etc/sudoers ${BACKUP_DIR}/sudoers.${BACKUP_DATE}
if [ $? -ne 0 ]; then
        echo "Error: backup failed /etc/sudoers"
        echo "Error: backup failed /etc/sudoers" >> $LOG_FILE 2>&1
        exit 1
fi
cp -f ${SOFTWARE_DIR}/sudoers /etc/sudoers
if [ $? -ne 0 ]; then
    echo "Error: file copy failed /etc/sudoers"
    mv ${BACKUP_DIR}/sudoers.${BACKUP_DATE} /etc/sudoers
    exit 1
else
    chmod 440 /etc/sudoers
fi

echo "====END :setting sudo====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:setting sudo===="

#tomcat6 user make
echo "====START :make tomcat user====" >> $LOG_FILE 2>&1

useradd tomcat
if [ $? -eq 0 ]; then
    echo "tomcat:${TOMCAT_PASS}" | /usr/sbin/chpasswd -c MD5 >> $LOG_FILE 2>&1
else
    echo "INFO: tomcat user exist. skipping chpasswd" >> $LOG_FILE 2>&1
    echo "INFO: tomcat user exist. skipping chpasswd"
fi

echo "====END :make tomcat user====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:make tomcat user===="

#
#
#
#Install puppet-server
echo "====START :install puppet-server====" >> $LOG_FILE 2>&1
echo "====START :install puppet-server===="
yum -y install puppet-${PUPPET_VERSION} puppet-server-${PUPPET_VERSION} >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: install failed puppet-server" >> $LOG_FILE 2>&1
    echo "Error: install failed puppet-server"
    exit 1
fi

if [ ! -d /etc/puppet/modules ]; then
    mkdir -p /etc/puppet/modules
    echo "====create puppet module dir====="
    echo "====create puppet module dir=====" >> $LOG_FILE 2>&1
fi

echo "==== START :install puppet module"
echo "==== START :install puppet module" >> $LOG_FILE 2>&1

#puppet module install puppetlabs-lvm  --version 0.0.1 >> $LOG_FILE 2>&1

#install customized puppetlabs-lvm from github
yum -y install git >> $LOG_FILE 2>&1
git clone https://github.com/scsk-oss/puppetlabs-lvm.git >> $LOG_FILE 2>&1

mkdir /etc/puppet/modules/lvm
cp -r puppetlabs-lvm/* /etc/puppet/modules/lvm/


echo "==== END :install puppet module"

echo "====START:copy puppet custom modules "
    cp -r ${SOFTWARE_DIR}/puppet-server/puppet/* /etc/puppet/
    chmod a+x /etc/puppet/*.sh

echo "====END: copy puppet custom modules "

if [ -e /etc/puppet/autosign.conf ]; then
    mv /etc/puppet/autosign.conf ${BACKUP_DIR}/autosign.conf.${BACKUP_DATE}
fi


cat > /etc/puppet/fileserver.conf << FILESERVER_CONF_EOF
# This file consists of arbitrarily named sections/modules
# defining where files are served from and to whom

# Define a section 'files'
# Adapt the allow/deny settings to your needs. Order
# for allow/deny does not matter, allow always takes precedence
# over deny
# [files]
#  path /var/lib/puppet/files
#  allow *.example.com
#  deny *.evil.example.com
#  allow 192.168.0.0/24
[plugins]
 allow *.${DOMAIN_NAME}

[mount_point]
 path /var/lib/puppet
 allow *.${DOMAIN_NAME}
FILESERVER_CONF_EOF

#namespaceauth.conf
cat > /etc/puppet/namespaceauth.conf << NAMESPACEAUTH_CONF_EOF
[fileserver]
  allow *.${DOMAIN_NAME}
[puppetreports]
  allow *.${DOMAIN_NAME}
[puppetbucket]
  allow *.${DOMAIN_NAME}
NAMESPACEAUTH_CONF_EOF

sed -i -e "s/$PUPPET_SAMPLE/$HOST_NAME/"  /etc/puppet/manifests/templates/basenode.pp
chkconfig puppetmaster on
chkconfig --list puppetmaster >> $CONF_LOG_FILE 2>&1

usermod -G puppet tomcat

echo "Starting PuppetMaster"
/etc/init.d/puppetmaster start >> $LOG_FILE 2>&1
sleep 5

echo "Enable autosign"
/etc/init.d/puppetmaster stop >> $LOG_FILE 2>&1
sleep 5

cat > /etc/puppet/autosign.conf << AUTOSIGN_CONF_EOF
*.${DOMAIN_NAME}
AUTOSIGN_CONF_EOF

/etc/init.d/puppetmaster start >> $LOG_FILE 2>&1
sleep 5


echo "====END :install puppet-server====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install puppet-server===="


#
#
#
#install java
echo "====START :install java====" >> $LOG_FILE 2>&1

cd ${SOFTWARE_DIR}/java
JDK_FILE=`find . -maxdepth 1 -name '*.bin' | xargs -n 1 basename | head -1`

echo "JDK Version :${JDK_FILE}"

chmod a+x ${SOFTWARE_DIR}/java/${JDK_FILE}
chmod a+x ${SOFTWARE_DIR}/java/env.sh
cp  ${SOFTWARE_DIR}/java/${JDK_FILE} ${SOFTWARE_DIR}/java/${JDK_FILE}.org
sed -i 's/more <<"EOF"/cat <<"EOF"/g' ${JDK_FILE}

yes | ${SOFTWARE_DIR}/java/${JDK_FILE} >> $LOG_FILE 2>&1

cp -p ${SOFTWARE_DIR}/java/env.sh /etc/profile.d/env.sh

cd ${BASE_DIR}

echo "====END :install java====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install java====" >> $LOG_FILE 2>&1

#
#
#
#install tomcat
echo "====START :install tomcat====" >> $LOG_FILE 2>&1

if [ ! -d /opt/tomcat ]; then
    mkdir -p /opt/tomcat
fi

cd /opt/tomcat
tar zxvf ${SOFTWARE_DIR}/tomcat6/apache-tomcat-${TOMCAT_VERSION}.tar.gz >> $LOG_FILE 2>&1
ln -sn /opt/tomcat/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat/default

cd /opt/tomcat/apache-tomcat-${TOMCAT_VERSION}/conf
mv context.xml ${BACKUP_DIR}/context.xml.${BACKUP_DATE}
cp -p ${SOFTWARE_DIR}/tomcat6/context.xml .

mv tomcat-users.xml ${BACKUP_DIR}/tomcat-users.xml.${BACKUP_DATE}
cp -p ${SOFTWARE_DIR}/tomcat6/tomcat-users.xml .

cd /opt/tomcat/apache-tomcat-${TOMCAT_VERSION}/webapps
rm -rf docs examples

chown -R tomcat:tomcat /opt/tomcat/apache-tomcat-${TOMCAT_VERSION}
cp -p ${SOFTWARE_DIR}/tomcat6/tomcat /etc/init.d/
chkconfig --add tomcat
chkconfig tomcat on
chkconfig --list tomcat >> $CONF_LOG_FILE 2>&1
chmod a+x /etc/init.d/tomcat

mkdir /var/run/tomcat
chown tomcat:tomcat /var/run/tomcat
cd ${BASE_DIR}

echo "====END :install tomcat====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install tomcat===="

#
#
#
#install Apache
echo "====START :install httpd====" >> $LOG_FILE 2>&1

#kawakami edit
yum install -y httpd >> $LOG_FILE 2>&1
#yum -y --nogpgcheck localinstall ${SOFTWARE_DIR}/apache/*.rpm >> $LOG_FILE 2>&1
chkconfig httpd on
chkconfig --list httpd >> $CONF_LOG_FILE 2>&1

echo "====END :install httpd====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install httpd===="

#
#
#install mysql
echo "====START :install mysql====" >> $LOG_FILE 2>&1

yum install -y mysql-server >> $LOG_FILE 2>&1
#yum -y --nogpgcheck localinstall ${SOFTWARE_DIR}/mysql/*.rpm >> $LOG_FILE 2>&1

mv /etc/my.cnf ${BACKUP_DIR}/my.cnf.${BACKUP_DATE}
cp -p ${SOFTWARE_DIR}/mysql/my.cnf /etc/
chkconfig mysqld on

/etc/init.d/mysqld start >> $LOG_FILE 2>&1

sleep 20

echo "====END :install mysql====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install mysql====" >> $LOG_FILE 2>&1

#create database
DB_CREATED_FLAG=`mysql -uroot -e "show databases" | grep adc`
if [ ! -n "${DB_CREATED_FLAG}" ]; then


mysql -uroot << CREATE_ADC_EOF
CREATE DATABASE adc;
grant all privileges on adc.* to ${ADC_DATABASE_USER}@'%' identified by '${ADC_DATABASE_PASS}';
grant all privileges on adc.* to ${ADC_DATABASE_USER}@'localhost' identified by '${ADC_DATABASE_PASS}';
flush privileges;
CREATE_ADC_EOF

if [ $? -ne 0 ]; then
    echo "Error: Create adc database failed" >> $LOG_FILE 2>&1
    echo "Error: Create adc database failed"
    exit 1
fi


mysql -uroot << CREATE_ZABBIX_EOF
CREATE DATABASE zabbix;
grant all privileges on zabbix.* to ${ZABBIX_DATABASE_USER}@'%' identified by '${ZABBIX_DATABASE_PASS}';
grant all privileges on zabbix.* to ${ZABBIX_DATABASE_USER}@'localhost' identified by '${ZABBIX_DATABASE_PASS}';
flush privileges;
CREATE_ZABBIX_EOF

if [ $? -ne 0 ]; then
    echo "Error: Create zabbix database failed" >> $LOG_FILE 2>&1
    echo "Error: Create zabbix database failed"
    exit 1
fi

mysql -uroot << CREATE_ADC_LOG_EOF
CREATE DATABASE adc_log;
grant all privileges on adc_log.* to ${ADC_DATABASE_USER}@'%' identified by '${ADC_DATABASE_PASS}';
grant all privileges on adc_log.* to ${ADC_DATABASE_USER}@'localhost' identified by '${ADC_DATABASE_PASS}';
flush privileges;
CREATE_ADC_LOG_EOF

    if [ $? -ne 0 ]; then
        echo "Error: Create adc_log database failed" >> $LOG_FILE 2>&1
        echo "Error: Create adc_log database failed"
        exit 1
    fi


#end create database

#Delete DefaultUsers and Databases
mysql -uroot <<DELETE_USDER_DATABASES_EOF
DELETE FROM mysql.user WHERE USER='';
DROP DATABASE IF EXISTS test;
DELETE_USDER_DATABASES_EOF

fi
#end delete defaultUsers and Dabases

#
#
#Install zabbix
echo "====START :install zabbix====" >> $LOG_FILE 2>&1
echo "====START :install zabbix===="

yum -y install zabbix-${ZABBIX_VERSION} zabbix-agent-${ZABBIX_VERSION} zabbix-server-${ZABBIX-VERSION} zabbix-server-mysql-${ZABBIX_VERSION} zabbix-web-${ZABBIX-VERSION} zabbix-web-mysql-${ZABBIX_VERSION} >> $LOG_FILE 2>&1

if [ $? -ne 0 ]; then
    echo "ERROR: Install base pkg for zabbix faile" >> $LOG_FILE 2>&1
    echo "ERROR: Install base pkg for zabbix faile"
    exit 1
fi

cd /usr/share/doc/zabbix-server-${ZABBIX_VERSION}/schema
cat mysql.sql | mysql -u${ZABBIX_DATABASE_USER} -p${ZABBIX_DATABASE_PASS} zabbix

cd ../data
cat data.sql | mysql -u${ZABBIX_DATABASE_USER} -p${ZABBIX_DATABASE_PASS} zabbix
cat images_mysql.sql | mysql -u${ZABBIX_DATABASE_USER} -p${ZABBIX_DATABASE_PASS} zabbix

cd ${BASE_DIR}
cp -p /etc/services ${BACKUP_DIR}/services.${BACKUP_DATE}
if [ $? -ne 0 ]; then
    echo "Error: backup failed /etc/services" >> $LOG_FILE 2>&1
    echo "Error: backup failed /etc/services"
    exit 1
fi

echo "zabbix-agent    10050/tcp       # Zabbix Agent" >> /etc/services
echo "zabbix-agent    10050/udp       # Zabbix Agent" >> /etc/services
echo "zabbix-trapper  10051/tcp       # Zabbix Trapper" >> /etc/services
echo "zabbix-trapper  10051/tcp       # Zabbix Trapper" >> /etc/services


cp -p /etc/zabbix/zabbix_server.conf ${BACKUP_DIR}/zabbix_server.conf.${BACKUP_DATE}
cp -p /etc/zabbix/zabbix_agentd.conf ${BACKUP_DIR}/zabbix_agentd.conf.${BACKUP_DATE}

cp -p /etc/php.ini ${BACKUP_DIR}/php.ini.${BACKUP_DATE}
if [ $? -ne 0 ]; then
    echo "Error: backup failed /etc/php.ini" >> $LOG_FILE 2>&1
    echo "Error: backup failed /etc/php.ini"
    exit 1
fi

sed -i -e 's/\(max_execution_time\)\s=\s\(30\)/\1 = 300/' /etc/php.ini
sed -i -e 's/\(max_input_time\)\s=\s\(60\)/\1 = 600/' /etc/php.ini

if [ ! -n "/etc/zabbix/zabbix.conf.php" ]; then
    cp /etc/zabbix/zabbix.conf.php ${BACKUP_DIR}/zabbix.conf.php.${BACKUP_DATE}
fi

chkconfig zabbix-server on
chkconfig zabbix-agent on

chkconfig --list zabbix-server >> $CONF_LOG_FILE 2>&1
chkconfig --list zabbix-agent >> $CONF_LOG_FILE 2>&1

echo "====END :install zabbix====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install zabbix===="


echo "-----------------------------------------------------------"
echo "You can change zabbix config"
echo "Check those config files and start zabbix"
echo ""
echo "/etc/zabbix/zabbix_server.conf"
echo "  DBUser=${ZABBIX_DATABASE_USER}"
echo "  DBPassword=${ZABBIX_DATABASE_PASS}"
echo ""

echo "/etc/zabbix/zabbix_agentd.conf"
echo "  Server=127.0.0.1"
echo "-----------------------------------------------------------"

#
#
#Install python
echo "====START :install python===="
echo "====START :install python====" >> $LOG_FILE 2>&1

cd ${SOFTWARE_DIR}/python
tar zxvf ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}.tgz >> $LOG_FILE 2>&1

cd ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}

#configure
${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}/configure --with-threads --enable-shared >> $LOG_FILE 2>&1

#openssl setting
OPENSSL_DIR=`sudo find / -name openssl | grep include | sed -e "s/\/include\/.*//"`
if [ -n "$OPENSSL_DIR" ]; then
    sed -i -e "s%#SSL=/usr/local/ssl%SSL=$OPENSSL_DIR%" ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}/Modules/Setup
    sed -i -e 's%#_ssl _ssl.c \\%_ssl _ssl.c \\%' ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}/Modules/Setup
    sed -i -e 's%#\t-DUSE_SSL -I\$(SSL)/include -I\$(SSL)/include/openssl \\%\t-DUSE_SSL -I\$(SSL)/include -I\$(SSL)/include/openssl \\%' ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}/Modules/Setup
    sed -i -e 's%#\t-L\$(SSL)/lib -lssl -lcrypto%\t-L\$(SSL)/lib -lssl -lcrypto%' ${SOFTWARE_DIR}/python/Python-${PYTHON_VERSION}/Modules/Setup
fi

echo "====START :make python===="
echo "====START :make python====" >> $LOG_FILE 2>&1

#make install
make >> $LOG_FILE 2>&1
make install >> $LOG_FILE 2>&1

#set python conf file
if [ -e /etc/ld.so.conf.d/python-${PYTHON_VERSION}.conf ]; then
    mv /etc/ld.so.conf.d/python-${PYTHON_VERSION}.conf ${BACKUP_DIR}/python-${PYTHON_VERSION}.conf.${BACKUP_DATE}
fi
cp -p ${SOFTWARE_DIR}/python/python-${PYTHON_VERSION}.conf /etc/ld.so.conf.d/python-${PYTHON_VERSION}.conf
ldconfig

#install ez_setup
#python ${SOFTWARE_DIR}/python/ez_setup.py >> $LOG_FILE 2>&1
wget http://peak.telecommunity.com/dist/ez_setup.py >> $LOG_FILE 2>&1
python ez_setup.py >> $LOG_FILE 2>&1

#install SQLAlchemy
easy_install SQLAlchemy\=\=${SQLALCHEMY_VERSION} >> $LOG_FILE 2&>1
if [ ${SQLALCHEMY_VERSION} == 0.7.3 ]; then
	if [ -e /usr/local/lib/python2.7/site-packages/SQLAlchemy-0.7.3-py2.7-linux-x86_64.egg/sqlalchemy/dialects/mysql/mysqlconnector.py ]; then
	cp -p /usr/local/lib/python2.7/site-packages/SQLAlchemy-0.7.3-py2.7-linux-x86_64.egg/sqlalchemy/dialects/mysql/mysqlconnector.py ${BACKUP_DIR}/mysqlconnector.py.${BACKUP_DATE}
	fi

sed -i -e 's/return connection.connection.get_characterset_info()/return connection.connection.get_charset()/' /usr/local/lib/python2.7/site-packages/SQLAlchemy-0.7.3-py2.7-linux-x86_64.egg/sqlalchemy/dialects/mysql/mysqlconnector.py
ldconfig
fi

#install libcloud
easy_install apache-libcloud\=\=${LIBCLOUD_VERSION} >> $LOG_FILE 2&>1

#install mysql-connector
wget https://launchpad.net/myconnpy/0.3/0.3.2/+download/mysql-connector-python-0.3.2-devel.tar.gz
easy_install -Z mysql-connector-python-0.3.2-devel.tar.gz >> $LOG_FILE 2>&1
#easy_install mysql-connector-python\=\=${MYSQL_CONNECTOR_PYTHON_VERSION} >> $LOG_FILE 2&>1

#sitecustomize.py(utf8)
if [ -e /usr/local/lib/python2.7/site-packages/sitecustomize.py ]; then
    mv /usr/local/lib/python2.7/site-packages/sitecustomize.py ${BACKUP_DIR}/mysqlconnector.py.${BACKUP_DATE}
fi
cp ${SOFTWARE_DIR}/python/sitecustomize.py /usr/local/lib/python2.7/site-packages/sitecustomize.py

ldconfig

echo "====END :install python====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install python===="

#
#
#Install pcc
echo "====START :install pcc====" >> $LOG_FILE 2>&1

mkdir -p /opt/adc/app/auto-web
mkdir -p /opt/adc/log/auto-web
mkdir -p /opt/adc/log/iaasgw
mkdir -p /opt/userdata

cd /opt/adc/app/auto-web
#pcc(auto-web)
jar xvf ${SOFTWARE_DIR}/pcc/auto-web-${PCC_VERSION}.war  >> $LOG_FILE 2>&1
ln -sn /opt/adc/app/auto-web/ /opt/tomcat/default/webapps/auto-web
cp -rp ${SOFTWARE_DIR}/pcc/conf /opt/adc/
cp -rp ${SOFTWARE_DIR}/pcc/puppet /opt/adc/app
cp -rp ${SOFTWARE_DIR}/pcc/data /opt

cp -p ${SOFTWARE_DIR}/pcc/auto.conf /etc/httpd/conf.d/

#iaasgateway
cp -frp ${SOFTWARE_DIR}/iaasgw /opt/adc/
cp -p /opt/adc/iaasgw/iaassystem.ini ${BACKUP_DIR}/iaassystem.ini.${BACKUP_DATE}
sed -i -e "s/USER =.*/USER = $ADC_DATABASE_USER/" /opt/adc/iaasgw/iaassystem.ini
sed -i -e "s/PASS =.*/PASS = $ADC_DATABASE_PASS/" /opt/adc/iaasgw/iaassystem.ini

#set env
if [ -n /etc/profile ]; then
    cp -rp /etc/profile ${BACKUP_DIR}/profile.${BACKUP_DATE}
fi
echo "export PYTHON_HOME=/usr/local/bin/python2.7" >> /etc/profile
echo "export IAASGW_HOME=/opt/adc/iaasgw" >> /etc/profile
echo "export PCC_CONFIG_HOME=/opt/adc/conf" >> /etc/profile
source /etc/profile

chown tomcat:tomcat /etc/puppet/manifests/site.pp
chown tomcat:tomcat /etc/puppet/manifests/auto
chown -R tomcat:tomcat /opt/adc

cp -rp ${SOFTWARE_DIR}/pcc/script /opt/adc/script

if [ "${NIFTY_PARAM}" =  "YES" ]; then
    cp -rp ${SOFTWARE_DIR}/niftyimage /opt/adc/
fi

#update ruby scripts
mv /usr/lib/ruby/site_ruby/1.8/puppet/network/http/webrick.rb ${BACKUP_DIR}/webrick.rb.${BACKUP_DATE}
mv /usr/lib/ruby/site_ruby/1.8/puppet/indirector/rest.rb ${BACKUP_DIR}/rest.rb.${BACKUP_DATE}
#mv /usr/lib/ruby/site_ruby/1.8/puppet/application/puppetrun.rb ${BACKUP_DIR}/puppetrun.rb.${BACKUP_DATE}
mv /usr/lib/ruby/site_ruby/1.8/puppet/application/kick.rb ${BACKUP_DIR}/kick.rb.${BACKUP_DATE}
mv /usr/lib/ruby/site_ruby/1.8/puppet/reports/store.rb ${BACKUP_DIR}/store.rb.${BACKUP_DATE}

cp -p /opt/adc/script/webrick.rb /usr/lib/ruby/site_ruby/1.8/puppet/network/http/webrick.rb
cp -p /opt/adc/script/rest.rb /usr/lib/ruby/site_ruby/1.8/puppet/indirector/rest.rb
#cp -p /opt/adc/script/puppetrun.rb /usr/lib/ruby/site_ruby/1.8/puppet/application/puppetrun.rb
cp -p /opt/adc/script/kick.rb /usr/lib/ruby/site_ruby/1.8/puppet/application/kick.rb
cp -p /opt/adc/script/store.rb /usr/lib/ruby/site_ruby/1.8/puppet/reports/store.rb

echo "====END :install pcc====" >> $LOG_FILE 2>&1
echo -e "====success\t\t:install pcc===="


#Install management-tool
echo "====START :install management-tool ===="
echo "====START :install management-tool ====" >> $LOG_FILE 2>&1
tar zxvf ${SOFTWARE_DIR}/pcc/management-tool-${PCC_VERSION}.tar.gz >> $LOG_FILE 2>&1
mv management-tool-${PCC_VERSION} /opt/adc
cd /opt/adc
ln -s management-tool-${PCC_VERSION} management-tool
chmod a+x /opt/adc/management-tool/bin/*.sh

echo "====end :install management-tool ====" >> $LOG_FILE 2>&1
echo "====end :install management-tool ===="

#Change IaasGW permission
touch /opt/adc/log/iaasgw/app.log
chown tomcat:tomcat /opt/adc/log/iaasgw/app.log

#create adc tables;
cd ${BASE_DIR}
${BASE_DIR}/createTables.sh

if [ $? -eq 0 ]; then
    echo "success: create adc tables"  >> $LOG_FILE 2>&1
    echo -e "====success\t\t: create adc tables"
fi

#set mysql root password
echo "====START :set mysql root password====" >> $LOG_FILE 2>&1

mysqladmin -uroot --password='' status >> $LOG_FILE 2>&1

if [ $? -eq 0 ]; then
    mysqladmin -uroot  --password='' password "${MYSQL_ROOT_PASS}"
    if [ $? -ne 0 ]; then
        echo "ERROR :set mysql root password failed" >> $LOG_FILE 2>&1
        echo "ERROR :set mysql root password failed"
    fi
else
    echo "INFO :mysql root password not set" >> $LOG_FILE 2>&1
    echo "INFO :mysql root password not set"
fi

echo "====END :set mysql root password====" >> $LOG_FILE 2>&1


#insert Sample Image Data
${BASE_DIR}/insertSampledata.sh
if [ $? -eq 0 ]; then
    echo "success: insert default sample data"  >> $LOG_FILE 2>&1
    echo -e "====success\t\t: insert default sample data"
fi

#
#
#install cron  scripts
chmod +x ${SOFTWARE_DIR}/cron/*
cp -p ${SOFTWARE_DIR}/cron/catalina_rotate.sh  /etc/cron.daily/
cp -p ${SOFTWARE_DIR}/cron/log_delete_auto-web.sh /etc/cron.monthly/
cp -p ${SOFTWARE_DIR}/cron/log_delete_tomcat.sh /etc/cron.monthly/
cp -p ${SOFTWARE_DIR}/cron/log_delete_iaasgw.sh /etc/cron.monthly/

#
#
#install end
echo "-----------------------------------------------------------"
echo "Install Process finished."
echo "Sample Data is ${SOFTWARE_DIR}/pcc/sampledata ."
echo "Now, you can change config.properties,"
echo "and you can start tomcat and apache."
echo "If you use Auto Scaling, you change apisystem.ini."
echo "-----------------------------------------------------------"
