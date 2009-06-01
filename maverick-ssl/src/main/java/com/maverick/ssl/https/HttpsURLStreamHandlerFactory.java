
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
			
package com.maverick.ssl.https;

import java.io.IOException;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpsURLStreamHandlerFactory implements URLStreamHandlerFactory {

    static String packagesPropertyName = "java.protocol.handler.pkgs"; //$NON-NLS-1$
    static String packageProperty = "com.maverick.ssl"; //$NON-NLS-1$

    public HttpsURLStreamHandlerFactory() {
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equalsIgnoreCase("https")) { //$NON-NLS-1$
            return new Handler();
        } else {
            return null;
        }
    }

    public static void addHTTPSSupport() throws IOException, SecurityException {

        String packagesProperty = System.getProperty(packagesPropertyName, ""); //$NON-NLS-1$
        int index = packagesProperty.indexOf(packageProperty + "|"); //$NON-NLS-1$
        if (index >= 0) {
            packagesProperty = packagesProperty.substring(0, index)
                + packagesProperty.substring(index + packageProperty.length() + 1);
        }
        index = packagesProperty.indexOf("|" + packageProperty); //$NON-NLS-1$
        if (index >= 0) {
            packagesProperty = packagesProperty.substring(0, index)
                + packagesProperty.substring(index + packageProperty.length() + 1);
        }
        if (packagesProperty.equals(packageProperty) || packagesProperty.equals("")) { //$NON-NLS-1$
            packagesProperty = packageProperty;
        } else {
            packagesProperty = packageProperty + "|" + packagesProperty; //$NON-NLS-1$
        }
        System.getProperties().put(packagesPropertyName, packagesProperty);
    }

    public static void removeHTTPSSupport() throws SecurityException {

        String packagesProperty = System.getProperty(packagesPropertyName, ""); //$NON-NLS-1$
        int index = packagesProperty.indexOf(packageProperty + "|"); //$NON-NLS-1$
        if (index >= 0) {
            packagesProperty = packagesProperty.substring(0, index)
                + packagesProperty.substring(index + packageProperty.length() + 1);
        }
        index = packagesProperty.indexOf("|" + packageProperty); //$NON-NLS-1$
        if (index >= 0) {
            packagesProperty = packagesProperty.substring(0, index)
                + packagesProperty.substring(index + packageProperty.length() + 1);
        }
        if (packagesProperty.equals(packageProperty)) {
            packagesProperty = ""; //$NON-NLS-1$
        }
        System.getProperties().put(packagesPropertyName, packagesProperty);
    }

    public static void setHTTPProxyHost(String host, int port, String username, String password, boolean isSecure)
                    throws SecurityException {
        System.getProperties().put(HttpsURLConnection.httpProxyHostProperty, host);
        System.getProperties().put(HttpsURLConnection.httpProxyPortProperty, "" + port); //$NON-NLS-1$
        System.getProperties().put(HttpsURLConnection.httpProxyUsernameProperty, "" + username); //$NON-NLS-1$
        System.getProperties().put(HttpsURLConnection.httpProxyPasswordProperty, "" + password); //$NON-NLS-1$
        System.getProperties().put(HttpsURLConnection.httpProxySecureProperty, "" + String.valueOf(isSecure)); //$NON-NLS-1$
    }

    public static void clearHTTPProxyHost() throws SecurityException {
        System.getProperties().remove(HttpsURLConnection.httpProxyHostProperty);
        System.getProperties().remove(HttpsURLConnection.httpProxyPortProperty);
        System.getProperties().remove(HttpsURLConnection.httpProxyUsernameProperty);
        System.getProperties().remove(HttpsURLConnection.httpProxyPasswordProperty);
        System.getProperties().remove(HttpsURLConnection.httpProxySecureProperty);
    }
}
