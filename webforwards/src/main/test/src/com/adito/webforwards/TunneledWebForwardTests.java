
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

public class TunneledWebForwardTests extends AbstractWebForwardTests {

    @Override
    public WebForward getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new TunneledSiteWebForward(-1, 1, "", "", "", "", false, calendar, calendar);
    }

    @Override
    public WebForward getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new TunneledSiteWebForward(getDefaultRealm().getRealmID(), 1, "http://localhost", "3sp", "Link to 3sp", "General", false, calendar, calendar);
    }

    @Override
    public WebForward getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new TunneledSiteWebForward(-1, 1, null, null, null, null, false, calendar, calendar);
    }

    @Test 
    public void mustHaveTests() {
    }
}