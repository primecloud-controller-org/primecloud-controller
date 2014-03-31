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
package jp.primecloud.auto.zabbix.model;

/**
 * <p>
 * エラーのレスポンスを返すためのエンティティクラスです。
 * </p>
 *
 */
public class ResponseError {

    private int code;

    private String message;

    private String data;

    /**
     * codeを取得します。
     *
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * codeを設定します。
     *
     * @param code code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * messageを取得します。
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * messageを設定します。
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * dataを取得します。
     *
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * dataを設定します。
     *
     * @param data data
     */
    public void setData(String data) {
        this.data = data;
    }

}
