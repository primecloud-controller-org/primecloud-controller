#!/bin/sh

PROGRAM="$0"
PROGRAM_DIR=`dirname $PROGRAM`
TOOL_HOME=`cd "$PROGRAM_DIR/.."; pwd`

JAVA_OPTS="-Dtool.home=$TOOL_HOME"
CLASSPATH="$TOOL_HOME/config/:$TOOL_HOME/lib/*"

#オプションのパース
while getopts "h" opt
do
   case $opt in
   \?) OPT_ERROR=1; break;;
   h) HELP="TRUE"; break;;
   esac
done
shift  $(($OPTIND - 1))

if [ $OPT_ERROR ]; then
		echo "正しいオプションを入力してください。"
		exit 1
fi

if [ "${HELP}" = "TRUE" ]; then
        sh pcc-usage.sh
        exit 0
else
#使用可能プラットフォーム一覧の取得
		#設定確認
        sh pcc-check.sh
		if [ $? -ne 0 ]; then
			echo "接続テストに失敗しました。management-config.propertiesの設定を確認して下さい。"
			exit 1
		fi

		java $JAVA_OPTS -cp $CLASSPATH jp.primecloud.auto.tool.management.main.Main -P

		exit 0
fi
