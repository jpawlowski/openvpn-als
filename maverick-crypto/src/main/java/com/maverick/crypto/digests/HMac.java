package com.maverick.crypto.digests;

import java.io.IOException;

public interface HMac {

    public int doFinal(byte[] out, int outOff);

    public int getMacSize();

    public int getOutputSize();

    public void init(byte[] key) throws IOException;

    public void reset();

    public void update(byte in);

    public void update(byte[] in, int inOff, int len);
}
