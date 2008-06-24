/* HEAD */
package com.adito.agent.client;

import java.util.Enumeration;
import java.util.Vector;


/**
 * Encapsulate a vendors <i>Web Browser</i> such as IE or Firefox and its proxy
 * settings.
 */
public class BrowserProxySettings {

    // Private instance variables

    private String browser;
    private ProxyInfo[] proxies;
    private String[] bypassAddr;

    /**
     * Get the browser name.
     * 
     * @return browser name
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * Set the browser name.
     * 
     * @param browser browser name
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * Get a string array of all address that should bypass the proxy server.
     * Wildcards may or may not be included. An empty array and all
     * communication will occurr through the proxy servers.
     * 
     * @return Returns the bypassAddr.
     */
    public String[] getBypassAddr() {
        return bypassAddr;
    }

    /**
     * Set a string array of all address that should bypass the proxy server.
     * Wildcards may or may not be included. An empty array and all
     * communication will occurr through the proxy servers.
     * 
     * @param bypassAddr Returns the bypassAddr.
     */
    public void setBypassAddr(String[] bypassAddr) {
        this.bypassAddr = bypassAddr;
    }

    /**
     * Get an array of all proxy servers configured for this browser.
     * 
     * @return proxy servers
     */
    public ProxyInfo[] getProxies() {
        return proxies;
    }

    /**
     * Set an array of all proxy servers configured for this browser.
     * 
     * @param proxies proxy servers
     */
    public void setProxies(ProxyInfo[] proxies) {
        this.proxies = proxies;
    }

    /**
     * Set an array of all proxy servers configured for this browser, with the active profiles proxy first.
     * 
     * @param Vector proxy servers
     */
    public void setProxiesActiveFirst(Vector proxies) {
        Vector activeProfileProxies = new Vector();
        Vector inactiveProfileProxies = new Vector();
        for (Enumeration e = proxies.elements(); e.hasMoreElements();) {
            ProxyInfo p = (ProxyInfo) e.nextElement();
            if (p.isActiveProfile()){
                // get active profile proxies
                activeProfileProxies.addElement(p);
            }
            else{
                // get inactive profile proxies
                inactiveProfileProxies.addElement(p);
            }
        }
        // add them together and set as array in this object
        for (Enumeration e = inactiveProfileProxies.elements(); e.hasMoreElements();) {
            ProxyInfo p = (ProxyInfo) e.nextElement();
            activeProfileProxies.addElement(p);
        }
        setProxies(new ProxyInfo[proxies.size()]);
        activeProfileProxies.copyInto(getProxies());
    }
}
