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
import ConfigParser
import os

root = os.environ.get('IAASGW_HOME', "")
inifile_sys = ConfigParser.SafeConfigParser()
inifile_sys.read(root + "/iaassystem.ini")

inifile_mess = ConfigParser.SafeConfigParser()
inifile_mess.read(root + "/message.ini")

def getDeviceProperty(hypervisor):
    return inifile_sys.items("DEVICE_MAP_"+str(hypervisor))   #"internal"

def getAzureDeviceProperty(imageOS):
    return inifile_sys.items("AZURE_DEVICE_MAP_"+str(imageOS))

def readIni(section, name):
    return inifile_sys.get(section, name)


def readMess(section, name):
    return inifile_mess.get(section, name)
