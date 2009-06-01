
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.replacementproxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.openvpn.als.boot.Replacer;


public class BaseSearch implements Replacer {
    private String base;

    public String getBase() {
        return base;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.services.Replacer#getReplacement(java.util.regex.Pattern,
     *      java.util.regex.Matcher, java.lang.String)
     */
    public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
        base = matcher.group(2);
        return "";
    }

}