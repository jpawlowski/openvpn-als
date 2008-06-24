
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
			
package com.adito.policyframework.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.core.forms.CoreForm;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.policyframework.Resource;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.attributes.DefaultAttributeDefinition;
import com.adito.properties.impl.resource.ResourceAttributes;
import com.adito.properties.impl.resource.ResourceKey;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.tabs.TabModel;

/**
 * Abstract implementation of {@link com.adito.core.forms.CoreForm} that
 * allows editing of {@link com.adito.policyframework.Resource} instances.
 * 
 * @param <T> of type Resource
 */
public abstract class AbstractResourceForm<T extends Resource> extends CoreForm implements TabModel {
    static Log log = LogFactory.getLog(AbstractResourceForm.class);

    protected String resourceName;
    protected String resourceDescription;
    protected int resourceId;
    protected MultiSelectSelectionModel policyModel;
    protected PropertyList selectedPolicies;
    protected User owner, user;
    protected String originalName;
    protected T resource;
    protected boolean readOnly;
    protected boolean assignOnly;
    private int navigationContext;
    private PropertyClass propertyClass;
    private List<AttributeValueItem> userAttributeValueItems;
    private List<String> categoryIds;
    private List<String> categoryTitles;
    private boolean showPersonalPolicies;

    /**
     * Constructor
     */
    public AbstractResourceForm() {
        selectedPolicies = new PropertyList();
        propertyClass = PropertyClassManager.getInstance().getPropertyClass(ResourceAttributes.NAME);
    }
    
    /**
     * Get a resource given its name. Used by the {@link #validate(ActionMapping, HttpServletRequest)}
     * method to make sure resources with the same name are not created.
     * 
     * @param resourceName resource name
     * @param session Session Info
     * @return resource
     * @throws Exception on any error
     */
    public abstract Resource getResourceByName(String resourceName, SessionInfo session) throws Exception;
    
    /**
     * Get the Id of the resource being edited or 0 if this is a new resource.
     * 
     * @return resource Id
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Set the ID of the resource being edited or 0 if this is a new resource.
     * 
     * @param resourceId resource Id
     */
    public void setResourceID(int resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Initialise the form. All concrete classes should call this.
     * 
     * @param request 
     * @param resource resource
     * @param editing editing
     * @param policyModel policy model
     * @param selectedPolicies selected policies
     * @param owner owner
     * @param assignOnly Assign Only
     * @throws Exception on any error
     */
    public void initialise(HttpServletRequest request, T resource, boolean editing, MultiSelectSelectionModel policyModel,
                    PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        this.resource = resource;
        originalName = resource.getResourceDisplayName();
        setResourceID(resource.getResourceId());
        setResourceName(resource.getResourceDisplayName());
        setResourceDescription(resource.getResourceDescription());
        setResource(resource);
        setNavigationContext(LogonControllerFactory.getInstance().getSessionInfo(request).getNavigationContext());
        if(editing) {
            setEditing();
        }
        else {
            setCreating();
        }
        setPolicyModel(policyModel);
        this.selectedPolicies = selectedPolicies;
        this.owner = owner;
        this.user = LogonControllerFactory.getInstance().getUser(request);
        this.assignOnly = assignOnly;
        userAttributeValueItems = new ArrayList<AttributeValueItem>();
        for (PropertyDefinition d : propertyClass.getDefinitions()) {
            AttributeDefinition def = new DefaultAttributeDefinition(d.getType(), d.getName(), d.getTypeMeta(), d.getCategory(), "", d
                            .getDefaultValue(), AttributeDefinition.UNKNOWN, 10, d.getMessageResourcesKey(), false, d.getLabel(), d.getDescription(), false, false, "");
            def.init(propertyClass);
           if (!def.isHidden() && isResourcePropertyDefinition(resource, d)) {
                    String value = def.getDefaultValue();
                    if (resource != null) {
                        value = Property.getProperty(new ResourceKey(def.getName(), resource.getResourceType(), resource.getResourceId()));
                    }
                    AttributeValueItem item = new AttributeValueItem(def, request, value, getSubCategoryString(resource));
                    userAttributeValueItems.add(item);
            }
        }

        /*
         * Sort the list of items and build up the list of categories
         */

        Collections.sort(userAttributeValueItems);
        categoryIds = new ArrayList<String>();
        categoryTitles = new ArrayList<String>();
        for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
            AttributeValueItem item = (AttributeValueItem) i.next();
            int idx = categoryIds.indexOf(item.getCategoryId());
            if (idx == -1) {
                categoryIds.add(item.getCategoryId());
                categoryTitles.add(item.getCategoryLabel());
            }
        }
    }

