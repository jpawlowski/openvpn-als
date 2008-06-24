package com.adito.core;

import java.util.Properties;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyValidator;

public class SSLProtocolValidator implements PropertyValidator {
	public void validate(PropertyDefinition definition, String value,
			Properties properties) throws CodedException {
		if(!value.equals("") && value.indexOf("SSLv3")==-1)
			throw new CoreException(2,"ssl","errors");
	}
}
