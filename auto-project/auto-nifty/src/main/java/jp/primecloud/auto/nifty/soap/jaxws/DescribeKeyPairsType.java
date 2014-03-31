
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
 * <p>Java class for DescribeKeyPairsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeKeyPairsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keySet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeKeyPairsInfoType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeKeyPairsType", propOrder = {
    "keySet"
})
public class DescribeKeyPairsType {

    @XmlElement(required = true)
    protected DescribeKeyPairsInfoType keySet;

    /**
     * Gets the value of the keySet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeKeyPairsInfoType }
     *     
     */
    public DescribeKeyPairsInfoType getKeySet() {
        return keySet;
    }

    /**
     * Sets the value of the keySet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeKeyPairsInfoType }
     *     
     */
    public void setKeySet(DescribeKeyPairsInfoType value) {
        this.keySet = value;
    }

    public DescribeKeyPairsType withKeySet(DescribeKeyPairsInfoType value) {
        setKeySet(value);
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
