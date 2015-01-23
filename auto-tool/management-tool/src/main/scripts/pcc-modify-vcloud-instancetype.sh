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
while getopts "f:h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   f) FARM_NAME="$OPTARG" ;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
        echo "myCloud名を指定してください。"
        echo "Example: `basename $0` -f myCloud"
        exit 1
fi

if [ -n "${FARM_NAME}" ]; then
        echo "${FARM_NAME} のインスタンス一覧(vCloudのみ)を IMAGE_NO:FQDN の形式で表示します。"

        #設定確認
        sh pcc-check.sh
        if [ $? -ne 0 ]; then
            echo "接続テストに失敗しました。management-config.propertiesの設定を確認して下さい。"
            exit 1
        fi

        #インスタンス一覧の取得
        SQL1="SELECT CONCAT(IMAGE_NO,':', FQDN) FROM adc.INSTANCE, adc.PLATFORM, adc.FARM WHERE FARM_NAME='"${FARM_NAME}"' AND INSTANCE.PLATFORM_NO=PLATFORM.PLATFORM_NO AND INSTANCE.FARM_NO=FARM.FARM_NO AND PLATFORM.PLATFORM_TYPE='vcloud'"
        RES1=`mysql -u adc -ppassw0rd -e "${SQL1}" | sed -e "1s/.*$//g"`
        for a in ${RES1[@]}; do
          echo "${a}"
        done

        #IMAGE変更インスタンスの入力値取得
        echo "インスタンスタイプを追加するサーバ名(FQDN)を指定してください。"
        echo -n "FQDN ==>"
        read FQDN
        #echo ${FQDN}

        #入力値FQDNの確認
        SQL2="SELECT CONCAT('COUNT=', COUNT(*)) FROM adc.INSTANCE WHERE FQDN='"${FQDN}"'"
        RES2=`mysql -u adc -ppassw0rd -e "${SQL2}" | sed -e "1s/.*$//g"`
        eval `echo $RES2`
        if [ "${COUNT}" != "1" ]; then
          echo "入力されたFQDNに該当するインスタンスが存在しません。"
          echo "再度、ツールを実行してください。"
          exit 0
        fi

        #IMAGE_NOの取得
        SQL3="SELECT CONCAT('IMAGE_NO=', IMAGE_NO) FROM adc.INSTANCE WHERE FQDN='"${FQDN}"'"
        RES3=`mysql -u adc -ppassw0rd -e "${SQL3}" | sed -e "1s/.*$//g"`
        eval `echo $RES3`

        #インスタンスタイプ一覧の取得
        SQL5="SELECT INSTANCE_TYPE_NAME FROM adc.PLATFORM_VCLOUD_INSTANCE_TYPE ORDER BY INSTANCE_TYPE_NO"
        RES5=`mysql -u adc -ppassw0rd -e "${SQL5}" | sed -e "1s/.*$//g"`
        for a in ${RES5[@]}; do
          echo $a
        done
        #インスタンスタイプの入力値取得
        echo "追加したいインスタンスタイプを指定してください。"
        echo -n "INSTANCE_TYPE ==>"
        read INSTANCE_TYPE

        #現在設定可能なインスタンスタイプの取得
        SQL7="SELECT CONCAT('INSTANCE_TYPES=', INSTANCE_TYPES) FROM adc.IMAGE_VCLOUD WHERE IMAGE_NO="${IMAGE_NO}"+5"
        RES7=`mysql -u adc -ppassw0rd -e "${SQL7}" | sed -e "1s/.*$//g"`
        eval `echo $RES7`

        #入力値の確認
        echo "入力された値は下記となります。"
        echo "Server Name ( FQDN )     ：${FQDN}"
        echo "selectable Instance Types：${INSTANCE_TYPES}"
        echo "add new Instance Type    ：${INSTANCE_TYPE}"
        echo -n "処理を進めますか？(y/n) ==>"
        read INPUT_FLG
        if [ "${INPUT_FLG}" != "y" ]; then
            echo "処理を終了します。再度実行ください。"
            exit 1
        fi

        #入力値の反映
        SQL6="UPDATE adc.IMAGE_VCLOUD SET INSTANCE_TYPES=CONCAT(INSTANCE_TYPES, '",${INSTANCE_TYPE}"') WHERE IMAGE_NO="${IMAGE_NO}"+5"
        RES6=`mysql -u adc -ppassw0rd -e "${SQL6}" | sed -e "1s/.*$//g"`

        #設定変更の判定
        if [ "${RES6}" = "" ]; then
          echo "カスタム設定１を実行しました。"
        else
          echo "カスタム設定の変更に失敗しました。"
          exit 1
        fi

        SQL4="UPDATE adc.INSTANCE SET IMAGE_NO="${IMAGE_NO}"+5 WHERE FQDN='"${FQDN}"'"
        RES4=`mysql -u adc -ppassw0rd -e "${SQL4}" | sed -e "1s/.*$//g"`
        ##echo "length of RES4 : "${#RES4}

        if [ "${RES4}" = "" ]; then
          echo "カスタム設定２を実行しました。"
        else
          echo "カスタム設定の変更に失敗しました。"
          exit 1
        fi

elif [ "${HELP}" = "TRUE" ]; then
        echo ""
        echo ">>>>>> `basename $0` コマンドはヘルプ機能に対応していません。<<<<<<"
        echo ""
        sh pcc-usage.sh
        exit 0
else
        echo "myCloud名を指定してください。"
        echo "Example: `basename $0` -f myCloud"
        exit 1
fi
