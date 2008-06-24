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
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.UserDatabaseManager;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Principal;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceUtil;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.impl.policyattributes.PolicyAttributeKey;
import com.adito.properties.impl.policyattributes.PolicyAttributes;
import com.adito.security.AuthenticationScheme;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.tabs.TabModel;

public class PolicyForm extends AbstractResourceForm<Policy> implements TabModel {
    final static Log log = LogFactory.getLog(PoliciesForm.class);
    private PropertyList selectedAccounts;
    private PropertyList selectedRoles;
    private List<AttributeValueItem> attributeValueItems;
    private String selectedTab = "details";
    private List categoryIds;
    private List categoryTitles;
    private PropertyClass propertyClass;

    public PolicyForm() {
        super();
        selectedAccounts = new PropertyList();
        selectedRoles = new PropertyList();
        propertyClass = PropertyClassManager.getInstance().getPropertyClass(PolicyAttributes.NAME);
    }

    public String getSelectedAccounts() {
        return selectedAccounts.getAsTextFieldText();
    }

    public void setSelectedAccounts(String selectedAccounts) {
        this.selectedAccounts.setAsTextFieldText(selectedAccounts);
    }

    public String getSelectedRoles() {
        return selectedRoles.getAsTextFieldText();
    }

    public void setSelectedRoles(String selectedRoles) {
        this.selectedRoles.setAsTextFieldText(selectedRoles);
    }

    public int getTabCount() {
        return 2 + (categoryIds.size());
    }

    public String getTabTitle(int idx) {
        switch (idx) {
            case 0:
            case 1:
                return null;
            default:
                return (String) categoryTitles.get(idx - 2);
        }
    }

    public String getTabName(int idx) {
        switch (idx) {
            case 0:
                return "details";
            case 1:
                return "principals";
            default:
                return (String) categoryIds.get(idx - 2);
        }
    }

    /**
     * @param selectedAccounts The selectedAccounts to set.
     */
    public void setSelectedAccounts(PropertyList selectedAccounts) {
        this.selectedAccounts = selectedAccounts;
    }

    /**
     * @param selectedRoles The selectedRoles to set.
     */
    public void setSelectedRoles(PropertyList selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public PropertyList getSelectedAccountsList() {
        return selectedAccounts;
    }

    public PropertyList getSelectedRolesList() {
        return selectedRoles;
    }

    public Resource getResourceByName(String name, SessionInfo session) throws Exception {
        return PolicyDatabaseFactory.getInstance().getPolicyByName(name, session.getUser().getRealm().getResourceId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      com.adito.policyframework.Resource, boolean,
     *      com.adito.input.MultiSelectSelectionModel,
     *      com.adito.boot.PropertyList, com.adito.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Policy resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
        Policy pol = (Policy) resource;
        selectedAccounts = new PropertyList();
        selectedRoles = new PropertyList();
        List principals = PolicyDatabaseFactory.getInstance().getPrincipalsGrantedPolicy((Policy) resource, user.getRealm());
        for (Iterator i = principals.iterator(); i.hasNext();) {
            Principal p = (Principal) i.next();
            if (p instanceof Role) {
                selectedRoles.add(p.getPrincipalName());
            } else {
                selectedAccounts.add(p.getPrincipalName());
            }
        }
    }

    public void initAttributes(HttpServletRequest request) {

        /*
         * Get all of the policy attribute definitions and wrap them in item
         * objects
         */

        attributeValueItems = new ArrayList();
        for (PropertyDefinition d : propertyClass.getDefinitions()) {
            AttributeDefinition def = (AttributeDefinition) d;
            if (!def.isHidden()) {
                if (def.getVisibility() != AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                    String value = def.getDefaultValue();
                    if (user != null) {
                        value = Property.getProperty(new PolicyAttributeKey(getResourceId(), def.getName()));
                    }
                    AttributeValueItem item = new AttributeValueItem(def, request, value);
                    attributeValueItems.add(item);
                }
            }
        }

        /*
         * Sort the list of items and build up the list of categories
         */

        Collections.sort(attributeValueItems);
        categoryIds = new ArrayList();
        categoryTitles = new ArrayList();
        for (Iterator i = attributeValueItems.iterator(); i.hasNext();) {
            AttributeValueItem item = (AttributeValueItem) i.next();
            int idx = categoryIds.indexOf(item.getCategoryId());
            if (idx == -1) {
                categoryIds.add(item.getCategoryId());
                categoryTitles.add(item.getCategoryLabel());
            }
        }
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;

    }

    public void applyToResource() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
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
     * Get the list of policy attribute value items
     * 
     * @return user attribute value items
     */
    public List<AttributeValueItem> getAttributeValueItems() {
        return attributeValueItems;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            try {
                for (AttributeValueItem item : attributeValueItems) {
                    PropertyDefinition def = item.getDefinition();
                    try {
                        def.validate(item.getValue().toString(), getClass().getClassLoader());
                    } catch (CoreException ce) {
                        ce.getBundleActionMessage().setArg3(item.getLabel());
                        errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                    }
                }

                SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
                UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(session.getRealm());
                for (String account : selectedAccounts) {
                    try {
                        udb.getAccount(account);
                    } catch (Exception e) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.error.invalidUser", account));
                    }
                }
                for (String role : selectedRoles) {
                    try {
                        if (udb.getRole(role) == null) {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.error.invalidRole", role));
                    }
                }
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.failedToValidate", e.getMessage()));
            }

            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
            boolean found = false;
            try {
                List wasAttached = PolicyDatabaseFactory.getInstance().getPrincipalsGrantedPolicy((Policy) this.getResource(),
                    info.getUser().getRealm()); // objects
                List nowAttached = this.getSelectedAccountsList();
                // only do this if the super user has been removed.
                if (wasAttached.contains(getUser()) && !nowAttached.contains(getUser().getPrincipalName())) {
                    List authSchemes = ResourceUtil.getGrantedResource(info, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
                    for (Iterator iter = authSchemes.iterator(); iter.hasNext();) {
                        AuthenticationScheme element = (DefaultAuthenticationScheme) iter.next();
                        if (!element.isSystemScheme() && element.getEnabled()) {
                            List attachedPolicies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(element,
                                info.getUser().getRealm());
                            for (Iterator iterator = attachedPolicies.iterator(); iterator.hasNext();) {
                                Policy policy = (Policy) iterator.next();
                                if (!this.getResource().equals(policy)
                                                && PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy, getUser())) {
                                    found = true;
                                }
                            }
                        }
                    }
                } else {
                    found = true;
                }
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage(
                                "authenticationSchemes.error.failedToValidateSuperUserAuthSchemeConnection"));
            }
            if (!found) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage("security",
                                "authenticationSchemes.error.mustHavePolicySuperUserAssociation"));
            }
        }
        return errs;
    }
}
