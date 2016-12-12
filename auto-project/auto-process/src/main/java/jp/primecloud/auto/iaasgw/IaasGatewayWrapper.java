/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.iaasgw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.util.MessageUtils;
import jp.primecloud.auto.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IaasGatewayWrapper{

    protected Long platformNo;

    protected Long userNo;

    protected Log log = LogFactory.getLog(getClass());

    protected EventLogger eventLogger;

    public IaasGatewayWrapper(Long userNo, Long platformNo, EventLogger eventLogger){
        this.platformNo = platformNo;
        this.userNo = userNo;
        this.eventLogger = eventLogger;
    }

    public String excGateway(String gwMod, List<String> gwParams){
        try{
            //パスは環境によって異なります　引数は
            //1 : Python.exeのパス       Ex."/C:/Python27/python"
            //2 : 実行モジュールパス     Ex."./StartInstance.py"
            //3 : 利用ユーザ
            //4 : プラットフォーム
            //5 : 以降は呼び出すGatewayの仕様に沿ってください

            List<String> params = new ArrayList<String>();
            StringBuilder logstr = new StringBuilder("");
            params.add(System.getenv().get("PYTHON_HOME"));
            params.add(System.getenv().get("IAASGW_HOME") + File.separatorChar + gwMod);
            params.add(String.valueOf(this.userNo));
            params.add(String.valueOf(this.platformNo));
            if (gwParams != null && gwParams.size() > 0) {
                params.addAll(gwParams);
                for (String item: gwParams) {
                    if(!"".equals(logstr)){
                        logstr.append(",");
                    }
                    logstr.append(item);
                }
            }

            ProcessBuilder pb = new ProcessBuilder(params);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.format("Call start IaasGateway (Method={0}, Params={1}) ", gwMod, logstr.toString()));
            }

            StreamThread is = new StreamThread(p.getInputStream());
            StreamThread es = new StreamThread(p.getErrorStream());
            is.start();
            es.start();

            //プロセスの終了待ち
            p.waitFor();

            //InputStreamのスレッド終了待ち
            is.join();
            es.join();

            String processOut = "";
            if(p.exitValue() == 0){
                //標準出力の内容を出力
                for (String s : is.getStringList()) {
                    if(s.startsWith("RESULT:")){
                        //リザルト出力を受け取る
                        processOut = s.replace("RESULT:", "");
                    }
                }
            }else{
                //標準エラーの内容を出力
                String rastErrMsg = "";
                for (String s : es.getStringList()) {
                    rastErrMsg = s;
                }

                //エラー出力から取れない場合は標準出力からとる
                if ("".equals(rastErrMsg)){
                    for (String s : is.getStringList()) {
                        rastErrMsg = s;
                    }
                }
                log.error(MessageUtils.format("IAAS GATEWAY ERR:{0}",rastErrMsg));
                throw new IaasgwException(rastErrMsg);
            }

            //リザルトはこのような形→itemi1#item2##itemi1#item2
            if (log.isInfoEnabled()) {
                log.info(MessageUtils.format("Call nomal end IaasGateway (Method={0}, Params={1}) ", gwMod, logstr.toString()));
            }
            return processOut;

        }catch (Exception e){
            throw handleException(e, gwMod, gwParams);
        }
    }


    public String describeKeyPairs() {
        return excGateway("DescribeKeyPairs.py", null);
    }

    public String describeSecurityGroups(String vpcId) {
        List<String> params = new ArrayList<String>();
        if(vpcId != null && !vpcId.equals("")) {
            params.add(String.valueOf(vpcId));
        }
        return excGateway("DescribeSecurityGroups.py", params);
    }


    public String describeAvailabilityZones() {
        return excGateway("DescribeAvailabilityZones.py", null);
    }

    public String describeSubnets(String vpcId) {
        List<String> params = new ArrayList<String>();
        if(vpcId != null && !vpcId.equals("")) {
            params.add(String.valueOf(vpcId));
        }
        return excGateway("DescribeSubnets.py", params);
    }

    public String describeNetworks() {
        List<String> params = new ArrayList<String>();
        return excGateway("DescribeNetworks.py", params);
    }

    public String describeAzureSubnets(String networkName) {
        List<String> params = new ArrayList<String>();
        if(networkName != null && !networkName.equals("")) {
            params.add(String.valueOf(networkName));
        }
        return excGateway("DescribeAzureSubnets.py", params);
    }

    public String describeFlavors(String flavorIds) {
        List<String> params = new ArrayList<String>();
        if(flavorIds != null && !flavorIds.equals("")) {
            params.add(String.valueOf(flavorIds));
        }
        return excGateway("DescribeFlavors.py", params);
    }

    /*****************************
     *  MyCloud
     *****************************/

    //Create VCloud vApp
    public void createMyCloud(String farmName) {
        List<String> params = new ArrayList<String>();
        params.add(farmName);
        excGateway("CreateMyCloud.py", params);
    }

    //Delete VCloud vApp
    public void deleteMyCloud(Long farmNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(farmNo));
        excGateway("DeleteMyCloud.py", params);
    }

    /*****************************
     *  Instances
     *****************************/

    public void startInstance(Long instanceNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(instanceNo));
        excGateway("StartInstance.py", params);
    }

    public void stopInstance(Long instanceNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(instanceNo));
        excGateway("StopInstance.py", params);
    }

    public void terminateInstance(String instanceId) {
        List<String> params = new ArrayList<String>();
        params.add(instanceId);
        excGateway("TerminateInstance.py", params);
    }

    /*****************************
     *  Volume
     *****************************/

    public void stopVolume(Long instanceNo, Long  volumeNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(instanceNo));
        params.add(String.valueOf(volumeNo));
        excGateway("StopVolume.py", params);
    }

    public void startVolume(Long instanceNo, Long  volumeNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(instanceNo));
        params.add(String.valueOf(volumeNo));
        excGateway("StartVolume.py", params);
    }

    public void deleteVolume(String  volumeId) {
        List<String> params = new ArrayList<String>();
        params.add(volumeId);
        excGateway("DeleteVolume.py", params);
    }

    /*****************************
     *  Addresses
     *****************************/

    public String allocateAddress() {
        List<String> params = new ArrayList<String>();
        return excGateway("AllocateAddress.py", params);
    }

    public void releaseAddress(String publicIp) {
        List<String> params = new ArrayList<String>();
        params.add(publicIp);
        excGateway("ReleaseAddress.py", params);
    }

    /*****************************
     *  KeyPairs
     *****************************/
    public String createKeyPair(String keyName) {
        List<String> params = new ArrayList<String>();
        params.add(keyName);
        return excGateway("CreateKeyPair.py", params);
    }


    public void deleteKeyPair(String keyName) {
        List<String> params = new ArrayList<String>();
        params.add(keyName);
        excGateway("DeleteKeyPair.py", params);
    }


    public void importKeyPair(String keyName, String publicKeyMaterial) {
        List<String> params = new ArrayList<String>();
        params.add(keyName);
        params.add(publicKeyMaterial);
        excGateway("ImportKeyPair.py", params);
    }

    /*****************************
     *  Snapshot
     *****************************/
    public String createSnapshot(String volumeId) {
        List<String> params = new ArrayList<String>();
        params.add(volumeId);
        return excGateway("CreateSnapshot.py", params);
    }


    public void deleteSnapshot(String snapshotId) {
        List<String> params = new ArrayList<String>();
        params.add(snapshotId);
        excGateway("DeleteSnapshot.py", params);
    }

    /*****************************
     *  LoadBalancer
     *****************************/
    public void startLoadBalancer(Long loadBalancerNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(loadBalancerNo));
        excGateway("StartLoadBalancer.py", params);
    }


    public void stopLoadBalancer(Long loadBalancerNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(loadBalancerNo));
        excGateway("StopLoadBalancer.py", params);
    }


    public void configureLoadBalancer(Long loadBalancerNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(loadBalancerNo));
        excGateway("ConfigureLoadBalancer.py", params);
    }


    /*****************************
     *  Other
     *****************************/

    public String getPasswordData(String instanceNo) {
        List<String> params = new ArrayList<String>();
        params.add(instanceNo);
        return excGateway("GetPasswordData.py", params);
    }

    public boolean synchronizeCloud(Long farmNo) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(farmNo));
        excGateway("SynchronizeCloud.py", params);
        return true;
    }

    /*****************************
     *  handleException
     *****************************/
    protected AutoException handleException(Exception exception, String actionName) {
        return handleException(exception, actionName, null);
    }

    protected AutoException handleException(Exception exception, String actionName, Object param) {
        if (exception instanceof IaasgwException){
            // イベントログ出力
            if (eventLogger != null) {
                eventLogger.error("SystemError", new Object[] { exception.getMessage() });
            }
        }

        String str = StringUtils.reflectToString(param);
        return new AutoException("EIAASGWP-300001", exception, actionName, str);
    }

}
