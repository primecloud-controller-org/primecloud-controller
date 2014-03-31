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

import jp.primecloud.auto.entity.crud.ComponentType;

/**
 * <p>
 * COMPONENT_TYPEに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseComponentTypeDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param componentTypeNo componentTypeNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public ComponentType read(
            Long componentTypeNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<ComponentType> readAll();

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param componentTypeNos componentTypeNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<ComponentType> readInComponentTypeNos(
            Collection<Long> componentTypeNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(ComponentType entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(ComponentType entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(ComponentType entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param componentTypeNo componentTypeNo
     */
    public void deleteByComponentTypeNo(
            Long componentTypeNo
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
     * @param componentTypeNo componentTypeNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByComponentTypeNo(
            Long componentTypeNo
        );

}
