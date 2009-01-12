package com.adito.extensions;

import com.adito.boot.AbstractPropertyClass;
import com.adito.boot.AbstractPropertyKey;

/**
 * A simple property class to support application parameter definitions.
 * 
 * @author brett
 */
public class ApplicationParameters extends AbstractPropertyClass {
	
	/**
	 * Property class name
	 */
	public final static String NAME = "applicationParameters";

	/**
	 * Constructor
	 */
	public ApplicationParameters() {
		super(NAME, true);
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.AbstractPropertyClass#retrievePropertyImpl(com.adito.boot.AbstractPropertyKey)
	 */
	protected String retrievePropertyImpl(AbstractPropertyKey key)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not yet supported.");
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.AbstractPropertyClass#storePropertyImpl(com.adito.boot.AbstractPropertyKey, java.lang.String)
	 */
	protected String storePropertyImpl(AbstractPropertyKey key, String value)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not yet supported.");
	}

}
