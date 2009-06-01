
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Base class for an application specific object
 */
public class DERApplicationSpecific
	extends DERObject
{
	private int		tag;
	private byte[]	octets;

	public DERApplicationSpecific(
		int		tag,
		byte[]	octets)
	{
		this.tag = tag;
		this.octets = octets;
	}

	public DERApplicationSpecific(
		int 							tag,
		DEREncodable 		object)
		throws IOException
	{
		this.tag = tag | DERTags.CONSTRUCTED;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DEROutputStream dos = new DEROutputStream(baos);

		dos.writeObject(object);

		this.octets = baos.toByteArray();
	}

	public boolean isConstructed()
	{
		return (tag & DERTags.CONSTRUCTED) != 0;
	}

	public byte[] getContents()
	{
		return octets;
	}

	public int getApplicationTag()
	{
		return tag & 0x1F;
	}

	public DERObject getObject()
		throws IOException
	{
		return new ASN1InputStream(new ByteArrayInputStream(getContents())).readObject();
	}

    /* (non-Javadoc)
     * @see org.bouncycastle.asn1.DERObject#encode(org.bouncycastle.asn1.DEROutputStream)
     */
    void encode(DEROutputStream out) throws IOException
    {
        out.writeEncoded(DERTags.APPLICATION | tag, octets);
    }
}
