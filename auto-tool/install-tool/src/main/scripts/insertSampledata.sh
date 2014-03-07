#!/bin/bash
BASE_DIR=`pwd`
cd ${BASE_DIR}


#Read parameter settings
SET_ENV=${BASE_DIR}/config.sh

if [ ! -f ${SET_ENV} ]; then
  echo "${SET_ENV}: No such file"
  exit 1
fi
. ${SET_ENV}

cd ${SOFTWARE_DIR}/pcc/sampledata/default

find . -maxdepth 1 -name '*.sql' | while read sql; do
	mysql adc -u ${ADC_DATABASE_USER} -p${ADC_DATABASE_PASS} < $sql
done


cd ${BASE_DIR}
exit 0
