
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
			
package com.adito.vfs;

import org.apache.struts.action.ActionForward;


/**
 * <p>This class holds the details required for a file upload action.
*/
public class UploadDetails {
    private String acceptTypes;
    private boolean extract;
    private String messageKey, errorKey;
    private String resourcePath;
    private String type;
    
    private String extraAttribute1;
    private String extraAttribute2;
    private String extraAttribute3;
    
    private ActionForward uploadedForward;
    private ActionForward doneForward;
    private ActionForward cancelForward;
    
    private String bundle;

    /**
     * @param bundle the bundle that contains message resources for type
     * @param type The type of upload
     * @param uploadedForward Where the upload action is.
     * @param doneForward The forward to go to after the upload is done.
     */
    public UploadDetails(String bundle, String type, ActionForward uploadedForward, ActionForward doneForward) {
        this(bundle, type, null, uploadedForward, doneForward, null);
    }
    
    /**
     * @param bundle the bundle that contains message resources for type
     * @param type The type of upload
     * @param resourcePath The path of the resource to upload.
     * @param uploadedForward Where the upload action is.
     * @param doneForward The forward to go to after the upload is done.
     * @param cancelForward The forward to return to if the operation is canceled.
     */
    public UploadDetails(String bundle, String type, String resourcePath, ActionForward uploadedForward, ActionForward doneForward, ActionForward cancelForward) {
        this.type = type;
        this.bundle = bundle;
        this.resourcePath = resourcePath;
        this.uploadedForward = uploadedForward;
        this.doneForward = doneForward;
        this.cancelForward = cancelForward;
    }
    
    /**
     * Get the bundle that contains message resources for the type
     * 
     * @return bundle
     */
    public String getBundle() {
        return bundle;
    }
    
    /**
     * @return The ActionForwrd to the cancel page.
     */
    public ActionForward getCancelForward() {
        return cancelForward;
    }
    
    /**
     * @return The ActionForward to go to when the upload is done.
     */
    public ActionForward getDoneForward() {
        return doneForward;
    }
    
    /**
     * @return The ActionForward to the upload page.
     */
    public ActionForward getUploadedForward() {
        return uploadedForward;
    }

    /**
     * @return The type of upload to occur.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Set the type of upload to occur. 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param messageKey Set the message key.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * @return Get the message key.
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @param errorKey Set the error key.
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    /**
     * @return Get the error key.
     */
    public String getErrorKey() {
        return errorKey;
    }

    /**
     * @param acceptTypes The accepted types.
     */
    public void setAcceptTypes(String acceptTypes) {
        this.acceptTypes = acceptTypes;
    }

    /**
     * @return Get the accept types.
     */
    public String getAcceptTypes() {
        return acceptTypes;
    }

    /**
     * @param extract Set weather the upload file is to be exrtacted, i.e. a zip file.
     */
    public void setExtract(boolean extract) {
        this.extract = extract;
    }

    /**
     * @return get weather the file is to be extracted.
     */
    public boolean getExtract() {
        return extract;
    }
    
    /**
     * @param resourcePath Set the path to the resoource to be uploaded.
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    
    /**
     * @return Get the path to the resource to be uploaded.
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @return Get extra attriute1
     */
    public String getExtraAttribute1() {
        return extraAttribute1;
    }

    /**
     * @param extraAttribute1 Set extra attribute1
     */
    public void setExtraAttribute1(String extraAttribute1) {
        this.extraAttribute1 = extraAttribute1;
    }

    /**
     * @return Get extra attriute2
     */
    public String getExtraAttribute2() {
        return extraAttribute2;
    }

    /**
     * @param extraAttribute2 Set extra attrinute2.
     */
    public void setExtraAttribute2(String extraAttribute2) {
        this.extraAttribute2 = extraAttribute2;
    }

    /**
     * @return Get extra attrinute3.
     */
    public String getExtraAttribute3() {
        return extraAttribute3;
    }

    /**
     * @param extraAttribute3 Set extra attribute3
     */
    public void setExtraAttribute3(String extraAttribute3) {
        this.extraAttribute3 = extraAttribute3;
    }
}
