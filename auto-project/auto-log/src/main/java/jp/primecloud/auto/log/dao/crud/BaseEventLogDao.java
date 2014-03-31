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

import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.log.entity.crud.EventLog;

/**
 * <p>
 * EVENT_LOGに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseEventLogDao {

    /**
     * 主キーに該当するレコードを検索します。
     * 
     * @param logNo logNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public EventLog read(
            Long logNo
        );

    /**
     * 全てのレコードを検索します。
     * 
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<EventLog> readAll();

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param logDate logDate
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<EventLog> readByLogDate(
            java.util.Date logDate
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param logDate logDate
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<EventLog> readByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     * 
     * @param logNos logNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<EventLog> readInLogNos(
            Collection<Long> logNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     * 
     * @param entity エンティティ
     */
    public void create(EventLog entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     * 
     * @param entity エンティティ
     */
    public void update(EventLog entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     * 
     * @param entity エンティティ
     */
    public void delete(EventLog entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     * 
     * @param logNo logNo
     */
    public void deleteByLogNo(
            Long logNo
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param logDate logDate
     */
    public void deleteByLogDate(
            java.util.Date logDate
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param logDate logDate
     * @param userNo userNo
     */
    public void deleteByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        );

    /**
     * 全てのレコードの件数を取得します。
     * 
     * @return 全てのレコードの件数。
     */
    public long countAll();

    /**
     * 主キーに該当するレコードの件数を取得します。
     * 
     * @param logNo logNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByLogNo(
            Long logNo
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param logDate logDate
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByLogDate(
            java.util.Date logDate
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param logDate logDate
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByLogDateAndUserNo(
            java.util.Date logDate, 
            Long userNo
        );

}
