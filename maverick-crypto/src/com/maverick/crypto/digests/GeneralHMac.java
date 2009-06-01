
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
			
package com.maverick.crypto.digests;




/**
 * HMAC implementation based on RFC2104
 *
 * H(K XOR opad, H(K XOR ipad, text))
 */
public class GeneralHMac
        implements HMac {
  private final static int BLOCK_LENGTH = 64;

  private final static byte IPAD = (byte) 0x36;
  private final static byte OPAD = (byte) 0x5C;

  private Digest digest;
  private int digestSize;
  private int outputSize;
  private byte[] inputPad = new byte[BLOCK_LENGTH];
  private byte[] outputPad = new byte[BLOCK_LENGTH];

  public GeneralHMac(
      Digest digest) {
    this.digest = digest;
    digestSize = digest.getDigestSize();
    outputSize = digestSize;
  }

  public GeneralHMac(
      Digest digest, int outputSize) {
    this.digest = digest;
    this.digestSize = digest.getDigestSize();
    this.outputSize = outputSize;
  }
  
  public String getAlgorithmName() {
    return digest.getAlgorithmName() + "/HMAC";
  }

  public int getOutputSize() {
      return outputSize;
  }

  public Digest getUnderlyingDigest() {
    return digest;
  }

  public void init(
      byte[] key) {
    digest.reset();

    if (key.length > BLOCK_LENGTH) {
      digest.update(key, 0, key.length);
      digest.doFinal(inputPad, 0);
      for (int i = digestSize; i < inputPad.length; i++) {
        inputPad[i] = 0;
      }
    }
    else {
      System.arraycopy(key, 0, inputPad, 0, key.length);
      for (int i = key.length; i < inputPad.length; i++) {
        inputPad[i] = 0;
      }
    }

    outputPad = new byte[inputPad.length];
    System.arraycopy(inputPad, 0, outputPad, 0, inputPad.length);

    for (int i = 0; i < inputPad.length; i++) {
      inputPad[i] ^= IPAD;
    }

    for (int i = 0; i < outputPad.length; i++) {
      outputPad[i] ^= OPAD;
    }

    digest.update(inputPad, 0, inputPad.length);
  }

  public int getMacSize() {
    return digestSize;
  }

  public void update(
      byte in) {
    digest.update(in);
  }

  public void update(
      byte[] in,
      int inOff,
      int len) {
    digest.update(in, inOff, len);
  }

  public int doFinal(
      byte[] out,
      int outOff) {
    byte[] tmp = new byte[digestSize];
    digest.doFinal(tmp, 0);

    digest.update(outputPad, 0, outputPad.length);
    digest.update(tmp, 0, tmp.length);

    int len = digest.doFinal(out, outOff);

    reset();

    return len;
  }

  /**
   * Reset the mac generator.
   */
  public void reset() {
    /*
     * reset the underlying digest.
     */
    digest.reset();

    /*
     * reinitialize the digest.
     */
    digest.update(inputPad, 0, inputPad.length);
  }
}
