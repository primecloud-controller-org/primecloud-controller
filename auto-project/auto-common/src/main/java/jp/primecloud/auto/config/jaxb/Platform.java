
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
 *         &lt;element name="internal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="aws" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="secure" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="euca" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="vpc" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="availabilityZone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="datacenter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="publicNetwork" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="privateNetwork" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="computeResource" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="instanceTypes">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="instanceType" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                                       &lt;element name="memory" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
 *         &lt;element name="nifty" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="wsdl" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *                   &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="zoneId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="networkid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="domainid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="proxy" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "internal",
    "aws",
    "vmware",
    "nifty",
    "cloudstack",
    "proxy"
})
@XmlRootElement(name = "platform")
public class Platform
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected long no;
    @XmlElement(required = true)
    protected String name;
    protected boolean internal;
    protected Platform.Aws aws;
    protected Platform.Vmware vmware;
    protected Platform.Nifty nifty;
    protected Platform.Cloudstack cloudstack;
    protected Platform.Proxy proxy;

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
     * Gets the value of the internal property.
     * 
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * Sets the value of the internal property.
     * 
     */
    public void setInternal(boolean value) {
        this.internal = value;
    }

    /**
     * Gets the value of the aws property.
     * 
     * @return
     *     possible object is
     *     {@link Platform.Aws }
     *     
     */
    public Platform.Aws getAws() {
        return aws;
    }

    /**
     * Sets the value of the aws property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platform.Aws }
     *     
     */
    public void setAws(Platform.Aws value) {
        this.aws = value;
    }

    /**
     * Gets the value of the vmware property.
     * 
     * @return
     *     possible object is
     *     {@link Platform.Vmware }
     *     
     */
    public Platform.Vmware getVmware() {
        return vmware;
    }

    /**
     * Sets the value of the vmware property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platform.Vmware }
     *     
     */
    public void setVmware(Platform.Vmware value) {
        this.vmware = value;
    }

    /**
     * Gets the value of the nifty property.
     * 
     * @return
     *     possible object is
     *     {@link Platform.Nifty }
     *     
     */
    public Platform.Nifty getNifty() {
        return nifty;
    }

    /**
     * Sets the value of the nifty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platform.Nifty }
     *     
     */
    public void setNifty(Platform.Nifty value) {
        this.nifty = value;
    }

    /**
     * Gets the value of the cloudstack property.
     * 
     * @return
     *     possible object is
     *     {@link Platform.Cloudstack }
     *     
     */
    public Platform.Cloudstack getCloudstack() {
        return cloudstack;
    }

    /**
     * Sets the value of the cloudstack property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platform.Cloudstack }
     *     
     */
    public void setCloudstack(Platform.Cloudstack value) {
        this.cloudstack = value;
    }

    /**
     * Gets the value of the proxy property.
     * 
     * @return
     *     possible object is
     *     {@link Platform.Proxy }
     *     
     */
    public Platform.Proxy getProxy() {
        return proxy;
    }

    /**
     * Sets the value of the proxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Platform.Proxy }
     *     
     */
    public void setProxy(Platform.Proxy value) {
        this.proxy = value;
    }

    public Platform withNo(long value) {
        setNo(value);
        return this;
    }

    public Platform withName(String value) {
        setName(value);
        return this;
    }

    public Platform withInternal(boolean value) {
        setInternal(value);
        return this;
    }

    public Platform withAws(Platform.Aws value) {
        setAws(value);
        return this;
    }

    public Platform withVmware(Platform.Vmware value) {
        setVmware(value);
        return this;
    }

    public Platform withNifty(Platform.Nifty value) {
        setNifty(value);
        return this;
    }

    public Platform withCloudstack(Platform.Cloudstack value) {
        setCloudstack(value);
        return this;
    }

    public Platform withProxy(Platform.Proxy value) {
        setProxy(value);
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
     *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="secure" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="euca" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="vpc" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="availabilityZone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "host",
        "port",
        "secure",
        "euca",
        "vpc",
        "availabilityZone"
    })
    public static class Aws
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String host;
        protected int port;
        protected boolean secure;
        protected boolean euca;
        protected boolean vpc;
        @XmlElement(required = true)
        protected String availabilityZone;

        /**
         * Gets the value of the host property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the value of the host property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHost(String value) {
            this.host = value;
        }

        /**
         * Gets the value of the port property.
         * 
         */
        public int getPort() {
            return port;
        }

        /**
         * Sets the value of the port property.
         * 
         */
        public void setPort(int value) {
            this.port = value;
        }

        /**
         * Gets the value of the secure property.
         * 
         */
        public boolean isSecure() {
            return secure;
        }

        /**
         * Sets the value of the secure property.
         * 
         */
        public void setSecure(boolean value) {
            this.secure = value;
        }

        /**
         * Gets the value of the euca property.
         * 
         */
        public boolean isEuca() {
            return euca;
        }

        /**
         * Sets the value of the euca property.
         * 
         */
        public void setEuca(boolean value) {
            this.euca = value;
        }

        /**
         * Gets the value of the vpc property.
         * 
         */
        public boolean isVpc() {
            return vpc;
        }

        /**
         * Sets the value of the vpc property.
         * 
         */
        public void setVpc(boolean value) {
            this.vpc = value;
        }

        /**
         * Gets the value of the availabilityZone property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvailabilityZone() {
            return availabilityZone;
        }

        /**
         * Sets the value of the availabilityZone property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvailabilityZone(String value) {
            this.availabilityZone = value;
        }

        public Platform.Aws withHost(String value) {
            setHost(value);
            return this;
        }

        public Platform.Aws withPort(int value) {
            setPort(value);
            return this;
        }

        public Platform.Aws withSecure(boolean value) {
            setSecure(value);
            return this;
        }

        public Platform.Aws withEuca(boolean value) {
            setEuca(value);
            return this;
        }

        public Platform.Aws withVpc(boolean value) {
            setVpc(value);
            return this;
        }

        public Platform.Aws withAvailabilityZone(String value) {
            setAvailabilityZone(value);
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
     *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="zoneId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="networkid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="domainid" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "ipAddress",
        "zoneId",
        "networkid",
        "domainid"
    })
    public static class Cloudstack
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String ipAddress;
        @XmlElement(required = true)
        protected String zoneId;
        @XmlElement(required = true)
        protected String networkid;
        @XmlElement(required = true)
        protected String domainid;

        /**
         * Gets the value of the ipAddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIpAddress() {
            return ipAddress;
        }

        /**
         * Sets the value of the ipAddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIpAddress(String value) {
            this.ipAddress = value;
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
         * Gets the value of the networkid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNetworkid() {
            return networkid;
        }

        /**
         * Sets the value of the networkid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNetworkid(String value) {
            this.networkid = value;
        }

        /**
         * Gets the value of the domainid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDomainid() {
            return domainid;
        }

        /**
         * Sets the value of the domainid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDomainid(String value) {
            this.domainid = value;
        }

        public Platform.Cloudstack withIpAddress(String value) {
            setIpAddress(value);
            return this;
        }

        public Platform.Cloudstack withZoneId(String value) {
            setZoneId(value);
            return this;
        }

        public Platform.Cloudstack withNetworkid(String value) {
            setNetworkid(value);
            return this;
        }

        public Platform.Cloudstack withDomainid(String value) {
            setDomainid(value);
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
     *         &lt;element name="wsdl" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "wsdl"
    })
    public static class Nifty
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String wsdl;

        /**
         * Gets the value of the wsdl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWsdl() {
            return wsdl;
        }

        /**
         * Sets the value of the wsdl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWsdl(String value) {
            this.wsdl = value;
        }

        public Platform.Nifty withWsdl(String value) {
            setWsdl(value);
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
     *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "host",
        "port",
        "user",
        "password"
    })
    public static class Proxy
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String host;
        protected int port;
        @XmlElement(required = true, nillable = true)
        protected String user;
        @XmlElement(required = true, nillable = true)
        protected String password;

        /**
         * Gets the value of the host property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the value of the host property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHost(String value) {
            this.host = value;
        }

        /**
         * Gets the value of the port property.
         * 
         */
        public int getPort() {
            return port;
        }

        /**
         * Sets the value of the port property.
         * 
         */
        public void setPort(int value) {
            this.port = value;
        }

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        }

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        }

        public Platform.Proxy withHost(String value) {
            setHost(value);
            return this;
        }

        public Platform.Proxy withPort(int value) {
            setPort(value);
            return this;
        }

        public Platform.Proxy withUser(String value) {
            setUser(value);
            return this;
        }

        public Platform.Proxy withPassword(String value) {
            setPassword(value);
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
     *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="datacenter" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="publicNetwork" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="privateNetwork" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="computeResource" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="instanceTypes">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="instanceType" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                             &lt;element name="memory" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
        "url",
        "username",
        "password",
        "datacenter",
        "publicNetwork",
        "privateNetwork",
        "computeResource",
        "instanceTypes"
    })
    public static class Vmware
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected String url;
        @XmlElement(required = true)
        protected String username;
        @XmlElement(required = true)
        protected String password;
        @XmlElement(required = true)
        protected String datacenter;
        @XmlElement(required = true)
        protected String publicNetwork;
        @XmlElement(required = true)
        protected String privateNetwork;
        @XmlElement(required = true)
        protected String computeResource;
        @XmlElement(required = true)
        protected Platform.Vmware.InstanceTypes instanceTypes;

        /**
         * Gets the value of the url property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the value of the url property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Gets the value of the username property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUsername() {
            return username;
        }

        /**
         * Sets the value of the username property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUsername(String value) {
            this.username = value;
        }

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Gets the value of the datacenter property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDatacenter() {
            return datacenter;
        }

        /**
         * Sets the value of the datacenter property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDatacenter(String value) {
            this.datacenter = value;
        }

        /**
         * Gets the value of the publicNetwork property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPublicNetwork() {
            return publicNetwork;
        }

        /**
         * Sets the value of the publicNetwork property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPublicNetwork(String value) {
            this.publicNetwork = value;
        }

        /**
         * Gets the value of the privateNetwork property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPrivateNetwork() {
            return privateNetwork;
        }

        /**
         * Sets the value of the privateNetwork property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPrivateNetwork(String value) {
            this.privateNetwork = value;
        }

        /**
         * Gets the value of the computeResource property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComputeResource() {
            return computeResource;
        }

        /**
         * Sets the value of the computeResource property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComputeResource(String value) {
            this.computeResource = value;
        }

        /**
         * Gets the value of the instanceTypes property.
         * 
         * @return
         *     possible object is
         *     {@link Platform.Vmware.InstanceTypes }
         *     
         */
        public Platform.Vmware.InstanceTypes getInstanceTypes() {
            return instanceTypes;
        }

        /**
         * Sets the value of the instanceTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link Platform.Vmware.InstanceTypes }
         *     
         */
        public void setInstanceTypes(Platform.Vmware.InstanceTypes value) {
            this.instanceTypes = value;
        }

        public Platform.Vmware withUrl(String value) {
            setUrl(value);
            return this;
        }

        public Platform.Vmware withUsername(String value) {
            setUsername(value);
            return this;
        }

        public Platform.Vmware withPassword(String value) {
            setPassword(value);
            return this;
        }

        public Platform.Vmware withDatacenter(String value) {
            setDatacenter(value);
            return this;
        }

        public Platform.Vmware withPublicNetwork(String value) {
            setPublicNetwork(value);
            return this;
        }

        public Platform.Vmware withPrivateNetwork(String value) {
            setPrivateNetwork(value);
            return this;
        }

        public Platform.Vmware withComputeResource(String value) {
            setComputeResource(value);
            return this;
        }

        public Platform.Vmware withInstanceTypes(Platform.Vmware.InstanceTypes value) {
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
         *         &lt;element name="instanceType" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *                   &lt;element name="memory" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
            "instanceType"
        })
        public static class InstanceTypes
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            protected List<Platform.Vmware.InstanceTypes.InstanceType> instanceType;

            /**
             * Gets the value of the instanceType property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the instanceType property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getInstanceType().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Platform.Vmware.InstanceTypes.InstanceType }
             * 
             * 
             */
            public List<Platform.Vmware.InstanceTypes.InstanceType> getInstanceType() {
                if (instanceType == null) {
                    instanceType = new ArrayList<Platform.Vmware.InstanceTypes.InstanceType>();
                }
                return this.instanceType;
            }

            public Platform.Vmware.InstanceTypes withInstanceType(Platform.Vmware.InstanceTypes.InstanceType... values) {
                if (values!= null) {
                    for (Platform.Vmware.InstanceTypes.InstanceType value: values) {
                        getInstanceType().add(value);
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
             *         &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}int"/>
             *         &lt;element name="memory" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
                "cpu",
                "memory"
            })
            public static class InstanceType
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(required = true)
                protected String name;
                protected int cpu;
                protected long memory;

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
                 * Gets the value of the cpu property.
                 * 
                 */
                public int getCpu() {
                    return cpu;
                }

                /**
                 * Sets the value of the cpu property.
                 * 
                 */
                public void setCpu(int value) {
                    this.cpu = value;
                }

                /**
                 * Gets the value of the memory property.
                 * 
                 */
                public long getMemory() {
                    return memory;
                }

                /**
                 * Sets the value of the memory property.
                 * 
                 */
                public void setMemory(long value) {
                    this.memory = value;
                }

                public Platform.Vmware.InstanceTypes.InstanceType withName(String value) {
                    setName(value);
                    return this;
                }

                public Platform.Vmware.InstanceTypes.InstanceType withCpu(int value) {
                    setCpu(value);
                    return this;
                }

                public Platform.Vmware.InstanceTypes.InstanceType withMemory(long value) {
                    setMemory(value);
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
