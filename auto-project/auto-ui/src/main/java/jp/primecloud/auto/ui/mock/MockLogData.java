package jp.primecloud.auto.ui.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import jp.primecloud.auto.log.entity.crud.EventLog;

public class MockLogData {

    private List<EventLog> eventLogs;

    public MockLogData(int sampleDataCount) {
        eventLogs = new ArrayList<EventLog>();
        setEventLogs(sampleDataCount);
    }

    private void setEventLogs(int sampleDataCount) {
        for (int i = 0; i < sampleDataCount; i++) {
            eventLogs.add(getEventLogSample());
        }
    }

    public EventLog getEventLogSample() {

        String componentName = "tomcat";
        String userName = "ayano";
        String farmName = "eucaly";
        String instanceName = "webap";

        EventLog eventLog = new EventLog();
        eventLog.setLogDate(getRandomDate());
        int componentNo = getRandomInt(10);
        eventLog.setComponentNo(new Long(componentNo));
        eventLog.setComponentName(componentName + componentNo);
        eventLog.setLogLevel(getRandomLogLevel());
        eventLog.setMessageCode("TestCode");
        int farmNo = getRandomInt(10);
        eventLog.setFarmName(farmName + farmNo);
        eventLog.setFarmNo(new Long(farmNo));
        int instanceNo = getRandomInt(10);
        eventLog.setInstanceName(instanceName + instanceNo);
        eventLog.setInstanceNo(new Long(instanceNo));
        int userNo = getRandomInt(10);
        eventLog.setUserName(userName + userNo);
        eventLog.setUserNo(new Long(userNo));
        eventLog.setMessage(getRandomMessage());

        return eventLog;
    }

    public Integer getRandomLogLevel() {
        Integer result = 0;
        switch (getRandomInt(6)) {
            case 1:
                result = 100;
                break;
            case 2:
                result = 40;
                break;
            case 3:
                result = 30;
                break;
            case 4:
                result = 20;
                break;
            case 5:
                result = 10;
                break;
            case 6:
                result = 0;
                break;
        }
        return result;

    }

    public String getRandomMessage() {
        String result = "メッセージ";
        switch (getRandomInt(5)) {
            case 1:
                result = "インスタンス i-4B9907A2 を起動しました。";
                break;
            case 2:
                result = "172.22.10.102 の逆引きレコードを lb559.testaaa.dev.csk-cc.com として追加しました。";
                break;
            case 3:
                result = "ホスト 10865 を更新しました。";
                break;
            case 4:
                result = "コンポーネント mysql を開始します。";
                break;
            default:
                result = "コンポーネントを設定します。";
                break;
        }
        return result;
    }

    public int getRandomInt(int x) {
        int randomNumber = (int) (Math.random() * x + 1);
        return randomNumber;
    }

    /**
     *
     * 本日から6ヶ月前までの日時をランダムで出力します
     *
     * @return
     */
    public Date getRandomDate() {
        int offsetMonth = 6;
        long val1 = System.currentTimeMillis();

        Calendar cdr = Calendar.getInstance();
        cdr.setTimeInMillis(val1);
        cdr.set(Calendar.MONTH, cdr.get(Calendar.MONTH) - offsetMonth);
        cdr.set(Calendar.HOUR_OF_DAY, 20);
        cdr.set(Calendar.MINUTE, 0);
        cdr.set(Calendar.SECOND, 0);
        long val2 = cdr.getTimeInMillis();

        Random r = new Random();
        long randomTS = (long) (r.nextDouble() * (val2 - val1)) + val1;
        Date d = new Date(randomTS);

        return d;
    }

    /**
     * eventLogsを取得します。
     *
     * @return eventLogs
     */
    public List<EventLog> getEventLogs() {
        return eventLogs;
    }
}
