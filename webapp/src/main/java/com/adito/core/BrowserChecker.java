package com.adito.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Util;

public class BrowserChecker extends BodyTagSupport {
	
	final static Log log = LogFactory.getLog(BrowserChecker.class);

	public static final String BROWSER_IE = "IE";
	public static final String BROWSER_OPERA = "Opera";
	public static final String BROWSER_SAFARI = "Safari";
	public static final String BROWSER_FIREFOX = "Firefox";
	
	static List<Browser> browsers = new ArrayList<Browser>();
	static {
		URL u = BrowserChecker.class.getResource("/META-INF/userAgents.properties");
		if(u == null) {
			log.error("Failed to find /META-INF/userAgents.txt, cannot detect browser.");
		}
		else {
			InputStream in = null;
			try {
				in = u.openStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				String line;
				while ( ( line = r.readLine() ) != null) {
					line = line.trim();
					if(!line.startsWith("#") && line.length() != 0) {
						try {
							browsers.add(new Browser(line));
						}
						catch(Exception e) {
							log.error("Syntax error '" + line + "'.");
						}
					}
				}
			}
			catch(IOException ioe) {
				log.error("Failed to load /META-INF/userAgents.txt", ioe);
			}
			finally {
				Util.closeStream(in);
			}
		}
	}

	private Browser browser;

    public BrowserChecker() {
    }

    public BrowserChecker(String userAgent) {
        setUserAgent(userAgent);
    }
    
    public void setUserAgent(String userAgent) {
        for(Browser b : browsers) {
        	if(userAgent.contains(b.getUserAgentPattern())) {
        		browser = b;
        		break;
        	}
        }
    	
    }
    
    public Browser getBrowser() {
    	return browser;
    }
    
    public boolean isBrowserVersion(String name, int version) {
    	return isBrowserVersionExpression(name, version == -1 ? "*" : "=" + version);
    }
    
    public boolean isBrowserVersionExpression(String name, String versionExpression) {
    	return isBrowser(name) && isVersionExpression(versionExpression);
    }    
    
    private boolean isBrowser(String name) {
    	return browser != null && browser.getName().equalsIgnoreCase(name);
    }
    
    private boolean isVersion(int version) {
    	return isVersionExpression("=" + version);
    }
    
    private boolean isVersionExpression(String versionExpression) { 
    	
    	boolean not = false;
    	
    	// Parse the expression
    	if(versionExpression.startsWith("!")) {
    		not = true;    		
    		versionExpression = versionExpression.substring(1);
    	}
    	boolean gt = false;
    	if(versionExpression.startsWith("+")) {
    		gt = true;	
    		versionExpression = versionExpression.substring(1);
    	}
    	boolean lt = false;
    	if(versionExpression.startsWith("-")) {
    		lt = true;	
    		versionExpression = versionExpression.substring(1);
    	}
    	boolean eq = false;
    	if(versionExpression.startsWith("=")) {
    		eq = true;	
    		versionExpression = versionExpression.substring(1);
    	}
    	
    	boolean ok = false;
    	
    	// Test if any version
    	if(versionExpression.equals("*")) {
    		ok = true;	
    	}
    	else {    	
	    	int version = Integer.parseInt(versionExpression);
	    	
	    	// Test if version matches expression
	    	if(eq) {
	   			ok = lt ? browser.getVersion() <= version : ( gt ? browser.getVersion() >= version : ( browser.getVersion() == version ) );
	    	}
	    	else {
	    		ok = lt ? browser.getVersion() < version : ( gt ? browser.getVersion() > version : false );
	    	}
    	}
    	
    	// Negate?
    	
    	if(not) {
    		ok = !ok;
    	}
    	
    	return ok;
    }
    
    public static class Browser {
    	String userAgentPattern;
    	String name;
    	int version = -1;
    	
    	Browser(String line) {
    		String[] arr = line.split(",");
    		userAgentPattern = arr[0];
    		name = arr[1];
    		if(arr.length > 2) {
    			version = arr[2].equals("") ? -1 : Integer.parseInt(arr[2]);
    		}
    	}

		public String getUserAgentPattern() {
			return userAgentPattern;
		}

		public String getName() {
			return name;
		}
		
		public int getVersion() {
			return version;
		}
    	
    }

}
