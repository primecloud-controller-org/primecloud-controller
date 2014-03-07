
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
 * <p>Java class for RebootInstancesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RebootInstancesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instancesSet" type="{https://cp.cloud.nifty.com/api/1.3/}RebootInstancesInfoType"/>
 *         &lt;element name="force" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RebootInstancesType", propOrder = {
    "instancesSet",
    "force"
})
public class RebootInstancesType {

    @XmlElement(required = true)
    protected RebootInstancesInfoType instancesSet;
    protected Boolean force;

    /**
     * Gets the value of the instancesSet property.
     * 
     * @return
     *     possible object is
     *     {@link RebootInstancesInfoType }
     *     
     */
    public RebootInstancesInfoType getInstancesSet() {
        return instancesSet;
    }

    /**
     * Sets the value of the instancesSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link RebootInstancesInfoType }
     *     
     */
    public void setInstancesSet(RebootInstancesInfoType value) {
        this.instancesSet = value;
    }

    /**
     * Gets the value of the force property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isForce() {
        return force;
    }

    /**
     * Sets the value of the force property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setForce(Boolean value) {
        this.force = value;
    }

    public RebootInstancesType withInstancesSet(RebootInstancesInfoType value) {
        setInstancesSet(value);
        return this;
    }

    public RebootInstancesType withForce(Boolean value) {
        setForce(value);
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
