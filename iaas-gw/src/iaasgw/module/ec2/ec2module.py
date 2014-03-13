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

class TestModule(object):
    def __init__(self, id, state, public_ip, private_ip, extra):
        self.id = id
        self.state = state
        self.public_ip = public_ip
        self.private_ip = private_ip
        self.extra = extra


class TagSet(object):
    def __init__(self, resourceId, resourceType  , key , value):
        self.resourceId = resourceId
        self.resourceType = resourceType
        self.key = key
        self.value = value

    def __repr__(self):
        return self.resourceId

class InstanceState(object):
    def __init__(self, description, instanceId  , reasonCode , state):
        self.description = description
        self.instanceId = instanceId
        self.reasonCode = reasonCode
        self.state = state

    def __repr__(self):
        return self.instanceId

class LoadBalancerDescription(object):
    def __init__(self, availabilityZones, backendServerDescriptions , canonicalHostedZoneName, canonicalHostedZoneNameID, \
                 createdTime, dnsName, healthCheck, instances, listenerDescriptions, loadBalancerName, policies, sourceSecurityGroup ):
        self.availabilityZones = availabilityZones
        self.backendServerDescriptions = backendServerDescriptions
        self.canonicalHostedZoneName = canonicalHostedZoneName
        self.canonicalHostedZoneNameID = canonicalHostedZoneNameID
        self.createdTime = createdTime
        self.dnsName = dnsName
        self.healthCheck = healthCheck
        self.instances = instances
        self.listenerDescriptions = listenerDescriptions
        self.loadBalancerName = loadBalancerName
        self.policies = policies
        self.sourceSecurityGroup = sourceSecurityGroup

    def __repr__(self):
        return self.loadBalancerName

class BackendServerDescription(object):
    def __init__(self, instancePort, policyNames):
        self.instancePort = instancePort
        self.policyNames = policyNames

    def __repr__(self):
        return self.instancePort + ':' + self.policyNames


class HealthCheck(object):
    def __init__(self, healthyThreshold, interval, target, timeout, unhealthyThreshold ):
        self.healthyThreshold = healthyThreshold
        self.interval = interval
        self.target = target
        self.timeout = timeout
        self.unhealthyThreshold = unhealthyThreshold


    def __repr__(self):
        return self.healthyThreshold


class ListenerDescription(object):
    def __init__(self, listener , policyNames):
        self.listener  = listener
        self.policyNames  = policyNames


    def __repr__(self):
        return self.policyNames


class Listener(object):
    def __init__(self, instancePort, instanceProtocol, loadBalancerPort, protocol, sslCertificateId = None):
        self.instancePort  = instancePort
        self.instanceProtocol  = instanceProtocol
        self.loadBalancerPort  = loadBalancerPort
        self.protocol  = protocol
        self.sslCertificateId  = sslCertificateId

    def __repr__(self):
        return self.instancePort

    def setSSLCertificateId(self, sslCertificateId):
        self.sslCertificateId  = sslCertificateId


class Policies(object):
    def __init__(self, appCookieStickinessPolicies , lbCookieStickinessPolicies , otherPolicies):
        self.appCookieStickinessPolicies  = appCookieStickinessPolicies
        self.lbCookieStickinessPolicies  = lbCookieStickinessPolicies
        self.otherPolicies  = otherPolicies

    def __repr__(self):
        return self.otherPolicies


class AppCookieStickinessPolicy(object):
    def __init__(self, cookieName, policyName):
        self.cookieName  = cookieName
        self.policyName  = policyName

    def __repr__(self):
        return self.cookieName + ':' +self.policyName


class LBCookieStickinessPolicy(object):
    def __init__(self, cookieExpirationPeriod, policyName ):
        self.cookieExpirationPeriod  = cookieExpirationPeriod
        self.policyName  = policyName

    def __repr__(self):
        return self.cookieExpirationPeriod + ':' +self.policyName

class SourceSecurityGroup(object):
    def __init__(self, groupName, ownerAlias):
        self.groupName  = groupName
        self.ownerAlias  = ownerAlias

    def __repr__(self):
        return self.groupName + ':' +self.ownerAlias


