
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
 * <p>Java class for DescribeInstancesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeInstancesResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reservationSet" type="{https://cp.cloud.nifty.com/api/1.3/}ReservationSetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeInstancesResponseType", propOrder = {
    "requestId",
    "reservationSet"
})
public class DescribeInstancesResponseType {

    @XmlElement(required = true)
    protected String requestId;
    @XmlElement(required = true)
    protected ReservationSetType reservationSet;

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the reservationSet property.
     * 
     * @return
     *     possible object is
     *     {@link ReservationSetType }
     *     
     */
    public ReservationSetType getReservationSet() {
        return reservationSet;
    }

    /**
     * Sets the value of the reservationSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReservationSetType }
     *     
     */
    public void setReservationSet(ReservationSetType value) {
        this.reservationSet = value;
    }

    public DescribeInstancesResponseType withRequestId(String value) {
        setRequestId(value);
        return this;
    }

    public DescribeInstancesResponseType withReservationSet(ReservationSetType value) {
        setReservationSet(value);
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
