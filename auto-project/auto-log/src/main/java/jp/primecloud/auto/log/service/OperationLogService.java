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
package jp.primecloud.auto.log.service;

/**
 * <p>
 * 操作ログに関するロジックのインタフェースです。
 * </p>
 *
 */
public interface OperationLogService {

    /**
     * イベントログの検索結果を取得します。
     *
     * @param userNo
     * @param userName
     * @param farmNo
     * @param screen
     * @param operation
     * @param instanceNo
     * @param componentNo
     * @param loadBalancerNo
     * @param memo
     * @return 検索結果に該当するイベントログのリスト。無ければ空のリスト。
     */
    public void writeOperationLog(Long userNo, String userName, Long farmNo, String screen, String operation,
            Long instanceNo, Long componentNo, Long loadBalancerNo, String memo);

}
