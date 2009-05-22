
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
			
package com.ovpnals.replacementproxy.forms;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.replacementproxy.ReplacementItem;

public class ReplacementsForm extends CoreForm {

	List replacementItems;

	static Log log = LogFactory.getLog(ReplacementsForm.class);

	public ReplacementsForm() {
	}

	public void initialize(List replacementItems) {
		this.replacementItems = replacementItems;
	}

	public void reset(ActionMapping mapping,
			javax.servlet.http.HttpServletRequest request) {

	}

	public List getReplacementItems() {
		return replacementItems;
	}

	public void setReplacementItem(int idx, ReplacementItem item) {
		if (replacementItems != null) {
			replacementItems.set(idx, item);
		}
	}

	public ReplacementItem getReplacementItem(int idx) {
		return replacementItems == null ? null
				: (ReplacementItem) replacementItems.get(idx);

	}
}