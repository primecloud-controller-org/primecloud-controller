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
