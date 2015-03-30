package jp.primecloud.auto.nifty.dto;

import java.io.Serializable;
import java.util.Date;

import com.nifty.cloud.sdk.disk.model.VolumeAttachment;


/**
 * <p>
 * nifty sdkのVolumeAttachmentクラスからのデータ移送用クラス
 * </p>
 *
 */
public class VolumeAttachmentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date attachTime;

    private Boolean deleteOnTermination;

    private String device;

    private String instanceId;

    private String state;

    private String volumeId;

    /**
     * attachTimeを取得します。
     * @return attachTime
     */
    public Date getAttachTime() {
        return attachTime;
    }

    /**
     * attachTimeを設定します。
     * @param attachTime attachTime
     */
    public void setAttachTime(Date attachTime) {
        this.attachTime = attachTime;
    }

    /**
     * deleteOnTerminationを取得します。
     * @return deleteOnTermination
     */
    public Boolean getDeleteOnTermination() {
        return deleteOnTermination;
    }

    /**
     * deleteOnTerminationを設定します。
     * @param deleteOnTermination deleteOnTermination
     */
    public void setDeleteOnTermination(Boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
    }

    /**
     * deviceを取得します。
     * @return device
     */
    public String getDevice() {
        return device;
    }

    /**
     * deviceを設定します。
     * @param device device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * instanceIdを取得します。
     * @return instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * instanceIdを設定します。
     * @param instanceId instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * stateを取得します。
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * stateを設定します。
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * volumeIdを取得します。
     * @return volumeId
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * volumeIdを設定します。
     * @param volumeId volumeId
     */
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public VolumeAttachmentDto(VolumeAttachment volumeAttachment){
        attachTime = volumeAttachment.getAttachTime();
        deleteOnTermination = volumeAttachment.getDeleteOnTermination();
        device = volumeAttachment.getDevice();
        instanceId = volumeAttachment.getInstanceId();
        state = volumeAttachment.getState();
        volumeId = volumeAttachment.getVolumeId();
    }
}
