<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="com.adito.security.SessionInfo"/>
<jsp:directive.page import="com.adito.core.CoreServlet"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.adito.security.Constants"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.net.URL"/>
<jsp:directive.page import="com.adito.boot.ContextHolder"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.Enumeration"/>
<%@ page import="com.adito.security.SystemDatabaseFactory" %>
<%@ page import="com.adito.security.IpRestriction" %>
<jsp:directive.page import="com.adito.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("adito.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
	if("del".equals(request.getParameter("op"))) {
		SystemDatabaseFactory.getInstance().removeIpRestriction(Integer.parseInt(request.getParameter("id")));
	}
%>
		<h1>IP Restrictions</h1>
		<table border="1">
			<thead>
				<tr>
					<td><b>Address</b></td>
					<td><b>Access</b></td>
					<td><b>Delete</b></td>
				</tr>
			</thead>
			<tbody>
		<%
		IpRestriction[] r = SystemDatabaseFactory.getInstance().getIpRestrictions();
		for(int i = 0 ; i < r.length; i++) {
		%>
			<tr>
				<td><%= r[i].getAddress() %></td>					
				<td><%= r[i].getAllowed() ? "Allow" : "Deny" %></td>							
				<td><a href="<%= "?op=del&id=" + r[i].getID() %>">X</a></td>
			</tr>
		<%
		}
		 %>
		 	</tbody>
		</table>
	</body>
</html>