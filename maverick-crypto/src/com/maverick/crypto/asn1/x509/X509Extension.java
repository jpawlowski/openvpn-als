
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
			
package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.ASN1OctetString;
import com.maverick.crypto.asn1.DERBoolean;

/**
 * an object for the elements in the X.509 V3 extension block.
 */
public class X509Extension
{
    boolean             critical;
    ASN1OctetString      value;

    public X509Extension(
        DERBoolean              critical,
        ASN1OctetString         value)
    {
        this.critical = critical.isTrue();
        this.value = value;
    }

    public X509Extension(
        boolean                 critical,
        ASN1OctetString         value)
    {
        this.critical = critical;
        this.value = value;
    }

    public boolean isCritical()
    {
        return critical;
    }

    public ASN1OctetString getValue()
    {
        return value;
    }

    public int hashCode()
    {
        if (this.isCritical())
        {
            return this.getValue().hashCode();
        }


        return ~this.getValue().hashCode();
    }

    public boolean equals(
        Object  o)
    {
        if (o == null || !(o instanceof X509Extension))
        {
            return false;
        }

        X509Extension   other = (X509Extension)o;

        return other.getValue().equals(this.getValue())
            && (other.isCritical() == this.isCritical());
    }
}
