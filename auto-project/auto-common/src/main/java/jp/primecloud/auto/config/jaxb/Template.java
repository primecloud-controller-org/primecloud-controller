
package jp.primecloud.auto.config.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="components" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="component" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="componentTypeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="diskSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="associate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="instances" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="instance" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="platformName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="aws" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="vmware" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="nifty" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="cloudstack" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
    "components",
    "instances"
})
@XmlRootElement(name = "template")
public class Template
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected long no;
    @XmlElement(required = true)
    protected String name;
    protected Template.Components components;
    protected Template.Instances instances;

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
     * Gets the value of the components property.
     * 
     * @return
     *     possible object is
     *     {@link Template.Components }
     *     
     */
    public Template.Components getComponents() {
        return components;
    }

    /**
     * Sets the value of the components property.
     * 
     * @param value
     *     allowed object is
     *     {@link Template.Components }
     *     
     */
    public void setComponents(Template.Components value) {
        this.components = value;
    }

    /**
     * Gets the value of the instances property.
     * 
     * @return
     *     possible object is
     *     {@link Template.Instances }
     *     
     */
    public Template.Instances getInstances() {
        return instances;
    }

    /**
     * Sets the value of the instances property.
     * 
     * @param value
     *     allowed object is
     *     {@link Template.Instances }
     *     
     */
    public void setInstances(Template.Instances value) {
        this.instances = value;
    }

    public Template withNo(long value) {
        setNo(value);
        return this;
    }

    public Template withName(String value) {
        setName(value);
        return this;
    }

    public Template withComponents(Template.Components value) {
        setComponents(value);
        return this;
    }

    public Template withInstances(Template.Instances value) {
        setInstances(value);
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
     *         &lt;element name="component" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="componentTypeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="diskSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="associate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "component"
    })
    public static class Components
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        protected List<Template.Components.Component> component;

        /**
         * Gets the value of the component property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the component property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getComponent().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Template.Components.Component }
         * 
         * 
         */
        public List<Template.Components.Component> getComponent() {
            if (component == null) {
                component = new ArrayList<Template.Components.Component>();
            }
            return this.component;
        }

        public Template.Components withComponent(Template.Components.Component... values) {
            if (values!= null) {
                for (Template.Components.Component value: values) {
                    getComponent().add(value);
                }
            }
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
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="componentTypeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="diskSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="associate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "name",
            "componentTypeName",
            "comment",
            "diskSize",
            "associate"
        })
        public static class Component
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(required = true)
            protected String name;
            @XmlElement(required = true)
            protected String componentTypeName;
            @XmlElement(required = true, nillable = true)
            protected String comment;
            @XmlElement(required = true, type = Integer.class, nillable = true)
            protected Integer diskSize;
            @XmlElement(required = true, nillable = true)
            protected String associate;

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
             * Gets the value of the componentTypeName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getComponentTypeName() {
                return componentTypeName;
            }

            /**
             * Sets the value of the componentTypeName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setComponentTypeName(String value) {
                this.componentTypeName = value;
            }

            /**
             * Gets the value of the comment property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getComment() {
                return comment;
            }

            /**
             * Sets the value of the comment property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setComment(String value) {
                this.comment = value;
            }

            /**
             * Gets the value of the diskSize property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getDiskSize() {
                return diskSize;
            }

            /**
             * Sets the value of the diskSize property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setDiskSize(Integer value) {
                this.diskSize = value;
            }

            /**
             * Gets the value of the associate property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAssociate() {
                return associate;
            }

            /**
             * Sets the value of the associate property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAssociate(String value) {
                this.associate = value;
            }

            public Template.Components.Component withName(String value) {
                setName(value);
                return this;
            }

            public Template.Components.Component withComponentTypeName(String value) {
                setComponentTypeName(value);
                return this;
            }

            public Template.Components.Component withComment(String value) {
                setComment(value);
                return this;
            }

            public Template.Components.Component withDiskSize(Integer value) {
                setDiskSize(value);
                return this;
            }

            public Template.Components.Component withAssociate(String value) {
                setAssociate(value);
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
     *         &lt;element name="instance" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="platformName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="aws" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="vmware" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="nifty" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="cloudstack" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
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
        "instance"
    })
    public static class Instances
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        protected List<Template.Instances.Instance> instance;

        /**
         * Gets the value of the instance property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the instance property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInstance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Template.Instances.Instance }
         * 
         * 
         */
        public List<Template.Instances.Instance> getInstance() {
            if (instance == null) {
                instance = new ArrayList<Template.Instances.Instance>();
            }
            return this.instance;
        }

        public Template.Instances withInstance(Template.Instances.Instance... values) {
            if (values!= null) {
                for (Template.Instances.Instance value: values) {
                    getInstance().add(value);
                }
            }
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
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="platformName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="aws" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
         *                   &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
         *                   &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
         *                   &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "name",
            "platformName",
            "comment",
            "aws",
            "vmware",
            "nifty",
            "cloudstack"
        })
        public static class Instance
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(required = true)
            protected String name;
            @XmlElement(required = true)
            protected String platformName;
            @XmlElement(required = true, nillable = true)
            protected String comment;
            protected Template.Instances.Instance.Aws aws;
            protected Template.Instances.Instance.Vmware vmware;
            protected Template.Instances.Instance.Nifty nifty;
            protected Template.Instances.Instance.Cloudstack cloudstack;

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
             * Gets the value of the platformName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPlatformName() {
                return platformName;
            }

            /**
             * Sets the value of the platformName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPlatformName(String value) {
                this.platformName = value;
            }

            /**
             * Gets the value of the comment property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getComment() {
                return comment;
            }

            /**
             * Sets the value of the comment property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setComment(String value) {
                this.comment = value;
            }

            /**
             * Gets the value of the aws property.
             * 
             * @return
             *     possible object is
             *     {@link Template.Instances.Instance.Aws }
             *     
             */
            public Template.Instances.Instance.Aws getAws() {
                return aws;
            }

            /**
             * Sets the value of the aws property.
             * 
             * @param value
             *     allowed object is
             *     {@link Template.Instances.Instance.Aws }
             *     
             */
            public void setAws(Template.Instances.Instance.Aws value) {
                this.aws = value;
            }

            /**
             * Gets the value of the vmware property.
             * 
             * @return
             *     possible object is
             *     {@link Template.Instances.Instance.Vmware }
             *     
             */
            public Template.Instances.Instance.Vmware getVmware() {
                return vmware;
            }

            /**
             * Sets the value of the vmware property.
             * 
             * @param value
             *     allowed object is
             *     {@link Template.Instances.Instance.Vmware }
             *     
             */
            public void setVmware(Template.Instances.Instance.Vmware value) {
                this.vmware = value;
            }

            /**
             * Gets the value of the nifty property.
             * 
             * @return
             *     possible object is
             *     {@link Template.Instances.Instance.Nifty }
             *     
             */
            public Template.Instances.Instance.Nifty getNifty() {
                return nifty;
            }

            /**
             * Sets the value of the nifty property.
             * 
             * @param value
             *     allowed object is
             *     {@link Template.Instances.Instance.Nifty }
             *     
             */
            public void setNifty(Template.Instances.Instance.Nifty value) {
                this.nifty = value;
            }

            /**
             * Gets the value of the cloudstack property.
             * 
             * @return
             *     possible object is
             *     {@link Template.Instances.Instance.Cloudstack }
             *     
             */
            public Template.Instances.Instance.Cloudstack getCloudstack() {
                return cloudstack;
            }

            /**
             * Sets the value of the cloudstack property.
             * 
             * @param value
             *     allowed object is
             *     {@link Template.Instances.Instance.Cloudstack }
             *     
             */
            public void setCloudstack(Template.Instances.Instance.Cloudstack value) {
                this.cloudstack = value;
            }

            public Template.Instances.Instance withName(String value) {
                setName(value);
                return this;
            }

            public Template.Instances.Instance withPlatformName(String value) {
                setPlatformName(value);
                return this;
            }

            public Template.Instances.Instance withComment(String value) {
                setComment(value);
                return this;
            }

            public Template.Instances.Instance withAws(Template.Instances.Instance.Aws value) {
                setAws(value);
                return this;
            }

            public Template.Instances.Instance withVmware(Template.Instances.Instance.Vmware value) {
                setVmware(value);
                return this;
            }

            public Template.Instances.Instance withNifty(Template.Instances.Instance.Nifty value) {
                setNifty(value);
                return this;
            }

            public Template.Instances.Instance withCloudstack(Template.Instances.Instance.Cloudstack value) {
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
             *         &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "imageName",
                "instanceType"
            })
            public static class Aws
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(required = true)
                protected String imageName;
                @XmlElement(required = true)
                protected String instanceType;

                /**
                 * Gets the value of the imageName property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getImageName() {
                    return imageName;
                }

                /**
                 * Sets the value of the imageName property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setImageName(String value) {
                    this.imageName = value;
                }

                /**
                 * Gets the value of the instanceType property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getInstanceType() {
                    return instanceType;
                }

                /**
                 * Sets the value of the instanceType property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setInstanceType(String value) {
                    this.instanceType = value;
                }

                public Template.Instances.Instance.Aws withImageName(String value) {
                    setImageName(value);
                    return this;
                }

                public Template.Instances.Instance.Aws withInstanceType(String value) {
                    setInstanceType(value);
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
             *         &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "imageName",
                "instanceType"
            })
            public static class Cloudstack
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(required = true)
                protected String imageName;
                @XmlElement(required = true)
                protected String instanceType;

                /**
                 * Gets the value of the imageName property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getImageName() {
                    return imageName;
                }

                /**
                 * Sets the value of the imageName property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setImageName(String value) {
                    this.imageName = value;
                }

                /**
                 * Gets the value of the instanceType property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getInstanceType() {
                    return instanceType;
                }

                /**
                 * Sets the value of the instanceType property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setInstanceType(String value) {
                    this.instanceType = value;
                }

                public Template.Instances.Instance.Cloudstack withImageName(String value) {
                    setImageName(value);
                    return this;
                }

                public Template.Instances.Instance.Cloudstack withInstanceType(String value) {
                    setInstanceType(value);
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
             *         &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "imageName",
                "instanceType"
            })
            public static class Nifty
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(required = true)
                protected String imageName;
                @XmlElement(required = true)
                protected String instanceType;

                /**
                 * Gets the value of the imageName property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getImageName() {
                    return imageName;
                }

                /**
                 * Sets the value of the imageName property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setImageName(String value) {
                    this.imageName = value;
                }

                /**
                 * Gets the value of the instanceType property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getInstanceType() {
                    return instanceType;
                }

                /**
                 * Sets the value of the instanceType property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setInstanceType(String value) {
                    this.instanceType = value;
                }

                public Template.Instances.Instance.Nifty withImageName(String value) {
                    setImageName(value);
                    return this;
                }

                public Template.Instances.Instance.Nifty withInstanceType(String value) {
                    setInstanceType(value);
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
             *         &lt;element name="imageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="instanceType" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "imageName",
                "instanceType"
            })
            public static class Vmware
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(required = true)
                protected String imageName;
                @XmlElement(required = true)
                protected String instanceType;

                /**
                 * Gets the value of the imageName property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getImageName() {
                    return imageName;
                }

                /**
                 * Sets the value of the imageName property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setImageName(String value) {
                    this.imageName = value;
                }

                /**
                 * Gets the value of the instanceType property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getInstanceType() {
                    return instanceType;
                }

                /**
                 * Sets the value of the instanceType property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setInstanceType(String value) {
                    this.instanceType = value;
                }

                public Template.Instances.Instance.Vmware withImageName(String value) {
                    setImageName(value);
                    return this;
                }

                public Template.Instances.Instance.Vmware withInstanceType(String value) {
                    setInstanceType(value);
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

    }

}