class SubnetInfo(object):
    def __init__(self, subnetId, state, vpcId, cidrBlock, availableIpAddressCount, availabilityZone, tagSet):
        self.subnetId = subnetId
        self.state = state
        self.vpcId = vpcId
        self.cidrBlock = cidrBlock
        self.availableIpAddressCount = availableIpAddressCount
        self.availabilityZone = availabilityZone
        self.tagSet = tagSet

    def __repr__(self):
        return self.subnetId

class VpcInfo(object):
    def __init__(self, vpcId, state, cidrBlock, dhcpOptionsId, tagSet):
        self.vpcId = vpcId
        self.state = state
        self.cidrBlock = cidrBlock
        self.dhcpOptionsId = dhcpOptionsId
        self.tagSet = tagSet

    def __repr__(self):
        return self.vpcId

class RegionInfo(object):
    def __init__(self, regionName, regionEndpoint):
        self.regionName = regionName
        self.regionEndpoint = regionEndpoint

    def __repr__(self):
        return self.regionName + ':' + self.regionEndpoint


class SnapshotSet(object):
    def __init__(self, snapshotId, volumeId, status, startTime, progress, ownerId, volumeSize, description, tagSet):
        self.snapshotId = snapshotId
        self.volumeId = volumeId
        self.status = status
        self.startTime = startTime
        self.progress = progress
        self.ownerId = ownerId
        self.volumeSize = volumeSize
        self.description = description
        self.tagSet = tagSet

    def __repr__(self):
        return self.snapshotId + ':' + self.description

class SecurityGroup(object):
    def __init__(self, ownerId, groupId, groupName, groupDescription, vpcId, ipPermissions, ipPermissionsEgress, tagSet):
        self.ownerId = ownerId
        self.groupId = groupId
        self.groupName = groupName
        self.groupDescription = groupDescription
        self.vpcId = vpcId
        self.ipPermissions = ipPermissions
        self.ipPermissionsEgress = ipPermissionsEgress
        self.tagSet = tagSet

    def __repr__(self):
        return self.groupId + ':' + self.groupName

class IpPermissions(object):
    def __init__(self, ipProtocol, fromPort, toPort, groups, ipRanges):
        self.ipProtocol = ipProtocol
        self.fromPort = fromPort
        self.toPort = toPort
        self.groups = groups
        self.ipRanges = ipRanges

    def __repr__(self):
        return self.ipProtocol + ': FROM@' + self.fromPort + '  TO@'+self.toPort


class KeyPair(object):
    def __init__(self, keyName, keyFingerprint):
        self.keyName = keyName
        self.keyFingerprint = keyFingerprint
        self.keyMaterial = None

    def __repr__(self):
        return self.keyName + ':' + self.keyFingerprint

    #場合によって使わない為外出ししておく
    def setKeyMaterial(self, keyMaterial):
        self.keyMaterial = keyMaterial

class Address(object):
    def __init__(self, publicIp, domain, instanceId):
        self.publicIp = publicIp
        self.domain = domain
        self.instanceId = instanceId

    def __repr__(self):
        return 'Address:%s' % self.publicIp


class Volume(object):
    def __init__(self, volumeId, size, snapshotId, availabilityZone,
                 status, createTime, attachmentSet, tagSet):
        self.volumeId = volumeId
        self.size = size
        self.snapshotId = snapshotId
        self.availabilityZone = availabilityZone
        self.status = status
        self.createTime = createTime
        self.attachmentSet = attachmentSet or {}
        self.tagSet = tagSet or {}
        #たぶんいらない self.driver = driver

    def __repr__(self):
        return 'Volume:%s' % self.volumeId

class AttachmentSet(object):

    def __init__(self, volumeId, instanceId, device, status, attachTime):
        self.volumeId = volumeId
        self.instanceId = instanceId
        self.device = device
        self.status = status
        self.attachTime = attachTime
        self.deleteOnTermination = False

    def __repr__(self):
        return 'AttachmentSet:%s' % self.volumeId

    #場合によって使わない為外出ししておく
    def setDeleteOnTermination(self, deleteOnTermination):
        self.deleteOnTermination = deleteOnTermination


