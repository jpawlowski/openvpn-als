package com.ovpnals.core;

import java.util.Properties;

import com.ovpnals.boot.CodedException;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyValidator;

public class SSLProtocolValidator implements PropertyValidator {
	public void validate(PropertyDefinition definition, String value,
			Properties properties) throws CodedException {
		if(!value.equals("") && value.indexOf("SSLv3")==-1)
			throw new CoreException(2,"ssl","errors");
	}
}
