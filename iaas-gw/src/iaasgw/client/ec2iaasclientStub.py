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
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.module.ec2.ec2module import AttachmentSet, Volume, Address, KeyPair, \
    SecurityGroup, IpPermissions, SnapshotSet, RegionInfo, VpcInfo, SubnetInfo, \
    TagSet, TestModule
from iaasgw.utils.stringUtils import isNotEmpty, isEmpty
from libcloud.compute.base import NodeImage
from libcloud.compute.drivers.ec2 import EC2APNENodeDriver, EC2APNEConnection, \
    NAMESPACE, ExEC2AvailabilityZone
from libcloud.compute.providers import Provider
from libcloud.utils import findtext, findattr, findall, fixxpath
import base64


class EC2IaasClient(EC2APNENodeDriver):

    logger = IaasLogger()

    connectionCls = EC2APNEConnection
    type = Provider.EC2
    api_name = 'ec2_ap_northeast'
    name = 'Amazon EC2 (ap-northeast-1)'
    friendly_name = 'Amazon Asia-Pacific Tokyo'
    country = 'JP'
    region_name = 'ap-northeast-1'
    path = '/'

    platformNo = None

    debugout = True


    ############################################################
    #
    #    基礎データ
    #
    ############################################################
    def getPlatformNo(self):
        return self.platformNo

    def setPlatformNo(self, platformNo):
        self.platformNo = platformNo


    ############################################################
    #
    #    参照系  describe
    #
    ############################################################

    def describeImages(self, location=None):
        return None

    def describeImage(self, imageId):
        return  NodeImage("IMAGEID","IMAGELOCATION","DRIVER",
            extra={'ROOTDEVICETYPE': "ROOTDEVICETYPE", 'PLATFORM': "PLATFORM"})



    #list_nodesとして提供されているが、機能としてはインスタンス指定を利用がいいか？
    def describeInstances(self, instanceid):
        return None


    #list_nodesとして提供されているものをそのまま使って必要な物だけ
    def describeInstance(self, instanceid):
        extra = {}
        extra.update({"availability":"ap-northeast-1"})
        extra.update({"dns_name":"TESTDNS"})
        extra.update({"private_dns":"TEST_PDNS"})
        extra.update({"private_dns":"TEST_PDNS"})
        return TestModule("TEST2", "running", '0.0.0.1','0.0.0.1', extra)
        #return TestModule("TEST2", "stopped", '0.0.0.1','0.0.0.1', extra)

    #LibCloudで提供されていない為独自実装
    def describeVolume(self, volumeId):
        volumes = self.describeVolumes(volumeId)

        if not volumes or len(volumes) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000110", [volumeId,])

        if len(volumes) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000111", [volumeId,])

        return volumes[0]

    #LibCloudで提供されていない為独自実装
    def describeVolumes(self, volumeId):
        #volume = Volume("TESTVOLID", None, None, None, 'in-use', None, None, None)
        volume = Volume("TESTVOLID", None, None, None, 'available', None, None, None)
        volumes = [volume,]
        return volumes

    def describeAddress(self, publicIp):
        address = self.describeAddresses(publicIp)

        if not address or len(address) == 0:
            #アドレスが存在しない場合
            raise IaasException("EPROCESS-000117", [publicIp,])

        if len(address) > 1:
            #アドレスを複数参照できた場合
            raise IaasException("EPROCESS-000118", [publicIp,])


        return address[0]
    #LibCloudで提供されているDescribeAddressesラップ関数は利用形態がマッチしないので独自実装とする
    #本来はpublicIpを省略する事も可能だが用途的に固定する
    def describeAddresses(self, publicIp=None):
        volume = Address(None, None, None)
        volumes = [volume,]
        return volumes



    #LibCloudで提供されているdescribeKeyPairsラップ関数はNameの入力が必須である為用途が異なる為、独自実装
    def describeKeyPairs(self):
        return None


    #LibCloudで提供されていない為独自実装
    def describeSecurityGroups(self):
        return None


    #ex_list_availability_zonesとして提供されているものをラップ
    def describeAvailabilityZones(self, only_available=True):
        zone = ExEC2AvailabilityZone("TESTTESTLB", None, None)
        zones=[zone,]
        return zones

    #LibCloudで提供されていない為独自実装
    def describeSnapshot(self, snapshotId):
        return None


    #LibCloudで提供されていない為独自実装
    def describeSnapshots(self, snapshotId=None):
        return None


    def describeRegions(self, regionName=None):
        return None


    def describeVpcs(self, vpcId=None):
        return None


    def describeSubnets(self, subnetId=None):
        return None


    def describeTags(self, instanceId=None):
        return None




    ############################################################
    #
    #    Instance 系
    #
    ############################################################

    #*********************そのまま*****************
    def runInstances (self, **kwargs):

        print ">>>>>>>",kwargs

        params = {
            'Action': 'RunInstances',
            'ImageId': kwargs.get('imageId'),
            'InstanceType': kwargs.get('instanceType'),
            'MinCount': kwargs.get('ex_mincount', '1'),
            'MaxCount': kwargs.get('ex_maxcount', '1'),
        }

        if 'securityGroup' in kwargs:
            if not isinstance(kwargs['securityGroup'], list):
                kwargs['securityGroup'] = [kwargs['securityGroup']]
            for sig in range(len(kwargs['securityGroup'])):
                params['SecurityGroup.%d' % (sig + 1,)] = kwargs['securityGroup'][sig]

        if 'blockDeviceMapping' in kwargs:
            if kwargs['blockDeviceMapping']:
                if not isinstance(kwargs['blockDeviceMapping'], list):
                    kwargs['blockDeviceMapping'] = [kwargs['blockDeviceMapping']]
                for sig, blockDevice in enumerate(kwargs['blockDeviceMapping']):
                    params['BlockDeviceMapping.%d.DeviceName'  % (sig + 1,)] = blockDevice["DeviceName"]
                    params['BlockDeviceMapping.%d.VirtualName'  % (sig + 1,)] = blockDevice["VirtualName "]

        if 'location' in kwargs:
            availability_zone = kwargs['location']
            if availability_zone:
                if availability_zone != self.region_name:
                    raise IaasException('Invalid availability zone: %s' % availability_zone)
                params['Placement.AvailabilityZone'] = availability_zone

        if 'kernelId' in kwargs:
            params['KernelId'] = kwargs['kernelId']

        if 'ramdiskId' in kwargs:
            params['RamdiskId'] = kwargs['ramdiskId']

        if 'keyName' in kwargs:
            params['KeyName'] = kwargs['keyName']

        if 'subnetId' in kwargs:
            params['SubnetId'] = kwargs['subnetId']

        if 'userData' in kwargs:
            params['UserData'] = base64.b64encode(kwargs['userData'])

        if 'ex_clienttoken' in kwargs:
            params['ClientToken'] = kwargs['ex_clienttoken']


        extra = {}
        extra.update({"availability":"TESTZONE"})
        extra.update({"'dns_name'":"TESTDNS"})
        extra.update({"'private_dns'":"TEST_PDNS"})
        extra.update({"'private_dns'":"TEST_PDNS"})
        return TestModule("TEST2", "TESTING", '0.0.0.1','0.0.0.1', extra)

    #LibCloudで提供されていない為独自実装
    def startInstance(self, instanceid):
        params = {'Action': 'StartInstances', 'InstanceId.0': instanceid}
        return {"code":'code', "name":'running'}

    #LibCloudで提供されていない為独自実装
    def stopInstance(self, instanceid):
        params = {'Action': 'StopInstances', 'InstanceId.0': instanceid}
        return {"code":'code', "name":'stopped'}

    #本来destroy_nodeはnode自体を受け取るがこの関数はIDを受ける形とした
    def terminateInstance(self, instanceid):
        params = {'Action': 'TerminateInstances', 'InstanceId': instanceid}
        return {"code":'code', "name":'terminated'}


    ############################################################
    #
    #    Volume 系
    #
    ############################################################

    #LibCloudで提供されていない為独自実装
    def createVolume(self, availabilityZone, size=None, snapshotId=None):
        params = {'Action': 'CreateVolume', 'AvailabilityZone':availabilityZone}

        #任意
        if size != None:
            params.update({'Size':size})
        if snapshotId != None:
            params.update({'SnapshotId':snapshotId})


        return Volume("TESTVOLID", None, None, None, 'available', None, None, None)

    #LibCloudで提供されていない為独自実装
    def attachVolume(self, volumeId, instanceId, device):
        params = {'Action': 'AttachVolume', 'VolumeId':volumeId, 'InstanceId':instanceId, 'Device':device }

        return None


    #LibCloudで提供されていない為独自実装
    def detachVolume(self,volumeId, instanceId=None, device=None):
        params = {'Action': 'DetachVolume', 'VolumeId':volumeId}

        #任意パラメータ
        if instanceId != None:
            params.update({'InstanceId':instanceId})
        if device != None:
            params.update({'Device':device})

        return None

    #LibCloudで提供されていない為独自実装
    def deleteVolume(self, volumeId):
        params = {'Action': 'DeleteVolume', 'VolumeId':volumeId}



    ############################################################
    #
    #    Address 系
    #
    ############################################################

    #LibCloudで提供されていない為独自実装
    #Domainを指定することも可能だが無視する
    def allocateAddress(self):
        params = {'Action': 'AllocateAddress'}

        return None


    #LibCloudで提供されていない為独自実装
    def associateAddress(self,  publicIp, instanceId):
        params = {'Action': 'AssociateAddress', 'PublicIp':publicIp, 'InstanceId':instanceId}

    #LibCloudで提供されていない為独自実装
    def disassociateAddress(self, publicIp, instanceId):
        params = {'Action': 'DisassociateAddress', 'PublicIp':publicIp}

    #LibCloudで提供されていない為独自実装
    def releaseAddress(self, publicIp):
        params = {'Action': 'ReleaseAddress', 'PublicIp':publicIp}


    ############################################################
    #
    #    KeyPair 系
    #
    ############################################################

    #LibCloudで提供されてるex_create_keypairはKeyNameを戻り値に含まない為、独自実装
    def createKeyPair(self, keyName):
        params = {'Action': 'CreateKeyPair', 'KeyName':keyName}

    #LibCloudで提供されていない為独自実装
    def deleteKeyPair(self, keyName):
        params = {'Action': 'DeleteKeyPair', 'KeyName':keyName}



    #LibCloudで提供されてるex_import_keypairはファイルベースの為、独自実装
    def importKeyPair(self, keyName, publicKeyMaterial):
        ###########################################
        #
        # まだ動きません   createKeyPairから直接使うとだめ
        # PCCで利用されていない為後回しとする
        #
        ###########################################

        publicKeyMaterial = base64.b64encode(publicKeyMaterial)
        params = {'Action': 'ImportKeyPair', 'KeyName':keyName, 'PublicKeyMaterial':publicKeyMaterial}



    ############################################################
    #
    #    Snapshot 系
    #
    ############################################################

    def createSnapshot(self, volumeId, description=None):
        #Owner, RestorableBy, Filter(name/Value)で絞る事ができるが制限する
        params = {'Action': 'CreateSnapshot', 'VolumeId':volumeId}
        #任意パラメータ
        if description != None:
            params.update({'Description': description})

        return None

    def deleteSnapshot(self, snapshotId):
        params = {'Action': 'DeleteSnapshot', 'SnapshotId':snapshotId}



    ############################################################
    #
    #    Tags 系
    #
    ############################################################

    def createTags(self, instanceId, tagSets):
        if not tagSets:
            return

        params = {'Action': 'CreateTags', 'ResourceId.0': instanceId}
        i = 0
        for tag in tagSets:
            params['Tag.%d.Key' % i] = tag.key
            params['Tag.%d.Value' % i] = tag.value
            i = i +1
            # ログ出力
            self.logger.info(None, "IPROCESS-100154", [instanceId, tag.key, tag.value])

    def createTag(self, instanceId, tag):
        if not tag:
            return
        tags = [tag]
        self.createTags(instanceId, tags)

    def deleteTags(self, instanceId, tagSets):
        if not tagSets:
            return

        params = {'Action': 'DeleteTags', 'ResourceId.0': instanceId}
        i = 0
        for tag in tagSets:
            params['Tag.%d.Key' % i] = tag.key
            params['Tag.%d.Value' % i] = tag.value
            i = i +1



    ############################################################
    #
    #    その他
    #
    ############################################################
    def getPasswordData(self, InstanceId):
        params = {'Action': 'GetPasswordData', 'InstanceId': InstanceId}

        elem = self.connection.request(self.path, params=params).object





