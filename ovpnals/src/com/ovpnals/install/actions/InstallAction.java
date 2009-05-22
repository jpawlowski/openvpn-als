
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.install.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.ContextKey;
import com.ovpnals.boot.KeyStoreManager;
import com.ovpnals.boot.KeyStoreType;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.boot.RepositoryStore;
import com.ovpnals.boot.Util;
import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.extensions.ExtensionBundle;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.install.forms.ConfigureProxiesForm;
import com.ovpnals.install.forms.ConfigureSuperUserForm;
import com.ovpnals.install.forms.CreateNewCertificateForm;
import com.ovpnals.install.forms.ImportExistingCertificateForm;
import com.ovpnals.install.forms.InstallForm;
import com.ovpnals.install.forms.SelectCertificateSourceForm;
import com.ovpnals.install.forms.SelectUserDatabaseForm;
import com.ovpnals.install.forms.SetKeyStorePasswordForm;
import com.ovpnals.install.forms.WebServerForm;
import com.ovpnals.jdbc.JDBCUserDatabase;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.realms.RealmKey;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.PasswordPolicyViolationException;
import com.ovpnals.security.PublicKeyStore;
import com.ovpnals.security.Role;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.UserDatabaseDefinition;
import com.ovpnals.setup.LicenseAgreementCallback;
import com.ovpnals.tasks.Task;
import com.ovpnals.tasks.TaskHttpServletRequest;
import com.ovpnals.tasks.TaskProgressBar;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.WizardActionStatus;

/**
 * Action that performs the final installation actions for the installer wizard.
 */
public class InstallAction extends AbstractInstallWizardAction {
    final static Log log = LogFactory.getLog(InstallAction.class);

    /**
     * Default certificate alias for OpenVPN-ALS web server
     */
    public static final String SERVER_CERTIFICATE = "server-certificate";

    private static final long INSTALL_TASK_DELAY = 250;

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        // Get the task and add some progress bars
        Task task = (Task) request.getAttribute(TaskHttpServletRequest.ATTR_TASK);
        TaskProgressBar overallProgress = new TaskProgressBar("overall", 0, 10, 0);
        task.addProgressBar(overallProgress);
        overallProgress.setNote(new BundleActionMessage("install", "taskProgress.install.overall.note"));
        TaskProgressBar atomicProgress = new TaskProgressBar("atomic", 0, 100, 0);
        task.addProgressBar(atomicProgress);

        // Do the install
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        ((InstallForm) form).setActionStatus(actionStatus);
        AbstractWizardSequence seq = getWizardSequence(request);


        overallProgress.setMaxValue(6);
        task.configured();

        /*
         * Do common stuff and get where to go next. This must be a redirect so
         * the task monitor works properly
         */
        super.unspecified(mapping, form, request, response);
        ActionForward fwd = mapping.findForward("installDone");

        doConfigureCertificate(overallProgress, atomicProgress, actionStatus, seq);
        doConfigureUserDatabase(overallProgress, atomicProgress, actionStatus, seq);
        doConfigureSuperUser(request, overallProgress, atomicProgress, actionStatus, seq);
        doWebServer(overallProgress, atomicProgress, actionStatus, seq);
        doConfigureProxies(request, overallProgress, atomicProgress, actionStatus, seq);
        doCommitProperties(overallProgress, atomicProgress);


