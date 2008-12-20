
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
			
package com.maverick.crypto.security;


import com.maverick.crypto.digests.SHA1Digest;

/**
 * An implementation of SecureRandom specifically for the
 * light-weight API, JDK 1.0, and the J2ME. Random generation is
 * based on the traditional SHA1 with counter. Calling setSeed
 * will always increase the entropy of the hash.
 *
 * @author Bouncycastle.org
 */
public class SecureRandom
    extends java.util.Random {
  private static SecureRandom rand = new SecureRandom();

  private byte[] seed;

  private long counter = 1;
  private SHA1Digest digest = new SHA1Digest();
  private byte[] state = new byte[digest.getDigestSize()];

  // public constructors
  public SecureRandom() {
    super(0);
    setSeed(System.currentTimeMillis());
  }

  public SecureRandom(
      byte[] inSeed) {
    setSeed(inSeed);
  }

  // protected constructors
  // protected SecureRandom(SecureRandomSpi srs, Provider provider);

  // public class methods
  public static SecureRandom getInstance(String algorithm) {
    return new SecureRandom();
  }

  public static SecureRandom getInstance() {
    synchronized(rand) {
      rand.setSeed(System.currentTimeMillis());
      return rand;
    }
  }

  public static byte[] getSeed(
      int numBytes) {
    byte[] rv = new byte[numBytes];

    synchronized(rand) {
      rand.setSeed(System.currentTimeMillis());
      rand.nextBytes(rv);
    }

    return rv;
  }

  // public instance methods
  public byte[] generateSeed(
      int numBytes) {
    byte[] rv = new byte[numBytes];

    nextBytes(rv);

    return rv;
  }

  // public final Provider getProvider();
  public synchronized void setSeed(
      byte[] inSeed) {
    digest.update(inSeed, 0, inSeed.length);
  }

  public synchronized void nextBytes(byte[] bytes) {
    nextBytes(bytes, 0, bytes.length);
  }

  // public methods overriding random
  public synchronized void nextBytes(
      byte[] bytes, int offset, int length) {
    int stateOff = 0;

    digest.doFinal(state, 0);

    for (int i = 0; i != length; i++) {
      if (stateOff == state.length) {
        byte[] b = longToBytes(counter++);

        digest.update(b, 0, b.length);
        digest.update(state, 0, state.length);
        digest.doFinal(state, 0);
        stateOff = 0;
      }
      bytes[i + offset] = state[stateOff++];
    }

    byte[] b = longToBytes(counter++);

    digest.update(b, 0, b.length);
    digest.update(state, 0, state.length);
  }

  public synchronized void setSeed(
      long rSeed) {
    if (rSeed != 0) {
      setSeed(longToBytes(rSeed));
    }
  }

  private byte[] intBytes = new byte[4];

  public synchronized int nextInt() {
    nextBytes(intBytes);

    int result = 0;

    for (int i = 0; i < 4; i++) {
      result = (result << 8) + (intBytes[i] & 0xff);
    }

    return result;
  }

  protected final synchronized int next(
      int numBits) {
    int size = (numBits + 7) / 8;
    byte[] bytes = new byte[size];

    nextBytes(bytes);

    int result = 0;

    for (int i = 0; i < size; i++) {
      result = (result << 8) + (bytes[i] & 0xff);
    }

    return result & ( (1 << numBits) - 1);
  }

  private byte[] longBytes = new byte[8];

  private synchronized byte[] longToBytes(
      long val) {
    for (int i = 0; i != 8; i++) {
      longBytes[i] = (byte) val;
      val >>>= 8;
    }

    return longBytes;
  }
}
