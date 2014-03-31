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

import com.amazonaws.services.ec2.model.KeyPair;
import com.xerox.amazonws.ec2.KeyPairInfo;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class KeyPairConverter extends AbstractConverter<KeyPairInfo, KeyPair> {

    @Override
    protected KeyPair convertObject(KeyPairInfo from) {
        KeyPair to = new KeyPair();

        to.setKeyName(from.getKeyName());
        to.setKeyMaterial(from.getKeyMaterial());
        to.setKeyFingerprint(from.getKeyFingerprint());

        return to;
    }

}
