#!/bin/sh

BASE_DIR=`pwd`
cd ${BASE_DIR}


#Read parameter settings
SET_ENV=${BASE_DIR}/config.sh

if [ ! -f ${SET_ENV} ]; then
  echo "${SET_ENV}: No such file"
  exit 1
fi
. ${SET_ENV}

echo "==== START :install Niftyimage files ===="
echo "==== START :install Niftyimage files ====" >> $LOG_FILE 2>&1

NIFTYDIR=/opt/adc/niftyimage

# JDK binary file check
JAVA_FILE=`find ${SOFTWARE_DIR}/java/ -maxdepth 1 -name '*.bin'`
if [ ! -n "${JAVA_FILE}" ]; then
        echo "JDK File was not disposed"
        echo "JDK File was not disposed" >> $LOG_FILE 2>&1
        exit 1
fi

mkdir -p ${NIFTYDIR}  >> $LOG_FILE 2>&1
cp -r ${SOFTWARE_DIR}/niftyimage ${NIFTYDIR} >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: copy failed. niftyimage dir."
    echo "Error: copy failed. niftyimage dir."  >> $LOG_FILE 2>&1
    exit 1
fi
cp ${SOFTWARE_DIR}/puppet-server/puppet/modules/geronimo/files/geronimo ${NIFTYDIR}/niftyimage/geronimo >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: copy failed. geronimo."
    echo "Error: copy failed. geronimo."  >> $LOG_FILE 2>&1
    exit 1
fi
cp ${SOFTWARE_DIR}/pcc/script/host-init/host-init.sh.nifty ${NIFTYDIR}/niftyimage/init >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: copy failed. host-init.sh.nifty."
    echo "Error: copy failed. host-init.sh.nifty."  >> $LOG_FILE 2>&1
    exit 1
fi
cp ${JAVA_FILE} ${NIFTYDIR}/niftyimage/init >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: copy failed. jdk."
    echo "Error: copy failed. jdk."  >> $LOG_FILE 2>&1
    exit 1
fi
cp /etc/openvpn/easy-rsa/keys/client/client.zip ${NIFTYDIR}/niftyimage/openvpn >> $LOG_FILE 2>&1
if [ $? -ne 0 ]; then
    echo "Error: copy failed. client.zip."
    echo "Error: copy failed. client.zip."  >> $LOG_FILE 2>&1
    exit 1
fi

cd ${NIFTYDIR}
if [ -e ${NIFTYDIR}/niftyimage.tar ]; then
    rm -f $NIFTYDIR}/niftyimage.tar
    echo "old niftyimage.tar file removed." 2>&1
fi
tar cvf ${NIFTYDIR}/niftyimage.tar niftyimage 2>&1
if [ $? -ne 0 ]; then
    echo "Error: niftyimage tar archive creating filed."
    echo "Error: niftyimage tar archive creating filed." >> $LOG_FILE 2>&1
    exit 1
fi

echo "==== END :install Niftyimage files ===="
echo "==== END :install Niftyimage files ====" >> $LOG_FILE 2>&1

