
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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.struts.action.ActionForm;

import com.adito.jdbc.DataAccessException;
import com.adito.policyframework.DuplicateResourceNameException;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceItem;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.security.SessionInfo;
import com.adito.services.ResourceService;
import com.adito.table.Pager;
import com.adito.table.TableItemModel;
import com.adito.testcontainer.AbstractMockStrutsTestCase;
import com.adito.testcontainer.StrutsExecutionStep;

/**
 * @param <T>
 * @param <R>
 */
public abstract class AbstractResourcesDispatchActionTest<T extends Resource, R extends ResourceItem<T>> extends
                AbstractMockStrutsTestCase {
    private int selectedRealmId;
    private String requestPath;
    private String forwardPath;
    private String editPath;
    private String confirmDeletePath;
    private String removedMessage;
    private Class<? extends AbstractResourcesForm<R>> actionFormClass;
    private ResourceService<T> resourceService;

    /**
     * @param strutsConfigXml
     * @param extensions
     */
    public AbstractResourcesDispatchActionTest(String strutsConfigXml, String extensions) {
        super(strutsConfigXml, extensions);
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        this.selectedRealmId = getSessionInfo().getRealmId();
    }

    protected final int getSelectedRealmId() {
        return selectedRealmId;
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
     * There are a couple of resources that potentially have a different path
     * for view, the rest use the edit path. By default we'll assume it's get
     * and the others can override this method.
     * 
     * @return viewPath
     */
    public String getViewPath() {
        return getEditPath();
    }

    /**
     * @return String
     */
    public final String getEditPath() {
        return editPath;
    }

    /**
     * @param editPath
     */
    public final void setEditPath(String editPath) {
        this.editPath = editPath;
    }

    /**
     * @return String
     */
    public final String getConfirmDeletePath() {
        return confirmDeletePath;
    }

    /**
     * @param confirmDeletePath
     */
    public final void setConfirmDeletePath(String confirmDeletePath) {
        this.confirmDeletePath = confirmDeletePath;
    }

    /**
     * @return String
     */
    public final String getRemovedMessage() {
        return removedMessage;
    }

    /**
     * @param removedMessage
     */
    public final void setRemovedMessage(String removedMessage) {
        this.removedMessage = removedMessage;
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
    public final void setActionFormClass(Class<? extends AbstractResourcesForm<R>> actionFormClass) {
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
     */
    public void testShowResource() {
        showResource();
    }

    /**
     */
    public void testShowResourceInUserConsole() {
        try {
            setNavigationContext(SessionInfo.USER_CONSOLE_CONTEXT);
            showResource();
        } finally {
            setNavigationContext(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT);
        }
    }

    @SuppressWarnings("unchecked")
    private void showResource() {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
        executeStep(executionStep);
        
        ActionForm actionForm = getActionForm();
        assertNotNull("Should not be null", actionForm);
        assertTrue("Should be of type " + actionFormClass.getName(), actionForm.getClass().isAssignableFrom(actionFormClass));

        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) actionForm;
        TableItemModel<R> model = resourcesForm.getModel();
        assertEquals("Should have " + getInitialResourceCount() + " row(s)", getInitialResourceCount(), model.getRowCount());
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testUnspecified() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();

        try {
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executeStep(executionStep);

            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            TableItemModel<R> model = resourcesForm.getModel();
            final int expectedRowCount = getInitialResourceCount() + 1;
            assertEquals("Should have " + expectedRowCount + " row(s)", expectedRowCount, model.getRowCount());
            assertResourceInModel(resource, model);
        } finally {
            deleteResource(resource);
        }
    }

    // TODO create, does anyone use this??

    /**
     * @throws Exception
     */
    public void testConfirmRemoveResource() throws Exception {
        T resource = createResource();

        try {
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getConfirmDeletePath());
            executionStep.addRequestParameter("actionTarget", "confirmRemove");
            executionStep.addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
            executeStep(executionStep);
            
            Collection<T> resources = getResources();
            assertEquals("Should be one resource", getInitialResourceCount() + 1, resources.size());
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws Exception
     */
    public void testRemoveResource() throws Exception {
        T resource = createResource();

        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getRequestPath());
        executionStep.addRequestParameter("actionTarget", "remove");
        executionStep.addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
        executionStep.addMessage(getRemovedMessage());
        executeStep(executionStep);
        
        Collection<T> resources = getResources();
        assertEquals("Should be zero resources", getInitialResourceCount(), resources.size());
    }

    /**
     * @throws Exception
     */
    public void testRemoveUnknownResource() throws Exception {
        performFailedAction("remove", "-1");
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    public void testViewResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();

        try {
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getViewPath());
            executionStep.addRequestParameter("actionTarget", "view");
            executionStep.addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
            executeStep(executionStep);
            
            T byId = getResourceById(resource.getResourceId());
            assertEquals("Resource should match", resource, byId);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    public void testResourceInformation() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();

        try {
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "/resourceInformation");
            executionStep.addRequestParameter("actionTarget", "information");
            executionStep.addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
            executeStep(executionStep);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    public void testEditResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();

        try {
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getEditPath());
            executionStep.addRequestParameter("actionTarget", "edit");
            executionStep.addRequestParameter("selectedResource", String.valueOf(resource.getResourceId()));
            executeStep(executionStep);
            
            T byId = getResourceById(resource.getResourceId());
            assertEquals("Resource should match", resource, byId);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     */
    public void testEditUnknownResource() {
        performFailedAction("edit", "-1");
    }

    // clone isn't in this test as it's covered as part of
    // AbstractResourceDispatchActionTest

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testListWithResource() throws DuplicateResourceNameException, NoPermissionException {
        T resource = createResource();

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "list");
            executeStep(executionStep);
            
            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            TableItemModel<R> model = resourcesForm.getModel();
            final int expectedRowCount = getInitialResourceCount() + 1;
            assertEquals("Should have " + expectedRowCount + " row(s)", expectedRowCount, model.getRowCount());
            assertResourceInModel(resource, model);
        } finally {
            deleteResource(resource);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testViewAsIcons() throws DuplicateResourceNameException, NoPermissionException {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
        executionStep.addRequestParameter("actionTarget", "viewIcons");
        executeStep(executionStep);
        
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
        assertEquals("Selected view should be list", AbstractResourcesForm.ICONS_VIEW, resourcesForm.getSelectedView());
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testViewAsList() throws DuplicateResourceNameException, NoPermissionException {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
        executionStep.addRequestParameter("actionTarget", "viewList");
        executeStep(executionStep);

        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
        assertEquals("Selected view should be list", AbstractResourcesForm.LIST_VIEW, resourcesForm.getSelectedView());
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testFilterWithSameStartingLetter() throws DuplicateResourceNameException, NoPermissionException {
        T toCreateOne = getDefaultResource();
        toCreateOne.setResourceName("XYZ resource");
        T toCreateTwo = getDefaultResource();
        toCreateTwo.setResourceName("XYZ Another resource");

        T resourceOne = createResource(toCreateOne);
        T resourceTwo = createResource(toCreateTwo);

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "filter");
            executionStep.addRequestParameter("filterText", "XYZ*");
            executeStep(executionStep);
            
            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            int filteredRowCount = resourcesForm.getPager().getFilteredRowCount();
            assertEquals("Should have 2 row(s)", 2, filteredRowCount);
        } finally {
            deleteResource(resourceOne);
            deleteResource(resourceTwo);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testFilterWithSameStartingLetterIsntCaseSensitive() throws DuplicateResourceNameException, NoPermissionException {
        T toCreateOne = getDefaultResource();
        toCreateOne.setResourceName("XYZ resource");
        T toCreateTwo = getDefaultResource();
        toCreateTwo.setResourceName("xyz Another resource");

        T resourceOne = createResource(toCreateOne);
        T resourceTwo = createResource(toCreateTwo);

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "filter");
            executionStep.addRequestParameter("filterText", "XYZ*");
            executeStep(executionStep);

            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            int filteredRowCount = resourcesForm.getPager().getFilteredRowCount();
            assertEquals("Should have 2 row(s)", 2, filteredRowCount);
        } finally {
            deleteResource(resourceOne);
            deleteResource(resourceTwo);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testFilterWithSameLetterSomewhere() throws DuplicateResourceNameException, NoPermissionException {
        T toCreateOne = getDefaultResource();
        toCreateOne.setResourceName("XYZ resource");
        T toCreateTwo = getDefaultResource();
        toCreateTwo.setResourceName("Resource XYZ");

        T resourceOne = createResource(toCreateOne);
        T resourceTwo = createResource(toCreateTwo);

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "filter");
            executionStep.addRequestParameter("filterText", "*XYZ*");
            executeStep(executionStep);

            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            int filteredRowCount = resourcesForm.getPager().getFilteredRowCount();
            assertEquals("Should have 2 row(s)", 2, filteredRowCount);
        } finally {
            deleteResource(resourceOne);
            deleteResource(resourceTwo);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testFilterWithUniqueLetter() throws DuplicateResourceNameException, NoPermissionException {
        T toCreateOne = getDefaultResource();
        toCreateOne.setResourceName("XYZ resource");
        T toCreateTwo = getDefaultResource();
        toCreateTwo.setResourceName("Different Resource");

        T resourceOne = createResource(toCreateOne);
        T resourceTwo = createResource(toCreateTwo);

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "filter");
            executionStep.addRequestParameter("filterText", "XYZ*");
            executeStep(executionStep);

            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            int filteredRowCount = resourcesForm.getPager().getFilteredRowCount();
            assertEquals("Should have 1 row", 1, filteredRowCount);
        } finally {
            deleteResource(resourceOne);
            deleteResource(resourceTwo);
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testMultiplePages() throws DuplicateResourceNameException, NoPermissionException {
        Collection<T> resources = createResourceBatch("Unique resource name", 15);

        try {
            performActionVerifyTiles("unspecified");
            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            Pager pager = resourcesForm.getPager();

            int filteredRowCount = pager.getFilteredRowCount();
            final int expectedRowCount = getInitialResourceCount() + 15;
            assertEquals("Should have " + expectedRowCount + " row(s)", expectedRowCount, filteredRowCount);

            assertFalse("Has previous page", pager.getHasPreviousPage());
            assertTrue("Has next page", pager.getHasNextPage());
            pager.nextPage();
            assertTrue("Has previous page", pager.getHasPreviousPage());
            assertFalse("Has next page", pager.getHasNextPage());
        } finally {
            for (T resource : resources) {
                deleteResource(resource);
            }
        }
    }

    /**
     * @throws DuplicateResourceNameException
     * @throws NoPermissionException
     */
    @SuppressWarnings("unchecked")
    public void testMultiplePagesWithFilter() throws DuplicateResourceNameException, NoPermissionException {
        T toCreateOne = getDefaultResource();
        toCreateOne.setResourceName("XYZ resource");
        T resourceOne = createResource(toCreateOne);
        Collection<T> resources = createResourceBatch("Unique resource name", 15);

        try {
            performActionVerifyTiles("unspecified");
            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", getForwardPath());
            executionStep.addRequestParameter("actionTarget", "filter");
            executionStep.addRequestParameter("filterText", "XYZ*");
            executeStep(executionStep);

            AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) getActionForm();
            Pager pager = resourcesForm.getPager();
            int filteredRowCount = pager.getFilteredRowCount();
            assertEquals("Should have 1 row", 1, filteredRowCount);

            assertFalse("Has previous page", pager.getHasPreviousPage());
            assertFalse("Has next page", pager.getHasNextPage());
        } finally {
            deleteResource(resourceOne);
            deleteResourceBatch(resources);
        }
    }

    protected final void assertResourceInModel(T resource, TableItemModel<R> model) {
        Collection<R> items = model.getItems();
        for (R item : items) {
            if (resource.equals(item.getResource())) {
                return;
            }
        }
        fail("resource was not found in model");
    }

    protected final void performFailedAction(String actionTarget, String selectedResource) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "", "/showException", false);
        executionStep.addRequestParameter("actionTarget", actionTarget);
        executionStep.addRequestParameter("selectedResource", selectedResource);
        executeStep(executionStep);
    }

    /**
     * Some actions have targets which simply redirect back to the start point.
     * One example are toggle actions (e.g. clicking a check box to make other
     * components enabled) which simply redirect to the original page. This is a
     * helper method to allow these actions to be tested more easily.
     * 
     * @param actionTarget
     * @param selectedResource
     */
    protected final void performRedirectAction(String actionTarget, String selectedResource) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getForwardPath());
        executionStep.addRequestParameter("actionTarget", actionTarget);
        executionStep.addRequestParameter("selectedResource", selectedResource);
        executeStep(executionStep);
    }
    
    protected final void performActionVerifyTiles(String actionTarget) {
        performActionVerifyTiles(getRequestPath(), actionTarget, getForwardPath());
    }

    protected final void performActionVerifyTiles(String requestPath, String actionTarget, String forwardPath) {
        StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), "display", forwardPath);
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

    protected final Collection<T> createResourceBatch(String prefix, int resourceCount) throws DuplicateResourceNameException {
        Collection<T> resources = new ArrayList<T>(resourceCount);
        for (int index = 0; index < resourceCount; index++) {
            T defaultResource = getDefaultResource();
            defaultResource.setResourceName(prefix + index);
            T createResource = createResource(defaultResource);
            resources.add(createResource);
        }
        return resources;
    }

    protected final void deleteResource(T resource) throws NoPermissionException {
        resourceService.removeResource(resource.getResourceId(), getSessionInfo());
    }

    protected final void deleteResourceBatch(Collection<T> resources) throws NoPermissionException {
        for (T resource : resources) {
            deleteResource(resource);
        }
    }

    protected final T getDefaultResource() {
        return getDefaultResource(selectedRealmId);
    }

    protected abstract T getDefaultResource(int selectedRealmId);

    /**
     * Override this method if, by default, there are entries in the database.
     * 
     * @return resource count, representing how many entries are expected by
     *         default.
     */
    protected int getInitialResourceCount() {
        return 0;
    }
}