
				/*
 *  OpenVPNALS
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
			
package com.maverick.http;

import java.util.Enumeration;
import java.util.Vector;

import com.maverick.util.URLUTF8Encoder;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class GetMethod extends HttpMethod {

    /**
     * 
     * @param uri The full query string for this request including parameters
     *        i.e /index.php?foo=bar
     */
    public GetMethod(String uri) {
        super("GET", uri); //$NON-NLS-1$
    }

    public GetMethod(String name, String uri) {
        super(name, uri);
    }

    public String getURI() {

        String encodedParams = ""; //$NON-NLS-1$
        for (Enumeration e = getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            Vector values = getParameterValueList(name);
            for (Enumeration e2 = values.elements(); e2.hasMoreElements();) {
                String value = (String) e2.nextElement();
                encodedParams += (encodedParams.length() > 0 ? "&" : "") + URLUTF8Encoder.encode(name, true) + "=" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    URLUTF8Encoder.encode(value, true);
            }
        }

        if (super.getURI().indexOf('?') > 0 && encodedParams.length() > 0)
            return super.getURI() + "&" + encodedParams; //$NON-NLS-1$
        else
            return super.getURI() + (encodedParams.length() > 0 ? ("?" + encodedParams) : ""); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
