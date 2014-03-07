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
package jp.primecloud.auto.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class CompositeEnumeration<E> implements Enumeration<E> {

    protected Enumeration<E>[] enumerations;

    private int index = 0;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param enumerations
     */
    public CompositeEnumeration(Enumeration<E>[] enumerations) {
        this.enumerations = enumerations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasMoreElements() {
        if (index >= enumerations.length) {
            return false;
        }

        Enumeration<E> enumeration = enumerations[index];
        if (enumeration.hasMoreElements()) {
            return true;
        }

        index++;
        return hasMoreElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException();
        }

        Enumeration<E> enumeration = enumerations[index];
        return enumeration.nextElement();
    }

}
