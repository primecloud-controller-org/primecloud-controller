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

from iaasgw.common.pccConnections import PCCVCloudConnection
from iaasgw.exception.iaasException import IaasException
from iaasgw.log.log import IaasLogger
from iaasgw.module.vcloud.vCloudModule import PccVMDisk, PccStorageProfile, \
    PccVAppNetwork, PccVMNetwork
from iaasgw.module.vcloud.vcloudXMLWriter import RecomposeVAppXML_ADD_VM, \
    RecomposeVAppXML_DEL_VM, InstantiateVAppXML, ComposeVAppXML, \
    SetProductSectionListXML
from iaasgw.utils.propertyUtil import getVCloudInfo, getOtherProperty
from iaasgw.utils.stringUtils import isNotEmpty, isBit
from libcloud.compute.base import Node
from libcloud.compute.drivers.vcloud import VCloud_1_5_NodeDriver, get_url_path, \
    fixxpath
from libcloud.utils.py3 import urlparse
from xml.etree import ElementTree as ET
import copy
import time
import traceback


urlparse = urlparse.urlparse

class VCloudIaasClient(VCloud_1_5_NodeDriver):

    STOPPED = "Stopped"
    RUNNING = "Running"
    UNKNOWN = "Unknown"
    RESOLVED = "Resolved"
    WAITING = "Waiting"

    RETRY_MAX = 3

    NODE_STATE_MAP = {'-1': UNKNOWN,
                 '0': UNKNOWN,
                 '1': RESOLVED,
                 '2': WAITING,
                 '3': UNKNOWN,
                 '4': RUNNING,
                 '5': UNKNOWN,
                 '6': UNKNOWN,
                 '7': UNKNOWN,
                 '8': STOPPED,
                 '9': UNKNOWN,
                 '10': UNKNOWN,
                 '11': WAITING,
                 '12': WAITING,
                 '13': WAITING,
                 '14': WAITING,
                 '15': WAITING,
                 }

    logger = IaasLogger()
    platformNo = None
    timeout = 600
    username = None
    vdc_name = None
    defnet = None

    connectionCls = PCCVCloudConnection

    def __init__(self, platforminfo, username, key, secret=None):
        self.platformNo =  platforminfo["platformNo"]
        self.username = username
        self.logger.info(u"利用プラットフォーム" + str(self.platformNo))


        #接続情報
        vCloudInfo = getVCloudInfo(self.platformNo)
        host = vCloudInfo["host"]
        secure = vCloudInfo["secure"]
        pltfmNotimeout = vCloudInfo["timeout"]
        self.vdc_name = vCloudInfo["vdc"]
        self.defnet = vCloudInfo["defnetwork"]

        #アクセスキー生成
        key = key + "@" + vCloudInfo["org"]

        #タイムアウト
        if pltfmNotimeout is not None:
            #コネクションタイムアウト
            self.connectionCls.timeout = int(pltfmNotimeout)
            #タスク待ちタイムアウト
            self.timeout = int(pltfmNotimeout)

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

        self.logger.info(u"接続情報==> "+host+": secure=" + str(secure))

        VCloud_1_5_NodeDriver.__init__(self, key=key, secret=secret, secure=secure, host= host)


############################################################
#
#    基礎データ
#
############################################################

    #########################
    #
    # プラットフォームNo取得
    #
    #########################
    def getPlatformNo(self):
        return self.platformNo

    #########################
    #
    # ユーザーネーム取得
    #
    #########################
    def getUsername(self):
        return self.username


