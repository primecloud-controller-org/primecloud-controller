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
package jp.primecloud.auto.aws.typica.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 * @param <FROM>
 * @param <TO>
 */
public abstract class AbstractConverter<FROM, TO> {

    public TO convert(FROM from) {
        if (from == null) {
            return null;
        }
        return convertObject(from);
    }

    public List<TO> convert(List<FROM> froms) {
        if (froms == null) {
            return null;
        }
        List<TO> tos = new ArrayList<TO>();
        for (FROM from : froms) {
            tos.add(convert(from));
        }
        return tos;
    }

    protected abstract TO convertObject(FROM from);

}
