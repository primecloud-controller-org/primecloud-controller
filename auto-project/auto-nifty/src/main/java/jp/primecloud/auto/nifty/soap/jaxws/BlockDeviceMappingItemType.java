
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
 * <p>Java class for BlockDeviceMappingItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BlockDeviceMappingItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deviceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="virtualName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="ebs" type="{https://cp.cloud.nifty.com/api/1.3/}EbsBlockDeviceType"/>
 *           &lt;element name="noDevice" type="{https://cp.cloud.nifty.com/api/1.3/}EmptyElementType"/>
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
@XmlType(name = "BlockDeviceMappingItemType", propOrder = {
    "deviceName",
    "virtualName",
    "ebs",
    "noDevice"
})
public class BlockDeviceMappingItemType {

    @XmlElement(required = true)
    protected String deviceName;
    protected String virtualName;
    protected EbsBlockDeviceType ebs;
    protected EmptyElementType noDevice;

    /**
     * Gets the value of the deviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the value of the deviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceName(String value) {
        this.deviceName = value;
    }

    /**
     * Gets the value of the virtualName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVirtualName() {
        return virtualName;
    }

    /**
     * Sets the value of the virtualName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVirtualName(String value) {
        this.virtualName = value;
    }

    /**
     * Gets the value of the ebs property.
     * 
     * @return
     *     possible object is
     *     {@link EbsBlockDeviceType }
     *     
     */
    public EbsBlockDeviceType getEbs() {
        return ebs;
    }

    /**
     * Sets the value of the ebs property.
     * 
     * @param value
     *     allowed object is
     *     {@link EbsBlockDeviceType }
     *     
     */
    public void setEbs(EbsBlockDeviceType value) {
        this.ebs = value;
    }

    /**
     * Gets the value of the noDevice property.
     * 
     * @return
     *     possible object is
     *     {@link EmptyElementType }
     *     
     */
    public EmptyElementType getNoDevice() {
        return noDevice;
    }

    /**
     * Sets the value of the noDevice property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyElementType }
     *     
     */
    public void setNoDevice(EmptyElementType value) {
        this.noDevice = value;
    }

    public BlockDeviceMappingItemType withDeviceName(String value) {
        setDeviceName(value);
        return this;
    }

    public BlockDeviceMappingItemType withVirtualName(String value) {
        setVirtualName(value);
        return this;
    }

    public BlockDeviceMappingItemType withEbs(EbsBlockDeviceType value) {
        setEbs(value);
        return this;
    }

    public BlockDeviceMappingItemType withNoDevice(EmptyElementType value) {
        setNoDevice(value);
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
