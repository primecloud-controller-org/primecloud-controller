
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
 * <p>Java class for AvailabilityZoneItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvailabilityZoneItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zoneName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zoneState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="regionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageSet" type="{https://cp.cloud.nifty.com/api/1.3/}AvailabilityZoneMessageSetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvailabilityZoneItemType", propOrder = {
    "zoneName",
    "zoneState",
    "regionName",
    "messageSet"
})
public class AvailabilityZoneItemType {

    @XmlElement(required = true)
    protected String zoneName;
    @XmlElement(required = true)
    protected String zoneState;
    @XmlElement(required = true)
    protected String regionName;
    @XmlElement(required = true)
    protected AvailabilityZoneMessageSetType messageSet;

    /**
     * Gets the value of the zoneName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZoneName() {
        return zoneName;
    }

    /**
     * Sets the value of the zoneName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZoneName(String value) {
        this.zoneName = value;
    }

    /**
     * Gets the value of the zoneState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZoneState() {
        return zoneState;
    }

    /**
     * Sets the value of the zoneState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZoneState(String value) {
        this.zoneState = value;
    }

    /**
     * Gets the value of the regionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * Sets the value of the regionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionName(String value) {
        this.regionName = value;
    }

    /**
     * Gets the value of the messageSet property.
     * 
     * @return
     *     possible object is
     *     {@link AvailabilityZoneMessageSetType }
     *     
     */
    public AvailabilityZoneMessageSetType getMessageSet() {
        return messageSet;
    }

    /**
     * Sets the value of the messageSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailabilityZoneMessageSetType }
     *     
     */
    public void setMessageSet(AvailabilityZoneMessageSetType value) {
        this.messageSet = value;
    }

    public AvailabilityZoneItemType withZoneName(String value) {
        setZoneName(value);
        return this;
    }

    public AvailabilityZoneItemType withZoneState(String value) {
        setZoneState(value);
        return this;
    }

    public AvailabilityZoneItemType withRegionName(String value) {
        setRegionName(value);
        return this;
    }

    public AvailabilityZoneItemType withMessageSet(AvailabilityZoneMessageSetType value) {
        setMessageSet(value);
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
