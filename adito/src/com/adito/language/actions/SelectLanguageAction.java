
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.language.actions;

import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.SystemProperties;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.core.ErrorConstants;
import com.adito.core.actions.DefaultAction;
import com.adito.security.SessionInfo;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * Action to set the default per session locale.
 */
public class SelectLanguageAction extends DefaultAction {

    final static Log log = LogFactory.getLog(SelectLanguageAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String referer = DAVUtilities.encodePath(CoreUtil.getRequestReferer(request), false);
        if(referer == null) {
            throw new CoreException(ErrorConstants.ERR_MISSING_REQUEST_PARAMETER, ErrorConstants.CATEGORY_NAME, "referer");
        }
        String localeCode = request.getParameter("locale");
        if(localeCode == null) {
            throw new CoreException(ErrorConstants.ERR_MISSING_REQUEST_PARAMETER, ErrorConstants.CATEGORY_NAME, "locale");
        }
        
        /* Tokenize the locale parameter so we only get the first line. This prevents
         * a header injection exploit as the (not validated) locale gets added as 
         * a cookie.
         */
        StringTokenizer t = new StringTokenizer(localeCode);
        String locale = t.nextToken();
        
        // Parse the locale code
        String country = "";
        String variant = "";
        String lang = locale;   
        int idx = locale.indexOf("_");
        if(idx != -1) {
            country = lang.substring(idx + 1);
            lang = lang.substring(0, idx);
        }
        idx = country.indexOf('_');
        if(idx != -1) {
            variant = country.substring(idx + 1);
            country = country.substring(0, idx);
        }
        
        // Store the new locale in the session and set a persistant cookie
        Locale l = new Locale(lang, country, variant);
        request.getSession().setAttribute(Globals.LOCALE_KEY, l);
        Cookie cookie = new Cookie(SystemProperties.get("adito.cookie", "SSLX_SSESHID") + "_LANG", locale.toString());
        cookie.setMaxAge(60 * 60 * 24 * 7); // a week
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
        return referer == null ? mapping.findForward("home") : new ActionForward(referer, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

}
