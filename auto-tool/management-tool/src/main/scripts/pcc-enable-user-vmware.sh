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
while getopts "u:P:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   u) USER_NAME="$OPTARG" ;;
   P) PLATFORM_NAME="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "ユーザ名とプラットフォーム名を指定してください。"
		echo "Example: `basename $0` -u username -P vmware"
		exit 1
fi

OPT=`getopt -o u:P:h -l username:,platform:,help -- $*`

if [ -n "${USER_NAME}" -a -n "${PLATFORM_NAME}" ]; then
		echo "${USER_NAME} の${PLATFORM_NAME} を有効化します。"

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

		#SSH_KEYの読み込み
		SSH_KEY_PUB=`cat "${KEYPAIR_DIR}"/"${USER_NAME}".pub`

        if [ -z "${SSH_KEY_PUB}" ]; then
                echo "公開鍵の読み込みに失敗しました。"
                exit 1
        fi

		#プラットフォーム番号の取得
		PLATFORM_NO=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -platformname "${PLATFORM_NAME}" -platformkind vmware`

		if [ "${PLATFORM_NO}" = "NULL" ]; then
			echo "${PLATFORM_NAME} というプラットフォームは存在しません。"
			exit 1
		fi

		if [ "${PLATFORM_NO}" = "OTHER" ]; then
			echo "${PLATFORM_NAME} は作成できません。"
			exit 1
		fi

        if [ "${PLATFORM_NO}" = "DISABLE" ]; then
            echo ${PLATFORM_NAME}" プラットフォームは無効になっています。"
            exit 1
        fi

        #ユーザーがすでに有効化されているかどうかの確認
        SQL_ALREADY_VMWARE_CREATED="SELECT USER_NO FROM VMWARE_KEY_PAIR where USER_NO =${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"
        USER_NO_CHECK=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -S -sql "${SQL_ALREADY_VMWARE_CREATED}" -columntype int -columnname USER_NO`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに${PLATFORM_NAME}を使用可能です。"
                exit 1
        fi

		#VMwareユーザーの作成
	    SQL_CREATE_VMWARE_KEY_PAIR="INSERT INTO VMWARE_KEY_PAIR values (null,${USER_NO},'${PLATFORM_NO}','${USER_NAME}','${SSH_KEY_PUB}')"
        MESSAGE=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -U -sql "${SQL_CREATE_VMWARE_KEY_PAIR}"`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

		echo "${USER_NAME} の${PLATFORM_NAME} を有効化しました。"
		exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
		echo "ユーザ名とプラットフォーム名を指定してください。"
		echo "Example: `basename $0` -u username -P vmware"
		exit 1
fi
