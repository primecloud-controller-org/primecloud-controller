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
package jp.primecloud.auto.common.log;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class ExtendedPatternParser extends PatternParser {

    /**
     * コンストラクタです。
     *
     * @param pattern
     */
    public ExtendedPatternParser(String pattern) {
        super(pattern);
    }

    @Override
    protected void finalizeConverter(char c) {
        PatternConverter pc = null;

        switch (c) {
            case 'H':
                pc = new HeaderPatternConverter(formattingInfo);
                currentLiteral.setLength(0);
                break;
        }

        if (pc != null) {
            addConverter(pc);
        } else {
            super.finalizeConverter(c);
        }
    }

    protected static class HeaderPatternConverter extends PatternConverter {

        public HeaderPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        @Override
        protected String convert(LoggingEvent event) {
            return LoggingUtils.createLogHeader();
        }

    }

}
