package jp.primecloud.auto.puppet;

public class PuppetScheduler {

    private static PuppetScheduler ps= new PuppetScheduler();

    private PuppetScheduler(){}

    public static PuppetScheduler getInstance(){
        return ps;
    }

    public synchronized void doStop(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            //むしする
        }
    }
}
