#!/bin/bash
DATABASE="adc"
HOST="localhost"
USERNAME="adc"
PASSWORD="password"
DATADIR="/etc/openvpn"
#
mysql -h ${HOST} -u ${USERNAME} -p${PASSWORD} ${DATABASE} -e "select FQDN,INSTANCE_CODE from INSTANCE" -Ns --connect_timeout=10 | awk '{print $1;print $2}' > ${DATADIR}/userdb.txt

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    rm -f ${DATADIR}/userdb.db && db_load -T -t hash -f ${DATADIR}/userdb.txt ${DATADIR}/userdb.db && chmod 600 ${DATADIR}/userdb.db
    if [ $? -eq 0 ]; then
       logger -p kern.info -t LogOpenvpn "success : openvpn loaduser_db sync success."
       rm -f ${DATADIR}/userdb.txt
    fi
else
    logger -p kern.warn -t LogOpenvpn "error : openvpn loaduser_db sync failed."
    rm -f ${DATADIR}/userdb.txt
    exit 1
fi

exit 0