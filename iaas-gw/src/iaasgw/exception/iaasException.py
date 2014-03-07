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
from iaasgw.utils.massageUtil import getMassage

class IaasException(Exception):
    def __init__(self, massageid = None, *args):
        self.massageid = massageid
        self.param = args
        self.massage = getMassage(massageid, *args)

    def __str__(self):
        if self.massageid:
            return repr(self.massage)
        else:
            return repr("NON MASSAGE!")

    def getMassageid(self):
        return self.massageid

    def getMassageParam(self):
        return self.param
