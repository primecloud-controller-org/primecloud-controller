
package jp.primecloud.auto.nifty.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * <p>Java class for RunInstancesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RunInstancesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="minCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="maxCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="keyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupSet" type="{https://cp.cloud.nifty.com/api/1.3/}GroupSetType"/>
 *         &lt;element name="additionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userData" type="{https://cp.cloud.nifty.com/api/1.3/}UserDataType" minOccurs="0"/>
 *         &lt;element name="addressingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="placement" type="{https://cp.cloud.nifty.com/api/1.3/}PlacementRequestType" minOccurs="0"/>
 *         &lt;element name="kernelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ramdiskId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="blockDeviceMapping" type="{https://cp.cloud.nifty.com/api/1.3/}BlockDeviceMappingType" minOccurs="0"/>
 *         &lt;element name="monitoring" type="{https://cp.cloud.nifty.com/api/1.3/}MonitoringInstanceType" minOccurs="0"/>
 *         &lt;element name="subnetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="disableApiTermination" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="instanceInitiatedShutdownBehavior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="admin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RunInstancesType", propOrder = {
    "imageId",
    "minCount",
    "maxCount",
    "keyName",
    "groupSet",
    "additionalInfo",
    "userData",
    "addressingType",
    "instanceType",
    "placement",
    "kernelId",
    "ramdiskId",
    "blockDeviceMapping",
    "monitoring",
    "subnetId",
    "disableApiTermination",
    "instanceInitiatedShutdownBehavior",
    "accountingType",
    "instanceId",
    "password",
    "admin"
})
public class RunInstancesType {

    @XmlElement(required = true)
    protected String imageId;
    protected int minCount;
    protected int maxCount;
    protected String keyName;
    @XmlElement(required = true)
    protected GroupSetType groupSet;
    protected String additionalInfo;
    protected UserDataType userData;
    protected String addressingType;
    @XmlElement(required = true)
    protected String instanceType;
    protected PlacementRequestType placement;
    protected String kernelId;
    protected String ramdiskId;
    protected BlockDeviceMappingType blockDeviceMapping;
    protected MonitoringInstanceType monitoring;
    protected String subnetId;
    protected Boolean disableApiTermination;
    protected String instanceInitiatedShutdownBehavior;
    protected String accountingType;
    protected String instanceId;
    @XmlElement(required = true)
    protected String password;
    protected String admin;

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
     * Gets the value of the minCount property.
     * 
     */
    public int getMinCount() {
        return minCount;
    }

    /**
     * Sets the value of the minCount property.
     * 
     */
    public void setMinCount(int value) {
        this.minCount = value;
    }

    /**
     * Gets the value of the maxCount property.
     * 
     */
    public int getMaxCount() {
        return maxCount;
    }

    /**
     * Sets the value of the maxCount property.
     * 
     */
    public void setMaxCount(int value) {
        this.maxCount = value;
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
     * Gets the value of the groupSet property.
     * 
     * @return
     *     possible object is
     *     {@link GroupSetType }
     *     
     */
    public GroupSetType getGroupSet() {
        return groupSet;
    }

    /**
     * Sets the value of the groupSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupSetType }
     *     
     */
    public void setGroupSet(GroupSetType value) {
        this.groupSet = value;
    }

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInfo(String value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the userData property.
     * 
     * @return
     *     possible object is
     *     {@link UserDataType }
     *     
     */
    public UserDataType getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserDataType }
     *     
     */
    public void setUserData(UserDataType value) {
        this.userData = value;
    }

    /**
     * Gets the value of the addressingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressingType() {
        return addressingType;
    }

    /**
     * Sets the value of the addressingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressingType(String value) {
        this.addressingType = value;
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
     * Gets the value of the placement property.
     * 
     * @return
     *     possible object is
     *     {@link PlacementRequestType }
     *     
     */
    public PlacementRequestType getPlacement() {
        return placement;
    }

    /**
     * Sets the value of the placement property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlacementRequestType }
     *     
     */
    public void setPlacement(PlacementRequestType value) {
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
     * Gets the value of the blockDeviceMapping property.
     * 
     * @return
     *     possible object is
     *     {@link BlockDeviceMappingType }
     *     
     */
    public BlockDeviceMappingType getBlockDeviceMapping() {
        return blockDeviceMapping;
    }

    /**
     * Sets the value of the blockDeviceMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlockDeviceMappingType }
     *     
     */
    public void setBlockDeviceMapping(BlockDeviceMappingType value) {
        this.blockDeviceMapping = value;
    }

    /**
     * Gets the value of the monitoring property.
     * 
     * @return
     *     possible object is
     *     {@link MonitoringInstanceType }
     *     
     */
    public MonitoringInstanceType getMonitoring() {
        return monitoring;
    }

    /**
     * Sets the value of the monitoring property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonitoringInstanceType }
     *     
     */
    public void setMonitoring(MonitoringInstanceType value) {
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
     * Gets the value of the disableApiTermination property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableApiTermination() {
        return disableApiTermination;
    }

    /**
     * Sets the value of the disableApiTermination property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableApiTermination(Boolean value) {
        this.disableApiTermination = value;
    }

    /**
     * Gets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceInitiatedShutdownBehavior() {
        return instanceInitiatedShutdownBehavior;
    }

    /**
     * Sets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceInitiatedShutdownBehavior(String value) {
        this.instanceInitiatedShutdownBehavior = value;
    }

    /**
     * Gets the value of the accountingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountingType() {
        return accountingType;
    }

    /**
     * Sets the value of the accountingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountingType(String value) {
        this.accountingType = value;
    }

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
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the admin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmin() {
        return admin;
    }

    /**
     * Sets the value of the admin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmin(String value) {
        this.admin = value;
    }

    public RunInstancesType withImageId(String value) {
        setImageId(value);
        return this;
    }

    public RunInstancesType withMinCount(int value) {
        setMinCount(value);
        return this;
    }

    public RunInstancesType withMaxCount(int value) {
        setMaxCount(value);
        return this;
    }

    public RunInstancesType withKeyName(String value) {
        setKeyName(value);
        return this;
    }

    public RunInstancesType withGroupSet(GroupSetType value) {
        setGroupSet(value);
        return this;
    }

    public RunInstancesType withAdditionalInfo(String value) {
        setAdditionalInfo(value);
        return this;
    }

    public RunInstancesType withUserData(UserDataType value) {
        setUserData(value);
        return this;
    }

    public RunInstancesType withAddressingType(String value) {
        setAddressingType(value);
        return this;
    }

    public RunInstancesType withInstanceType(String value) {
        setInstanceType(value);
        return this;
    }

    public RunInstancesType withPlacement(PlacementRequestType value) {
        setPlacement(value);
        return this;
    }

    public RunInstancesType withKernelId(String value) {
        setKernelId(value);
        return this;
    }

    public RunInstancesType withRamdiskId(String value) {
        setRamdiskId(value);
        return this;
    }

    public RunInstancesType withBlockDeviceMapping(BlockDeviceMappingType value) {
        setBlockDeviceMapping(value);
        return this;
    }

    public RunInstancesType withMonitoring(MonitoringInstanceType value) {
        setMonitoring(value);
        return this;
    }

    public RunInstancesType withSubnetId(String value) {
        setSubnetId(value);
        return this;
    }

    public RunInstancesType withDisableApiTermination(Boolean value) {
        setDisableApiTermination(value);
        return this;
    }

    public RunInstancesType withInstanceInitiatedShutdownBehavior(String value) {
        setInstanceInitiatedShutdownBehavior(value);
        return this;
    }

    public RunInstancesType withAccountingType(String value) {
        setAccountingType(value);
        return this;
    }

    public RunInstancesType withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    public RunInstancesType withPassword(String value) {
        setPassword(value);
        return this;
    }

    public RunInstancesType withAdmin(String value) {
        setAdmin(value);
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