############################################################
#
#    参照系  describe
#
############################################################

    #########################
    #
    # 組織取得
    #
    #########################
    def getUseVdc(self):
        return self._get_vdc(self.vdc_name)

    #########################
    #
    # 組織設定ストレージプロファイル取得
    #
    #########################
    def getVdcStorageprofiles(self, vdc):
        res = self.requestLoop(get_url_path(vdc.id)).object

        #ストレージプロファイル
        storageprofiles = {}
        for storageprofile in res.findall(fixxpath(res, 'VdcStorageProfiles/VdcStorageProfile')):
            storageprofiles[storageprofile.get('name')] = PccStorageProfile(storageprofile.get('name'), storageprofile.get('href'))

        return storageprofiles

    #########################
    #
    # 組織ネットワーク取得
    #
    #########################
    def describeVdcNetwork(self):
        vdc = self.getUseVdc()
        res = self.requestLoop(get_url_path(vdc.id)).object

        #VDCネットワーク
        vdcnetworks = []
        for vdcnetworkconfig in res.findall(fixxpath(res, 'AvailableNetworks/Network')):
            name = vdcnetworkconfig.get('name')
            href = vdcnetworkconfig.get('href')
            res2 = self.requestLoop(get_url_path(href)).object

            vdcnetworks.append(self._makePccVAppNetwork(res2, name, href))

        return vdcnetworks

    #########################
    #
    # VApp取得
    #
    #########################
    def describeMyCloud(self, vdc, vApp_name):
        vAppName = vdc.name + "-" + vApp_name
        return self.ex_find_node(vAppName, vdc)


    #########################
    #
    # VApp取得
    #
    #########################
    def describeVdcMyCloud(self, vdc):
        res = self.connection.request(get_url_path(vdc.id))
        elms = res.object.findall(fixxpath(
            res.object, "ResourceEntities/ResourceEntity")
        )
        vapps = [
            (i.get('name'), i.get('href'))
            for i in elms
            if i.get('type')
                == 'application/vnd.vmware.vcloud.vApp+xml'
                and i.get('name')
        ]

        nodes = []
        for vapp_name, vapp_href in vapps:
            try:
                res = self.connection.request(
                    get_url_path(vapp_href),
                    headers={'Content-Type': 'application/vnd.vmware.vcloud.vApp+xml'}
                )
                nodes.append(self._to_node(res.object))
            except Exception:
                self.logger.error(traceback.format_exc())
                raise

        return nodes

    #########################
    #
    # VAPPネットワーク取得
    #
    #########################
    def describeVappNetwork(self, vapp):
        vappnetworks = []
        res = self.requestLoop(get_url_path(vapp.id)).object
        for networkconfig in res.findall(fixxpath(res, 'NetworkConfigSection/NetworkConfig')):
            name = networkconfig.get('networkName')
            #未設定用NW「'none'」は無視する
            if name == 'none':
                continue

            vappnetworks.append(self._makePccVAppNetwork(networkconfig, name))

        return vappnetworks


    #########################
    #
    # VM取得（全件）
    #
    #########################
    def describeInstances(self, vApp):
        return vApp.extra['vms']

    #########################
    #
    # VM取得（単）
    #
    #########################
    def describeInstance(self, vApp, vm_name):
        vms = vApp.extra['vms']
        target = None
        for vm in vms:
            if vm["name"] == vm_name:
                target = vm
        return target

    #########################
    #
    # VMネットワーク取得
    #
    #########################
    def describeVMNetwork(self, vm):
        res = self.requestLoop('%s/networkConnectionSection' % get_url_path(vm["id"]))
        print ET.tostring(res.object)
        primary_index = res.object.find(fixxpath(res.object, 'PrimaryNetworkConnectionIndex')).text
        net_conns = res.object.findall(fixxpath(res.object, 'NetworkConnection'))
        retNetworks = []
        for item in net_conns:
            name = item.get('network')
            ipMode = item.find(fixxpath(item, 'IpAddressAllocationMode')).text
            ipAddress = item.find(fixxpath(item, 'IpAddress')).text
            index = item.find(fixxpath(item, 'NetworkConnectionIndex')).text
            isPrimary = False
            if index == primary_index:
                isPrimary = True

            retNetworks.append(PccVMNetwork(name, ipAddress, ipMode, index, isPrimary))

        return retNetworks

    #########################
    #
    # 名称からストレージプロファイル取得
    #
    #########################
    def describeStorageProfile(self, vdc, sp_Name):
        res = self.requestLoop(get_url_path(vdc.id)).object

        for storageprofile in res.findall(fixxpath(res, 'VdcStorageProfiles/VdcStorageProfile')):
            if storageprofile.get('name') == sp_Name:
                return PccStorageProfile(storageprofile.get('name'), storageprofile.get('href'))
        return None

    #########################
    #
    # イメージの名称（リスト）を取得
    #
    #########################
    def describeImageNames(self, location=None):
        imageNames = []
        images = self.list_images()
        for image in images:
            res = self.requestLoop(image.id).object
            res_ents = res.findall(fixxpath(res, "Children/Vm"))
            for i in res_ents:
                imageNames.append(i.get("name"))

        return imageNames

    #########################
    #
    # イメージのリンクを取得
    #
    #########################
    def describeImageHref(self, imagename):
        images = self.list_images()
        for image in images:
            res = self.requestLoop(image.id).object
            res_ents = res.findall(fixxpath(res, "Children/Vm"))
            for i in res_ents:
                if imagename == i.get("name"):
                    return i.get("href")

    #########################
    #
    # VMに設定されているディスクを取得
    #
    #########################
    def describeVolumes(self, vm):
        rasd_ns = '{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}'

        # Get virtualHardwareSection/disks section
        res = self.requestLoop('%s/virtualHardwareSection/disks' % get_url_path(vm["id"]))
        disks =[]
        for item in res.object.findall(fixxpath(res.object, 'Item')):
            if item.find('%sHostResource' % rasd_ns) is not None:
                name = item.find('%sInstanceID' % rasd_ns).text
                size = item.find('%sHostResource' % rasd_ns).get(fixxpath(item, 'capacity'))
                busType = item.find('%sHostResource' % rasd_ns).get(fixxpath(item, 'busType'))
                unitNo = item.find('%sAddressOnParent' % rasd_ns).text

                disks.append(PccVMDisk(name, size, busType, unitNo))

        return disks

    #########################
    #
    # VMに設定されているディスクを取得（単）
    #
    #########################
    def describeVolume(self, vm, deiskid):
        disks = self.describeVolumes(vm)
        for desk in disks:
            if str(desk.name) == str(deiskid):
                return desk

        return None

    #########################
    #
    # ネットワークの名称（リスト）を取得
    # describeVdcNetworkを利用してください
    #
    #########################
    #def describeNetworkNames(self):
    #    networkNames = []
    #    for network in self.networks:
    #        networkNames.append(network.get("name"))
    #    return networkNames

    #########################
    #
    # ネットワークのリンクを取得
    #
    #########################
    def describeNetworkHref(self, name):
        for network in self.networks:
            if name == network.get("name"):
                return network.get("href")

    #########################
    #
    # VMに設定されているCPU数を取得
    #
    #########################
    def describeCPU(self, vm):
        res = self.requestLoop('%s/virtualHardwareSection/cpu' % get_url_path(vm["id"]))

        cpu =res.object.find('{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}VirtualQuantity').text
        return cpu

    #########################
    #
    # VMに設定されているメモリ数を取得
    #
    #########################
    def describeMemory(self, vm):
        res = self.requestLoop('%s/virtualHardwareSection/memory' % get_url_path(vm["id"]))

        memory =res.object.find('{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}VirtualQuantity').text
        return memory

    #########################
    #
    # ネットワーク設定取得補助
    #
    #########################
    def _makePccVAppNetwork(self, network_xml, name, href=None):
            name = name
            href = href
            fencemode = None
            gateway = None
            netmask = None
            dns1 = None
            dns2 = None
            iprangeF =None
            iprangeT =None
            primary = False


            if name == getOtherProperty("vCloud.PCCNetwork"):
                primary = True

            if href is None:
                link = network_xml.find(fixxpath(network_xml, 'Link'))
                if link is not None:
                    href = link.get('href')

            fence = network_xml.find(fixxpath(network_xml, 'Configuration/FenceMode'))
            if fence is not None:
                fencemode = fence.text

            scope =  network_xml.find(fixxpath(network_xml, 'Configuration/IpScopes/IpScope'))
            for elem in scope:
                if elem.tag == '{http://www.vmware.com/vcloud/v1.5}Gateway':
                    gateway = elem.text
                if elem.tag == '{http://www.vmware.com/vcloud/v1.5}Netmask':
                    netmask = elem.text
                if elem.tag == '{http://www.vmware.com/vcloud/v1.5}Dns1':
                    dns1 = elem.text
                if elem.tag == '{http://www.vmware.com/vcloud/v1.5}Dns2':
                    dns2 = elem.text

            ipranges =  network_xml.findall(fixxpath(network_xml, 'Configuration/IpScopes/IpScope/IpRanges/IpRange'))
            if ipranges is not None:
                for iprange in ipranges:
                    for elem in iprange:
                        if elem.tag == '{http://www.vmware.com/vcloud/v1.5}StartAddress':
                            iprangeF = elem.text
                        if elem.tag == '{http://www.vmware.com/vcloud/v1.5}EndAddress':
                            iprangeT = elem.text
                    #複数範囲は現状想定しない
                    break

            return PccVAppNetwork(name, href, fencemode, gateway, netmask, dns1, dns2, iprangeF, iprangeT, primary)


