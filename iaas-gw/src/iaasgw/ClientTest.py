 # coding: UTF-8
 #
 # Copyright 2014 by SCSK Corporation.
 # 
 # This file is part of PrimeCloud Controller(TM).
 # 
 # PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 2 of the License, or
 # (at your option) any later version.
 # 
 # PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 # 
from iaasgw.client.ec2iaasclient import EC2IaasClient
from iaasgw.client.ec2iaasclientLB import EC2IaasClientLB
from iaasgw.module.ec2.ec2module import TagSet, Listener
from libcloud.compute.base import NodeImage, NodeSize
import sys
import time


if __name__ == '__main__':

    param = sys.argv
    print "0:" + param[0]
    print "1:" + param[1]

    flg = param[1]

    userinfo = EC2Users('wakabayashi')
    client = EC2IaasClient(userinfo.getAccessId(), userinfo.getSecretKey())
    clientLB = EC2IaasClientLB(userinfo.getAccessId(), userinfo.getSecretKey())

    if flg == '1':
        print u"====参照系のテスト===="

        print "====START describeImages===="
        images = client.describeImages()
        for image in images:
            print "==== Image ===="
            print 'imageId      :' + image.id
            print 'name         :' + image.name

        print "====START describeInstances===="
        ins = client.describeInstances("i-7cdfd47d")
        print "==== Instances ===="
        print 'instanceId      :' + ins.id
        print 'name            :' + ins.name


        print "====START describeVolumes===="
        vol = client.describeVolumes("vol-8d39c2e1")
        print "==== Volume ===="
        print 'volumeId         :' + vol.volumeId
        print 'size             :' + vol.size
        print 'createTime       :' + vol.createTime

        print "====START describeAddresses===="
        ad = client.describeAddresses()
        print "==== Addresses ===="
        print 'publicIp         :' + ad.publicIp
        print 'domain           :' + str(ad.domain)
        print 'instanceId       :' + ad.instanceId

        print "====START describeKeyPairs===="
        kps = client.describeKeyPairs()
        for kp in kps:
            print "==== KeyPairs ===="
            print 'keyName         :' + kp.keyName
            print 'keyFingerprint  :' + kp.keyFingerprint

        print "====START describeSecurityGroups===="
        sg = client.describeSecurityGroups()
        print "==== SecurityGroups ===="
        print 'groupId           :' + str(sg.groupId)
        print 'groupName         :' + str(sg.groupName)
        print 'groupDescription  :' + str(sg.groupDescription)

        print "====START describeAvailabilityZones===="
        zones = client.describeAvailabilityZones()
        for zone in zones:
            print "==== AvailabilityZones ===="
            print 'zoneName           :' + zone.name
            print 'zoneState          :' + zone.zone_state
            print 'regionName         :' + zone.region_name


        print "====START describeAvailabilityZones===="
        sps = client.describeSnapshots()
        for sp in sps:
            print "==== Snapshots ===="
            print 'snapshotId           :' + sp.snapshotId
            print 'volumeId             :' + sp.volumeId
            print 'description          :' + sp.description

        print "====START describeRegions===="
        regs = client.describeRegions()
        for reg in regs:
            print "==== Regions ===="
            print 'regionName           :' + reg.regionName
            print 'regionEndpoint       :' + reg.regionEndpoint


        print "====START describeVpcs===="
        vpcs = client.describeVpcs()
        for vpc in vpcs:
            print "==== Vpc ===="
            print 'vpcId           :' + vpc.vpcId
            print 'state           :' + vpc.state

        print "====START describeSubnets===="
        sns = client.describeSubnets()
        for sn in sns:
            print "==== Subnet ===="
            print 'subnetId           :' + sn.subnetId
            print 'state              :' + sn.state


        print "====START describeTags===="
        tags = client.describeTags()
        for tag in tags:
            print "==== Tag ===="
            print 'resourceId         :' + tag.resourceId
            print 'key                :' + tag.key
            print 'value              :' + tag.value


    elif flg == '2':

        print "====START describeLoadBalancers===="
        lbs = clientLB.describeLoadBalancers()
        for lb in lbs:
            print "==== LoadBalancer ===="
            print 'loadBalancerName     :' + lb.loadBalancerName
            print 'dnsName              :' + lb.dnsName

        print "====START describeInstanceHealth===="
        instanceStates = clientLB.describeInstanceHealth(lbs[0].loadBalancerName)
        for instanceState in instanceStates:
            print "==== instanceState ===="
            print 'description     :' + instanceState.description
            print 'state           :' + instanceState.state

    elif flg == '3':
        print u"====インスタンス操作系のテスト===="


        print "====START runInstances===="

        image1 = NodeImage('ami-dcfa4edd', 'TESTIMAGE', None)
        size1 = NodeSize('t1.micro', None, None,None,None,None,None)

        node = client.runInstances(image=image1,size=size1, name='LIBCLOUD TEST')
        nodeid = node.id

        ins = client.describeInstances(nodeid)
        while ins.state != 0:
            time.sleep(10)
            print u"====インスタンス起動中====:", ins.state
            ins = client.describeInstances(nodeid)

        print nodeid + u"を作成しました"


        print "====START terminateInstances===="
        ins = client.describeInstances(nodeid)
        print u"====現在のステータス====:", ins.state

        isSuccess = client.terminateInstances(nodeid)
        if isSuccess:
            print nodeid + u"を削除しました。"
        else:
            print nodeid + u"の削除に失敗しました。"


        print "====START startInstances===="
        previousState = client.startInstances("i-7cdfd47d")
        print "previousState   :", previousState

        ins = client.describeInstances("i-7cdfd47d")
        print "====WAITING====:", ins.state
        while ins.state != 0:
            time.sleep(10)
            print "====WAITING====:", ins.state
            ins = client.describeInstances("i-7cdfd47d")
        print u"i-7cdfd47dを開始しました。"


        print "====START stopInstances===="
        previousState = client.stopInstances("i-7cdfd47d")
        print "previousState   :", previousState

        ins = client.describeInstances("i-7cdfd47d")
        print "====WAITING====:", ins.state
        while ins.state != 4:
            time.sleep(10)
            print "====WAITING====:", ins.state
            ins = client.describeInstances("i-7cdfd47d")

        print u"i-7cdfd47dを停止しました。"

    elif flg == '4':
        print u"====ボリューム操作系のテスト===="

        #事前準備ゾーンの取得
        zones = client.describeAvailabilityZones()
        useZone = zones[0]

        print "====START createVolume===="
        volume = client.createVolume(useZone.name, size = "1")
        volumeId = volume.volumeId

        print volumeId + u'のボリュームを作成しました。'

        #少し待つ
        time.sleep(60)

