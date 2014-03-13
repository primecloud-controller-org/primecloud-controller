#coding: UTF-8
'''
Created on 2012/03/16


'''
import ConfigParser
import os

root = os.environ.get('IAASGW_HOME', "")
inifile_sys = ConfigParser.SafeConfigParser()
inifile_sys.read(root + "/iaassystem.ini")

inifile_mess = ConfigParser.SafeConfigParser()
inifile_mess.read(root + "/message.ini")

def getDeviceProperty(hypervisor):
    return inifile_sys.items("DEVICE_MAP_"+str(hypervisor))   #"internal"

def readIni(section, name):
    return inifile_sys.get(section, name)


def readMess(section, name):
    return inifile_mess.get(section, name)
