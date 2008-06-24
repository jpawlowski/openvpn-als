
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
			
package com.adito.extensions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;

import com.adito.core.BundleActionMessage;
import com.adito.core.GlobalWarning;
import com.adito.core.GlobalWarningManager;
import com.adito.core.UploadHandler;
import com.adito.core.GlobalWarning.DismissType;
import com.adito.extensions.store.ExtensionStore;
import com.adito.vfs.UploadDetails;

public class ExtensionUploadHandler implements UploadHandler {

	public static final String TYPE_EXTENSION = "EXTENSION";

	public ActionForward performUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails fileUpload,
										FormFile file) throws IOException, Exception {
		String id = file.getFileName().replaceAll(".zip", "");
		ExtensionBundle bundle = null;
		try {
			bundle = ExtensionStore.getInstance().getExtensionBundle(id);
		} catch (Exception e) {
			// do nothing as there is no extention with the name.
		}
		if (bundle == null) {
			bundle = ExtensionStore.getInstance().installExtension(id, file.getInputStream());
			ExtensionStore.getInstance().licenseCheck(bundle, request, fileUpload.getUploadedForward());
			ExtensionStore.getInstance().postInstallExtension(bundle, request);
		} else {
			bundle = ExtensionStore.getInstance().updateExtension(id, file.getInputStream(), request, file.getFileSize());
			if (bundle.isContainsPlugin())
				GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
								"extensionStore.message.extensionUpdatedRestartRequired"), DismissType.DISMISS_FOR_USER));
		}

		return fileUpload.getUploadedForward();

	}

	public boolean checkFileToUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails fileUpload,
										FormFile file) throws IOException, Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
