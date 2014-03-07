#!/bin/sh

TOOL=`basename $0`
DELDATE=60
LOG_DIR=/opt/adc/log/auto-web

find $LOG_DIR -name "*.log.*" -mtime +$DELDATE -exec rm -f {} \;
if [ $? -eq 0 ]; then
    logger -p kern.info -t LogDelete "$TOOL : success : delete $DELDATE days ago's log files success."
else
    logger -p kern.warn -t LogDelete "$TOOL : error : delete $DELDATE days ago's log files failed."
fi


