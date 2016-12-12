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
package jp.primecloud.auto.api.lb;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.lb.EditLoadBalancerResponse;
import jp.primecloud.auto.common.constant.PCCConstant;
import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.entity.crud.AwsLoadBalancer;
import jp.primecloud.auto.entity.crud.CloudstackLoadBalancer;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.PlatformAws;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;

@Path("/EditLoadBalancer")
public class EditLoadBalancer extends ApiSupport {

    /**
     * ロードバランサ編集
     *
     * @param loadBalancerNo ロードバランサ番号
     * @param componentNo コンポーネント番号
     * @param securityGroups セキュリティグループ(カンマ区切り、複数)
     * @param cidrBlock サブネット(cidrBlockのカンマ区切り、複数)
     * @param comment コメント
     * @return EditLoadBalancerResponse
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EditLoadBalancerResponse editLoadBalancer(@QueryParam(PARAM_NAME_LOAD_BALANCER_NO) String loadBalancerNo,
            @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
            @QueryParam(PARAM_NAME_SECURITY_GROUPS) String securityGroups,
            @QueryParam(PARAM_NAME_SUBNET) String cidrBlock, @QueryParam(PARAM_NAME_COMMENT) String comment,
            @QueryParam(PARAM_NAME_IS_INTERNAL) String isInternal) {

        // 入力チェック
        // LoadBalancerNo
        ApiValidate.validateLoadBalancerNo(loadBalancerNo);

        LoadBalancer loadBalancer = getLoadBalancer(Long.parseLong(loadBalancerNo));

        // 権限チェック
        User user = checkAndGetUser(loadBalancer);

        // ComponentNo
        ApiValidate.validateComponentNo(componentNo);

        Component component = getComponent(Long.parseLong(componentNo));

        if (BooleanUtils.isFalse(component.getFarmNo().equals(loadBalancer.getFarmNo()))) {
            //ファームとコンポーネントが一致しない
            throw new AutoApplicationException("EAPI-100022", "Component", loadBalancer.getFarmNo(),
                    PARAM_NAME_COMPONENT_NO, componentNo);
        }

        //vpcId取得
        PlatformAws platformAws = null;
        String vpcId = null;
        if (LB_TYPE_ELB.equals(loadBalancer.getType())) {
            platformAws = platformAwsDao.read(loadBalancer.getPlatformNo());
            if (platformAws.getVpc() && StringUtils.isNotEmpty(platformAws.getVpcId())) {
                vpcId = platformAws.getVpcId();
            }
        }

        //SecurityGroups
        if (LB_TYPE_ELB.equals(loadBalancer.getType()) && platformAws.getVpc()) {
            //ELB+VPCの場合
            ApiValidate.validateSecurityGroups(securityGroups);
            if (checkSecurityGroups(user.getUserNo(), loadBalancer.getPlatformNo(), vpcId, securityGroups) == false) {
                //プラットフォームにセキュリティグループが存在しない
                throw new AutoApplicationException("EAPI-100019", loadBalancer.getPlatformNo(), securityGroups);
            }
        }

        //Subnet
        String subnetId = null;
        String availabilityZone = null;
        if (LB_TYPE_ELB.equals(loadBalancer.getType()) && platformAws.getVpc()) {
            //ELB+VPCの場合
            ApiValidate.validateSubnet(cidrBlock);
            List<String> cidrBlocks = commaTextToList(cidrBlock);
            List<Subnet> subnets = getSubnet(user.getUserNo(), loadBalancer.getPlatformNo(), vpcId, cidrBlocks);
            if (subnets.size() != cidrBlocks.size()) {
                //サブネットがプラットフォームに存在しない
                throw new AutoApplicationException("EAPI-000017", loadBalancer.getPlatformNo(), cidrBlock);
            }
            StringBuffer subnetBuffer = new StringBuffer();
            StringBuffer zoneBuffer = new StringBuffer();
            List<String> zones = new ArrayList<String>();
            for (Subnet subnet : subnets) {
                if (zones.contains(subnet.getAvailabilityZone())) {
                    //同じゾーンのサブネットを複数選択している場合
                    throw new AutoApplicationException("EAPI-100032", cidrBlock);
                }
                zones.add(subnet.getAvailabilityZone());
                subnetBuffer.append(subnetBuffer.length() > 0 ? "," + subnet.getSubnetId() : subnet.getSubnetId());
                zoneBuffer.append(zoneBuffer.length() > 0 ? "," + subnet.getAvailabilityZone() : subnet
                        .getAvailabilityZone());
            }
            subnetId = subnetBuffer.toString();
            availabilityZone = zoneBuffer.toString();
        }

        // Comment
        ApiValidate.validateComment(comment);

        // isInternal
        boolean internal = false;
        if (LB_TYPE_ELB.equals(loadBalancer.getType()) && platformAws.getVpc()) {
            if (isInternal != null) {
                ApiValidate.validateIsStaticIp(isInternal);
                internal = Boolean.parseBoolean(isInternal);
            } else {
                AwsLoadBalancer awsLoadBalancer = awsLoadBalancerDao.read(Long.parseLong(loadBalancerNo));
                internal = awsLoadBalancer.getInternal();
            }
        } else {
            if (isInternal != null) {
                // ELB かつ プラットフォームがVPC以外の場合は内部ロードバランサ指定不可
                throw new AutoApplicationException("EAPI-100040", loadBalancerNo);
            }
        }

        CloudstackLoadBalancer cloudstackLoadBalancer = null;
        if (PCCConstant.LOAD_BALANCER_CLOUDSTACK.equals(loadBalancer.getType())) {
            cloudstackLoadBalancer = cloudstackLoadBalancerDao.read(Long.parseLong(loadBalancerNo));
        }

        LoadBalancerStatus status = LoadBalancerStatus.fromStatus(loadBalancer.getStatus());
        if (LoadBalancerStatus.WARNING == status) {
            // ロードバランサ ステータスが Warning
            throw new AutoApplicationException("EAPI-100025", loadBalancerNo);
        }

        // ロードバランサ 編集
        if (LB_TYPE_ELB.equals(loadBalancer.getType())) {
            //EBS(Elastic Load Balance)
            //TODO
            loadBalancerService.updateAwsLoadBalancer(Long.parseLong(loadBalancerNo),
                    loadBalancer.getLoadBalancerName(), comment, Long.parseLong(componentNo), subnetId, securityGroups,
                    availabilityZone, internal);
        } else if (LB_TYPE_ULTRA_MONKEY.equals(loadBalancer.getType())) {
            //UltraMonkey
            loadBalancerService.updateUltraMonkeyLoadBalancer(Long.parseLong(loadBalancerNo),
                    loadBalancer.getLoadBalancerName(), comment, Long.parseLong(componentNo));
        } else if (LB_TYPE_CLOUDSTACK.equals(loadBalancer.getType())) {
            //CloudStack
            loadBalancerService.updateCloudstackLoadBalancer(Long.parseLong(loadBalancerNo),
                    loadBalancer.getLoadBalancerName(), comment, Long.parseLong(componentNo),
                    cloudstackLoadBalancer.getAlgorithm(), cloudstackLoadBalancer.getPublicport(),
                    cloudstackLoadBalancer.getPrivateport());
        }

        EditLoadBalancerResponse response = new EditLoadBalancerResponse();

        return response;
    }

    /**
     * サブネットの取得
     *
     * @param userNo ユーザ番号
     * @param platformNo プラットフォーム番号
     * @param vpcId vpcId
     * @param cidrBlocks cidrBlockのリスト
     * @return List<Subnet> cidrBlockに合致するサブネットのみ取得
     */
    private List<Subnet> getSubnet(Long userNo, Long platformNo, String vpcId, List<String> cidrBlocks) {
        List<Subnet> subnets = new ArrayList<Subnet>();
        if (cidrBlocks.size() > 0) {
            List<Subnet> subnets2 = awsDescribeService.getSubnets(userNo, platformNo);
            for (Subnet subnet : subnets2) {
                if (cidrBlocks.contains(subnet.getCidrBlock())) {
                    subnets.add(subnet);
                }
            }
        }
        return subnets;
    }

