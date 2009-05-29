package com.maverick.crypto.digests;

public interface Digest {

    public int doFinal(byte[] output, int offset);

    public void finish();

    public String getAlgorithmName();

    public int getDigestSize();

    public void reset();

    public void update(byte in);

    public void update(byte[] in, int inOff, int len);
}
