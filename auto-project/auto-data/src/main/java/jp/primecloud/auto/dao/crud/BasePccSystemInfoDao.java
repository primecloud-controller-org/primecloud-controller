package jp.primecloud.auto.dao.crud;
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


import jp.primecloud.auto.entity.crud.PccSystemInfo;

/**
 * <p>
 * PCC_SYSTEM_INFOに対応したDAOのベーススインタフェースです。
 * </p>
 *
 */
public interface BasePccSystemInfoDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param secretKey secretKey
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public PccSystemInfo read();

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(PccSystemInfo entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(PccSystemInfo entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(PccSystemInfo entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 全てのレコードの件数を取得します。
     *
     * @return 全てのレコードの件数。
     */
    public long countAll();
}
