
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERBitString;
import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.DEREncodable;
import com.maverick.crypto.asn1.DERIA5String;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.DEROutputStream;
import com.maverick.crypto.asn1.misc.MiscObjectIdentifiers;
import com.maverick.crypto.asn1.misc.NetscapeCertType;
import com.maverick.crypto.asn1.misc.NetscapeRevocationURL;
import com.maverick.crypto.asn1.misc.VerisignCzagExtension;
import com.maverick.crypto.asn1.ASN1Dump;
import com.maverick.crypto.asn1.pkcs.PKCSObjectIdentifiers;
import com.maverick.crypto.asn1.x509.BasicConstraints;
import com.maverick.crypto.asn1.x509.KeyUsage;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;
import com.maverick.crypto.asn1.x509.X509Extension;
import com.maverick.crypto.asn1.x509.X509Extensions;
import com.maverick.crypto.encoders.Hex;
import com.maverick.crypto.publickey.*;

public class X509Certificate
{
    private X509CertificateStructure    c;
    private Hashtable                   pkcs12Attributes = new Hashtable();
    private Vector                      pkcs12Ordering = new Vector();

    public X509Certificate(
        X509CertificateStructure    c)
    {
        this.c = c;
    }

    public void checkValidity()
        throws CertificateException
    {
        this.checkValidity(new Date());
    }

    public void checkValidity(
        Date    date)
        throws CertificateException
    {
        if (date.after(this.getNotAfter()))
        {
            throw new CertificateException(
          CertificateException.CERTIFICATE_EXPIRED,
          "Certificate expired on " + c.getEndDate().getTime());
        }

        if (date.before(this.getNotBefore()))
        {
            throw new CertificateException(
          CertificateException.CERTIFICATE_NOT_YET_VALID,
          "certificate not valid till " + c.getStartDate().getTime());
        }
    }

    public int getVersion()
    {
        return c.getVersion();
    }

    public BigInteger getSerialNumber()
    {
        return c.getSerialNumber().getValue();
    }

    public X509Name getIssuerDN()
    {
        return c.getIssuer();
    }

    public X509Name getSubjectDN()
    {
        return c.getSubject();
    }

    public Date getNotBefore()
    {
        return c.getStartDate().getDate();
    }

    public Date getNotAfter()
    {
        return c.getEndDate().getDate();
    }

