#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

#VCloud用のKeypairが作成されるディレクトリ
KEYPAIR_DIR="$TOOL_HOME/key_pair"

#オプションのパース
while getopts "u:P:a:s:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   u) USER_NAME="$OPTARG" ;;
   P) PLATFORM_NAME="$OPTARG" ;;
   a) VCLOUD_USER="$OPTARG" ;;
   s) VCLOUD_PASSWORD="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
        echo "ユーザ名とプラットフォーム名を指定してください。"
        echo "Example: `basename $0` -u username -P vcloud"
        echo "任意のVCloudユーザ名とVCloudユーザパスワードを用いる場合。"
        echo "Example: `basename $0` -u username -P vcloud -a <VCLOUD_USER> -s <VCLOUD_PASSWORD>"
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

        #SSH_KEYの読み込み
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

        #VCLOUD_USERとVCLOUD_PASSWORDをオプションに付けていない場合は、コンフィグから読む
        if [ -z "${VCLOUD_USER}" -a -z "${VCLOUD_PASSWORD}" ]; then
            VCLOUD_USER=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config VCLOUD_USER"`
            if [ "${VCLOUD_USER}" = "NULL" ]; then
                echo "VCLOUD_USERの読み込みに失敗しました。"
                exit 1
            fi

            VCLOUD_PASSWORD=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config VCLOUD_PASSWORD"`
            if [ "${VCLOUD_PASSWORD}" = "NULL" ]; then
                echo "VCLOUD_PASSWORDの読み込みに失敗しました。"
                exit 1
            fi
        else
            #VCLOUD_USERかVCLOUD_PASSWORDの指定が無ければエラー
            if [ -z "${VCLOUD_USER}" ]; then
                echo "VCLOUD_USERの値を入力してください。"
                exit 1
            elif [ -z "${VCLOUD_PASSWORD}" ]; then
                echo "VCLOUD_PASSWORDの値を入力してください。"
                exit 1
            fi
        fi

        #プラットフォーム番号の取得
        PLATFORM_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -platformname \"${PLATFORM_NAME}\" -platformkind vcloud"`

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
        SQL_ALREADY_VCLOUD_CERTIFICATE_CREATED="SELECT USER_NO FROM VCLOUD_CERTIFICATE where USER_NO =${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_VCLOUD_CERTIFICATE_CREATED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに${PLATFORM_NAME}を使用可能です。"
                exit 1
        fi

        SQL_ALREADY_VCLOUD_KEY_PAIR_CREATED="SELECT USER_NO FROM VCLOUD_KEY_PAIR where USER_NO =${USER_NO} and PLATFORM_NO=${PLATFORM_NO}"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_VCLOUD_KEY_PAIR_CREATED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに${PLATFORM_NAME}を使用可能です。"
                exit 1
        fi

        #VCloud認証情報の作成
        SQL_CREATE_VCLOUD_CERTIFICATE="INSERT INTO VCLOUD_CERTIFICATE values (${USER_NO}, '${PLATFORM_NO}','${VCLOUD_USER}','${VCLOUD_PASSWORD}')"
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_CREATE_VCLOUD_CERTIFICATE}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

        #VCloudキーペア情報の作成
        SQL_CREATE_VCLOUD_KEY_PAIR="INSERT INTO VCLOUD_KEY_PAIR values (null,${USER_NO},'${PLATFORM_NO}','${USER_NAME}','${SSH_KEY_PUB}')"
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_CREATE_VCLOUD_KEY_PAIR}\""`

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
        echo "Example: `basename $0` -u username -P vcloud"
        echo "任意のVCloudユーザ名とVCloudユーザパスワードを用いる場合。"
        echo "Example: `basename $0` -u username -P vcloud -a <VCLOUD_USER> -s <VCLOUD_PASSWORD>"
        exit 1
fi
