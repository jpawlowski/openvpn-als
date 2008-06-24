
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

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;
import com.maverick.crypto.security.SecureRandom;

public class RsaPrivateKey
    extends RsaKey {

  protected BigInteger privateExponent;

  protected final static byte[] ASN_SHA1 = {
      0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e,
      0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14
  };
  
  public RsaPrivateKey(BigInteger modulus, BigInteger privateExponent) {
    super(modulus);
    this.privateExponent = privateExponent;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateKeyInterface#getPrivateExponent()
 */
public BigInteger getPrivateExponent() {
    return privateExponent;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.RsaPrivateKeyInterface#sign(byte[])
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

      BigInteger privateExponent = getPrivateExponent();
      BigInteger modulus = getModulus();
      signatureInt = Rsa.doPrivate(dataInt,
                                   modulus, privateExponent);

      byte[] sig = unsignedBigIntToBytes(signatureInt, mLen);

      return sig;

  }

  protected static byte[] unsignedBigIntToBytes(BigInteger bi, int size) {
    byte[] tmp = bi.toByteArray();
    byte[] tmp2 = null;
    if (tmp.length > size) {
      tmp2 = new byte[size];
      System.arraycopy(tmp, tmp.length - size, tmp2, 0, size);
    }
    else if (tmp.length < size) {
      tmp2 = new byte[size];
      System.arraycopy(tmp, 0, tmp2, size - tmp.length, tmp.length);
    }
    else {
      tmp2 = tmp;
    }
    return tmp2;
  }

  public boolean equals(Object obj) {
    if (obj instanceof RsaPrivateKey) {
      RsaPrivateKey key = (RsaPrivateKey) obj;
      return key.getBitLength() == getBitLength()
          && key.getModulus().compareTo(getModulus()) == 0
          && key.getPrivateExponent().compareTo(getPrivateExponent()) == 0;
    }
    return false;
  }
}
