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
package jp.primecloud.auto.ui.validator;

import com.vaadin.data.validator.AbstractStringValidator;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class IntegerRangeValidator extends AbstractStringValidator {

    protected Integer from;

    protected Integer to;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param from
     * @param to
     * @param errorMessage
     */
    public IntegerRangeValidator(Integer from, Integer to, String errorMessage) {
        super(errorMessage);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValidString(String value) {
        try {
            int port = Integer.parseInt(value);
            if (from != null && port < from) {
                return false;
            }
            if (to != null && port > to) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
