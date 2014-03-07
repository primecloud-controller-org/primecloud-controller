
package jp.primecloud.auto.config.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="no" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="runOrder" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="zabbixTemplate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "no",
    "name",
    "layer",
    "runOrder",
    "zabbixTemplate"
})
@XmlRootElement(name = "componentType")
public class ComponentType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected long no;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String layer;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer runOrder;
    @XmlElement(required = true, nillable = true)
    protected String zabbixTemplate;

    /**
     * Gets the value of the no property.
     * 
     */
    public long getNo() {
        return no;
    }

    /**
     * Sets the value of the no property.
     * 
     */
    public void setNo(long value) {
        this.no = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the layer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the value of the layer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer(String value) {
        this.layer = value;
    }

    /**
     * Gets the value of the runOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRunOrder() {
        return runOrder;
    }

    /**
     * Sets the value of the runOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRunOrder(Integer value) {
        this.runOrder = value;
    }

    /**
     * Gets the value of the zabbixTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZabbixTemplate() {
        return zabbixTemplate;
    }

    /**
     * Sets the value of the zabbixTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZabbixTemplate(String value) {
        this.zabbixTemplate = value;
    }

    public ComponentType withNo(long value) {
        setNo(value);
        return this;
    }

    public ComponentType withName(String value) {
        setName(value);
        return this;
    }

    public ComponentType withLayer(String value) {
        setLayer(value);
        return this;
    }

    public ComponentType withRunOrder(Integer value) {
        setRunOrder(value);
        return this;
    }

    public ComponentType withZabbixTemplate(String value) {
        setZabbixTemplate(value);
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
