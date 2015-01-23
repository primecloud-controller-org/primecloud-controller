#!/bin/sh

PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

#VMware用のKeypairが作成されるディレクトリ
KEYPAIR_DIR="$TOOL_HOME/key_pair"

#KEY_PAIR用のディレクトリ作成
if [ ! -d $KEYPAIR_DIR ]; then
  mkdir -p ${KEYPAIR_DIR}
fi

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
        echo "${USER_NAME} を作成します。"

        #ユーザ名の長さ制限15文字
        if [ ${#USER_NAME} -gt 15 ]; then
        echo "ユーザー名は15文字以内で入力してください。"
                exit 1
        fi

        #ユーザ名はアルファベットと英字のみに制限
        CHECK=`echo ${USER_NAME} | grep ^[a-zA-Z0-9\-]*$`
        if [ -z "${CHECK}" ]; then
                echo "ユーザ名に使用可能な文字は半角英数と-(ハイフン)です。"
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

        #ユーザ作成処理
        #ユーザーがすでに作成されているかどうかの確認
        SQL_ALREADY_CREATED="SELECT USER_NO FROM USER where USERNAME ='${USER_NAME}'"
        USER_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CREATED}\" -columntype int -columnname USER_NO"`
        if [ "${USER_NO}" != "NULL" ]; then
                echo "${USER_NAME} は既にPCCに存在します。"
                exit 1
        fi

        #Zabbix有効オプションを読む
        ZABBIX_USE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -config \"zabbix.useZabbix\""`

        if [ "${ZABBIX_USE}" = "true" ]; then
            #すでにZabbixユーザが作成されているか確認
            ZABBIX_USERNAME=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -Z -get -username ${USER_NAME}"`
            if [ "${ZABBIX_USERNAME}" != "NULL" ]; then
                    echo "${USER_NAME} は既Zabbixに存在します。"
                    exit 1
            fi
        fi

        # キーペアの確認
        if [ -f "${KEYPAIR_DIR}/${USER_NAME}.pub" ]; then
                echo "すでにKEY_PAIRが存在します。"
                exit 1;
        fi

        if [ -f "${KEYPAIR_DIR}/${USER_NAME}.pem" ]; then
                echo "すでにKEY_PAIRが存在します。"
                exit 1;
        fi

        #PCCユーザーの作成
        SQL_CREATE_PCC_USER="INSERT INTO USER SET USERNAME=?, PASSWORD=?"
        ENCRYPTED_PASSWORD=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -E -password \"${PASSWORD}\""`
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_CREATE_PCC_USER}\" -prepared \"${USER_NAME}\" \"${ENCRYPTED_PASSWORD}\""`

        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}"
                exit 1
        fi

        #作成したユーザーNOの取得
        USER_NO=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -S -sql \"${SQL_ALREADY_CREATED}\" -columntype int -columnname USER_NO"`

        #作成したユーザーNOをチェック
        if [ "${USER_NO}" = "NULL" ]; then
                echo "${USER_NAME} の作成に失敗しました。"
                exit 1
        fi

        #PCCユーザーの更新(ユーザ権限の付与)
        #ユーザ作成時はマスターユーザかつパワーユーザとしておく
        SQL_UPDATE_PCC_USER="UPDATE USER SET MASTER_USER=?, POWER_USER=? WHERE USER_NO=?" 
        MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -U -sql \"${SQL_UPDATE_PCC_USER}\" -prepared \"${USER_NO}\" \"0\" \"${USER_NO}\""`
        
        if [ -n "${MESSAGE}" ]; then
                echo "${MESSAGE}" 
                exit 1
        fi

        #VMWAREのキーペアを作成

        #公開鍵の作成
        ssh-keygen -f "${KEYPAIR_DIR}/${USER_NAME}" -P ''

        #秘密鍵に.pemという拡張子をつける
        mv  "${KEYPAIR_DIR}/${USER_NAME}" "${KEYPAIR_DIR}/${USER_NAME}.pem"

        SSH_KEY_PUB=`cat ${KEYPAIR_DIR}/${USER_NAME}.pub`

        #Zabbixモードが有効ならZabbixユーザを追加
        if [ "${ZABBIX_USE}" = "true" ]; then
            #Zabbixユーザの作成
            #簡略化のため名前と苗字にもユーザー名を入れています
            MESSAGE=`su tomcat -c "java ${JAVA_OPTS} -cp ${CLASSPATH} ${MAIN} -Z -C -username \"${USER_NAME}\" -password \"${PASSWORD}\" -firstname \"${USER_NAME}\" -familyname \"${USER_NAME}\""`

            if [ -n "${MESSAGE}" ]; then
                    echo "${MESSAGE}"
                    exit 1
            fi
        fi

    echo "${USER_NAME} を作成しました。"
    echo "秘密鍵は ${KEYPAIR_DIR}/${USER_NAME}.pemです。"
    exit 0
elif [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
        echo "ユーザ名とパスワードを指定してください。"
        echo "Example: `basename $0` -u username -p password"
        exit 1
fi