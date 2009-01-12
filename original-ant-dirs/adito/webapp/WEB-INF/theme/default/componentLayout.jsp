<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.io.StringWriter"%>
<%@ page import="java.io.PrintWriter"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<bean:page id="sessionObj" property="session" />
<bean:page id="requestObj" property="request" />

<% try { %>
<tiles:insert flush="false" attribute="content" />

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
	%>
<pre> <%= sw.toString() %> </pre>
<%
	} %>
