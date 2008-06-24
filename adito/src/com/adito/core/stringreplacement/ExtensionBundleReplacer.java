
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
			
package com.adito.core.stringreplacement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adito.extensions.ExtensionBundle;

public class ExtensionBundleReplacer extends AbstractReplacementVariableReplacer {

	private ExtensionBundle bundle;

	public ExtensionBundleReplacer(ExtensionBundle bundle) {
		super();
		this.bundle = bundle;
	}

	public static String replace(ExtensionBundle bundle, String input) {
		VariableReplacement r = new VariableReplacement();
		r.setExtensionBundle(bundle);
		return r.replace(input);
	}

	public String processReplacementVariable(Pattern pattern, Matcher matcher,
			String replacementPattern, String type, String key)
			throws Exception {
		if (type.equalsIgnoreCase("bundle")) {
			if (bundle == null) {
				return null;
			}
			if (key.equals("id")) {
				return bundle.getId();
			} else if (key.equals("name")) {
				return bundle.getName();
			} else if (key.equals("description")) {
				return bundle.getDescription();
			} else if (key.equals("baseDir")) {
				if (VariableReplacement.log.isDebugEnabled())
					VariableReplacement.log.debug("Replacing base dir with "
							+ bundle.getBaseDir().getCanonicalPath());
				return bundle.getBaseDir().getCanonicalPath();
			} else {
				throw new Exception("Unknown key " + key + " for type " + type
						+ ".");
			}
		}
		return null;
	}
}