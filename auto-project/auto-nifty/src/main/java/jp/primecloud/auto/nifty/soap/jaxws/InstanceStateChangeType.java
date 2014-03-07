
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
 * <p>Java class for InstanceStateChangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstanceStateChangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instanceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="currentState" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceStateType"/>
 *         &lt;element name="previousState" type="{https://cp.cloud.nifty.com/api/1.3/}InstanceStateType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceStateChangeType", propOrder = {
    "instanceId",
    "currentState",
    "previousState"
})
public class InstanceStateChangeType {

    @XmlElement(required = true)
    protected String instanceId;
    @XmlElement(required = true)
    protected InstanceStateType currentState;
    @XmlElement(required = true)
    protected InstanceStateType previousState;

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
     * Gets the value of the currentState property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceStateType }
     *     
     */
    public InstanceStateType getCurrentState() {
        return currentState;
    }

    /**
     * Sets the value of the currentState property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceStateType }
     *     
     */
    public void setCurrentState(InstanceStateType value) {
        this.currentState = value;
    }

    /**
     * Gets the value of the previousState property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceStateType }
     *     
     */
    public InstanceStateType getPreviousState() {
        return previousState;
    }

    /**
     * Sets the value of the previousState property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceStateType }
     *     
     */
    public void setPreviousState(InstanceStateType value) {
        this.previousState = value;
    }

    public InstanceStateChangeType withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    public InstanceStateChangeType withCurrentState(InstanceStateType value) {
        setCurrentState(value);
        return this;
    }

    public InstanceStateChangeType withPreviousState(InstanceStateType value) {
        setPreviousState(value);
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
