/* ========================================================================== *
 * Copyright (C) 2004-2005 Pier Fumagalli <http://www.betaversion.org/~pier/> *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package net.openvpn.als.vfs.webdav;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.Util;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.LaunchSessionFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.WebDAVAuthenticationModule;
import net.openvpn.als.vfs.VFSRepository;
import net.openvpn.als.vfs.VFSResource;
import net.openvpn.als.vfs.webdav.methods.COPY;
import net.openvpn.als.vfs.webdav.methods.DELETE;
import net.openvpn.als.vfs.webdav.methods.GET;
import net.openvpn.als.vfs.webdav.methods.HEAD;
import net.openvpn.als.vfs.webdav.methods.MKCOL;
import net.openvpn.als.vfs.webdav.methods.MOVE;
import net.openvpn.als.vfs.webdav.methods.OPTIONS;
import net.openvpn.als.vfs.webdav.methods.PROPFIND;
import net.openvpn.als.vfs.webdav.methods.PROPPATCH;
import net.openvpn.als.vfs.webdav.methods.PUT;

/**
 * <p>
 * The <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * transactions processor.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DAVProcessor {
    final static Log log = LogFactory.getLog(DAVProcessor.class);

    /**
     * <p>
     * All the implemented methods, comma separated.
     * </p>
     */
    public static final String METHODS = "COPY,DELETE,GET,HEAD,MKCOL,MOVE,OPTIONS,PROPFIND,PROPPATCH,PUT";

    private VFSRepository repository = null;
    private Map methods = null;

    private static Map methodImpls = new HashMap();

    public static void addDAVMethod(Class cls) {

        methodImpls.put(Util.getSimpleClassName(cls), cls);
    }

    public static void removeDAVMethod(Class cls) {
        methodImpls.remove(Util.getSimpleClassName(cls));
    }

    /**
     * <p>
     * Create a new {@link DAVProcessor} instance.
     * </p>
     * 
     * @param repository
     */
    public DAVProcessor(VFSRepository repository) {
        this.repository = repository;
        initialiseProcessor();
    }

    /**
     * 
     */
    public void initialiseProcessor() {
        this.methods = new HashMap();
        this.methods.put("DELETE", new DELETE());
        this.methods.put("GET", new GET());
        this.methods.put("HEAD", new HEAD());
        this.methods.put("MKCOL", new MKCOL());
        this.methods.put("MOVE", new MOVE());
        this.methods.put("OPTIONS", new OPTIONS());
        this.methods.put("PROPFIND", new PROPFIND());
        this.methods.put("PROPPATCH", new PROPPATCH());
        this.methods.put("PUT", new PUT());
        this.methods.put("COPY", new COPY());

        Map.Entry entry;
        for (Iterator it = methodImpls.entrySet().iterator(); it.hasNext();) {
            try {
                entry = (Map.Entry) it.next();
                this.methods.put(entry.getKey(), ((Class) entry.getValue()).newInstance());
            } catch (InstantiationException e) {
                log.error("Could not create DAVMethod implementation", e);
            } catch (IllegalAccessException e) {
                log.error("Could not create DAVMethod implementation", e);
            }
        }
    }

    /**
     * <p>
     * Process the specified {@link DAVTransaction} fully.
     * </p>
     * 
     * @throws IOException
     */
    public void process(DAVTransaction transaction) throws Exception {

        String method = transaction.getMethod();
        if (this.methods.containsKey(method)) {
            String path = transaction.getPath();
            if (log.isDebugEnabled())
                log.debug("Looking for resource for '" + path + "'");

            // Get the best launch session possible
            LaunchSession launchSession = null;
            String launchId = transaction.getRequest().getParameter(LaunchSession.LAUNCH_ID);
            if (launchId != null) {
                launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
            }
            
            /* If there is no specified launch session, create one. We can make it 
             * a tracked session later if the mount requires it
             */
            if(launchSession == null) {
                /* The session needn't be available for mounts that don't need
                 * OpenVPNALS Authorization
                 */
                launchSession = new LaunchSession(transaction.getSessionInfo());
            }
            

            /* Get the resource. The launch session may be converted to a tracked session
             * at this point
             */
            VFSResource resource = repository.getResource(launchSession, path, transaction.getCredentials());

            /*
             * This is to verify that we have access. We process the cause of
             * the exception to determine whether an authentication exception
             * should be thrown
             */
            resource.verifyAccess();

            /*
             * Now verify the launch session allows access
             */

            DAVMethod instance = ((DAVMethod) this.methods.get(method));

            // Some mounts do not require authentication
            instance.process(transaction, resource);
        } else {
            String message = "Method \"" + method + "\" not implemented";
            throw new DAVException(501, message);
        }
    }

    /**
     * @return
     */
    public VFSRepository getRepository() {
        return repository;
    }
}
