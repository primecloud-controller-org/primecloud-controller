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

from iaasgw.utils.stringUtils import isNotEmpty
from libcloud.compute.drivers.vcloud import fixxpath
from xml.etree import ElementTree as ET

class RecomposeVAppXML_ADD_VM(object):
    def __init__(self, name, image, vm_name, vm_storage, vm_networks, vm_fqdn):
        self.name = name
        self.image = image
        self.vm_name = vm_name
        self.vm_storage = vm_storage
        self.vm_networks = vm_networks
        self.vm_fqdn = vm_fqdn

        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()
        #追加VM情報
        self._add_vm_item(self.root)

    def _make_instantiation_root(self):
        return ET.Element(
            "RecomposeVAppParams",
            {'name': self.name,
             'deploy': 'false',
             'powerOn': 'false',
             'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )

    def _add_vm_item(self, parent):
        sourcedItem = ET.SubElement(parent,
                                    "SourcedItem",
                                    {'sourceDelete':'false'}
        )
        ET.SubElement(
            sourcedItem,
            "Source",
            {'href': self.image,
             'name': self.vm_name}
        )

        instantionation_params = ET.SubElement(sourcedItem, "InstantiationParams")
        network_connection_section = ET.SubElement(instantionation_params,
                                                   "NetworkConnectionSection",
                                                   {'xmlns': "http://www.vmware.com/vcloud/v1.5",
                                                    'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )
        ET.SubElement(
            network_connection_section,
            "Info",
            {'xmlns': "http://schemas.dmtf.org/ovf/envelope/1"}
        )

        #散りあえずプライマリーは０
        primary_xml =  ET.SubElement(network_connection_section, 'PrimaryNetworkConnectionIndex')
        primary_xml.text = "0"

        netIndex = 0
        p_index = 0
        for vm_network in self.vm_networks:
            networkConnection = ET.SubElement(network_connection_section,
                                              'NetworkConnection',
                                              {'network': vm_network.name}
            )
            ET.SubElement(networkConnection, 'NetworkConnectionIndex').text = str(netIndex)
            if isNotEmpty(vm_network.ipAddress):
                ET.SubElement(networkConnection, 'IpAddress').text = vm_network.ipAddress
            ET.SubElement(networkConnection, 'IsConnected').text = "true"
            ET.SubElement(networkConnection, 'IpAddressAllocationMode').text = vm_network.ipMode
            #プライマリかどうか
            if vm_network.isPrimary:
                p_index = netIndex

            netIndex = netIndex + 1

        #プライマリを指定のものへ変更
        primary_xml.text = str(p_index)

        guest_customization_section = ET.SubElement(instantionation_params,
                                                   "GuestCustomizationSection",
                                                   {'xmlns': "http://www.vmware.com/vcloud/v1.5",
                                                    'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )
        ET.SubElement(
            guest_customization_section,
            "Info",
            {'xmlns': "http://schemas.dmtf.org/ovf/envelope/1"}
        )
        ET.SubElement(guest_customization_section, 'Enabled').text = "true"
        ET.SubElement(guest_customization_section, 'ComputerName').text = self.vm_name

        ET.SubElement(
            sourcedItem,
            "StorageProfile",
            {'href': self.vm_storage.href,
             'name': self.vm_storage.name}
        )


class RecomposeVAppXML_DEL_VM(object):
    def __init__(self, name, vm_harf):
        self.name = name
        self.vm_harf = vm_harf

        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()
        #追加VM情報
        self._del_vm_item(self.root)

    def _make_instantiation_root(self):
        return ET.Element(
            "RecomposeVAppParams",
            {'name': self.name,
             'deploy': 'false',
             'powerOn': 'false',
             'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )

    def _del_vm_item(self, parent):
        ET.SubElement(parent, "DeleteItem", {'href':self.vm_harf})


class InstantiateVAppXML(object):
    def __init__(self, name, template, networks, fence=None):
        self.name = name
        self.template = template
        self.networks = networks
        if fence is None:
            self.fence = "bridged"
        else:
            self.fence = fence
        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()

        if self.networks:
            instantionation_params = ET.SubElement(self.root, "InstantiationParams")
            network_config_section = ET.SubElement(instantionation_params, "NetworkConfigSection")
            ET.SubElement(
                network_config_section,
                "Info",
                {'xmlns': "http://schemas.dmtf.org/ovf/envelope/1"}
            )

            for net in self.networks:
                network_config = ET.SubElement(network_config_section, "NetworkConfig")
                self._add_network_association(network_config, net)

        self._add_vapp_template(self.root)

    def _make_instantiation_root(self):
        return ET.Element(
            "InstantiateVAppTemplateParams",
            {'name': self.name,
             'deploy': 'false',
             'powerOn': 'false',
             'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )

    def _add_vapp_template(self, parent):
        return ET.SubElement(
            parent,
            "Source",
            {'href': self.template}
        )

    def _add_network_association(self, parent, network):
        # Don't set a custom vApp VM network name
        parent.set('networkName', network.get('name'))
        configuration = ET.SubElement(parent, 'Configuration')
        ET.SubElement(configuration, 'ParentNetwork', {'href': network.get('href')})
        ET.SubElement(configuration, 'FenceMode').text = self.fence

class ComposeVAppXML(object):
    def __init__(self, name, useNetworks):
        self.name = name
        self.networks = useNetworks

        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()

        if self.networks:
            instantionation_params = ET.SubElement(self.root, "InstantiationParams")
            network_config_section = ET.SubElement(instantionation_params, "NetworkConfigSection")
            ET.SubElement(
                network_config_section,
                "Info",
                {'xmlns': "http://schemas.dmtf.org/ovf/envelope/1"}
            )

            for net in self.networks:
                network_config = ET.SubElement(network_config_section, "NetworkConfig")
                self._add_network_association(network_config, net)

    def _make_instantiation_root(self):
        return ET.Element(
            "ComposeVAppParams",
            {'name': self.name,
             'deploy': 'false',
             'powerOn': 'false',
             'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )

    def _add_network_association(self, parent, network):
        # Don't set a custom vApp VM network name
        parent.set('networkName', network.name)
        configuration = ET.SubElement(parent, 'Configuration')
        ET.SubElement(configuration, 'ParentNetwork', {'href': network.href})
        #フェンスモードはブリッジ固定
        ET.SubElement(configuration, 'FenceMode').text = 'bridged'



class SetMetadataXML(object):
    def __init__(self, metadatas):
        self.metadatas = metadatas

        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()

        for metadata in self.metadatas:
            entry = ET.SubElement(self.root, "MetadataEntry")
            ET.SubElement(entry, 'Key').text = metadata.key
            ET.SubElement(entry, 'Value').text = metadata.value

    def _make_instantiation_root(self):
        return ET.Element(
            "Metadata ",
            {'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance"}
        )



class SetProductSectionListXML(object):
    #rasd_ns = '{http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData}'
    rasd_ns = 'ovf:'
    def __init__(self, metadatas):
        self.metadatas = metadatas

        self._build_xmltree()

    def tostring(self):
        return ET.tostring(self.root)

    def _build_xmltree(self):
        self.root = self._make_instantiation_root()
        entry = ET.SubElement(self.root, "ovf:ProductSection", {"required":"true"})
        for metadata in self.metadatas:
            ET.SubElement(entry, "ovf:Info").text='SET PCC USER DATA'
            property = ET.SubElement(entry, "ovf:Property",{
             'ovf:type': 'string',
             'ovf:key': metadata.key,
             'ovf:value': metadata.value,
             'ovf:userConfigurable':'true'})
            ET.SubElement(property, "ovf:Label").text=metadata.key

    def _make_instantiation_root(self):
        return ET.Element(
            "ProductSectionList",
            {'xml:lang': 'en',
             'xmlns': "http://www.vmware.com/vcloud/v1.5",
             'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
             'xmlns:ovf': "http://schemas.dmtf.org/ovf/envelope/1"}
        )



