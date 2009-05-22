
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
			
package com.ovpnals.replacementproxy;

/**
 * Default implementation of the {@link com.ovpnals.replacementproxy.Replacement}
 * interface.
 */

public class DefaultReplacement implements Replacement {

    private String mimeType, matchPattern, replacePattern, sitePattern;
    private int sequence;
    private int replaceType;

    /**
     * Construct a new default replacement
     * 
     * @param mimeType mime type this replacement applies to (<code>null</code> for all)
     * @param replaceType the replacement type. See {@link Replacement#getReplaceType()} for
     * 			more details.
     * @param sequence sequence number of replacement, i.e. the order in which the replacements are performed.
     * @param sitePattern a regular expression used to determine if a replacement should be used for a particular URL.
     * @param matchPattern regular expression to locate content to replace.
     * @param replacePattern extension regular expression replacement pattern generate content for the replacement.
     */
    public DefaultReplacement(String mimeType, int replaceType, int sequence, String sitePattern, String matchPattern,
                    String replacePattern) {
        super();
        this.replaceType = replaceType;
        this.mimeType = mimeType;
        this.sequence = sequence;
        this.sitePattern = sitePattern;
        this.matchPattern = matchPattern;
        this.replacePattern = replacePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getMimeType()
     */
    public String getMimeType() {
        return mimeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getSitePattern()
     */
    public String getSitePattern() {
        return sitePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getSequence()
     */
    public int getSequence() {
        return sequence;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getMatchPattern()
     */
    public String getMatchPattern() {
        return matchPattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getReplacePattern()
     */
    public String getReplacePattern() {
        return replacePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#setMatchPattern(java.lang.String)
     */
    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#setMimeType(java.lang.String)
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#setReplacePattern(java.lang.String)
     */
    public void setReplacePattern(String replacePattern) {
        this.replacePattern = replacePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#setSitePattern(java.lang.String)
     */
    public void setSitePattern(String sitePattern) {
        this.sitePattern = sitePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#getReplaceType()
     */
    public int getReplaceType() {
        return replaceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.services.Replacement#setReplaceType(int)
     */
    public void setReplaceType(int replaceType) {
        this.replaceType = replaceType;
    }

    /**
     * The the sequence number for this replacement, i.e. determines order
     * in which it is performed. The lower the sequence, the earlier the 
     * replacement will be performed.
     * 
     * @param sequence sequence
     */
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
