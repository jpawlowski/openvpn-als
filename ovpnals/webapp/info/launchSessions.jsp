<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.ovpnals.policyframework.LaunchSessionFactory"/>
<jsp:directive.page import="com.ovpnals.policyframework.LaunchSession"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("ovpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
%>
		<h1>Launch Sessions</h1>
		<table border="1">
			<thead>
				<tr>
					<td><b>Id</b></td>
					<td><b>Session</b></td>
					<td><b>User</b></td>
					<td><b>Resource</b></td>
					<td><b>Policy</b></td>
				</tr>
			</thead>
			<tbody>
		<%
		for(Iterator i = LaunchSessionFactory.getInstance().getLaunchSession().iterator(); i.hasNext(); ) {
			LaunchSession si = (LaunchSession)i.next();		
		%>
			<tr>
				<td><%= si.getId() %></td>					
				<td><%= si.getSession().getHttpSession().getId() %></td>							
				<td><%= si.getSession().getUser().getPrincipalName() %></td>							
				<td><%= si.getResource() == null ? "None" : ( si.getResource().getResourceName() + " [" + si.getResource().getResourceType().getResourceTypeId() + "]" ) %></td>			
				<td><%= si.getPolicy() == null ? "None" : si.getPolicy().getResourceName() %></td>
			</tr>
		<%
		}
		 %>
		 	</tbody>
		</table>
	</body>
</html>