
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
			
package com.maverick.crypto.digests;

import com.maverick.util.ByteArrayWriter;

import java.math.BigInteger;


/**
 * <p>Useful utility class that provides a wrapper around a digest with
 * methods to update the digest with many of the common parameter types
 * used throughout the API.</p>
 * @author Lee David Painter
 */
public class Hash {
  private Digest digest;

  /**
   * Create a hash with the digest provided.
   * @param digest
   */
  public Hash(String type) {
    this.digest = DigestFactory.createDigest(type);
  }

  public Hash(Digest digest) {
      this.digest = digest;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.digests.Tmp#putBigInteger(java.math.BigInteger)
 */
  public void putBigInteger(BigInteger bi) {
    byte[] data = bi.toByteArray();

    putInt(data.length);
    putBytes(data);
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.digests.Tmp#putByte(byte)
 */
  public void putByte(byte b) {
    digest.update(b);
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.digests.Tmp#putBytes(byte[])
 */
  public void putBytes(byte[] data) {
    digest.update(data, 0, data.length);
  }

  /* (non-Javadoc)
   * @see com.maverick.crypto.digests.Tmp#putBytes(byte[], int, int)
   */
  public void putBytes(byte[] data, int offset, int len) {
    digest.update(data, offset, len);
  }

  /* (non-Javadoc)
   * @see com.maverick.crypto.digests.Tmp#putInt(int)
   */
  public void putInt(int i) {
    putBytes(ByteArrayWriter.encodeInt(i));
  }

  /* (non-Javadoc)
   * @see com.maverick.crypto.digests.Tmp#putString(java.lang.String)
   */
  public void putString(String str) {
    putInt(str.length());
    putBytes(str.getBytes());
  }

  /* (non-Javadoc)
   * @see com.maverick.crypto.digests.Tmp#reset()
   */
  public void reset() {
    digest.reset();
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.digests.Tmp#doFinal()
 */
  public byte[] doFinal() {
    byte[] hash = new byte[digest.getDigestSize()];
    digest.doFinal(hash, 0);
    return hash;
  }
}
