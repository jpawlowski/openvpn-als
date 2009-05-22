<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="com.ovpnals.security.SessionInfo"/>
<jsp:directive.page import="com.ovpnals.core.CoreServlet"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.ovpnals.security.Constants"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="com.ovpnals.agent.DefaultAgentManager"/>
<jsp:directive.page import="java.util.Set"/>
<jsp:directive.page import="com.ovpnals.agent.AgentTunnel"/>
<jsp:directive.page import="com.maverick.multiplex.Channel"/>
<jsp:directive.page import="com.ovpnals.security.LogonControllerFactory"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("ovpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
%>
		<h1>Sessions</h1>
		<table border="1">
			<thead>
				<tr>
					<td><b>Id</b></td>
					<td><b>User</b></td>
					<td><b>User Agent</b></td>
					<td><b>Type</b></td>
					<td><b>Address</b></td>
					<td><b>Timeout Blocks</b></td>
					<td><b>Tickets</b></td>
					<td><b>Agents / Embedded Clients</b></td>
					<td><b>More</b></td>
				</tr>
			</thead>
			<tbody>
		<%
		for(Iterator i = LogonControllerFactory.getInstance().getActiveSessions().values().iterator(); i.hasNext(); ) {
			SessionInfo si = (SessionInfo)i.next();		
			try {
		%>
			<tr>
				<td><%= String.valueOf(si.getId()) %></td>					
				<td><%= si.getUser().getPrincipalName() %></td>							
				<td><%= si.getUserAgent() %></td>			
				<td><%= si.getType() == SessionInfo.UI ? "UI" : ( si.getType() == SessionInfo.AGENT ? "Agent" : "WebDAV" )  %></td>				
				<td><%= String.valueOf(si.getAddress()) %></td>	
				<% if(si.getHttpSession() != null) { %>
					<td>
					<%	Map stob = (Map)si.getHttpSession().getAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
						if(stob != null) {
							for(Iterator j = stob.entrySet().iterator(); j.hasNext(); ) {
								Map.Entry entry = (Map.Entry)j.next();
								%><%= (String)entry.getKey() %> (<%= (String)entry.getValue() %>)<br/><%
							}
						}
					 %>
					 </td>
					 <td>
					 	VPN Auth:<%= si.getHttpSession().getAttribute(Constants.VPN_AUTHORIZATION_TICKET) %><br/>
					 	Logon:<%= si.getHttpSession().getAttribute(Constants.LOGON_TICKET) %><br/>
					 	Web Folder:<%= si.getHttpSession().getAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET) %><br/>
					 	Domain:<%= si.getHttpSession().getAttribute(Constants.DOMAIN_LOGON_TICKET) %><br/>
					 </td>
				<% } else { %>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				<% }  %>
				 <td>										 
				 	<%	
				 	AgentTunnel agent = DefaultAgentManager.getInstance().getAgentBySession(si); %>				 	
			 	 	<% 
				 		if(agent != null) {
				 	%><%=  agent.getType() %> (<%= agent.getActiveChannelCount() %> active channels<br/><%
				 			Channel[] channels = agent.getActiveChannels();
				 			if(channels != null) {
				 				for(int ch = 0 ; ch < channels.length; ch++) {
				 	%>
				 	<% %>
				 	<%
				 				}
				 			}
				 	 	}
				 	  %>
				 </td>
				 <td>
				 	<a href="<%= "sessionInfo.jsp?ticket=" + si.getLogonTicket() %>"/>Info</a>
				 </td>				 
			</tr>
		<%
			}
			catch(Throwable t) {
				t.printStackTrace();
			}
		}
		 %>
		 	</tbody>
		</table>
	</body>
</html>