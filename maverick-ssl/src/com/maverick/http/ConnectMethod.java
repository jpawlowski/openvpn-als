
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;

import com.maverick.ssl.SSLTransportFactory;

/**
 * Implementation of an {@link HttpMethod} that is specified to <i>OpenVPNALS</i>
 * and used for SSL-Tunnnels.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class ConnectMethod extends HttpMethod {

    String hostname;
    int port;
    boolean targetIsSecure;
    HttpMethod toExecute;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ConnectMethod.class);

    // #endif

    public ConnectMethod(String hostname, int port, boolean targetIsSecure, HttpMethod toExecute) {
        super("CONNECT", hostname + ":" + port); //$NON-NLS-1$ //$NON-NLS-2$
        this.hostname = hostname;
        this.port = port;
        this.targetIsSecure = targetIsSecure;
        this.toExecute = toExecute;
    }

    public ConnectMethod(String hostname, int port, boolean targetIsSecure) {
        this(hostname, port, targetIsSecure, null);
    }

    public String getVersion() {
        return "1.0"; //$NON-NLS-1$
    }

    public String getURI() {
        return hostname + ":" + port; //$NON-NLS-1$
    }

    public void processRequest(HttpRequest request) {

        request.setHeaderField("Proxy-Connection", "Keep-Alive"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    public HttpResponse execute(HttpRequest request, HttpConnection con) throws IOException {

        HttpResponse response = super.execute(request, con);

        // #ifdef DEBUG
        log.info(MessageFormat.format(Messages.getString("ConnectMethod.httpConnect"), new Object[] { hostname, new Integer(port), new Integer(response.getStatus()), response.getReason() })); //$NON-NLS-1$

        for (Enumeration e = response.getHeaderFieldNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            log.info(name + ": " + response.getHeaderField(name)); //$NON-NLS-1$
        }
        // #endif

        if (response.getStatus() == 200) {

            if (targetIsSecure)
                ((SocketWithLayeredTransport) response.getConnection().getSocket()).pushTransport(SSLTransportFactory.newInstance());

        }

        return response;
    }

}
