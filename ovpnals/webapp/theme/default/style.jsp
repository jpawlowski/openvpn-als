<%@ page contentType="text/css;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="com.ovpnals.core.CoreServlet" %>
<jsp:directive.page import="com.ovpnals.core.BrowserChecker"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/server/taglibs/core" prefix="core"%>
<%@ include file="layoutStyles.css" %>

<%@page import="com.ovpnals.extensions.store.ExtensionStore"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.ovpnals.extensions.ExtensionBundle"%>

<core:browserCheck browser="<%= BrowserChecker.BROWSER_IE %>" version="*">
	<%@ include file="iePre.css" %>
</core:browserCheck>

<%@ include file="elementStyles.css" %>
<%@ include file="buttonStyles.css" %>
<%@ include file="helpStyles.css" %>
<%@ include file="componentStyles.css" %>
<%@ include file="pagerStyles.css" %>
<%@ include file="dialogStyles.css" %>
<%@ include file="tableStyles.css" %>
<%@ include file="formStyles.css" %>
<%@ include file="tabStyles.css" %>
<%@ include file="ajaxStyles.css" %>
<%@ include file="actionStyles.css" %>
<%@ include file="pageStyles.css" %>
<%@ include file="popupStyles.css" %>
<%@ include file="wizardStyles.css" %>
<%@ include file="modalbox.css" %>

<%
response.setHeader("Cache-Control","max-age=300"); //HTTP 1.1
%>
<core:browserCheck browser="<%= BrowserChecker.BROWSER_IE %>" version="-7">
	<%@ include file="ie.css" %>
</core:browserCheck>
<core:browserCheck browser="<%= BrowserChecker.BROWSER_IE %>" version="+=7">
	<%@ include file="ie7.css" %>
</core:browserCheck>
<core:browserCheck browser="<%= BrowserChecker.BROWSER_OPERA %>" version="*">
	<%@ include file="opera.css" %>
</core:browserCheck>
<core:browserCheck browser="<%= BrowserChecker.BROWSER_FIREFOX %>" version="*">
	<%@ include file="firefox.css" %>
</core:browserCheck>

<%
for(Iterator i = ExtensionStore.getInstance().getExtensionBundles().iterator(); i.hasNext(); ) {
	ExtensionBundle bundle = (ExtensionBundle)i.next();
%>
<tiles:insert page="<%= bundle.getId() + ".css" %>"/>
<%
}
%>


