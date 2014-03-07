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
from iaasgw.common.pccConnections import PCCEC2APNEConnection, PCCEC2Connection, \
    PCCEC2EUConnection, PCCEC2USWestConnection, PCCEC2USWestOregonConnection, \
    PCCEC2APSEConnection
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.module.ec2.ec2module import AttachmentSet, Volume, Address, KeyPair, \
    SecurityGroup, IpPermissions, SnapshotSet, RegionInfo, VpcInfo, SubnetInfo, \
    TagSet
from iaasgw.utils.propertyUtil import getAwsInfo
from iaasgw.utils.stringUtils import isEmpty
from libcloud.compute.base import NodeImage
from libcloud.compute.drivers.ec2 import EC2NodeDriver
from libcloud.compute.providers import Provider
#環境によって位置が違います
#from libcloud.utils import findtext, findattr, findall, fixxpath
from libcloud.utils.xml import findtext, findattr, findall, fixxpath
import base64
import libcloud
import traceback


REGION_PARAM ={"NORTHEAST":
                   {"connectionCls": PCCEC2APNEConnection,
                    "api_name": 'ec2_ap_northeast',
                    "name":'Amazon EC2 (ap-northeast-1)',
                    "friendly_name" : 'Amazon Asia-Pacific Tokyo',
                    "country" : 'JP',
                    "region_name" : 'ap-northeast-1'},
               "US_EAST":
                   {"connectionCls": PCCEC2Connection,
                    "api_name": 'ec2_us_east',
                    "name":'Amazon EC2 (us-east-1)',
                    "friendly_name" : 'Amazon US N. Virginia',
                    "country" : 'US',
                    "region_name" : 'us-east-1'},
               "EU_WEST":
                   {"connectionCls": PCCEC2EUConnection,
                    "api_name": 'ec2_eu_west',
                    "name":'Amazon EC2 (eu-west-1)',
                    "friendly_name" : 'Amazon Europe Ireland',
                    "country" : 'IE',
                    "region_name" : 'eu-west-1'},
               "US_WEST":
                   {"connectionCls": PCCEC2USWestConnection,
                    "api_name": 'ec2_us_west',
                    "name":'Amazon EC2 (us-west-1)',
                    "friendly_name" : 'Amazon US N. California',
                    "country" : 'US',
                    "region_name" : 'us-west-1'},
               "US_WEST_OREGON":
                   {"connectionCls": PCCEC2USWestOregonConnection,
                    "api_name": 'ec2_us_west_oregon',
                    "name":'Amazon EC2 (us-west-2)',
                    "friendly_name" : 'Amazon US West - Oregon',
                    "country" : 'US',
                    "region_name" : 'us-west-2'},
               "SOUTHEAST":
                   {"connectionCls": PCCEC2APSEConnection,
                    "api_name": 'ec2_ap_southeast',
                    "name":'Amazon EC2 (ap-southeast-1)',
                    "friendly_name" : 'Amazon Asia-Pacific Singapore',
                    "country" : 'SG',
                    "region_name" : 'ap-southeast-1'},
               }


#APIバージョン変更
API_VERSION = '2012-03-01'
NAMESPACE = "http://ec2.amazonaws.com/doc/%s/" % (API_VERSION)

libcloud.compute.drivers.ec2.API_VERSION = API_VERSION
libcloud.compute.drivers.ec2.NAMESPACE = NAMESPACE


class EC2IaasNodeDriver(EC2NodeDriver):

    type = Provider.EC2
    path = '/'

    def __init__(self, region, key, secret=None, secure=True, useProxy=False):
        param = REGION_PARAM[region]

        self.connectionCls = param["connectionCls"]
        self.api_name = param["api_name"]
        self.name = param["name"]
        self.friendly_name = param["friendly_name"]
        self.country = param["country"]
        self.region_name = param["region_name"]

        #プロキシ利用
        self.connectionCls.useProxy = useProxy

        super(EC2IaasNodeDriver, self).__init__(key=key, secret=secret, secure=secure)