#        volumeId = "vol-c531f2a9"
        print "====START deleteVolume===="
        attachmentSet = client.deleteVolume( volumeId)
        print volumeId + u'のボリュームを削除しました。'




        print "====START attachVolume===="
        attachmentSet = client.attachVolume( "vol-8d39c2e1", 'i-7cdfd47d', "/dev/sdf")
        print u'i-7cdfd47dをアタッチしました'

        #少し待つ
        time.sleep(60)

        print "====START detachVolume===="
        attachmentSet = client.detachVolume( "vol-8d39c2e1", 'i-7cdfd47d', '/dev/sdf')
        print u'i-7cdfd47dをデタッチしました'

    elif flg == '5':
        print u"====アドレス操作系のテスト===="

        print "====START startInstances===="
        previousState = client.startInstances("i-7cdfd47d")
        print "previousState   :", previousState

        ins = client.describeInstances("i-7cdfd47d")
        print "====WAITING====:", ins.state
        while ins.state != 0:
            time.sleep(10)
            print "====WAITING====:", ins.state
            ins = client.describeInstances("i-7cdfd47d")
        print u"i-7cdfd47dを開始しました。"


        print "====START allocateAddress===="
        publicIp = client.allocateAddress()
        print publicIp
        print publicIp + u'を確保しました'

        print "====START associateAddress===="
        client.associateAddress(publicIp, 'i-7cdfd47d')
        print publicIp + u'をi-7cdfd47dへ割り当てました'

        #publicIp = '176.34.28.125'
        print "====START disassociateAddress===="
        client.disassociateAddress(publicIp)
        print publicIp + u'の割り当てを解除しました'

        print "====START releaseAddress===="
        client.releaseAddress(publicIp)
        print publicIp + u'を開放しました'

        print "====START stopInstances===="
        previousState = client.stopInstances("i-7cdfd47d")
        print "previousState   :", previousState

        ins = client.describeInstances("i-7cdfd47d")
        print "====WAITING====:", ins.state
        while ins.state != 4:
            time.sleep(10)
            print "====WAITING====:", ins.state
            ins = client.describeInstances("i-7cdfd47d")

        print u"i-7cdfd47dを停止しました。"


    elif flg == '6':
        print u"====キーペア操作系のテスト===="

        print "====START createKeyPair===="
        keypair = client.createKeyPair("TESTUSER")
        print keypair.keyName
        print keypair.keyFingerprint
        print keypair.keyMaterial

        print "====START deleteKeyPair===="
        client.deleteKeyPair("TESTUSER")
        print u'キーペア削除しました'


