
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
 * <p>Java class for DescribeImagesResponseItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeImagesResponseItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="imageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="imageLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="imageState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="imageOwnerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isPublic" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="productCodes" type="{https://cp.cloud.nifty.com/api/1.3/}ProductCodesSetType" minOccurs="0"/>
 *         &lt;element name="architecture" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="imageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kernelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ramdiskId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="platform" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stateReason" type="{https://cp.cloud.nifty.com/api/1.3/}StateReasonType" minOccurs="0"/>
 *         &lt;element name="imageOwnerAlias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rootDeviceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rootDeviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="blockDeviceMapping" type="{https://cp.cloud.nifty.com/api/1.3/}BlockDeviceMappingType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeImagesResponseItemType", propOrder = {
    "imageId",
    "imageLocation",
    "imageState",
    "imageOwnerId",
    "isPublic",
    "productCodes",
    "architecture",
    "imageType",
    "kernelId",
    "ramdiskId",
    "platform",
    "stateReason",
    "imageOwnerAlias",
    "name",
    "description",
    "rootDeviceType",
    "rootDeviceName",
    "blockDeviceMapping"
})
public class DescribeImagesResponseItemType {

    @XmlElement(required = true)
    protected String imageId;
    protected String imageLocation;
    @XmlElement(required = true)
    protected String imageState;
    @XmlElement(required = true)
    protected String imageOwnerId;
    protected boolean isPublic;
    protected ProductCodesSetType productCodes;
    protected String architecture;
    protected String imageType;
    protected String kernelId;
    protected String ramdiskId;
    protected String platform;
    protected StateReasonType stateReason;
    protected String imageOwnerAlias;
    protected String name;
    protected String description;
    protected String rootDeviceType;
    protected String rootDeviceName;
    protected BlockDeviceMappingType blockDeviceMapping;

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
     * Gets the value of the imageLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageLocation() {
        return imageLocation;
    }

    /**
     * Sets the value of the imageLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageLocation(String value) {
        this.imageLocation = value;
    }

    /**
     * Gets the value of the imageState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageState() {
        return imageState;
    }

    /**
     * Sets the value of the imageState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageState(String value) {
        this.imageState = value;
    }

    /**
     * Gets the value of the imageOwnerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageOwnerId() {
        return imageOwnerId;
    }

    /**
     * Sets the value of the imageOwnerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageOwnerId(String value) {
        this.imageOwnerId = value;
    }

    /**
     * Gets the value of the isPublic property.
     * 
     */
    public boolean isIsPublic() {
        return isPublic;
    }

    /**
     * Sets the value of the isPublic property.
     * 
     */
    public void setIsPublic(boolean value) {
        this.isPublic = value;
    }

    /**
     * Gets the value of the productCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ProductCodesSetType }
     *     
     */
    public ProductCodesSetType getProductCodes() {
        return productCodes;
    }

    /**
     * Sets the value of the productCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductCodesSetType }
     *     
     */
    public void setProductCodes(ProductCodesSetType value) {
        this.productCodes = value;
    }

    /**
     * Gets the value of the architecture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the value of the architecture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArchitecture(String value) {
        this.architecture = value;
    }

    /**
     * Gets the value of the imageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the value of the imageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageType(String value) {
        this.imageType = value;
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
     * Gets the value of the platform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the value of the platform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlatform(String value) {
        this.platform = value;
    }

    /**
     * Gets the value of the stateReason property.
     * 
     * @return
     *     possible object is
     *     {@link StateReasonType }
     *     
     */
    public StateReasonType getStateReason() {
        return stateReason;
    }

    /**
     * Sets the value of the stateReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateReasonType }
     *     
     */
    public void setStateReason(StateReasonType value) {
        this.stateReason = value;
    }

    /**
     * Gets the value of the imageOwnerAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageOwnerAlias() {
        return imageOwnerAlias;
    }

    /**
     * Sets the value of the imageOwnerAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageOwnerAlias(String value) {
        this.imageOwnerAlias = value;
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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the rootDeviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootDeviceType() {
        return rootDeviceType;
    }

    /**
     * Sets the value of the rootDeviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootDeviceType(String value) {
        this.rootDeviceType = value;
    }

    /**
     * Gets the value of the rootDeviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootDeviceName() {
        return rootDeviceName;
    }

    /**
     * Sets the value of the rootDeviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootDeviceName(String value) {
        this.rootDeviceName = value;
    }

    /**
     * Gets the value of the blockDeviceMapping property.
     * 
     * @return
     *     possible object is
     *     {@link BlockDeviceMappingType }
     *     
     */
    public BlockDeviceMappingType getBlockDeviceMapping() {
        return blockDeviceMapping;
    }

    /**
     * Sets the value of the blockDeviceMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlockDeviceMappingType }
     *     
     */
    public void setBlockDeviceMapping(BlockDeviceMappingType value) {
        this.blockDeviceMapping = value;
    }

    public DescribeImagesResponseItemType withImageId(String value) {
        setImageId(value);
        return this;
    }

    public DescribeImagesResponseItemType withImageLocation(String value) {
        setImageLocation(value);
        return this;
    }

    public DescribeImagesResponseItemType withImageState(String value) {
        setImageState(value);
        return this;
    }

    public DescribeImagesResponseItemType withImageOwnerId(String value) {
        setImageOwnerId(value);
        return this;
    }

    public DescribeImagesResponseItemType withIsPublic(boolean value) {
        setIsPublic(value);
        return this;
    }

    public DescribeImagesResponseItemType withProductCodes(ProductCodesSetType value) {
        setProductCodes(value);
        return this;
    }

    public DescribeImagesResponseItemType withArchitecture(String value) {
        setArchitecture(value);
        return this;
    }

    public DescribeImagesResponseItemType withImageType(String value) {
        setImageType(value);
        return this;
    }

    public DescribeImagesResponseItemType withKernelId(String value) {
        setKernelId(value);
        return this;
    }

    public DescribeImagesResponseItemType withRamdiskId(String value) {
        setRamdiskId(value);
        return this;
    }

    public DescribeImagesResponseItemType withPlatform(String value) {
        setPlatform(value);
        return this;
    }

    public DescribeImagesResponseItemType withStateReason(StateReasonType value) {
        setStateReason(value);
        return this;
    }

    public DescribeImagesResponseItemType withImageOwnerAlias(String value) {
        setImageOwnerAlias(value);
        return this;
    }

    public DescribeImagesResponseItemType withName(String value) {
        setName(value);
        return this;
    }

    public DescribeImagesResponseItemType withDescription(String value) {
        setDescription(value);
        return this;
    }

    public DescribeImagesResponseItemType withRootDeviceType(String value) {
        setRootDeviceType(value);
        return this;
    }

    public DescribeImagesResponseItemType withRootDeviceName(String value) {
        setRootDeviceName(value);
        return this;
    }

    public DescribeImagesResponseItemType withBlockDeviceMapping(BlockDeviceMappingType value) {
        setBlockDeviceMapping(value);
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
