
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
			
package com.adito.extensions.forms;

import com.adito.core.forms.AbstractMultiFormDispatchForm;
import com.adito.extensions.store.ExtensionStoreDescriptor;

public final class ConfigureExtensionsForm extends AbstractMultiFormDispatchForm {
	static final long serialVersionUID = 258708989713991658L;
    
    public static final int EXTENSIONS_TAB_ID = 80;
    
    ExtensionStoreDescriptor descriptor;

	public ConfigureExtensionsForm() {
		super(EXTENSIONS_TAB_ID);
	}
    
    public ExtensionStoreDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(ExtensionStoreDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}