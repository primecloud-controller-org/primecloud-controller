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
while getopts "u:p:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   u) USER_NAME="$OPTARG" ;;
   p) PASSWORD="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
        echo "ユーザ名とパスワードを指定してください。"
        echo "Example: `basename $0` -u username -p password"
        exit 1
fi

if [ -n "${USER_NAME}" -a -n "${PASSWORD}" ]; then

        echo "${USER_NAME} のパスワードを変更します。"

        #ユーザ名はアルファベットと英字のみに制限
        CHECK=`echo ${USER_NAME} | grep ^[a-zA-Z0-9\-]*$`
        if [ -z "${CHECK}" ]; then
                echo "ユーザ名に使用可能な文字は半角英数と-(ハイフン)です。"
                exit 1
        fi

        #ユーザ名の長さ制限15文字
        if [ ${#USER_NAME} -gt 15 ]; then
        echo "ユーザー名は15文字以内で入力してください。"
                exit 1
        fi

        #パスワードの文字列チェック
        CHECK=`expr "${PASSWORD}" : ".*\&.*\|.*\".*\|.*'.*\|.*<.*\|.*>.*"`
        if [ ${CHECK} -ne 0 ]; then
            echo "パスワードに&,',\",>,<の文字は使用出来ません。"
            exit 1
        fi

        #パスワードの長さ制限
        if [ ${#PASSWORD} -gt 15 ]; then
            echo "パスワードは15文字以内で入力してください。"
            exit 1
        fi

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
                echo "${USER_NAME} はPCCに存在しません。"
                exit 1
        fi

        #Zabbix有効オプションを読む
        ZABBIX_USE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config \"zabbix.useZabbix\""`
        #if [ "${ZABBIX_USE}" = "true" ]; then

            #すでにZabbixユーザが作成されているか確認
            #ZABBIX_USERNAME=`java $JAVA_OPTS -cp $CLASSPATH $MAIN -Z -get -username ${USER_NAME}`
            #if [ "${ZABBIX_USERNAME}" == "NULL" ]; then
            #        echo "${USER_NAME} はZabbixに存在しません。"
            #        exit 1
            #fi
        #fi

        #ユーザがすでに無効化されているかどうかの確認
        SQL_USER_DISABLED="SELECT USER_NO from USER where USER_NO = ${USER_NO} and (ENABLED = 0 or PASSWORD like 'DISABLE\t%')"
        USER_NO_CHECK=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_USER_DISABLED}\" -columntype int -columnname USER_NO"`

        if [ "${USER_NO_CHECK}" != "NULL" ]; then
                echo "${USER_NAME} はすでに無効化されています。"
                exit 1
        fi

        #対象のユーザのパスワードを変更する
        SQL_USER_PASSWORD_UPDATE="UPDATE USER SET PASSWORD=? where USER_NO =?"
        ENCRYPTED_PASSWORD=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -E -password \"${PASSWORD}\""`
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_USER_PASSWORD_UPDATE}\" -prepared \"${ENCRYPTED_PASSWORD}\" \"${USER_NO}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

        if [ "${ZABBIX_USE}" = "true" ]; then
            #Zabbixユーザのパスワードを変更する。
            MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -Z -U -username \"${USER_NAME}\" -password \"${PASSWORD}\""`

            if [ -n "${MESSAGE}" ]; then
                    echo "${MESSAGE}"
                    exit 1
            fi
        fi
        echo "${USER_NAME} のパスワードを変更しました。"
        exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
        echo "ユーザ名とパスワードを指定してください。"
        echo "Example: `basename $0` -u username -p password"
        exit 1
fi
