package com.adito.input.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.adito.input.validators.TimeValidator;

public class TimeValidatorTest {
	 /**
     * @throws Exception
     */
    @Test
    public void badDateFormats() {
    	TimeValidator v = new TimeValidator();
    	assertTrue("Should parse", parse(v, "00:00"));
    	assertTrue("Should parse", parse(v, "00:01"));
    	assertTrue("Should parse", parse(v, "13:01"));
    	assertTrue("Should parse", parse(v, "23:59"));
    	assertFalse("Should not parse", parse(v, "24:00"));
    	assertFalse("Should not parse", parse(v, "a00:00"));
    	assertFalse("Should not parse", parse(v, "00:0a"));
    	assertFalse("Should not parse", parse(v, "23:21am"));
    	assertFalse("Should not parse", parse(v, "99:99"));
    }
    
    boolean parse(TimeValidator v, String time) {
    	try {
    		v.validate(null, time, null);
    		return true;
    	}
    	catch(Exception e) {
    		return false;
    	}
    }
}