<%
	// Background color
	String c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.background", "");
	if(!c.equals("")) {
		out.println("body { background-color: " + c + "; }");		
	}
	
	// Foreground color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.foreground", "");
	if(!c.equals("")) {
		out.println("body { color: " + c + "; }");		
	}
	
	// Font
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.font", "");
	if(!c.equals("")) {
		out.println("#popup_content, #help_page, select, input, textarea, body, div#component_pageinfo, .titled_dialog_content td, .dialog_categorized_table, " +
					".categorized_table, { font: " + c + "; }");		
	}
	
	// Header color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.header.background", "");
	if(!c.equals("")) {
		out.println("div#component_pageHeader { background-color: " + c + "; }");		
	}
	
	// Sidebar background color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.sideBar.background", "");
	if(!c.equals("")) {
		out.println("div#component_navmenu, #layout_footer, #layout_menus { background-color: " + c + "; }");		
	}
	
	// Sidebar foreground color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.sideBar.foreground", "");
	if(!c.equals("")) {
		out.println("div#component_navmenu a.deselected, div#component_navmenu, #layout_footer, #layout_menus { color: " + c + "; }");			
	}
	
	// Sidebar active color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.sideBar.active.foreground", "");
	if(!c.equals("")) {
		out.println("div#component_navmenu a.selected, #layout_footer #component_logonStatus { color: " + c + "; }");			
	}
	
	// Navmode background color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.navmode.background", "");
	if(!c.equals("")) {
		out.println("div#component_navmode { background-color: " + c + "; }");		
	}
	
	// Navmode foreground color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.navmode.foreground", "");
	if(!c.equals("")) {
		out.println("div#component_navmode { color: " + c + "; }");		
	}
	
	// Panel background color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.background", "");
	if(!c.equals("")) {
		out.println(".tabContent, .dialog, .dialog_content, .titled_dialog_content { background-color: " + c + "; }");		
		out.println(".tabHeadings a:link.currentTab, .tabHeadings a:visited.currentTab { background-color: " + c + "; }");
	}
	
	// Darker panel background color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.background.darker", "");
	if(!c.equals("")) {
		out.println(".tabHeadings a:link, .tabHeadings a:visited { background-color: " + c + "; }");
	}
	
	// Panel foreground color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.foreground", "");
	if(!c.equals("")) {
		out.println(".tabContent, .dialog, .dialog_content,.titled_dialog_content { color: " + c + "; }");		
		out.println(".tabHeadings a:link.currentTab, .tabHeadings a:visited.currentTab { color: " + c + "; }");
		out.println("#propertyItems tr a, #propertyItems tr a:visited, #propertyItems tr a:link, #propertyItems tr a:hover { color: " + c + "; }");
	}
	
	// Darker foreground background color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.foreground.darker", "");
	if(!c.equals("")) {
		out.println(".tabHeadings a:link, .tabHeadings a:visited { color: " + c + "; }");
	}
	
	// Panel border color
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.border", "");
	if(!c.equals("")) {
		out.println(".tabContent, .dialog, .dialog_content,.titled_dialog_content { border: " + c + "; }");			
	}
	
	// Panel rounded corners (moz. only)
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.panel.border.rounded", "true");
	if(c.equals("true")) {
		out.println(".tabContent, .dialog, .dialog_content,.titled_dialog_content { -moz-border-radius:12px;  }");
		out.println("input.cancel, input.close, input.configure, input.exit, input.finish, input.multiAdd, input.multiRemove," +
					"input.new, input.no, input.ok, input.next, input.previous, input.reset, input.retry, input.save, input.yes," +
					"input.search, input.viewIcons, input.viewList, input.done, input.upload,  input.multiUp, input.info, input.multiDown { -moz-border-radius: 12px; }");
	}
	
	// Table header background
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.header.background", "");
	if(!c.equals("")) {
		out.println(".resource_table thead tr, .resource_table tfoot tr { background-color: " + c + "; }");
	}
	
	// Table header foreground
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.header.foreground", "");
	if(!c.equals("")) {
		out.println(".resource_table thead tr, .resource_table tfoot tr { color: " + c + "; }");
		out.println(".resource_table tr a.columnHeader, .resource_table tr a.pagerEnabled { color: " + c + "; }");
	}
	
	// Table highlight background
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.highlight.background", "");
	if(!c.equals("")) {
		out.println(".resource_table tr.highlight { background-color: " + c + "; }");
	}
	
	// Table highlight foreground
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.highlight.foreground", "");
	if(!c.equals("")) {
	}
	
	// Table Lowlight background
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.lowlight.background", "");
	if(!c.equals("")) {
		out.println(".resource_table tr.lowlight { background-color: " + c + "; }");
	}
	
	// Table Lowlight foreground
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.table.lowlight.foreground", "");
	if(!c.equals("")) {
	}
	
	// Button background
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.button.background", "");
	if(!c.equals("")) {
		out.println("input.cancel, input.close, input.configure, input.exit, input.finish, input.multiAdd, input.multiRemove," +
					"input.new, input.no, input.ok, input.next, input.previous, input.reset, input.retry, input.save, input.yes," +
					"input.search, input.viewIcons, input.viewList, input.upload, input.done,  input.multiUp, input.info, input.multiDown { background-color: " + c + "; }");
	}
	
	// Button foreground
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.button.foreground", "");
	if(!c.equals("")) {
		out.println("input.cancel, input.close, input.configure, input.exit, input.finish, input.multiAdd, input.multiRemove," +
					"input.new, input.no, input.ok, input.next, input.previous, input.reset, input.retry, input.save, input.yes," +
					"input.search, input.viewIcons, input.viewList, input.upload, input.done,  input.multiUp, input.info, input.multiDown { color: " + c + "; }");
	}
	
	// Button border
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.button.border", "");
	if(!c.equals("")) {
		out.println("input.cancel, input.close, input.configure, input.exit, input.finish, input.multiAdd, input.multiRemove," +
					"input.new, input.no, input.ok, input.next, input.previous, input.reset, input.retry, input.save, input.yes," +
					"input.search, input.viewIcons, input.viewList, input.upload, input.done,  input.multiUp, input.info, input.multiDown { border: " + c + "; }");
	}
	
	// Link foreground
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.link.foreground", "");
	if(!c.equals("")) {
		out.println("a:link, a:visited, a:hover, a:active { color: " + c + "; }");
	}
	
	// Link font
	c = SystemProperties.get("ovpnals.defaultTheme.userDefinedStyles.link.font", "");
	if(!c.equals("")) {
		out.println("a:link, a:visited, a:hover, a:active { font: " + c + "; }");
	}
	
	

	
%>
