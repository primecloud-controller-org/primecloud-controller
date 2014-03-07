package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformNifty;



@XmlRootElement(name="PlatformNiftyResponse")
@XmlType(propOrder = {})
public class PlatformNiftyResponse {

    public PlatformNiftyResponse() {}

    public PlatformNiftyResponse(PlatformNifty nifty) {

    }
}