############################################################
#
#    vApp 系
#
############################################################

    #########################
    #
    # 空VAppを作成
    #
    #########################
    def createMyCloud(self, vdc, name, defNetworks):

        try:
            vappName = vdc.name + "-" + name
            vapp_href = self._compose_MyCloud(vdc, vappName, defNetworks)
            res = self.requestLoop(get_url_path(vapp_href))
            node = self._to_node(res.object)

            #実行ログ
            self.logger.info(None, "IPROCESS-100701", [name])

            return node
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000801", [name,])

    #########################
    #
    # テンプレートからVAppを作成
    #現在未使用 テンプレートからマイクラウドを作成する場合に使用
    #
    #########################
    def createMyCloudByTemplate(self, vdc, name, template_name, defNetworks):
        #vAppテンプレートを名称から特定
        template = None
        templates = self.list_images()
        for temp in templates:
            if template_name == temp.name:
                template = temp.id

        try:
            vappName = vdc + "-" + name
            vapp_href = self._instantiate_MyCloud(vdc, vappName, template, defNetworks)
            res = self.requestLoop(get_url_path(vapp_href))
            node = self._to_node(res.object)
            return node
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000801", [name,])

    #########################
    #
    # VAppを削除
    #
    #########################
    def terminateMyCloud(self, myCloud):
        try:
            self.destroy_node(myCloud)

            #実行ログ
            self.logger.info(None, "IPROCESS-100702", [myCloud.name])

        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000802", [myCloud.name,])


    #########################
    #
    # 空VAppを作成補助
    #
    #########################
    def _compose_MyCloud(self, vdc, name, useNetworks):
        compose_xml = ComposeVAppXML(
            name=name,
            useNetworks=useNetworks
        )

        self.logger.info(compose_xml.tostring())
        # Instantiate VM and get identifier.
        res = self.requestLoop(
            '%s/action/composeVApp' % get_url_path(vdc.id),
            data=compose_xml.tostring(),
            method='POST',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.composeVAppParams+xml'}
        )
        vapp_href = res.object.get('href')

        task_href = res.object.find(fixxpath(res.object, "Tasks/Task")).get('href')
        self._wait_for_task_completion(task_href)
        return vapp_href

    #########################
    #
    # テンプレートVAppを作成補助
    #現在未使用 テンプレートからマイクラウドを作成する場合に使用
    #
    #########################
    def _instantiate_MyCloud(self, name, template, networks):
        instantiate_xml = InstantiateVAppXML(
            name=name,
            template=template,
            networks=networks
        )

        # Instantiate VM and get identifier.
        res = self.requestLoop(
            '%s/action/instantiateVAppTemplate' % get_url_path(self.vdc.id),
            data=instantiate_xml.tostring(),
            method='POST',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml'}
        )
        vapp_href = res.object.get('href')

        task_href = res.object.find(fixxpath(res.object, "Tasks/Task")).get('href')
        self._wait_for_task_completion(task_href)
        return vapp_href

    #########################
    #
    # ネットワークのチェック
    #
    #########################
    def checkNetwork(self, vapp, instanceNw):
        vappnetworks = self.describeVappNetwork(vapp)

        #不足チェック
        for net in instanceNw:
            isExist = False
            for vappnet in vappnetworks:
                if net["NETWORK_NAME"] == vappnet.name:
                    isExist = True
                    break
            #存在しない
            if not isExist:
                self._add_vapp_nw(vapp, net)

    #########################
    #
    # ネットワーク追加（VAPP）
    #
    #########################
    def _add_vapp_nw(self, vapp, newNetwork):
        rasd_ns = "{http://www.vmware.com/vcloud/v1.5}"

        res = self.requestLoop('%s/networkConfigSection' % get_url_path(vapp.id))
        network_config = ET.SubElement(res.object, "%sNetworkConfig" % rasd_ns)
        # Don't set a custom vApp VM network name
        network_config.set('networkName', newNetwork["NETWORK_NAME"])
        configuration = ET.SubElement(network_config, '%sConfiguration' % rasd_ns)
        for vdcnet in self.networks:
            if vdcnet.get('name') == newNetwork["NETWORK_NAME"]:
                ET.SubElement(configuration, '%sParentNetwork' % rasd_ns, {'href': vdcnet.get('href')})
                ET.SubElement(configuration, '%sFenceMode' % rasd_ns).text = "bridged"

        self.logger.info(ET.tostring(res.object))
        #変更を行った場合のみ通信する
        res = self.requestLoop(
            '%s/networkConfigSection' % get_url_path(vapp.id),
            data=ET.tostring(res.object),
            method='PUT',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.networkConfigSection+xml'}
        )
        self._wait_for_task_completion(res.object.get('href'))