        return fwd;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     */
    public ActionForward installDone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
         return mapping.findForward("display");
    }
    

    /**
     * Redisplay.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward redisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("display");
    }

    /**
     * Exit the installation wizard and shutdown.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        request.getSession().removeAttribute(Constants.WIZARD_SEQUENCE);
        return mapping.findForward("exitInstaller");
    }

    /**
     * Re-run the installation wizard.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward rerun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("rerun");
    }



    private void doConfigureCertificate(TaskProgressBar overallProgress, TaskProgressBar atomicProgress,
                                        List<WizardActionStatus> actionStatus, AbstractWizardSequence seq)
                    throws InterruptedException {
        // Certificates (1)
        overallProgress.setValue(1);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.certificates.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        actionStatus.add(configureCertificate(seq));
        Thread.sleep(INSTALL_TASK_DELAY);
    }

    private void doConfigureUserDatabase(TaskProgressBar overallProgress, TaskProgressBar atomicProgress,
                                         List<WizardActionStatus> actionStatus, AbstractWizardSequence seq) throws Exception,
                    InterruptedException {
        // User database (2)
        overallProgress.setValue(2);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.userDatabase.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        actionStatus.add(configureUserDatabase(seq));
        Thread.sleep(INSTALL_TASK_DELAY);
    }

    private void doConfigureSuperUser(HttpServletRequest request, TaskProgressBar overallProgress, TaskProgressBar atomicProgress,
                                      List<WizardActionStatus> actionStatus, AbstractWizardSequence seq)
                    throws InterruptedException {
        // Super user (3)
        overallProgress.setValue(3);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.superUser.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        actionStatus.addAll(configureSuperUser(seq, request));
        Thread.sleep(INSTALL_TASK_DELAY);
    }

    private void doWebServer(TaskProgressBar overallProgress, TaskProgressBar atomicProgress,
                             List<WizardActionStatus> actionStatus, AbstractWizardSequence seq) throws InterruptedException {
        // Web server (4)
        overallProgress.setValue(4);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.webServer.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        actionStatus.add(webServer(seq));
        Thread.sleep(INSTALL_TASK_DELAY);
    }

    private void doConfigureProxies(HttpServletRequest request, TaskProgressBar overallProgress, TaskProgressBar atomicProgress,
                                    List<WizardActionStatus> actionStatus, AbstractWizardSequence seq) throws InterruptedException {
        // Proxies (5)
        overallProgress.setValue(5);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.proxies.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        actionStatus.add(configureProxies(seq, request));
        Thread.sleep(INSTALL_TASK_DELAY);
    }

    private void doCommitProperties(TaskProgressBar overallProgress, TaskProgressBar atomicProgress) throws InterruptedException {
        // Now commit properties (6)
        overallProgress.setValue(6);
        atomicProgress.setNote(new BundleActionMessage("install", "taskProgress.install.atomic.commitProperties.note"));
        Thread.sleep(INSTALL_TASK_DELAY);
        PropertyClassManager.getInstance().commit();
        Thread.sleep(INSTALL_TASK_DELAY);
    }



    WizardActionStatus configureProxies(AbstractWizardSequence seq, HttpServletRequest request) {
        try {
            // boolean useSOCKSProxy =
            // "true".equals(seq.getAttribute(ConfigureProxiesForm.ATTR_USE_SOCKS_PROXY,
            // "false"));
            boolean useHTTPProxy = "true".equals(seq.getAttribute(ConfigureProxiesForm.ATTR_USE_HTTP_PROXY, "false"));

            /*
             * Configure SOCKS proxy. Note Maverick HTTP does not yet support
             * this but will at some point ;-)
             */
            // if (useSOCKSProxy) {
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyHost",
            // (String)
            // seq.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_HOSTNAME,
            // ""), seq.getSession());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyPort",
            // (String)
            // seq.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_PORT, ""),
            // getSessionInfo());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyUser",
            // (String)
            // seq.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_USERNAME,
            // ""), getSessionInfo());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyPassword",
            // (String)
            // seq.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_PASSWORD,
            // ""), getSessionInfo());
            // } else {
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyHost", "", getSessionInfo());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyPort", "3128", getSessionInfo());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyUser", "", getSessionInfo());
            // PropertyUtil.getPropertyUtil().setProperty(0, null,
            // "proxies.socksProxyPassword", "", getSessionInfo());
            // }
            /*
             * Configure HTTP proxy. Supported by both Java API and Maverick
             * HTTP.
             */
            SessionInfo sessionInfo = getSessionInfo(request);
            if (useHTTPProxy) {
                Property.setProperty(new ContextKey("proxies.http.proxyHost"), (String) seq.getAttribute(
                    ConfigureProxiesForm.ATTR_HTTP_PROXY_HOSTNAME, ""), sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyPort"), (String) seq.getAttribute(
                    ConfigureProxiesForm.ATTR_HTTP_PROXY_PORT, ""), sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyUser"), (String) seq.getAttribute(
                    ConfigureProxiesForm.ATTR_HTTP_PROXY_USERNAME, ""), sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyPassword"), (String) seq.getAttribute(
                    ConfigureProxiesForm.ATTR_HTTP_PROXY_PASSWORD, ""), sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.nonProxyHosts"), ((PropertyList) seq.getAttribute(
                    ConfigureProxiesForm.ATTR_HTTP_NON_PROXY_HOSTS, null)), sessionInfo);
            } else {
                Property.setProperty(new ContextKey("proxies.http.proxyHost"), "", sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyPort"), 1080, sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyUser"), "", sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.proxyPassword"), "", sessionInfo);
                Property.setProperty(new ContextKey("proxies.http.nonProxyHosts"), PropertyList.EMPTY_LIST, sessionInfo);
            }
            return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.proxiesConfigured");
        } catch (Exception e) {
            log.error("Failed to configure web server.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.failedToConfigureProxies", e.getMessage());
        }
    }

    WizardActionStatus configureUserDatabase(AbstractWizardSequence seq) throws Exception {
        Realm r = UserDatabaseManager.getInstance().getDefaultRealm();
        String newDatabase = (String) seq.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE, "");
        Boolean changed = (Boolean) seq.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE_CHANGED, Boolean.FALSE);
        UserDatabase defaultUserDatabase = UserDatabaseManager.getInstance().getDefaultUserDatabase();

        // if the defaultUserDatabase couldn't be opened previously then there's
        // nothing we can do here
        if (changed.booleanValue() && defaultUserDatabase.isOpen()) {
            // we need to disassociate all users and roles from the system.
            PolicyDatabaseFactory.getInstance().revokeAllPoliciesFromPrincipals(r);
            // close the old database.
            defaultUserDatabase.close();
        }
        Property.setProperty(new RealmKey("security.userDatabase", r), newDatabase, seq.getSession());
        return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.userDatabaseConfigured");
    }

    WizardActionStatus webServer(AbstractWizardSequence seq) {
        Property.setProperty(new ContextKey("webServer.port"),
            (String) seq.getAttribute(WebServerForm.ATTR_WEB_SERVER_PORT, "443"), seq.getSession());
        Property.setProperty(new ContextKey("webServer.protocol"), (String) seq.getAttribute(
            WebServerForm.ATTR_WEB_SERVER_PROTOCOL, "https"), seq.getSession());
        PropertyList l = PropertyList.createFromTextFieldText((String) seq
                        .getAttribute(WebServerForm.ATTR_LISTENING_INTERFACES, ""));
        Property.setProperty(new ContextKey("webServer.bindAddress"), l, seq.getSession());
        l = PropertyList.createFromTextFieldText((String) seq.getAttribute(WebServerForm.ATTR_VALID_EXTERNAL_HOSTS, ""));
        Property.setProperty(new SystemConfigKey("webServer.validExternalHostnames"), l, seq.getSession());
        Property.setProperty(new SystemConfigKey("webServer.invalidHostnameAction"), (String) seq.getAttribute(
            WebServerForm.ATTR_INVALID_HOSTNAME_ACTION, "none"), seq.getSession());
        return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.webServerConfigured");
    }

    List<WizardActionStatus> configureSuperUser(AbstractWizardSequence seq, HttpServletRequest request) {

        List<WizardActionStatus> l = new ArrayList<WizardActionStatus>();

        String superUser = (String) seq.getAttribute(ConfigureSuperUserForm.ATTR_SUPER_USER, null);
        String superUserPassword = (String) seq.getAttribute(ConfigureSuperUserForm.ATTR_SUPER_USER_PASSWORD, "");
        String email = (String) seq.getAttribute(ConfigureSuperUserForm.ATTR_SUPER_USER_EMAIL, "");

        // TODO implement as special policy instead of default administrator
        try {
            UserDatabase udb = (UserDatabase) seq.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE_INSTANCE, null);
            if (!udb.isOpen()) {
                udb.open(CoreServlet.getServlet(), udb.getRealm());
            }

            User user = null;
            try {
                /*
                 * Try and get the super user. If an exception occurs we assume
                 * it doesn't exist and so try to create, otherwise it is
                 * updated
                 */

                user = udb.getAccount(superUser);
                resetUser(request, user);

                /*
                 * The super exists so just update it
                 */
                if (isSuperUserCreationSupported(udb)) {
                    // (Probably) Already exists, just update
                    udb.updateAccount(user, email, user.getFullname(), user.getRoles());
                    l.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.superUserUpdated"));
                }
            } catch (Exception unfe) {
                if (isSuperUserCreationSupported(udb)) {
                    udb.createAccount(superUser, superUserPassword, email, "Super User", new Role[] {});
                    l.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.superUserCreated"));
                    user = udb.getAccount(superUser);
                    resetUser(request, user);
                }
            }

            Property.setProperty(new RealmKey("security.administrators", user.getRealm().getResourceId()), superUser, seq
                            .getSession());

            /*
             * Set the super user password
             */
            if (udb.supportsPasswordChange() && !superUserPassword.equals("")) {
                udb.changePassword(superUser, "", superUserPassword, false);
                l.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.superUserPasswordSet"));
            }
        } catch (PasswordPolicyViolationException e) {
            log.error("Failed to configure super user.", e);
            l.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.failedToInstallExtension.password"));
        } catch (Exception e) {
            log.error("Failed to configure super user.", e);
            l.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.failedToConfigureSuperUser", e.getMessage()));
        }
        return l;
    }

    private void resetUser(HttpServletRequest request, User user) throws Exception {
        boolean disabled = !PolicyUtil.isEnabled(user);
        SessionInfo session = this.getSessionInfo(request);
        if (disabled) {
            if (log.isInfoEnabled())
                log.info("Re-enabling user " + user.getPrincipalName());
            PolicyUtil.setEnabled(user, true, null, session);
        }
        LogonControllerFactory.getInstance().unlockUser(user.getPrincipalName());

        /**
         * LDP - Reset the private key for this user, if they have lost their
         * password this is the only way to reset but they will be asked for
         * their old password if we do not reset this.
         */
        if (PublicKeyStore.getInstance().hasPrivateKey(user.getPrincipalName())) {
            PublicKeyStore.getInstance().removeKeys(user.getPrincipalName());
        }
    }

    private boolean isSuperUserCreationSupported(UserDatabase userDatabase) {
        UserDatabaseDefinition userDatabaseDefinition = UserDatabaseManager.getInstance().getUserDatabaseDefinition(
            JDBCUserDatabase.DATABASE_TYPE);
        if (userDatabaseDefinition == null) {
            return false;
        }
        Class userDatabaseClass = userDatabaseDefinition.getUserDatabaseClass();
        boolean isJdbcDatabase = userDatabaseClass.equals(userDatabase.getClass());
        return isJdbcDatabase && userDatabase.supportsAccountCreation();
    }


    WizardActionStatus configureCertificate(AbstractWizardSequence seq) {
        String certSource = (String) seq.getAttribute(SelectCertificateSourceForm.ATTR_CERTIFICATE_SOURCE, "");
        if (certSource.equals(SelectCertificateSourceForm.CREATE_NEW_CERTIFICATE)) {
            return createNewCertificate(seq);
        } else if (certSource.equals(SelectCertificateSourceForm.IMPORT_EXISTING_CERTIFICATE)) {
            return importCertificate(seq);
        } else {
            return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.usedCurrentCertificate");
        }
    }

    WizardActionStatus createNewCertificate(AbstractWizardSequence seq) {

        try {

            KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE);

            if (mgr.isKeyStoreExists()) {
                mgr.deleteKeyStore();
            }

            String alias = InstallAction.SERVER_CERTIFICATE;
            String passphrase = (String) seq.getAttribute(SetKeyStorePasswordForm.ATTR_KEY_STORE_PASSWORD, null);
            if (passphrase != null && !passphrase.equals("")) {
                Property.setProperty(new ContextKey("webServer.keystore.sslCertificate.password"), passphrase, seq.getSession());
                mgr.setStorePassword(passphrase);
            }

            mgr.createKeyStore();
            String dname = "cn="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_HOSTNAME, ""))
                            + ", ou="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_ORGANISATIONAL_UNIT,
                                "")) + ", o="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_COMPANY, "")) + ", l="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_CITY, "")) + ", st="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_STATE, "")) + ", c="
                            + Util.escapeForDNString((String) seq.getAttribute(CreateNewCertificateForm.ATTR_COUNTRY_CODE, ""));
            mgr.createKey(alias, dname);
            Property.setProperty(new ContextKey("webServer.keyStoreType"), KeyStoreManager.TYPE_JKS.getName(), null);
            Property.setProperty(new ContextKey("webServer.alias"), alias, null);

            CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_CERTIFICATE_CREATED, alias, null).addAttribute(
                CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, alias).addAttribute(CreateNewCertificateForm.ATTR_HOSTNAME,
                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_HOSTNAME, "")).addAttribute(
                CreateNewCertificateForm.ATTR_ORGANISATIONAL_UNIT,
                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_ORGANISATIONAL_UNIT, "")).addAttribute(
                CreateNewCertificateForm.ATTR_COMPANY, (String) seq.getAttribute(CreateNewCertificateForm.ATTR_COMPANY, ""))
                            .addAttribute(CreateNewCertificateForm.ATTR_STATE,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_STATE, "")).addAttribute(
                                CreateNewCertificateForm.ATTR_CITY,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_CITY, "")).addAttribute(
                                CreateNewCertificateForm.ATTR_COUNTRY_CODE,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_COUNTRY_CODE, ""));

            CoreServlet.getServlet().fireCoreEvent(coreEvent);

        } catch (Exception e) {
            log.error("Failed to create keystore / certificate.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.failedToCreateNewCertificate", e.getMessage());
        }
        return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.newCertificateCreated");
    }

    WizardActionStatus importCertificate(AbstractWizardSequence seq) {

        try {

            KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE);

            if (mgr.isKeyStoreExists()) {
                mgr.deleteKeyStore();
            }

            String alias = (String) seq.getAttribute(ImportExistingCertificateForm.ATTR_ALIAS, null);
            String passphrase = (String) seq.getAttribute(ImportExistingCertificateForm.ATTR_PASSPHRASE, "");
            KeyStoreType keyStoreType = KeyStoreManager.getKeyStoreType((String) seq.getAttribute(
                ImportExistingCertificateForm.ATTR_KEY_STORE_TYPE, ""));
            File uploadedFile = (File) seq.getAttribute(ImportExistingCertificateForm.ATTR_UPLOADED_FILE, null);

            mgr.setStorePassword(passphrase);

            if (keyStoreType.equals(KeyStoreManager.TYPE_PKCS12)) {
                mgr.setKeyStoreType(KeyStoreManager.TYPE_JKS);
                alias = mgr.importPKCS12Key(uploadedFile, passphrase, alias, SERVER_CERTIFICATE);
            } else {
            	FileOutputStream out = new FileOutputStream(mgr.getKeyStoreFile());
                try {
                    FileInputStream in = new FileInputStream(uploadedFile);
                    try {
                        Util.copy(in, out);
                    } finally {
                        in.close();
                    }
                } finally {
                    out.close();
                }
                
                KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).reloadKeystore();

                try {
                	alias = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getKeyStore().aliases().nextElement();
                } catch(Throwable ex) {
                	log.error("Could not find first alias", ex);
                }
                
            }

            Property.setProperty(new ContextKey("webServer.alias"), alias, null);
            Property.setProperty(new ContextKey("webServer.keystore.sslCertificate.password"), passphrase, null);
            CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_IMPORTED, null, null).addAttribute(
                CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, alias).addAttribute(CreateNewCertificateForm.ATTR_HOSTNAME,
                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_HOSTNAME, "")).addAttribute(
                CreateNewCertificateForm.ATTR_ORGANISATIONAL_UNIT,
                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_ORGANISATIONAL_UNIT, "")).addAttribute(
                CreateNewCertificateForm.ATTR_COMPANY, (String) seq.getAttribute(CreateNewCertificateForm.ATTR_COMPANY, ""))
                            .addAttribute(CreateNewCertificateForm.ATTR_STATE,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_STATE, "")).addAttribute(
                                CreateNewCertificateForm.ATTR_CITY,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_CITY, "")).addAttribute(
                                CreateNewCertificateForm.ATTR_COUNTRY_CODE,
                                (String) seq.getAttribute(CreateNewCertificateForm.ATTR_COUNTRY_CODE, ""));

            CoreServlet.getServlet().fireCoreEvent(coreEvent);

        } catch (Exception e) {
            log.error("Failed to create keystore / certificate.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.failedToImportCertificate", e.getMessage());
        } finally {
        }
        return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "installation.install.status.certificateImported");
    }

    class ExtensionLicenseAgreementCallback implements LicenseAgreementCallback {

        private ExtensionBundle bundle;
        private RepositoryStore repStore;
        private List<WizardActionStatus> actionStatus;

        ExtensionLicenseAgreementCallback(RepositoryStore repStore, ExtensionBundle bundle, List<WizardActionStatus> actionStatus) {
            this.bundle = bundle;
            this.repStore = repStore;
            this.actionStatus = actionStatus;

        }

        public void licenseAccepted(HttpServletRequest request) {
            try {
                ExtensionStore.getInstance().postInstallExtension(bundle, request);
                actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                                "installation.install.status.installedExtension", bundle.getName(), bundle.getId()));
            } catch (Exception e) {
                log.error("Failed to install extension " + bundle.getId() + ".", e);
                actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                                "installation.install.status.failedToInstallExtension", bundle.getId(), e.getMessage()));

            }
        }

        public void licenseRejected(HttpServletRequest request) {

            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "installation.install.status.licenseRejected", bundle.getId()));

            // Remove the repository entry if it is in use
            if (ExtensionStore.getInstance().isRepositoryBacked()) {
                try {
                    repStore.removeEntry(bundle.getId() + ".zip");
                } catch (IOException ex) {
                }
            }

            // Remove the expanded bundle
            if (bundle.getBaseDir().exists()) {
                Util.delTree(bundle.getBaseDir());
            }

            // Reload the extension store
            try {
                ExtensionStore.getInstance().reload(bundle.getId());
            } catch (Exception e) {
                log.error("Failed to reload extension store.");
            }
        }
    }
}