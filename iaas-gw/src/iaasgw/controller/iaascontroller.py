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

class IaasController(object):

    def startInstance(self, instanceNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('startInstance not implemented for this Controller')
    def stopInstance(self, instanceNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('stopInstance not implemented for this Controller')
    def terminateInstance(self, instanceNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('terminateInstance not implemented for this Controller')
    def startVolume(self, instanceNo, volumeNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('startVolume not implemented for this Controller')
    def stopVolume(self, instanceNo, volumeNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('stopVolume not implemented for this Controller')
    def deleteVolume(self, volumeId):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('deleteVolume not implemented for this Controller')
    def startLoadBalancer(self, loadBalancerNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('startLoadBalancer not implemented for this Controller')
    def stopLoadBalancer(self, loadBalancerNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('stopLoadBalancer not implemented for this Controller')
    def configureLoadBalancer(self, loadBalancerNo):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('configureLoadBalancer not implemented for this Controller')
    def allocateAddress(self):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('allocateAddress not implemented for this Controller')
    def releaseAddress(self, publicIp):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('releaseAddress not implemented for this Controller')
    def createSnapshot(self, volumeId):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('createSnapshot not implemented for this Controller')
    def deleteSnapshot(self, snapshotId):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('deleteSnapshot not implemented for this Controller')
    def getPasswordData(self, instanceId):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('getPasswordData not implemented for this Controller')
    def describeKeyPairs(self):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('describeKeyPairs not implemented for this Controller')
    def createKeyPair(self, keyName):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('createKeyPair not implemented for this Controller')
    def deleteKeyPair(self, keyName):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('deleteKeyPair not implemented for this Controller')
    def importKeyPair(self, keyName, publicKeyMaterial):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('importKeyPair not implemented for this Controller')
    def describeSecurityGroups(self, vpcid):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('describeSecurityGroups not implemented for this Controller')
    def describeAvailabilityZones(self):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('describeAvailabilityZones not implemented for this Controller')
    def describeSubnets(self, vpcid):
        '''オーバーライドされなかったとき
        '''
        raise NotImplementedError('describeSubnets not implemented for this Controller')