    /**
     * Method which is used to filter the resource attributes available on a
     * resource.
     * 
     * @param resource
     * @param d
     * @return boolean
     */
    public boolean isResourcePropertyDefinition(T resource, PropertyDefinition d) {
        return d.getCategory() == resource.getResourceType().getResourceTypeId();
    }
    
    /**
     * Method which returns the subCategory if there is one.
     * 
     * @param resource
     * @return String
     */
    public String getSubCategoryString(T resource) {
        return null;
    }
    
    /**
     * If this is an {@link com.adito.policyframework.OwnedResource}
     * then this method will return the owner.
     * 
     * @return owner
     */
    public User getOwner() {
        return owner;
    }
    
    /**
     * Get the user that is creating / editing this resource.
     * 
     * @return user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Get the resource being editing
     * 
     * @return resource
     */
    public T getResource() {
        return resource;
    }
    
    /**	
     * Set the resource being edited
     * 
     * @param resource
     */
    public void setResource(T resource) {
        this.resource = resource;
        originalName = resource.getResourceDisplayName();
        setResourceID(resource.getResourceId());
        setResourceName(resource.getResourceDisplayName());
        setResourceDescription(resource.getResourceDescription());
    }

    /**
     * Get the resource description
     * 
     * @return resource description
     */
    public String getResourceDescription() {
        return resourceDescription;
    }

