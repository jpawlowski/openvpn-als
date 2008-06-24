
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
			
package com.maverick.http;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.maverick.crypto.digests.Hash;
import com.maverick.crypto.digests.MD5Digest;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class DigestAuthentication extends HttpAuthenticator {

    Hashtable params;

    /**
     * Hexa values used when creating 32 character long digest in HTTP
     * DigestScheme in case of authentication.
     * 
     * @see #encode(byte[])
     */
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /** Whether the digest authentication process is complete */
    private boolean complete;

    // TODO: supply a real nonce-count, currently a server will interprete a
    // repeated request as a replay
    private static final String NC = "00000001"; // nonce-count is always 1 //$NON-NLS-1$
                                                    // //$NON-NLS-1$
    private static final int QOP_MISSING = 0;
    private static final int QOP_AUTH_INT = 1;
    private static final int QOP_AUTH = 2;

    private int qopVariant = QOP_MISSING;
    private String cnonce;
    boolean isAuthenticated = false;

    public DigestAuthentication(String uri, String host, int port, boolean secure) {
        super("Digest", uri, host, port, secure); //$NON-NLS-1$
    }

    public void setChallenge(String challenge) {
        try {
            params = ParameterParser.extractParams(challenge);

            boolean unsupportedQop = false;
            // qop parsing
            String qop = (String) params.get("qop"); //$NON-NLS-1$
            if (qop != null) {
                StringTokenizer tok = new StringTokenizer(qop, ","); //$NON-NLS-1$
                while (tok.hasMoreTokens()) {
                    String variant = tok.nextToken().trim();
                    if (variant.equals("auth")) { //$NON-NLS-1$
                        qopVariant = QOP_AUTH;
                        break; // that's our favourite, because auth-int is
                                // unsupported
                    } else if (variant.equals("auth-int")) { //$NON-NLS-1$
                        qopVariant = QOP_AUTH_INT;
                    } else {
                        unsupportedQop = true;
                    }
                }
            }

            this.cnonce = createCnonce();
        } catch (IOException ex) {
        }
    }

    public boolean isStateless() {
        return false;
    }

    /**
     * authenticate
     * 
     * @param request HttpRequest
     * @throws IOException
     * @todo Implement this com.maverick.proxy.http.HttpAuthenticator method
     */
    public void authenticate(HttpRequest request, HttpMethod method) throws IOException {
        params.put("methodname", method.getName()); //$NON-NLS-1$
        params.put("uri", method.getURI()); //$NON-NLS-1$

        String serverDigest = createDigest(credentials.getUsername(), credentials.getPassword(), "US-ASCII"); //$NON-NLS-1$

        request.setHeaderField(authorizationHeader, "Digest " //$NON-NLS-1$
            + createDigestHeader(credentials.getUsername(), serverDigest));
    }

    /**
     * processResponse
     * 
     * @param response HttpResponse
     * @todo Implement this com.maverick.proxy.http.HttpAuthenticator method
     */
    public int processResponse(HttpResponse response) {
        return (hasCompleted = response.getStatus() >= 200 && response.getStatus() < 400) ? AUTHENTICATION_COMPLETED
            : AUTHENTICATION_FAILED;
    }

    private String createDigest(String uname, String pwd, String charset) throws IOException {

        final String digAlg = "MD5"; //$NON-NLS-1$

        // Collecting required tokens
        String uri = (String) params.get("uri"); //$NON-NLS-1$
        String realm = (String) params.get("realm"); //$NON-NLS-1$
        String nonce = (String) params.get("nonce"); //$NON-NLS-1$
        String qop = (String) params.get("qop"); //$NON-NLS-1$
        String method = (String) params.get("methodname"); //$NON-NLS-1$
        String algorithm = (String) params.get("algorithm"); //$NON-NLS-1$

        // If an algorithm is not specified, default to MD5.
        if (algorithm == null) {
            algorithm = "MD5"; //$NON-NLS-1$
        }

        if (qopVariant == QOP_AUTH_INT) {
            throw new IOException(Messages.getString("DigestAuthentication.unsupportedQop")); //$NON-NLS-1$
        }

        Hash hash = new Hash(new MD5Digest());

        // 3.2.2.2: Calculating digest
        StringBuffer tmp = new StringBuffer(uname.length() + realm.length() + pwd.length() + 2);
        tmp.append(uname);
        tmp.append(':');
        tmp.append(realm);
        tmp.append(':');
        tmp.append(pwd);
        // unq(username-value) ":" unq(realm-value) ":" passwd
        String a1 = tmp.toString();
        // a1 is suitable for MD5 algorithm
        if (algorithm.equals("MD5-sess")) { //$NON-NLS-1$
            // H( unq(username-value) ":" unq(realm-value) ":" passwd )
            // ":" unq(nonce-value)
            // ":" unq(cnonce-value)

            hash.putBytes(a1.getBytes("US-ASCII")); //$NON-NLS-1$
            String tmp2 = encode(hash.doFinal());
            StringBuffer tmp3 = new StringBuffer(tmp2.length() + nonce.length() + cnonce.length() + 2);
            tmp3.append(tmp2);
            tmp3.append(':');
            tmp3.append(nonce);
            tmp3.append(':');
            tmp3.append(cnonce);
            a1 = tmp3.toString();
        } else if (!algorithm.equals("MD5")) { //$NON-NLS-1$

        }

        hash.reset();
        hash.putBytes(a1.getBytes("US-ASCII")); //$NON-NLS-1$
        String md5a1 = encode(hash.doFinal());

        String a2 = null;
        if (qopVariant == QOP_AUTH_INT) {
            // we do not have access to the entity-body or its hash
            // TODO: add Method ":" digest-uri-value ":" H(entity-body)
        } else {
            a2 = method + ":" + uri; //$NON-NLS-1$
        }

        hash.reset();
        hash.putBytes(a2.getBytes("US-ASCII")); //$NON-NLS-1$
        String md5a2 = encode(hash.doFinal());

        // 3.2.2.1
        String serverDigestValue;
        if (qopVariant == QOP_MISSING) {

            StringBuffer tmp2 = new StringBuffer(md5a1.length() + nonce.length() + md5a2.length());
            tmp2.append(md5a1);
            tmp2.append(':');
            tmp2.append(nonce);
            tmp2.append(':');
            tmp2.append(md5a2);
            serverDigestValue = tmp2.toString();
        } else {

            String qopOption = getQopVariantString();
            StringBuffer tmp2 = new StringBuffer(md5a1.length() + nonce.length() + NC.length() + cnonce.length()
                + qopOption.length() + md5a2.length() + 5);
            tmp2.append(md5a1);
            tmp2.append(':');
            tmp2.append(nonce);
            tmp2.append(':');
            tmp2.append(NC);
            tmp2.append(':');
            tmp2.append(cnonce);
            tmp2.append(':');
            tmp2.append(qopOption);
            tmp2.append(':');
            tmp2.append(md5a2);
            serverDigestValue = tmp2.toString();
        }

        hash.reset();
        hash.putBytes(serverDigestValue.getBytes("US-ASCII")); //$NON-NLS-1$
        String serverDigest = encode(hash.doFinal());

        return serverDigest;
    }

    public static String createCnonce() {

        String cnonce;
        Hash hash = new Hash(new MD5Digest());

        cnonce = Long.toString(System.currentTimeMillis());

        hash.putBytes(cnonce.getBytes());
        cnonce = encode(hash.doFinal());

        return cnonce;
    }

    private static String encode(byte[] binaryData) {

        if (binaryData.length != 16) {
            return null;
        }

        char[] buffer = new char[32];
        for (int i = 0; i < 16; i++) {
            int low = (int) (binaryData[i] & 0x0f);
            int high = (int) ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[(i * 2) + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);
    }

    private String createDigestHeader(String uname, String digest) throws IOException {

        StringBuffer sb = new StringBuffer();
        String uri = (String) params.get("uri"); //$NON-NLS-1$
        String realm = (String) params.get("realm"); //$NON-NLS-1$
        String nonce = (String) params.get("nonce"); //$NON-NLS-1$
        String nc = (String) params.get("nc"); //$NON-NLS-1$
        String opaque = (String) params.get("opaque"); //$NON-NLS-1$
        String response = digest;
        String qop = (String) params.get("qop"); //$NON-NLS-1$
        String algorithm = (String) params.get("algorithm"); //$NON-NLS-1$

        sb.append("username=\"" + uname + "\"") //$NON-NLS-1$ //$NON-NLS-2$
            .append(", realm=\"" + realm + "\"") //$NON-NLS-1$ //$NON-NLS-2$
            .append(", nonce=\"" + nonce + "\"").append(", uri=\"" + uri + "\"") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            .append(", response=\"" + response + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        if (qopVariant != QOP_MISSING) {
            sb.append(", qop=\"" + getQopVariantString() + "\"") //$NON-NLS-1$ //$NON-NLS-2$
                .append(", nc=" + NC) //$NON-NLS-1$
                .append(", cnonce=\"" + cnonce + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (algorithm != null) {
            sb.append(", algorithm=\"" + algorithm + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (opaque != null) {
            sb.append(", opaque=\"" + opaque + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return sb.toString();
    }

    private String getQopVariantString() {
        String qopOption;
        if (qopVariant == QOP_AUTH_INT) {
            qopOption = "auth-int"; //$NON-NLS-1$
        } else {
            qopOption = "auth"; //$NON-NLS-1$
        }
        return qopOption;
    }

	public String getInformation() {
		return (String)params.get("realm");
	}

}