############################################################
#
#    vm 系
#
############################################################

    #########################
    #
    # VMを作成
    #
    #########################
    def createInstances(self, node, **kwargs):
        image = kwargs['image']
        vm_name = kwargs['vm_name']
        vm_storage = kwargs['vm_storage']
        vm_networks = kwargs.get('vm_networks')
        vm_fqdn = kwargs.get('fqdn')


        try:
            self._add_vm(node, image, vm_name, vm_fqdn, vm_storage, vm_networks)
            res = self.requestLoop(get_url_path(node.id))

            #実行ログ
            self.logger.info(None, "IPROCESS-100703", [vm_name,])

            return self._to_node(res.object)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000803", [vm_name,])


    #########################
    #
    # VMを開始
    #
    #########################
    def startInstance(self, node, vm):
        try:
            node = self.ex_deploy_vm(node, vm)

            #実行ログ
            self.logger.info(None, "IPROCESS-100705", [vm["name"],])

            return node
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000805", [vm["name"],])

    #########################
    #
    # VMを停止
    #
    #########################
    def stopInstance(self, node, vm):
        try:
            node =  self.ex_undeploy_vm(node, vm)

            #実行ログ
            self.logger.info(None, "IPROCESS-100706", [vm["name"],])

            return node
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000806", [vm["name"],])


    #########################
    #
    # VMを削除
    #
    #########################
    def terminateInstance(self, node, vm_name):
        try:
            vm = self.describeInstance(node, vm_name)
            vm_harf = vm["id"]
            self._del_vm(node, vm_harf)

            #実行ログ
            self.logger.info(None, "IPROCESS-100704", [vm_name,])

        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000804", [vm_name,])


    #########################
    #
    # VMを編集
    #
    #########################
    def editInstance(self, vm, **kwargs):
        try:
            node = self.ex_edit_vm(vm["id"], **kwargs)

            #実行ログ
            self.logger.info(None, "IPROCESS-100710", [vm["name"],])

            return node
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000810", [vm["name"],])


    #########################
    #
    # VMを作成補助
    #
    #########################
    def _add_vm(self, node, image, vm_name, vm_fqdn, vm_storage, vm_networks):
        add_xml = RecomposeVAppXML_ADD_VM(
            name=node.name,
            image=image,
            vm_name=vm_name,
            vm_storage = vm_storage,
            vm_networks=vm_networks,
            vm_fqdn=vm_fqdn
        )

        self.logger.info(add_xml.tostring())

        # Instantiate VM and get identifier.
        res = self.requestLoop(
            '%s/action/recomposeVApp ' % get_url_path(node.id),
            data=add_xml.tostring(),
            method='POST',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.recomposeVAppParams+xml'}
        )
        task_href = res.object.get('href')
        self._wait_for_task_completion(task_href)


    #########################
    #
    # VMを削除補助
    #
    #########################
    def _del_vm(self, node, vm_harf):
        del_xml = RecomposeVAppXML_DEL_VM(
            name=node.name,
            vm_harf=vm_harf
        )

        self.logger.info(del_xml.tostring())

        # Instantiate VM and get identifier.
        res = self.requestLoop(
            '%s/action/recomposeVApp ' % get_url_path(node.id),
            data=del_xml.tostring(),
            method='POST',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.recomposeVAppParams+xml'}
        )
        task_href = res.object.get('href')
        self._wait_for_task_completion(task_href)


    #########################
    #
    # VMを編集補助
    # 現在はストレージプロファイルのみに対応
    #
    #########################
    def ex_edit_vm(self, vm_harf, **kwargs):
        storageProfile = kwargs.get('storageProfile')

        res = self.requestLoop(get_url_path(vm_harf))

        #ストレージの変更
        if storageProfile is not None:
            res.object.find(fixxpath(res.object, "StorageProfile")).set('name', storageProfile.name)
            res.object.find(fixxpath(res.object, "StorageProfile")).set('href', storageProfile.href)

        self.logger.info(ET.tostring(res.object))

        ress = self.requestLoop(get_url_path(vm_harf),
                data=ET.tostring(res.object),
                method='PUT',
                headers={'Content-Type': 'application/vnd.vmware.vcloud.vm+xml'}
            )
        self._wait_for_task_completion(ress.object.get('href'))


    #########################
    #
    # VMへの命令制御
    # 現在は利用していません
    #
    #########################
    def _perform_power_operation_vm(self, node, vm, operation):
        res = self.requestLoop(
            '%s/power/action/%s' % (get_url_path(vm["id"]), operation), method='POST')

        self._wait_for_task_completion(res.object.get('href'), self.connectionCls.timeout)
        res = self.requestLoop(get_url_path(node.id))
        return self._to_node(res.object)


    #########################
    #
    # 開始命令制御
    #
    #########################
    def ex_deploy_vm(self, node, vm):
        deploy_xml = ET.Element('DeployVAppParams', {'powerOn': 'true', 'xmlns': 'http://www.vmware.com/vcloud/v1.5'})
        res = self.requestLoop('%s/action/deploy' % get_url_path(vm["id"]),
                                      data=ET.tostring(deploy_xml),
                                      method='POST',
                                      headers={'Content-Type': 'application/vnd.vmware.vcloud.deployVAppParams+xml'}
                                      )
        self._wait_for_task_completion(res.object.get('href'))
        res = self.requestLoop(get_url_path(node.id))
        return self._to_node(res.object)


    #########################
    #
    # 停止命令制御
    #
    #########################
    def ex_undeploy_vm(self, node, vm):
        undeploy_xml = ET.Element('UndeployVAppParams', {'xmlns': 'http://www.vmware.com/vcloud/v1.5'})
        undeploy_power_action_xml = ET.SubElement(undeploy_xml, 'UndeployPowerAction')
        undeploy_power_action_xml.text = 'shutdown'

        try:
            res = self.requestLoop('%s/action/undeploy' % get_url_path(vm["id"]),
                data=ET.tostring(undeploy_xml),
                method='POST',
                headers={'Content-Type': 'application/vnd.vmware.vcloud.undeployVAppParams+xml'}
                )
            self._wait_for_task_completion(res.object.get('href'))
        except Exception:
            undeploy_power_action_xml.text = 'powerOff'
            res = self.requestLoop(
                '%s/action/undeploy' % get_url_path(vm["id"]),
                data=ET.tostring(undeploy_xml),
                method='POST',
                headers={'Content-Type': 'application/vnd.vmware.vcloud.undeployVAppParams+xml'}
                )
            self._wait_for_task_completion(res.object.get('href'))

        res = self.requestLoop(get_url_path(node.id))
        return self._to_node(res.object)



