package com.maverick.crypto.digests;

public class DigestFactory {


    static DigestProvider provider = null;

    public DigestFactory() {
    }

    public static void setProvider(DigestProvider provider) {
        DigestFactory.provider = provider;
    }

    public static Digest createDigest(String type) {

        if(provider!=null)
            return provider.createDigest(type);
        else {
            if(type.equals("MD5")) {
                return new MD5Digest();
            }
            else {
                return new SHA1Digest();
            }
        }
    }
}
