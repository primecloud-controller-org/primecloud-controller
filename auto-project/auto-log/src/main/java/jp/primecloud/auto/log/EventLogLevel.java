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
package jp.primecloud.auto.log;

/**
 * <p>
 * イベントログのログレベルを管理する列挙型クラスです。
 * ログレベルごとにコードを設定します。
 * </p>
 *
 */
public enum EventLogLevel {

    OFF(100),

    ERROR(40),

    WARN(30),

    INFO(20),

    DEBUG(10),

    ALL(0);

    private Integer code;

    private EventLogLevel(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static EventLogLevel fromCode(Integer code) {
        for (EventLogLevel eventLogLevel : values()) {
            if (eventLogLevel.getCode().equals(code)) {
                return eventLogLevel;
            }
        }
        throw new IllegalArgumentException(code + " is illegal.");
    }

}
