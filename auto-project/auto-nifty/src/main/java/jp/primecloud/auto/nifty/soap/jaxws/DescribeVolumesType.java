
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
 * <p>Java class for DescribeVolumesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeVolumesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="volumeSet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeVolumesSetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeVolumesType", propOrder = {
    "volumeSet"
})
public class DescribeVolumesType {

    @XmlElement(required = true)
    protected DescribeVolumesSetType volumeSet;

    /**
     * Gets the value of the volumeSet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeVolumesSetType }
     *     
     */
    public DescribeVolumesSetType getVolumeSet() {
        return volumeSet;
    }

    /**
     * Sets the value of the volumeSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeVolumesSetType }
     *     
     */
    public void setVolumeSet(DescribeVolumesSetType value) {
        this.volumeSet = value;
    }

    public DescribeVolumesType withVolumeSet(DescribeVolumesSetType value) {
        setVolumeSet(value);
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
