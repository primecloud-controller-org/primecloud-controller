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
   a) AWS_ACCESS_ID="$OPTARG" ;;
   s) AWS_SECRET_KEY="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "ユーザ名とプラットフォーム名を指定してください。"
		echo "Example: `basename $0` -u username -P ec2"
		echo "任意のACCESS_IDとSECRET_KEYを用いる場合。"
		echo "Example: `basename $0` -u username -P ec2 -a <ACCESS_ID> -s <SECRET_KEY>"
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
        USER_NO=`su tomcat -c "java $JAVA_OPTS -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CREATED}\" -columntype int -columnname USER_NO"`

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

        #ユーザがすでに無効化されているかどうかの確認
        SQL_USER_DISABLED="SELECT USER_NO from USER where USER_NO = ${USER_NO} and (ENABLED = 0 or PASSWORD like 'DISABLE\t%')"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_USER_DISABLED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに無効化されています。"
                exit 1
        fi

		#ACCESS_IDとSECRET_KEYをオプションに付けていない場合は、コンフィグから読む
        if [ -z "${AWS_ACCESS_ID}" -a -z "${AWS_SECRET_KEY}" ]; then
            AWS_ACCESS_ID=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config AWS_ACCESS_ID"`
			if [ "${AWS_ACCESS_ID}" = "NULL" ]; then
				echo "AWS_ACCESS_IDの読み込みに失敗しました。"
				exit 1
			fi

            AWS_SECRET_KEY=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config AWS_SECRET_KEY"`
			if [ "${AWS_SECRET_KEY}" = "NULL" ]; then
				echo "AWS_SECRET_KEYの読み込みに失敗しました。"
				exit 1
			fi
        else
            #ACCESS_IDかSECRET_KEYの指定が無ければエラー
			if [ -z "${AWS_ACCESS_ID}" ]; then
				echo "ACCESS_IDの値を入力してください。"
				exit 1
			elif [ -z "${AWS_SECRET_KEY}" ]; then
				echo "SECRET_KEYの値を入力してください。"
				exit 1
			fi

        fi

		#プラットフォーム番号の取得
        PLATFORM_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -platformname \"${PLATFORM_NAME}\" -platformkind ec2"`

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
        SQL_ALREADY_AWS_CREATED="SELECT USER_NO FROM AWS_CERTIFICATE where USER_NO =${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_AWS_CREATED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに${PLATFORM_NAME}を使用可能です。"
                exit 1
        fi

        #AWSユーザー認証情報の作成
        SQL_CREATE_AWS_CERTIFICATE="INSERT INTO AWS_CERTIFICATE values (${USER_NO}, '${PLATFORM_NO}','${AWS_ACCESS_ID}','${AWS_SECRET_KEY}',NULL,NULL,NULL)"
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_CREATE_AWS_CERTIFICATE}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

		#AWS用のキーペアをAPIを通して送る
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -I -keyname \"${USER_NAME}\" -publickey \"${SSH_KEY_PUB}\" -userno \"${USER_NO}\" -platformno \"${PLATFORM_NO}\" -platformkind ec2"`

        if [ -n "${MESSAGE}" ]; then
            SQL_DELETE_AWS_CERTIFICATE="DELETE FROM AWS_CERTIFICATE where USER_NO = ${USER_NO} and PLATFORM_NO = ${PLATFORM_NO}"
        	if [ "${MESSAGE}" = "IMPORT_SKIPPED" ]; then
        		echo "${USER_NAME} のキーペアはすでにインポートされている為、インポートをスキップします。"
        	elif [ "${MESSAGE}" = "VPCID_EMPTY" ]; then
        	    echo "VPCを利用する場合はVPC_IDをPLATFORM_AWSテーブルに設定して下さい。"
        	    #エラーの場合はAWSユーザー認証情報を削除する
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_AWS_CERTIFICATE}\""`
                if [ -n "${MESSAGE}" ]; then
                        echo "${MESSAGE}"
                fi
        		exit 1
        	elif [ "${MESSAGE}" = "SUBNET_EMPTY" ]; then
        	    echo "指定されたACCESS_IDではVPCを使用することが出来ません。"
        	    #エラーの場合はAWSユーザー認証情報を削除する
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_AWS_CERTIFICATE}\""`
                if [ -n "${MESSAGE}" ]; then
                        echo "${MESSAGE}"
                fi
        		exit 1
        	elif [ "${MESSAGE}" = "IMPORT_ERROR" ]; then
        		echo "${USER_NAME} キーのインポートに失敗しました。ACCESS_ID、SECRET_KEYの値を確認して下さい。"
                #エラーの場合はAWSユーザー認証情報を削除する
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_AWS_CERTIFICATE}\""`
                if [ -n "${MESSAGE}" ]; then
                        echo "${MESSAGE}"
                fi
        		exit 1
        	else
        	    echo "${MESSAGE}"
                #エラーの場合はAWSユーザー認証情報を削除する
                MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_DELETE_AWS_CERTIFICATE}\""`
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
		echo "Example: `basename $0` -u username -P ec2"
		echo "任意のACCESS_IDとSECRET_KEYを用いる場合。"
		echo "Example: `basename $0` -u username -P ec2 -a <ACCESS_ID> -s <SECRET_KEY>"
		exit 1
fi
