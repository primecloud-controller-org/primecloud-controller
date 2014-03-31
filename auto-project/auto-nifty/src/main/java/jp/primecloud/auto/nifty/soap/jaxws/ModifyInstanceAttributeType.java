
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
 * <p>Java class for ModifyInstanceAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ModifyInstanceAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="instanceType" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *           &lt;element name="kernel" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *           &lt;element name="ramdisk" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *           &lt;element name="userData" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *           &lt;element name="disableApiTermination" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeBooleanValueType"/>
 *           &lt;element name="instanceInitiatedShutdownBehavior" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *           &lt;element name="blockDeviceMapping" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceBlockDeviceMappingType"/>
 *           &lt;element name="instanceName" type="{https://cp.cloud.nifty.com/api/1.3/}AttributeValueType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModifyInstanceAttributeType", propOrder = {
    "instanceId",
    "instanceType",
    "kernel",
    "ramdisk",
    "userData",
    "disableApiTermination",
    "instanceInitiatedShutdownBehavior",
    "blockDeviceMapping",
    "instanceName"
})
public class ModifyInstanceAttributeType {

    @XmlElement(required = true)
    protected String instanceId;
    protected AttributeValueType instanceType;
    protected AttributeValueType kernel;
    protected AttributeValueType ramdisk;
    protected AttributeValueType userData;
    protected AttributeBooleanValueType disableApiTermination;
    protected AttributeValueType instanceInitiatedShutdownBehavior;
    protected InstanceBlockDeviceMappingType blockDeviceMapping;
    protected AttributeValueType instanceName;

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
     * Gets the value of the instanceType property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getInstanceType() {
        return instanceType;
    }

    /**
     * Sets the value of the instanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setInstanceType(AttributeValueType value) {
        this.instanceType = value;
    }

    /**
     * Gets the value of the kernel property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getKernel() {
        return kernel;
    }

    /**
     * Sets the value of the kernel property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setKernel(AttributeValueType value) {
        this.kernel = value;
    }

    /**
     * Gets the value of the ramdisk property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getRamdisk() {
        return ramdisk;
    }

    /**
     * Sets the value of the ramdisk property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setRamdisk(AttributeValueType value) {
        this.ramdisk = value;
    }

    /**
     * Gets the value of the userData property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setUserData(AttributeValueType value) {
        this.userData = value;
    }

    /**
     * Gets the value of the disableApiTermination property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeBooleanValueType }
     *     
     */
    public AttributeBooleanValueType getDisableApiTermination() {
        return disableApiTermination;
    }

    /**
     * Sets the value of the disableApiTermination property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeBooleanValueType }
     *     
     */
    public void setDisableApiTermination(AttributeBooleanValueType value) {
        this.disableApiTermination = value;
    }

    /**
     * Gets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getInstanceInitiatedShutdownBehavior() {
        return instanceInitiatedShutdownBehavior;
    }

    /**
     * Sets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setInstanceInitiatedShutdownBehavior(AttributeValueType value) {
        this.instanceInitiatedShutdownBehavior = value;
    }

    /**
     * Gets the value of the blockDeviceMapping property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceBlockDeviceMappingType }
     *     
     */
    public InstanceBlockDeviceMappingType getBlockDeviceMapping() {
        return blockDeviceMapping;
    }

    /**
     * Sets the value of the blockDeviceMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceBlockDeviceMappingType }
     *     
     */
    public void setBlockDeviceMapping(InstanceBlockDeviceMappingType value) {
        this.blockDeviceMapping = value;
    }

    /**
     * Gets the value of the instanceName property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValueType }
     *     
     */
    public AttributeValueType getInstanceName() {
        return instanceName;
    }

    /**
     * Sets the value of the instanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValueType }
     *     
     */
    public void setInstanceName(AttributeValueType value) {
        this.instanceName = value;
    }

    public ModifyInstanceAttributeType withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    public ModifyInstanceAttributeType withInstanceType(AttributeValueType value) {
        setInstanceType(value);
        return this;
    }

    public ModifyInstanceAttributeType withKernel(AttributeValueType value) {
        setKernel(value);
        return this;
    }

    public ModifyInstanceAttributeType withRamdisk(AttributeValueType value) {
        setRamdisk(value);
        return this;
    }

    public ModifyInstanceAttributeType withUserData(AttributeValueType value) {
        setUserData(value);
        return this;
    }

    public ModifyInstanceAttributeType withDisableApiTermination(AttributeBooleanValueType value) {
        setDisableApiTermination(value);
        return this;
    }

    public ModifyInstanceAttributeType withInstanceInitiatedShutdownBehavior(AttributeValueType value) {
        setInstanceInitiatedShutdownBehavior(value);
        return this;
    }

    public ModifyInstanceAttributeType withBlockDeviceMapping(InstanceBlockDeviceMappingType value) {
        setBlockDeviceMapping(value);
        return this;
    }

    public ModifyInstanceAttributeType withInstanceName(AttributeValueType value) {
        setInstanceName(value);
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
