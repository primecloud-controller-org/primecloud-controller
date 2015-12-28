package jp.primecloud.auto.api.mock;

import jp.primecloud.auto.iaasgw.IaasGatewayFactory;
import jp.primecloud.auto.iaasgw.IaasGatewayWrapper;

public class MockIaasGatewayFactory extends IaasGatewayFactory {

    @Override
    public IaasGatewayWrapper createIaasGateway(Long userNo, Long platformNo) {
        return new MockIaasGatewayWrapper(userNo, platformNo, 15, null);
    }

}
