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
import jp.primecloud.auto.entity.crud.LoadBalancerListener;

/**
 * <p>
 * LOAD_BALANCER_LISTENERに対応したDAOのベースインタフェースです。
 * </p>
 *
 */
public interface BaseLoadBalancerListenerDao {

    /**
     * 主キーに該当するレコードを検索します。
     * 
     * @param loadBalancerNo loadBalancerNo
     * @param loadBalancerPort loadBalancerPort
     * @return 主キーに該当するレコードのエンティティ。レコードがない場合はnull。
     */
    public LoadBalancerListener read(
            Long loadBalancerNo,
            Integer loadBalancerPort
        );

    /**
     * 全てのレコードを検索します。
     * 
     * @return 全てのレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<LoadBalancerListener> readAll();

    /**
     * 与えられたキーに該当するレコードを検索します。
     *
     * @param loadBalancerNo loadBalancerNo
     * @return 与えられたキーに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<LoadBalancerListener> readByLoadBalancerNo(
            Long loadBalancerNo
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     * 
     * @param loadBalancerNos loadBalancerNoのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<LoadBalancerListener> readInLoadBalancerNos(
            Collection<Long> loadBalancerNos
        );

    /**
     * 主キーのコレクションに該当するレコードを検索します。
     * 
     * @param loadBalancerPorts loadBalancerPortのコレクション
     * @return 主キーのコレクションに該当するレコードのエンティティのリスト。レコードがない場合は空リスト。
     */
    public List<LoadBalancerListener> readInLoadBalancerPorts(
            Collection<Integer> loadBalancerPorts
        );

    /**
     * 与えられたエンティティの内容でレコードを挿入します。
     * 
     * @param entity エンティティ
     */
    public void create(LoadBalancerListener entity);

    /**
     * 与えられたエンティティの内容でレコードを更新します。
     * 
     * @param entity エンティティ
     */
    public void update(LoadBalancerListener entity);

    /**
     * 与えられたエンティティのレコードを削除します。
     * 
     * @param entity エンティティ
     */
    public void delete(LoadBalancerListener entity);

    /**
     * 全てのレコードを削除します。
     */
    public void deleteAll();

    /**
     * 主キーに該当するレコードを削除します。
     * 
     * @param loadBalancerNo loadBalancerNo
     * @param loadBalancerPort loadBalancerPort
     */
    public void deleteByLoadBalancerNoAndLoadBalancerPort(
            Long loadBalancerNo,
            Integer loadBalancerPort
        );

    /**
     * 与えられたキーに該当するレコードを削除します。
     *
     * @param loadBalancerNo loadBalancerNo
     */
    public void deleteByLoadBalancerNo(
            Long loadBalancerNo
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
     * @param loadBalancerNo loadBalancerNo
     * @param loadBalancerPort loadBalancerPort
     * @return 主キーに該当するレコードの件数。
     */
    public long countByLoadBalancerNoAndLoadBalancerPort(
            Long loadBalancerNo,
            Integer loadBalancerPort
        );

    /**
     * 与えられたキーに該当するレコードの件数を取得します。
     *
     * @param loadBalancerNo loadBalancerNo
     * @return 与えられたキーに該当するレコードの件数。
     */
    public long countByLoadBalancerNo(
            Long loadBalancerNo
        );

}
