#!/bin/sh
PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"
MAIN="jp.primecloud.auto.tool.management.main.Main"

##VCloud用のKeypairが作成されるディレクトリ
#KEYPAIR_DIR="$TOOL_HOME/key_pair"

#オプションのパース
while getopts "P:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   P) PLATFORM_NAME="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
        echo "vCloudプラットフォーム名を指定してください。"
        echo "Example: `basename $0` -P vcloud"
        exit 1
fi

if [ -n "${PLATFORM_NAME}" ]; then
        echo "${PLATFORM_NAME} を変更します。現在の設定は下記の通りです。"

        #設定確認
        sh pcc-check.sh
        if [ $? -ne 0 ]; then
            echo "接続テストに失敗しました。management-config.propertiesの設定を確認して下さい。"
            exit 1
        fi

        #vCloudプラットフォーム情報の取得
        SQL="SELECT CONCAT('URL=', URL), CONCAT('ORG_NAME=', ORG_NAME), CONCAT('VDC_NAME=', VDC_NAME), CONCAT('DEF_NETWORK=', DEF_NETWORK) FROM adc.PLATFORM, adc.PLATFORM_VCLOUD WHERE PLATFORM_NAME='"${PLATFORM_NAME}"' AND PLATFORM.PLATFORM_NO=PLATFORM_VCLOUD.PLATFORM_NO"
        RES=`mysql -u adc -ppassw0rd -e "${SQL}" | sed -e "1s/.*$//g"`
        eval `echo $RES`
        echo "URL        ="${URL}
        echo "ORG_NAME   ="${ORG_NAME}
        echo "VDC_NAME   ="${VDC_NAME}
        echo "DEF_NETWORK="${DEF_NETWORK}

        #入力値の取得
        echo "同じ値を設定する場合も、入力してください。"
        echo -n "URL        ==>"
        read URL
        echo -n "ORG_NAME   ==>"
        read ORG_NAME
        echo -n "VDC_NAME   ==>"
        read VDC_NAME
        echo -n "DEF_NETWORK==>"
        read DEF_NETWORK

        #入力値の確認
        echo "入力された値は下記となります。"
        echo "URL        =${URL}"
        echo "ORG_NAME   ="${ORG_NAME}
        echo "VDC_NAME   ="${VDC_NAME}
        echo "DEF_NETWORK="${DEF_NETWORK}
        echo -n "処理を進めますか？(y/n) ==>"
        read INPUT_FLG
        if [ "${INPUT_FLG}" != "y" ]; then
            echo "処理を終了します。再度実行ください。"
            exit 1
        fi

        #PLATFROM_NOの取得
        SQL2="SELECT CONCAT('PLATFORM_NO=', PLATFORM_NO) FROM adc.PLATFORM WHERE PLATFORM_NAME='"${PLATFORM_NAME}"'"
        RES2=`mysql -u adc -ppassw0rd -e "${SQL2}" | sed -e "1s/.*$//g"`
        eval `echo $RES2`
  #echo "PLATFORM_NO="${PLATFORM_NO}

        #入力値の反映
        SQL3="UPDATE adc.PLATFORM_VCLOUD SET URL='"${URL}"', ORG_NAME='"${ORG_NAME}"', VDC_NAME='"${VDC_NAME}"', DEF_NETWORK='"${DEF_NETWORK}"' WHERE PLATFORM_NO="${PLATFORM_NO}""
        RES3=`mysql -u adc -ppassw0rd -e "${SQL3}" | sed -e "1s/.*$//g"`
        ##echo "length of RES3 : "${#RES3}

        #設定変更の判定
        if [ "${RES3}" = "" ]; then
          echo "${PLATFORM_NAME} の設定変更を反映しました。"
        else
          echo "${PLATFORM_NAME} の設定変更に失敗しました。"
          exit 1
        fi

        #PLATFROMの有効化
        SQL4="UPDATE adc.PLATFORM SET SELECTABLE=1 WHERE PLATFORM_NO="${PLATFORM_NO}""
        RES4=`mysql -u adc -ppassw0rd -e "${SQL4}" | sed -e "1s/.*$//g"`
        ##echo "length of RES4 : "${#RES4}

        #有効化の判定
        if [ "${RES4}" = "" ]; then
          echo "${PLATFORM_NAME} を有効化しました。"
          exit 0
        else
          echo "${PLATFORM_NAME} の有効化に失敗しました。"
          exit 1
        fi

elif [ "${HELP}" = "TRUE" ]; then
        echo ""
        echo ">>>>>> `basename $0` コマンドはヘルプ機能に対応していません。<<<<<<"
        echo ""
        sh pcc-usage.sh
        exit 0
else
        echo "vCloudプラットフォーム名を指定してください。"
        echo "Example: `basename $0` -P vcloud"
        exit 1
fi
