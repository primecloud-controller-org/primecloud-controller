
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
 * <p>Java class for DescribeImagesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeImagesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="executableBySet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeImagesExecutableBySetType" minOccurs="0"/>
 *         &lt;element name="imagesSet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeImagesInfoType"/>
 *         &lt;element name="ownersSet" type="{https://cp.cloud.nifty.com/api/1.3/}DescribeImagesOwnersType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeImagesType", propOrder = {
    "executableBySet",
    "imagesSet",
    "ownersSet"
})
public class DescribeImagesType {

    protected DescribeImagesExecutableBySetType executableBySet;
    @XmlElement(required = true)
    protected DescribeImagesInfoType imagesSet;
    protected DescribeImagesOwnersType ownersSet;

    /**
     * Gets the value of the executableBySet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeImagesExecutableBySetType }
     *     
     */
    public DescribeImagesExecutableBySetType getExecutableBySet() {
        return executableBySet;
    }

    /**
     * Sets the value of the executableBySet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeImagesExecutableBySetType }
     *     
     */
    public void setExecutableBySet(DescribeImagesExecutableBySetType value) {
        this.executableBySet = value;
    }

    /**
     * Gets the value of the imagesSet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeImagesInfoType }
     *     
     */
    public DescribeImagesInfoType getImagesSet() {
        return imagesSet;
    }

    /**
     * Sets the value of the imagesSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeImagesInfoType }
     *     
     */
    public void setImagesSet(DescribeImagesInfoType value) {
        this.imagesSet = value;
    }

    /**
     * Gets the value of the ownersSet property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeImagesOwnersType }
     *     
     */
    public DescribeImagesOwnersType getOwnersSet() {
        return ownersSet;
    }

    /**
     * Sets the value of the ownersSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeImagesOwnersType }
     *     
     */
    public void setOwnersSet(DescribeImagesOwnersType value) {
        this.ownersSet = value;
    }

    public DescribeImagesType withExecutableBySet(DescribeImagesExecutableBySetType value) {
        setExecutableBySet(value);
        return this;
    }

    public DescribeImagesType withImagesSet(DescribeImagesInfoType value) {
        setImagesSet(value);
        return this;
    }

    public DescribeImagesType withOwnersSet(DescribeImagesOwnersType value) {
        setOwnersSet(value);
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
