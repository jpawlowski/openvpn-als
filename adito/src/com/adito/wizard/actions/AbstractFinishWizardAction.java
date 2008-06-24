package com.adito.wizard.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.policyframework.Permission;
import com.adito.policyframework.ResourceType;

/**
 * <p>
 * Abstract Extension of {@link AbstractWizardAction} with the new abstract
 * method exit.
 */
public abstract class AbstractFinishWizardAction extends AbstractWizardAction {


    /**
     * Constructor
     */
    public AbstractFinishWizardAction() {
        super();
    }

    /**
     * @param resourceType The resource type.
     * @param permissions The premissions.
     */
    public AbstractFinishWizardAction(ResourceType resourceType, Permission[] permissions) {
        super(resourceType, permissions);
    }

    /**
     * <p>
     * The method provides a abstract class for exiting the resource.
     * 
     * @param mapping The ActionMapping
     * @param form The ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return The ActionForward
     * @throws Exception Any exceptions
     */
    public abstract ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception;
}
