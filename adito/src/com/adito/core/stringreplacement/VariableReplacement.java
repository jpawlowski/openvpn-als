
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ReplacementEngine;
import com.adito.boot.Replacer;
import com.adito.boot.RequestHandlerRequest;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Policy;
import com.adito.security.SessionInfo;

public class VariableReplacement {
    final static Log log = LogFactory.getLog(VariableReplacement.class);

    final static Replacer GLOBAL_REPLACER = new GlobalReplacer();
    public final static String VARIABLE_PATTERN = "\\$\\{[^}]*\\}";

    private ExtensionBundle extensionBundle;
    private ExtensionDescriptor extensionDescriptor;
    private Map parameters;
    private RequestHandlerRequest request;
    private HttpServletRequest servletRequest;
    private SessionInfo session;
    Policy policy;
    private ReplacementEngine.Encoder encoder;
    private String username;
    private int realm;

    public void setUsernameAndRealm(String username, int Realm) {
        this.username = username;
        this.realm = realm;
    }

    public void setApplicationShortcut(ExtensionDescriptor extensionDescriptor, Map parameters) {
        this.extensionDescriptor = extensionDescriptor;
        this.parameters = parameters;
    }

    public void setEncoder(ReplacementEngine.Encoder encoder) {
        this.encoder = encoder;
    }

    public void setExtensionBundle(ExtensionBundle extensionBundle) {
        this.extensionBundle = extensionBundle;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public void setLaunchSession(LaunchSession launchSession) {
        this.policy = launchSession.getPolicy();
        this.session = launchSession.getSession();        
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public void setRequest(RequestHandlerRequest request) {
        this.request = request;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setSession(SessionInfo session) {
        this.session = session;
    }

    public long replace(InputStream in, OutputStream out, String charset) throws IOException {
    	if (log.isDebugEnabled())
    		log.debug("Replacing using streams, reading stream into memory");
        StringBuffer str = new StringBuffer(4096);
        byte[] buf = new byte[32768];
        int read;
        while ((read = in.read(buf)) > -1) {
            str.append(charset == null ? new String(buf, 0, read) : new String(buf, 0, read, charset));
            if (log.isDebugEnabled())
            	log.debug("Got block of " + read + ", waiting for next one");
        }
        if (log.isDebugEnabled())
        	log.debug("Read all blocks, performing replacement");
        byte[] b = charset == null ? replace(str.toString()).getBytes() : replace(str.toString()).getBytes(charset);
        if (log.isDebugEnabled())
        	log.debug("Writing replaced content back (" + b.length + " bytes)");
        out.write(b);
        return b.length;
    }

    public String replace(String input) {
        ReplacementEngine engine = new ReplacementEngine();
        if (encoder != null) {
            engine.setEncoder(encoder);
        }
        
        // User attributes and policy attributes can nest replacement variables so these must
        // be done first
        if (session != null) {
            engine.addPattern(VARIABLE_PATTERN, new UserAttributesReplacer(session.getUser().getPrincipalName(), session.getUser()
                            .getRealm().getResourceId()), null);
        } else {
            if (username != null) {
                engine.addPattern(VARIABLE_PATTERN, new UserAttributesReplacer(username, realm), null);
            }
        }
        
        if (policy != null) {
            engine.addPattern(VARIABLE_PATTERN, new PolicyAttributesReplacer(policy), null);
        }
        
        // Now do the variable patterns that may be nested in attributes
        
        engine.addPattern(VARIABLE_PATTERN, GLOBAL_REPLACER, null);
        if (extensionBundle != null)
            engine.addPattern(VARIABLE_PATTERN, new ExtensionBundleReplacer(extensionBundle), null);
        if (extensionDescriptor != null) {
            engine.addPattern(VARIABLE_PATTERN, new ExtensionDescriptorReplacer(extensionDescriptor, parameters), null);
        }
        if (request != null) {
            engine.addPattern(VARIABLE_PATTERN, new RequestHandlerRequestReplacer(request), null);
        }
        if (servletRequest != null) {
            engine.addPattern(VARIABLE_PATTERN, new ServletRequestReplacer(servletRequest), null);
        }
        if (session != null) {
            engine.addPattern(VARIABLE_PATTERN, new SessionInfoReplacer(session), null);
        }
        return engine.replace(input);
    }
}