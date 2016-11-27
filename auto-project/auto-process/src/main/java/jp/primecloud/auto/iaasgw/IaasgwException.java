/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.iaasgw;

import org.apache.commons.lang.StringUtils;



public class IaasgwException extends Exception {

    /** シリアル */
    private static final long serialVersionUID = 6353444266483618113L;


    /** エラータイプ **/
    /** 0:不明 **/
    public final int ERR_TYPE_UNKNOWN = 0;
    /** 1:CLOUDSTACK*/
    public final int ERR_TYPE_CLOUDSTACK = 1;
    /** 2:AWS*/
    public final int ERR_TYPE_AWS = 2;
    /** 11:Iaasgw内部エラー*/
    public final int ERR_TYPE_IAASGW = 11;
    /** 12:コンパイル系エラー*/
    public final int ERR_TYPE_COMPILE = 12;
    /** 13:設定ファイル系エラー*/
    public final int ERR_TYPE_SETUP = 13;
    /** 21:Libcloud系エラー*/
    public final int ERR_TYPE_LIBCLOUD = 21;

    /** エラータイプ格納用 **/
    private int errType = ERR_TYPE_UNKNOWN;


    public IaasgwException() {
    }

    public IaasgwException(String message) {
        super(message);
    }

    public int getErrType() {
        return errType;
    }

    @Override
    public String getMessage() {
        String mess = super.getMessage();


        if (StringUtils.isEmpty(mess)) return "";

        if (mess.indexOf("IaasException") > 0){
            /*
             * IaasException Iaasgw内部エラー
             * 例）IaasException: 'Launching instance failed.'
             * IaasException:を取り除いて返す
             */
            errType = ERR_TYPE_IAASGW;
            return mess.substring(mess.indexOf(":") + 1);

        }else if (mess.startsWith("Exception")) {
            /*
             * Cloudstack側がエラーを返した場合はこの戻り方となる
             * 例）Exception: {u'listsshkeypairsresponse': {u'errorcode': 401, u'errortext': u'unable to verify user credentials and/or request signature'}}
             * 1:「'」でスプリット
             * 2:分割した中から"errortext"を探す
             * 3：その2つ先をメッセージとして返す。
             * 注）1つ先は「: u」等、区切り文字が来る
             */
            String[] cserr = mess.split("'");
            for (int i=0; i < cserr.length; i++ ){
                if ("errortext".equals(cserr[i])){
                    errType = ERR_TYPE_CLOUDSTACK;
                    return cserr[i+2];
                }
            }
            /*
             * AWS側がエラーを返した場合はこの戻り方となる
             * 例）Exception: InvalidAMIID.NotFound: The image id 'ami-2269df23' does not exist
             * 1:「:」でスプリット
             * 2:分割した順にLibcloudException、AWSException、Messageとなるので3つめを返す
             * 注）分割数が足りない場合は無視する
             */
            String[] awserr = mess.split(":");
            if (awserr.length >= 3){
                errType = ERR_TYPE_AWS;
                return awserr[2];
            }

            /*
             * どちらにも一致しない場合はそのまま返す
             */
            return mess;

        }else if (mess.indexOf("AttributeError") > 0){
            /*
             * AttributeError コンパイル系エラー
             * 例）AttributeError: 'EC2IaasClient' object has no attribute 'waitGetPasswordData'
             * AttributeError:を取り除いて返す
             *
             */
            errType = ERR_TYPE_COMPILE;
            return mess.substring(mess.indexOf(":") + 1);

        }else if (mess.indexOf("NoOptionError") > 0) {
            /*
             * NoOptionError 設定ファイル系エラー
             * 例）NoOptionError: No option 'proxy' in section: 'PLATFORM_5'
             * NoOptionError:を取り除いて返す
             *
             */
            errType = ERR_TYPE_SETUP;
            return mess.substring(mess.indexOf(":") + 1);

        }else if (mess.indexOf("LibcloudError") > 0) {
            /*
             * LibcloudError Libcloud内部エラー
             * 例）LibcloudError: <LibcloudError in None 'Job did not complete in 1200 seconds'>
             * 明確な場合を除きLibcloudError:を取り除いて返す
             *
             */
            errType = ERR_TYPE_LIBCLOUD;
            //タイムアウト
            if (mess.indexOf("seconds") > 0){
                return "Completion timeout ! ";
            }

            return mess.substring(mess.indexOf(":") + 1);
        }

        //該当しない場合はそのまま
        return mess;
    }

}
