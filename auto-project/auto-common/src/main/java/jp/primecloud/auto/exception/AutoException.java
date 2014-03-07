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
import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.util.MessageUtils;


/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class AutoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    private Object[] additions;

    private String message;

    private String simpleMessage;

    private List<String> details;

    /**
     * コンストラクタです。
     *
     * @param code
     */
    public AutoException(String code, Object... additions) {
        this(code, null, additions);
    }

    /**
     * コンストラクタです。
     *
     * @param cause
     */
    public AutoException(String code, Throwable cause, Object... additions) {
        super(cause);
        this.code = code;
        this.additions = additions;

        simpleMessage = MessageUtils.getMessage(code, additions);
        message = "[" + code + "] " + simpleMessage;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param detailInfo
     */
    public void addDetailInfo(String detailInfo) {
        if (this.details == null) {
            this.details = new ArrayList<String>();
        }
        this.details.add(detailInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);

            List<String> details = getDetails();
            if (details != null) {
                for (String detail : details) {
                    s.println("Detail: " + detail);
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

            List<String> details = getDetails();
            if (details != null) {
                for (String detail : details) {
                    s.println("Detail: " + detail);
                }
            }
        }
    }

    /**
     * codeを取得します。
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * additionsを取得します。
     *
     * @return additions
     */
    public Object[] getAdditions() {
        return additions;
    }

    /**
     * messageを取得します。
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * simpleMessageを取得します。
     *
     * @return simpleMessage
     */
    public String getSimpleMessage() {
        return simpleMessage;
    }

    /**
     * detailsを取得します。
     *
     * @return details
     */
    public List<String> getDetails() {
        return details;
    }

}
