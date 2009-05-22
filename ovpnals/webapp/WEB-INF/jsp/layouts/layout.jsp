<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/server/taglibs/core" prefix="core" %>
<%@ taglib uri="/server/taglibs/navigation" prefix="navigation" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<tiles:useAttribute name="menuItem" scope="request" classname="java.lang.String" ignore="true"/> 
<navigation:menu name="<%= menuItem %>"/>

<tiles:useAttribute name="resourcePrefix" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="resourceBundle" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="infoImage" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="info" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="messageArea" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="header" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="footer" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="content" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="actionLink" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="noBodyStyle" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="pageStyle" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="pageHeader" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="displayGlobalWarnings" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="profileSelector" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="menu" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="updateAction" scope="request" classname="java.lang.String"/> 
<tiles:useAttribute name="rssFeed" scope="request" classname="java.lang.String"/>
<bean:define id="themeLayout">/WEB-INF/<core:themePath/>/layout.jsp</bean:define>
<tiles:insert flush="false" beanName="themeLayout">
	<tiles:put name="pageHeader" beanName="pageHeader"/>
	<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
	<tiles:put name="resourceBundle" beanName="resourceBundle"/>
	<tiles:put name="infoImage" beanName="infoImage"/>
	<tiles:put name="info" beanName="info"/>
	<tiles:put name="messageArea" beanName="messageArea"/>
	<tiles:put name="header" beanName="header"/>
	<tiles:put name="footer" beanName="footer"/>
	<tiles:put name="content" beanName="content"/>
	<tiles:put name="actionLink" beanName="actionLink"/>
	<tiles:put name="noBodyStyle" beanName="noBodyStyle"/>
	<tiles:put name="pageStyle" beanName="pageStyle"/>
	<tiles:put name="displayGlobalWarnings" beanName="displayGlobalWarnings"/>		
	<tiles:put name="profileSelector" beanName="profileSelector"/>			
	<tiles:put name="menu" beanName="menu"/>			
	<tiles:put name="actionLink" beanName="actionLink"/>			
	<tiles:put name="menuItem" beanName="menuItem"/>			
	<tiles:put name="updateAction" beanName="updateAction"/>			
	<tiles:put name="rssFeed" beanName="rssFeed"/>			
</tiles:insert>