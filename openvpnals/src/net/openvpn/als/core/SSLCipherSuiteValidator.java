package net.openvpn.als.core;

import java.util.Properties;

import net.openvpn.als.boot.CodedException;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyValidator;

public class SSLCipherSuiteValidator implements PropertyValidator {

	public void validate(PropertyDefinition definition, String value,
			Properties properties) throws CodedException {
		if(!value.equals("") && value.indexOf("SSL_RSA_WITH_RC4_128_MD5")==-1)
			throw new CoreException(1,"ssl","errors");
	}

}
