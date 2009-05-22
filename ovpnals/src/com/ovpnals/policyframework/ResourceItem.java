/*
 */
package com.ovpnals.policyframework;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.TableItem;

/**
 * @param <T> 
 */
public class ResourceItem<T extends Resource> implements TableItem {

    private T resource;
    private List<Policy> policies;
    private String checked;
    private String lastLaunchedPolicy;

    public ResourceItem(T resource) {
        this(resource, Collections.<Policy>emptyList());
    }
    
    public ResourceItem(T resource, List<Policy> policies) {
        this.resource = resource;
        this.policies = policies;
    }
    
    public String getLastLaunchedPolicy() {
    	return lastLaunchedPolicy;
    }
    
    public boolean getMultiplePolicies() {
        return policies.size() > 1;
    }
    
    public String getFirstPolicyName() {
        return policies.size() > 0 ? policies.get(0).getResourceName() : ""; 
    }
    
    public List<Policy> getPolicies() {
        return policies;
    }

    public T getResource() {
        return resource;
    }

    public Object getColumnValue(int col) {
        return resource.getResourceDisplayName() == null ? "<Unknown>" : resource.getResourceDisplayName();
    }

    public void setChecked(String checked) {
        this.checked = checked;        
    }

    public String getChecked() {
        return checked;        
    }

    public String getSmallIconPath(HttpServletRequest request) {
        return getThemePath(request) + "/images/actions/resource.gif";
    }

    public String getLargeIconPath(HttpServletRequest request) {
        return getThemePath(request) + "/images/actions/largeResource.gif";
    }

    public String getLargeIconAdditionalIcon(HttpServletRequest request) {
        return "";
    }

    public String getLargeIconAdditionalText(HttpServletRequest request) {
        return "";
    }

    public String getLink(int policy, HttpServletRequest request) {
    	return getLink(policy, null, request);
    }

    public String getLink(int policy, String referer, HttpServletRequest request) {
    	return "#";
    }

    public String getOnClick(int policy, HttpServletRequest request) {
    	return "";
    }
    
    protected String getLaunchLink(int policy, String referer, HttpServletRequest request, String requestPath) {
        SessionInfo sessionInfo = getSessionInfo(request);
        if (sessionInfo != null) {
            String returnTo = Util.urlEncode(Util.isNullOrTrimmedBlank(referer) ? CoreUtil.getRealRequestURI(request) : referer);
            return requestPath + "?resourceId=" + getResource().getResourceId() + "&policy=" + policy + "&returnTo=" + returnTo;
        }
        return "#";
    }

    protected String getThemePath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession());
    }
    
    protected SessionInfo getSessionInfo(HttpServletRequest request) {
        return LogonControllerFactory.getInstance().getSessionInfo(request);
    }

    @Override
    public String toString() {
        return getResource().toString();
    }
}