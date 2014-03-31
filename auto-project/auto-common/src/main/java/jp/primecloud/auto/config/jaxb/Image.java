
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
 *         &lt;element name="platformNo" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="os" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="selectable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="componentTypeNos" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zabbixTemplate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="aws" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="kernelId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ramdiskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ebsImage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="vmware" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="templateName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="nifty" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="cloudstack" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="templateId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="zoneId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "platformNo",
    "os",
    "selectable",
    "componentTypeNos",
    "zabbixTemplate",
    "aws",
    "vmware",
    "nifty",
    "cloudstack"
})
@XmlRootElement(name = "image")
public class Image
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected long no;
    @XmlElement(required = true)
    protected String name;
    protected long platformNo;
    @XmlElement(required = true)
    protected String os;
    protected boolean selectable;
    @XmlElement(required = true, nillable = true)
    protected String componentTypeNos;
    @XmlElement(required = true, nillable = true)
    protected String zabbixTemplate;
    protected Image.Aws aws;
    protected Image.Vmware vmware;
    protected Image.Nifty nifty;
    protected Image.Cloudstack cloudstack;

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
     * Gets the value of the platformNo property.
     * 
     */
    public long getPlatformNo() {
        return platformNo;
    }

    /**
     * Sets the value of the platformNo property.
     * 
     */
    public void setPlatformNo(long value) {
        this.platformNo = value;
    }

    /**
     * Gets the value of the os property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets the value of the os property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOs(String value) {
        this.os = value;
    }

    /**
     * Gets the value of the selectable property.
     * 
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Sets the value of the selectable property.
     * 
     */
    public void setSelectable(boolean value) {
        this.selectable = value;
    }

    /**
     * Gets the value of the componentTypeNos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentTypeNos() {
        return componentTypeNos;
    }

    /**
     * Sets the value of the componentTypeNos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentTypeNos(String value) {
        this.componentTypeNos = value;
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

    /**
     * Gets the value of the aws property.
     * 
     * @return
     *     possible object is
     *     {@link Image.Aws }
     *     
     */
    public Image.Aws getAws() {
        return aws;
    }

    /**
     * Sets the value of the aws property.
     * 
     * @param value
     *     allowed object is
     *     {@link Image.Aws }
     *     
     */
    public void setAws(Image.Aws value) {
        this.aws = value;
    }

    /**
     * Gets the value of the vmware property.
     * 
     * @return
     *     possible object is
     *     {@link Image.Vmware }
     *     
     */
    public Image.Vmware getVmware() {
        return vmware;
    }

    /**
     * Sets the value of the vmware property.
     * 
     * @param value
     *     allowed object is
     *     {@link Image.Vmware }
     *     
     */
    public void setVmware(Image.Vmware value) {
        this.vmware = value;
    }

    /**
     * Gets the value of the nifty property.
     * 
     * @return
     *     possible object is
     *     {@link Image.Nifty }
     *     
     */
    public Image.Nifty getNifty() {
        return nifty;
    }

    /**
     * Sets the value of the nifty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Image.Nifty }
     *     
     */
    public void setNifty(Image.Nifty value) {
        this.nifty = value;
    }

    /**
     * Gets the value of the cloudstack property.
     * 
     * @return
     *     possible object is
     *     {@link Image.Cloudstack }
     *     
     */
    public Image.Cloudstack getCloudstack() {
        return cloudstack;
    }

    /**
     * Sets the value of the cloudstack property.
     * 
     * @param value
     *     allowed object is
     *     {@link Image.Cloudstack }
     *     
     */
    public void setCloudstack(Image.Cloudstack value) {
        this.cloudstack = value;
    }

    public Image withNo(long value) {
        setNo(value);
        return this;
    }

    public Image withName(String value) {
        setName(value);
        return this;
    }

    public Image withPlatformNo(long value) {
        setPlatformNo(value);
        return this;
    }

    public Image withOs(String value) {
        setOs(value);
        return this;
    }

    public Image withSelectable(boolean value) {
        setSelectable(value);
        return this;
    }

    public Image withComponentTypeNos(String value) {
        setComponentTypeNos(value);
        return this;
    }

    public Image withZabbixTemplate(String value) {
        setZabbixTemplate(value);
        return this;
    }

    public Image withAws(Image.Aws value) {
        setAws(value);
        return this;
    }

    public Image withVmware(Image.Vmware value) {
        setVmware(value);
        return this;
    }

    public Image withNifty(Image.Nifty value) {
        setNifty(value);
        return this;
    }

    public Image withCloudstack(Image.Cloudstack value) {
        setCloudstack(value);
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
     *         &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="kernelId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ramdiskId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ebsImage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        "imageId",
        "kernelId",
        "ramdiskId",
        "instanceTypes",
        "ebsImage"
    })
    public static class Aws
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String imageId;
        @XmlElement(required = true, nillable = true)
        protected String kernelId;
        @XmlElement(required = true, nillable = true)
        protected String ramdiskId;
        @XmlElement(required = true)
        protected String instanceTypes;
        protected boolean ebsImage;

        /**
         * Gets the value of the imageId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImageId() {
            return imageId;
        }

        /**
         * Sets the value of the imageId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImageId(String value) {
            this.imageId = value;
        }

        /**
         * Gets the value of the kernelId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKernelId() {
            return kernelId;
        }

        /**
         * Sets the value of the kernelId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKernelId(String value) {
            this.kernelId = value;
        }

        /**
         * Gets the value of the ramdiskId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRamdiskId() {
            return ramdiskId;
        }

        /**
         * Sets the value of the ramdiskId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRamdiskId(String value) {
            this.ramdiskId = value;
        }

        /**
         * Gets the value of the instanceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstanceTypes() {
            return instanceTypes;
        }

        /**
         * Sets the value of the instanceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstanceTypes(String value) {
            this.instanceTypes = value;
        }

        /**
         * Gets the value of the ebsImage property.
         * 
         */
        public boolean isEbsImage() {
            return ebsImage;
        }

        /**
         * Sets the value of the ebsImage property.
         * 
         */
        public void setEbsImage(boolean value) {
            this.ebsImage = value;
        }

        public Image.Aws withImageId(String value) {
            setImageId(value);
            return this;
        }

        public Image.Aws withKernelId(String value) {
            setKernelId(value);
            return this;
        }

        public Image.Aws withRamdiskId(String value) {
            setRamdiskId(value);
            return this;
        }

        public Image.Aws withInstanceTypes(String value) {
            setInstanceTypes(value);
            return this;
        }

        public Image.Aws withEbsImage(boolean value) {
            setEbsImage(value);
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
     *         &lt;element name="templateId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="zoneId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "templateId",
        "zoneId",
        "instanceTypes"
    })
    public static class Cloudstack
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String templateId;
        @XmlElement(required = true)
        protected String zoneId;
        @XmlElement(required = true)
        protected String instanceTypes;

        /**
         * Gets the value of the templateId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTemplateId() {
            return templateId;
        }

        /**
         * Sets the value of the templateId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTemplateId(String value) {
            this.templateId = value;
        }

        /**
         * Gets the value of the zoneId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZoneId() {
            return zoneId;
        }

        /**
         * Sets the value of the zoneId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZoneId(String value) {
            this.zoneId = value;
        }

        /**
         * Gets the value of the instanceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstanceTypes() {
            return instanceTypes;
        }

        /**
         * Sets the value of the instanceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstanceTypes(String value) {
            this.instanceTypes = value;
        }

        public Image.Cloudstack withTemplateId(String value) {
            setTemplateId(value);
            return this;
        }

        public Image.Cloudstack withZoneId(String value) {
            setZoneId(value);
            return this;
        }

        public Image.Cloudstack withInstanceTypes(String value) {
            setInstanceTypes(value);
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
     *         &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "imageId",
        "instanceTypes"
    })
    public static class Nifty
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String imageId;
        @XmlElement(required = true)
        protected String instanceTypes;

        /**
         * Gets the value of the imageId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImageId() {
            return imageId;
        }

        /**
         * Sets the value of the imageId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImageId(String value) {
            this.imageId = value;
        }

        /**
         * Gets the value of the instanceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstanceTypes() {
            return instanceTypes;
        }

        /**
         * Sets the value of the instanceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstanceTypes(String value) {
            this.instanceTypes = value;
        }

        public Image.Nifty withImageId(String value) {
            setImageId(value);
            return this;
        }

        public Image.Nifty withInstanceTypes(String value) {
            setInstanceTypes(value);
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
     *         &lt;element name="templateName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="instanceTypes" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "templateName",
        "instanceTypes"
    })
    public static class Vmware
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String templateName;
        @XmlElement(required = true)
        protected String instanceTypes;

        /**
         * Gets the value of the templateName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         * Sets the value of the templateName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTemplateName(String value) {
            this.templateName = value;
        }

        /**
         * Gets the value of the instanceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstanceTypes() {
            return instanceTypes;
        }

        /**
         * Sets the value of the instanceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstanceTypes(String value) {
            this.instanceTypes = value;
        }

        public Image.Vmware withTemplateName(String value) {
            setTemplateName(value);
            return this;
        }

        public Image.Vmware withInstanceTypes(String value) {
            setInstanceTypes(value);
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

}
