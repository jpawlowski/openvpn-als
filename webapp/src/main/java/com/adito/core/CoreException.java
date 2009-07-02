package com.adito.core;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

import com.adito.boot.CodedException;


/**
 */
public class CoreException extends CodedException {

	/**
	 * Default bundle name
	 */
    public static final String DEFAULT_BUNDLE = "errors";
    
    //  Private instance variable

	private BundleActionMessage bundleActionMessage;
    private String category;
    
    
    /**
     * Constructor.
     *
     * @param code
     * @param category
     */
    public CoreException(int code, String category) {
        this(code, category, (Throwable)null);
    }
    
    /**
     * Constructor.
     *
     * @param code
     * @param category
     * @param arg0
     */
    public CoreException(int code, String category, String arg0) {
        this(code, category, DEFAULT_BUNDLE, (Throwable)null, arg0);
    }
    
    /**
     * Constructor.
     *
     * @param code code
     * @param category category
     * @param cause
     */
    public CoreException(int code, String category, Throwable cause) {
        this(code, category, DEFAULT_BUNDLE, cause);
    }
    
    /**
     * Constructor.
     *
     * @param code
     * @param category
     * @param bundle
     * @param cause
     */
    public CoreException(int code, String category, String bundle, Throwable cause) {
        this(code, category, bundle, cause, cause == null ? null : cause.getMessage());
    }
    
    /**
     * Constructor.
     *
     * @param code
     * @param category
     * @param bundle
     * @param cause
     * @param arg0
     */
    public CoreException(int code, String category, String bundle, Throwable cause, String arg0) {
        this(code, category, bundle, cause, arg0, null, null, null);
        
    }
    
    /**
     * Constructor.
     *
     * @param code
     * @param category
     * @param bundle
     * @param cause
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public CoreException(int code, String category, String bundle, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
        super(code, "Error code " + category + "/" + code, cause);        
        this.bundleActionMessage = new BundleActionMessage(bundle, "error." + category + "." + code, arg0, arg1, arg2, arg3);
        this.category = category;
    }
	
	/**
     * Get the bundle action message object.
     * 
	 * @return bundle action message
	 */
	public BundleActionMessage getBundleActionMessage(){
		return bundleActionMessage;
	}
    
    /**
     * Get the bundle name
     * 
     * @return bundle name
     */
    public String getBundle() {
        return bundleActionMessage.getBundle();
    }

    /**
     * Get the localised text for this error message.
     * 
     * @param session session
     * @return localised text
     */
    public String getLocalizedMessage(HttpSession session) {
        MessageResources mr = getMessageResources(session, bundleActionMessage.getBundle());
        if(mr == null) {
            return "[Could not locate message resources for bundle "+ bundleActionMessage.getBundle() +"]";
        }
        Locale l = (Locale)session.getAttribute(Globals.LOCALE_KEY);
        l = l == null ? Locale.getDefault() : l;
        String message = mr.getMessage(l, bundleActionMessage.getKey(), bundleActionMessage.getArg0(), bundleActionMessage.getArg1(), bundleActionMessage.getArg2(), bundleActionMessage.getArg3());
        if(message == null) {
            message = super.getMessage();
        }
        return message;
    }
    
    protected MessageResources getMessageResources(HttpSession session, String bundle) {
    	return CoreUtil.getMessageResources(session, bundle);    	
    }
    
    protected MessageResources getMessageResources(String bundle) {
    	return (MessageResources)CoreServlet.getServlet().getServletContext().getAttribute(
            bundle);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getMessage() {
        MessageResources cmr = getMessageResources(getBundle());
        String message = null;
        if(cmr != null) {
            message = cmr.getMessage(bundleActionMessage.getKey(),  bundleActionMessage.getArg0(), bundleActionMessage.getArg1(), bundleActionMessage.getArg2(), bundleActionMessage.getArg3());
        }
        if(message == null) {
            message = super.getMessage();
        }
        return message;
    }

    /**
     * Get the error category
     * 
     * @return error category
     */
    public String getCategory() {
    	return category;
    }
	
}
