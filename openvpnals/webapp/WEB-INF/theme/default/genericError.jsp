<%@ page import="net.openvpn.als.core.CoreScript" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<jsp:directive.page import="net.openvpn.als.core.CoreUtil"/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/server/taglibs/core" prefix="core" %>
<%@ taglib uri="/server/taglibs/navigation" prefix="navigation" %>

<html>
	<head>
		<title><bean:message key='http.error.pageTitle' bundle="navigation" arg0="<%= String.valueOf(pageContext.getErrorData().getStatusCode()) %>"/></title>
		<link type="text/css" rel="stylesheet" href='<core:themePath/>/style.jsp'/>		
		<core:pageScripts position="<%= String.valueOf(CoreScript.PAGE_HEADER) %>"/>
	<% if(pageContext.getErrorData() != null && pageContext.getErrorData().getStatusCode() == 403) { %>
		<meta http-equiv="refresh" content="5; URL=/showHome.do">
	<% } %>
	</head>
	<body>
		<div id="popup_content_centered_outer">
			<div id="page_error"> 
				<div class="titled_dialog_content">
					<div>
						<table class="titled_dialog_table">
							<thead>
								<tr>
									<td colspan="2">                         
										<bean:message key='<%= "http.error." + pageContext.getErrorData().getStatusCode() + ".title" %>' bundle="navigation"/>
									</td>
								</tr>                          
							</thead>
							<tbody>
								<tr>
									<td class="icon">
										<bean:define id="themePath"><core:themePath/></bean:define>
										<img src="<%= themePath + "/images/dialog/error.gif" %>"/>
									</td>
									<td class="text">
										<table class="dialog_form_table">
											<tbody>
												<tr>
													<td>
														<div class="confirmMessage">														
															<bean:message key='<%= "http.error." + pageContext.getErrorData().getStatusCode() + ".description" %>' bundle="navigation"/>
														</div>
													</td>
												</tr>       
											</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>