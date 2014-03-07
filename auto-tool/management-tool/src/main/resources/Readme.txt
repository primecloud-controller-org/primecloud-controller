PCC Management Tool ver 2.2

usage: PCC script
 -C                             Create Mode
 -columnname <arg>              columnName
 -columntype <arg>              columnType
 -dbpass,--password <arg>       PrimeCloud Controller database password
 -dburl,--connectionurl <arg>   PrimeCloud Controller database url
 -dbuser,--username <arg>       PrimeCloud Controller database username
 -familyname <arg>              Create the familyname
 -firstname <arg>               Create the firstname
 -h,--help                      help
 -I                             IaasGateWay Mode
 -iaasgwAccessId <arg>          IaasGateWay Access Id
 -iaasgwSecretKey <arg>         IaasGateWay SecretKey
 -keyName <arg>                 import your key pair as keyName
 -password <arg>                Create the password
 -proxyHost <arg>               proxyHost
 -proxyPassword <arg>           proxyPassword
 -proxyPort <arg>               proxyPort
 -proxyUsername <arg>           proxyUsername
 -publicKey <arg>               import your public key
 -S                             SELECT mode
 -sql <arg>                     SQL
 -U                             UPDATE mode
 -username <arg>                Create the username
 -Z                             Zabbix mode
 -zabbixdbpass <arg>            Zabbix database password
 -zabbixdburl <arg>             Zabbix database url
 -zabbixdbuser <arg>            Zabbix database username
 -zabbixpass <arg>              Zabbix admin password
 -zabbixurl <arg>               Zabbix web url
 -zabbixuser <arg>              Zabbix admin username


Examples

SELECT文の発行
java -jar ${AUTO_SCRIPT} -S -sql "${SQL_USER_LIST}" -dburl ${PCC_DB} -dbuser ${PCC_DB_USER} -dbpass ${PCC_DB_PASSWORD}

