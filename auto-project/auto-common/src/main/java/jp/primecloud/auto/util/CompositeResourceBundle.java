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
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class CompositeResourceBundle extends ResourceBundle {

    protected List<ResourceBundle> bundles;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param bundles
     */
    public CompositeResourceBundle(List<ResourceBundle> bundles) {
        this.bundles = bundles;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<String> getKeys() {
        Enumeration<?>[] enumerations = new Enumeration<?>[2];
        for (int i = 0; i < bundles.size(); i++) {
            enumerations[i] = bundles.get(i).getKeys();
        }
        return new CompositeEnumeration<String>((Enumeration<String>[]) enumerations);
    }

    @Override
    protected Object handleGetObject(String key) {
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return bundle.getObject(key);
            }
        }
        return null;
    }

}
