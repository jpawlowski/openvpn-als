
				/*
 *  OpenVPN-ALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.ovpnals.jdbc.hsqldb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maverick.crypto.encoders.Base64;
import com.ovpnals.boot.ReplacementEngine;

/**
 * Utility class containing static methods that are registered with the embedded
 * HSQLDB server as functions.
 */
public class DBFunctions {

    /**
     * Tests whether a string matches a supplied regular expression and returns
     * the text if it does.
     * 
     * @param text text to match
     * @param regex regular expression
     * @return text
     */
    public static String matches(String text, String regex) {
        Pattern pattern = ReplacementEngine.getPatternPool().getPattern(regex, false, false);
        try {
            Matcher matcher = pattern.matcher(text);
            return matcher.find() ? text : "";
        } finally {
            ReplacementEngine.getPatternPool().releasePattern(pattern);
        }
    }

    /*
     * This is required to make fresh installs work correctly. As of 0.1.4,
     * passwords became BASE64 encoded and now use the ENCPASSWORD function.
     */
    public static String password(String text) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return digest(text);
    }

    /**
     * Encode a password one-way by first by producing an MD5 hash of the password
     * then encoding that in BASE64.
     * 
     * @param password password to encode
     * @return encoded password
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String encPassword(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String encoded = encode(digest(password));
        return encoded;
    }

    /**
     * Encode a string as BASE64.
     * 
     * @param string string to encode
     * @return encoded string
     */
    public static String encode(String string) {
        return new String(Base64.encode(string.getBytes()));
    }

    /**
     * Decode a string from BASE64 to its original form.
     * 
     * @param string string to decode
     * @return decoded string
     */
    public static String decode(String string) {
        return new String(Base64.decode(string.getBytes()));
    }

    /**
     * Create an MD5 hash of the provided string
     * 
     * @param string string to hash
     * @return hashed string
     */
    public static String digest(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] b = string.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(b);
        byte[] digest = md.digest();
        return new String(digest);
    }

}
