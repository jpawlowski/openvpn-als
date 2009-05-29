package com.maverick.crypto.digests;

public interface DigestProvider {

    public Digest createDigest(String type);

}
