<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" language="java" %>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/server/taglibs/core" prefix="core"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<html>
	<head>
		<title>Redirect</title>
	    <meta http-equiv="Refresh" content="2;url=<%= request.getParameter("redirectURL") %>">

	</head>
</html>
