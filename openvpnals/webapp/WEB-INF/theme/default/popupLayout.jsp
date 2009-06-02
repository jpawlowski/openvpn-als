<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<jsp:directive.page import="net.openvpn.als.core.PanelManager"/>
<jsp:directive.page import="net.openvpn.als.core.Panel"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="net.openvpn.als.boot.Util"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="net.openvpn.als.core.DefaultPanel"/>
<jsp:directive.page import="javax.swing.ActionMap"/>
<jsp:directive.page import="org.apache.struts.action.ActionMapping"/>
<jsp:directive.page import="org.apache.struts.Globals"/>

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

<%@page import="net.openvpn.als.core.CoreScript"%>
<html>
	<bean:page id="sessionObj" property="session"/>
	<bean:page id="requestObj" property="request"/>
	<tiles:useAttribute ignore="true" name="resourcePrefix" scope="request" classname="java.lang.String"/> 
	<tiles:useAttribute ignore="true" name="resourceBundle" scope="request" classname="java.lang.String"/> 
	<tiles:useAttribute ignore="true" name="rssFeed" scope="request" classname="java.lang.String"/> 
	<tiles:useAttribute name="content" scope="request" classname="java.lang.String"/> 
	<tiles:useAttribute ignore="true" name="actionLink" scope="request" classname="java.lang.String"/> 	
	
	<tiles:insert flush="false" attribute="pageHeader">
		<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
		<tiles:put name="resourceBundle" beanName="resourceBundle"/>
	</tiles:insert>
	
	<body id="<%= "mapping_" + ((ActionMapping)request.getAttribute(Globals.MAPPING_KEY)).getPath().replace("/","").replace("\\.do", "") %>">
		<core:pageScripts/>
		<div id="layout_page" class="popup">
			<div id="layout_topbar">	
				<div id="component_smallPageHeader">		
				</div>
			</div>
       		<div id="layout_center">
	       		<table cellpadding="0" border="0" cellspacing="0" id="layout_inner">
	       			<tr class="layout_row">
	       				<td id="layout_leftbar">
			       			<div id="layout_leftbar_inner">
		       				<% 
		       					List leftbarPanels = PanelManager.getInstance().getPanels(Panel.SIDEBAR, request, response, "layout_leftbar_inner", DefaultPanel.POPUP_LAYOUT);
								for(Iterator i = leftbarPanels.iterator(); i.hasNext(); ) {
									Panel p = (Panel)i.next();
									try { %>									
											<tiles:insert flush="false" page="<%= p.getTileIncludePath(pageContext) %>">																										
												<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
												<tiles:put name="resourceBundle" beanName="resourceBundle"/>
												<tiles:put name="displayGlobalWarnings" beanName="displayGlobalWarnings"/>					
												<tiles:put name="actionLink" beanName="actionLink"/>
												<tiles:put name="updateAction" beanName="updateAction"/>
												<tiles:put name="infoImage" beanName="infoImage"/>
												<tiles:put name="info" beanName="info"/>
												<tiles:put name="updateAction" beanName="updateAction"/>
												<tiles:put name="infoImage" beanName="infoImage"/>
												<tiles:put name="info" beanName="info"/>
												<tiles:put name="layout" value="main"/>
											</tiles:insert>
											
										<%
									} catch(Throwable headerException) {
										System.err.println("----> Error occured processing JSP header");
										headerException.printStackTrace();	
										System.err.println("<---- End of JSP header error");
										StringWriter sw = new StringWriter();
										headerException.printStackTrace(new PrintWriter(sw));
										%> <pre> <%= sw.toString() %> </pre> <%
									} 
								}
								%>
							</div>
						</td>
						<td id="layout_main">	
							<div id="layout_content">	
								<% 
								List contentPanels = PanelManager.getInstance().getPanels(Panel.CONTENT, request, response, DefaultPanel.POPUP_LAYOUT);
								for(Iterator i = contentPanels.iterator(); i.hasNext(); ) {
									Panel p = (Panel)i.next();
									try { %>									
										<tiles:insert flush="false" page="<%= p.getTileIncludePath(pageContext) %>">
												<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
												<tiles:put name="resourceBundle" beanName="resourceBundle"/>
												<tiles:put name="displayGlobalWarnings" beanName="displayGlobalWarnings"/>					
												<tiles:put name="actionLink" beanName="actionLink"/>
												<tiles:put name="updateAction" beanName="updateAction"/>
												<tiles:put name="infoImage" beanName="infoImage"/>
												<tiles:put name="info" beanName="info"/>
												<tiles:put name="updateAction" beanName="updateAction"/>
												<tiles:put name="infoImage" beanName="infoImage"/>
												<tiles:put name="info" beanName="info"/>
												<tiles:put name="layout" value="main"/>
										</tiles:insert>
								<%  } catch(Throwable headerException) {
										System.err.println("----> Error occured processing JSP header");
										headerException.printStackTrace();	
										System.err.println("<---- End of JSP header error");
										StringWriter sw = new StringWriter();
										headerException.printStackTrace(new PrintWriter(sw));
										%> <pre> <%= sw.toString() %> </pre> <%
									} 
								}
								%>
							</div>
						</td>
						<td id="layout_rightbar">	
							<div id="layout_rightbar_inner">	
								<logic:notEqual name="messageArea" value="">	
									<% try { %>
											<% 
											List rightbarPanels = PanelManager.getInstance().getPanels(Panel.MESSAGES, request, response, "layout_rightbar_inner", DefaultPanel.POPUP_LAYOUT);
											for(Iterator i = rightbarPanels.iterator(); i.hasNext(); ) {
												Panel p = (Panel)i.next(); 
												String path = p.getTileIncludePath(pageContext);
											%>
												<tiles:insert flush="false" page="<%= path %>">
													<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
													<tiles:put name="resourceBundle" beanName="resourceBundle"/>
													<tiles:put name="displayGlobalWarnings" beanName="displayGlobalWarnings"/>					
													<tiles:put name="actionLink" beanName="actionLink"/>
													<tiles:put name="updateAction" beanName="updateAction"/>
													<tiles:put name="infoImage" beanName="infoImage"/>
													<tiles:put name="info" beanName="info"/>
													<tiles:put name="updateAction" beanName="updateAction"/>
													<tiles:put name="infoImage" beanName="infoImage"/>
													<tiles:put name="info" beanName="info"/>
													<tiles:put name="layout" value="main"/>
												</tiles:insert>	
											<% 
											}
										} catch(Throwable infoException) {
											System.err.println("----> Error occured processing JSP info");
											infoException.printStackTrace();
											// May contain sensitive information
											// Util.dumpSessionAttributes(session);							
											// Util.dumpRequestAttributes(request);
											// Util.dumpRequestParameters(request);
											System.err.println("<---- End of JSP info error");
											StringWriter sw = new StringWriter();
											infoException.printStackTrace(new PrintWriter(sw));
											%> <pre> <%= sw.toString() %> </pre> <%
											} %>
								</logic:notEqual>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<core:pageScripts position="<%= String.valueOf(CoreScript.BEFORE_BODY_END) %>"/>
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