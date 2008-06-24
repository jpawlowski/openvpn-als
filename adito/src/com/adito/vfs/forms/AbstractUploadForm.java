
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
			
package com.adito.vfs.forms;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionMapping;

import com.adito.core.forms.CoreForm;
import com.adito.vfs.UploadDetails;

/**
 *  
 */
public class AbstractUploadForm extends CoreForm {

    private UploadDetails fileUpload;
    private int uploadId;
    
    public void initialise(int uploadId, UploadDetails fileUpload) {
        this.fileUpload = fileUpload;
        this.uploadId = uploadId;
    }
    
    public UploadDetails getUploadDetails() {
        return fileUpload;
    }
    
    public void setUploadDetails(UploadDetails fileUpload) {
        this.fileUpload = fileUpload;
    }
    
    public int getUploadId() {
        return uploadId;
    }
    
    public void setUploadId(int uploadId) {
        this.uploadId = uploadId;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
     */
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
    }
}
