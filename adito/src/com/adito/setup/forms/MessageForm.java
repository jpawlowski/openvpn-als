package com.adito.setup.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyList;
import com.adito.core.UserDatabaseManager;
import com.adito.core.forms.CoreForm;
import com.adito.input.MultiSelectDataSource;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.policyframework.PolicyDataSource;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserNotFoundException;
import com.adito.tabs.TabModel;

public class MessageForm extends CoreForm implements TabModel {
	final static Log log = LogFactory.getLog(MessageForm.class);
	private PropertyList selectedAccounts;
	private PropertyList selectedRoles;
	private PropertyList selectedPolicies;
	private MultiSelectDataSource selectedPolicyDataSource;
	private MultiSelectSelectionModel selectedPolicySelection;
	private String subject;
	private String content;
	private boolean urgent;
	private User sender;
	private String selectedTab = "message";
	private String selectedSink;
    private boolean showPersonalPolicies;

	public MessageForm() {
		super();
		selectedAccounts = new PropertyList();
		selectedRoles = new PropertyList();
		selectedPolicies = new PropertyList();
		selectedPolicyDataSource = new PolicyDataSource();
	}

	public String getSelectedSink() {
		return selectedSink;
	}

	public void setSelectedSink(String selectedSink) {
		this.selectedSink = selectedSink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject == null ? subject : subject.trim();
	}

	public boolean getUrgent() {
		return urgent;
	}

	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}

	public String getSelectedPolicies() {
		return selectedPolicies.getAsTextFieldText();
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

	public void setSelectedPolicies(String selectedPolicies) {
		this.selectedPolicies.setAsTextFieldText(selectedPolicies);
	}
    
    public void setSelectedPolicies(PropertyList selectedPolicies) {
        this.selectedPolicies = selectedPolicies;
    }

	public MultiSelectSelectionModel getSelectedPolicySelectionModel() {
		return selectedPolicySelection;
	}

	public void setSelectedAccounts(PropertyList selectedAccounts) {
		this.selectedAccounts = selectedAccounts;
	}

	public void setSelectedRoles(PropertyList selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	public PropertyList getSelectedPoliciesList() {
		return selectedPolicies;
	}

	public PropertyList getSelectedAccountsList() {
		return selectedAccounts;
	}

	public PropertyList getSelectedRolesList() {
		return selectedRoles;
	}

	public User getSender() {
		return sender;
	}

	public void initialise(MultiSelectSelectionModel selectedPolicySelection, PropertyList selectedPolicies, SessionInfo session)
					throws Exception {
		this.selectedPolicySelection = selectedPolicySelection;
		this.selectedPolicies = selectedPolicies;
		selectedAccounts = new PropertyList();
		selectedRoles = new PropertyList();
		if (selectedPolicySelection == null)
			selectedPolicySelection = new MultiSelectSelectionModel(session, selectedPolicyDataSource, selectedPolicies);
		selectedPolicySelection.rebuild(session);
		this.sender = session.getUser();
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		urgent = false;
	}

	public int getTabCount() {
		return 2;
	}

	public String getTabName(int idx) {
		switch (idx) {
			case 0:
				return "message";
			default:
				return "recipients";
		}
	}

	public String getTabTitle(int idx) {

		// Get from resources
		return null;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errs = new ActionErrors();
		if (isCommiting()) {
			if (isEmpty(getSubject())) {
				errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.noSubject"));
			}
			if (getSelectedAccountsList().size() == 0 && getSelectedRolesList().size() == 0
				&& getSelectedPoliciesList().size() == 0) {
				errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.noRecipients"));
			}

			// Validate selected users
			try {
				for (String account : getSelectedAccountsList()) {
					try {
						UserDatabaseManager.getInstance().getDefaultUserDatabase().getAccount(account);
					} catch (UserNotFoundException unfe) {
						errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.invalidAccount", account));
					}
				}

				// Validate selected groups
				for (String role : getSelectedRolesList()) {
					try {
						UserDatabaseManager.getInstance().getDefaultUserDatabase().getRole(role);
					} catch (Exception e) {
						errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.invalidRole", role));
					}
				}

				// Validate selected policies
				for (String policy : getSelectedPoliciesList()) {
                    if (PolicyDatabaseFactory.getInstance().getPolicy(Integer.parseInt(policy)).getResourceName() == null) {
						errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.invalidPolicy", policy));
					}
				}
			} catch (Exception e) {
				errs.add(Globals.ERROR_KEY, new ActionMessage("sendMessage.error.failedToValidate", e.getMessage()));
				log.error("Failed to validate.", e);
			}
		}
		return errs;

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
     * @return selectedPolicySelection
     */
    public MultiSelectSelectionModel getSelectedPolicySelection() {
        return selectedPolicySelection;
    }

    /**
     * @param selectedPolicySelection
     */
    public void setSelectedPolicySelection(MultiSelectSelectionModel selectedPolicySelection) {
        this.selectedPolicySelection = selectedPolicySelection;
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
