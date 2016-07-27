package jp.primecloud.auto.api.address;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.ApiSupport;
import jp.primecloud.auto.api.ApiValidate;
import jp.primecloud.auto.api.response.address.DeleteAwsAddressResponse;
import jp.primecloud.auto.entity.crud.AwsAddress;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

@Path("/DeleteAwsAddress")
public class DeleteAwsAddress extends ApiSupport {

    // TODO: 引数のFarmNoは本来は不要だが、現在はIaaSGWの実装不良のために必要
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DeleteAwsAddressResponse deleteAwsAddress(@QueryParam(PARAM_NAME_ADDRESS_NO) String addressNo,
            @QueryParam(PARAM_NAME_FARM_NO) String farmNo) {
        // 入力チェック
        ApiValidate.validateAddressNo(addressNo);
        ApiValidate.validateFarmNo(farmNo);

        // ファーム取得
        Farm farm = getFarm(Long.parseLong(farmNo));

        // ユーザ取得
        User user = checkAndGetUser(farm);

        // アドレス情報取得
        AwsAddress awsAddress = awsAddressDao.read(Long.parseLong(addressNo));

        // アドレス情報が存在しない場合
        if (awsAddress == null) {
            throw new AutoApplicationException("EAPI-100000", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // 他ユーザのアドレスの場合
        if (!awsAddress.getUserNo().equals(user.getUserNo())) {
            throw new AutoApplicationException("EAPI-000020", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // プラットフォーム取得
        Platform platform = platformDao.read(awsAddress.getPlatformNo());

        // プラットフォームを選択できない場合
        if (BooleanUtils.isNotTrue(platform.getSelectable())) {
            throw new AutoApplicationException("EAPI-000020", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // プラットフォームを利用できない場合
        if (!platformService.isUseablePlatforms(user.getUserNo(), platform)) {
            throw new AutoApplicationException("EAPI-000020", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // インスタンスに関連付けられている場合
        if (StringUtils.isNotEmpty(awsAddress.getInstanceId())) {
            throw new AutoApplicationException("EAPI-100046", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // Elastic IPを削除
        iaasDescribeService.deleteAddress(user.getUserNo(), platform.getPlatformNo(), awsAddress.getAddressNo());

        DeleteAwsAddressResponse response = new DeleteAwsAddressResponse();

        return response;
    }

}
