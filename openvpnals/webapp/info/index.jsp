<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:directive.page import="net.openvpn.als.boot.SystemProperties"/>
<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("openvpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
%>
		<h1>Information Pages</h1>
		<ul>
			<li><a href="definitions.jsp">Property Definitions (XML)</a></li>
			<li><a href="launchSessions.jsp">Active Launch Sessions</a></li>
			<li><a href="remoteTunnels.jsp">Active Remote Tunnels</a></li>
			<li><a href="sessions.jsp">Active User Sessions</a></li>
			<li><a href="systemProperties.jsp">System Properties</a></li>
			<li><a href="threads.jsp">Threads</a></li>
			<li><a href="ipRestrictions.jsp">IP Restrictions</a></li>
			<li><a href="webForwardCache.jsp">Web Forward Cache</a></li>
			<li><a href="wikiEventCodes.jsp">Event Codes (Wiki Table)</a></li>
			<li><a href="wikiCategories.jsp">Property Categories (Wiki Table)</a></li>
			<li><a href="wikiDefinitions.jsp">Property Definitions (Wiki Table)</a></li>
			<li><a href="wikiResourceTypes.jsp">Resource Types (Wiki Table)</a></li>
			<li><a href="wikiPropertyClasses.jsp">Property Classes (Wiki Table)</a></li>
			<li><a href="wikiPermissionTypes.jsp">Permission Types (Wiki Table)</a></li>
		</ul>
	</body>
</html>
