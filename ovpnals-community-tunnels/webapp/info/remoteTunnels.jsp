<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="com.ovpnals.tunnels.agent.RemoteTunnelManagerFactory"/>
<jsp:directive.page import="com.ovpnals.tunnels.agent.RemoteTunnel"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/>

<html>
	<body>
	<%
		if(!"true".equals(SystemProperties.get("ovpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
			}
	%>
		<h1>Remote Tunnels</h1>
		<h2><a href="remoteTunnels.jsp">Refresh</a></h2>
		<table border="1">
			<thead>
				<tr>
					<td><b>Port</b></td>
					<td><b>Interface</b></td>
					<td><b>Agent</b></td>
					<td><b>Stop</b></td>
				</tr>
			</thead>
			<tbody>
		<%
			for(Iterator i = RemoteTunnelManagerFactory.getInstance().getRemoteTunnels().iterator(); i.hasNext(); ) {
			RemoteTunnel rt = (RemoteTunnel)i.next();
		%>
			<tr>
				<td><%= rt.getTunnel().getSourcePort() %></td>					
				<td><%= rt.getTunnel().getSourceInterface() == null || rt.getTunnel().getSourceInterface().equals("") ? "All local" : rt.getTunnel().getSourceInterface() %></td>							
				<td><%= rt.getAgent().getId() %></td>			
				<%
				if("stop".equals(request.getParameter("action") ) &&  
					rt.getTunnel().getSourcePort() == Integer.parseInt(request.getParameter("sourcePort")) &&
						(
						( rt.getTunnel().getSourceInterface() == null && request.getParameter("sourceInterface").equals("") )
						
						 ||
							rt.getTunnel().getSourceInterface().equals(request.getParameter("sourceInterface") ) 
				        ) ) {
					rt.stopListener();
						%>
					<td>Stopped</td>			
					<%
				}
				else {
				%>				
					<td><a href="<%= "remoteTunnels.jsp?action=stop&sourcePort=" + rt.getTunnel().getSourcePort() + "&sourceInterface=" + ( rt.getTunnel().getSourceInterface() == null ? "" : rt.getTunnel().getSourceInterface() ) %>">X</a></td>			
				<%
				}
				 %>
			</tr>
		<%
		}
		 %>
		 	</tbody>
		</table>
	</body>
</html>