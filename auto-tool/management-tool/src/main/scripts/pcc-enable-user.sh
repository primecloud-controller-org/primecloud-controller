#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

#VMware用のKeypairが作成されるディレクトリ
KEYPAIR_DIR="$TOOL_HOME/key_pair"

#オプションのパース
while getopts "u:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   u) USER_NAME="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "有効にしたいユーザ名を指定してください。"
		echo "Example: `basename $0` -u username"
        exit 1
fi

if [ -n "${USER_NAME}" ]; then
        echo "${USER_NAME} を有効化します。"

        #設定確認
        sh pcc-check.sh
		if [ $? -ne 0 ]; then
			echo "接続テストに失敗しました。management-config.propertiesの設定を確認して下さい。"
			exit 1
		fi
        #ユーザ有効化処理
        #ユーザが作成されているかどうかの確認
        SQL_ALREADY_CREATED="SELECT USER_NO FROM USER where USERNAME ='${USER_NAME}'"
        USER_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CREATED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO}" = "NULL" ]; then
                echo "${USER_NAME} は作成されていません。"
                exit 1
        fi

		#無効化文字列を読む
        DISABLE_CODE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config DISABLE_CODE"`
		if [ "${DISABLE_CODE}" = "NULL" ]; then
			echo "無効化文字列の読み込みに失敗しました。"
			exit 1
		fi

        #すでに有効化されているかか確認
        SQL_USER_ENABLED="SELECT LOCATE('${DISABLE_CODE}',PASSWORD) as LOCATE from USER where USER_NO=${USER_NO}"
        LOCATE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_USER_ENABLED}\" -columntype int -columnname LOCATE"`

        if [ "${LOCATE}" = "0" ]; then
                echo "${USER_NAME} はすでに有効化されています。"
                exit 1
        fi

		#Zabbix有効オプションを読む
        ZABBIX_USE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config \"zabbix.useZabbix\""`

		if [ "${ZABBIX_USE}" = "true" ]; then
	        #すでにZabbixユーザが作成されているか確認
            ZABBIX_USERNAME=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -Z -get -username ${USER_NAME}"`
	        if [ "${ZABBIX_USERNAME}" = "NULL" ]; then
	                echo "${USER_NAME} はZabbixユーザに存在しません。"
	                exit 1
	        fi
        fi

        #対象のユーザのパスワードを有効化する
        SQL_USER_ENABLE="UPDATE USER SET PASSWORD=TRIM(LEADING '${DISABLE_CODE}' FROM PASSWORD) where USER_NO=${USER_NO}"
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_USER_ENABLE}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

		if [ "${ZABBIX_USE}" = "true" ]; then
	        #ZABBIXユーザの有効化
            MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -Z -enable -username ${USER_NAME}"`

	        if [ -n "${MESSAGE}" ]; then
	                echo "${MESSAGE}"
	                exit 1
	        fi
		fi
        echo "${USER_NAME} を有効化しました。"

        exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
		echo "有効にしたいユーザ名を指定してください。"
		echo "Example: `basename $0` -u username"
        exit 1
fi
