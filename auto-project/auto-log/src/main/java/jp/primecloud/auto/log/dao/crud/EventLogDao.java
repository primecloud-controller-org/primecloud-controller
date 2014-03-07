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
package jp.primecloud.auto.log.dao.crud;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jp.primecloud.auto.log.entity.crud.EventLog;


/**
 * <p>
 * EVENT_LOGに対応したDAOインタフェースです。
 * </p>
 *
 */
public interface EventLogDao extends BaseEventLogDao {

    /**
     * イベントログを検索します。
     *
     * @param searchCondition
     * @return 検索条件にマッチするエンティティのリストを返す。レコードがない場合は空リスト。
    */
    public List<EventLog> readBySearchCondition(SearchCondition searchCondition);

    /**
     * イベントログの検索結果の数を取得します。
     *
     * @param searchCondition
     * @return 検索条件にマッチする数。レコードがない場合は0。
     */
    public long countBySearchCondition(SearchCondition searchCondition);

    public class SearchCondition implements Serializable {

        private static final long serialVersionUID = 1L;

        private Date fromDate;

        private Date toDate;

        private Long farmNo;

        private Integer logLevel;

        private Long componentNo;

        private Long instanceNo;

        private Long userNo;

        private Integer limit;

        /**
         * fromDateを取得します。
         *
         * @return fromDate
         */
        public Date getFromDate() {
            return fromDate;
        }

        /**
         * fromDateを設定します。
         *
         * @param fromDate fromDate
         */
        public void setFromDate(Date fromDate) {
            this.fromDate = fromDate;
        }

        /**
         * toDateを取得します。
         *
         * @return toDate
         */
        public Date getToDate() {
            return toDate;
        }

        /**
         * toDateを設定します。
         *
         * @param toDate toDate
         */
        public void setToDate(Date toDate) {
            this.toDate = toDate;
        }

        /**
         * farmNoを取得します。
         *
         * @return farmNo
         */
        public Long getFarmNo() {
            return farmNo;
        }

        /**
         * farmNoを設定します。
         *
         * @param farmNo farmNo
         */
        public void setFarmNo(Long farmNo) {
            this.farmNo = farmNo;
        }

        /**
         * logLevelを取得します。
         *
         * @return logLevel
         */
        public Integer getLogLevel() {
            return logLevel;
        }

        /**
         * logLevelを設定します。
         *
         * @param logLevel logLevel
         */
        public void setLogLevel(Integer logLevel) {
            this.logLevel = logLevel;
        }

        /**
         * componentNoを取得します。
         *
         * @return componentNo
         */
        public Long getComponentNo() {
            return componentNo;
        }

        /**
         * componentNoを設定します。
         *
         * @param componentNo componentNo
         */
        public void setComponentNo(Long componentNo) {
            this.componentNo = componentNo;
        }

        /**
         * instanceNoを取得します。
         *
         * @return instanceNo
         */
        public Long getInstanceNo() {
            return instanceNo;
        }

        /**
         * instanceNoを設定します。
         *
         * @param instanceNo instanceNo
         */
        public void setInstanceNo(Long instanceNo) {
            this.instanceNo = instanceNo;
        }

        /**
         * userNoを取得します。
         *
         * @return userNo
         */
        public Long getUserNo() {
            return userNo;
        }

        /**
         * userNoを設定します。
         *
         * @param userNo userNo
         */
        public void setUserNo(Long userNo) {
            this.userNo = userNo;
        }

        /**
         * limitを取得します。
         *
         * @return limit
         */
        public Integer getLimit() {
            return limit;
        }

        /**
         * limitを設定します。
         *
         * @param limit limit
         */
        public void setLimit(Integer limit) {
            this.limit = limit;
        }

    }

}
