<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/server/taglibs/core" prefix="core" %>
<%@ taglib uri="/server/taglibs/navigation" prefix="navigation" %>
<bean:define id="genericError">/WEB-INF<core:themePath/>/genericError.jsp</bean:define>
<tiles:insert flush="false" beanName="genericError"/>
<% response.setStatus(pageContext.getErrorData().getStatusCode()); %>