    /**
     * Set the resource description.
     * 
     * @param resourceDescription resource description
     */
    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription.trim();
    }

    /**
     * Get the resource name
     * 
     * @return resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (isCommiting()) {
            if (getResourceName() == null || getResourceName().equals("")) {
                errors.add(Globals.ERROR_KEY, new BundleActionMessage("policyframework", "error.createResource.missingName"));
            }
            if (getResourceName().length() > Resource.MAX_RESOURCE_NAME_LENGTH) {
                errors.add(Globals.ERROR_KEY, new BundleActionMessage("policyframework", "error.createResource.resourceNameTooLong", String.valueOf(Resource.MAX_RESOURCE_NAME_LENGTH)));
            }
            if (getResourceDescription().equals("")) {
                errors.add(Globals.ERROR_KEY, new BundleActionMessage("policyframework", "error.createResource.missingDescription", String.valueOf(Resource.MAX_RESOURCE_NAME_LENGTH)));
            }
            if(!getEditing() || !originalName.equals(getResourceName())) {
                validateResourceNameUnique(request, errors);
            }
            /* Make sure the selected policies are all currently available, this prevents
             * anyone fiddling the polices they are allowed to configure
             */
            for (String policyId : selectedPolicies) {
                if(!policyModel.contains(policyId)) {
                    throw new Error("User doesn't have permission to select the policy '" + policyId + "', this shouldn't happen.");
                }
            }
        }
        return errors;
    }

    private void validateResourceNameUnique(HttpServletRequest request, ActionErrors errors) {
        try {
            Resource resource = getResourceByName(getResourceName(), LogonControllerFactory.getInstance().getSessionInfo(request));
            if (resource != null) {
                errors.add(Globals.ERROR_KEY, new BundleActionMessage("policyframework", "error.createResource.resourceNameInUse",
                                getResourceName()));
            }
        } catch (Exception e) {
            errors.add(Globals.ERROR_KEY, new BundleActionMessage("policyframework",
                            "error.createResource.failedToDetermineIfResourceExists", e.getMessage()));
        }
    }

    /**
     * Set the resource name
     * 
     * @param resourceName resource name
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName.trim();
    }

    /**
     * Get the selected policies as a <i>Property List</i> string.
     * 
     * @return the selected policies
     * @see PropertyList
     */
    public String getSelectedPolicies() {
        return selectedPolicies.getAsTextFieldText();
    }

    /**
     * Set the selected policies as a <i>Property List</i> string.
     * 
     * @param selectedPolicies the selected policies
     * @see PropertyList
     */
    public void setSelectedPolicies(String selectedPolicies) {
        this.selectedPolicies.setAsTextFieldText(selectedPolicies);
    }

    /**
     * Set the selected policies as a <i>Property List</i>.
     * 
     * @param selectedPolicies the selected policies
     * @see PropertyList
     */
    public void setSelectedPolicies(PropertyList selectedPolicies) {
        this.selectedPolicies = selectedPolicies;
    }
    
    /**
     * Get the model to use for policy selection
     * 
     * @return policy selection model
     */
    public MultiSelectSelectionModel getPolicyModel() {
        return policyModel;
    }

    /**
     * Set the model to use for policy selection
     * 
     * @param policyModel policy selection model
     */
    public void setPolicyModel(MultiSelectSelectionModel policyModel) {
        this.policyModel = policyModel;
    }
    
    /**
     * Get the list of selected policies
     * 
     * @return selected policies
     */
    public PropertyList getSelectedPoliciesList() {
        return selectedPolicies;
    }
    
    /**
     * Get if this resource is read only
     * 
     * @return resource read only
     */
    public boolean getReadOnly() {
        return readOnly;
    }
    
    /**
     * Set this resource to be read only. This does not accept a boolean
     * argument to prevent struts forms being able to modify it.
     */
    public void setReadOnly() {
        readOnly = true;        
    }
    
    /**
     * Set this resource to be writeable only. This does not accept a boolean
     * argument to prevent struts forms being able to modify it.
     */
    public void setWriteable() {
        readOnly = false;        
    }

    /**
     * Apply the collected form fields to the resource object
     * 
     * @throws Exception on any error
     */
    public void apply()  throws Exception {
        if(getReadOnly()) {
            throw new Exception("Read only");
        }
        resource.setResourceName(getResourceName());
        resource.setResourceDescription(getResourceDescription());
        applyToResource();
    }
    
    /**
     * Concrete implementations must provide this method to persist any
     * additional form fields to the resource object being edited.
     * 
     * @throws Exception on any error
     */
    public abstract void applyToResource() throws Exception;

    /**
     * @return boolean
     */
    public boolean isAssignOnly() {
        return assignOnly;
    }

    /**
     * @param assignOnly
     */
    public void setAssignOnly(boolean assignOnly) {
        this.assignOnly = assignOnly;
    }
    
    /**
     * Get a list of the category Titles
     * 
     * @return category titles
     */
    public List getCategoryTitles() {
        return categoryTitles;
    }

    /**
     * Get a list of the category ids
     * 
     * @return category ids
     */
    public List getCategoryIds() {
        return categoryIds;
    }
    
    /**
     * Get the list of user attribute value items
     * 
     * @return user attribute value items
     */
    public List getAttributeValueItems() {
        return userAttributeValueItems;
    }

    /* (non-Javadoc)
     * @see com.adito.core.forms.CoreForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        super.reset(mapping, request);
        recordSelectedPoliciesStatus(request);
        if (userAttributeValueItems != null) {
            for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
                AttributeValueItem item = (AttributeValueItem) i.next();
                if (item.getDefinition().getType() == PropertyDefinition.TYPE_BOOLEAN) {
                    item.setSelected(false);
                }
            }
        }
    }

    /**
     * @param request
     */
    public void recordSelectedPoliciesStatus(javax.servlet.http.HttpServletRequest request) {
        String requestSelectedPolicies = request.getParameter("selectedPolicies");
        if (!Util.isNullOrTrimmedBlank(requestSelectedPolicies)) {
            selectedPolicies.setAsTextFieldText(requestSelectedPolicies);
        } else if (null != selectedPolicies) {
            selectedPolicies.clear();
        }
        if (null != policyModel) {
            policyModel.getSelectedValues().clear();
            List<LabelValueBean> availableValues = policyModel.getAvailableValues();
            for (LabelValueBean labelValueBean : availableValues) {
                if (selectedPolicies.contains(labelValueBean.getValue())){
                    policyModel.getSelectedValues().add(labelValueBean);
                } else {
                    policyModel.getSelectedValues().remove(labelValueBean);
                }
            }
            policyModel.rebuild(LogonControllerFactory.getInstance().getSessionInfo(request));//getAvailableValues().remove(labelValueBean);
        }
    }
    
    /**
     * Get the navigation Context
     * 
     * @return int navigationContext
     */
    public int getNavigationContext() {
        return navigationContext;
    }

    /**
     * set the navigation context
     * @param navigationContext int
     */
    public void setNavigationContext(int navigationContext) {
        this.navigationContext = navigationContext;
    }

    /**
     * @return showPersonalPolicies
     */
    public boolean isShowPersonalPolicies() {
        return showPersonalPolicies;
    }

    /**
     * @param showPersonalPolicies
     */
    public void setShowPersonalPolicies(boolean showPersonalPolicies) {
        this.showPersonalPolicies = showPersonalPolicies;
    }
}