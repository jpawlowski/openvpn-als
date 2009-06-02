<%@ page contentType="text/plain;charset=UTF-8" language="java" %><%@page import="java.io.PrintWriter"%><jsp:directive.page import="net.openvpn.als.security.SessionInfo"/><jsp:directive.page import="net.openvpn.als.core.CoreServlet"/><jsp:directive.page import="java.util.Iterator"/><jsp:directive.page import="net.openvpn.als.security.Constants"/><jsp:directive.page import="java.util.Map"/><jsp:directive.page import="java.util.List"/><jsp:directive.page import="net.openvpn.als.boot.PropertyDefinition"/><jsp:directive.page import="net.openvpn.als.boot.DefaultPropertyDefinition"/><jsp:directive.page import="net.openvpn.als.boot.PropertyClass"/><jsp:directive.page import="net.openvpn.als.boot.PropertyClassManager"/><jsp:directive.page import="org.apache.struts.util.MessageResources"/><jsp:directive.page import="net.openvpn.als.core.CoreUtil"/>
<jsp:directive.page import="net.openvpn.als.properties.attributes.AttributeDefinition"/>
<jsp:directive.page import="net.openvpn.als.boot.PropertyDefinitionCategory"/>
<jsp:directive.page import="java.util.Collection"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="net.openvpn.als.boot.SystemProperties"/><% 
	if(!"true".equals(SystemProperties.get("openvpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
	String spaces = "                                                 ";
	for(Iterator j = PropertyClassManager.getInstance().getPropertyClasses().iterator(); j.hasNext(); ) {
		PropertyClass propertyClass = (PropertyClass)j.next();
		boolean headingDone = false;		
		for(Iterator i = propertyClass.getCategories().iterator(); i.hasNext(); ) {
			PropertyDefinitionCategory cat = (PropertyDefinitionCategory)i.next();
			MessageResources mr = cat.getBundle() == null ? null : CoreUtil.getMessageResources(session, cat.getBundle());
			String description = mr == null ? null : mr.getMessage("category." + cat.getId() + ".name");
			if(description != null) { 
				if(!headingDone) { 
					headingDone = true; %>
---+ <%= propertyClass.getName() %>
|  *Name*  |  *Id*  |  *Image*  |<%
				} 		
				int indent=0;
				PropertyDefinitionCategory ncat = cat;
				while(true) {
					out.println(spaces.substring(0, indent));
					out.println(ncat.getId());
					Collection c = ncat.getCategories();
					if(c.size() != 0) {
						indent += 4;
						ncat = (PropertyDefinitionCategory)c.iterator().next();
					}
					else {
						if(ncat.getParent() == null) {
						}
						else {
							ArrayList l = new ArrayList(ncat.getParent().getCategories());
							int idx = l.indexOf(ncat);
							if(idx == l.size() - 1) {
								ncat = null; 
							}
							else {
								ncat = (PropertyDefinitionCategory)l.get(idx + 1);
							}
						}
					}
					
				}
		 	}  
		 } 
 	} %>