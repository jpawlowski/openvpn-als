
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

import java.math.BigInteger;

import com.maverick.crypto.digests.SHA1Digest;

public class DsaPrivateKey
    extends DsaKey {

  protected BigInteger x;

  public DsaPrivateKey(BigInteger p, BigInteger q, BigInteger g, BigInteger x) {
    super(p, q, g);
    this.x = x;
  }

  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.DsaPrivateKeyInterface#getX()
 */
public BigInteger getX() {
    return x;
  }

  /* (non-Javadoc)
   * @see com.maverick.ssh.SshPrivateKey#sign(byte[])
   */
  /* (non-Javadoc)
 * @see com.maverick.crypto.publickey.DsaPrivateKeyInterface#sign(byte[])
 */
public byte[] sign(byte[] msg) {

    SHA1Digest h = new SHA1Digest();
    h.update(msg, 0, msg.length);
    byte[] data = new byte[h.getDigestSize()];
    h.doFinal(data, 0);
    return Dsa.sign(x, p, q, g, data);
  }

  public boolean equals(Object obj) {
    if (obj instanceof DsaPrivateKey) {
      DsaPrivateKey key = (DsaPrivateKey) obj;
      return x.equals(key.x)
          && p.equals(key.p)
          && q.equals(key.q)
          && g.equals(key.g);
    }
    return false;
  }

}
