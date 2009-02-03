
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
			

package com.adito.core.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.util.RequestUtils;

import com.adito.core.DefaultPanel;
import com.adito.core.Panel;
import com.adito.core.PanelManager;
import com.adito.core.forms.AbstractMultiFormDispatchForm;
import com.adito.policyframework.Permission;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;

/**
 */
public abstract class AbstractMultiFormDispatchAction extends AuthenticatedDispatchAction {
    private final Map<String, AuthenticatedDispatchAction> actions = new HashMap<String, AuthenticatedDispatchAction>();
    private final int placement_;

    /**
     * Constructor
     * 
     * @param resourceType
     * @param permissions
     * @param placement
     */
    public AbstractMultiFormDispatchAction(ResourceType resourceType, Permission permissions[], int placement) {
        super(resourceType, permissions);
        placement_ = placement;
    }

    @SuppressWarnings("unchecked")
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response, String name) throws Exception {
        Collection<Panel> panels = PanelManager.getInstance().getPanels(placement_, request, response, DefaultPanel.MAIN_LAYOUT);
        Collection<SubActionWrapper> subActions = getSubActions(mapping, form, request, panels);

        AbstractMultiFormDispatchForm dispatchForm = (AbstractMultiFormDispatchForm) form;
        dispatchForm.init(subActions, mapping, request);
        
        ActionForward fwd = provideActionTargets(subActions, dispatchForm, request, response, name);
        if(fwd != null) {
            return fwd;
        }

        // Now do the normal dispatch handling. This is what determines the
        // forward
        if (null == name)
            return unspecified(mapping, form, request, response);

        return getActionForward(mapping, form, request, response, name);
    }

    private Collection<SubActionWrapper> getSubActions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                       Collection<Panel> panels) throws ClassNotFoundException,
                    IllegalAccessException, InstantiationException, InvocationTargetException {
        Collection<SubActionWrapper> subActions = new ArrayList<SubActionWrapper>();
        int index = 0;
        for (Panel panel : panels) {
            /*
             * First get all of the availble action mappings and create all of
             * the forms First try to find the panels action config. Look for
             * the same name as the panel id prefixed by a /
             */
            ActionConfig config = mapping.getModuleConfig().findActionConfig("/" + panel.getId());
            if (null != config && config instanceof ActionMapping) {
                ActionMapping subMapping = (ActionMapping) config;
                SubActionWrapper subActionWrapper = getSubActionWrapper(mapping, form, subMapping, request, index);
                if (null != subActionWrapper)
                    subActions.add(subActionWrapper);
            }
            index++;
        }
        return subActions;
    }

    private static SubActionWrapper getSubActionWrapper(ActionMapping mapping, ActionForm form, ActionMapping subMapping,
                                                        HttpServletRequest request, int index) throws ClassNotFoundException,
                    IllegalAccessException, InstantiationException, InvocationTargetException {
        String formName = subMapping.getName();
        ActionForm subForm = getActionForm(subMapping, request);

        FormBeanConfig formBean = mapping.getModuleConfig().findFormBeanConfig(formName);
        String className = formBean == null ? null : formBean.getType();
        if (className == null)
            return null;

        if (subForm == null || !className.equals(subForm.getClass().getName()))
            subForm = (ActionForm) Class.forName(className).newInstance();

        if ("request".equals(mapping.getScope()))
            request.setAttribute(formName, subForm);
        else
            request.getSession().setAttribute(formName, subForm);

        subForm.reset(mapping, request);

        /*
         * We dont want to try and populate all forms on a post, only the one
         * that has requested it. For this the form must have a hidden parameter
         * with the name of 'subForm' and the value being the form name to
         * populate
         */
        AbstractMultiFormDispatchForm dispatchForm = (AbstractMultiFormDispatchForm) form;
        if (formName.equals(dispatchForm.getSubForm())) {
            dispatchForm.setSelectedTab(dispatchForm.getTabName(index));
            BeanUtils.populate(subForm, request.getParameterMap());
        }

        return new SubActionWrapper(subForm, subMapping);
    }

    private static ActionForm getActionForm(ActionMapping subMapping, HttpServletRequest request) {
        String formName = subMapping.getName();
        if ("request".equals(subMapping.getScope()))
            return (ActionForm) request.getAttribute(formName);
        else
            return (ActionForm) request.getSession().getAttribute(formName);
    }

    private ActionForward provideActionTargets(Collection<SubActionWrapper> subActions, AbstractMultiFormDispatchForm dispatchForm,
                                      HttpServletRequest request, HttpServletResponse response, String name)
                    throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        if (null == name || name.equals(""))
            name = "unspecified";
		// JDR added the ability to refresh all sub forms.
        boolean refreshAll = new Boolean(request.getParameter("refreshAll"));
        
        String subForm = dispatchForm.getSubForm();
        ActionForm subform = (ActionForm) request.getSession().getAttribute(subForm);
        ActionForward fwd = null;

        // Now try to create actions and run the provided action target on each.
        // Any forwards returned are ignored
        for (SubActionWrapper wrapper : subActions) {
            try {
                    // We have an action config, so get the action instance
                    Action action = processActionCreate(request, response, wrapper.getMapping());
                    // Identify the method object to be dispatched to
                    if (dispatchForm.isSubFormEmpty() || wrapper.getForm().equals(subform) || refreshAll) {
                        Method method = getMethod(action, name);
                        Object args[] = { wrapper.getMapping(), wrapper.getForm(), request, response };
                        // Invoke the method. We don't care about the forward
                        // returned,
                        // it will not be used.
                        if(wrapper.getForm().equals(subform)) {
                            fwd = (ActionForward)method.invoke(action, args);
                        }
                        else {
                            method.invoke(action, args);
                        }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        
        return fwd;
    }

    private Method getMethod(Action action, String name) throws NoSuchMethodException {
        synchronized (methods) {
            String methodName = action.getClass().getName() + "#" + name;
            if (!methods.containsKey(methods)) {
                Method method = action.getClass().getMethod(name, types);
                methods.put(methodName, method);
            }
            return (Method) methods.get(methodName);
        }
    }

    private Action processActionCreate(HttpServletRequest request, HttpServletResponse response, ActionConfig mapping)
                    throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        synchronized (actions) {
            String className = mapping.getType();
            if (!actions.containsKey(className)) {
                if (log.isTraceEnabled())
                    log.trace("  Creating new Action instance");
                AuthenticatedDispatchAction instance = (AuthenticatedDispatchAction) RequestUtils.applicationInstance(className);
                instance.setServlet(servlet);
                actions.put(className, instance);
            }
            return actions.get(className);
        }
    }

    private ActionForward getActionForward(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response, String name) throws Exception {
        try {
            Method method = getMethod(name);
            Object args[] = { mapping, form, request, response };
            return (ActionForward) method.invoke(this, args);
        } catch (ClassCastException e) {
            log.error(getMessage(mapping, name, "dispatch.return"), e);
            throw e;
        } catch (IllegalAccessException e) {
            log.error(getMessage(mapping, name, "dispatch.error"), e);
            throw e;
        } catch (NoSuchMethodException e) {
            return unspecified(mapping, form, request, response);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                log.error(getMessage(mapping, name, "dispatch.error"), e);
                throw new ServletException(t);
            }
        }
    }

    private static String getMessage(ActionMapping mapping, String name, String message) {
        return messages.getMessage(message, mapping.getPath(), name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return mapping.findForward("display");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public static final class SubActionWrapper {
        private final ActionForm form_;
        private final ActionMapping mapping_;

        SubActionWrapper(ActionForm form, ActionMapping mapping) {
            form_ = form;
            mapping_ = mapping;
        }

        public ActionForm getForm() {
            return form_;
        }

        public ActionMapping getMapping() {
            return mapping_;
        }
    }
}