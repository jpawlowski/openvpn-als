
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
			
package com.adito.testcontainer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpSession;

import servletunit.struts.MockStrutsTestCase;

import com.adito.boot.Context;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.properties.DefaultPropertyProfile;
import com.adito.properties.Property;
import com.adito.properties.PropertyProfile;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.services.PolicyServiceImpl;

/**
 * Starting point for any Struts based unit testing
 */
public abstract class AbstractMockStrutsTestCase extends MockStrutsTestCase {
    private static final String TEST_PROPERTY = "adito.testing";
    private static final String DEV_CONFIG_PROPERTY = "adito.useDevConfig";
    private static final String DEV_EXTENSIONS_PROPERTY = "adito.devExtensions";
    private static final String DATABASE_TYPE = "builtIn";
    private static String USERNAME = "testAdministrator";
    private static String PASSWORD = "newPassword";

    private static Context CONTEXT;
    private final String strutsConfigXml;
    private final String extensions;
    private static User superUser;
    private SessionInfo sessionInfo;
    private boolean finished;

    /**
     * Default constructor
     * 
     * @param strutsConfigXml - the Struts configuration file
     * @param extensions - the extensions to load
     */
    public AbstractMockStrutsTestCase(String strutsConfigXml, String extensions) {
        this.strutsConfigXml = strutsConfigXml;
        this.extensions = extensions;
    }

    protected final void setUp() throws Exception {
        super.setUp();
        setConfigFile("/WEB-INF/struts-config.xml, " + strutsConfigXml);
        getMockRequest().setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        getMockRequest().setServletPath("");
        getMockRequest().setContextPath("");
        
        oneTimeSetUp();
        sessionInfo = getSessionInfo(superUser);
        getMockRequest().getSession().setAttribute(Constants.SESSION_INFO, sessionInfo);
        authenticate();
        onSetUp();
    }

    protected void onSetUp() throws Exception {
    }
    
    private void oneTimeSetUp() throws Exception {
        if (CONTEXT == null) {
            deleteDatabase();
            System.setProperty(TEST_PROPERTY, "true");
            System.setProperty(DEV_CONFIG_PROPERTY, "true");
            System.setProperty(DEV_EXTENSIONS_PROPERTY, extensions);
            CONTEXT = TestContext.getTestContext();
            superUser = createSuperUser();

            // is there a super user and can login?
            UserDatabaseManager databaseManager = UserDatabaseManager.getInstance();
            UserDatabase userDatabase = databaseManager.getUserDatabase(superUser.getRealm().getRealmID());
            User account = userDatabase.getAccount(superUser.getPrincipalName());
            PolicyServiceImpl.getInstance().checkLogin(account);
        }
    }

    private User createSuperUser() throws Exception {
        UserDatabase userDatabase = getUserService().createUserDatabase(DATABASE_TYPE, UserDatabaseManager.DEFAULT_REALM_NAME, UserDatabaseManager.DEFAULT_REALM_DESCRIPTION, true);
        User user = userDatabase.createAccount(USERNAME, "", "", "", new Role[] {});
        userDatabase.changePassword(user.getPrincipalName(), "", PASSWORD, false);
        Property.setProperty(new RealmKey("security.userDatabase", userDatabase.getRealm()), DATABASE_TYPE, getSessionInfo());
        Property.setProperty(new RealmKey("security.administrators", userDatabase.getRealm()), USERNAME, getSessionInfo());
        return user;
    }
    
    /**
     * @return UserDatabaseManager
     * @throws Exception
     */
    public static UserDatabaseManager getUserService() throws Exception {
        return UserDatabaseManager.getInstance();
    }

    protected final void tearDown() throws Exception {
        super.tearDown();
        LogonControllerFactory.getInstance().logoff(sessionInfo.getLogonTicket());
        clearRequestParameters();
        onTearDown();
        
        if (finished) {
            oneTimeTearDown();
        }
    }
    
    protected void onTearDown() {
    }

    private static void oneTimeTearDown() {
        if (CONTEXT != null) {
            CONTEXT.shutdown(false);
        }
        deleteDatabase();
    }

    /**
     * This can be called from anywhere as long as the TestContext has been
     * initialised.
     */
    private static void deleteDatabase() {
        for (File file : TestContext.DB_DIR.listFiles()) {
            file.delete();
        }
    }

    protected final void authenticate() throws Exception {
        authenticate(sessionInfo);
    }

    protected final void authenticate(SessionInfo sessionInfo) throws Exception {
        LogonControllerFactory.getInstance().addSession("logonTicket", sessionInfo, request, response);
        HttpSession session = getRequest().getSession();
        session.setAttribute(Constants.LOGON_TICKET, "logonTicket");
        session.setAttribute(Constants.SELECTED_PROFILE, getPropertyProfile());
    }

