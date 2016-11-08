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
import jp.primecloud.auto.entity.crud.Platform;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.process.aws.AwsProcessClient;

import org.apache.commons.lang.BooleanUtils;

@Path("/DeleteAwsAddress")
public class DeleteAwsAddress extends ApiSupport {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DeleteAwsAddressResponse deleteAwsAddress(@QueryParam(PARAM_NAME_ADDRESS_NO) String addressNo) {
        // 入力チェック
        ApiValidate.validateAddressNo(addressNo);

        // ユーザ取得
        User user = checkAndGetUser();

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
        if (!platformService.isUsablePlatform(user.getUserNo(), platform)) {
            throw new AutoApplicationException("EAPI-000020", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // インスタンスに関連付けられている場合
        if (awsAddress.getInstanceNo() != null) {
            throw new AutoApplicationException("EAPI-100046", "AwsAddress", PARAM_NAME_ADDRESS_NO, addressNo);
        }

        // AWSアドレスを削除
        AwsProcessClient awsProcessClient = awsProcessClientFactory.createAwsProcessClient(user.getUserNo(),
                platform.getPlatformNo());
        awsAddressProcess.deleteAddress(awsProcessClient, awsAddress.getAddressNo());

        DeleteAwsAddressResponse response = new DeleteAwsAddressResponse();

        return response;
    }

}
