package net.openvpn.als.core;

import java.util.Properties;

import net.openvpn.als.boot.CodedException;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyValidator;

public class SSLProtocolValidator implements PropertyValidator {
	public void validate(PropertyDefinition definition, String value,
			Properties properties) throws CodedException {
		if(!value.equals("") && value.indexOf("SSLv3")==-1)
			throw new CoreException(2,"ssl","errors");
	}
}
