
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
 *         &lt;element ref="{}platforms"/>
 *         &lt;element ref="{}componentTypes"/>
 *         &lt;element ref="{}images"/>
 *         &lt;element ref="{}templates"/>
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
    "platforms",
    "componentTypes",
    "images",
    "templates"
})
@XmlRootElement(name = "autoConfig")
public class AutoConfig
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected Platforms platforms;
    @XmlElement(required = true)
    protected ComponentTypes componentTypes;
    @XmlElement(required = true)
    protected Images images;
    @XmlElement(required = true)
    protected Templates templates;

    /**
     * Gets the value of the platforms property.
     * 
     * @return
     *     possible object is
     *     {@link Platforms }
     *     
     */
    public Platforms getPlatforms() {
        return platforms;
    }

    /**
     * Sets the value of the platforms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platforms }
     *     
     */
    public void setPlatforms(Platforms value) {
        this.platforms = value;
    }

    /**
     * Gets the value of the componentTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentTypes }
     *     
     */
    public ComponentTypes getComponentTypes() {
        return componentTypes;
    }

    /**
     * Sets the value of the componentTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentTypes }
     *     
     */
    public void setComponentTypes(ComponentTypes value) {
        this.componentTypes = value;
    }

    /**
     * Gets the value of the images property.
     * 
     * @return
     *     possible object is
     *     {@link Images }
     *     
     */
    public Images getImages() {
        return images;
    }

    /**
     * Sets the value of the images property.
     * 
     * @param value
     *     allowed object is
     *     {@link Images }
     *     
     */
    public void setImages(Images value) {
        this.images = value;
    }

    /**
     * Gets the value of the templates property.
     * 
     * @return
     *     possible object is
     *     {@link Templates }
     *     
     */
    public Templates getTemplates() {
        return templates;
    }

    /**
     * Sets the value of the templates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Templates }
     *     
     */
    public void setTemplates(Templates value) {
        this.templates = value;
    }

    public AutoConfig withPlatforms(Platforms value) {
        setPlatforms(value);
        return this;
    }

    public AutoConfig withComponentTypes(ComponentTypes value) {
        setComponentTypes(value);
        return this;
    }

    public AutoConfig withImages(Images value) {
        setImages(value);
        return this;
    }

    public AutoConfig withTemplates(Templates value) {
        setTemplates(value);
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
