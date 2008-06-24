package com.adito.security.pki;

import java.security.SecureRandom;

public class Utils {

	static SecureRandom rnd = new SecureRandom();
	
	
	public static SecureRandom getRND() {
		rnd.setSeed(System.currentTimeMillis());
		return rnd;
	}
}
