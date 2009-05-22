package com.ovpnals.security;

import org.apache.struts.action.ActionForward;

public class InputRequiredException extends Exception {
	
	ActionForward forward;
	
	public InputRequiredException() {
		
	}
	public InputRequiredException(ActionForward forward) {
		this.forward = forward;
	}
	
	public ActionForward getForward() {
		return forward;
	}
}
