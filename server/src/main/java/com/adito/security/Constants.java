
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
			
package com.adito.security;

import com.adito.extensions.ExtensionDescriptor;
import com.adito.navigation.actions.RedirectAction;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceStack;
import com.adito.vfs.UploadManager;

/**
 * Strings contants used throughout Adito.
 *
 * @author Lee David Painter
 *
 * Application constants.
 */
public class Constants {

    /* Session attribute keys * */

    /**
     * Key store store the sessions {@link UploadManager}.
     */
    public static final String UPLOAD_MANAGER = "uploadManager";

    /**
     * Key for session attribute that stores the currently pending VPN ticket.
     * As soon as a VPN client uses the ticket for authorization a new ticket
     * will be generated and placed in the session attributes ready for the
     * next use.
     */
    public final static String VPN_AUTHORIZATION_TICKET = "pendingVPNTicket";

    /**
     * Key used to store the one use ticket for launching a web from the
     * network places page.
     */
    public static final String WEB_FOLDER_LAUNCH_TICKET = "webFolderLaunchTicket";

    /**
     * Key used to store the referer which may be passed around actions
     * under some circumstances.
     */
    public static final String REFERER = "referer";

    /**
     * Comment for <code>VPN_AUTOSTART</code>
     */
    public final static String VPN_AUTOSTART = "autoStartVPN";

    /**
     * Comment for <code>LOGON_INFO</code>
     */
    public static final String LOGON_INFO = "logonInfo";

    /**
     * Comment for <code>LOGON_TICKET</code>
     */
    public final static String LOGON_TICKET = "logonTicket";

    public final static String DOMAIN_LOGON_TICKET = "domainLogonTicket";

    public final static String REQ_ATTR_AGENT_AUTHENTICATION_TICKET = "agentAuthenticationTicket";

    public final static String REQ_ATTR_AGENT_EXTENSION = "agentExtension";

    /**
     * Comment for <code>SELECTED_MENU</code>
     */
    public static final String SELECTED_MENU = "selectedMenu";

    /**
     * Comment for <code>AUTH_SENT</code>
     */
    public final static String AUTH_SENT = "authSent";

    /**
     * Comment for <code>USER</code>
     */
    public final static String USER = "user";

    /**
     * Comment for <code>SESSION_LOCKED</code>
     */
    public final static String SESSION_LOCKED = "sessionLocked";

    /**
     * Comment for <code>GLOBAL_WARNINGS</code>
     */
    public final static String SESSION_GLOBAL_WARNINGS = "globalWarnings";

    /**
     * Comment for <code>GLOBAL_WARNINGS</code>
     */
    public final static String CONTEXT_GLOBAL_WARNINGS = "sessionGlobalWarnings";

    /**
     * Comment for <code>GLOBAL_WARNINGS_KEY</code>
     */
    public final static String GLOBAL_WARNINGS_KEY = "com.adito.GLOBAL_WARNINGS";

    /**
     * Comment for <code>BUNDLE_MESSAGES_KEY</code>
     */
    public final static String BUNDLE_MESSAGES_KEY = "com.adito.BUNDLE_MESSAGES";

    /**
     * Comment for <code>BUNDLE_ERRORS_KEY</code>
     */
    public final static String BUNDLE_ERRORS_KEY = "com.adito.BUNDLE_ERRORS";

    /**
     * Comment for <code>LOGOFF_HOOK</code>
     */
    public static final String LOGOFF_HOOK = "logoffHook";

    /**
     * Comment for <code>PROFILES</code>
     */
    public static final String PROFILES = "profiles";

    /**
     * Comment for <code>SELECTED_PROFILE</code>
     */
    public static final String SELECTED_PROFILE = "selectedProfile";

    /**
     * Comment for <code>ATTR_GET_HISTORY</code>
     */
    public static final String ATTR_GET_HISTORY = "getHistory";

    /**
     * Comment for <code>EXCEPTION</code>
     */
    public static final String EXCEPTION = "exception";

    /**
     * Comment for <code>ORIGINAL_REQUEST</code>
     */
    public static final String ORIGINAL_REQUEST = "originalRequest";

    /**
     * Comment for <code>SESSION_TIMEOUT_BLOCKS</code>
     */
    public static final String SESSION_TIMEOUT_BLOCKS = "sessionTimeoutBlocks";

