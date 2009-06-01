
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
			
package com.maverick.crypto.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class ASN1Encodable
    implements DEREncodable
{
	public byte[] getEncoded()
		throws IOException
	{
		ByteArrayOutputStream	bOut = new ByteArrayOutputStream();
		ASN1OutputStream		aOut = new ASN1OutputStream(bOut);

		aOut.writeObject(this);

		return bOut.toByteArray();
	}

    public int hashCode()
    {
        return this.getDERObject().hashCode();
    }

    public boolean equals(
        Object  o)
    {
        if ((o == null) || !(o instanceof DEREncodable))
        {
            return false;
        }

        DEREncodable other = (DEREncodable)o;

        return this.getDERObject().equals(other.getDERObject());
    }

    public DERObject getDERObject()
    {
        return this.toASN1Object();
    }

    public abstract DERObject toASN1Object();
}
