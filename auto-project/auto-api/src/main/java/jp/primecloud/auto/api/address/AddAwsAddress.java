package jp.primecloud.auto.api.address;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.address.AddAwsAddressResponse;
import jp.primecloud.auto.api.response.address.AwsAddressResponse;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;

@Path("/AddAwsAddress")
public class AddAwsAddress extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AddAwsAddressResponse addAwsAddress(@QueryParam(PARAM_NAME_PLATFORM_NO) String platformNo) {
        // 入力チェック
        ApiValidate.validatePlatformNo(platformNo);

        // ユーザ取得
        User user = checkAndGetUser();

        // プラットフォーム取得
        Platform platform = platformDao.read(Long.parseLong(platformNo));

        // プラットフォームが存在しない場合
        if (platform == null) {
            throw new AutoApplicationException("EAPI-100000", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        // プラットフォームを選択できない場合
        if (BooleanUtils.isNotTrue(platform.getSelectable())) {
            throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        // プラットフォームを利用できない場合
        if (!platformService.isUseablePlatforms(user.getUserNo(), platform)) {
            throw new AutoApplicationException("EAPI-000020", "Platform", PARAM_NAME_PLATFORM_NO, platformNo);
        }

        // Elastic IPを確保
        Long addressNo = iaasDescribeService.createAddress(user.getUserNo(), platform.getPlatformNo());

        // アドレス情報を取得
        AwsAddress awsAddress = awsAddressDao.read(addressNo);

        AwsAddressResponse awsAddressResponse = new AwsAddressResponse(awsAddress);
        AddAwsAddressResponse response = new AddAwsAddressResponse(awsAddressResponse);

        return response;
    }

}
