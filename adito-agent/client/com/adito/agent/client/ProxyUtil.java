
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
			
package com.adito.agent.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.adito.agent.client.util.Utils;

/**
 * Utilities to detect proxy server settings from the browser.
 */
public class ProxyUtil {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ProxyUtil.class);
    // #endif

    /**
     * Attempt to proxy settings from Firefox.
     * 
     * @return firefox proxy settings
     * @throws IOException if firefox settings could not be obtained for some
     *         reason
     */
    public static BrowserProxySettings lookupFirefoxProxySettings() throws IOException {

        try {

            Vector proxies = new Vector();
            Vector bypassAddr = new Vector();

            File home = new File(Utils.getHomeDirectory());
            File firefoxAppData;

            if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                firefoxAppData = new File(home, "Application Data\\Mozilla\\Firefox\\profiles.ini"); //$NON-NLS-1$
            } else {
                firefoxAppData = new File(home, ".mozilla/firefox/profiles.ini"); //$NON-NLS-1$
            }

            // Look for Path elements in the profiles.ini
            BufferedReader reader = null;
            Hashtable profiles = new Hashtable();
            String line;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(firefoxAppData)));
                String currentProfileName = ""; //$NON-NLS-1$

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("[") && line.endsWith("]")) { //$NON-NLS-1$ //$NON-NLS-2$
                        currentProfileName = line.substring(1, line.length() - 1);
                        continue;
                    }

                    if (line.startsWith("Path=")) { //$NON-NLS-1$
                        profiles.put(currentProfileName, new File(firefoxAppData.getParent(), line.substring(5)));
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            // Iterate through all the profiles and load the proxy infos from
            // the prefs.js file

            File prefsJS;
            String profileName;
            for (Enumeration e = profiles.keys(); e.hasMoreElements();) {
                profileName = (String) e.nextElement();
                prefsJS = new File((File) profiles.get(profileName), "prefs.js"); //$NON-NLS-1$
                Properties props = new Properties();
                reader = null;
                try {
                    if (!prefsJS.exists()){
                        // needed to defend against un-initialised profiles.
                        // #ifdef DEBUG
                        log.info("The file " + prefsJS.getAbsolutePath() + " does not exist."); //$NON-NLS-1$
                        // #endif
                        // now remove it from the map.
                        profiles.remove(profileName);
                        continue;
                    }
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(prefsJS)));
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("user_pref(\"")) { //$NON-NLS-1$
                            int idx = line.indexOf("\"", 11); //$NON-NLS-1$
                            if (idx == -1)
                                continue;
                            String pref = line.substring(11, idx);

                            // Save this position
                            int pos = idx + 1;

                            // Look for another quote
                            idx = line.indexOf("\"", idx + 1); //$NON-NLS-1$

                            String value;
                            if (idx == -1) {
                                // No more quotes
                                idx = line.indexOf(" ", pos); //$NON-NLS-1$

                                if (idx == -1)
                                    continue;

                                int idx2 = line.indexOf(")", pos); //$NON-NLS-1$

                                if (idx2 == -1)
                                    continue;

                                value = line.substring(idx + 1, idx2);

                            } else {

                                // String value
                                int idx2 = line.indexOf("\"", idx + 1); //$NON-NLS-1$

                                if (idx2 == -1)
                                    continue;

                                value = line.substring(idx + 1, idx2);
                            }

                            props.put(pref, value);

                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
                ProxyInfo p;
                /**
                 * Extract some proxies from the properites, if the proxy is
                 * enabled
                 */
                if ("1".equals(props.get("network.proxy.type"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    boolean isProfileActive = checkProfileActive(prefsJS);
                    if (props.containsKey("network.proxy.ftp")) { //$NON-NLS-1$
                        p = createProxyInfo("ftp=" + props.get("network.proxy.ftp") + ":" + props.get("network.proxy.ftp_port"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                            "Firefox Profile [" + profileName + "]" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        p.setActiveProfile(isProfileActive);
                        proxies.addElement(p);
                    }

                    if (props.containsKey("network.proxy.http")) { //$NON-NLS-1$
                        p = createProxyInfo("http=" + props.get("network.proxy.http") + ":" + props.get("network.proxy.http_port"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                            "Firefox Profile [" + profileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                        p.setActiveProfile(isProfileActive);
                        proxies.addElement(p);
                    }

                    if (props.containsKey("network.proxy.ssl")) { //$NON-NLS-1$
                        p = createProxyInfo("ssl=" + props.get("network.proxy.ssl") + ":" + props.get("network.proxy.ssl_port"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                            "Firefox Profile [" + profileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                        p.setActiveProfile(isProfileActive);
                        proxies.addElement(p);
                    }

                    if (props.containsKey("network.proxy.socks")) { //$NON-NLS-1$
                        p = createProxyInfo("socks=" + props.get("network.proxy.socks") + ":" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                        + props.get("network.proxy.socks_port"), "Firefox Profile [" + profileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        p.setActiveProfile(isProfileActive);
                        proxies.addElement(p);
                    }

                    if (props.containsKey("network.proxy.no_proxies_on")) { //$NON-NLS-1$

                        StringTokenizer tokens = new StringTokenizer(props.getProperty("network.proxy.no_proxies_on"), ","); //$NON-NLS-1$ //$NON-NLS-2$

                        while (tokens.hasMoreTokens()) {
                            bypassAddr.addElement(((String) tokens.nextToken()).trim());
                        }

                    }
                }
            }

            // need to ensure that the returned values are sorted correctly...
            BrowserProxySettings bps = new BrowserProxySettings();
            bps.setBrowser("Mozilla Firefox"); //$NON-NLS-1$
            bps.setProxiesActiveFirst(proxies);
            bps.setBypassAddr(new String[bypassAddr.size()]);
            bypassAddr.copyInto(bps.getBypassAddr());
            return bps;

        } catch (Throwable t) {
            throw new IOException("Failed to get proxy information from Firefox profiles: " + t.getMessage()); //$NON-NLS-1$
        }
    }

    private static boolean checkProfileActive(File prefsJS) {
        String parentFile = prefsJS.getParent();
        File lockFile = new File(parentFile, "parent.lock");
        if (lockFile.exists()){
            return true;
        }
        else{
            return false;
        }
        
    }

    /**
     * Attempt to proxy settings from Internet Explorer.
     * 
     * @return internet explorer proxy settings
     * @throws IOException if IE settings could not be obtained for some reason
     */
    public static BrowserProxySettings lookupIEProxySettings() throws IOException {

        try {
            Vector addresses = new Vector();
            Vector proxies = new Vector();
            String proxyServerValue = null;
            String proxyOveride = null;

            /* Only use jRegistry if on Windows, running 1.3 or up Java
             * and NOT Windows Vista with JDK6.0 (because of jvm crash)
             */
            
            if (Utils.isSupportedJRE("+1.3") && Utils.isSupportedPlatform("Windows") /*&&
            		!(Utils.isSupportedOSVersion("+6.0") && Utils.isSupportedJRE("+1.6"))*/) {
            		
                /*
                 * We can use jRegistryKey API to lookup IE settings in the
                 * registry
                 */
//                RegistryKey key = new RegistryKey(RootKey.HKEY_CURRENT_USER,
//                                "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings"); //$NON-NLS-1$

                String proxyEnable = WinRegistry.getRegistryValue("user", "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable", "0");
                if (proxyEnable!=null) { //$NON-NLS-1$
                    /*
                     * We have ProxyEnable so check to see if we are using a
                     * proxy
                     */
                    if (proxyEnable.equals("1")) { //$NON-NLS-1$ //$NON-NLS-2$

                    	proxyServerValue = WinRegistry.getRegistryValue("user", "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyServer", null);
                        if (proxyServerValue!=null) { //$NON-NLS-1$
                            /**
                             * We have some proxy settings. The values will be
                             * in the format "server.proxy.net:8888" or
                             */
                        	
                        	proxyOveride = WinRegistry.getRegistryValue("user", "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyOverride", null);
                        }
                    } else {

                    }
                }
            } else {
                if (System.getProperty("java.vendor").startsWith("Microsoft")) { //$NON-NLS-1$ //$NON-NLS-2$
                    try {
                        Class clazz = Class.forName("com.ms.lang.RegKey"); //$NON-NLS-1$
                        int userRoot = clazz.getField("USER_ROOT").getInt(null); //$NON-NLS-1$
                        int keyOpenAll = clazz.getField("KEYOPEN_ALL").getInt(null); //$NON-NLS-1$
                        // #ifdef DEBUG
                        log.info(Messages.getString("ProxyUtil.lookingForRoot")); //$NON-NLS-1$
                        // #endif
                        Object rootKey = clazz.getMethod("getRootKey", new Class[] { int.class }).invoke(null, //$NON-NLS-1$
                            new Object[] { new Integer(userRoot) });
                        // #ifdef DEBUG
                        log.info(Messages.getString("ProxyUtil.getIERegistryKey")); //$NON-NLS-1$
                        // #endif
                        Object key = clazz.getConstructor(new Class[] { clazz, String.class, int.class }).newInstance(
                            new Object[] { rootKey, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", //$NON-NLS-1$
                                            new Integer(keyOpenAll) });
                        // #ifdef DEBUG
                        log.info(Messages.getString("ProxyUtil.checkingIfProxyEnabled")); //$NON-NLS-1$
                        // #endif
                        if (((Integer) (clazz.getMethod("getIntValue", new Class[] { String.class }).invoke(key, //$NON-NLS-1$
                            new Object[] { "ProxyEnable" }))).intValue() == 1) { //$NON-NLS-1$
                            // #ifdef DEBUG
                            log.info(Messages.getString("ProxyUtil.gettingProxyServerList")); //$NON-NLS-1$
                            // #endif
                            proxyServerValue = (String) (clazz.getMethod("getStringValue", //$NON-NLS-1$
                                new Class[] { String.class, String.class }).invoke(key, new Object[] { "ProxyServer", "" })); //$NON-NLS-1$ //$NON-NLS-2$
                            // #ifdef DEBUG
                            log.info(Messages.getString("ProxyUtil.gettingProxyOverides")); //$NON-NLS-1$
                            // #endif
                            proxyOveride = (String) (clazz.getMethod("getStringValue", new Class[] { String.class, String.class }) //$NON-NLS-1$
                                            .invoke(key, new Object[] { "ProxyOverride", "" })); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                } else {
                    // #ifdef DEBUG
                    log.info(MessageFormat.format(Messages.getString("ProxyUtil.unsupportedJavaRuntime"), new Object[] { System.getProperty("java.version"), System.getProperty("java.vendor") } ) ); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    // #endif
                }

            }

            ProxyInfo p;

            if (proxyServerValue != null && proxyServerValue.indexOf(';') > -1) {
                /**
                 * Format is multiple
                 * "ftp=ftp.com:4444;gopher=gopher.com:3333;http=198.162.1.119:8888;https=https.com:2222;socks=socks.com:1111"
                 */
                StringTokenizer tokens = new StringTokenizer(proxyServerValue, ";"); //$NON-NLS-1$

                while (tokens.hasMoreTokens()) {
                    p = createProxyInfo(tokens.nextToken(), "IE Proxy Settings"); //$NON-NLS-1$
                    proxies.addElement(p);
                }

            } else if (proxyServerValue != null) {
                /**
                 * Format is single "http=server.proxy.net:8888" or
                 * "server.proxy.net:8888"
                 */
                p = createProxyInfo(proxyServerValue, "IE Proxy Settings"); //$NON-NLS-1$
                proxies.addElement(p);
            }

            BrowserProxySettings bps = new BrowserProxySettings();
            bps.setBrowser("Internet Explorer"); //$NON-NLS-1$
            bps.setProxies(new ProxyInfo[proxies.size()]);
            proxies.copyInto(bps.getProxies());
            if (proxyOveride != null) {

                StringTokenizer tokens = new StringTokenizer(proxyOveride, ";"); //$NON-NLS-1$

                while (tokens.hasMoreTokens()) {
                    addresses.addElement(tokens.nextToken());
                }
            }

            bps.setBypassAddr(new String[addresses.size()]);
            addresses.copyInto(bps.getBypassAddr());
            return bps;

        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException(MessageFormat.format(
                Messages.getString("ProxyUtil.failedToLookupIEProxies"), new Object[] { t.getMessage() } ) ); //$NON-NLS-1$
        }
    }

    private static ProxyInfo createProxyInfo(String proxyInfo, String sourceIdent) {
        int idx = proxyInfo.indexOf('=');
        int idx2 = proxyInfo.indexOf(':');
        ProxyInfo proxy = new ProxyInfo(idx > -1 ? proxyInfo.substring(0, idx) : "all", "", "", proxyInfo.substring( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            (idx > -1 ? idx + 1 : 0), idx2), Integer.parseInt(proxyInfo.substring(idx2 + 1)), sourceIdent);
        return proxy;
    }

    /**
     * Test entry point.
     * 
     * @param args arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
    	try {
	    	BrowserProxySettings settings = lookupFirefoxProxySettings();
	        // #ifdef DEBUG
	    	System.out.println("Browser = " + settings.getBrowser()); //$NON-NLS-1$
	        // #endif
	        ProxyInfo[] info = settings.getProxies();
	        for (int i = 0; i < info.length; i++) {
	            // #ifdef DEBUG
	        	System.out.println("    " + info[i].toUri() + " [" + info[i].getSourceIdent() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            // #endif
	        }
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
        
        try {
	        BrowserProxySettings settings = lookupIEProxySettings();
	        // #ifdef DEBUG
	        System.out.println("Browser = " + settings.getBrowser()); //$NON-NLS-1$
	        // #endif
	        ProxyInfo[] info = settings.getProxies();
	        for (int i = 0; i < info.length; i++) {
	            // #ifdef DEBUG
	        	System.out.println("    " + info[i].toUri() + " [" + info[i].getSourceIdent() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            // #endif
	        }
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
    }

}
