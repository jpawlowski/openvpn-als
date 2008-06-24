
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
			
package com.adito.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adito.security.Constants;
import com.adito.vfs.clipboard.Clipboard;

public class ClipboardPanel extends DefaultPanel {

	public ClipboardPanel() {
		super("clipboard", Panel.MESSAGES, 90, "/WEB-INF/jsp/tiles/clipboard.jspf", null, "navigation", true, true, true, true);
	}

	public boolean isAvailable(HttpServletRequest request, HttpServletResponse response, String layout) {
		boolean available = super.isAvailable(request, response, layout);
		if (available) {
			Clipboard c = (Clipboard) request.getSession().getAttribute(Constants.CLIPBOARD);
			available = c != null && c.getContent().size() > 0;
		}
		return available;
	}
}