    /**
     * Comment for <code>SCOPE_PERSONAL</code>
     */
    public static final String SCOPE_PERSONAL = "personal";

    /**
     * Comment for <code>CONTACTING_APPLICATION_STORE</code>
     */
    public static final String CONTACTING_APPLICATION_STORE = "contactingApplicationStore";

    /**
     * Comment for <code>SCOPE_GLOBAL</code>
     */
    public static final String SCOPE_GLOBAL = "global";

    /**
     * Comment for <code>FORCE_WEBDAV_METHOD</code>
     */
    public static final String FORCE_WEBDAV_METHOD = "forceWebdavMethod"; // TODO
                                                                          // horrid
                                                                          // hack

    /**
     * Comment for <code>SCOPE_SETUP</code>
     */
    public static final String SCOPE_SETUP = "setup";

    /**
     * Comment for <code>AGENT_SESSION_TIMEOUT_BLOCK_ID</code>
     */
    public static final String AGENT_SESSION_TIMEOUT_BLOCK_ID = "vpnClientSessionTimeoutBlockId";

    /**
     * Comment for <code>ATTR_CACHE</code>
     */
    public static final String ATTR_CACHE = "adito.cache";

    /**
     * Comment for <code>ATTR_COOKIE_MAP</code>
     */
    public static final String ATTR_COOKIE_MAP = "adito.cookieMap";

    /**
     * Comment for <code>AUTH_SESSION</code>
     */
    public static final String AUTH_SESSION = "authSession";

    /**
     * Comment for <code>SESSION_INFO</code>
     */
    public static final String SESSION_INFO = "sessionInfo";

    /**
     * Comment for <code>RESTARTING</code>
     */
    public static final String RESTARTING = "restarting";

    /**
     * Key used to store the current navigation menu tree  menu items.
     */
    public static final String MENU_TREE = "menuTree";

    /**
     * Key used to store any page task menu items available for this page
     */
    public static final String PAGE_TASKS = "pageTasks";

    /**
     * Key used to store any tool bar menu items available for this page
     */
    public static final String TOOL_BAR_ITEMS = "toolBarItems";
    
    /**
     * Key user to store the navigation bar menu items
     */
    public static final String NAV_BAR = "navBar";

    /**
     * Comment for <code>HOST</code>
     */
    public static final String HOST = "host";

    /**
     * Comment for <code>PASSWORD_CHANGE_REASON_MESSAGE</code>
     */
    public static final String PASSWORD_CHANGE_REASON_MESSAGE = "passwordChangeReasonMessage";

    /**
     * Comment for <code>PAGE_INTERCEPT_LISTENERS</code>
     */
    public static final String PAGE_INTERCEPT_LISTENERS = "pageInterceptListeners";

    /**
     * Comment for <code>PAGE_INTERCEPTED</code>
     */
    public static final String PAGE_INTERCEPTED = "pageIntercepted";

    /**
     * Comment for <code>SESSION_HOOK</code>
     */
    public static final String SESSION_HOOK = "sessionHook";

    /**
     * Comment for <code>LICENSE_AGREEMENTS</code>
     */
    public static final String LICENSE_AGREEMENTS = "licenseAgreement";

    /**
     * Comment for <code>PERSONAL_QUESTION</code>
     */
    public static final String PERSONAL_QUESTION = "personalQuestion";

    /**
     * Key used to access {@link com.adito.vfs.clipboard.Clipboard} 
     * object used to move and copy files in the VFS
     */
    public static final String CLIPBOARD = "clipboard";

    /**
     * A user session may have a single wizard sequence active at any
     * time. 
     */
    public static final String WIZARD_SEQUENCE = "wizardSequence";

    /**
     * A wizard may be suspended and returned to later. This key is 
     * used to store the sequence object temporarily. 
     */
    public static final String SUSPENDED_WIZARD_SEQUENCE = "suspendedWizardSequence";

    /**
     * Each session may have a number of HTTP clients active (
     * currently only used for reverse proxy).
     */
    public static final String HTTP_CLIENTS = "httpClients";

    /**
     * Holds any resources that may have been launched in the session. This
     * is currently used by the reverse proxy handler to avoide having to lookup in the
     * database for every request.
     */
    public static final String LAUNCH_SESSIONS = "launchSessions";

