package jp.primecloud.auto.iaasgw;

import jp.primecloud.auto.dao.crud.AwsCertificateDao;
import jp.primecloud.auto.dao.crud.PlatformDao;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogger;


public class IaasGatewayFactory {

    protected Integer describeInterval;

    protected boolean sync = false;

    protected AwsCertificateDao awsCertificateDao;

    protected PlatformDao platformDao;

    protected EventLogger eventLogger;

    public IaasGatewayWrapper createIaasGateway(Long userNo, Long platformNo) {

        Platform platform = platformDao.read(platformNo);
        if (platform == null) {
            throw new AutoException("EPROCESS-000004", platformNo);
        }

        if ("aws".equals(platform.getPlatformType()) == false && "cloudstack".equals(platform.getPlatformType()) == false) {
            throw new AutoException("EPROCESS-000005", platform.getPlatformNo());
        }

        // AwsCertificateを取得
        //AwsCertificate awsCertificate = awsCertificateDao.read(userNo, platform.getNo());
        //if (awsCertificate == null) {
        //    throw new AutoException("EPROCESS-000006", userNo, platform.getNo());
        //}

        return new IaasGatewayWrapper(userNo, platform.getPlatformNo(), describeInterval, eventLogger);

    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    /**
     * describeIntervalを設定します。
     *
     * @param describeInterval describeInterval
     */
    public void setDescribeInterval(Integer describeInterval) {
        this.describeInterval = describeInterval;
    }

    /**
     * syncを設定します。
     *
     * @param sync sync
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    /**
     * awsCertificateDaoを設定します。
     *
     * @param awsCertificateDao awsCertificateDao
     */
    public void setAwsCertificateDao(AwsCertificateDao awsCertificateDao) {
        this.awsCertificateDao = awsCertificateDao;
    }

    /**
     * platformDaoを設定します。
     *
     * @param platformDao platformDao
     */
    public void setPlatformDao(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }
}
