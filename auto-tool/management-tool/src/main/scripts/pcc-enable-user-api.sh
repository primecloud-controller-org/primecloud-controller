#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

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
		echo "ユーザ名を指定してください。"
		echo "Example: `basename $0` -u username"
		exit 1
fi

if [ -n "${USER_NAME}" ]; then
		echo "${USER_NAME} のPCC-API を有効化します。"

		#設定確認
        sh pcc-check.sh
		if [ $? -ne 0 ]; then
			echo "接続テストに失敗しました。management-config.propertiesの設定を確認して下さい。"
			exit 1
		fi

        #ユーザーがすでに作成されているかどうかの確認
        SQL_ALREADY_CREATED="SELECT USER_NO FROM USER where USERNAME ='${USER_NAME}'"
        USER_NO=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_ALREADY_CREATED}" -columntype int -columnname USER_NO`

        if [ "${USER_NO}" = "NULL" ]; then
                echo "${USER_NAME} は作成されていません。先にPCCのユーザを作成してください。"
                exit 1
        fi

		#無効化文字列を読む
        DISABLE_CODE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -config DISABLE_CODE`
		if [ "${DISABLE_CODE}" = "NULL" ]; then
			echo "DISABLE_CODEの読み込みに失敗しました。"
			exit 1
		fi

        #無効化されているか確認
        SQL_USER_DISABLED="SELECT LOCATE('${DISABLE_CODE}',PASSWORD) as LOCATE from USER where USER_NO=${USER_NO}"
        LOCATE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_USER_DISABLED}" -columntype int -columnname LOCATE`

        if [ "${LOCATE}" != "0" ]; then
                echo "${USER_NAME} は無効化されています。"
                exit 1
        fi

        #ユーザーがすでに有効化されているかどうかの確認
        SQL_ALREADY_API_CREATED="SELECT USER_NO FROM API_CERTIFICATE where USER_NO =${USER_NO}"
        USER_NO_CHECK=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_ALREADY_API_CREATED}" -columntype int -columnname USER_NO`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでにPCC-APIを使用可能です。"
                exit 1
        fi

        #PCC-API認証情報のACCESS_IDを生成
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -A -userno "${USER_NO}" -generateType accessId`
        if [ "${MESSAGE}" == "GENERATE_ERROR" ]; then
                echo "ACCESS_IDの生成に失敗しました。"
                exit 1
        fi
        API_ACCESS_ID="${MESSAGE}"

        #PCC-API認証情報のSECRET_KEYを生成
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -A -userno "${USER_NO}" -generateType secretKey`
        if [ "${MESSAGE}" == "GENERATE_ERROR" ]; then
                echo "SECRET_KEYの生成に失敗しました。"
                exit 1
        fi
        API_SECRET_KEY="${MESSAGE}"

        #PCC-API認証情報の作成
        SQL_CREATE_API_CERTIFICATE="INSERT INTO API_CERTIFICATE values (${USER_NO}, '${API_ACCESS_ID}','${API_SECRET_KEY}')"
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -U -sql "${SQL_CREATE_API_CERTIFICATE}"`
        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

        echo "${USER_NAME} のPCC-API を有効化しました。"
        echo "API_ACCESS_ID = ${API_ACCESS_ID}"
        echo "API_SECRET_KEY = ${API_SECRET_KEY}"
		exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
		echo "ユーザ名を指定してください。"
		echo "Example: `basename $0` -u username"
		exit 1
fi
