
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
			
package com.adito.webforwards;

import java.util.Calendar;

import org.junit.Test;

public class ReverseProxyWebForwardTests extends AbstractWebForwardTests {

    @Override
    public WebForward getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new ReverseProxyWebForward(-1, 1, WebForward.TYPE_HOST_BASED_REVERSE_PROXY, "", "", "", "", "", "", "", "", "", "", "", false, false, calendar, calendar, "");
    }

    @Override
    public WebForward getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new ReverseProxyWebForward(getDefaultRealm().getRealmID(), 1, WebForward.TYPE_HOST_BASED_REVERSE_PROXY, "http://mail.3sp.co.uk", "OWA", "Outlook web access.", "General", "", "", "None", "", "", "", "", false, false, calendar, calendar, "");
    }

    @Override
    public WebForward getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new ReverseProxyWebForward(-1, 1, WebForward.TYPE_HOST_BASED_REVERSE_PROXY, null, null, null, null, null, null, null, null, null, null, null, false, false, calendar, calendar, null);
    }

    @Test 
    public void mustHaveTests() {
    }
}