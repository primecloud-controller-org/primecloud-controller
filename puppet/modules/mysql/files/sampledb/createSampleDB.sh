#!/bin/sh
[ -f .SAMPLEDB_CREATED ] && exit 0
mysql -uroot --password="$1" < 01_sample_ddl.sql
[ $? -ne 0 ] && exit 1
mysql -uroot --password="$1" < 03_sample_data.sql
[ $? -ne 0 ] && exit 1
touch .SAMPLEDB_CREATED
exit 0

