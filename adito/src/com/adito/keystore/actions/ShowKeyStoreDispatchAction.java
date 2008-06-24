
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
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.FileDownloadPageInterceptListener;
import com.adito.keystore.CSRDownload;
import com.adito.keystore.forms.ShowKeyStoreForm;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.table.actions.AbstractPagerAction;

/**
 * Actions performed on the key stores
 */
public class ShowKeyStoreDispatchAction extends AbstractPagerAction {

    static Log log = LogFactory.getLog(ShowKeyStoreDispatchAction.class);


    /**
     * Construtor
     */
    public ShowKeyStoreDispatchAction() {
        super(PolicyConstants.KEYSTORE_RESOURCE_TYPE, new Permission[] {
            PolicyConstants.PERM_CHANGE
        });
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward selectKeyStore(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String sel = ((ShowKeyStoreForm)form).getSelectedItem();
        System.out.println(sel);
        return mapping.findForward("confirmRemoveCertificate");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        ActionMessages msgs = new ActionMessages();
        String sel = ((ShowKeyStoreForm)form).getSelectedItem();
        ((ShowKeyStoreForm)form).getSelectedKeyStore().deleteCertificate(sel);
        CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.KEYSTORE_CERTIFICATE_DELETED, sel, LogonControllerFactory.getInstance().getSessionInfo(request))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, sel));
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("keyStore.certificates.remove.message.certificateRemoved", sel));
        saveMessages(request, msgs);
        ActionForward fwd = mapping.findForward("reload");
        String orig = fwd.getPath();
        fwd = mapping.findForward("restartRequired"); 
        fwd = CoreUtil.addParameterToForward(fwd, "no", orig);
        return fwd;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","must-revalidate");
        CoreUtil.clearFlow(request);
        
        ((ShowKeyStoreForm) form).initialize(request.getSession());
        return mapping.findForward("display");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward exportCertificate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        String sel = ((ShowKeyStoreForm) form).getSelectedItem();
        KeyStore systemClientStore = ((ShowKeyStoreForm) form).getSelectedKeyStore().getKeyStore();
        FileDownloadPageInterceptListener l = (FileDownloadPageInterceptListener) CoreUtil.getPageInterceptListenerById(request
                        .getSession(), "fileDownload");
        if (l == null) {
            l = new FileDownloadPageInterceptListener();
            CoreUtil.addPageInterceptListener(request.getSession(), l);
        }
        File clientCertFile = new File(CoreUtil.getTempDownloadDirectory(getSessionInfo(request)), sel + ".cer");
        FileOutputStream out = new FileOutputStream(clientCertFile);
        X509Certificate cert = (X509Certificate) systemClientStore.getCertificate(sel);
        out.write(cert.getEncoded());
        out.flush();
        out.close();
        l.addDownload(new CSRDownload(clientCertFile, clientCertFile.getName(), "application/octet-stream", mapping
                        .findForward("success"), "exportCertificate.message", "keystore", sel));
        return mapping.findForward("success");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward exportPrivate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                        HttpServletResponse response) throws Exception {
        String sel = ((ShowKeyStoreForm) form).getSelectedItem();
        
        KeyStore systemClientStore = ((ShowKeyStoreForm) form).getSelectedKeyStore().getKeyStore();
        FileDownloadPageInterceptListener l = (FileDownloadPageInterceptListener) CoreUtil.getPageInterceptListenerById(request
                        .getSession(), "fileDownload");
        if (l == null) {
            l = new FileDownloadPageInterceptListener();
            CoreUtil.addPageInterceptListener(request.getSession(), l);
        }
        File clientCertFile = new File(CoreUtil.getTempDownloadDirectory(getSessionInfo(request)), sel + ".p12");
        FileOutputStream out = new FileOutputStream(clientCertFile);
        char[] password = ((ShowKeyStoreForm) form).getSelectedKeyStore().getKeyStorePassword().toCharArray();
        if (systemClientStore.isKeyEntry(sel)){
            PrivateKey keypair = ((ShowKeyStoreForm) form).getSelectedKeyStore().getPrivateKey(sel,
                            password);
          KeyStore userStore = KeyStore.getInstance("PKCS12", "BC");
          userStore.load(null, null);
          userStore.setKeyEntry(sel, keypair, ((ShowKeyStoreForm) form).getPassword().toCharArray(), ((ShowKeyStoreForm) form).getSelectedKeyStore().getCertificateChain(sel));
          userStore.store(out, ((ShowKeyStoreForm) form).getPassword().toCharArray());
          out.close();
        }
        l.addDownload(new CSRDownload(clientCertFile, clientCertFile.getName(), "application/octet-stream", mapping.findForward("success"),
                        "exportPrivateKey.message", "keystore", sel));
        return mapping.findForward("success");
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward displayPromptForPrivateKeyPassphrase(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return mapping.findForward("displayPromptForPrivateKeyPassphrase");
    }
    
    /**
     * Cancel and logout.
     * 
     * @param mapping mappng
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward finished(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("cancel");
    }

}
