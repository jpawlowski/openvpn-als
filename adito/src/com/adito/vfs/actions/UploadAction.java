
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
			
package com.adito.vfs.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.core.RedirectWithMessages;
import com.adito.core.UploadHandler;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.vfs.UploadDetails;
import com.adito.vfs.VfsUtils;
import com.adito.vfs.forms.UploadForm;
import com.adito.vfs.utils.UploadHandlerFactory;

/**
 * Implementation of {@link AuthenticatedAction} that accepts an HTTP upload
 * and stores it somewhere.
 * <p>
 * The upload itself is delegated to an {@link UploadHandler}. This is created by
 * the {@link UploadHandlerFactory} using the type attribute provided by the
 * current {@link UploadDetails}.
 * <p>
 * The upload details themselves are retrieved using the <i>uploadId</i>
 * attribute of the associated form. This should have been set by the previous
 * action {@link ShowUploadAction}.
 */
public class UploadAction extends AuthenticatedAction {

    static Log log = LogFactory.getLog(UploadAction.class);

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	if (log.isDebugEnabled())
    		log.debug("Execute file upload.");
        UploadForm uploadForm = (UploadForm) form;
        int uploadId = uploadForm.getUploadId();
        UploadDetails upload = CoreUtil.getUpload(request.getSession(), uploadId);
        if (upload == null) {
            throw new Exception("No file upload details configured for upload id " + uploadId + ".");
        }
        int sessionTimeoutBlockId = LogonControllerFactory.getInstance().addSessionTimeoutBlock(request.getSession(),
            "Upload request.");
        try {
            UploadHandler handler = UploadHandlerFactory.getInstance().getUploader(upload.getType());
            if (handler == null) {
                throw new Exception("No handler for upload type " + upload.getType());
            }
            request.setAttribute(Constants.REQ_ATTR_UPLOAD_DETAILS, new Integer(uploadId));
            
            boolean isFileExist = handler.checkFileToUpload(request, response, upload, uploadForm.getUploadFile());
            if (isFileExist) {
                request.getSession().setAttribute("uploadForm", uploadForm);
                return new ActionForward("/confirmFileUpload.do?arg0=" + uploadForm.getUploadFile().getFileName());
            } else {
                return confirmUpload(mapping, form, request, response);
            }

        } catch (Exception e) {
            String stacktraceAsString = VfsUtils.maskSensitiveArguments(CoreUtil.toString(e));
            log.error("Failed to upload:" + stacktraceAsString);
            
            String exceptionMessage = VfsUtils.maskSensitiveArguments(e.getMessage());
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("vfs", "upload.info.fileNotUploaded", exceptionMessage));
            saveErrors(request, msgs);
            if (null != upload.getUploadedForward())
                return upload.getUploadedForward();
            else 
                throw e;
        } finally {
            LogonControllerFactory.getInstance().removeSessionTimeoutBlock(request.getSession(), sessionTimeoutBlockId);
        }
    }
    
    public ActionForward confirmUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Execute file upload.");
        UploadForm uploadForm = (UploadForm) form;
        int uploadId = uploadForm.getUploadId();
        UploadDetails upload = CoreUtil.getUpload(request.getSession(), uploadId);
        if (upload == null) {
            throw new Exception("No file upload details configured for upload id " + uploadId + ".");
        }
        int sessionTimeoutBlockId = LogonControllerFactory.getInstance().addSessionTimeoutBlock(request.getSession(),
            "Upload request.");
        
        try {
            UploadHandler handler = UploadHandlerFactory.getInstance().getUploader(upload.getType());
            if (handler == null) {
                throw new Exception("No handler for upload type " + upload.getType());
            }
            request.setAttribute(Constants.REQ_ATTR_UPLOAD_DETAILS, new Integer(uploadId));

            ActionForward fwd = handler.performUpload(request, response, upload, uploadForm.getUploadFile());
            ActionMessages msgs = new ActionMessages();
            if(upload.getResourcePath()==null) {
                msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("vfs", "upload.info.fileUploadedNoPath", uploadForm.getUploadFile()
                        .getFileName()));
            } else {
                msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("vfs", "upload.info.fileUploaded", uploadForm.getUploadFile()
                            .getFileName(), upload.getResourcePath()));
            }
            saveMessages(request, msgs);
            request.setAttribute(Constants.REQ_ATTR_UPLOAD_DETAILS, uploadId);
            uploadForm.setUploadId(uploadId);
            return fwd;
        }
        catch (CoreException ce) {
            log.error("Failed to upload.", ce);
            ActionMessages errs = getErrors(request);
            errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
            saveErrors(request, errs);
            if (null != upload.getUploadedForward())
                return new RedirectWithMessages(upload.getUploadedForward(), request);
            else 
                throw ce;
        }        
        catch (Exception e) {
            log.error("Failed to upload.", e);
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("vfs", "upload.info.fileNotUploaded", e.getMessage()));
            saveErrors(request, msgs);
            if (null != upload.getUploadedForward())
                return upload.getUploadedForward();
            else 
                throw e;
        } finally {
            LogonControllerFactory.getInstance().removeSessionTimeoutBlock(request.getSession(), sessionTimeoutBlockId);
        }
        
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}