class EC2IaasClient(EC2IaasNodeDriver):

    logger = IaasLogger()
    platformNo = None
    debugout = True
    username = None

    def __init__(self, platforminfo, username, key, secret=None):

        self.platformNo = platforminfo["platformNo"]
        self.username = username
        useProxy  =  platforminfo["proxy"]
        #プロキシ利用
        if useProxy == 1:
            useProxy = True
        else:
            useProxy = False

        #リュージョン取得
        awsInfo = getAwsInfo(self.platformNo)
        region =  awsInfo["region"]

        super(EC2IaasClient, self).__init__(region=region, key=key, secret=secret, useProxy=useProxy)

    ############################################################
    #
    #    基礎データ
    #
    ############################################################
    def getPlatformNo(self):
        return self.platformNo

    def setPlatformNo(self, platformNo):
        self.platformNo = platformNo

    def getUsername(self):
        return self.username

    ############################################################
    #
    #    参照系  describe
    #
    ############################################################

    def describeImages(self, location=None):
        return super(EC2IaasClient, self).list_images(location)

    def describeImage(self, imageId):
        params = {'Action': 'DescribeImages', 'ImageId.0':imageId}

        #ここで利用する_to_imagesは拡張版です
        images = self._to_images(
            self.connection.request(self.path, params=params).object
        )

        if not images or len(images) == 0:
            #インスタンスが存在しない場合
            return None

        return images[0]


    #list_nodesとして提供されているが、機能としてはインスタンス指定を利用がいいか？
    def describeInstances(self, instanceid=None):
        params = {'Action':'DescribeInstances'}

        #任意パラメータ
        if instanceid != None:
            params.update({'InstanceId.0':instanceid})

        elem = self.connection.request(self.path, params=params).object

        nodes = []
        for rs in findall(element=elem, xpath='reservationSet/item',
                          namespace=NAMESPACE):
            groups = [g.findtext('')
                      for g in findall(element=rs,
                                       xpath='groupSet/item/groupId',
                                       namespace=NAMESPACE)]

            nodes += self._to_nodes(rs, 'instancesSet/item', groups)
        nodes_elastic_ips_mappings = self.ex_describe_addresses(nodes)
        for node in nodes:
            node.public_ip.extend(nodes_elastic_ips_mappings[node.id])
        return nodes


    #list_nodesとして提供されているものをそのまま使って必要な物だけ
    def describeInstance(self, instanceid):
        nodes = self.describeInstances(instanceid)

        if not nodes or len(nodes) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000101", [instanceid,])

        if len(nodes) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000102", [instanceid,])

        return nodes[0]


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
    def describeVolumes(self, volumeId=None):
        params = {'Action': 'DescribeVolumes'}
        #任意パラメータ
        if volumeId != None:
            params.update({'VolumeId.0': volumeId})


        elem = self.connection.request(self.path, params=params).object


        volumes = []
        for rs in findall(element=elem, xpath='volumeSet/item', namespace=NAMESPACE):
            volumes.append(self._to_volumes(rs))

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
        params = {'Action': 'DescribeAddresses'}
        #任意パラメータ
        if publicIp != None:
            params.update({'PublicIp.0': publicIp})

        elem = self.connection.request(self.path, params=params).object

        address = []
        for rs in findall(element=elem, xpath='addressesSet/item', namespace=NAMESPACE):
            address.append(self._to_address(rs))

        return address


    #LibCloudで提供されているdescribeKeyPairsラップ関数はNameの入力が必須である為用途が異なる為、独自実装
    def describeKeyPairs(self):
        params = {'Action': 'DescribeKeyPairs'}

        elem = self.connection.request(self.path, params=params).object

        keypairs = []
        for rs in findall(element=elem, xpath='keySet/item', namespace=NAMESPACE):
            keypairs.append(self._to_keypair(rs))

        return keypairs


    #LibCloudで提供されていない為独自実装
    def describeSecurityGroups(self):
        #GroupName, GroupId, Filter(name/Value)で絞る事ができるがPCCでは利用しないデフォルト検索を提供する
        params = {'Action': 'DescribeSecurityGroups'}

        elem = self.connection.request(self.path, params=params).object

        securityGroups = []
        for rs in findall(element=elem, xpath='securityGroupInfo/item', namespace=NAMESPACE):
            securityGroups.append(self._to_securityGroup(rs))

        return securityGroups


    #ex_list_availability_zonesとして提供されているものをラップ
    def describeAvailabilityZones(self, only_available=True):
        return super(EC2IaasClient, self).ex_list_availability_zones(only_available)

    #LibCloudで提供されていない為独自実装
    def describeSnapshot(self, snapshotId):
        snapshots = self.describeSnapshots(snapshotId)

        if not snapshots or len(snapshots) == 0:
            #アドレスが存在しない場合
            raise IaasException("EPROCESS-000122", [snapshotId,])

        if len(snapshots) > 1:
            #アドレスを複数参照できた場合
            raise IaasException("EPROCESS-000123", [snapshotId,])

        return snapshots[0]


    #LibCloudで提供されていない為独自実装
    def describeSnapshots(self, snapshotId=None):
        #Owner, RestorableBy, Filter(name/Value)で絞る事ができるが制限する
        params = {'Action': 'DescribeSnapshots'}
        #任意パラメータ
        if snapshotId != None:
            params.update({'SnapshotId': snapshotId})

        elem = self.connection.request(self.path, params=params).object

        snapshots = []
        for rs in findall(element=elem, xpath='snapshotSet/item', namespace=NAMESPACE):
            snapshots.append(self._to_snapshot(rs))

        return snapshots


    def describeRegions(self, regionName=None):
        params = {'Action': 'DescribeRegions'}
        #任意パラメータ
        if regionName != None:
            params.update({'RegionName': regionName})

        elem = self.connection.request(self.path, params=params).object

        regionInfos = []
        for rs in findall(element=elem, xpath='regionInfo/item', namespace=NAMESPACE):
            regionInfos.append(self._to_regionInfo(rs))

        return regionInfos


    def describeVpcs(self, vpcId=None):
        params = {'Action': 'DescribeVpcs'}
        #任意パラメータ
        if vpcId != None:
            params.update({'VpcId': vpcId})

        elem = self.connection.request(self.path, params=params).object

        vpcInfos = []
        for rs in findall(element=elem, xpath='vpcSet/item', namespace=NAMESPACE):
            vpcInfos.append(self._to_vpcInfo(rs))

        return vpcInfos


    def describeSubnets(self, subnetId=None):
        params = {'Action': 'DescribeSubnets'}
        #任意パラメータ
        if subnetId != None:
            params.update({'SubnetId': subnetId})

        elem = self.connection.request(self.path, params=params).object

        subnetInfos = []
        for rs in findall(element=elem, xpath='subnetSet/item', namespace=NAMESPACE):
            subnetInfos.append(self._to_subnetInfo(rs))

        return subnetInfos


    def describeTags(self, instanceId=None):
        params = {'Action': 'DescribeTags'}
        #任意パラメータ
        if instanceId != None:
            params.update({'Filter.0.Name': 'resource-id'})
            params.update({'Filter.0.Value.0': instanceId})


        elem = self.connection.request(self.path, params=params).object

        tagSets = []
        for rs in findall(element=elem, xpath='tagSet/item', namespace=NAMESPACE):
            tagSets.append(self._to_tagSet(rs))

        return tagSets




    ############################################################
    #
    #    Instance 系
    #
    ############################################################
    def runInstances (self, groupmap, **kwargs):
        params = {
            'Action': 'RunInstances',
            'ImageId': kwargs.get('imageId'),
            'InstanceType': kwargs.get('instanceType'),
            'MinCount': kwargs.get('ex_mincount', '1'),
            'MaxCount': kwargs.get('ex_maxcount', '1'),
        }

        isGroupID = False
        isPrivateIp = False
        if 'subnetId' in kwargs and kwargs['subnetId'] is not None:
            params['SubnetId'] = kwargs['subnetId']
            #サブネットありの場合はセキュリティグループはIDを使う
            isGroupID = True
            isPrivateIp = True


        if 'securityGroup' in kwargs:
            if kwargs['securityGroup'] is not None:
                securityGroups = kwargs['securityGroup']
                for sig in range(len(securityGroups)):
                    if isGroupID:
                        params['SecurityGroupId.%d' % (sig + 1,)] = groupmap[securityGroups[sig]]
                    else:
                        params['SecurityGroup.%d' % (sig + 1,)] = securityGroups[sig]

        if 'blockDeviceMapping' in kwargs:
            if kwargs['blockDeviceMapping'] is not None:
                if not isinstance(kwargs['blockDeviceMapping'], list):
                    kwargs['blockDeviceMapping'] = [kwargs['blockDeviceMapping']]
                for sig, blockDevice in enumerate(kwargs['blockDeviceMapping']):
                    params['BlockDeviceMapping.%d.DeviceName'  % (sig + 1,)] = blockDevice["DeviceName"]
                    params['BlockDeviceMapping.%d.VirtualName'  % (sig + 1,)] = blockDevice["VirtualName"]

        if 'location' in kwargs:
            availability_zone = kwargs['location']
            if availability_zone:
                #if availability_zone != self.region_name:
                #    raise IaasException('Invalid availability zone: %s' % availability_zone)
                params['Placement.AvailabilityZone'] = availability_zone

        if 'kernelId' in kwargs and kwargs['kernelId'] is not None and '' != kwargs['kernelId']:
            params['KernelId'] = kwargs['kernelId']

        if 'ramdiskId' in kwargs and kwargs['ramdiskId'] is not None and '' != kwargs['ramdiskId']:
            params['RamdiskId'] = kwargs['ramdiskId']

        if 'keyName' in kwargs and kwargs['keyName'] is not None and '' != kwargs['keyName']:
            params['KeyName'] = kwargs['keyName']

        if isPrivateIp and 'privateIpAddress' in kwargs and kwargs['privateIpAddress'] is not None and '' != kwargs['privateIpAddress']:
            params['PrivateIpAddress'] = kwargs['privateIpAddress']

        if 'userData' in kwargs and kwargs['userData'] is not None:
            params['UserData'] = base64.b64encode(kwargs['userData'])

        if 'ex_clienttoken' in kwargs and kwargs['ex_clienttoken'] is not None:
            params['ClientToken'] = kwargs['ex_clienttoken']

        instance = None
        try:
            self.logger.info(params)
            object = self.connection.request(self.path, params=params).object
            nodes = self._to_nodes(object, 'instancesSet/item')
            instance = nodes[0]

        except Exception:
            self.logger.error(traceback.format_exc())
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000105", [])

        #実行ログ
        self.logger.info(None, "IPROCESS-100115", [instance.id])

        return instance

    #LibCloudで提供されていない為独自実装
    def startInstance(self, instanceid):
        params = {'Action': 'StartInstances', 'InstanceId.0': instanceid}
        try:
            elem = self.connection.request(self.path, params=params).object
            previousStates = []
            for rs in findall(element=elem, xpath='instancesSet/item', namespace=NAMESPACE):
                code = findtext(element=rs, xpath='previousState/code', namespace=NAMESPACE)
                name = findtext(element=rs, xpath='previousState/name', namespace=NAMESPACE)
                previousStates.append({"code":code, "name":name})

            if len(previousStates) > 1:
                raise IaasException("EPROCESS-000127", [instanceid,])

            #実行ログ
            self.logger.info(None, "IPROCESS-100111", [instanceid,])

            return previousStates[0]

        except Exception:
            # インスタンス起動失敗時
            raise IaasException("EPROCESS-000125", [instanceid,])
        return None

    #LibCloudで提供されていない為独自実装
    def stopInstance(self, instanceid):
        params = {'Action': 'StopInstances', 'InstanceId.0': instanceid}
        try:
            elem = self.connection.request(self.path, params=params).object
            previousStates = []
            for rs in findall(element=elem, xpath='instancesSet/item', namespace=NAMESPACE):
                code = findtext(element=rs, xpath='previousState/code', namespace=NAMESPACE)
                name = findtext(element=rs, xpath='previousState/name', namespace=NAMESPACE)
                previousStates.append({"code":code, "name":name})

            if len(previousStates) > 1:
                raise IaasException("EPROCESS-000130", [instanceid,])

            #実行ログ
            self.logger.info(None, "IPROCESS-100113", [instanceid,])

            return previousStates[0]
        except Exception:
            # インスタンス停止失敗時
            raise IaasException("EPROCESS-000128", [instanceid,])
        return None

    #本来destroy_nodeはnode自体を受け取るがこの関数はIDを受ける形とした
    def terminateInstance(self, instanceid):
        params = {'Action': 'TerminateInstances', 'InstanceId': instanceid}
        try:
            elem = self.connection.request(self.path, params=params).object
            previousStates = []
            for rs in findall(element=elem, xpath='instancesSet/item', namespace=NAMESPACE):
                code = findtext(element=rs, xpath='previousState/code', namespace=NAMESPACE)
                name = findtext(element=rs, xpath='previousState/name', namespace=NAMESPACE)
                previousStates.append({"code":code, "name":name})

            if len(previousStates) > 1:
                #複数のインスタンスが削除された場合
                raise IaasException("EPROCESS-000108", [instanceid,])

            #実行ログ
            self.logger.info(None, "IPROCESS-100117", [instanceid,])

            return previousStates[0]
        except Exception:
            # インスタンス削除失敗時
            raise IaasException("EPROCESS-000107", [instanceid,])
        return None

    def modifyInstanceAttribute(self, instanceid, **kwargs):
        params = {'Action': 'ModifyInstanceAttribute', 'InstanceId': instanceid}

        #現状はインスタンスタイプのみ変更に対応する
        if 'InstanceType' in kwargs and kwargs['InstanceType'] is not None and '' != kwargs['InstanceType']:
            params['InstanceType.Value'] = kwargs['InstanceType']

        try:
            self.connection.request(self.path, params=params).object
        except Exception:
            # インスタンス変更失敗時
            raise IaasException("EPROCESS-000107", [instanceid,])
        return None

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
            params.update({'Size':str(size)})
        if snapshotId != None:
            params.update({'SnapshotId':snapshotId})

        elem = self.connection.request(self.path, params=params).object
        voluem = self._to_volume(elem)

        #実行ログ
        self.logger.info(None, "IPROCESS-100121", [voluem.volumeId,])

        return voluem

    #LibCloudで提供されていない為独自実装
    def attachVolume(self, volumeId, instanceId, device):
        params = {'Action': 'AttachVolume', 'VolumeId':volumeId, 'InstanceId':instanceId, 'Device':device }
        elem = self.connection.request(self.path, params=params).object

        #実行ログ
        self.logger.info(None, "IPROCESS-100123", [volumeId,instanceId])

        return self._to_attachmentSet(elem)


    #LibCloudで提供されていない為独自実装
    def detachVolume(self,volumeId, instanceId=None, device=None):
        params = {'Action': 'DetachVolume', 'VolumeId':volumeId}

        #任意パラメータ
        if instanceId != None:
            params.update({'InstanceId':instanceId})
        if device != None:
            params.update({'Device':device})

        elem = self.connection.request(self.path, params=params).object
        #実行ログ
        self.logger.info(None, "IPROCESS-100125", [volumeId,instanceId])

        #deleteOnTerminationが戻りのエレメントに無いのでそのまま動くか不明
        return self._to_attachmentSet(elem)


    #LibCloudで提供されていない為独自実装
    def deleteVolume(self, volumeId):
        params = {'Action': 'DeleteVolume', 'VolumeId':volumeId}

        self.connection.request(self.path, params=params).object

        #実行ログ
        self.logger.info(None, "IPROCESS-100127", [volumeId,])


    ############################################################
    #
    #    Address 系
    #
    ############################################################

    #LibCloudで提供されていない為独自実装
    #Domainを指定することも可能だが無視する
    def allocateAddress(self):
        params = {'Action': 'AllocateAddress'}

        elem = self.connection.request(self.path, params=params).object

        publicIp = findattr(element=elem, xpath="publicIp", namespace=NAMESPACE)

        # ログ出力
        self.logger.info(None, "IPROCESS-100133", [publicIp,])

        return publicIp


    #LibCloudで提供されていない為独自実装
    def associateAddress(self,  publicIp, instanceId):
        params = {'Action': 'AssociateAddress', 'PublicIp':publicIp, 'InstanceId':instanceId}

        self.connection.request(self.path, params=params).object
        # ログ出力
        self.logger.info(None, "IPROCESS-100131", [publicIp, instanceId,])


    #LibCloudで提供されていない為独自実装
    def disassociateAddress(self, publicIp, instanceId):
        params = {'Action': 'DisassociateAddress', 'PublicIp':publicIp}

        self.connection.request(self.path, params=params).object

        # ログ出力
        self.logger.info(None, "IPROCESS-100132", [publicIp, instanceId,])


    #LibCloudで提供されていない為独自実装
    def releaseAddress(self, publicIp):
        params = {'Action': 'ReleaseAddress', 'PublicIp':publicIp}

        self.connection.request(self.path, params=params).object
        # ログ出力
        self.logger.info(None, "IPROCESS-100134", [publicIp,])


    ############################################################
    #
    #    KeyPair 系
    #
    ############################################################

    #LibCloudで提供されてるex_create_keypairはKeyNameを戻り値に含まない為、独自実装
    def createKeyPair(self, keyName):
        params = {'Action': 'CreateKeyPair', 'KeyName':keyName}

        elem = self.connection.request(self.path, params=params).object

        keypair = self._to_keypair(elem)
        keypair.setKeyMaterial(findattr(element=elem, xpath="keyMaterial", namespace=NAMESPACE))


    #LibCloudで提供されていない為独自実装
    def deleteKeyPair(self, keyName):
        params = {'Action': 'DeleteKeyPair', 'KeyName':keyName}

        self.connection.request(self.path, params=params).object

        #戻り値なし


    #LibCloudで提供されてるex_import_keypairはファイルベースの為、独自実装
    def importKeyPair(self, keyName, publicKeyMaterial):
        publicKeyMaterial = base64.b64encode(publicKeyMaterial)
        params = {'Action': 'ImportKeyPair', 'KeyName':keyName, 'PublicKeyMaterial':publicKeyMaterial}

        elem = self.connection.request(self.path, params=params).object

        return self._to_keypair(elem)


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

        elem = self.connection.request(self.path, params=params).object
        snapshot = self._to_snapshot(elem)

        # ログ出力
        self.logger.info(None, "IPROCESS-100151", [snapshot.snapshotId, volumeId])

        return snapshot

    def deleteSnapshot(self, snapshotId):
        params = {'Action': 'DeleteSnapshot', 'SnapshotId':snapshotId}

        self.connection.request(self.path, params=params).object
        # ログ出力
        self.logger.info(None, "IPROCESS-100153", [snapshotId])



    ############################################################
    #
    #    Tags 系
    #
    ############################################################

    def createTags(self, resourceId, tagSets):
        if not tagSets:
            return

        params = {'Action': 'CreateTags', 'ResourceId.0': resourceId}
        i = 0
        for tag in tagSets:
            params['Tag.%d.Key' % i] = tag.key
            params['Tag.%d.Value' % i] = tag.value
            i = i +1
            # ログ出力
            self.logger.info(None, "IPROCESS-100154", [resourceId, tag.key, tag.value])

        self.connection.request(self.path, params=params).object

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

        self.connection.request(self.path, params=params).object



    ############################################################
    #
    #    その他
    #
    ############################################################
    def getPasswordData(self, InstanceId):
        params = {'Action': 'GetPasswordData', 'InstanceId': InstanceId}

        elem = self.connection.request(self.path, params=params).object

        #True Falseを受ける
        passwdData = findattr(element=elem, xpath="passwordData", namespace=NAMESPACE)

        if isEmpty(passwdData):
            # パスワードデータを取得できない場合
            raise IaasException("EPROCESS-000133", [InstanceId,])
        return passwdData



    '''********************************以下ローカルメソッド******************************************
    '''
    def build_list_params(self, params, items, label):
        if isinstance(items, str):
            items = [items]
        for i, item in enumerate(items, 1):
            params[label % i] = item

    def _to_image(self, element):
        n = NodeImage(id=findtext(element=element, xpath='imageId', namespace=NAMESPACE),
                      name=findtext(element=element, xpath='imageLocation', namespace=NAMESPACE),
                      driver=self.connection.driver,
                      extra={'rootDeviceType': findattr(element=element, xpath="rootDeviceType", namespace=NAMESPACE),
                             'platform': findattr(element=element, xpath="platform", namespace=NAMESPACE),}
        )
        return n

    def _to_tagSet(self,  element):
        n = TagSet(
            resourceId = findattr(element=element, xpath="resourceId", namespace=NAMESPACE),
            resourceType = findattr(element=element, xpath="resourceType", namespace=NAMESPACE),
            key = findattr(element=element, xpath="key", namespace=NAMESPACE),
            value = findattr(element=element, xpath="value", namespace=NAMESPACE),
        )
        return n

    def _to_subnetInfo(self,  element):
        n = SubnetInfo(
            subnetId = findattr(element=element, xpath="subnetId", namespace=NAMESPACE),
            state = findattr(element=element, xpath="state", namespace=NAMESPACE),
            vpcId = findattr(element=element, xpath="vpcId", namespace=NAMESPACE),
            cidrBlock = findattr(element=element, xpath="cidrBlock", namespace=NAMESPACE),
            availableIpAddressCount = findattr(element=element, xpath="availableIpAddressCount", namespace=NAMESPACE),
            availabilityZone = findattr(element=element, xpath="availabilityZone", namespace=NAMESPACE),
            tagSet = findattr(element=element, xpath="tagSet", namespace=NAMESPACE),
        )
        return n




    def _to_vpcInfo(self,  element):
        n = VpcInfo(
            vpcId = findattr(element=element, xpath="vpcId", namespace=NAMESPACE),
            state = findattr(element=element, xpath="state", namespace=NAMESPACE),
            cidrBlock = findattr(element=element, xpath="cidrBlock", namespace=NAMESPACE),
            dhcpOptionsId = findattr(element=element, xpath="dhcpOptionsId", namespace=NAMESPACE),
            tagSet = findattr(element=element, xpath="tagSet", namespace=NAMESPACE),
        )
        return n


    def _to_regionInfo(self,  element):
        n = RegionInfo(
            regionName = findattr(element=element, xpath="regionName", namespace=NAMESPACE),
            regionEndpoint = findattr(element=element, xpath="regionEndpoint", namespace=NAMESPACE),
        )
        return n


    def _to_snapshot(self,  element):
        n = SnapshotSet(
            snapshotId = findattr(element=element, xpath="snapshotId", namespace=NAMESPACE),
            volumeId = findattr(element=element, xpath="volumeId", namespace=NAMESPACE),
            status = findattr(element=element, xpath="status", namespace=NAMESPACE),
            startTime = findattr(element=element, xpath="startTime", namespace=NAMESPACE),
            progress = findattr(element=element, xpath="progress", namespace=NAMESPACE),
            ownerId = findattr(element=element, xpath="ownerId", namespace=NAMESPACE),
            volumeSize = findattr(element=element, xpath="volumeSize", namespace=NAMESPACE),
            description = findattr(element=element, xpath="description", namespace=NAMESPACE),
            tagSet = findattr(element=element, xpath="tagSet", namespace=NAMESPACE),
        )
        return n

    def _to_securityGroup(self,  element):

        ipPermissions = []
        for rs in findall(element=element, xpath='ipPermissions/item', namespace=NAMESPACE):
            ipPermissions.append(self._to_ipPermissions(rs))


        n = SecurityGroup(
            ownerId = findattr(element=element, xpath="ownerId", namespace=NAMESPACE),
            groupId = findattr(element=element, xpath="groupId", namespace=NAMESPACE),
            groupName = findattr(element=element, xpath="groupName", namespace=NAMESPACE),
            groupDescription = findattr(element=element, xpath="groupDescription", namespace=NAMESPACE),
            vpcId = findattr(element=element, xpath="vpcId", namespace=NAMESPACE),
            ipPermissions = ipPermissions,
            ipPermissionsEgress = findattr(element=element, xpath="ipPermissionsEgress", namespace=NAMESPACE),
            tagSet = findattr(element=element, xpath="tagSet", namespace=NAMESPACE),
        )
        return n


    def _to_ipPermissions(self,  element):
        ipRanges = []
        for rs in findall(element=element, xpath='ipRanges/item', namespace=NAMESPACE):
            ipRanges.append(findattr(element=rs, xpath="cidrIp", namespace=NAMESPACE))

        n = IpPermissions(
            ipProtocol = findattr(element=element, xpath="ipProtocol", namespace=NAMESPACE),
            fromPort = findattr(element=element, xpath="fromPort", namespace=NAMESPACE),
            toPort = findattr(element=element, xpath="toPort", namespace=NAMESPACE),
            groups = findattr(element=element, xpath="groups", namespace=NAMESPACE),
            ipRanges = ipRanges,
        )
        return n




    def _to_volumes(self,  element):
        #ステータスチェック（保留）
        #try:
        #    state = self.NODE_STATE_MAP[
        #            findattr(element=element, xpath="instanceState/name",
        #                     namespace=NAMESPACE)
        #    ]
        #except KeyError:
        #    state = NodeState.UNKNOWN

        tags = dict((findtext(element=item, xpath='key', namespace=NAMESPACE),
                     findtext(element=item, xpath='value', namespace=NAMESPACE))

        for item in findall(element=element, xpath='tagSet/item', namespace=NAMESPACE))

        attachment = []
        for rs in findall(element=element, xpath='attachmentSet/item', namespace=NAMESPACE):
            attachmentset = self._to_attachmentSet(rs)
            attachmentset.setDeleteOnTermination(findattr(element=element, xpath="deleteOnTermination", namespace=NAMESPACE))
            attachment.append(attachmentset)

        v = self._to_volume(element, attachment, tags)
        return v


    def _to_volume(self, element, attachmentSet=None, tags=None):
        v = Volume(
               volumeId = findattr(element=element, xpath="volumeId", namespace=NAMESPACE),
               size = findattr(element=element, xpath="size", namespace=NAMESPACE),
               snapshotId = findattr(element=element, xpath="snapshotId", namespace=NAMESPACE),
               availabilityZone = findattr(element=element, xpath="availabilityZone", namespace=NAMESPACE),
               status = findattr(element=element, xpath="status", namespace=NAMESPACE),
               createTime = findattr(element=element, xpath="createTime", namespace=NAMESPACE),
               attachmentSet = attachmentSet,
               tagSet = tags
        )
        return v


    def _to_attachmentSet(self, element):
        n = AttachmentSet(
            volumeId = findattr(element=element, xpath="volumeId", namespace=NAMESPACE),
            instanceId = findattr(element=element, xpath="instanceId", namespace=NAMESPACE),
            device = findattr(element=element, xpath="device", namespace=NAMESPACE),
            status = findattr(element=element, xpath="status", namespace=NAMESPACE),
            attachTime = findattr(element=element, xpath="attachTime", namespace=NAMESPACE),
        )
        return n

    def _to_address(self, element):
        n = Address(
            publicIp = findattr(element=element, xpath="publicIp", namespace=NAMESPACE),
            domain = findattr(element=element, xpath="domain", namespace=NAMESPACE),
            instanceId = findattr(element=element, xpath="instanceId", namespace=NAMESPACE),
        )
        return n


    def _to_keypair(self, element):
        n = KeyPair(
            keyName = findattr(element=element, xpath="keyName", namespace=NAMESPACE),
            keyFingerprint = findattr(element=element, xpath="keyFingerprint", namespace=NAMESPACE),
            #keyMaterial = findattr(element=element, xpath="keyMaterial", namespace=NAMESPACE),
        )
        return n




