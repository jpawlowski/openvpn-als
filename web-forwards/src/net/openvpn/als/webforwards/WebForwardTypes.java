
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
			
package net.openvpn.als.webforwards;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.maverick.http.HttpAuthenticatorFactory;

public class WebForwardTypes {

    public static final String DEFAULT_ENCODING = "Default";

    public static final String FORM_SUBMIT_NONE = "NONE";
	public static final String FORM_SUBMIT_GET = "GET";
	public static final String FORM_SUBMIT_POST = "POST";
	public static final String FORM_SUBMIT_JAVASCRIPT = "JavaScript";
	
    public final static List WEB_FORWARD_TYPES = new ArrayList();
    public final static List PREFERED_SCHEMES = new ArrayList();
    public final static List FORM_SUBMIT_TYPES = new ArrayList();
    public final static List ENCODING_TYPES = new ArrayList();
    
    static {
        WEB_FORWARD_TYPES.add(new WebForwardTypeItem(WebForward.TYPE_TUNNELED_SITE, WebForward.ATTR_TUNNELED_SITE));
        WEB_FORWARD_TYPES.add(new WebForwardTypeItem(WebForward.TYPE_REPLACEMENT_PROXY, WebForward.ATTR_REPLACEMENT_PROXY));
        WEB_FORWARD_TYPES.add(new WebForwardTypeItem(WebForward.TYPE_PATH_BASED_REVERSE_PROXY, WebForward.ATTR_PATH_BASED_REVERSE_PROXY));
        WEB_FORWARD_TYPES.add(new WebForwardTypeItem(WebForward.TYPE_HOST_BASED_REVERSE_PROXY, WebForward.ATTR_HOST_BASED_REVERSE_PROXY));
        
        PREFERED_SCHEMES.add(new LabelValueBean(HttpAuthenticatorFactory.BASIC, HttpAuthenticatorFactory.BASIC));
        PREFERED_SCHEMES.add(new LabelValueBean(HttpAuthenticatorFactory.DIGEST, HttpAuthenticatorFactory.DIGEST));
        PREFERED_SCHEMES.add(new LabelValueBean(HttpAuthenticatorFactory.NTLM, HttpAuthenticatorFactory.NTLM));
        PREFERED_SCHEMES.add(new LabelValueBean(HttpAuthenticatorFactory.NONE, HttpAuthenticatorFactory.NONE));

        FORM_SUBMIT_TYPES.add(new LabelValueBean(FORM_SUBMIT_NONE, FORM_SUBMIT_NONE));
        FORM_SUBMIT_TYPES.add(new LabelValueBean(FORM_SUBMIT_POST, FORM_SUBMIT_POST));
        FORM_SUBMIT_TYPES.add(new LabelValueBean(FORM_SUBMIT_GET, FORM_SUBMIT_GET));
        FORM_SUBMIT_TYPES.add(new LabelValueBean(FORM_SUBMIT_JAVASCRIPT, FORM_SUBMIT_JAVASCRIPT));
        
        ENCODING_TYPES.add(new LabelValueBean(DEFAULT_ENCODING, DEFAULT_ENCODING));
        for(Iterator i = Charset.availableCharsets().values().iterator(); i.hasNext(); ) {
            Charset cs = (Charset)i.next();
            ENCODING_TYPES.add(new LabelValueBean(cs.toString(), cs.toString()));
        }     
    }
}
