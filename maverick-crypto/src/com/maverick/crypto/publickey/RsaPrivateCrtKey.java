
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
			
package com.maverick.crypto.publickey;

import java.io.IOException;
import java.math.BigInteger;

import com.maverick.crypto.digests.SHA1Digest;
import com.maverick.crypto.security.SecureRandom;

/**
 *
 * @author Lee David Painter
 */
public class RsaPrivateCrtKey
    extends RsaPrivateKey {

  protected BigInteger publicExponent;
  protected BigInteger primeP;
  protected BigInteger primeQ;
  protected BigInteger primeExponentP;
  protected BigInteger primeExponentQ;
  protected BigInteger crtCoefficient;

  /*public RsaPrivateCrtKey(BigInteger modulus,
                          BigInteger publicExponent,
                          BigInteger privateExponent,
                          BigInteger primeP, BigInteger primeQ,
                          BigInteger crtCoefficient) {
    this(modulus, publicExponent, privateExponent, primeP, primeQ,
         Rsa.getPrimeExponent(privateExponent, primeP),
         Rsa.getPrimeExponent(privateExponent, primeQ),
         crtCoefficient);
  }*/

  public RsaPrivateCrtKey(BigInteger modulus,
                          BigInteger publicExponent,
                          BigInteger privateExponent,
                          BigInteger primeP, BigInteger primeQ,
                          BigInteger primeExponentP,
                          BigInteger primeExponentQ,
                          BigInteger crtCoefficient) {
    super(modulus, privateExponent);
    this.publicExponent = publicExponent;
    this.primeP = primeP;
    this.primeQ = primeQ;
    this.primeExponentP = primeExponentP;
    this.primeExponentQ = primeExponentQ;
    this.crtCoefficient = crtCoefficient;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getPublicExponent()
 */
public BigInteger getPublicExponent() {
    return publicExponent;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getPrimeP()
 */
public BigInteger getPrimeP() {
    return primeP;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getPrimeQ()
 */
public BigInteger getPrimeQ() {
    return primeQ;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getPrimeExponentP()
 */
public BigInteger getPrimeExponentP() {
    return primeExponentP;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getPrimeExponentQ()
 */
public BigInteger getPrimeExponentQ() {
    return primeExponentQ;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#getCrtCoefficient()
 */
public BigInteger getCrtCoefficient() {
    return crtCoefficient;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateCrtKeyInterface#sign(byte[])
 */
public byte[] sign(byte[] msg) throws IOException {


    SHA1Digest hash = new SHA1Digest();
    hash.update(msg, 0, msg.length);

    byte[] data = new byte[hash.getDigestSize()];
    hash.doFinal(data, 0);


      byte[] tmp = new byte[data.length + ASN_SHA1.length];
      System.arraycopy(ASN_SHA1, 0, tmp, 0, ASN_SHA1.length);
      System.arraycopy(data, 0, tmp, ASN_SHA1.length, data.length);
      data = tmp;

      BigInteger dataInt = new BigInteger(1, data);
      int mLen = (getModulus().bitLength() + 7) / 8;

      dataInt = Rsa.padPKCS1(dataInt, 1, mLen);

      BigInteger signatureInt = null;

      BigInteger primeP = getPrimeP();
      BigInteger primeQ = getPrimeQ();
      BigInteger primeExponentP = getPrimeExponentP();
      BigInteger primeExponentQ = getPrimeExponentQ();
      BigInteger crtCoefficient = getCrtCoefficient();

      signatureInt = Rsa.doPrivateCrt(dataInt,
                                      primeP, primeQ,
                                      primeExponentP,
                                      primeExponentQ,
                                      crtCoefficient);

      byte[] sig = unsignedBigIntToBytes(signatureInt, mLen);

      return sig;
  }

}
