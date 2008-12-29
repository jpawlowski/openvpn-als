
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
			
package com.adito.keystore.actions;

import java.io.File;
import java.io.FileWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.core.CoreUtil;
import com.adito.core.FileDownloadPageInterceptListener;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.keystore.CSRDownload;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.properties.Property;
import com.adito.security.SessionInfo;

/**
 * Action for downloading a certificate
 */
public class DownloadServerCertificateCSRAction extends AuthenticatedAction {

    /**
     * Constructor
     */
    public DownloadServerCertificateCSRAction() {
        super(PolicyConstants.KEYSTORE_RESOURCE_TYPE, new Permission[] {
                        PolicyConstants.PERM_CHANGE
        });
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        FileDownloadPageInterceptListener l = (FileDownloadPageInterceptListener) CoreUtil.getPageInterceptListenerById(request
                        .getSession(), "fileDownload");
        if (l == null) {
            l = new FileDownloadPageInterceptListener();
            CoreUtil.addPageInterceptListener(request.getSession(), l);
        }
        File f = new File(CoreUtil.getTempDownloadDirectory(getSessionInfo(request)), "server.csr");
        String pw = Property.getProperty(new ContextKey("webServer.keystore.sslCertificate.password"));
        String data = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).generateCSR(Property.getProperty(new ContextKey("webServer.alias")), pw);
        FileWriter fos = new FileWriter(f);
        fos.write(data);
        fos.flush();
        fos.close();
        l.addDownload(new CSRDownload(f, f.getName(), "application/octet-stream", mapping.findForward("success"),
                        "downloadCSR.message", "keystore"));
        return mapping.findForward("success");
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}