    /**
     * セキュリティグループ名がプラットフォームに存在するかのチェック
     *
     * @param userNo ユーザ番号
     * @param platformNo プラットフォーム番号
     * @param vpcId vpcId
     * @param securityGroups セキュリティグループ(カンマ区切り、複数)
     * @return true:存在する、false:存在しない
     */
    private boolean checkSecurityGroups(Long userNo, Long platformNo, String vpcId, String securityGroups) {
        boolean isContain = false;
        if (StringUtils.isNotEmpty(securityGroups)) {
            List<String> groupNames = getSecurityGroupNames(userNo, platformNo, vpcId);
            for (String grouName : securityGroups.split(",")) {
                if (groupNames.contains(grouName.trim())) {
                    isContain = true;
                } else {
                    return false;
                }
            }
        }
        return isContain;
    }

    /**
     * SecurityGroupNameの一覧を取得する
     *
     * @param userNo ユーザ番号
     * @param platformNo プラットフォーム番号
     * @param vpcId vpcId(vpc以外の場合はNULL)
     * @return SecurityGroupNameの一覧
     */
    private List<String> getSecurityGroupNames(Long userNo, Long platformNo, String vpcId) {
        List<String> groupNames = new ArrayList<String>();
        List<SecurityGroup> groups = awsDescribeService.getSecurityGroups(userNo, platformNo);
        for (SecurityGroup group : groups) {
            groupNames.add(group.getGroupName());
        }
        return groupNames;
    }

    /**
     * カンマ区切りの文字列からリストを作成する
     *
     * @param instanceTypesText カンマ区切りのインスタンスタイプ(名称)文字列
     * @return インスタンスタイプ(名称)のリスト
     */
    private static List<String> commaTextToList(String commaText) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isNotEmpty(commaText)) {
            for (String splitStr : StringUtils.split(commaText, ",")) {
                list.add(splitStr.trim());
            }
        }
        return list;
    }

}
