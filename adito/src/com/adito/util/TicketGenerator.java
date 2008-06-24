
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
			
package com.adito.util;

import com.maverick.crypto.digests.MD5Digest;
import com.maverick.crypto.encoders.Hex;
import com.maverick.crypto.security.SecureRandom;

public class TicketGenerator {

  static TicketGenerator instance;

  TicketGenerator() {
  }

  public static TicketGenerator getInstance() {
    if (instance == null) {
      instance = new TicketGenerator();

    }
    return instance;
  }

  public String generateUniqueTicket(String prefix) {
      return generateUniqueTicket(prefix, -1);
  }

  public String generateUniqueTicket(String prefix, int len) {

    MD5Digest md5 = new MD5Digest();
    long time = System.currentTimeMillis();

    md5.update( (byte) (time >> 56));
    md5.update( (byte) (time >> 48));
    md5.update( (byte) (time >> 40));
    md5.update( (byte) (time >> 32));
    md5.update( (byte) (time >> 24));
    md5.update( (byte) (time >> 16));
    md5.update( (byte) (time >> 8));
    md5.update( (byte) (time >> 0));

    byte[] data = new byte[256];
    SecureRandom.getInstance().nextBytes(data);
    md5.update(data, 0, data.length);

    byte[] hash = new byte[md5.getDigestSize()];
    md5.doFinal(hash, 0);

    String val = prefix + new String(Hex.encode(hash));

    if(len > 0 && len < val.length())
        val = val.substring(0, len);
    return val;
  }

}
