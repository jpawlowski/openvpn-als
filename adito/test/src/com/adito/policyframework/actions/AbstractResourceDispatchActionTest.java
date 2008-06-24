
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
			
package com.adito.policyframework.actions;

import java.util.Collection;

import org.apache.struts.action.ActionForm;

import com.adito.jdbc.DataAccessException;
import com.adito.policyframework.DuplicateResourceNameException;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Resource;
import com.adito.policyframework.forms.AbstractResourceForm;
import com.adito.services.ResourceService;
import com.adito.testcontainer.AbstractMockStrutsTestCase;
import com.adito.testcontainer.StrutsExecutionStep;

/**
 * @param <T>
 */
public abstract class AbstractResourceDispatchActionTest<T extends Resource> extends AbstractMockStrutsTestCase {
    private int selectedRealmId;
    private String initialRequestPath;
    private String requestPath;
    private String forwardPath;
    private String savedMessage;
    private Class<? extends AbstractResourceForm<T>> actionFormClass;
    private ResourceService<T> resourceService;

    /**
     * @param strutsConfigXml
     * @param extensions
     */
    public AbstractResourceDispatchActionTest(String strutsConfigXml, String extensions) {
        super(strutsConfigXml, extensions);
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        selectedRealmId = getSessionInfo().getRealmId();
    }

    /**
     * @return String
     */
    public final String getInitialRequestPath() {
        return initialRequestPath;
    }

    /**
     * @param initialRequestPath
     */
    public final void setInitialRequestPath(String initialRequestPath) {
        this.initialRequestPath = initialRequestPath;
    }

    /**
     * @return String
     */
    public final String getRequestPath() {
        return requestPath;
    }

    /**
     * @param requestPath
     */
    public final void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    /**
     * @return String
     */
    public final String getForwardPath() {
        return forwardPath;
    }

    /**
     * @param forwardPath
     */
    public final void setForwardPath(String forwardPath) {
        this.forwardPath = forwardPath;
    }

    /**
     * @return String
     */
    public final String getSavedMessage() {
        return savedMessage;
    }

    /**
     * @param savedMessage
     */
    public final void setSavedMessage(String savedMessage) {
        this.savedMessage = savedMessage;
    }

    /**
     * @return Class
     */
    public final Class<?> getActionFormClass() {
        return actionFormClass;
    }

    /**
     * @param actionFormClass
     */
    public final void setActionFormClass(Class<? extends AbstractResourceForm<T>> actionFormClass) {
        this.actionFormClass = actionFormClass;
    }

    /**
     * @return ResourceService
     */
    public final ResourceService<T> getResourceService() {
        return resourceService;
    }

