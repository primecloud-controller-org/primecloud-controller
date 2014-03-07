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
from iaasgw.common.pccConnections import PCCCloudStackConnection
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.utils.propertyUtil import getCloudStackInfo
from libcloud.compute.drivers.cloudstack import CloudStackNodeDriver, \
    CloudStackNode, CloudStackAddress
from libcloud.loadbalancer.base import LoadBalancer, Member
import base64

class CloudStackIaasClient(CloudStackNodeDriver):


    logger = IaasLogger()
    platformNo = None
    timeout = 600
    devicetype = None
    username = None
    hostid = None

    connectionCls = PCCCloudStackConnection

    def __init__(self, platforminfo, username, key, secret=None):
        self.platformNo =  platforminfo["platformNo"]
        self.username = username
        self.logger.info(u"利用プラットフォーム" + str(self.platformNo))

        #接続情報
        cloudstackInfo = getCloudStackInfo(self.platformNo)
        host = cloudstackInfo["host"]
        path = cloudstackInfo["path"]
        port = cloudstackInfo["port"]
        secure = cloudstackInfo["secure"]
        pltfmNotimeout = cloudstackInfo["timeout"]
        self.devicetype =  cloudstackInfo["device"]
        self.hostid = cloudstackInfo["hostid"]

        #タイムアウト
        if pltfmNotimeout is not None:
            self.connectionCls.timeout = int(pltfmNotimeout)

        #プロキシ利用
        useProxy = platforminfo["proxy"]
        if useProxy == 1:
            useProxy = True
        else:
            useProxy = False
        self.connectionCls.useProxy = useProxy

        #プロトコル
        if secure == 1:
            secure = True
        else:
            secure = False

        self.logger.info(u"接続情報==> "+host+":" + port + path + " secure=" + str(secure))

        CloudStackNodeDriver.__init__(self, key=key, secret=secret, secure=secure,
                                      host= host, path=path, port=port)

    ############################################################
    #
    #    基礎データ
    #
    ############################################################
    def getPlatformNo(self):
        return self.platformNo

    def getDeviceType(self):
        return self.devicetype

    def getUsername(self):
        return self.username



    ############################################################
    #
    #    参照系  describe
    #
    ############################################################
    def getHypervisor(self, zoneid):
        args = {'zoneid': zoneid}
        result = self._sync_request('listHypervisors', **args)
        print result
        hypervisor = result.get('hypervisor', [])
        return hypervisor[0]


    def describeImages(self, location=None):
        args = {'templatefilter': 'executable' }

        if location is not None:
            args['zoneid'] = location.id

        result = self._sync_request('listTemplates', **args)
        imgs = result.get('template', [])
        return imgs


    def describeImage(self, imageId):
        images = self.describeImages()
        retImage = None
        for image in images:
            if imageId == image['id'] :
                retImage = image

        if not retImage :
            #イメージが存在しない場合
            raise IaasException("EPROCESS-000724", [imageId,])

        return retImage

    def describeOnlyInstances(self):
        vms = self._sync_request('listVirtualMachines')
        return vms

    def describeInstances(self, instanceid=None, name=None):
        args = {}
        if instanceid is not None:
            args['id'] = instanceid
        if name is not None:
            args['name'] = name

        vms = self._sync_request('listVirtualMachines', **args)
        addrs = self._sync_request('listPublicIpAddresses')

        public_ips = {}
        for addr in addrs.get('publicipaddress', []):
            if 'virtualmachineid' not in addr:
                continue
            vm_id = addr['virtualmachineid']
            if vm_id not in public_ips:
                public_ips[vm_id] = {}
            public_ips[vm_id][addr['ipaddress']] = addr['id']

        nodes = []

        for vm in vms.get('virtualmachine', []):
            private_ips = []

            for nic in vm['nic']:
                if 'ipaddress' in nic:
                    private_ips.append(nic['ipaddress'])

            node = CloudStackNode(
                id=vm['id'],
                name=vm.get('displayname', None),
                state=self.NODE_STATE_MAP[vm['state']],
                #Libcloudのバージョンによってはこちらを利用してください
                #public_ip=public_ips.get(vm['id'], {}).keys(),
                #private_ip=private_ips,
                public_ips = public_ips.get(vm['id'], {}).keys(),
                private_ips = private_ips,
                driver=self,
                extra={
                    'zoneid': vm['zoneid'],
                    'serviceofferingid': vm['serviceofferingid'],
                    'displayname': vm['displayname'],
                }
            )

            addrs = public_ips.get(vm['id'], {}).items()
            addrs = [CloudStackAddress(node, v, k) for k, v in addrs]
            node.extra['ip_addresses'] = addrs

            nodes.append(node)

        return nodes

    def describeInstance(self, instanceid):
        nodes = self.describeInstances(instanceid = instanceid)

        if not nodes or len(nodes) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000701", [instanceid,])

        if len(nodes) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000702", [instanceid,])

        return nodes[0]


    def describeVolume(self, volumeId):
        volumes = self.describeVolumes()

        if not volumes or len(volumes) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000705", [volumeId,])

        if len(volumes) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000706", [volumeId,])

        return volumes[0]

    def describeVolumes(self, volumeId = None):
        args = {}
        if volumeId is not None:
            args['id'] = volumeId

        result = self._sync_request('listVolumes', **args)
        volumes = result.get('volume', [])
        return volumes

    def describeKeyPairs(self):
        result = self._sync_request('listSSHKeyPairs')
        keypairs = result.get('sshkeypair', [])
        return keypairs

    def describeSecurityGroups(self):
        result = self._sync_request('listSecurityGroups')
        securityGroups = result.get('securitygroup', [])
        return securityGroups

    def describeAvailabilityZones(self):
        result = self._sync_request('listZones')
        zones = result['zone']
        return zones


    def describeSnapshot(self, snapshotId):
        snapshots = self.describeSnapshots(snapshotId)

        if not snapshots or len(snapshots) == 0:
            #アドレスが存在しない場合
            raise IaasException("EPROCESS-000713", [snapshotId,])

        if len(snapshots) > 1:
            #アドレスを複数参照できた場合
            raise IaasException("EPROCESS-000714", [snapshotId,])

        return snapshots[0]


    def describeSnapshots(self, snapshotId=None):
        args = {}
        if snapshotId is not None:
            args['id'] = snapshotId

        result = self._sync_request('listSnapshots', **args)

        snapshots = result.get('snapshot', [])

        return snapshots


    def describeLoadBalancer(self, loadBalancerid):
        loadBalancers = self.describeLoadBalancers(loadBalancerid)

        if len(loadBalancers) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000719", [loadBalancerid,])

        if len(loadBalancers) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000720", [loadBalancerid,])

        return loadBalancers[0]


    def describeLoadBalancers(self, loadBalancerid = None):
        args = {}
        if loadBalancerid is not None:
            args['id'] = loadBalancerid

        loadBalancers = self._sync_request('listLoadBalancerRules', **args)
        balancers = loadBalancers.get('loadbalancerrule', [])
        return [self._to_balancer(balancer) for balancer in balancers]


    def describeLoadBalancerRuleInstances(self, loadBalancerid, instances = None):
        balancer = self.describeLoadBalancer(loadBalancerid)
        members = self._sync_request('listLoadBalancerRuleInstances', id=balancer.id)
        members = members['loadbalancerruleinstance']
        return [self._to_member(m, balancer.ex_private_port) for m in members]


    def describeServiceOfferings(self, domainid = None):
        args = {}
        if domainid is not None:
            args['domainid'] = domainid

        result = self._sync_request('listServiceOfferings', **args)
        serviceOffs = result['serviceoffering']
        return serviceOffs

    def describeDiskOfferings(self, domainid = None):
        args = {}
        if domainid is not None:
            args['domainid'] = domainid

        result = self._sync_request('listDiskOfferings', **args)
        diskofferings = result['diskoffering']
        return diskofferings

    def describeNetworks(self):
        result = self._sync_request('listNetworks')
        network = result['network']
        return network


    def describePublicIpAddress(self, addressid):
        addresses = self.describePublicIpAddresses(addressid)

        if len(addresses) == 0:
            #インスタンスが存在しない場合
            raise IaasException("EPROCESS-000708", [addressid,])

        if len(addresses) > 1:
            #インスタンスを複数参照できた場合
            raise IaasException("EPROCESS-000709", [addressid,])

        return addresses[0]

    def describePublicIpAddresses(self, addressid=None):
        args = {}
        if addressid is not None:
            args['id'] = addressid
        result = self._sync_request('listPublicIpAddresses', **args)
        network = result['publicipaddress']
        return network


    ############################################################
    #
    #    Instance 系
    #
    ############################################################
    def runInstances (self, name, dname, serviceofferingid, imageid, zoneid, **kwargs):
        extra_args = {}
        if zoneid is None:
            location = self.describeAvailabilityZones()[0]
            zoneid = location['id']

        #以下任意パラメータ
        if self.hostid is not None:
            extra_args['hostid'] = self.hostid

        network_id = kwargs.pop('network_id', None)
        if network_id is not None:
            extra_args['networkids'] = network_id

        securityGroups = kwargs.pop('securitygroupids', None)
        if securityGroups is not None:
            extra_args['securitygroupnames'] = securityGroups

        keypair = kwargs.pop('keypair', None)
        if keypair is not None:
            extra_args['keypair'] = keypair

        userdata = kwargs.pop('userdata', None)
        if userdata is not None:
            extra_args['userdata'] = base64.b64encode(userdata)

        #インスタンス作成
        result = self._async_request('deployVirtualMachine',
            name=name,
            displayname=dname,
            serviceofferingid=serviceofferingid,
            templateid=imageid,
            zoneid=zoneid,
            **extra_args
        )

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000703", [])

        return node



    def startInstance(self, instanceid):
        params = {'id':instanceid,}
        result =  self._async_request('startVirtualMachine', **params)

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000715", [])

        return node

    def changeInstance(self, instanceid, serviceofferingid):
        params = {'id':instanceid, 'serviceofferingid':serviceofferingid}
        result =  self._sync_request('changeServiceForVirtualMachine', **params)

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000715", [])

        return node

    def stopInstance(self, instanceid):
        params = {'id': instanceid}
        result =  self._async_request('stopVirtualMachine', **params)

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000717", [])
        return node

    #本来destroy_nodeはnode自体を受け取るがこの関数はIDを受ける形とした
    def terminateInstance(self, instanceid):
        result =  self._async_request('destroyVirtualMachine', id=instanceid)

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000704", [])

        return node

    def rebootInstance(self, instanceid):
        params = {'id': instanceid}
        result =  self._async_request('rebootVirtualMachine', **params)

        node = None
        if 'virtualmachine' in  result:
            node = result['virtualmachine']
        else:
            raise IaasException("EPROCESS-000717", [])
        return node



    ############################################################
    #
    #    Volume 系
    #
    ############################################################
    def createVolume(self, name, location=None, size=None, snapshotId=None, diskid=None, iscustomized=True):
        args = {}

        if snapshotId is None and diskid is None:
            # ボリューム作成失敗時
            raise IaasException("**********")

        if snapshotId is not None:
            args['snapshotid'] = snapshotId

        if diskid is not None:
            args['diskofferingid'] = diskid

        if size is not None and iscustomized:
            args['size'] = str(size)

        if location is None:
            location = self.describeAvailabilityZones()[0]["id"]
        args['zoneid'] = location

        result = self._async_request('createVolume',
                    name=name,
                    **args)

        volume = None
        if 'volume' in  result:
            volume = result['volume']
        else:
            raise IaasException("EPROCESS-000707", [])
        return volume


    def attachVolume(self, volumeId, instanceId, deviceid=None):
        args = {}
        if deviceid is not None:
            args['deviceid'] = deviceid

        result = self._async_request('attachVolume',
                    id = volumeId,
                    virtualmachineid = instanceId,
                    **args)

        volume = result['volume']
        return volume

    def detachVolume(self, volumeId, instanceId=None, deviceid=None):
        args = {}

        if volumeId is not None:
            args['id'] = volumeId
        else:
            if deviceid is not None:
                args['deviceid'] = deviceid
            if instanceId is not None:
                args['virtualmachineid'] = instanceId

        result = self._async_request('detachVolume', **args)

        volume = result['volume']
        return volume

    def deleteVolume(self, volumeId):
        result = self._sync_request('deleteVolume', id = volumeId)

        if result.get('success', '').lower() != 'true':
            return False
        else:
            return True

    ############################################################
    #
    #    Address 系
    #
    ############################################################

    def enableStaticNat(self, addressid, virtualmachineid):
        result = self._sync_request('enableStaticNat', ipaddressid = addressid, virtualmachineid=virtualmachineid)

        if result.get('success', '').lower() != 'true':
            return False
        else:
            return True

    def associateAddress(self,  zoneid=None):

        #渡されてなければデフォルトゾーン
        if zoneid is None:
            zoneid = self.describeAvailabilityZones()[0]["id"]

        result = self._sync_request('associateIpAddress', zoneid = zoneid)

        addressid = result['id']
        return addressid

    def disassociateAddress(self, addressid):
        result = self._async_request('disassociateIpAddress', id = addressid)

        if result['success'] != 'true':
            return False
        else:
            return True


    def disableStaticNat(self, addressid):
        result = self._async_request('disableStaticNat', ipaddressid = addressid)

        if result['success'] != 'true':
            return False
        else:
            return True


    ############################################################
    #
    #    KeyPair 系
    #
    ############################################################

    def createKeyPair(self, keyName):
        result = self._sync_request('createSSHKeyPair', name = keyName)

        keyPair = result['keypair']
        return keyPair


    def deleteKeyPair(self, keyName):
        result = self._sync_request('deleteSSHKeyPair', name = keyName)

        if result.get('success', '').lower() != 'true':
            return False
        else:
            return True


    def registerSSHKeyPair(self, keyName, keyMaterial):
        result = self._sync_request('registerSSHKeyPair', name = keyName, publickey = keyMaterial)

        keypair = result['keypair']
        keyFingerprint = keypair['fingerprint']

        return keyFingerprint

    ############################################################
    #
    #    Snapshot 系
    #
    ############################################################

    def createSnapshot(self, volumeId):
        result = self._async_request('createSnapshot', volumeid = volumeId)

        snapshot = result['snapshot']
        return snapshot

    def deleteSnapshot(self, snapshotId):
        result = self._async_request('deleteSnapshot', id = snapshotId)

        if result['success'] != 'true':
            return False
        else:
            return True


    ############################################################
    #
    #    Tags 系
    #
    ############################################################

    #存在しません

    ############################################################
    #
    #    その他
    #
    ############################################################
    def getPasswordData(self, instanceId):
        try:
            result = self._sync_request('getVMPassword', id = instanceId)
            password = result['password']
            encryptedpassword = password['encryptedpassword']
        except Exception:
            raise IaasException("EPROCESS-000721", [instanceId,])

        return encryptedpassword


    ############################################################
    #
    #    LoadBalancer 操作系
    #
    ############################################################

    ALGORITHM_ROUND_ROBIN = 'roundrobin'
    ALGORITHM_LEAST_CONNECTIONS = 'leastconn'

    def createLoadBalancer(self, name, algorithm=None,
                           port=80, private_port=None, zoneid=None, addressid=None):

        if algorithm is None:
            algorithm = self.ALGORITHM_ROUND_ROBIN
        if zoneid is None:
            zoneid = self.describeAvailabilityZones()[0]['id']
        else:
            zoneid = zoneid

        if private_port is None:
            private_port = port

        if addressid is None:
            result = self._async_request('associateIpAddress', zoneid=zoneid)
            addressid = result['ipaddress']['id']

        result = self._sync_request('createLoadBalancerRule',
            algorithm=algorithm,
            name=name,
            privateport=private_port,
            publicport=port,
            publicipid=addressid,
        )

        balancerid = result['id']
        balancer = self.describeLoadBalancer(balancerid)

        return balancer


    def deleteLoadBalancer(self, loadBalancerid):
        balancer = self.describeLoadBalancer(loadBalancerid)

        self._async_request('deleteLoadBalancerRule', id=balancer.id)
        self._async_request('disassociateIpAddress',  id=balancer.ex_public_ip_id)

    def attach_members(self, loadBalancerid, virtualmachineids):
        for virtualmachineid in virtualmachineids:
            self._async_request('assignToLoadBalancerRule', id=loadBalancerid, virtualmachineids=virtualmachineid)

        return True


    def detach_members(self, loadBalancerid, virtualmachineids):
        for virtualmachineid in virtualmachineids:
            self._async_request('removeFromLoadBalancerRule', id=loadBalancerid, virtualmachineids=virtualmachineid)
        return True


    def _to_balancer(self, obj):
        balancer = LoadBalancer(
            id=obj['id'],
            name=obj['name'],
            state=obj['state'],
            ip=obj['publicip'],
            port=obj['publicport'],
            driver=self.connection.driver
        )
        balancer.ex_private_port = obj['privateport']
        balancer.ex_public_ip_id = obj['publicipid']
        return balancer

    def _to_member(self, obj, port):
        return Member(
            id=obj['id'],
            ip=obj['nic'][0]['ipaddress'],
            port=port
        )

