package com.ovpnals.core;

public class FieldValidationException extends Exception {
    
	private String resourceKey;
	
	/**
	 * Constructor
	 * 
	 * @param resourceKey resource key to use for exception text
	 */
	public FieldValidationException(String resourceKey){
		super();                           // Call default Super Constructor
		this.resourceKey = resourceKey;
	}
	
	/**
	 * Get the resource key to use for the
	 * field validation exception text
	 * 
	 * @return resource key to use for message
	 */
	public String getResourceKey() {
		return resourceKey;
	}
}
