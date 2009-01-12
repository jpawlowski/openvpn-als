package com.adito.tasks.actions;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreRequestProcessor;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.core.filters.GZIPResponseWrapper;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.tasks.AbstractTask;
import com.adito.tasks.Task;
import com.adito.tasks.TaskException;
import com.adito.tasks.TaskHttpServletRequest;
import com.adito.tasks.TaskHttpServletResponse;
import com.adito.tasks.TaskManager;
import com.adito.tasks.TaskProgressBar;
import com.adito.tasks.forms.TaskProgressForm;
import com.adito.util.Utils;

public class TaskProgressAction extends AuthenticatedDispatchAction {
    
    static HttpSession SESSION_DEBUG_TEST = null;
    
    final static Log log = LogFactory.getLog(TaskProgressAction.class);

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(response instanceof GZIPResponseWrapper) {
            ((GZIPResponseWrapper)response).setCompress(false);
        }
        Task task = TaskManager.getInstance().getTask(Integer.parseInt(request.getParameter("id")));
        if(task == null) {
            log.warn("Unknown task ID requested.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(request.getSession().isNew()) {
            log.warn("Update request must not be a new session.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(ContextHolder.getContext().isSetupMode()) {
            // No need to check session
        }
        else {
            SessionInfo sessionInfo = 
                LogonControllerFactory.getInstance().getSessionInfo(request);
            if(sessionInfo != task.getSession()) {
                log.warn("Not task owner.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }
        task.waitForConfiguration();
        String xml = buildXML(request, task);
        response.setContentType("text/xml");
        byte[] arr = xml.getBytes(request.getCharacterEncoding() == null ? "utf-8" : request.getCharacterEncoding());
        response.setContentLength(arr.length);
        Util.noCache(response);
        response.getOutputStream().write(arr);
        
        // Only remove the task when client knows it has completed
        if(task.isComplete()) {
            TaskManager.getInstance().remove(task);
        }
        return null;
    }

    private String buildXML(HttpServletRequest request, Task task) {
        StringBuffer xml = new StringBuffer();
        xml.append("<task>");
        xml.append("<taskId>");
        xml.append(task.hashCode());
        xml.append("</taskId>");
        xml.append("<taskComplete>");
        xml.append(task.isComplete());
        xml.append("</taskComplete>");
        if(task.isComplete()) {
            String onFinish = task.getOnFinish();
            if(!Util.isNullOrTrimmedBlank(onFinish)) {
                xml.append("<onFinish>");
                xml.append(Util.urlEncode(onFinish));
                xml.append("</onFinish>");
            }
        }
        xml.append("<progressBars>");
        Collection<TaskProgressBar> progressBars = task.getProgressBars();
        for(TaskProgressBar bar : progressBars) {
            xml.append("<progressBar>");
            xml.append("<progressBarId>");
            xml.append(bar.getId());
            xml.append("</progressBarId>");
            xml.append("<progressBarMin>");
            xml.append(bar.getMinValue());
            xml.append("</progressBarMin>");
            xml.append("<progressBarMax>");
            xml.append(bar.getMaxValue());
            xml.append("</progressBarMax>");
            xml.append("<progressBarValue>");
            xml.append(bar.getValue());
            xml.append("</progressBarValue>");
            xml.append("<progressBarNote>");
            if(bar.getNote() != null) {
                BundleActionMessage bam = bar.getNote();
                MessageResources mr = CoreUtil.getMessageResources(request.getSession(), bam.getBundle());
                Locale l = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
                xml.append(Utils.htmlescape(mr.getMessage(l, bar.getNote().getKey(), 
                    bam.getArg0(), bam.getArg1(), bam.getArg2(), bam.getArg3())));
            }
            else {
                xml.append("No note");
            }
            xml.append("</progressBarNote>");
            xml.append("</progressBar>");
        }
        xml.append("</progressBars>");        
        xml.append("</task>\n");
        return xml.toString();
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String bundle = request.getParameter("bundle");
        if(response instanceof GZIPResponseWrapper) {
            ((GZIPResponseWrapper)response).setCompress(false);
        }
        if(bundle == null) {
            throw new Exception("No bundle parameter");
        }
        String name = request.getParameter("name");
        if(name == null) {
            throw new Exception("No name parameter");
        }
        
        Task t = new WrappedServletTask(bundle, name, request, response);
        ((TaskProgressForm)form).setTask(t);
        TaskManager.getInstance().startTask(t, getSessionInfo(request));
        t.waitForConfiguration();
        Util.noCache(response);
        return mapping.findForward("display");
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

    class WrappedServletTask extends AbstractTask {
        private TaskHttpServletRequest req;
        private TaskHttpServletResponse res;
        
        WrappedServletTask(String bundle, String name, HttpServletRequest req, HttpServletResponse res) {
            super(bundle, name);
            this.req = new TaskHttpServletRequest(req, this);
            this.res = new TaskHttpServletResponse(res);
        }

        public void run() throws TaskException {
            try {
                CoreServlet.getServlet().service(req, res);
                String onFinish = getOnFinish();
                if(onFinish == null) {
                    throw new Exception("No forward returned to go to when task is complete.");
                }
                if(onFinish.startsWith(".")) {
                    throw new Exception("Forward returned to go to when task is complete is a tile (" + onFinish + "), this is not allowed. The forward must point an absolute or relative URL.");
                }
            } catch(Exception e) {
                throw new TaskException(TaskException.INTERNAL_ERROR, e);
            } 
        }

        public String getOnFinish() {
            ActionForward fwd = (ActionForward)req.getAttribute(TaskHttpServletRequest.ATTR_TASK_FORWARD); 
            return fwd == null ? null : fwd.getPath();
        }
    }
}
