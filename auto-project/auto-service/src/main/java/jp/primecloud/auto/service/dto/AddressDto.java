package jp.primecloud.auto.service.dto;

import java.io.Serializable;

import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.CloudstackAddress;


public class AddressDto  implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = -725492940968709342L;

    private Long addressNo;

    private String publicIp;

    private Long userNo;

    private Long platformNo;

    private Long instanceNo;

    public Long getAddressNo() {
        return addressNo;
    }

    public void setAddressNo(Long addressNo) {
        this.addressNo = addressNo;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public Long getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    public Long getUserNo() {
        return userNo;
    }

    public void setUserNo(Long userNo) {
        this.userNo = userNo;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public AddressDto(){
        addressNo = null;
        publicIp = null;
        instanceNo = null;
        userNo = null;
        platformNo = null;
    }
    public AddressDto(AwsAddress address){
        addressNo = address.getAddressNo();
        publicIp = address.getPublicIp();
        instanceNo = address.getInstanceNo();
        userNo = address.getUserNo();
        platformNo = address.getPlatformNo();
    }

    public AddressDto(CloudstackAddress address){
        addressNo = address.getAddressNo();
        publicIp = address.getIpaddress();
        instanceNo = address.getInstanceNo();
        userNo = address.getAccount();
        platformNo = address.getPlatformNo();
    }
}
