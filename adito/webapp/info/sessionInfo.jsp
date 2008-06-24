<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="com.adito.security.SystemDatabaseFactory" %>
<%@ page import="com.adito.security.IpRestriction" %>
<jsp:directive.page import="com.adito.security.SessionInfo"/>
<jsp:directive.page import="com.adito.core.CoreServlet"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.adito.security.Constants"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.net.URL"/>
<jsp:directive.page import="com.adito.boot.ContextHolder"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.Enumeration"/>
<jsp:directive.page import="com.adito.security.LogonControllerFactory"/>
<jsp:directive.page import="org.apache.commons.cache.CacheStat"/>
<jsp:directive.page import="org.apache.commons.cache.SimpleCache"/>
<jsp:directive.page import="com.adito.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("adito.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
	SessionInfo si = LogonControllerFactory.getInstance().getSessionInfo(request.getParameter("ticket"));
	if(si == null) {
	%>
		<h1>Invalid session</h1>
	<% } else { 
		SimpleCache cache = si.getHttpSession() == null ? null : (SimpleCache)si.getHttpSession().getAttribute(Constants.ATTR_CACHE);
		if(cache != null && request.getParameter("clearCache") != null) {
			cache.clear();
		}
	%>
		<h1>Session</h1>
		<table cellpadding="0">
			<tr>
				<td>ID:</td>
				<td><%= si.getId() %></td>
			</tr>
			<tr>
				<td>Ticket:</td>
				<td><%= si.getLogonTicket() %></td>
			</tr>
			<tr>
				<td>Navigation context:</td>
				<td><%= si.getNavigationContext() %></td>
			</tr>
			<tr>
				<td>Realm:</td>
				<td><%= si.getRealm().getResourceName() + " (" + si.getRealmId() + ")" %></td>
			</tr>
			<tr>
				<td>Type:</td>
				<td><%= si.getType() %></td>
			</tr>
			<tr>
				<td>User Agent:</td>
				<td><%= si.getUserAgent() %></td>
			</tr>
			<tr>
				<td>Address:</td>
				<td><%= si.getAddress() %></td>
			</tr>
		</table>
		<% if(cache != null) { %>
		<h1>Web Forward Cache</h1>		
		<h4>Actions</h4>
		<a href="<%= "?clearCache&ticket=" + si.getLogonTicket() %>">Clear</a>
		</h4>
		<table cellpadding="0">
			<tr>
				<td>Current capacity:</td>
				<td><%= cache.getStat(CacheStat.CUR_CAPACITY)  %></td>
			</tr>
			<tr>
				<td>Retrieves requested:</td>
				<td><%= cache.getStat(CacheStat.NUM_RETRIEVE_REQUESTED)  %></td>
			</tr>
			<tr>
				<td>Retrieves found:</td>
				<td><%= cache.getStat(CacheStat.NUM_RETRIEVE_FOUND)  %></td>
			</tr>
			<tr>
				<td>Retrieves not found:</td>
				<td><%= cache.getStat(CacheStat.NUM_RETRIEVE_NOT_FOUND)  %></td>
			</tr>
			<tr>
				<td>Store requests:</td>
				<td><%= cache.getStat(CacheStat.NUM_STORE_REQUESTED)  %></td>
			</tr>
			<tr>
				<td>Stored:</td>
				<td><%= cache.getStat(CacheStat.NUM_STORE_STORED)  %></td>
			</tr>
			<tr>
				<td>Not stored:</td>
				<td><%= cache.getStat(CacheStat.NUM_STORE_NOT_STORED)  %></td>
			</tr>
		</table>
		<% } 
		}%>
	</body>
</html>