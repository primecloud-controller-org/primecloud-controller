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
package jp.primecloud.auto.zabbix.model.trigger;

import java.io.Serializable;

/**
 * <p>
 * Triggerのエンティティクラスです。
 * </p>
 *
 */
public class Trigger implements Serializable {

    private static final long serialVersionUID = 1L;

    private String triggerid;

    private String expression;

    private String description;

    private String url;

    private Integer status;

    private Integer value;

    private Integer priority;

    private Integer lastchange;

    private Integer depLevel;

    private String comments;

    private String error;

    private Integer templateid;

    private Integer type;

    /**
     * triggeridを取得します。
     *
     * @return triggerid
     */
    public String getTriggerid() {
        return triggerid;
    }

    /**
     * triggeridを設定します。
     *
     * @param triggerid triggerid
     */
    public void setTriggerid(String triggerid) {
        this.triggerid = triggerid;
    }

    /**
     * expressionを取得します。
     *
     * @return expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * expressionを設定します。
     *
     * @param expression expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * descriptionを取得します。
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * descriptionを設定します。
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * urlを取得します。
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * statusを取得します。
     *
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * valueを取得します。
     *
     * @return value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * valueを設定します。
     *
     * @param value value
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * priorityを取得します。
     *
     * @return priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * priorityを設定します。
     *
     * @param priority priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * lastchangeを取得します。
     *
     * @return lastchange
     */
    public Integer getLastchange() {
        return lastchange;
    }

    /**
     * lastchangeを設定します。
     *
     * @param lastchange lastchange
     */
    public void setLastchange(Integer lastchange) {
        this.lastchange = lastchange;
    }

    /**
     * depLevelを取得します。
     *
     * @return depLevel
     */
    public Integer getDepLevel() {
        return depLevel;
    }

    /**
     * depLevelを設定します。
     *
     * @param depLevel depLevel
     */
    public void setDepLevel(Integer depLevel) {
        this.depLevel = depLevel;
    }

    /**
     * commentsを取得します。
     *
     * @return comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * commentsを設定します。
     *
     * @param comments comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * errorを取得します。
     *
     * @return error
     */
    public String getError() {
        return error;
    }

    /**
     * errorを設定します。
     *
     * @param error error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * templateidを取得します。
     *
     * @return templateid
     */
    public Integer getTemplateid() {
        return templateid;
    }

    /**
     * templateidを設定します。
     *
     * @param templateid templateid
     */
    public void setTemplateid(Integer templateid) {
        this.templateid = templateid;
    }

    /**
     * typeを取得します。
     *
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * typeを設定します。
     *
     * @param type type
     */
    public void setType(Integer type) {
        this.type = type;
    }

}
