#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"
		#コンフィグが正常に設定されているかのチェック

		#Zabbix有効オプションを読む
        ZABBIX_USE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -config "zabbix.useZabbix"`
        if [ $? -ne 0 ]; then
        	echo "設定ファイルの読み込みに失敗しました。"
        	exit 1
        fi

		if [ "${ZABBIX_USE}" = "NULL" -o "${ZABBIX_USE}" = "true" -o "${ZABBIX_USE}" = "false" ]; then
			:
		else
			echo "設定ファイルの読み込みに失敗しました。"
			exit 1
		fi

		#PCCのDBへの接続テスト
        SQL_CHECK="SELECT 1 FROM dual"
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_CHECK}" -columntype int -columnname 1`

        if [ "${MESSAGE}" != "1" ]; then
                echo "PCCのDBサーバへの接続に失敗しました。"
                exit 1
        fi

		if [ "${ZABBIX_USE}" = "true" ]; then
			#ZabbixのDBへ接続テスト
	        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -Z -S -sql "${SQL_CHECK}" -columntype int -columnname 1`

			if [ "${MESSAGE}" != "1" ]; then
	                echo "ZabbixのDBサーバへの接続に失敗しました。"
	                exit 1
	        fi

			#ZabbixのAPIへの接続テスト
	        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -Z -check`

	        if [ "${MESSAGE}" = "NULL" ]; then
	                echo "ZabbixへのAPI接続テストに失敗しました。"
	                exit 1
	        fi
        fi
exit 0