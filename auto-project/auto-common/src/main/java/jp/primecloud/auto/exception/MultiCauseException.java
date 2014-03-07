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
package jp.primecloud.auto.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MultiCauseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Throwable[] causes;

    /**
     * コンストラクタです。
     *
     * @param causes
     */
    public MultiCauseException(Throwable[] causes) {
        super("Exception occurs by multiple cause.");
        this.causes = causes;
    }

    /**
     * コンストラクタです。
     *
     * @param message
     * @param causes
     */
    public MultiCauseException(String message, Throwable[] causes) {
        super(message);
        this.causes = causes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);

            Throwable[] causes = getCauses();
            if (causes != null) {
                for (int i = 0; i < causes.length; i++) {
                    s.println();
                    s.println("Cause" + i + ":");
                    causes[i].printStackTrace(s);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            super.printStackTrace(s);

            Throwable[] causes = getCauses();
            if (causes != null) {
                for (int i = 0; i < causes.length; i++) {
                    s.println();
                    s.println("Cause" + i + ":");
                    causes[i].printStackTrace(s);
                }
            }
        }
    }

    /**
     * causesを取得します。
     *
     * @return causes
     */
    public Throwable[] getCauses() {
        return causes;
    }

}
