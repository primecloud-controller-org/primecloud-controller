#coding: UTF-8
'''
Created on 2012/03/22


'''
from iaasgw.log.log import IaasLogger
import os
import re

class PropertiesReader(dict):
    logger = IaasLogger()
    prop = re.compile(r"([\w. ]+)\s*=\s*(.*)")
    src = os.environ.get('PCC_CONFIG_HOME', "") + "/config.properties"

    def __init__(self):
        self.loadConfig()

    def loadFileData(self):
        data = ""
        try:
            file = open(self.src, 'r')
            for line in file:
                if not line.startswith("#"):
                    data = data + line
            file.close()

        except IOError:
            pass

        return data

    def loadConfig(self):
        for (key, val) in self.prop.findall(self.loadFileData()):
            self[key.strip()] = val.rstrip()
