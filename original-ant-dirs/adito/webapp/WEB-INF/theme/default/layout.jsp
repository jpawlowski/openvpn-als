<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.adito.boot.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="com.adito.core.CoreServlet" %>
<%@ page import="com.adito.core.CoreScript" %>
<%@ page import="com.adito.core.PanelManager" %>

<%@ page import="com.adito.core.AvailableMenuItem" %>
<%@ page import="com.adito.wizard.AbstractWizardSequence" %>
<%@ page import="com.adito.security.Constants" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.adito.core.Panel" %>
<jsp:directive.page import="com.adito.core.CoreUtil"/>
<jsp:directive.page import="com.adito.core.DefaultPanel"/>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/server/taglibs/core" prefix="core" %>
<%@ taglib uri="/server/taglibs/navigation" prefix="navigation" %>
<%@ taglib uri="/server/taglibs/security" prefix="security" %>
<%@ taglib uri="/server/taglibs/input" prefix="input" %>

<bean:page id="sessionObj" property="session"/>
<bean:page id="requestObj" property="request"/> 

<% try { 
%>
	<%@page import="com.adito.extensions.ExtensionBundle"%>
<%@page import="com.adito.extensions.store.ExtensionStore"%>
<html>
		<bean:page id="sessionObj" property="session"/>
		<bean:page id="requestObj" property="request"/>
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
		<tiles:useAttribute name="displayGlobalWarnings" scope="request" classname="java.lang.String"/> 
		<tiles:useAttribute name="updateAction" scope="request" classname="java.lang.String"/> 
		<tiles:useAttribute name="menuItem" scope="request" classname="java.lang.String" ignore="true"/> 
		<tiles:useAttribute name="rssFeed" scope="request" classname="java.lang.String"/>
	
		<tiles:insert flush="false" attribute="pageHeader">
			<tiles:put name="resourcePrefix" beanName="resourcePrefix"/>
			<tiles:put name="resourceBundle" beanName="resourceBundle"/>
		</tiles:insert>
		<% String onload = (String)request.getAttribute(Constants.REQ_ATTR_EXEC_ON_LOAD); %>
		<body class="<%= pageStyle %>" onload="<%= onload == null ? "" : onload %>" >			
			<core:pageScripts/>
			<% 
			
			for(Iterator i = ExtensionStore.getInstance().getExtensionBundles().iterator(); i.hasNext(); ) {
				ExtensionBundle bundle = (ExtensionBundle)i.next();
			%>
				<tiles:insert flush="false" page="<%= "/WEB-INF/jsp/tiles/bodystart-" + bundle.getId() + ".jspf" %>"/>
			<%
			}
			%>
			<div id="layout_page">
				<% if(!Boolean.TRUE.equals(request.getAttribute(Constants.REQ_ATTR_HIDE_HEADER))) { %>
					<div id="layout_topbar">	
						<div id="component_pageHeader">	
			   				<div id="component_navigationBar">
								<ul>
									<logic:iterate id="rootMenuItem" name="navBar" type="com.adito.core.AvailableMenuItem">			
										<li>
											<input:toolTip textAlign="center" width="120" styleId="<%= rootMenuItem.getMenuItem().getId() + "Link" %>" href="<%= rootMenuItem.getPath() %>" key="<%= "navBar." + rootMenuItem.getMenuItem().getId() %>" bundle="<%= rootMenuItem.getMenuItem().getMessageResourcesKey() %>">
												<div id="<%= "navButton_" + rootMenuItem.getMenuItem().getId() %>" class="<%= rootMenuItem.getMenuItem().getId() + "Image" %>">
													&nbsp;
												</div>
											</input:toolTip>
										</li>
									</logic:iterate>
								</ul>
							</div>		 
						</div>
					</div>
				<% } %>
		       	<div id="layout_center">
		       		<!--  can't do in CSS, anyone any ideas?!? -->
		       		<table cellpadding="0" border="0" cellspacing="0" id="layout_inner">
		       			<tr class="layout_row">	
		       				<td id="layout_leftbar">
		       					<div id="layout_leftbar_inner">
			       				<% 
			       					List leftbarPanels = PanelManager.getInstance().getPanels(Panel.SIDEBAR, request, response, "layout_leftbar_inner", DefaultPanel.MAIN_LAYOUT);
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
								<logic:notEqual name="content" value="">	
									<div id="layout_content">
										<% 
										List contentPanels = PanelManager.getInstance().getPanels(Panel.CONTENT, request, response, "layout_content", DefaultPanel.MAIN_LAYOUT);
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
								</logic:notEqual>
							</td>
							<logic:notEqual name="messageArea" value="false">	
								<td id="layout_rightbar">
									<div id="layout_rightbar_inner">	
										<logic:notEqual name="messageArea" value="">	
											<% try { %>
													<% 
													List rightbarPanels = PanelManager.getInstance().getPanels(Panel.MESSAGES, request, response, "layout_rightbar_inner", DefaultPanel.MAIN_LAYOUT);
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
													System.err.println("<---- End of JSP info error");
													StringWriter sw = new StringWriter();
													infoException.printStackTrace(new PrintWriter(sw));
													%> <pre> <%= sw.toString() %> </pre> <%
													} %>
										</logic:notEqual>
									</div>
								</td>		
							</logic:notEqual>
						</tr>
					</table>
				</div>
			</div>				
			<% 
			
			for(Iterator i = ExtensionStore.getInstance().getExtensionBundles().iterator(); i.hasNext(); ) {
				ExtensionBundle bundle = (ExtensionBundle)i.next();
			%>
				<tiles:insert page="<%= "/WEB-INF/jsp/tiles/bodyend-" + bundle.getId() + ".jspf" %>"/>
			<%
			}
			%>
			<core:pageScripts position="<%= String.valueOf(CoreScript.BEFORE_BODY_END) %>"/>
			<script language="JavaScript">
	// Turn effects on or off
	fx = !opera && <core:getProperty propertyName="ui.specialEffects" userProfile="true"/>;
			</script>
			<div id="debugWindow">
				<pre id="debugConsole">
				</pre>
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