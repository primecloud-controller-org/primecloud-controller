#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

#Keypairが作成されるディレクトリ
KEYPAIR_DIR="$TOOL_HOME/key_pair"

#オプションのパース
while getopts "u:P:a:s:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   u) USER_NAME="$OPTARG" ;;
   P) PLATFORM_NAME="$OPTARG" ;;
   a) CLOUDSTACK_ACCESS_ID="$OPTARG" ;;
   s) CLOUDSTACK_SECRET_KEY="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "ユーザ名とプラットフォーム名を指定してください。"
		echo "Example: `basename $0` -u username -P cloudstack"
		echo "任意のACCESS_IDとSECRET_KEYを用いる場合。"
		echo "Example: `basename $0` -u username -P cloudstack -a <ACCESS_ID> -s <SECRET_KEY>"
		exit 1
fi

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
        USER_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CREATED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO}" = "NULL" ]; then
                echo "${USER_NAME} は作成されていません。先にPCCのユーザを作成してください。"
                exit 1
        fi

		#KEY_PAIRの読み込み
		SSH_KEY_PUB=`cat "${KEYPAIR_DIR}"/"${USER_NAME}".pub`
        if [ -z "${SSH_KEY_PUB}" ]; then
                echo "公開鍵の読み込みに失敗しました。"
                exit 1
        fi

		#無効化文字列を読む
        DISABLE_CODE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config DISABLE_CODE"`
		if [ "${DISABLE_CODE}" = "NULL" ]; then
			echo "DISABLE_CODEの読み込みに失敗しました。"
			exit 1
		fi

        #無効化されているか確認
        SQL_USER_DISABLED="SELECT LOCATE('${DISABLE_CODE}',PASSWORD) as LOCATE from USER where USER_NO=${USER_NO}"
        LOCATE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_USER_DISABLED}\" -columntype int -columnname LOCATE"`

        if [ "${LOCATE}" != "0" ]; then
                echo "${USER_NAME} は無効化されています。"
                exit 1
        fi

		#ACCESS_IDとSECRET_KEYをオプションに付けていない場合は、コンフィグから読む
        if [ -z "${CLOUDSTACK_ACCESS_ID}" -a -z "${CLOUDSTACK_SECRET_KEY}" ]; then
            CLOUDSTACK_ACCESS_ID=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config CLOUDSTACK_ACCESS_ID"`
			if [ "${CLOUDSTACK_ACCESS_ID}" = "NULL" ]; then
				echo "CLOUDSTACK_ACCESS_IDの読み込みに失敗しました。"
				exit 1
			fi

            CLOUDSTACK_SECRET_KEY=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config CLOUDSTACK_SECRET_KEY"`
			if [ "${CLOUDSTACK_SECRET_KEY}" = "NULL" ]; then
				echo "CLOUDSTACK_SECRET_KEYの読み込みに失敗しました。"
				exit 1
			fi
        else
            #ACCESS_IDかSECRET_KEYの指定が無ければエラー
			if [ -z "${CLOUDSTACK_ACCESS_ID}" ]; then
				echo "ACCESS_IDの値を入力してください。"
				exit 1
			elif [ -z "${CLOUDSTACK_SECRET_KEY}" ]; then
				echo "SECRET_KEYの値を入力してください。"
				exit 1
			fi

        fi

		#プラットフォーム番号の取得
        PLATFORM_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -platformname \"${PLATFORM_NAME}\" -platformkind cloudstack"`

		if [ "${PLATFORM_NO}" = "NULL" ]; then
			echo ${PLATFORM_NAME}" というプラットフォームは存在しません。"
			exit 1
		fi

		if [ "${PLATFORM_NO}" = "OTHER" ]; then
			echo ${PLATFORM_NAME}" プラットフォーム名が間違っています。"
			exit 1
		fi

        if [ "${PLATFORM_NO}" = "DISABLE" ]; then
            echo ${PLATFORM_NAME}" プラットフォームは無効になっています。"
            exit 1
        fi

        #ユーザーがすでに有効化されているかどうかの確認
        SQL_ALREADY_CLOUDSTACK_CREATED="SELECT ACCOUNT FROM CLOUDSTACK_CERTIFICATE where ACCOUNT=${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CLOUDSTACK_CREATED}\" -columntype int -columnname ACCOUNT"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに${PLATFORM_NAME}を使用可能です。"
                exit 1
        fi

        #CLOUDSTACKユーザーの作成
        SQL_CREATE_CLOUDSTACK_CERTIFICATE="INSERT INTO CLOUDSTACK_CERTIFICATE values (${USER_NO}, '${PLATFORM_NO}','${CLOUDSTACK_ACCESS_ID}','${CLOUDSTACK_SECRET_KEY}',NULL)"
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_CREATE_CLOUDSTACK_CERTIFICATE}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

		#CLOUDSTACK用のキーペアをAPIを通して送る
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -I -keyname \"${USER_NAME}\" -publickey \"${SSH_KEY_PUB}\" -userno \"${USER_NO}\" -platformno \"${PLATFORM_NO}\" -platformkind cloudstack"`

        if [ -n "${MESSAGE}" ]; then
            SQL_DELETE_CLOUDSTACK_CERTIFICATE="DELETE FROM CLOUDSTACK_CERTIFICATE where ACCOUNT=${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"

        	if [ "${MESSAGE}" = "IMPORT_SKIPPED" ]; then
        		echo "${USER_NAME} のキーペアはすでにインポートされている為、インポートをスキップします。"
        	elif [ "${MESSAGE}" = "IMPORT_ERROR" ]; then
        		echo "${USER_NAME} キーのインポートに失敗しました。ACCESS_ID、SECRET_KEYの値を確認して下さい。"
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_CLOUDSTACK_CERTIFICATE}\""`
                if [ -n "${MESSAGE}" ]; then
                        echo "${MESSAGE}"
                fi
        		exit 1
        	else
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_CLOUDSTACK_CERTIFICATE}\""`
                if [ -n "${MESSAGE}" ]; then
                        echo "${MESSAGE}"
                fi
                exit 1
        	fi
        fi

        echo "${USER_NAME} の${PLATFORM_NAME} を有効化しました。"
		exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
		echo "ユーザ名とプラットフォーム名を指定してください。"
		echo "Example: `basename $0` -u username -P cloudstack"
		echo "任意のACCESS_IDとSECRET_KEYを用いる場合。"
		echo "Example: `basename $0` -u username -P cloudstack -a <ACCESS_ID> -s <SECRET_KEY>"
		exit 1
fi