############################################################
#
#    Volume 系
#
############################################################
    #########################
    #
    # ディスクの追加
    #
    #########################
    def attachVolume(self, vm, disk):
        try:
            self._validate_vm_disk_size(disk["SIZE"])
            diskid = self._add_vm_disk(vm, disk)

            #実行ログ
            self.logger.info(None, "IPROCESS-100707", [vm["name"],])

            return self.describeVolume(vm, diskid)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000807", [vm["name"],])

    #########################
    #
    # ディスクの編集
    #
    #########################
    def editVolume(self, vm, disk):
        try:
            self._validate_vm_disk_size(disk["SIZE"])
            diskid = self._edit_vm_disk(vm, disk)

            #実行ログ
            self.logger.info(None, "IPROCESS-100711", [vm["name"],])

            return self.describeVolume(vm, diskid)
        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000811", [vm["name"],])


    #########################
    #
    # ディスクの削除
    #
    #########################
    def detachVolume(self, vm, diskid):
        try:
            self._del_vm_disk(vm, diskid)

            #実行ログ
            self.logger.info(None, "IPROCESS-100708", [vm["name"],])

        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000808", [vm["name"],])

    #########################
    #
    # ディスクの追加制御
    #
    #########################
    def _add_vm_disk(self, vm, vm_disk):
        #ディスクが存在しない場合は何もしない
        if vm_disk is None:
            return

        rasd_ns = '{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}'

        # virtualHardwareSection/disks 取得
        res = self.requestLoop('%s/virtualHardwareSection/disks' % get_url_path(vm["id"]))

        existing_ids = []
        new_disk = None
        #既存ディスク情報のチェック
        for item in res.object.findall(fixxpath(res.object, 'Item')):
            for elem in item:
                if elem.tag == '%sInstanceID' % rasd_ns:
                    existing_ids.append(int(elem.text))
            if item.find('%sHostResource' % rasd_ns) is not None:
                new_disk = item

        #追加するディスク情報
        new_disk = copy.deepcopy(new_disk)
        for elem in new_disk:
            #不要なパラメータを消す
            if elem.tag in ['%sAddressOnParent' % rasd_ns, '%sParent' % rasd_ns]:
                new_disk.remove(elem)

        disk_id = max(existing_ids) + 1
        diskName = 'Hard Disk ' + str(disk_id)
        #new_disk.find('%sAddressOnParent' % rasd_ns).text = str(vm_disk["UNIT_NO"])
        new_disk.find('%sInstanceID' % rasd_ns).text = str(disk_id)
        new_disk.find('%sElementName' % rasd_ns).text = diskName
        new_disk.find('%sHostResource' % rasd_ns).set(fixxpath(new_disk, 'capacity'), str(int(vm_disk["SIZE"]) * 1024))
        res.object.append(new_disk)

        self.logger.info(ET.tostring(res.object))
        res = self.requestLoop(
            '%s/virtualHardwareSection/disks' % get_url_path(vm["id"]),
            data=ET.tostring(res.object),
            method='PUT',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.rasditemslist+xml'}
        )
        self._wait_for_task_completion(res.object.get('href'))

        return disk_id

    #########################
    #
    # ディスクの編集制御
    #
    #########################
    def _edit_vm_disk(self, vm, disk):
        #ディスクが存在しない場合は何もしない
        if disk is None:
            return

        rasd_ns = '{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}'

        # virtualHardwareSection/disks 取得
        res = self.requestLoop('%s/virtualHardwareSection/disks' % get_url_path(vm["id"]))

        for item in res.object.findall(fixxpath(res.object, 'Item')):
            if item.find('%sInstanceID' % rasd_ns) is not None:
                if str(item.find('%sInstanceID' % rasd_ns).text) == str(disk["DISK_ID"]):
                    item.find('%sHostResource' % rasd_ns).set(fixxpath(item, 'capacity'), str(int(disk["SIZE"]) * 1024))

        self.logger.info(ET.tostring(res.object))
        res = self.requestLoop(
            '%s/virtualHardwareSection/disks' % get_url_path(vm["id"]),
            data=ET.tostring(res.object),
            method='PUT',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.rasditemslist+xml'}
        )
        self._wait_for_task_completion(res.object.get('href'))

    #########################
    #
    # ディスクの削除制御
    #
    #########################
    def _del_vm_disk(self, vm, disk_id):
        #ディスクIDが指定されていない場合は何もしない
        if disk_id is None:
            return

        rasd_ns = '{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}'

        # virtualHardwareSection/disks 取得
        res = self.requestLoop('%s/virtualHardwareSection/disks' % get_url_path(vm["id"]))

        for item in res.object.findall(fixxpath(res.object, 'Item')):
            if item.find('%sHostResource' % rasd_ns) is not None:
                name = item.find('%sInstanceID' % rasd_ns).text
                if str(name) == str(disk_id):
                    res.object.remove(item)

        self.logger.info(ET.tostring(res.object))

        res = self.requestLoop(
            '%s/virtualHardwareSection/disks' % get_url_path(vm["id"]),
            data=ET.tostring(res.object),
            method='PUT',
            headers={'Content-Type': 'application/vnd.vmware.vcloud.rasditemslist+xml'}
        )
        self._wait_for_task_completion(res.object.get('href'))


