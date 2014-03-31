
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
 * <p>Java class for InstanceBlockDeviceMappingResponseItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstanceBlockDeviceMappingResponseItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deviceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="ebs" type="{https://cp.cloud.nifty.com/api/1.3/}EbsInstanceBlockDeviceMappingResponseType"/>
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
@XmlType(name = "InstanceBlockDeviceMappingResponseItemType", propOrder = {
    "deviceName",
    "ebs"
})
public class InstanceBlockDeviceMappingResponseItemType {

    @XmlElement(required = true)
    protected String deviceName;
    protected EbsInstanceBlockDeviceMappingResponseType ebs;

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
     * Gets the value of the ebs property.
     * 
     * @return
     *     possible object is
     *     {@link EbsInstanceBlockDeviceMappingResponseType }
     *     
     */
    public EbsInstanceBlockDeviceMappingResponseType getEbs() {
        return ebs;
    }

    /**
     * Sets the value of the ebs property.
     * 
     * @param value
     *     allowed object is
     *     {@link EbsInstanceBlockDeviceMappingResponseType }
     *     
     */
    public void setEbs(EbsInstanceBlockDeviceMappingResponseType value) {
        this.ebs = value;
    }

    public InstanceBlockDeviceMappingResponseItemType withDeviceName(String value) {
        setDeviceName(value);
        return this;
    }

    public InstanceBlockDeviceMappingResponseItemType withEbs(EbsInstanceBlockDeviceMappingResponseType value) {
        setEbs(value);
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