    /**
     * Attribute set on the session to indicate that the home page can be redirected
     */
    public static final String REDIRECT_HOME = "redirectHomePage";

    /**
     * Contacks the current stack of editing resources. See {@link ResourceStack}.
     */
    public static final String EDITING_RESOURCE_STACK = "editingResourceStack";

    /**
     * Used to pass arbitrary entities between a list type action to an
     * edit type action
     */
    public static final String EDITING_ITEM = "editingItem";
    
    /*
     * Request attributes (generally passed from one action to another via a
     * forward
     */
    
    /**
     * Stores a {@link LaunchSession} between requests
     */
    public static final String REQ_ATTR_LAUNCH_SESSION = "launchSession";

    /**
     * Stores the referering page when launching the VPN client. The browse will
     * be directed back to this page when the client has launched.
     */
    public static final String REQ_ATTR_LAUNCH_AGENT_REFERER = "launchAgentReferer";

    /**
     * Stores the message resources key to display when the personal answers must
     * be changed.
     */
    public static final String REQ_ATTR_PERSONAL_ANSWERS_CHANGE_REASON_MESSAGE = "personalAnswersChangeReason";

    /**
     * {@link com.adito.navigation.actions.HelpAction} stores this 
     * attribute in the request. It contains the {@link com.adito.boot.PropertyDefinition}
     * that help has been requsted for.
     */
    public static final String REQ_ATTR_PROPERTY_DEFINITION = "propertyDefinition";

    /**
     * Contains the current {@link com.adito.vfs.UploadDetails}  to be
     * passed on to {@link com.adito.vfs.actions.ShowUploadAction}.
     */
    public static final String REQ_ATTR_UPLOAD_DETAILS = "uploadDetails";

    /**
     * This request attribute should be set to {@link Boolean#FALSE} if 
     * you want to prevent the stream being gzipped by the compress filter.
     */
    public static final String REQ_ATTR_COMPRESS = "compress";
    
    /**
     * Pages may specify that the <i>Header</i> component of the layout be
     * hidden when the page is displayed by setting the request attribute.
     */
    public static final String REQ_ATTR_HIDE_HEADER = "layout.hideHeader";
    
    /**
     * Pages may specify that the <i>Side Bar</i> component of the layout be
     * hidden when the page is displayed by setting the request attribute.
     */
    public static final String REQ_ATTR_HIDE_SIDE_BAR = "layout.hideSideBar";
    
    /**
     * Key to store action messages for warnings
     */
    public static final String REQ_ATTR_WARNINGS = "warnings";

    /**
     * Full url used for {@link RedirectAction}.
     */
    public static final String REQ_ATTR_FORWARD_TO = "url";
    
    /**
     * Target used for {@link RedirectAction}.
     */
    public static final String REQ_ATTR_TARGET = "target";
    
    /**
     * Fragment of javascript to execute on load
     */
    public static final String REQ_ATTR_EXEC_ON_LOAD = "execOnLoad";
    
    /**
     * Folder used for {@link RedirectAction} (Web folders).
     */
    public static final String REQ_ATTR_FOLDER = "folder";

    /**
     * Stores the current action mapping object 
     */
    public static final String REQ_ATTR_ACTION_MAPPING = "actionMapping";

    /**
     * Stores the current actions form
     */
    public static final String REQ_ATTR_FORM = "form";

    /**
     * A {@link Resource} object should be stored under this attribute 
     * in the request prior to a redirect to <i>resourceInformation</i>.
     */
	public static final String REQ_ATTR_INFO_RESOURCE = "infoResource";

    /**
     * This request attribute will contain {@link Boolean#TRUE} if the
     * current page is in a popup window.
     */
	public static final String REQ_ATTR_POPUP = "popup";
	
	/*
	 * Launch session attributes
	 */
	
	/**
	 * Stores {@link ExtensionDescriptor} to launch 
	 */
	public static final String LAUNCH_ATTR_AGENT_EXTENSION = "launchAgentExtensions";
	
	/**
	 * Stores URL to return to after launch 
	 */
	public static final String LAUNCH_ATTR_AGENT_RETURN_TO = "launchAgentReturnTo";

	/**
	 * Agent launch ticket
	 */
	public static final String LAUNCH_ATTR_AGENT_TICKET = "launchAgentTicket";

}