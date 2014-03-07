
package jp.primecloud.auto.nifty.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * <p>Java class for RunningInstancesItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RunningInstancesItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instanceState" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceStateType"/>
 *         &lt;element name="privateDnsName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dnsName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="keyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amiLaunchIndex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productCodes" type="{https://cp.cloud.nifty.com/api/1.3/}ProductCodesSetType" minOccurs="0"/>
 *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="launchTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="placement" type="{https://cp.cloud.nifty.com/api/1.3/}PlacementResponseType" minOccurs="0"/>
 *         &lt;element name="kernelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ramdiskId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="platform" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="monitoring" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceMonitoringStateType" minOccurs="0"/>
 *         &lt;element name="subnetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vpcId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="privateIpAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stateReason" type="{https://cp.cloud.nifty.com/api/1.3/}StateReasonType" minOccurs="0"/>
 *         &lt;element name="architecture" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rootDeviceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rootDeviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="blockDeviceMapping" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceBlockDeviceMappingResponseType" minOccurs="0"/>
 *         &lt;element name="instanceLifecycle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spotInstanceRequestId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RunningInstancesItemType", propOrder = {
    "instanceId",
    "imageId",
    "instanceState",
    "privateDnsName",
    "dnsName",
    "reason",
    "keyName",
    "amiLaunchIndex",
    "productCodes",
    "instanceType",
    "launchTime",
    "placement",
    "kernelId",
    "ramdiskId",
    "platform",
    "monitoring",
    "subnetId",
    "vpcId",
    "privateIpAddress",
    "ipAddress",
    "stateReason",
    "architecture",
    "rootDeviceType",
    "rootDeviceName",
    "blockDeviceMapping",
    "instanceLifecycle",
    "spotInstanceRequestId"
})
public class RunningInstancesItemType {

    @XmlElement(required = true)
    protected String instanceId;
    protected String imageId;
    @XmlElement(required = true)
    protected InstanceStateType instanceState;
    @XmlElement(required = true)
    protected String privateDnsName;
    protected String dnsName;
    protected String reason;
    protected String keyName;
    protected String amiLaunchIndex;
    protected ProductCodesSetType productCodes;
    @XmlElement(required = true)
    protected String instanceType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar launchTime;
    protected PlacementResponseType placement;
    protected String kernelId;
    protected String ramdiskId;
    protected String platform;
    protected InstanceMonitoringStateType monitoring;
    protected String subnetId;
    protected String vpcId;
    protected String privateIpAddress;
    protected String ipAddress;
    protected StateReasonType stateReason;
    protected String architecture;
    protected String rootDeviceType;
    protected String rootDeviceName;
    protected InstanceBlockDeviceMappingResponseType blockDeviceMapping;
    protected String instanceLifecycle;
    protected String spotInstanceRequestId;

    /**
     * Gets the value of the instanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the value of the instanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceId(String value) {
        this.instanceId = value;
    }

    /**
     * Gets the value of the imageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Sets the value of the imageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageId(String value) {
        this.imageId = value;
    }

    /**
     * Gets the value of the instanceState property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceStateType }
     *     
     */
    public InstanceStateType getInstanceState() {
        return instanceState;
    }

    /**
     * Sets the value of the instanceState property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceStateType }
     *     
     */
    public void setInstanceState(InstanceStateType value) {
        this.instanceState = value;
    }

    /**
     * Gets the value of the privateDnsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivateDnsName() {
        return privateDnsName;
    }

    /**
     * Sets the value of the privateDnsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivateDnsName(String value) {
        this.privateDnsName = value;
    }

    /**
     * Gets the value of the dnsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnsName() {
        return dnsName;
    }

    /**
     * Sets the value of the dnsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnsName(String value) {
        this.dnsName = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the keyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Sets the value of the keyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyName(String value) {
        this.keyName = value;
    }

    /**
     * Gets the value of the amiLaunchIndex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmiLaunchIndex() {
        return amiLaunchIndex;
    }

    /**
     * Sets the value of the amiLaunchIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmiLaunchIndex(String value) {
        this.amiLaunchIndex = value;
    }

    /**
     * Gets the value of the productCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ProductCodesSetType }
     *     
     */
    public ProductCodesSetType getProductCodes() {
        return productCodes;
    }

    /**
     * Sets the value of the productCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductCodesSetType }
     *     
     */
    public void setProductCodes(ProductCodesSetType value) {
        this.productCodes = value;
    }

    /**
     * Gets the value of the instanceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceType() {
        return instanceType;
    }

    /**
     * Sets the value of the instanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceType(String value) {
        this.instanceType = value;
    }

    /**
     * Gets the value of the launchTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLaunchTime() {
        return launchTime;
    }

    /**
     * Sets the value of the launchTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLaunchTime(XMLGregorianCalendar value) {
        this.launchTime = value;
    }

    /**
     * Gets the value of the placement property.
     * 
     * @return
     *     possible object is
     *     {@link PlacementResponseType }
     *     
     */
    public PlacementResponseType getPlacement() {
        return placement;
    }

    /**
     * Sets the value of the placement property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlacementResponseType }
     *     
     */
    public void setPlacement(PlacementResponseType value) {
        this.placement = value;
    }

    /**
     * Gets the value of the kernelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKernelId() {
        return kernelId;
    }

    /**
     * Sets the value of the kernelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKernelId(String value) {
        this.kernelId = value;
    }

    /**
     * Gets the value of the ramdiskId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRamdiskId() {
        return ramdiskId;
    }

    /**
     * Sets the value of the ramdiskId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRamdiskId(String value) {
        this.ramdiskId = value;
    }

    /**
     * Gets the value of the platform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the value of the platform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlatform(String value) {
        this.platform = value;
    }

    /**
     * Gets the value of the monitoring property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceMonitoringStateType }
     *     
     */
    public InstanceMonitoringStateType getMonitoring() {
        return monitoring;
    }

    /**
     * Sets the value of the monitoring property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceMonitoringStateType }
     *     
     */
    public void setMonitoring(InstanceMonitoringStateType value) {
        this.monitoring = value;
    }

    /**
     * Gets the value of the subnetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetId() {
        return subnetId;
    }

    /**
     * Sets the value of the subnetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetId(String value) {
        this.subnetId = value;
    }

    /**
     * Gets the value of the vpcId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVpcId() {
        return vpcId;
    }

    /**
     * Sets the value of the vpcId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVpcId(String value) {
        this.vpcId = value;
    }

    /**
     * Gets the value of the privateIpAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    /**
     * Sets the value of the privateIpAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivateIpAddress(String value) {
        this.privateIpAddress = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the stateReason property.
     * 
     * @return
     *     possible object is
     *     {@link StateReasonType }
     *     
     */
    public StateReasonType getStateReason() {
        return stateReason;
    }

    /**
     * Sets the value of the stateReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateReasonType }
     *     
     */
    public void setStateReason(StateReasonType value) {
        this.stateReason = value;
    }

    /**
     * Gets the value of the architecture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the value of the architecture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArchitecture(String value) {
        this.architecture = value;
    }

    /**
     * Gets the value of the rootDeviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootDeviceType() {
        return rootDeviceType;
    }

    /**
     * Sets the value of the rootDeviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootDeviceType(String value) {
        this.rootDeviceType = value;
    }

    /**
     * Gets the value of the rootDeviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootDeviceName() {
        return rootDeviceName;
    }

    /**
     * Sets the value of the rootDeviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootDeviceName(String value) {
        this.rootDeviceName = value;
    }

    /**
     * Gets the value of the blockDeviceMapping property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceBlockDeviceMappingResponseType }
     *     
     */
    public InstanceBlockDeviceMappingResponseType getBlockDeviceMapping() {
        return blockDeviceMapping;
    }

    /**
     * Sets the value of the blockDeviceMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceBlockDeviceMappingResponseType }
     *     
     */
    public void setBlockDeviceMapping(InstanceBlockDeviceMappingResponseType value) {
        this.blockDeviceMapping = value;
    }

    /**
     * Gets the value of the instanceLifecycle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceLifecycle() {
        return instanceLifecycle;
    }

    /**
     * Sets the value of the instanceLifecycle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceLifecycle(String value) {
        this.instanceLifecycle = value;
    }

    /**
     * Gets the value of the spotInstanceRequestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpotInstanceRequestId() {
        return spotInstanceRequestId;
    }

    /**
     * Sets the value of the spotInstanceRequestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpotInstanceRequestId(String value) {
        this.spotInstanceRequestId = value;
    }

    public RunningInstancesItemType withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    public RunningInstancesItemType withImageId(String value) {
        setImageId(value);
        return this;
    }

    public RunningInstancesItemType withInstanceState(InstanceStateType value) {
        setInstanceState(value);
        return this;
    }

    public RunningInstancesItemType withPrivateDnsName(String value) {
        setPrivateDnsName(value);
        return this;
    }

    public RunningInstancesItemType withDnsName(String value) {
        setDnsName(value);
        return this;
    }

    public RunningInstancesItemType withReason(String value) {
        setReason(value);
        return this;
    }

    public RunningInstancesItemType withKeyName(String value) {
        setKeyName(value);
        return this;
    }

    public RunningInstancesItemType withAmiLaunchIndex(String value) {
        setAmiLaunchIndex(value);
        return this;
    }

    public RunningInstancesItemType withProductCodes(ProductCodesSetType value) {
        setProductCodes(value);
        return this;
    }

    public RunningInstancesItemType withInstanceType(String value) {
        setInstanceType(value);
        return this;
    }

    public RunningInstancesItemType withLaunchTime(XMLGregorianCalendar value) {
        setLaunchTime(value);
        return this;
    }

    public RunningInstancesItemType withPlacement(PlacementResponseType value) {
        setPlacement(value);
        return this;
    }

    public RunningInstancesItemType withKernelId(String value) {
        setKernelId(value);
        return this;
    }

    public RunningInstancesItemType withRamdiskId(String value) {
        setRamdiskId(value);
        return this;
    }

    public RunningInstancesItemType withPlatform(String value) {
        setPlatform(value);
        return this;
    }

    public RunningInstancesItemType withMonitoring(InstanceMonitoringStateType value) {
        setMonitoring(value);
        return this;
    }

    public RunningInstancesItemType withSubnetId(String value) {
        setSubnetId(value);
        return this;
    }

    public RunningInstancesItemType withVpcId(String value) {
        setVpcId(value);
        return this;
    }

    public RunningInstancesItemType withPrivateIpAddress(String value) {
        setPrivateIpAddress(value);
        return this;
    }

    public RunningInstancesItemType withIpAddress(String value) {
        setIpAddress(value);
        return this;
    }

    public RunningInstancesItemType withStateReason(StateReasonType value) {
        setStateReason(value);
        return this;
    }

    public RunningInstancesItemType withArchitecture(String value) {
        setArchitecture(value);
        return this;
    }

    public RunningInstancesItemType withRootDeviceType(String value) {
        setRootDeviceType(value);
        return this;
    }

    public RunningInstancesItemType withRootDeviceName(String value) {
        setRootDeviceName(value);
        return this;
    }

    public RunningInstancesItemType withBlockDeviceMapping(InstanceBlockDeviceMappingResponseType value) {
        setBlockDeviceMapping(value);
        return this;
    }

    public RunningInstancesItemType withInstanceLifecycle(String value) {
        setInstanceLifecycle(value);
        return this;
    }

    public RunningInstancesItemType withSpotInstanceRequestId(String value) {
        setSpotInstanceRequestId(value);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