############################################################
#
#    Address 系
#
############################################################

    #########################
    #
    # ネットワーク変更（VM）
    #
    #########################
    def _change_vm_nw(self, vm_id, vm_nw):
        rasd_ns = "{http://www.vmware.com/vcloud/v1.5}"

        indexList = ["0","1","2","3","4","5","6","7","8","9","10",]

        #リストを名称キーのマップへ変換
        editmap = {}
        makemap = {}
        for nw in vm_nw:
            if nw["NETWORK_INDEX"] is not None:
                editmap[str(nw["NETWORK_INDEX"])] = nw
            else:
                makemap[str(nw["NETWORK_NO"])] = nw

        isEdit = False
        vms = self._get_vm_elements(vm_id)
        for vm in vms:
            res = self.requestLoop('%s/networkConnectionSection' % get_url_path(vm.get('href')))
            def_primary_index = res.object.find(fixxpath(res.object, 'PrimaryNetworkConnectionIndex')).text
            primary_index = def_primary_index
            net_conns = res.object.findall(fixxpath(res.object, 'NetworkConnection'))
            for item in net_conns:
                name = item.get('network')
                index = item.find(fixxpath(item, 'NetworkConnectionIndex')).text
                #対象の設定を取得
                if not editmap.has_key(index):
                    #取得できなければこのNWは削除
                    res.object.remove(item)
                    isEdit = True
                else:
                    #利用済インデックスをリストから削除
                    indexList.remove(index)
                    #取得できれば設定を比較
                    newNw = editmap.pop(index)

                    #Primary チェック
                    if isBit(newNw["IS_PRIMARY"]):
                        primary_index = newNw["NETWORK_INDEX"]

                    #IPMODE
                    if newNw["IP_MODE"] != item.find(fixxpath(item, 'IpAddressAllocationMode')).text:
                        item.find(fixxpath(item, 'IpAddressAllocationMode')).text = newNw["IP_MODE"]
                        isEdit = True
                        if newNw["IP_MODE"] == "MANUAL":
                            #設定「MANUAL」の場合はアドレスも変更する
                            item.find(fixxpath(item, 'IpAddress')).text = newNw["IP_ADDRESS"]
                    else:
                        if newNw["IP_ADDRESS"] != item.find(fixxpath(item, 'IpAddress')).text:
                            #IPアドレスのみ変更する場合
                            item.find(fixxpath(item, 'IpAddress')).text = newNw["IP_ADDRESS"]
                            isEdit = True

            #邪魔なのでLinkタグを消す
            link = res.object.find(fixxpath(res.object, 'Link'))
            res.object.remove(link)

            #追加ネットワークの登録
            if len(makemap) > 0:
                for key in makemap:
                    newNw = makemap[key]
                    networkConnection = ET.Element('%sNetworkConnection' % rasd_ns,
                                              {'network': newNw["NETWORK_NAME"]}
                                            )
                    ET.SubElement(networkConnection, '%sNetworkConnectionIndex' % rasd_ns).text = str(indexList[0])

                    #Primary チェック
                    if isBit(newNw["IS_PRIMARY"]):
                        primary_index = indexList[0]

                    if isNotEmpty(newNw["IP_ADDRESS"]):
                        ET.SubElement(networkConnection, '%sIpAddress' % rasd_ns).text = newNw["IP_ADDRESS"]
                    ET.SubElement(networkConnection, '%sIsConnected' % rasd_ns).text = "true"
                    ET.SubElement(networkConnection, '%sIpAddressAllocationMode' % rasd_ns).text = newNw["IP_MODE"]

                    res.object.append(networkConnection)
                    isEdit  = True
                    indexList.remove(indexList[0])


            if str(def_primary_index) != str(primary_index):
                #プライマリの再設定
                res.object.find(fixxpath(res.object, 'PrimaryNetworkConnectionIndex')).text = str(primary_index)
                isEdit  = True

            #取っ払ったLinkを戻す
            res.object.append(link)
            self.logger.info(ET.tostring(res.object))
            #変更を行った場合のみ通信する
            if isEdit:
                self.logger.info(ET.tostring(res.object))
                res = self.requestLoop(
                    '%s/networkConnectionSection' % get_url_path(vm.get('href')),
                    data=ET.tostring(res.object),
                    method='PUT',
                    headers={'Content-Type': 'application/vnd.vmware.vcloud.networkConnectionSection+xml'}
                )
                self._wait_for_task_completion(res.object.get('href'))

