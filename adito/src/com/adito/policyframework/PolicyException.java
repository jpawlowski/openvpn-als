package com.adito.policyframework;

import com.adito.core.CoreException;

/**
 * Specialisation of {@link CoreException} for exceptions generate during
 * use of the policy framework.
 */
public class PolicyException extends CoreException {
	
	/**
	 * General internal error
	 */
	public final static int INTERNAL_ERROR = 1;
	
	/**
	 * Error category
	 */
	public final static String ERROR_CATEGORY = "policyframework";

    /**
     * Constructor.
     *
     * @param code
     */
    public PolicyException(int code) {
        super(code, ERROR_CATEGORY);
    }

    /**
     * Constructor.
     *
     * @param code
     * @param message
     * @param cause
     */
    public PolicyException(int code, String message, Throwable cause) {
        super(code, ERROR_CATEGORY, message, cause);
    }

    /**
     * Constructor.
     *
     * @param code
     * @param message
     */
    public PolicyException(int code, String message) {
        super(code, ERROR_CATEGORY, message);
    }

    /**
     * Constructor.
     *
     * @param code
     * @param cause
     */
    public PolicyException(int code, Throwable cause) {
        super(code, ERROR_CATEGORY, cause);
    }
}
