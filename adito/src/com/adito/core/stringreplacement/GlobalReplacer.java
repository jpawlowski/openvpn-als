
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
			
package com.adito.core.stringreplacement;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adito.boot.ContextHolder;
import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.boot.SystemProperties;
import com.adito.core.CoreServlet;
import com.adito.properties.Property;

public class GlobalReplacer extends AbstractReplacementVariableReplacer {

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase(KeyStoreManager.DEFAULT_KEY_STORE)) {
            if (key.equals("untrustedCertificate")) {
                return String.valueOf(!KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE)
                                .isCertificateTrusted(Property.getProperty(new ContextKey("webServer.alias"))));
            } else {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
        } else if (type.equalsIgnoreCase("server")) {
            // NOTE 29/1/06 - BPS - This is here to maintain
            // compatibility with current extension descriptors
            if (key.equals("untrustedCertificate")) {
                return String.valueOf(!KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE)
                                .isCertificateTrusted(Property.getProperty(new ContextKey("webServer.alias"))));
            } else if (key.equals("disableNewSSLEngine")){
                return SystemProperties.get("adito.disableNewSSLEngine", "false");
            } else {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
        } else if (type.equalsIgnoreCase("context")) {
            if (key.equals("conf.dir")) {
            	// PLUNDEN: Removing the context
                // return ContextHolder.getContext().getConfDirectory().getAbsolutePath();
            	return CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB_INF/" + SystemProperties.get("adito.directories.conf", "conf");
                // end change
            } else if (key.equals("log.dir")) {
            	// PLUNDEN: Removing the context
                // return ContextHolder.getContext().getLogDirectory().getAbsolutePath();
            	return CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB_INF/" + SystemProperties.get("adito.directories.logs", "logs");
                // end change
            } else if (key.equals("db.dir")) {
            	// PLUNDEN: Removing the context
                // return ContextHolder.getContext().getDBDirectory().getAbsolutePath();
            	return CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB_INF/" + SystemProperties.get("adito.directories.db", "db");
                // end change
            } else if (key.equals("temp.dir")) {
            	// PLUNDEN: Removing the context
                // return ContextHolder.getContext().getTempDirectory().getAbsolutePath();
            	return CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB_INF/" + SystemProperties.get("adito.directories.tmp", "tmp");
                // end change
            } else if (key.equals("version")) {
            	// PLUNDEN: Removing the context
                // return ContextHolder.getContext().getVersion().toString();
            	return SystemProperties.get("adito.version", "0.9.1");
                // end change
            } else {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
        }
		return null;
	}
}