#!/bin/sh

LOG_DIR=/opt/tomcat/default/logs

cp -p $LOG_DIR/catalina.out $LOG_DIR/catalina.out.`date "+%Y-%m-%d"`.log
if [ -f "$LOG_DIR/catalina.out.`date "+%Y-%m-%d"`.log" ]; then
    cat /dev/null > $LOG_DIR/catalina.out
fi