############################################################
#
#    KeyPair 系
#
############################################################

    #制御項目なし

############################################################
#
#    Snapshot 系
#
############################################################

    #制御項目なし

############################################################
#
#    Tags 系
#
############################################################

    #制御項目なし

############################################################
#
#    その他
#
############################################################

    #########################
    #
    # Windows用　パスワード取得
    #
    #########################
    def getPasswordData(self, instanceId):
        pass

    #########################
    #
    # ユーザーデータ設定
    #
    #########################
    def setProductSections(self, vm, metadatas):
        add_xml = SetProductSectionListXML(
            metadatas=metadatas
        )

        self.logger.info(add_xml.tostring())

        try:
            # Instantiate VM and get identifier.
            res = self.requestLoop(
                '%s/productSections' % get_url_path(vm["id"]),
                data=add_xml.tostring(),
                method='PUT',
                headers={'Content-Type': 'application/vnd.vmware.vcloud.productSections+xml'}
            )
            task_href = res.object.get('href')
            self._wait_for_task_completion(task_href)

            #実行ログ
            self.logger.info(None, "IPROCESS-100709", [vm["name"],])

        except Exception:
            self.logger.error(traceback.format_exc())
            raise IaasException("EPROCESS-000809", [])


############################################################
#
#    LoadBalancer 操作系
#
############################################################

    #制御項目なし

