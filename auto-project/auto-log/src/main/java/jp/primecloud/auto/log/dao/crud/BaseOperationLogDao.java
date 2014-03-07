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

import jp.primecloud.auto.log.entity.crud.OperationLog;

/**
 * <p>
 * operation_logに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseOperationLogDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param OLogNo OLogNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public OperationLog read(
            Long OLogNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OperationLog> readAll();

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param OLogDate OLogDate
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OperationLog> readByOLogDate(
            java.util.Date OLogDate
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param OLogDate OLogDate
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OperationLog> readByOLogDateAndUserNo(
            java.util.Date OLogDate,
            Long userNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param OLogNos OLogNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OperationLog> readInOLogNos(
            Collection<Long> OLogNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(OperationLog entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(OperationLog entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(OperationLog entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param OLogNo OLogNo
     */
    public void deleteByOLogNo(
            Long OLogNo
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param OLogDate OLogDate
     */
    public void deleteByOLogDate(
            java.util.Date OLogDate
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param OLogDate OLogDate
     * @param userNo userNo
     */
    public void deleteByOLogDateAndUserNo(
            java.util.Date OLogDate,
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
     * @param OLogNo OLogNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByOLogNo(
            Long OLogNo
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param OLogDate OLogDate
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByOLogDate(
            java.util.Date OLogDate
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param OLogDate OLogDate
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByOLogDateAndUserNo(
            java.util.Date OLogDate,
            Long userNo
        );

}