    public byte[] getTBSCertificate()
        throws CertificateException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);

        try
        {
            dOut.writeObject(c.getTBSCertificate());

            return bOut.toByteArray();
        }
        catch (IOException e)
        {
            throw new CertificateException(CertificateException.CERTIFICATE_ENCODING_ERROR,
                                           e.toString());
        }
    }

    public byte[] getSignature()
    {
        return c.getSignature().getBytes();
    }

    /**
     * return a more "meaningful" representation for the signature algorithm used in
     * the certficate.
     */
    /*public String getSigAlgName()
    {
        Provider    prov = Security.getProvider("BC");
        String      algName = prov.getProperty("Alg.Alias.Signature." + this.getSigAlgOID());

        if (algName != null)
        {
            return algName;
        }

        Provider[] provs = Security.getProviders();

        //
        // search every provider looking for a real algorithm
        //
        for (int i = 0; i != provs.length; i++)
        {
            algName = provs[i].getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
            if (algName != null)
            {
                return algName;
            }
        }

        return this.getSigAlgOID();
    }*/

    /**
     * return the object identifier for the signature.
     */
    public String getSigAlgOID()
    {
        return c.getSignatureAlgorithm().getObjectId().getId();
    }


    public String getSigAlgName() throws CertificateException {
      if(getSigAlgOID().equals("1.2.840.113549.1.1.4")) {
        return "MD5WithRSAEncryption";
      } else if(getSigAlgOID().equals("1.2.840.113549.1.1.5")) {
        return "SHA1WithRSAEncryption";
      } else
        throw new CertificateException(CertificateException.CERTIFICATE_UNSUPPORTED_ALGORITHM,
                                       "Unsupported signature algorithm id "
                                       + getSigAlgOID());
    }

    /**
     * return the signature parameters, or null if there aren't any.
     */
    public byte[] getSigAlgParams()
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();

        if (c.getSignatureAlgorithm().getParameters() != null)
        {
            try
            {
                DEROutputStream         dOut = new DEROutputStream(bOut);

                dOut.writeObject(c.getSignatureAlgorithm().getParameters());
            }
            catch (Exception e)
            {
                throw new RuntimeException("exception getting sig parameters " + e);
            }

            return bOut.toByteArray();
        }
        else
        {
            return null;
        }
    }

    public boolean[] getIssuerUniqueID()
    {
        DERBitString    id = c.getTBSCertificate().getIssuerUniqueId();

        if (id != null)
        {
            byte[]          bytes = id.getBytes();
            boolean[]       boolId = new boolean[bytes.length * 8 - id.getPadBits()];

            for (int i = 0; i != boolId.length; i++)
            {
                boolId[i] = (bytes[i / 8] & (0x80 >>> (i % 8))) != 0;
            }

            return boolId;
        }

        return null;
    }

    public boolean[] getSubjectUniqueID()
    {
        DERBitString    id = c.getTBSCertificate().getSubjectUniqueId();

        if (id != null)
        {
            byte[]          bytes = id.getBytes();
            boolean[]       boolId = new boolean[bytes.length * 8 - id.getPadBits()];

            for (int i = 0; i != boolId.length; i++)
            {
                boolId[i] = (bytes[i / 8] & (0x80 >>> (i % 8))) != 0;
            }

            return boolId;
        }

        return null;
    }

    public boolean[] getKeyUsage()
    {
        byte[]  bytes = this.getExtensionBytes("2.5.29.15");
        int     length = 0;

        if (bytes != null)
        {
            try
            {
                DERInputStream  dIn = new DERInputStream(new ByteArrayInputStream(bytes));
                DERBitString    bits = (DERBitString)dIn.readObject();

                bytes = bits.getBytes();
                length = (bytes.length * 8) - bits.getPadBits();
            }
            catch (Exception e)
            {
                throw new RuntimeException("error processing key usage extension");
            }

            boolean[]       keyUsage = new boolean[(length < 9) ? 9 : length];

            for (int i = 0; i != length; i++)
            {
                keyUsage[i] = (bytes[i / 8] & (0x80 >>> (i % 8))) != 0;
            }

            return keyUsage;
        }

        return null;
    }

    public int getBasicConstraints()
    {
        byte[]  bytes = this.getExtensionBytes("2.5.29.19");

        if (bytes != null)
        {
            try
            {
                DERInputStream  dIn = new DERInputStream(new ByteArrayInputStream(bytes));
                ASN1Sequence    seq = (ASN1Sequence)dIn.readObject();

                if (seq.size() == 2)
                {
                    if (((DERBoolean)seq.getObjectAt(0)).isTrue())
                    {
                        return ((DERInteger)seq.getObjectAt(1)).getValue().intValue();
                    }
                    else
                    {
                        return -1;
                    }
                }
                else if (seq.size() == 1)
                {
                    if (seq.getObjectAt(0) instanceof DERBoolean)
                    {
                        if (((DERBoolean)seq.getObjectAt(0)).isTrue())
                        {
                            return Integer.MAX_VALUE;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                    else
                    {
                        return -1;
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("error processing key usage extension");
            }
        }

        return -1;
    }

    public X509Extension[] getCriticalExtensionOIDs()
    {
        if (this.getVersion() == 3)
        {
            Vector         set = new Vector();
            X509Extensions  extensions = c.getTBSCertificate().getExtensions();

            if (extensions != null)
            {
                Enumeration     e = extensions.oids();

                while (e.hasMoreElements())
                {
                    DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
                    X509Extension       ext = extensions.getExtension(oid);

                    if (ext.isCritical())
                    {
                        set.addElement(oid.getId());
                    }
                }

                X509Extension[] ext = new X509Extension[set.size()];
                set.copyInto(ext);
                return ext;
            }
        }

        return null;
    }

    private byte[] getExtensionBytes(String oid)
    {
        X509Extensions exts = c.getTBSCertificate().getExtensions();

        if (exts != null)
        {
            X509Extension   ext = exts.getExtension(new DERObjectIdentifier(oid));
            if (ext != null)
            {
                return ext.getValue().getOctets();
            }
        }

        return null;
    }

    public byte[] getExtensionValue(String oid)
    {
        X509Extensions exts = c.getTBSCertificate().getExtensions();

        if (exts != null)
        {
            X509Extension   ext = exts.getExtension(new DERObjectIdentifier(oid));

            if (ext != null)
            {
                ByteArrayOutputStream    bOut = new ByteArrayOutputStream();
                DEROutputStream            dOut = new DEROutputStream(bOut);

                try
                {
                    dOut.writeObject(ext.getValue());

                    return bOut.toByteArray();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("error encoding " + e.toString());
                }
            }
        }

        return null;
    }

    public X509Extension[] getNonCriticalExtensionOIDs()
    {
        if (this.getVersion() == 3)
        {
            Vector         set = new Vector();
            X509Extensions  extensions = c.getTBSCertificate().getExtensions();

            if (extensions != null)
            {
                Enumeration     e = extensions.oids();

                while (e.hasMoreElements())
                {
                    DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
                    X509Extension       ext = extensions.getExtension(oid);

                    if (!ext.isCritical())
                    {
                        set.addElement(oid.getId());
                    }
                }

                X509Extension[] ext = new X509Extension[set.size()];
                set.copyInto(ext);
                return ext;
            }
        }

        return null;
    }

    public boolean hasUnsupportedCriticalExtension()
    {
        if (this.getVersion() == 3)
        {
            X509Extensions  extensions = c.getTBSCertificate().getExtensions();

            if (extensions != null)
            {
                Enumeration     e = extensions.oids();

                while (e.hasMoreElements())
                {
                    DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
                    if (oid.getId().equals("2.5.29.15")
                       || oid.getId().equals("2.5.29.19"))
                    {
                        continue;
                    }

                    X509Extension       ext = extensions.getExtension(oid);

                    if (ext.isCritical())
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public PublicKey getPublicKey() throws CertificateException
    {
      try {
        AlgorithmIdentifier algId = c.getSubjectPublicKeyInfo().getAlgorithmId();
        if (algId.getObjectId().equals(PKCSObjectIdentifiers.rsaEncryption)
            || algId.getObjectId().equals(X509ObjectIdentifiers.id_ea_rsa)) {
          RSAPublicKeyStructure rsa = RSAPublicKeyStructure.getInstance(
            c.getSubjectPublicKeyInfo().
            getPublicKey());
          return new RsaPublicKey(rsa.getModulus(),
                                  rsa.getPublicExponent());
        } else
          throw new CertificateException(CertificateException.CERTIFICATE_UNSUPPORTED_ALGORITHM,
                                         "Public key algorithm id "
                                         + algId.getObjectId().getId()
                                         + " is not supported");

      }
      catch (IOException ex) {
        throw new CertificateException(CertificateException.CERTIFICATE_GENERAL_ERROR,
                                       ex.getMessage());
      }
    }

    public byte[] getEncoded()
        throws CertificateException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);

        try
        {
            dOut.writeObject(c);

            return bOut.toByteArray();
        }
        catch (IOException e)
        {
            throw new CertificateException(
          CertificateException.CERTIFICATE_ENCODING_ERROR,
          e.toString());
        }
    }

    public void setBagAttribute(
        DERObjectIdentifier oid,
        DEREncodable        attribute)
    {
        pkcs12Attributes.put(oid, attribute);
        pkcs12Ordering.addElement(oid);
    }

    public DEREncodable getBagAttribute(
        DERObjectIdentifier oid)
    {
        return (DEREncodable)pkcs12Attributes.get(oid);
    }

    public Enumeration getBagAttributeKeys()
    {
        return pkcs12Ordering.elements();
    }

    public String toString()
    {
        StringBuffer    buf = new StringBuffer();
        String          nl = System.getProperty("line.separator");


          buf.append("  [0]         Version: " + this.getVersion() + nl);
          buf.append("         SerialNumber: " + this.getSerialNumber() + nl);
          buf.append("             IssuerDN: " + this.getIssuerDN() + nl);
          buf.append("           Start Date: " + this.getNotBefore() + nl);
          buf.append("           Final Date: " + this.getNotAfter() + nl);
          buf.append("            SubjectDN: " + this.getSubjectDN() + nl);
        try {
          buf.append("           Public Key: " + this.getPublicKey() + nl);

        }
        catch (CertificateException ex1) {
          buf.append("           Public Key:  " + ex1.getMessage());
        }

        try {
          buf.append("  Signature Algorithm: " + this.getSigAlgName() + nl);
        } catch(CertificateException ex1) {
          buf.append("  Signature Algorithm: " + ex1.getMessage());
        }

        byte[]  sig = this.getSignature();

        buf.append("            Signature: " + new String(Hex.encode(sig, 0, 20)) + nl);
        for (int i = 20; i < sig.length; i += 20)
        {
            if (i < sig.length - 20)
            {
                buf.append("                       " + new String(Hex.encode(sig, i, 20)) + nl);
            }
            else
            {
                buf.append("                       " + new String(Hex.encode(sig, i, sig.length - i)) + nl);
            }
        }

        X509Extensions  extensions = c.getTBSCertificate().getExtensions();

        if (extensions != null)
        {
            Enumeration     e = extensions.oids();

            if (e.hasMoreElements())
            {
                buf.append("       Extensions: \n");
            }

            while (e.hasMoreElements())
            {
                DERObjectIdentifier     oid = (DERObjectIdentifier)e.nextElement();
                X509Extension           ext = extensions.getExtension(oid);

                if (ext.getValue() != null)
                {
                    byte[]                  octs = ext.getValue().getOctets();
                    ByteArrayInputStream    bIn = new ByteArrayInputStream(octs);
                    DERInputStream          dIn = new DERInputStream(bIn);
                    buf.append("                       critical(" + ext.isCritical() + ") ");
                    try
                    {
                        if (oid.equals(X509Extensions.BasicConstraints))
                        {
                            buf.append(new BasicConstraints((ASN1Sequence)dIn.readObject()) + nl);
                        }
                        else if (oid.equals(X509Extensions.KeyUsage))
                        {
                            buf.append(new KeyUsage((DERBitString)dIn.readObject()) + nl);
                        }
                        else if (oid.equals(MiscObjectIdentifiers.netscapeCertType))
                        {
                            buf.append(new NetscapeCertType((DERBitString)dIn.readObject()) + nl);
                        }
                        else if (oid.equals(MiscObjectIdentifiers.netscapeRevocationURL))
                        {
                            buf.append(new NetscapeRevocationURL((DERIA5String)dIn.readObject()) + nl);
                        }
                        else if (oid.equals(MiscObjectIdentifiers.verisignCzagExtension))
                        {
                            buf.append(new VerisignCzagExtension((DERIA5String)dIn.readObject()) + nl);
                        }
                        else
                        {
                            buf.append(oid.getId());
                            buf.append(" value = " + ASN1Dump.dumpAsString(dIn.readObject()) + nl);
                            //buf.append(" value = " + "*****" + nl);
                        }
                    }
                    catch (Exception ex)
                    {
                        buf.append(oid.getId());
                   //     buf.append(" value = " + new String(Hex.encode(ext.getValue().getOctets())) + nl);
                        buf.append(" value = " + "*****" + nl);
                    }
                }
                else
                {
                    buf.append(nl);
                }
            }
        }

        return buf.toString();
    }

    public final void verify(
        PublicKey   key)
        throws CertificateException
    {



    }


}
