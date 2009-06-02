<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="net.openvpn.als.security.SessionInfo"/>
<jsp:directive.page import="net.openvpn.als.core.CoreServlet"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="net.openvpn.als.security.Constants"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.net.URL"/>
<jsp:directive.page import="net.openvpn.als.boot.ContextHolder"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.Enumeration"/>
<jsp:directive.page import="net.openvpn.als.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("openvpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
%>
		<h1>System Properties</h1>
		<pre>
		<%
		for(Enumeration e = System.getProperties().keys() ; e.hasMoreElements(); ) {
			String key = (String)e.nextElement();
			out.println(key + "=" + SystemProperties.get(key));
		}
		 %>
		</pre>
		<h1>Classpath</h1>
		<pre>
		<%
		URL[] u = ContextHolder.getContext().getContextLoaderClassPath();
		for(int i = 0 ; i < u.length; i++) {
			out.println(u[i].toExternalForm());
		}
		 %>
		</pre>
		<h1>Resource Bases</h1>
		<pre>
		<%
		Collection urls =  ContextHolder.getContext().getResourceBases();
		for(Iterator i = urls.iterator(); i.hasNext(); ) {
			out.println(((URL)i.next()).toExternalForm());
		}
		 %>
		</pre>
	</body>
</html>