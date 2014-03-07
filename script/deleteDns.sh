#!/bin/bash

NAME=${0##*/}

start() {
    ln -snf /etc/init.d/$NAME /etc/rc0.d/K15$NAME
    ln -snf /etc/init.d/$NAME /etc/rc6.d/K15$NAME
    touch /var/lock/subsys/$NAME
}

stop () {
    # delete Dynamic DNS
    /usr/bin/nsupdate << EOF
update delete $HOSTNAME 3600 IN A
send
quit
EOF

    rm -f /var/lock/subsys/$NAME
}

case "$1" in
    "start")
        start
        ;;
    "stop")
        stop
        ;;
esac

exit 0
