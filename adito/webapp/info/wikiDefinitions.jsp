<%@ page contentType="text/plain;charset=UTF-8" language="java" %><%@page import="java.io.PrintWriter"%><jsp:directive.page import="com.adito.security.SessionInfo"/><jsp:directive.page import="com.adito.core.CoreServlet"/><jsp:directive.page import="java.util.Iterator"/><jsp:directive.page import="com.adito.security.Constants"/><jsp:directive.page import="java.util.Map"/><jsp:directive.page import="java.util.List"/><jsp:directive.page import="com.adito.boot.PropertyDefinition"/><jsp:directive.page import="com.adito.boot.DefaultPropertyDefinition"/><jsp:directive.page import="com.adito.boot.PropertyClass"/><jsp:directive.page import="com.adito.boot.PropertyClassManager"/><jsp:directive.page import="org.apache.struts.util.MessageResources"/><jsp:directive.page import="com.adito.core.CoreUtil"/>
<jsp:directive.page import="com.adito.properties.attributes.AttributeDefinition"/>
<jsp:directive.page import="com.adito.boot.SystemProperties"/><% 	if(!"true".equals(SystemProperties.get("adito.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
	for(Iterator j = PropertyClassManager.getInstance().getPropertyClasses().iterator(); j.hasNext(); ) {
		PropertyClass propertyClass = (PropertyClass)j.next();
		boolean headingDone = false;
		for(Iterator i = propertyClass.getDefinitions().iterator(); i.hasNext(); ) {
			DefaultPropertyDefinition def = (DefaultPropertyDefinition)i.next();
			MessageResources mr = def.getMessageResourcesKey() == null ? null :  CoreUtil.getMessageResources(session, def.getMessageResourcesKey());
			String description = def instanceof AttributeDefinition ?  (  mr == null ? null : mr.getMessage("attribute." + def.getName() + ".title") ) : (  mr == null ? null : mr.getMessage(def.getName() + ".name") );
			// If there is no description it is probably user defined
			if(description != null) {
				if(!headingDone) { 
					headingDone = true; %>
---+ <%= propertyClass.getName() %>
|  *Name*  |  *Description*  |  *Bundle*  |  *Type*  | *Category* |  *Default Value*  |  *Order*  |<%
				} 			
%>
| <%= def.getName() %> | <%= description == null ? " " : description %> | <%= def.getMessageResourcesKey() == null ? " " : def.getMessageResourcesKey() %> | <%= def.getType() %> | <%= String.valueOf(def.getCategory()) %> | <%= def.getDefaultValue() == null ? " " : def.getDefaultValue() %> | <%= String.valueOf(def.getSortOrder()) %> | <% }  } %>
<% } %>