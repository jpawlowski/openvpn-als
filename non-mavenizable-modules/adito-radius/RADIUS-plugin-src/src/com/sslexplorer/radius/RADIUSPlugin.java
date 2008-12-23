/* HEADER */

package com.sslexplorer.radius;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import com.sslexplorer.boot.ContextHolder;
import com.sslexplorer.boot.DefaultPropertyDefinition;
import com.sslexplorer.boot.PropertyDefinition;
import com.sslexplorer.core.CoreServlet;
import com.sslexplorer.core.ExtensionActionMapping;
import com.sslexplorer.core.ExtensionFormBeanConfig;
import com.sslexplorer.core.ExtensionMessageResourcesConfig;
import com.sslexplorer.core.MenuItem;
import com.sslexplorer.plugin.Plugin;
import com.sslexplorer.plugin.PluginDefinition;
import com.sslexplorer.plugin.PluginException;
import com.sslexplorer.security.AuthenticationSchemeManager;

/**
 * @author Brett Smith <brett@3sp.com>
 */
public class RADIUSPlugin implements Plugin {

    final static Log log = LogFactory.getLog(RADIUSPlugin.class);

    private CoreServlet coreServlet;
    private ExtensionMessageResourcesConfig[] mrc;
    private ExtensionActionMapping[] am;

    /**
     *  
     */ 
    public RADIUSPlugin() {
        super();
 
        // Create the new struts message resources.
        mrc = new ExtensionMessageResourcesConfig[1];
        mrc[0] = new ExtensionMessageResourcesConfig();
        mrc[0].setKey("radius");
        mrc[0].setParameter("com.sslexplorer.radius.ApplicationResources");
        
        // Create the new struts action mappings.        
        am = new ExtensionActionMapping[1];
        am[0] = new ExtensionActionMapping();
        am[0].setName("propertiesForm");
        am[0].setPath("/showRADIUSConfiguration");
        am[0].setScope("session");
        am[0].setType("com.sslexplorer.administration.actions.PropertiesAction");
        am[0].setValidate(false);
        am[0].setParameter("setup,200,/showRADIUSConfiguration.do?action=unspecified,false,0");
        am[0].setInput(".site.RADIUSConfiguration");
        am[0].addForwardConfig(new ActionForward("display", ".site.RADIUSConfiguration", false));

    }


    /* (non-Javadoc)
     * @see com.sslexplorer.plugin.Plugin#initPlugin(com.sslexplorer.plugin.PluginDefinition)
     */
    public void initPlugin(PluginDefinition arg0) throws PluginException {
        log.info("Initialising RADIUSPlugin");
        coreServlet = CoreServlet.getServlet();
        
        // Register the new authentication scheme
        AuthenticationSchemeManager.getInstance().registerScheme("RADIUS", RADIUSAuthenticationScheme.class, "radius");
        
        // Register the new property definitions

        PropertyDefinition serverHostName = new DefaultPropertyDefinition(PropertyDefinition.TYPE_STRING, "radius.serverHostName", "",
                        false, 200, "localhost", 2, 10, "radius");
        ContextHolder.getContext().getPropertyDatabase().registerPropertyDefinition(serverHostName);
        PropertyDefinition authenticationPort = new DefaultPropertyDefinition(PropertyDefinition.TYPE_INTEGER, "radius.authenticationPort", "",
                        false, 200, "1812", 2, 20, "radius");
        ContextHolder.getContext().getPropertyDatabase().registerPropertyDefinition(authenticationPort);
        PropertyDefinition accountingPort = new DefaultPropertyDefinition(PropertyDefinition.TYPE_INTEGER, "radius.accountingPort", "",
                        false, 200, "1813", 2, 30, "radius");
        ContextHolder.getContext().getPropertyDatabase().registerPropertyDefinition(accountingPort);
        PropertyDefinition sharedSecret = new DefaultPropertyDefinition(PropertyDefinition.TYPE_STRING, "radius.sharedSecret", "",
                        false, 200, "shared_secret", 2, 40, "radius");
        ContextHolder.getContext().getPropertyDatabase().registerPropertyDefinition(sharedSecret);
        PropertyDefinition authenticationMethod = new DefaultPropertyDefinition(PropertyDefinition.TYPE_LIST, "radius.authenticationMethod", "chap,ppp",
                        false, 200, "chap", 2, 50, "radius");
        ContextHolder.getContext().getPropertyDatabase().registerPropertyDefinition(authenticationMethod);
        
        // Add the new menu items
        coreServlet.addMenuItem("setup", new MenuItem("radiusConfiguration", "radius", "/showRADIUSConfiguration.do", true, true,
                        true));
    }

    public void startPlugin() throws PluginException {
        log.info("Starting RADIUSPlugin");
    }

    public boolean canStopPlugin() {
        return false;
    }

    public void stopPlugin() throws PluginException {
    }

    public ExtensionMessageResourcesConfig[] getMessageResourcesConfig() {
        return mrc;
    }

    public ExtensionActionMapping[] getActionMapping() {
        return am;
    }

    public ExtensionFormBeanConfig[] getFormBeanConfig() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.sslexplorer.plugin.Plugin#getTilesConfigFile()
     */
    public String getTilesConfigFile() {
        return "/WEB-INF/radius-tiles-defs.xml";
    }
}