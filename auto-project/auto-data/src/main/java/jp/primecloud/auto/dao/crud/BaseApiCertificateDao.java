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
import jp.primecloud.auto.entity.crud.ApiCertificate;

/**
 * <p>
 * API_CERTIFICATEに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseApiCertificateDao {

    /**
     * 主キーに該当するレコードを検索します。
     *
     * @param userNo userNo
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public ApiCertificate read(
            Long userNo
        );

    /**
     * 全てのレコードを検索します。
     *
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<ApiCertificate> readAll();

    /**
     * 一意キーに該当するレコードを検索します。
     *
     * @param apiAccessId apiAccessId
     * @return 一意キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public ApiCertificate readByApiAccessId(
            String apiAccessId
        );

    /**
     * 一意キーに該当するレコードを検索します。
     *
     * @param apiSecretKey apiSecretKey
     * @return 一意キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public ApiCertificate readByApiSecretKey(
            String apiSecretKey
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     *
     * @param userNos userNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<ApiCertificate> readInUserNos(
            Collection<Long> userNos
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     *
     * @param entity エンティティ
     */
    public void create(ApiCertificate entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     *
     * @param entity エンティティ
     */
    public void update(ApiCertificate entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     *
     * @param entity エンティティ
     */
    public void delete(ApiCertificate entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     *
     * @param userNo userNo
     */
    public void deleteByUserNo(
            Long userNo
        );

    /**
     * 一意キーに該当するレコードを削除します。
     *
     * @param apiAccessId apiAccessId
     */
    public void deleteByApiAccessId(
            String apiAccessId
        );

    /**
     * 一意キーに該当するレコードを削除します。
     *
     * @param apiSecretKey apiSecretKey
     */
    public void deleteByApiSecretKey(
            String apiSecretKey
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
     * @return 主キーに該当するレコードの件数。
     */
    public long countByUserNo(
            Long userNo
        );

    /**
     * 一意キーに該当するレコードの件数を取得します。
     *
     * @param apiAccessId apiAccessId
     * @return 一意キーに該当するレコードの件数。
     */
    public long countByApiAccessId(
            String apiAccessId
        );

    /**
     * 一意キーに該当するレコードの件数を取得します。
     *
     * @param apiSecretKey apiSecretKey
     * @return 一意キーに該当するレコードの件数。
     */
    public long countByApiSecretKey(
            String apiSecretKey
        );

}
