
				/*
 *  Adito
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
			
package com.maverick.http;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
class ParameterParser {

    /** String to be parsed */
    private char[] chars = null;

    /** Current position in the string */
    private int pos = 0;

    /** Maximum position in the string */
    private int len = 0;

    /** Start of a token */
    private int i1 = 0;

    /** End of a token */
    private int i2 = 0;

    /** Default ParameterParser constructor */
    public ParameterParser() {
        super();
    }

    /** Are there any characters left to parse? */
    private boolean hasChar() {
        return this.pos < this.len;
    }

    /** A helper method to process the parsed token. */
    private String getToken(boolean quoted) {
        // Trim leading white spaces
        while ((i1 < i2) && (Character.isWhitespace(chars[i1]))) {
            i1++;
        }
        // Trim trailing white spaces
        while ((i2 > i1) && (Character.isWhitespace(chars[i2 - 1]))) {
            i2--;
        }
        // Strip away quotes if necessary
        if (quoted) {
            if (((i2 - i1) >= 2) && (chars[i1] == '"') && (chars[i2 - 1] == '"')) {
                i1++;
                i2--;
            }
        }
        String result = null;
        if (i2 > i1) {
            result = new String(chars, i1, i2 - i1);
        }
        return result;
    }

    /** Is given character present in the array of characters? */
    private boolean isOneOf(char ch, char[] charray) {
        boolean result = false;
        for (int i = 0; i < charray.length; i++) {
            if (ch == charray[i]) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Parse out a token until any of the given terminators is encountered.
     */
    private String parseToken(final char[] terminators) {
        char ch;
        i1 = pos;
        i2 = pos;
        while (hasChar()) {
            ch = chars[pos];
            if (isOneOf(ch, terminators)) {
                break;
            }
            i2++;
            pos++;
        }
        return getToken(false);
    }

    /**
     * Parse out a token until any of the given terminators is encountered.
     * Special characters in quoted tokens are escaped.
     */
    private String parseQuotedToken(final char[] terminators) {
        char ch;
        i1 = pos;
        i2 = pos;
        boolean quoted = false;
        while (hasChar()) {
            ch = chars[pos];
            if (!quoted && isOneOf(ch, terminators)) {
                break;
            }
            if (ch == '"') {
                quoted = !quoted;
            }
            i2++;
            pos++;
        }
        return getToken(true);
    }

    /**
     * Extracts a list of {@link NameValuePair}s from the given string.
     * 
     * @param str the string that contains a sequence of name/value pairs
     * @return a list of {@link NameValuePair}s
     * 
     */
    public Vector parse(final String str, char separator) {

        if (str == null) {
            return new Vector();
        }
        return parse(str.toCharArray(), separator);
    }

    /**
     * Extracts a list of {@link NameValuePair}s from the given array of
     * characters.
     * 
     * @param chars the array of characters that contains a sequence of
     *        name/value pairs
     * 
     * @return a list of {@link NameValuePair}s
     */
    public Vector parse(final char[] chars, char separator) {

        if (chars == null) {
            return new Vector();
        }
        return parse(chars, 0, chars.length, separator);
    }

    /**
     * Extracts a list of {@link NameValuePair}s from the given array of
     * characters.
     * 
     * @param chars the array of characters that contains a sequence of
     *        name/value pairs
     * @param offset - the initial offset.
     * @param length - the length.
     * 
     * @return a list of {@link NameValuePair}s
     */
    public Vector parse(final char[] chars, int offset, int length, char separator) {

        if (chars == null) {
            return new Vector();
        }
        Vector params = new Vector();
        this.chars = chars;
        this.pos = offset;
        this.len = length;

        String paramName = null;
        String paramValue = null;
        while (hasChar()) {
            paramName = parseToken(new char[] { '=', separator });
            paramValue = null;
            if (hasChar() && (chars[pos] == '=')) {
                pos++; // skip '='
                paramValue = parseQuotedToken(new char[] { separator });
            }
            if (hasChar() && (chars[pos] == separator)) {
                pos++; // skip separator
            }
            if ((paramName != null) && (paramName.length() > 0)) {
                params.addElement(new NameValuePair(paramName, paramValue));
            }
        }
        return params;
    }

    public static Hashtable extractParams(String challengeStr) throws IOException {
        if (challengeStr == null) {
            throw new IllegalArgumentException(Messages.getString("ParameterParser.challendMayNotBeNull")); //$NON-NLS-1$
        }
        int idx = challengeStr.indexOf(' ');
        if (idx == -1) {
            throw new IOException(MessageFormat.format(Messages.getString("ParameterParser.invalidChallenge"), new Object[] { challengeStr })); //$NON-NLS-1$
        }
        Hashtable map = new Hashtable();
        ParameterParser parser = new ParameterParser();
        Vector params = parser.parse(challengeStr.substring(idx + 1, challengeStr.length()), ',');
        for (int i = 0; i < params.size(); i++) {
            NameValuePair param = (NameValuePair) params.elementAt(i);
            map.put(param.getName().toLowerCase(), param.getValue());
        }
        return map;
    }
}
