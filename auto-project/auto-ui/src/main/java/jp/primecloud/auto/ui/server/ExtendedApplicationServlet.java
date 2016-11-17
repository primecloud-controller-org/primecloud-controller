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
package jp.primecloud.auto.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.ui.util.ContextUtils;
import jp.primecloud.auto.ui.util.ViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
@SuppressWarnings("serial")
public class ExtendedApplicationServlet extends ApplicationServlet {

    private static final Log log = LogFactory.getLog(ExtendedApplicationServlet.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // セッションをコンテキストとして設定
        HttpSession session = request.getSession(true);
        ContextUtils.setSession(session);
        LoggingUtils.removeContext();
        LoggingUtils.setUserNo(ViewContext.getUserNo());
        LoggingUtils.setUserName(ViewContext.getUsername());
        LoggingUtils.setFarmNo(ViewContext.getFarmNo());
        LoggingUtils.setFarmName(ViewContext.getFarmName());
        LoggingUtils.setLoginUserNo(ViewContext.getLoginUser());

        try {
            super.service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            LoggingUtils.removeContext();
            ContextUtils.removeSession();
        }
    }

}