############################################################
#
#    LibCloud オーバーライド
#
############################################################


    #########################
    #
    # リクエストの失敗をRETRY_MAXまでリトライする
    #
    #########################
    def requestLoop(self, *args, **kwargs):
        retry = 0
        status = "not"
        while status != 'go':
            try:
                res = self.connection.request(*args, **kwargs)
                return res
            except Exception:
                if retry > self.RETRY_MAX:
                    raise
                else:
                    retry = retry +1
                    time.sleep(5)


    #########################
    #
    # 説明
    # タスクタイムアウトを設定値に合せて変更出来るように修正
    # NOT オーバーライド
    #
    #
    #########################
    def _wait_for_task_completion(self, task_href):
        #VCloud_1_5_NodeDriver._wait_for_task_completion(self, task_href, timeout = self.timeout)

        start_time = time.time()
        res = self.connection.request(get_url_path(task_href))
        status = res.object.get('status')
        retry = 0
        while status != 'success':
            if status == 'error':
                if retry > self.RETRY_MAX:
                    # Get error reason from the response body
                    error_elem = res.object.find(fixxpath(res.object, 'Error'))
                    error_msg = "Unknown error"
                    if error_elem is not None:
                        error_msg = error_elem.get('message')
                    raise Exception("Error status returned by task %s.: %s" % (task_href, error_msg))
                else:
                    retry = retry +1

            if status == 'canceled':
                raise Exception("Canceled status returned by task %s." % task_href)

            if (time.time() - start_time >= self.timeout):
                raise Exception("Timeout (%s sec) while waiting for task %s." % (self.timeout, task_href))

            time.sleep(5)
            res = self.connection.request(get_url_path(task_href))
            status = res.object.get('status')


    #########################
    #
    # 説明
    # IPアドレスのパブリック、プライベートの判断基準を追加
    # ストレージプロファイルを保持するよう修正
    #
    #
    #########################
    def _to_node(self, node_elm):

        publicNetworkName = getOtherProperty("vCloud.PCCNetwork")

        # Parse VMs as extra field
        vms = []
        for vm_elem in node_elm.findall(fixxpath(node_elm, 'Children/Vm')):
            public_ips = []
            private_ips = []
            other_ips = []

            for connection in vm_elem.findall(fixxpath(vm_elem, 'NetworkConnectionSection/NetworkConnection')):
                if connection.attrib['network'] != "none":
                    ip = connection.find(fixxpath(connection, "IpAddress"))
                    if connection.attrib['network'] == publicNetworkName:
                        public_ips.append(ip.text)
                    else:
                        if connection.attrib['network'] == self.defnet:
                            private_ips.append(ip.text)
                        else:
                            other_ips.append(ip.text)

            #デフォルトネットワークが無い（private_ipが無い）場合
            #その他の1つ目をprivate_ipへ設定
            if len(private_ips) == 0 and len(other_ips) > 0:
                private_ips.append(other_ips[0])

########################################
#プライベートIPの設定に関し別案
########################################
#            primary_ips = []
#            def_ips = []
#            primary_index = vm_elem.object.find(fixxpath(vm_elem, 'NetworkConnectionSection/PrimaryNetworkConnectionIndex')).text
#            for connection in vm_elem.findall(fixxpath(vm_elem, 'NetworkConnectionSection/NetworkConnection')):
#                if connection.attrib['network'] != "none":
#                    ip = connection.find(fixxpath(connection, "IpAddress"))
#                    index = connection.find(fixxpath(connection, "NetworkConnectionIndex"))
#                    if connection.attrib['network'] == publicNetworkName:
#                        public_ips.append(ip.text)
#                    else:
#                        if index == primary_index:
#                            primary_ips.append(ip.text)
#
#                        if connection.attrib['network'] == self.defnet:
#                            def_ips.append(ip.text)
#                        else:
#                            other_ips.append(ip.text)
#
#            #プライベートUPの設定順
#            #   1:プライマリネットワーク（PCCネットワークの場合は除外）
#            #   2:デフォルトネットワーク
#            #   3:その他ネットワーク
#            if len(primary_ips) != 0:
#                private_ips.append(primary_ips[0])
#            else:
#                if len(def_ips) != 0:
#                    private_ips.append(other_ips[0])
#                elif len(other_ips) > 0:
#                    private_ips.append(other_ips[0])

            #VMへ設定
            vm = {
                'id': vm_elem.get('href'),
                'name': vm_elem.get('name'),
                'state': self.NODE_STATE_MAP[vm_elem.get('status')],
                'public_ips': public_ips,
                'private_ips': private_ips,
                'storageprofile':vm_elem.find(fixxpath(vm_elem, 'StorageProfile')).get('name'),
            }
            vms.append(vm)

        # Take the node IP addresses from all VMs
        public_ips = []
        private_ips = []
        for vm in vms:
            public_ips.extend(vm['public_ips'])
            private_ips.extend(vm['private_ips'])

        # Find vDC
        vdc_id = next(link.get('href') for link in node_elm.findall(fixxpath(node_elm, 'Link'))
            if link.get('type') == 'application/vnd.vmware.vcloud.vdc+xml')
        vdc = next(vdc for vdc in self.vdcs if vdc.id == vdc_id)

        # Find TASK
        tasks = node_elm.findall(fixxpath(node_elm, 'Tasks/Task'))
        isTask = False
        if len(tasks) > 0:
            isTask = True

        node = Node(id=node_elm.get('href'),
                    name=node_elm.get('name'),
                    state=self.NODE_STATE_MAP[node_elm.get('status')],
                    public_ips=public_ips,
                    private_ips=private_ips,
                    driver=self.connection.driver,
                    extra={'vdc': vdc.name, 'vms': vms, 'task': isTask})
        return node

