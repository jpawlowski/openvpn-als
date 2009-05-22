package com.ovpnals.core.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.Util;
import com.ovpnals.core.BrowserChecker;

public class BrowserCheckTag extends BodyTagSupport {
	
	final static Log log = LogFactory.getLog(BrowserCheckTag.class);

    private String browser;
    private String version;
    private boolean required = true;

    public BrowserCheckTag() {
        super();
    }

    public int doStartTag() {
    	BrowserChecker checker = new BrowserChecker(((HttpServletRequest)pageContext.getRequest()).getHeader("user-agent"));
    	boolean ok = checker.isBrowserVersionExpression(browser, Util.isNullOrTrimmedBlank(version) ? "*" : version);
        return ok ? (required ? EVAL_BODY_INCLUDE : SKIP_BODY ) : (required ? SKIP_BODY : EVAL_BODY_INCLUDE );
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public void setBrowser(String browser) {
    	this.browser = browser;
    }

    public void release() {
        super.release();
        version = null;
        browser = null;
        required = true;
    }

}
