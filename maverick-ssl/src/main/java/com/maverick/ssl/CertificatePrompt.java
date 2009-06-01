
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
			
package com.maverick.ssl;

import com.maverick.crypto.asn1.x509.X509Certificate;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public abstract class CertificatePrompt {

    static CertificatePrompt prompt;

    public final static int CONNECT_THIS_TIME = 0;
    public final static int CONNECT_ALWAYS = 1;
    public final static int ABORT = 2;

    private X509Certificate x509;

    public CertificatePrompt() {
        super();
    }

    public static void setDefaultCertificatePrompt(CertificatePrompt prompt) {
        CertificatePrompt.prompt = prompt;
    }

    public abstract int untrusted(X509Certificate cert);

    public abstract int invalid(X509Certificate cert);

}
