package com.ovpnals.core;

import java.util.Properties;

import com.ovpnals.boot.CodedException;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyValidator;

public class SSLCipherSuiteValidator implements PropertyValidator {

	public void validate(PropertyDefinition definition, String value,
			Properties properties) throws CodedException {
		if(!value.equals("") && value.indexOf("SSL_RSA_WITH_RC4_128_MD5")==-1)
			throw new CoreException(1,"ssl","errors");
	}

}
