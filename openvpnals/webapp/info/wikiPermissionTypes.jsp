<%@ page contentType="text/plain;charset=UTF-8" language="java" %><jsp:directive.page import="net.openvpn.als.policyframework.PolicyDatabaseFactory"/><jsp:directive.page import="net.openvpn.als.policyframework.ResourceType"/><jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="java.util.Collections"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="net.openvpn.als.boot.PropertyClassManager"/>
<jsp:directive.page import="net.openvpn.als.boot.PropertyClass"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="net.openvpn.als.policyframework.Permission"/>
<jsp:directive.page import="org.apache.struts.util.MessageResources"/>
<jsp:directive.page import="net.openvpn.als.core.CoreUtil"/>
<jsp:directive.page import="net.openvpn.als.boot.SystemProperties"/><%
if(!"true".equals(SystemProperties.get("openvpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
}
%>|  *Id*  |  *Name*  |  *Bundle*  |
<%
	ArrayList p = new ArrayList();
	List l = PolicyDatabaseFactory.getInstance().getResourceTypes(null);
	Collections.sort(l);
	String permClass = null;
	for(Iterator i = l.iterator(); i.hasNext(); ) {
		ResourceType resourceType = (ResourceType)i.next();	
		for(Iterator j = resourceType.getPermissions().iterator(); j.hasNext(); ) {
			Permission perm = (Permission)j.next();
			if(!p.contains(perm)) {
				p.add(perm);
			}
		}
	}
	for(Iterator i = p.iterator(); i.hasNext(); ) {
		Permission permission = (Permission)i.next();
		MessageResources mr = CoreUtil.getMessageResources(session, permission.getBundle());
%>| <%= String.valueOf(permission.getId()) %> | <%= mr.getMessage("permission." + permission.getId() + ".title") %> | <%= String.valueOf(permission.getBundle()) %> |
<% 	} %>