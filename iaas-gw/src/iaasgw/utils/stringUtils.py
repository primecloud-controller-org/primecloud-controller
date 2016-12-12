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

from xmlrpclib import datetime
import time


def isEmpty(instr, trim=True):
    if not instr:
        return True

    if not isinstance(instr, str):
        instr = str(instr)

    if trim:
        if len(instr.strip()) == 0:
            return True

    return False

def isNotEmpty(instr, trim=True):
    if instr != None:
        if not isinstance(instr, str):
            instr = str(instr)

        if trim:
            if len(instr.strip()) > 0:
                return True
        else:
            if len(instr) > 0:
                return True

    return False


def isBit(inbit):
    if not inbit:
        return False

    if inbit == 0:
        return False
    else:
        return True

def startsWithIgnoreCase(str, prefix):
    if str.startswith(prefix):
        return True

    return False


def getCurrentTimeMillis():
    return int(time.mktime(datetime.datetime.today().timetuple()))
