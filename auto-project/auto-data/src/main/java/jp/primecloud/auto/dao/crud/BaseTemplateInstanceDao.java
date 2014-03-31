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

import jp.primecloud.auto.entity.crud.TemplateInstance;

/**
 * <p>
 * TEMPLATE_INSTANCEに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseTemplateInstanceDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param templateInstanceNo templateInstanceNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public TemplateInstance read(
            Long templateInstanceNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<TemplateInstance> readAll();

    /**
     * 一意キーに該当するレコードを検索します。
     *
     * @param templateNo templateNo
     * @param templateInstanceName templateInstanceName
     * @return 一意キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public TemplateInstance readByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param templateNo templateNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<TemplateInstance> readByTemplateNo(
            Long templateNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param platformNo platformNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<TemplateInstance> readByPlatformNo(
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param imageNo imageNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<TemplateInstance> readByImageNo(
            Long imageNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param templateInstanceNos templateInstanceNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<TemplateInstance> readInTemplateInstanceNos(
            Collection<Long> templateInstanceNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(TemplateInstance entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(TemplateInstance entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(TemplateInstance entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param templateInstanceNo templateInstanceNo
     */
    public void deleteByTemplateInstanceNo(
            Long templateInstanceNo
        );

    /**
     * 一意キーに該当するレコードを削除します。
     *
     * @param templateNo templateNo
     * @param templateInstanceName templateInstanceName
     */
    public void deleteByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param templateNo templateNo
     */
    public void deleteByTemplateNo(
            Long templateNo
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
     * @param templateInstanceNo templateInstanceNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByTemplateInstanceNo(
            Long templateInstanceNo
        );

    /**
     * 一意キーに該当するレコードの件数を取得します。
     *
     * @param templateNo templateNo
     * @param templateInstanceName templateInstanceName
     * @return 一意キーに該当するレコードの件数。
     */
    public long countByTemplateNoAndTemplateInstanceName(
            Long templateNo,
            String templateInstanceName
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param templateNo templateNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByTemplateNo(
            Long templateNo
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
