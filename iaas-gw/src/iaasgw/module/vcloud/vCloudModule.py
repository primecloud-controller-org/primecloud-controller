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

class PccVMNetwork(object):
    def __init__(self, name, ipAddress, ipMode, index, isPrimary):
        self.name = name
        self.ipAddress = ipAddress
        self.ipMode = ipMode
        self.index = index
        self.isPrimary = isPrimary

    def toString(self):
        str = "name="+self.name
        str = str + ", ipAddress="+self.ipAddress
        str = str + ", ipMode="+self.ipMode
        str = str + ", index="+self.index
        return str

class PccVMDisk(object):
    def __init__(self, name, size, busType, unitNo):
        self.name = name
        self.size = size
        self.busType = busType
        self.unitNo = unitNo

    def toString(self):
        str = "name="+self.name
        str = str + ", size="+self.size
        str = str + ", busType="+self.busType
        str = str + ", unitNo="+self.unitNo
        return str

class VCloudMetadataSet(object):
    def __init__(self, key, value):
        self.key = key
        self.value = value

    def toString(self):
        str = "key="+self.key
        str = str + ", value="+self.value
        return str


class PccStorageProfile(object):
    def __init__(self, name, href):
        self.name = name
        self.href = href

    def toString(self):
        str = "name="+self.name
        str = str + ", href="+self.href
        return str

class PccVAppNetwork(object):
    def __init__(self, name, href, fencemode, gateway, netmask, dns1, dns2, rangeF, rangeT, primary):
        self.name = name
        self.href = href
        self.fencemode = fencemode
        self.gateway = gateway
        self.netmask = netmask
        self.dns1 = dns1
        self.dns2 = dns2
        self.rangeF = rangeF
        self.rangeT = rangeT
        self.primary = primary

    def toString(self):
        str = "name="+self.name
        str = str + ", href="+self.href
        str = str + ", fencemode="+self.fencemode
        str = str + ", gateway="+self.gateway
        str = str + ", netmask="+self.netmask
        str = str + ", dns1="+self.dns1
        str = str + ", dns2="+self.dns2
        str = str + ", rangeF="+self.rangeF
        str = str + ", rangeT="+self.rangeT
        str = str + ", primary="+str(self.primary)
        return str
