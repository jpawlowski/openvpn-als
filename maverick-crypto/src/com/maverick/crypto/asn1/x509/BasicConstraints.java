
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


import java.math.BigInteger;

import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.ASN1Encodable;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.DERObject;
import com.maverick.crypto.asn1.DERSequence;

public class BasicConstraints
    extends ASN1Encodable
{
    DERBoolean  cA = new DERBoolean(false);
    DERInteger  pathLenConstraint = null;

    public static BasicConstraints getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static BasicConstraints getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof BasicConstraints)
        {
            return (BasicConstraints)obj;
        }
        else if (obj instanceof ASN1Sequence)
        {
            return new BasicConstraints((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("unknown object in factory");
    }

    public BasicConstraints(
        ASN1Sequence   seq)
    {
    	if (seq.size() == 0)
    	{
    		this.cA = null;
    		this.pathLenConstraint = null;
    	}
    	else
    	{
        	this.cA = (DERBoolean)seq.getObjectAt(0);
	        if (seq.size() > 1)
	        {
	            this.pathLenConstraint = (DERInteger)seq.getObjectAt(1);
	        }
    	}
    }

    /**
     * @deprecated use one of the other two unambigous constructors.
     * @param cA
     * @param pathLenConstraint
     */
    public BasicConstraints(
        boolean cA,
        int     pathLenConstraint)
    {
    	if (cA )
    	{
        	this.cA = new DERBoolean(cA);
        	this.pathLenConstraint = new DERInteger(pathLenConstraint);
    	}
    	else
    	{
    		this.cA = null;
    		this.pathLenConstraint = null;
    	}
    }

    public BasicConstraints(
        boolean cA)
    {
    	if (cA)
    	{
			this.cA = new DERBoolean(true);
    	}
    	else
    	{
			this.cA = null;
    	}
        this.pathLenConstraint = null;
    }

	/**
	 * create a cA=true object for the given path length constraint.
	 *
	 * @param pathLenConstraint
	 */
	public BasicConstraints(
		int     pathLenConstraint)
	{
		this.cA = new DERBoolean(true);
		this.pathLenConstraint = new DERInteger(pathLenConstraint);
	}

    public boolean isCA()
    {
        return (cA != null) && cA.isTrue();
    }

    public BigInteger getPathLenConstraint()
    {
        if (pathLenConstraint != null)
        {
            return pathLenConstraint.getValue();
        }

        return null;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * BasicConstraints := SEQUENCE {
     *    cA                  BOOLEAN DEFAULT FALSE,
     *    pathLenConstraint   INTEGER (0..MAX) OPTIONAL
     * }
     * </pre>
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

		if (cA != null)
		{
	        v.add(cA);

	        if (pathLenConstraint != null)
	        {
	            v.add(pathLenConstraint);
	        }
		}

        return new DERSequence(v);
    }

    public String toString()
    {
		if (pathLenConstraint == null)
		{
			if (cA == null)
			{
				return "BasicConstraints: isCa(false)";
			}
			return "BasicConstraints: isCa(" + this.isCA() + ")";
		}
		return "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + pathLenConstraint.getValue();
    }
}
