#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

#オプションのパース
while getopts "h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "正しいオプションを入力してください。"
	exit 1
fi

if [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
        echo "接続テストを開始します。"
        #コンフィグが正常に設定されているかのチェック

		#Zabbix有効オプションを読む
        ZABBIX_USE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -config "zabbix.useZabbix"`
        if [ $? -ne 0 ]; then
        	echo "設定ファイルの読み込みに失敗しました。"
        	exit 1
        fi

		if [ "${ZABBIX_USE}" = "NULL" -o "${ZABBIX_USE}" = "true" -o "${ZABBIX_USE}" = "false" ]; then
				echo "設定ファイルの読み込みに成功しました。"
		else
				echo "設定ファイルの読み込みに失敗しました。"
		fi

		#PCCのDBへの接続テスト
        SQL_CHECK="SELECT 1 FROM dual"
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_CHECK}" -columntype int -columnname 1`

        if [ "${MESSAGE}" != "1" ]; then
                echo "PCCのDBサーバへの接続に失敗しました。"
        else
                echo "PCCのDBへの接続テストに成功しました。"
        fi

		if [ "${ZABBIX_USE}" = "true" ]; then
			#ZabbixのDBへ接続テスト
	        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -Z -S -sql "${SQL_CHECK}" -columntype int -columnname 1`

			if [ "${MESSAGE}" != "1" ]; then
	                echo "ZabbixのDBサーバへの接続に失敗しました。"
			else
					echo "ZabbixのDBサーバへの接続テストに成功しました。"
	        fi

			#ZabbixのAPIへの接続テスト
	        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -Z -check`

	        if [ "${MESSAGE}" = "NULL" ]; then
	                echo "ZabbixへのAPI接続テストに失敗しました。"
	        else
	                echo "ZabbixへのAPI接続テストに成功しました。"
	        fi
		fi

        echo "接続テストに成功しました。"
		exit 0
fi