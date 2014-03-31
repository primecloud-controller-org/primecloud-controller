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
package jp.primecloud.auto.dao.crud;

import java.util.Collection;
import java.util.List;

import jp.primecloud.auto.entity.crud.Instance;

/**
 * <p>
 * INSTANCEに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseInstanceDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param instanceNo instanceNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public Instance read(
            Long instanceNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<Instance> readAll();

    /**
     * 一意キーに該当するレコードを検索します。
     *
     * @param farmNo farmNo
     * @param instanceName instanceName
     * @return 一意キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public Instance readByFarmNoAndInstanceName(
            Long farmNo,
            String instanceName
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param farmNo farmNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<Instance> readByFarmNo(
            Long farmNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param platformNo platformNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<Instance> readByPlatformNo(
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param imageNo imageNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<Instance> readByImageNo(
            Long imageNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param instanceNos instanceNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<Instance> readInInstanceNos(
            Collection<Long> instanceNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(Instance entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(Instance entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(Instance entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param instanceNo instanceNo
     */
    public void deleteByInstanceNo(
            Long instanceNo
        );

    /**
     * 一意キーに該当するレコードを削除します。
     *
     * @param farmNo farmNo
     * @param instanceName instanceName
     */
    public void deleteByFarmNoAndInstanceName(
            Long farmNo,
            String instanceName
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param farmNo farmNo
     */
    public void deleteByFarmNo(
            Long farmNo
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param platformNo platformNo
     */
    public void deleteByPlatformNo(
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param imageNo imageNo
     */
    public void deleteByImageNo(
            Long imageNo
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
     * @param instanceNo instanceNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByInstanceNo(
            Long instanceNo
        );

    /**
     * 一意キーに該当するレコードの件数を取得します。
     *
     * @param farmNo farmNo
     * @param instanceName instanceName
     * @return 一意キーに該当するレコードの件数。
     */
    public long countByFarmNoAndInstanceName(
            Long farmNo,
            String instanceName
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param farmNo farmNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByFarmNo(
            Long farmNo
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param platformNo platformNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByPlatformNo(
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param imageNo imageNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByImageNo(
            Long imageNo
        );

}
