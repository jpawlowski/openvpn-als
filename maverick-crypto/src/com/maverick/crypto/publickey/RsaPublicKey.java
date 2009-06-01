
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
			
package com.maverick.crypto.publickey;

import java.io.IOException;
import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;


public class RsaPublicKey
    extends RsaKey implements PublicKey {

  protected BigInteger publicExponent;

  protected final static byte[] ASN_SHA1 = {
      0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e,
      0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14
  };  
  
  public RsaPublicKey() {
  }
  
  public RsaPublicKey(BigInteger modulus, BigInteger publicExponent) {
    super(modulus);
    this.publicExponent = publicExponent;
  }

  public BigInteger getPublicExponent() {
    return publicExponent;
  }
  
  protected void setPublicExponent(BigInteger publicExponent) {
      this.publicExponent = publicExponent;
  }

  public boolean verifySignature(byte[] signature, byte[] msg) {

	  BigInteger signatureInt = new BigInteger(1, signature);

      signatureInt = Rsa.doPublic(signatureInt,
                                  getModulus(), publicExponent);

      signatureInt = Rsa.removePKCS1(signatureInt, 1);

      signature = signatureInt.toByteArray();

      SHA1Digest h = new SHA1Digest();
      h.update(msg, 0, msg.length);
      byte[] data = new byte[h.getDigestSize()];
      h.doFinal(data, 0);

      if(data.length != (signature.length - ASN_SHA1.length)) {
        return false;
      }

      byte[] cmp = ASN_SHA1;
      for(int i = 0, j = 0; i < signature.length; i++, j++) {
        if(i == ASN_SHA1.length) {
          cmp = data;
          j = 0;
        }
        if(signature[i] != cmp[j]) {
          return false;
        }
      }
      return true;

  }

  public int hashCode() {

      int hashCode = getModulus().hashCode();

      hashCode ^= publicExponent.hashCode();

      return hashCode;
  }


}
