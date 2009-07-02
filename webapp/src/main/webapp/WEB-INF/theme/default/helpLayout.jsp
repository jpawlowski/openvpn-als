<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.adito.boot.Util" %>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/server/taglibs/core" prefix="core" %>
<%@ taglib uri="/server/taglibs/navigation" prefix="navigation" %>
<%@ taglib uri="/server/taglibs/security" prefix="security" %>

<bean:page id="sessionObj" property="session"/>
<bean:page id="requestObj" property="request"/> 

<% try { 
%>

<html>
	<bean:page id="sessionObj" property="session"/>
	<bean:page id="requestObj" property="request"/>

	<tiles:insert flush="false" attribute="pageHeader">
		<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
		<tiles:put name="resourceBundle" beanName="resourceBundle"/>
	</tiles:insert>
	
	<body>	
		<div id="help_page">
			<div id="help_banner_image">
			</div>
			<div id="help_banner_text">
				<bean:message key="help.bannerText" bundle="navigation"/>
			</div>			
			<div id="help_content">
			<% try { %>
					<tiles:insert attribute="content" flush="false"/>

				<% } catch(Throwable contentException) {
				System.err.println("----> Error occured processing JSP content");
				contentException.printStackTrace();
			    // May contain sensitive information
				// Util.dumpSessionAttributes(session);							
				// Util.dumpRequestAttributes(request);
				// Util.dumpRequestParameters(request);
				System.err.println("<---- End of JSP content error");
				StringWriter sw = new StringWriter();
				contentException.printStackTrace(new PrintWriter(sw));
				%> <pre> <%= sw.toString() %> </pre> <%
				} %>
			</div>
		</div>
	</body>
</html>

<% } catch(Throwable t) {
	System.err.println("----> Error occured processing JSP");
	t.printStackTrace();
	System.err.println("<---- End of JSP error");
	StringWriter sw = new StringWriter();
	t.printStackTrace(new PrintWriter(sw));
	%> <pre> <%= sw.toString() %> </pre> <%
	} %>