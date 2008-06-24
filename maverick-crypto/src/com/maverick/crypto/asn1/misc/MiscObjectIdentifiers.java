
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
			
package com.maverick.crypto.asn1.misc;

import com.maverick.crypto.asn1.DERObjectIdentifier;

public interface MiscObjectIdentifiers
{
    //
    // Netscape
    //       iso/itu(2) joint-assign(16) us(840) uscompany(1) netscape(113730) cert-extensions(1) }
    //
    static final String                 netscape                = "2.16.840.1.113730.1";
    static final DERObjectIdentifier    netscapeCertType        = new DERObjectIdentifier(netscape + ".1");
    static final DERObjectIdentifier    netscapeBaseURL         = new DERObjectIdentifier(netscape + ".2");
    static final DERObjectIdentifier    netscapeRevocationURL   = new DERObjectIdentifier(netscape + ".3");
    static final DERObjectIdentifier    netscapeCARevocationURL = new DERObjectIdentifier(netscape + ".4");
    static final DERObjectIdentifier    netscapeRenewalURL      = new DERObjectIdentifier(netscape + ".7");
    static final DERObjectIdentifier    netscapeCApolicyURL     = new DERObjectIdentifier(netscape + ".8");
    static final DERObjectIdentifier    netscapeSSLServerName   = new DERObjectIdentifier(netscape + ".12");
    static final DERObjectIdentifier    netscapeCertComment     = new DERObjectIdentifier(netscape + ".13");
    //
    // Verisign
    //       iso/itu(2) joint-assign(16) us(840) uscompany(1) verisign(113733) cert-extensions(1) }
    //
    static final String                 verisign                = "2.16.840.1.113733.1";

    //
    // CZAG - country, zip, age, and gender
    //
    static final DERObjectIdentifier    verisignCzagExtension   = new DERObjectIdentifier(verisign + ".6.3");
}
