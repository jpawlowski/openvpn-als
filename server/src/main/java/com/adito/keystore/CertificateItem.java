
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
			
package com.adito.keystore;

import java.security.cert.Certificate;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.KeyStoreManager;
import com.adito.core.CoreUtil;
import com.adito.install.actions.InstallAction;
import com.adito.table.TableItem;

/**
 * A {@link com.adito.table.TableItem} implementation used by displaying
 * certificates or keys in a {@link com.adito.boot.KeyStoreManager}.
 */
public class CertificateItem implements TableItem {
    
    // Private instance variables
    
    /**
     * Constant for the if the selected item is a key.
     */
    public static final String KEY = "key";
    /**
     * Constant for the if the selected item is a certificate.
     */
    public static final String CERTIFICATE = "cert";
    /**
     * Constant for the if the selected item is unknown.
     */
    public static final String UNKNOWN = "unknown";
    
    
    private String alias;
    private KeyStoreManager keyStoreManager;

    /**
     * Constructor
     * 
     * @param alias alias for certificate
     * @param certificate the certificate object itself
     * @param keyStoreManager the key store manager that stores this key
     * 
     */
    public CertificateItem(String alias, Certificate certificate, KeyStoreManager keyStoreManager) {
        super();
        this.alias  = alias;
        this.keyStoreManager = keyStoreManager;
    }
    
    /**
     * Get if this item is removable
     * 
     * @return removeable
     */
    public boolean isRemoveable() {
        return keyStoreManager.getRemoveable() && !(keyStoreManager.getName().equals(KeyStoreManager.DEFAULT_KEY_STORE) && alias.equals(InstallAction.SERVER_CERTIFICATE));
    }
    
    /**
     * Get the type of certificate as a string. Current possible values are
     * <b>Trusted Cert.</b>, <b>Certificate</b>, <b>Trusted Key</b>,
     * <b>Key</b> or <b>Unknown</b>.
     * 
     * @return type of certificate as a string.
     */
    public String getType() {
        try {
	        if(keyStoreManager.getKeyStore().isCertificateEntry(alias)) {	            
	            return CERTIFICATE ;
	        }
	        else if (keyStoreManager.getKeyStore().isKeyEntry(alias)){	            
	            return KEY ;
	        }
        }
        catch(Exception e) {
        }
        return UNKNOWN;
    }
    
    /**
     * Get the alias of the certificate / key
     * 
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /* (non-Javadoc)
     * @see com.adito.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch(col) {
            case 0:
                return alias;
            default:
                return getType();
        }
    }

    public String getSmallIconPath(HttpServletRequest request) {
        if (getType().equals(CERTIFICATE)){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/exportCertificate.gif";
        }
        else{
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/exportPrivate.gif";
        }
    }

}
