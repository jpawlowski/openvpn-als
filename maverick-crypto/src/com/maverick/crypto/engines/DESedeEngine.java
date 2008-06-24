
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
			
package com.maverick.crypto.engines;

import java.io.IOException;

/**
 * a class that provides a basic DESede (or Triple DES) engine.
 */
public class DESedeEngine
    extends DESEngine {
  protected static final int BLOCK_SIZE = 8;

  private int[] workingKey1 = null;
  private int[] workingKey2 = null;
  private int[] workingKey3 = null;

  private boolean forEncryption;

  /**
   * standard constructor.
   */
  public DESedeEngine() {
  }

  /**
   * initialise a DESede cipher.
   *
   * @param forEncryption whether or not we are for encryption.
   * @param params the parameters required to set up the cipher.
   * @exception IllegalArgumentException if the params argument is
   * inappropriate.
   */
  public void init(
      boolean encrypting,
      byte[] key) {

    byte[] keyMaster = key;
    byte[] key1 = new byte[8], key2 = new byte[8], key3 = new byte[8];

    this.forEncryption = encrypting;

    if (keyMaster.length == 24) {
      System.arraycopy(keyMaster, 0, key1, 0, key1.length);
      System.arraycopy(keyMaster, 8, key2, 0, key2.length);
      System.arraycopy(keyMaster, 16, key3, 0, key3.length);

      workingKey1 = generateWorkingKey(encrypting, key1);
      workingKey2 = generateWorkingKey(!encrypting, key2);
      workingKey3 = generateWorkingKey(encrypting, key3);
    }
    else { // 16 byte key
      System.arraycopy(keyMaster, 0, key1, 0, key1.length);
      System.arraycopy(keyMaster, 8, key2, 0, key2.length);

      workingKey1 = generateWorkingKey(encrypting, key1);
      workingKey2 = generateWorkingKey(!encrypting, key2);
      workingKey3 = workingKey1;
    }
  }

  public String getAlgorithmName() {
    return "DESede";
  }

  public int getBlockSize() {
    return BLOCK_SIZE;
  }

  public int processBlock(
      byte[] in,
      int inOff,
      byte[] out,
      int outOff) throws IOException {
    if (workingKey1 == null) {
      throw new IllegalStateException("DESede engine not initialised");
    }

    if ( (inOff + BLOCK_SIZE) > in.length) {
      throw new IOException("input buffer too short");
    }

    if ( (outOff + BLOCK_SIZE) > out.length) {
      throw new IOException("output buffer too short");
    }

    if (forEncryption) {
      desFunc(workingKey1, in, inOff, out, outOff);
      desFunc(workingKey2, out, outOff, out, outOff);
      desFunc(workingKey3, out, outOff, out, outOff);
    }
    else {
      desFunc(workingKey3, in, inOff, out, outOff);
      desFunc(workingKey2, out, outOff, out, outOff);
      desFunc(workingKey1, out, outOff, out, outOff);
    }

    return BLOCK_SIZE;
  }

  public void reset() {
  }
}
