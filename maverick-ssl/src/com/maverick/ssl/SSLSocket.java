
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
			
package com.maverick.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import com.maverick.crypto.asn1.x509.X509Certificate;
import com.maverick.ssl.https.HttpsURLStreamHandlerFactory;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class SSLSocket extends Socket {

    SSLTransport transport;
    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SSLSocket.class);

    // #endif

    public SSLSocket(String host, int port) throws IOException, UnknownHostException, SSLException {
        this(host, port, false);
    }

    public SSLSocket(String host, int port, boolean delayHandshake) throws IOException, UnknownHostException, SSLException {
        super(host, port);
        transport = SSLTransportFactory.newInstance();
        if (!delayHandshake)
            startHandshake(null);
    }

    public InputStream getInputStream() throws IOException {
        return transport.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return transport.getOutputStream();
    }

    protected void startHandshake(SSLContext context) throws IOException, SSLException {
        transport.initialize(super.getInputStream(), super.getOutputStream());
    }

    protected InputStream getRawInputStream() throws IOException {
        return super.getInputStream();
    }

    protected OutputStream getRawOutputStream() throws IOException {
        return super.getOutputStream();
    }

    public static void main(String[] args) {

        try {

            HttpsURLStreamHandlerFactory.addHTTPSSupport();

            URL url = new URL("https://localhost"); //$NON-NLS-1$

            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(false);
            con.setAllowUserInteraction(false);

            con.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-shockwave-flash, */*"); //$NON-NLS-1$ //$NON-NLS-2$
            con.setRequestProperty("Accept-Encoding", "gzip, deflate"); //$NON-NLS-1$ //$NON-NLS-2$
            con.setRequestProperty("Accept-Language", "en-gb"); //$NON-NLS-1$ //$NON-NLS-2$
            con.setRequestProperty("Connection", "Keep-Alive"); //$NON-NLS-1$ //$NON-NLS-2$
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)"); //$NON-NLS-1$ //$NON-NLS-2$

            con.connect();

            InputStream in = con.getInputStream();
            int read;

            while ((read = in.read()) > -1) {
                System.out.write(read);
            }

        } catch (SSLIOException ex) {
            ex.getRealException().printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
