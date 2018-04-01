package jp.primecloud.auto.nifty.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nifty.cloud.sdk.disk.model.Volume;
import com.nifty.cloud.sdk.disk.model.VolumeAttachment;

/**
 * <p>
 * nifty sdkのVolumeクラスからのデータ移送用クラス
 * </p>
 *
 */
public class VolumeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountingType;

    private List<VolumeAttachmentDto> attachments;

    private String availabilityZone;

    private Date createTime;

    private String diskType;

    private String nextMonthAccountingType;

    private String size;

    private String snapshotId;

    private String status;

    private String volumeId;

    /**
     * accountingTypeを取得します。
     * @return accountingType
     */
    public String getAccountingType() {
        return accountingType;
    }

    /**
     * accountingTypeを設定します。
     * @param accountingType accountingType
     */
    public void setAccountingType(String accountingType) {
        this.accountingType = accountingType;
    }

    /**
     * attachmentsを取得します。
     * @return attachments
     */
    public List<VolumeAttachmentDto> getAttachments() {
        return attachments;
    }

    /**
     * attachmentsを設定します。
     * @param attachments attachments
     */
    public void setAttachments(List<VolumeAttachmentDto> attachments) {
        this.attachments = attachments;
    }

    /**
     * availabilityZoneを取得します。
     * @return availabilityZone
     */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * availabilityZoneを設定します。
     * @param availabilityZone availabilityZone
     */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * createTimeを取得します。
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * createTimeを設定します。
     * @param createTime createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * diskTypeを取得します。
     * @return diskType
     */
    public String getDiskType() {
        return diskType;
    }

    /**
     * diskTypeを設定します。
     * @param diskType diskType
     */
    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    /**
     * nextMonthAccountingTypeを取得します。
     * @return nextMonthAccountingType
     */
    public String getNextMonthAccountingType() {
        return nextMonthAccountingType;
    }

    /**
     * nextMonthAccountingTypeを設定します。
     * @param nextMonthAccountingType nextMonthAccountingType
     */
    public void setNextMonthAccountingType(String nextMonthAccountingType) {
        this.nextMonthAccountingType = nextMonthAccountingType;
    }

    /**
     * sizeを取得します。
     * @return size
     */
    public String getSize() {
        return size;
    }

    /**
     * sizeを設定します。
     * @param size size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * snapshotIdを取得します。
     * @return snapshotId
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * snapshotIdを設定します。
     * @param snapshotId snapshotId
     */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * statusを取得します。
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
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

    public VolumeDto(Volume volume) {
        accountingType = volume.getAccountingType();
        attachments = new ArrayList<VolumeAttachmentDto>();
        if (volume.getAttachments() != null && volume.getAttachments().size() > 0) {
            for (VolumeAttachment attachment : volume.getAttachments()) {
                attachments.add(new VolumeAttachmentDto(attachment));
            }
        }
        availabilityZone = volume.getAvailabilityZone();
        createTime = volume.getCreateTime();
        diskType = volume.getDiskType();
        nextMonthAccountingType = volume.getNextMonthAccountingType();
        size = volume.getSize();
        snapshotId = volume.getSnapshotId();
        status = volume.getStatus();
        volumeId = volume.getVolumeId();
    }

}
