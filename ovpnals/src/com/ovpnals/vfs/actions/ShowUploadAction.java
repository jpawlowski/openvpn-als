package com.ovpnals.vfs.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.vfs.UploadDetails;
import com.ovpnals.vfs.forms.ShowUploadForm;

/**
 * <p>
 * This is the action which performes the upload of a given file to the location
 * in the {@link com.ovpnals.vfs.UploadDetails}.
 */
public class ShowUploadAction extends AuthenticatedDispatchAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return upload(mapping, form, request, response);
    }

    /**
     * Display the upload page.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        Integer i = (Integer) request.getAttribute(Constants.REQ_ATTR_UPLOAD_DETAILS);
        if (i == null) {
            throw new Exception("No upload id.");
        }
        UploadDetails upload = CoreUtil.getUpload(request.getSession(), i.intValue());
        if (upload == null) {
            throw new Exception("No file upload details configured for upload id " + i + ".");
        }
        ((ShowUploadForm) form).initialise(i.intValue(), upload);
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Uploading is complete. Clean up.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward uploadDone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        UploadDetails upload = CoreUtil.removeUpload(request.getSession(), ((ShowUploadForm) form).getUploadId());
        return upload.getDoneForward();
    }

    /**
     * Uploading is complete. Clean up.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        UploadDetails upload = CoreUtil.removeUpload(request.getSession(), ((ShowUploadForm) form).getUploadId());
        return upload.getCancelForward();
    }

}
