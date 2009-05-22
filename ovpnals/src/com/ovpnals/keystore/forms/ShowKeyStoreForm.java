
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.keystore.forms;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.boot.KeyStoreManager;
import com.ovpnals.keystore.CertificateItem;
import com.ovpnals.setup.CertificatesTableItemModel;
import com.ovpnals.table.forms.AbstractPagerForm;

/**
 * Form for showing keystore content.
 */
public class ShowKeyStoreForm extends AbstractPagerForm {

    private static final long serialVersionUID = 2153872643060037840L;

    static Log log = LogFactory.getLog(ShowKeyStoreForm.class);

    // Private instance varaibles
    private String password;
    private String confirmPassword;
    private String selectedKeyStoreName;

    /**
     * Constructor
     */
    public ShowKeyStoreForm() {
        super(new CertificatesTableItemModel());
        selectedKeyStoreName = KeyStoreManager.DEFAULT_KEY_STORE;
    }
    
    /**
     * Get the password.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     * 
     * @param password old password
     */
    public void setPassword(String password) {
        this.password = password.trim();
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        super.reset(mapping, request);
        this.password = null;
        this.confirmPassword = null;
    }

    /**
     * @param selectedKeyStoreName
     */
    public void setSelectedKeyStoreName(String selectedKeyStoreName) {
        this.selectedKeyStoreName = selectedKeyStoreName;
    }

    /**
     * @return String
     */
    public String getSelectedKeyStoreName() {
        return selectedKeyStoreName;
    }

    /**
     * @return KeyStoreManager
     */
    public KeyStoreManager getSelectedKeyStore() {
        return KeyStoreManager.getInstance(getSelectedKeyStoreName());
    }

    /**
     * @return List
     */
    public List getKeyStores() {
        return KeyStoreManager.getKeyStores();
    }

    /**
     * @param session
     */
    public void initialize(HttpSession session) {
        super.initialize(session, "alias");
        CertificateItem[] c = getCertificateItems();
        if(c != null) {
            for(int i = 0 ; i < c.length; i++) {
                getModel().addItem(c[i]);
            }
        }
        getPager().rebuild(getFilterText());
    }

    /**
     * Return an array of {@link CertificateItem} objects contained within
     * the keystore.
     * 
     * @return array of {@link CertificateItem}s.
     */
    public CertificateItem[] getCertificateItems() {
        KeyStoreManager sel = getSelectedKeyStore(); 
        if (!sel.isKeyStoreEmpty()){
            Enumeration e = sel.getCertificateAliases();
            if(e != null) {
                CertificateItem[] cert = new CertificateItem[sel.getSize()];
                int i = 0;
                while(e.hasMoreElements()) {
                    String alias = (String) e.nextElement();
                    cert[i++] = new CertificateItem(alias, sel.getCertificate(alias), sel);
                }
                return cert;
            } 
        }
        return null;
    }

    /**
     * @return String
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @param confirmPassword
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if ("exportPrivate".equals(request.getParameter("actionTarget"))) {
            ActionErrors errors = new ActionErrors();
            try {
                if (getPassword().length() == 0) {
                    errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.noNewPassword"));
                } else if (!getPassword().equals(getConfirmPassword())) {
                    errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.newAndConfirmPasswordsDontMatch"));
                } 
            } catch (Exception e) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.validateFailed", e.getMessage()));
            }
            return errors;
        }
        return null;
    }

}