#        print "====START importKeyPair===="
#        keypair2 = client.importKeyPair("TESTUSER", keypair.keyMaterial)
#        print keypair2
#
#
#        print "====後処理===="
#        keypair = client.deleteKeyPair("TESTUSER")
#        print u'キーペア削除しました'


    elif flg == '7':
        print u"====スナップショット操作系のテスト===="

        print "====START createSnapshot===="
        snapshot = client.createSnapshot("vol-8d39c2e1")
        print snapshot


        print "====START deleteSnapshot===="
        client.deleteSnapshot(snapshot.snapshotId)
        print u'スナップショット削除しました'

    elif flg == '8':
        print u"====タグ操作系のテスト===="

        tag = TagSet(None, None, 'TEST', 'TSET')
        tags = []
        tags.append(tag)

        print "====START createTags===="
        client.createTags('i-7cdfd47d', tags)
        print u'タグを作成しました'


        print "====START deleteSnapshot===="
        client.deleteTags('i-7cdfd47d', tags)
        print u'タグを削除しました'

    elif flg == '9':
        print u"====その他のテスト===="

        print "====START getPasswordData===="
        passwdData = client.getPasswordData('i-7cdfd47d')
        print passwdData


    elif flg == '9':
        print u"====その他のテスト===="
        print "====START getPasswordData===="
        passwdData = client.getPasswordData('i-7cdfd47d')
        print passwdData

    elif flg == '10':
        print u"====ロードバランサーのテスト===="
        #リスナー作成
        listener = Listener("65535", "TCP", "65535","TCP",None)
        listeners = [listener]

        #事ゾーンの取得
        zones = client.describeAvailabilityZones()
        useZones = [zones[0]]

        #インスタンスの取得
        ins = client.describeInstances("i-7cdfd47d")
        instances = [ins.id]

        lbs = clientLB.describeLoadBalancers()
        for lb in lbs:
            print "==== LoadBalancer ===="
            print 'loadBalancerName     :' + lb.loadBalancerName
            print 'dnsName              :' + lb.dnsName


#        print "====START createLoadBalancer===="
#        dnsName = clientLB.createLoadBalancer(useZones, listeners, "TESTBALANCER")
#        print dnsName + u"を作成しました"


#        print "====START registerInstancesWithLoadBalancer===="
#        instanceids = clientLB.registerInstancesWithLoadBalancer(instances, "TESTBALANCER")
#        print instanceids[0] + u"を割り当てましたしました"

#        print "====START createLoadBalancerListeners===="
#        instances = clientLB.createLoadBalancerListeners(listeners, "TESTBALANCER")
#        print u"リスナーを開始しました"

#        print "====START configureHealthCheck===="
#        lbs = clientLB.describeLoadBalancers()
#        health = lbs[0].healthCheck
#
#        healthCheck = clientLB.configureHealthCheck(health, "TESTBALANCER")
#        print healthCheck.healthyThreshold
#        print healthCheck.interval
#        print healthCheck.target
#        print healthCheck.timeout
#        print healthCheck.unhealthyThreshold

#        print "====START deleteLoadBalancerListeners===="
#        instances = clientLB.deleteLoadBalancerListeners(["65535",], "TESTBALANCER")
#        print u"リスナーを終了しました"

#        print "====START deregisterInstancesFromLoadBalancer===="
#        instanceids = clientLB.deregisterInstancesFromLoadBalancer(instances, "TESTBALANCER")
#        print instanceids
#        print u"↑が空であれば割り当てを解除しました"

        print "====START deleteLoadBalancer===="
        clientLB.deleteLoadBalancer("TESTBALANCER")
        print u'ロードバランサを削除しました'


    else:
        sys.exit()