    private static PropertyProfile getPropertyProfile() {
        Calendar instance = Calendar.getInstance();
        return new DefaultPropertyProfile(-1, -1, "", "", "", instance, instance);
    }

    /**
     * @return SessionInfo
     */
    protected final SessionInfo getSessionInfo() {
        return sessionInfo;
    }
    
    /**
     * @param account
     * @return SessionInfo
     */
    private SessionInfo getSessionInfo(User account) {
        return SessionInfo.nextSession(getSession(), "", account, getLocalHost(), SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "");
    }

    protected final void setNavigationContext(int context) {
        SessionInfo session = getSessionInfo();
        session.setNavigationContext(context);
        CoreUtil.resetMainNavigation(getSession());
    }
    
    private static InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return null;
        }
    }
    
    @Deprecated
    @Override
    public void verifyForward(String forwardName) {
        throw new UnsupportedOperationException("Don't use this method, instead use assertForward");
    }

    @Deprecated
    @Override
    public void verifyForwardPath(String forwardPath) {
        throw new UnsupportedOperationException("Don't use this method, instead use assertForward");
    }
    
    @Deprecated
    @Override
    public void verifyInputForward() {
        throw new UnsupportedOperationException("Don't use this method, instead use assertForward");
    }

    @Deprecated
    @Override
    public void verifyInputTilesForward(String definitionName) {
        throw new UnsupportedOperationException("Don't use this method, instead use assertForward");
    }

    protected final void executeStep(StrutsExecutionStep executionStep) {
        setRequestPathInfo(executionStep.getRequestPath());
        Map<String, String> requestParameters = executionStep.getRequestParameters();
        for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
            addRequestParameter(entry.getKey(), entry.getValue());
        }
        
        if (executionStep.isExpectSuccess()) {
            actionPerform();
        } else {
            try {
                actionPerform();
                fail("should have caused an exception");
            } catch (Throwable t) {
                // ignore, nothing to do
            }
        }
        
        String[] messages = executionStep.getMessages();
        if (messages.length == 0) {
            // for the time being we aren't going to check this,
            // we should but there is quite a bit of weirdness
            // when required field messages are added
            // verifyNoActionMessages();
        } else {
            verifyActionMessages(messages);
        }

        String[] errors = executionStep.getErrors();
        if (errors.length == 0) {
            verifyNoActionErrors();
        } else {
            verifyActionErrors(errors);
        }
        
        String tileName = executionStep.getTileName();
        if (Util.isNullOrTrimmedBlank(tileName)) {
            assertForward(executionStep.getForwardPath());
        } else {
            verifyTilesForward(tileName, executionStep.getForwardPath());
        }
    }
    
    /**
     * Performs the same check as verifyForward and verifyForwardPath, the
     * problem is these both try to do an exact match. As the actualForward may
     * contain parameters which we don't expect this can cause problems e.g.
     * /showMyAction.do?actionTarget=doSomething&msgId=3.
     * 
     * @param expectedPath
     */
    protected void assertForward(String expectedPath) {
        assertEquals("Forward should match", toStrippedUrl(expectedPath), toStrippedUrl(getActualForward()));
    }
    
    protected static final void assertActionTarget(String forwardPath, String actionTarget) {
        if (forwardPath == null) {
            fail("Forward path was null");
        }

        final String prefix = "actionTarget=";
        int lastIndexOf = forwardPath.lastIndexOf(prefix);
        if (lastIndexOf == -1) {
            fail("Action target " + actionTarget + " not found");
        }

        String actionTargetStipped = forwardPath.substring(lastIndexOf + prefix.length());
        if (actionTargetStipped.length() >= actionTarget.length()) {
            String realActionAction = actionTargetStipped.substring(0, actionTarget.length());
            assertEquals("Action target should match", actionTarget, realActionAction);
        } else {
            assertEquals("Action target should match", actionTarget, actionTargetStipped);
        }
    }

    /**
     * Remove everything from the URL after the action name
     * e.g. /showMyAction.do?actionTarget=doSomething becomes /showMyAction
     * @param toStrip
     * @return stripped URL
     */
    protected static final String toStrippedUrl(String toStrip) {
        if (toStrip == null) {
            return "";
        }

        int lastIndexOf = toStrip.lastIndexOf(".");
        if (lastIndexOf == -1 || !toStrip.contains(".do")) {
            return toStrip;
        }
        return toStrip.substring(0, lastIndexOf);
    }

    /**
     * This test is required as a hack to shutdown the context.
     */
    public final void testZ() {
        finished = true;
    }
}