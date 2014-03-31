
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
 * <p>Java class for DescribeInstanceAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeInstanceAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;group ref="{https://cp.cloud.nifty.com/api/1.3/}DescribeInstanceAttributesGroup"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeInstanceAttributeType", propOrder = {
    "instanceId",
    "instanceType",
    "kernel",
    "ramdisk",
    "userData",
    "disableApiTermination",
    "instanceInitiatedShutdownBehavior",
    "rootDeviceName",
    "blockDeviceMapping"
})
public class DescribeInstanceAttributeType {

    @XmlElement(required = true)
    protected String instanceId;
    protected EmptyElementType instanceType;
    protected EmptyElementType kernel;
    protected EmptyElementType ramdisk;
    protected EmptyElementType userData;
    protected EmptyElementType disableApiTermination;
    protected EmptyElementType instanceInitiatedShutdownBehavior;
    protected EmptyElementType rootDeviceName;
    protected EmptyElementType blockDeviceMapping;

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
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getInstanceType() {
        return instanceType;
    }

    /**
     * Sets the value of the instanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setInstanceType(EmptyElementType value) {
        this.instanceType = value;
    }

    /**
     * Gets the value of the kernel property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getKernel() {
        return kernel;
    }

    /**
     * Sets the value of the kernel property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setKernel(EmptyElementType value) {
        this.kernel = value;
    }

    /**
     * Gets the value of the ramdisk property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getRamdisk() {
        return ramdisk;
    }

    /**
     * Sets the value of the ramdisk property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setRamdisk(EmptyElementType value) {
        this.ramdisk = value;
    }

    /**
     * Gets the value of the userData property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setUserData(EmptyElementType value) {
        this.userData = value;
    }

    /**
     * Gets the value of the disableApiTermination property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getDisableApiTermination() {
        return disableApiTermination;
    }

    /**
     * Sets the value of the disableApiTermination property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setDisableApiTermination(EmptyElementType value) {
        this.disableApiTermination = value;
    }

    /**
     * Gets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getInstanceInitiatedShutdownBehavior() {
        return instanceInitiatedShutdownBehavior;
    }

    /**
     * Sets the value of the instanceInitiatedShutdownBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setInstanceInitiatedShutdownBehavior(EmptyElementType value) {
        this.instanceInitiatedShutdownBehavior = value;
    }

    /**
     * Gets the value of the rootDeviceName property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getRootDeviceName() {
        return rootDeviceName;
    }

    /**
     * Sets the value of the rootDeviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setRootDeviceName(EmptyElementType value) {
        this.rootDeviceName = value;
    }

    /**
     * Gets the value of the blockDeviceMapping property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getBlockDeviceMapping() {
        return blockDeviceMapping;
    }

    /**
     * Sets the value of the blockDeviceMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setBlockDeviceMapping(EmptyElementType value) {
        this.blockDeviceMapping = value;
    }

    public DescribeInstanceAttributeType withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    public DescribeInstanceAttributeType withInstanceType(EmptyElementType value) {
        setInstanceType(value);
        return this;
    }

    public DescribeInstanceAttributeType withKernel(EmptyElementType value) {
        setKernel(value);
        return this;
    }

    public DescribeInstanceAttributeType withRamdisk(EmptyElementType value) {
        setRamdisk(value);
        return this;
    }

    public DescribeInstanceAttributeType withUserData(EmptyElementType value) {
        setUserData(value);
        return this;
    }

    public DescribeInstanceAttributeType withDisableApiTermination(EmptyElementType value) {
        setDisableApiTermination(value);
        return this;
    }

    public DescribeInstanceAttributeType withInstanceInitiatedShutdownBehavior(EmptyElementType value) {
        setInstanceInitiatedShutdownBehavior(value);
        return this;
    }

    public DescribeInstanceAttributeType withRootDeviceName(EmptyElementType value) {
        setRootDeviceName(value);
        return this;
    }

    public DescribeInstanceAttributeType withBlockDeviceMapping(EmptyElementType value) {
        setBlockDeviceMapping(value);
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
