
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
 * <p>Java class for DescribeAvailabilityZonesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeAvailabilityZonesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availabilityZoneSet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeAvailabilityZonesSetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeAvailabilityZonesType", propOrder = {
    "availabilityZoneSet"
})
public class DescribeAvailabilityZonesType {

    @XmlElement(required = true)
    protected DescribeAvailabilityZonesSetType availabilityZoneSet;

    /**
     * Gets the value of the availabilityZoneSet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeAvailabilityZonesSetType }
     *     
     */
    public DescribeAvailabilityZonesSetType getAvailabilityZoneSet() {
        return availabilityZoneSet;
    }

    /**
     * Sets the value of the availabilityZoneSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeAvailabilityZonesSetType }
     *     
     */
    public void setAvailabilityZoneSet(DescribeAvailabilityZonesSetType value) {
        this.availabilityZoneSet = value;
    }

    public DescribeAvailabilityZonesType withAvailabilityZoneSet(DescribeAvailabilityZonesSetType value) {
        setAvailabilityZoneSet(value);
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
