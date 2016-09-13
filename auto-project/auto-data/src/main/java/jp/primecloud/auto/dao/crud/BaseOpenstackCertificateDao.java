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
import jp.primecloud.auto.entity.crud.OpenstackCertificate;

/**
 * <p>
 * OPENSTACK_CERTIFICATEに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseOpenstackCertificateDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param userNo userNo
     * @param platformNo platformNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public OpenstackCertificate read(
            Long userNo,
            Long platformNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OpenstackCertificate> readAll();

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OpenstackCertificate> readByUserNo(
            Long userNo
        );

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param platformNo platformNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OpenstackCertificate> readByPlatformNo(
            Long platformNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param userNos userNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OpenstackCertificate> readInUserNos(
            Collection<Long> userNos
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param platformNos platformNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<OpenstackCertificate> readInPlatformNos(
            Collection<Long> platformNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(OpenstackCertificate entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(OpenstackCertificate entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(OpenstackCertificate entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param userNo userNo
     * @param platformNo platformNo
     */
    public void deleteByUserNoAndPlatformNo(
            Long userNo,
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param userNo userNo
     */
    public void deleteByUserNo(
            Long userNo
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
     * 全てのレコードの件数を取得します。
     *
     * @return 全てのレコードの件数。
     */
    public long countAll();

    /**
     * 主キーに該当するレコードの件数を取得します。
     *
     * @param userNo userNo
     * @param platformNo platformNo
     * @return 主キーに該当するレコードの件数。
     */
    public long countByUserNoAndPlatformNo(
            Long userNo,
            Long platformNo
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param userNo userNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByUserNo(
            Long userNo
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

}
