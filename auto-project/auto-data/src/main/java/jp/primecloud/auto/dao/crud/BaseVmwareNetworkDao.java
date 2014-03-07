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

import jp.primecloud.auto.entity.crud.VmwareNetwork;

/**
 * <p>
 * VMWARE_NETWORKに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseVmwareNetworkDao {

    /**
     * 主キーに該当するレコードを検索します。
     * 
     * @param networkNo networkNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public VmwareNetwork read(
            Long networkNo
        );

    /**
     * 全てのレコードを検索します。
     * 
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<VmwareNetwork> readAll();

    /**
     * 一意キーに該当するレコードを検索します。
     *
     * @param platformNo platformNo
     * @param networkName networkName
     * @return 一意キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public VmwareNetwork readByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param platformNo platformNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<VmwareNetwork> readByPlatformNo(
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param farmNo farmNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<VmwareNetwork> readByFarmNo(
            Long farmNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     * 
     * @param networkNos networkNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<VmwareNetwork> readInNetworkNos(
            Collection<Long> networkNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     * 
     * @param entity エンティティ
     */
    public void create(VmwareNetwork entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     * 
     * @param entity エンティティ
     */
    public void update(VmwareNetwork entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     * 
     * @param entity エンティティ
     */
    public void delete(VmwareNetwork entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     * 
     * @param networkNo networkNo
     */
    public void deleteByNetworkNo(
            Long networkNo
        );

    /**
     * 一意キーに該当するレコードを削除します。
     *
     * @param platformNo platformNo
     * @param networkName networkName
     */
    public void deleteByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
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
     * @param farmNo farmNo
     */
    public void deleteByFarmNo(
            Long farmNo
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
     * @param networkNo networkNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByNetworkNo(
            Long networkNo
        );

    /**
     * 一意キーに該当するレコードの件数を取得します。
     *
     * @param platformNo platformNo
     * @param networkName networkName
     * @return 一意キーに該当するレコードの件数。
     */
    public long countByPlatformNoAndNetworkName(
            Long platformNo,
            String networkName
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
     * @param farmNo farmNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByFarmNo(
            Long farmNo
        );

}