    /**
     * @param resourceService
     */
    public final void setResourceService(ResourceService<T> resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testUnspecified() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();
        addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
        performActionAndVerifyPath(getInitialRequestPath(), "edit", getRequestPath());

        try {
            performActionAndVerifyTiles("unspecified");
            ActionForm actionForm = getActionForm();
            assertNotNull("Should not be null", actionForm);
            assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));

            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) actionForm;
            T formResource = resourceForm.getResource();
            assertEquals(resource, formResource);
        } finally {
            deleteResource(resource);
        }
    }
    
    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testCloneResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeCloneRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            T formResource = resourceForm.getResource();
            
            // can't call the standard assertEquals here as the names won't match
            assertEquals("resourceType should match", resource.getResourceType(), formResource.getResourceType());
            assertNotSame("resourceName should not match", resource.getResourceName(), formResource.getResourceName());
            assertTrue("resourceName should end with the original resource name", formResource.getResourceName().endsWith(resource.getResourceName()));
            assertEquals("resourceDescription should match", resource.getResourceDescription(), formResource.getResourceDescription());
            assertEquals("dataCreated should match", resource.getDateCreated(), formResource.getDateCreated());   
            assertResourceEquals(resource, formResource);
        } finally {
            deleteResource(resource);
        }
    }
    
    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testCloneCommitResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeCloneRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();

            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getInitialRequestPath());
            executionStep.addRequestParameter("actionTarget", "commit");
            executionStep.addMessage(getSavedMessage());
            executeStep(executionStep);

            T formResource = resourceForm.getResource();
            T byId = getResourceById(formResource.getResourceId());
            assertEquals(formResource, byId);
            deleteResource(byId);
        } finally {
            deleteResource(resource);
        }
    }
    
    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testCloneEditAndCommitResource() throws Exception {
        T resource = executeCloneRedirect();
        
        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            updateProperties(resourceForm);
            
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getInitialRequestPath());
            executionStep.addRequestParameter("actionTarget", "commit");
            executionStep.addMessage(getSavedMessage());
            executeStep(executionStep);

            T formResource = resourceForm.getResource();
            T byId = getResourceById(formResource.getResourceId());
            assertEquals(formResource, byId);
            deleteResource(byId);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     */
    public void testCreateResourceNotImplemented() {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "", getActualForward(), false);
        executionStep.addRequestParameter("actionTarget", "create");
        executeStep(executionStep);
    }

    /**
     */
    public void testCreateResourceImplemented() {
        navigateToCreatePage();
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testCreateResourceWithCommit() throws Exception {
        navigateToCreatePage();
        createResourceWithCommit();
    }

    @SuppressWarnings("unchecked")
    protected final void createResourceWithCommit() throws Exception {
        AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
        resourceForm.setResourceName("newResourceName");
        updateProperties(resourceForm);

        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getInitialRequestPath());
        executionStep.addRequestParameter("actionTarget", "commit");
        executionStep.addMessage(getSavedMessage());
        executeStep(executionStep);
        
        Collection<T> resources = getResources();
        assertEquals("Should be one resource", getInitialResourceCount() + 1, resources.size());

        T resource = getResourceById(resourceForm.getResource().getResourceId());
        assertEquals(resourceForm.getResource(), resource);
        deleteResource(resource);
    }
    
    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testViewResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            performActionAndVerifyTiles("view");
            ActionForm actionForm = getActionForm();
            assertNotNull("Should not be null", actionForm);
            assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));

            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) actionForm;
            assertTrue("Should be read only", resourceForm.getReadOnly());
            T formResource = resourceForm.getResource();
            assertEquals(resource, formResource);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testEditResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            performActionAndVerifyTiles("edit");
            ActionForm actionForm = getActionForm();
            assertNotNull("Should not be null", actionForm);
            assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));

            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) actionForm;
            T formResource = resourceForm.getResource();
            assertEquals(resource, formResource);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testEditNoResourceName() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            resourceForm.setResourceName("");
            testValidationFailure("error.createResource.missingName");
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testEditResourceNameTooLong() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            String resourceName = generateString(Resource.MAX_RESOURCE_NAME_LENGTH + 1);
            resourceForm.setResourceName(resourceName);
            testValidationFailure("error.createResource.resourceNameTooLong");
        } finally {
            deleteResource(resource);
        }
    }

    private static String generateString(int characters) {
        StringBuilder builder = new StringBuilder(characters);
        for (int index = 0; index < characters; index++) {
            builder.append("0");
        }
        return builder.toString();
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testEditNoResourceDescription() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            resourceForm.setResourceDescription("");
            testValidationFailure("error.createResource.missingDescription");
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testEditResourceNameInUse() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();
        T duplicateResource = getDefaultResource();
        duplicateResource.setResourceName("nameInUse");
        duplicateResource = createResource(duplicateResource);

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            resourceForm.setResourceName("nameInUse");
            testValidationFailure("error.createResource.resourceNameInUse");
        } finally {
            deleteResource(resource);
            deleteResource(duplicateResource);
        }
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testEditCommitResource() throws Exception {
        T resource = executeEditRedirect();

        try {
            AbstractResourceForm<T> resourceForm = (AbstractResourceForm<T>) getActionForm();
            updateProperties(resourceForm);

            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getInitialRequestPath());
            executionStep.addRequestParameter("actionTarget", "commit");
            executionStep.addMessage(getSavedMessage());
            executeStep(executionStep);
            
            T byId = getResourceById(resource.getResourceId());
            assertEquals(resourceForm.getResource(), byId);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     */
    public void testRefresh() {
        performActionAndVerifyPath(getRequestPath(), "refresh", getInitialRequestPath());
    }
    
    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    public void testDisplay() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            performActionAndVerifyTiles("display");
        } finally {
            deleteResource(resource);
        }
    }
    
    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    public void ZtestCancel() throws DuplicateResourceNameException, NoPermissionException {
        T resource = executeEditRedirect();

        try {
            performActionAndVerifyTiles("edit");
            performActionAndVerifyPath(getRequestPath(), "cancel", getInitialRequestPath());
        } finally {
            deleteResource(resource);
        }
    }
    
    protected final void updateProperties(AbstractResourceForm<T> resourceForm) throws Exception {
        resourceForm.setResourceDescription("newResourceDescription");
        updateResourceProperties(resourceForm);
    }
    
    protected abstract void updateResourceProperties(AbstractResourceForm<T> resourceForm) throws Exception;

    protected final void assertEquals(T original, T updated) {
        assertEquals("resourceType should match", original.getResourceType(), updated.getResourceType());
        assertEquals("resourceName should match", original.getResourceName(), updated.getResourceName());
        assertEquals("resourceDescription should match", original.getResourceDescription(), updated.getResourceDescription());
        assertEquals("dataCreated should match", original.getDateCreated(), updated.getDateCreated());     
        assertResourceEquals(original, updated);
    }
    
    /**
     * Subclasses can implement this method to compare the specific resource
     * attributes.
     * 
     * @param original
     * @param updated
     */
    protected abstract void assertResourceEquals(T original, T updated);
    
    protected final T executeCloneRedirect() throws DuplicateResourceNameException {
        T resource = createResource();
        addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
        performActionAndVerifyPath(getInitialRequestPath(), "clone", getRequestPath());
        
        performActionAndVerifyTiles("clone");
        ActionForm actionForm = getActionForm();
        assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));
        return resource;
    }
    
    protected final T executeCloneRedirectNotImplemented() throws DuplicateResourceNameException {
        T resource = createResource();
        addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
        performActionAndVerifyPath(getInitialRequestPath(), "clone", "");
        
        performActionAndVerifyTiles("clone");
        ActionForm actionForm = getActionForm();
        assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));
        return resource;
    }
    
    protected final T executeEditRedirect() throws DuplicateResourceNameException {
        T resource = createResource();
        addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
        performActionAndVerifyPath(getInitialRequestPath(), "edit", getRequestPath());

        performActionAndVerifyTiles("edit");
        ActionForm actionForm = getActionForm();
        assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));
        return resource;
    }

    protected void navigateToCreatePage() {
        performActionAndVerifyTiles("create");
        ActionForm actionForm = getActionForm();
        assertTrue("Should be of type " + getActionFormClass().getName(), actionForm.getClass().isAssignableFrom(getActionFormClass()));
    }

    protected final void testValidationFailure(String errorMessage) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(requestPath, "display", getForwardPath());
        executionStep.addRequestParameter("actionTarget", "commit");
        executionStep.addMessage("info.requiredFieldIndicator");
        executionStep.addError(errorMessage);
        executeStep(executionStep);
    }
    
    protected final void performActionAndVerifyTiles(String actionTarget) {
        performActionAndVerifyTiles(getRequestPath(), actionTarget, getForwardPath());
    }

    protected final void performActionAndVerifyTiles(String requestPath, String actionTarget, String forwardPath) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(requestPath, "display", forwardPath);
        executionStep.addRequestParameter("actionTarget", actionTarget);
        executeStep(executionStep);
    }

    protected final void performActionAndVerifyPath(String requestPath, String actionTarget, String forwardPath) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(requestPath, forwardPath);
        executionStep.addRequestParameter("actionTarget", actionTarget);
        executeStep(executionStep);
    }

    protected final Collection<T> getResources() throws DataAccessException, NoPermissionException {
        return resourceService.getResources(getSessionInfo());
    }

    protected final T getResourceById(int resourceId) {
        return resourceService.getResourceById(resourceId);
    }

    protected final T createResource() throws DuplicateResourceNameException {
        return createResource(getDefaultResource());
    }

    protected final T createResource(T resource) throws DuplicateResourceNameException {
        return resourceService.createResource(resource, getSessionInfo());
    }

    protected final void deleteResource(T resource) throws NoPermissionException {
        resourceService.removeResource(resource.getResourceId(), getSessionInfo());
    }

    protected final T getDefaultResource() {
        return getDefaultResource(selectedRealmId);
    }

    protected abstract T getDefaultResource(int selectedRealmId);

    /**
     * Override this method if, by default, there are entries in the database.
     * 
     * @return resource count, representing how many entries are expected by default.
     */
    protected int getInitialResourceCount() {
        return 0;
    }
}