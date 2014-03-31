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
package jp.primecloud.auto.api;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.primecloud.auto.api.response.EventLogResponse;
import jp.primecloud.auto.api.response.ListEventLogResponse;
import jp.primecloud.auto.entity.crud.Component;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.Instance;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.dao.crud.EventLogDao.SearchCondition;
import jp.primecloud.auto.log.entity.crud.EventLog;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;



@Path("/ListEventLog")
public class ListEventLog extends ApiSupport {

    // 表示件数リミット
    private static final Integer LIMIT = 1000;

    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD_HHMMSS);

    private Comparator<EventLogResponse> comparatorEventLog;

    /**
     *
     * EventLog参照処理を行います
     *
     * @param isFromCurrent 現在時刻/日付フラグ
     * @param fromCurrent 現在時刻からの時間
     * @param fromDate 日付け（FROM）
     * @param toDate 日付け（TO）
     * @param farmNo ファームNo
     * @param logLevel ログレベル
     * @param componentNo コンポーネントNo
     * @param instanceNo インスタンスNo
     * @param orderName 並び替え
     * @param orderBy 昇順/降順
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ListEventLogResponse login(
	        @QueryParam(PARAM_NAME_USER) String userName,
	        @QueryParam(PARAM_NAME_IS_FROM_CURRENT) String isFromCurrent,
	        @QueryParam(PARAM_NAME_FROM_CURRENT) String fromCurrent,
	        @QueryParam(PARAM_NAME_FROM_DATE) String fromDate,
	        @QueryParam(PARAM_NAME_TO_DATE) String toDate,
	        @QueryParam(PARAM_NAME_FARM_NO) String farmNo,
	        @QueryParam(PARAM_NAME_LOG_LEVEL) String logLevel,
	        @QueryParam(PARAM_NAME_COMPONENT_NO) String componentNo,
	        @QueryParam(PARAM_NAME_INSTANCE_NO) String instanceNo,
	        @QueryParam(PARAM_NAME_ORDER_NAME) String orderName,
	        @QueryParam(PARAM_NAME_ORDER_ASC_DESC) String orderAscDesc){

        ListEventLogResponse response = new ListEventLogResponse();

        try {
            // 入力チェック
            // isFromCurrent
            ApiValidate.validateIsFromCurrent(isFromCurrent);
            if (Boolean.parseBoolean(isFromCurrent)) {
                // fromCurrent
                ApiValidate.validateFromCurrent(fromCurrent);
            } else {
                // fromDate
                ApiValidate.validateFromDate(fromDate);
                // toDate
                ApiValidate.validateToDate(toDate);
            }

            //farmNo
            if (StringUtils.isNotEmpty(farmNo) ||
                StringUtils.isNotEmpty(componentNo) ||
                StringUtils.isNotEmpty(instanceNo)) {
                ApiValidate.validateFarmNo(farmNo);
            }
            //logLevel
            ApiValidate.validateLogLevel(logLevel);
            //componentNo
            if (StringUtils.isNotEmpty(componentNo)) {
                ApiValidate.validateComponentNo(componentNo);
            }
            //instanceNo
            if (StringUtils.isNotEmpty(instanceNo)) {
                ApiValidate.validateInstanceNo(instanceNo);
            }
            //orderName
            if (StringUtils.isNotEmpty(orderAscDesc)) {
                ApiValidate.validateOrderName(orderName);
            }
            //orderAscDesc
            if (StringUtils.isNotEmpty(orderName)) {
                ApiValidate.validateOrderAscDesc(orderAscDesc);
            }

            if(StringUtils.isNotEmpty(farmNo)) {
                //ファーム取得
                Farm farm = farmDao.read(Long.parseLong(farmNo));
                if(farm == null) {
                    // ファームが存在しない
                    throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
                }
            }

            if (StringUtils.isNotEmpty(instanceNo)) {
                Instance instance = instanceDao.read(Long.parseLong(instanceNo));
                if (instance == null || BooleanUtils.isTrue(instance.getLoadBalancer())) {
                    // インスタンスが存在しない or インスタンスがロードバランサ
                    throw new AutoApplicationException("EAPI-100000", "Instance", PARAM_NAME_INSTANCE_NO, instanceNo);
                }

                if (BooleanUtils.isFalse(instance.getFarmNo().equals(Long.parseLong(farmNo)))) {
                    //ファームとインスタンスが一致しない
                    throw new AutoApplicationException("EAPI-100022", "Instance", farmNo, PARAM_NAME_INSTANCE_NO, instanceNo);
                }
            }

            if (StringUtils.isNotEmpty(componentNo)) {
                // コンポーネント取得
                Component component = componentDao.read(Long.parseLong(componentNo));
                if (component == null || BooleanUtils.isTrue(component.getLoadBalancer())) {
                    // コンポーネントが存在しない または ロードバランサーコンポーネント
                    throw new AutoApplicationException("EAPI-100000", "Component", PARAM_NAME_COMPONENT_NO, componentNo);
                }

                if (component.getFarmNo().equals(Long.parseLong(farmNo)) == false) {
                    //ファームとコンポーネントが一致しない
                    throw new AutoApplicationException("EAPI-100022", "Component", farmNo, PARAM_NAME_COMPONENT_NO, componentNo);
                }
            }

            // ユーザ取得
            User user = userDao.readByUsername(userName);

            //検索条件設定
            SearchCondition searchCondition = new SearchCondition();
            searchCondition.setUserNo(user.getUserNo());
            if (Boolean.parseBoolean(isFromCurrent)) {
                //fromCurrent
                searchCondition.setFromDate(getFromCurrentDate(fromCurrent));
            } else {
                //fromDate
                searchCondition.setFromDate(sdf.parse(fromDate));
                //toDate
                if (StringUtils.isNotEmpty(toDate)) {
                    searchCondition.setToDate(sdf.parse(toDate));
                }
            }

            if (StringUtils.isNotEmpty(farmNo)) {
                searchCondition.setFarmNo(Long.parseLong(farmNo));
            }

            if (StringUtils.isNotEmpty(logLevel)) {
                searchCondition.setLogLevel(getLogLevelCode(logLevel));
            }

            if (StringUtils.isNotEmpty(componentNo)) {
                searchCondition.setComponentNo(Long.parseLong(componentNo));
            }

            if (StringUtils.isNotEmpty(instanceNo)) {
                searchCondition.setInstanceNo(Long.parseLong(instanceNo));
            }
            searchCondition.setLimit(LIMIT);

            List<EventLog> eventLogs = eventLogService.readBySearchCondition(searchCondition);
            List<EventLogResponse> eventLogResponceList = new ArrayList<EventLogResponse>();
            for (EventLog eventLog: eventLogs) {
                EventLogResponse eventLogResponse = new EventLogResponse(eventLog);
                eventLogResponse.setDate(sdf.format(eventLog.getLogDate()));
                eventLogResponse.setLogLevel(EventLogLevel.fromCode(eventLog.getLogLevel()).name());
                eventLogResponceList.add(eventLogResponse);
            }

            //ソートクラス作成
            comparatorEventLog = getComparator(orderName, orderAscDesc);
            Collections.sort(eventLogResponceList, comparatorEventLog);

            response.setEventLogs(eventLogResponceList);
            response.setSuccess(true);
        } catch (Throwable e){
            String message = "";
            if (e instanceof AutoException || e instanceof AutoApplicationException) {
                message = e.getMessage();
            } else {
                message = MessageUtils.getMessage("EAPI-000000");
            }
            log.error(message, e);
            response.setMessage(message);
            response.setSuccess(false);
        }

        return  response;
    }

    private static Date getFromCurrentDate(String fromCurrent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (fromCurrent.endsWith("d")) {
            int date = Integer.parseInt(fromCurrent.split("d")[0]);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - date);
        } else if (fromCurrent.endsWith("H")) {
            int hour = Integer.parseInt(fromCurrent.split("H")[0]);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        } else if (fromCurrent.endsWith("m")) {
            int minute = Integer.parseInt(fromCurrent.split("m")[0]);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
        }
        return calendar.getTime();
    }

    private static Comparator<EventLogResponse> getComparator(final String orderName, final String orderAscDesc) {
        Comparator<EventLogResponse> comparator  = new Comparator<EventLogResponse>() {
            @Override
            public int compare(EventLogResponse o1, EventLogResponse o2) {
                int ascDesc = (orderAscDesc.equals("DESC")) ? -1: 1;
                if (StringUtils.isNotEmpty(orderName) && StringUtils.isNotEmpty(orderAscDesc)) {
                    if (orderName.equals("Date")) {
                        return o1.getDate().compareTo(o2.getDate()) * ascDesc;
                    } else if (orderName.equals("LogLevel")) {
                        return o1.getLogLevel().compareTo(o2.getLogLevel()) * ascDesc;
                    } else if (orderName.equals("FarmName")) {
                        return o1.getFarmName().compareTo(o2.getFarmName()) * ascDesc;
                    } else if (orderName.equals("ComponentName")) {
                        return o1.getComponentName().compareTo(o2.getComponentName()) * ascDesc;
                    } else if (orderName.equals("InstanceName")) {
                        return o1.getInstanceName().compareTo(o2.getInstanceName()) * ascDesc;
                    } else if(orderName.equals("Message")) {
                        return o1.getMessage().compareTo(o2.getMessage()) * ascDesc;
                    }
                    return o1.getDate().compareTo(o2.getDate()) * -1;
                } else {
                    return o1.getDate().compareTo(o2.getDate()) * -1;
                }
            }
        };
        return comparator;
    }

    private static Integer getLogLevelCode(String logLevel) {
        if  (EventLogLevel.OFF.name().equals(logLevel)) {
            return EventLogLevel.OFF.getCode();
        } else if (EventLogLevel.ERROR.name().equals(logLevel)) {
            return EventLogLevel.ERROR.getCode();
        } else if (EventLogLevel.WARN.name().equals(logLevel)) {
            return EventLogLevel.WARN.getCode();
        } else if (EventLogLevel.INFO.name().equals(logLevel)) {
            return EventLogLevel.INFO.getCode();
        }
        return EventLogLevel.ALL.getCode();
    }
}