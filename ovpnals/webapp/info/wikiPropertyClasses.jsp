<%@ page contentType="text/plain;charset=UTF-8" language="java" %><jsp:directive.page import="com.ovpnals.policyframework.PolicyDatabaseFactory"/><jsp:directive.page import="com.ovpnals.policyframework.ResourceType"/><jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="java.util.Collections"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="com.ovpnals.boot.PropertyClassManager"/>
<jsp:directive.page import="com.ovpnals.boot.PropertyClass"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/><%
if(!"true".equals(SystemProperties.get("ovpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
}
%>|  *Name*  |
<%
	List l = new ArrayList(PropertyClassManager.getInstance().getPropertyClasses());
	Collections.sort(l);
	for(Iterator i = l.iterator(); i.hasNext(); ) {
		PropertyClass propertyClass = (PropertyClass)i.next();
%>| <%= propertyClass.getName() %> |
<% 	} %>