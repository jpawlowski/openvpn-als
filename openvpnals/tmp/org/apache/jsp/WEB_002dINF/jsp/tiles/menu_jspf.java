package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.List;
import java.util.Iterator;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.security.Constants;
import net.openvpn.als.core.AvailableMenuItem;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardStep;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.core.CoreServlet;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.boot.Util;
import net.openvpn.als.security.SessionInfo;
import java.util.Date;
import net.openvpn.als.security.Constants;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import net.openvpn.als.security.SessionInfo;
import java.io.File;
import net.openvpn.als.boot.Util;
import net.openvpn.als.boot.SystemProperties;

public final class menu_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(6);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/navigation.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
    _jspx_dependants.add("/WEB-INF/jsp/tiles/footerInfo.jspf");
    _jspx_dependants.add("/WEB-INF/jsp/tiles/aboutInfo.jspf");
    _jspx_dependants.add("/WEB-INF/security.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_navigation_menuAvailable;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_titleKey_titleId_styleClass_panelId_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_name_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_empty_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEqual_value_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_security_checkAuthenticated_requiresAuthentication;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_scope_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_arg1_arg0_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_navigation_menuAvailable = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_titleKey_titleId_styleClass_panelId_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_name_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_empty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEqual_value_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_security_checkAuthenticated_requiresAuthentication = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_scope_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_arg1_arg0_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_navigation_menuAvailable.release();
    _jspx_tagPool_logic_notEmpty_name.release();
    _jspx_tagPool_input_frame_titleKey_titleId_styleClass_panelId_bundle.release();
    _jspx_tagPool_logic_iterate_type_name_id.release();
    _jspx_tagPool_logic_notEmpty_property_name.release();
    _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_logic_empty_property_name.release();
    _jspx_tagPool_logic_notEqual_value_property_name.release();
    _jspx_tagPool_security_checkAuthenticated_requiresAuthentication.release();
    _jspx_tagPool_logic_notEmpty_scope_name.release();
    _jspx_tagPool_bean_message_key_bundle_arg1_arg0_nobody.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    net.openvpn.als.core.AvailableMenuItem _jspx_level1MenuItem_1 = null;
    net.openvpn.als.core.AvailableMenuItem _jspx_level2MenuItem_2 = null;

    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      //  navigation:menuAvailable
      net.openvpn.als.navigation.tags.MenuAvailableTag _jspx_th_navigation_menuAvailable_0 = (net.openvpn.als.navigation.tags.MenuAvailableTag) _jspx_tagPool_navigation_menuAvailable.get(net.openvpn.als.navigation.tags.MenuAvailableTag.class);
      _jspx_th_navigation_menuAvailable_0.setPageContext(_jspx_page_context);
      _jspx_th_navigation_menuAvailable_0.setParent(null);
      int _jspx_eval_navigation_menuAvailable_0 = _jspx_th_navigation_menuAvailable_0.doStartTag();
      if (_jspx_eval_navigation_menuAvailable_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\r');
          out.write('\n');
          out.write('	');
          //  logic:notEmpty
          org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_0 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
          _jspx_th_logic_notEmpty_0.setPageContext(_jspx_page_context);
          _jspx_th_logic_notEmpty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_navigation_menuAvailable_0);
          _jspx_th_logic_notEmpty_0.setName("menuTree");
          int _jspx_eval_logic_notEmpty_0 = _jspx_th_logic_notEmpty_0.doStartTag();
          if (_jspx_eval_logic_notEmpty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\t\r\n");
              out.write("\t\t");
 AvailableMenuItem selectedMenuItem = (AvailableMenuItem)request.getAttribute(Constants.SELECTED_MENU); 
		   String titleKey = "navigation.userConsole";
		   String titleId = "userConsoleTitle";
		   if(CoreUtil.isInManagementConsole(request)) {
		   		titleKey = "navigation.managementConsole";
		   		titleId = "managementConsoleTitle";
		   }
		
              out.write("\r\n");
              out.write("\t\t");
              //  input:frame
              net.openvpn.als.input.tags.FrameTag _jspx_th_input_frame_0 = (net.openvpn.als.input.tags.FrameTag) _jspx_tagPool_input_frame_titleKey_titleId_styleClass_panelId_bundle.get(net.openvpn.als.input.tags.FrameTag.class);
              _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
              _jspx_th_input_frame_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
              _jspx_th_input_frame_0.setTitleId( titleId );
              _jspx_th_input_frame_0.setTitleKey( titleKey );
              _jspx_th_input_frame_0.setBundle("navigation");
              _jspx_th_input_frame_0.setStyleClass("component_messageBox");
              _jspx_th_input_frame_0.setPanelId("menu");
              int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
              if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_input_frame_0.doInitBody();
                }
                do {
                  out.write("\r\n");
                  out.write("\t\t\t<div id=\"component_navmenu\">\t\r\n");
                  out.write("\t\t\t\t");
                  //  logic:iterate
                  org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
                  _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
                  _jspx_th_logic_iterate_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
                  _jspx_th_logic_iterate_0.setId("rootMenuItem");
                  _jspx_th_logic_iterate_0.setName("menuTree");
                  _jspx_th_logic_iterate_0.setType("net.openvpn.als.core.AvailableMenuItem");
                  int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
                  if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    net.openvpn.als.core.AvailableMenuItem rootMenuItem = null;
                    if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_logic_iterate_0.doInitBody();
                    }
                    rootMenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("rootMenuItem");
                    do {
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t");
                      //  logic:notEmpty
                      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_1 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
                      _jspx_th_logic_notEmpty_1.setPageContext(_jspx_page_context);
                      _jspx_th_logic_notEmpty_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
                      _jspx_th_logic_notEmpty_1.setName("rootMenuItem");
                      _jspx_th_logic_notEmpty_1.setProperty("path");
                      int _jspx_eval_logic_notEmpty_1 = _jspx_th_logic_notEmpty_1.doStartTag();
                      if (_jspx_eval_logic_notEmpty_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\r\n");
                          out.write("\t\t\t\t\t\t\t<div class=\"item\">\r\n");
                          out.write("\t\t\t\t\t\t\t\t");
                          //  input:toolTip
                          net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.get(net.openvpn.als.input.tags.ToolTipTag.class);
                          _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
                          _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
                          _jspx_th_input_toolTip_0.setStyleId( rootMenuItem == selectedMenuItem ? "selected" : "deselected" );
                          _jspx_th_input_toolTip_0.setHref( rootMenuItem.getPath() );
                          _jspx_th_input_toolTip_0.setTarget( rootMenuItem.getMenuItem().getTarget() );
                          _jspx_th_input_toolTip_0.setBundle( rootMenuItem.getMenuItem().getMessageResourcesKey() );
                          _jspx_th_input_toolTip_0.setKey( "menuItem." + rootMenuItem.getMenuItem().getId() + ".description"  );
                          int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
                          if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_input_toolTip_0.doInitBody();
                            }
                            do {
                              //  bean:message
                              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                              _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
                              _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_0);
                              _jspx_th_bean_message_0.setKey( "menuItem." + rootMenuItem.getMenuItem().getId() + ".title"  );
                              _jspx_th_bean_message_0.setBundle( rootMenuItem.getMenuItem().getMessageResourcesKey() );
                              int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
                              if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
                              int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                            } while (true);
                            if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                          }
                          if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                            return;
                          _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.reuse(_jspx_th_input_toolTip_0);
                          out.write("\r\n");
                          out.write("\t\t\t\t\t\t\t</div>\r\n");
                          out.write("\t\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_logic_notEmpty_1.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                      }
                      if (_jspx_th_logic_notEmpty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                        return;
                      _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_1);
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t");
                      //  logic:empty
                      org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_0 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
                      _jspx_th_logic_empty_0.setPageContext(_jspx_page_context);
                      _jspx_th_logic_empty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
                      _jspx_th_logic_empty_0.setName("rootMenuItem");
                      _jspx_th_logic_empty_0.setProperty("path");
                      int _jspx_eval_logic_empty_0 = _jspx_th_logic_empty_0.doStartTag();
                      if (_jspx_eval_logic_empty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\r\n");
                          out.write("\t\t\t\t\t\t\t<div class=\"group\">\r\n");
                          out.write("\t\t\t\t\t\t\t\t");
                          //  bean:message
                          org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                          _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
                          _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_0);
                          _jspx_th_bean_message_1.setKey( "menuItem." + rootMenuItem.getMenuItem().getId() + ".title"  );
                          _jspx_th_bean_message_1.setBundle( rootMenuItem.getMenuItem().getMessageResourcesKey() );
                          int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
                          if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                            return;
                          _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
                          out.write("\r\n");
                          out.write("\t\t\t\t\t\t\t</div>\r\n");
                          out.write("\t\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_logic_empty_0.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                      }
                      if (_jspx_th_logic_empty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                        return;
                      _jspx_tagPool_logic_empty_property_name.reuse(_jspx_th_logic_empty_0);
                      out.write("\r\n");
                      out.write("\t\t\t\t\t");
                      //  logic:notEqual
                      org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_0 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_property_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
                      _jspx_th_logic_notEqual_0.setPageContext(_jspx_page_context);
                      _jspx_th_logic_notEqual_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
                      _jspx_th_logic_notEqual_0.setName("rootMenuItem");
                      _jspx_th_logic_notEqual_0.setProperty("empty");
                      _jspx_th_logic_notEqual_0.setValue("true");
                      int _jspx_eval_logic_notEqual_0 = _jspx_th_logic_notEqual_0.doStartTag();
                      if (_jspx_eval_logic_notEqual_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\r\n");
                          out.write("\t\t\t\t\t\t");
                          //  logic:iterate
                          org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_1 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
                          _jspx_th_logic_iterate_1.setPageContext(_jspx_page_context);
                          _jspx_th_logic_iterate_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_0);
                          _jspx_th_logic_iterate_1.setId("level1MenuItem");
                          _jspx_th_logic_iterate_1.setName("rootMenuItem");
                          _jspx_th_logic_iterate_1.setType("net.openvpn.als.core.AvailableMenuItem");
                          int _jspx_eval_logic_iterate_1 = _jspx_th_logic_iterate_1.doStartTag();
                          if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                            net.openvpn.als.core.AvailableMenuItem level1MenuItem = null;
                            if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_logic_iterate_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_logic_iterate_1.doInitBody();
                            }
                            level1MenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("level1MenuItem");
                            do {
                              out.write("\t\t\t\t\t\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              //  logic:notEmpty
                              org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_2 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
                              _jspx_th_logic_notEmpty_2.setPageContext(_jspx_page_context);
                              _jspx_th_logic_notEmpty_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
                              _jspx_th_logic_notEmpty_2.setName("level1MenuItem");
                              _jspx_th_logic_notEmpty_2.setProperty("path");
                              int _jspx_eval_logic_notEmpty_2 = _jspx_th_logic_notEmpty_2.doStartTag();
                              if (_jspx_eval_logic_notEmpty_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t<div class=\"item\">\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              //  input:toolTip
                              net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.get(net.openvpn.als.input.tags.ToolTipTag.class);
                              _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
                              _jspx_th_input_toolTip_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_2);
                              _jspx_th_input_toolTip_1.setStyleId( level1MenuItem == selectedMenuItem ? "selected" : "deselected" );
                              _jspx_th_input_toolTip_1.setHref( level1MenuItem.getPath() );
                              _jspx_th_input_toolTip_1.setTarget( level1MenuItem.getMenuItem().getTarget() );
                              _jspx_th_input_toolTip_1.setBundle( level1MenuItem.getMenuItem().getMessageResourcesKey() );
                              _jspx_th_input_toolTip_1.setKey( "menuItem." + level1MenuItem.getMenuItem().getId() + ".description"  );
                              int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
                              if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_input_toolTip_1.doInitBody();
                              }
                              do {
                              //  bean:message
                              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                              _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
                              _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_1);
                              _jspx_th_bean_message_2.setKey( "menuItem." + level1MenuItem.getMenuItem().getId() + ".title"  );
                              _jspx_th_bean_message_2.setBundle( level1MenuItem.getMenuItem().getMessageResourcesKey() );
                              int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
                              if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
                              int evalDoAfterBody = _jspx_th_input_toolTip_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_input_toolTip_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.reuse(_jspx_th_input_toolTip_1);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_notEmpty_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_logic_notEmpty_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_2);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              //  logic:empty
                              org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_1 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
                              _jspx_th_logic_empty_1.setPageContext(_jspx_page_context);
                              _jspx_th_logic_empty_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
                              _jspx_th_logic_empty_1.setName("level1MenuItem");
                              _jspx_th_logic_empty_1.setProperty("path");
                              int _jspx_eval_logic_empty_1 = _jspx_th_logic_empty_1.doStartTag();
                              if (_jspx_eval_logic_empty_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t<div class=\"group\">\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              //  bean:message
                              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                              _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
                              _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_1);
                              _jspx_th_bean_message_3.setKey( "menuItem." + level1MenuItem.getMenuItem().getId() + ".title"  );
                              _jspx_th_bean_message_3.setBundle( level1MenuItem.getMenuItem().getMessageResourcesKey() );
                              int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
                              if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_3);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_empty_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_logic_empty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_empty_property_name.reuse(_jspx_th_logic_empty_1);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t");
                              //  logic:notEqual
                              org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_1 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_property_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
                              _jspx_th_logic_notEqual_1.setPageContext(_jspx_page_context);
                              _jspx_th_logic_notEqual_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
                              _jspx_th_logic_notEqual_1.setName("level1MenuItem");
                              _jspx_th_logic_notEqual_1.setProperty("empty");
                              _jspx_th_logic_notEqual_1.setValue("true");
                              int _jspx_eval_logic_notEqual_1 = _jspx_th_logic_notEqual_1.doStartTag();
                              if (_jspx_eval_logic_notEqual_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\t\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              //  logic:iterate
                              org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_2 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
                              _jspx_th_logic_iterate_2.setPageContext(_jspx_page_context);
                              _jspx_th_logic_iterate_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_1);
                              _jspx_th_logic_iterate_2.setId("level2MenuItem");
                              _jspx_th_logic_iterate_2.setName("level1MenuItem");
                              _jspx_th_logic_iterate_2.setType("net.openvpn.als.core.AvailableMenuItem");
                              int _jspx_eval_logic_iterate_2 = _jspx_th_logic_iterate_2.doStartTag();
                              if (_jspx_eval_logic_iterate_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              net.openvpn.als.core.AvailableMenuItem level2MenuItem = null;
                              if (_jspx_eval_logic_iterate_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_logic_iterate_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_logic_iterate_2.doInitBody();
                              }
                              level2MenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("level2MenuItem");
                              do {
                              out.write("\t\t\t\t\t\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              //  logic:notEmpty
                              org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_3 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
                              _jspx_th_logic_notEmpty_3.setPageContext(_jspx_page_context);
                              _jspx_th_logic_notEmpty_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_2);
                              _jspx_th_logic_notEmpty_3.setName("level2MenuItem");
                              _jspx_th_logic_notEmpty_3.setProperty("path");
                              int _jspx_eval_logic_notEmpty_3 = _jspx_th_logic_notEmpty_3.doStartTag();
                              if (_jspx_eval_logic_notEmpty_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t<div class=\"item\">\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  input:toolTip
                              net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_2 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.get(net.openvpn.als.input.tags.ToolTipTag.class);
                              _jspx_th_input_toolTip_2.setPageContext(_jspx_page_context);
                              _jspx_th_input_toolTip_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_3);
                              _jspx_th_input_toolTip_2.setStyleId( level2MenuItem == selectedMenuItem ? "selected" : "deselected" );
                              _jspx_th_input_toolTip_2.setHref( level2MenuItem.getPath() );
                              _jspx_th_input_toolTip_2.setTarget( level2MenuItem.getMenuItem().getTarget() );
                              _jspx_th_input_toolTip_2.setBundle( level2MenuItem.getMenuItem().getMessageResourcesKey() );
                              _jspx_th_input_toolTip_2.setKey( "menuItem." + level2MenuItem.getMenuItem().getId() + ".description"  );
                              int _jspx_eval_input_toolTip_2 = _jspx_th_input_toolTip_2.doStartTag();
                              if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                              out = _jspx_page_context.pushBody();
                              _jspx_th_input_toolTip_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                              _jspx_th_input_toolTip_2.doInitBody();
                              }
                              do {
                              //  bean:message
                              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_4 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                              _jspx_th_bean_message_4.setPageContext(_jspx_page_context);
                              _jspx_th_bean_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_2);
                              _jspx_th_bean_message_4.setKey( "menuItem." + level2MenuItem.getMenuItem().getId() + ".title"  );
                              _jspx_th_bean_message_4.setBundle( level2MenuItem.getMenuItem().getMessageResourcesKey() );
                              int _jspx_eval_bean_message_4 = _jspx_th_bean_message_4.doStartTag();
                              if (_jspx_th_bean_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_4);
                              int evalDoAfterBody = _jspx_th_input_toolTip_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_input_toolTip_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_input_toolTip_target_styleId_key_href_bundle.reuse(_jspx_th_input_toolTip_2);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t</div>\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_notEmpty_3.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_logic_notEmpty_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_3);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              //  logic:empty
                              org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_2 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
                              _jspx_th_logic_empty_2.setPageContext(_jspx_page_context);
                              _jspx_th_logic_empty_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_2);
                              _jspx_th_logic_empty_2.setName("level2MenuItem");
                              _jspx_th_logic_empty_2.setProperty("path");
                              int _jspx_eval_logic_empty_2 = _jspx_th_logic_empty_2.doStartTag();
                              if (_jspx_eval_logic_empty_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                              do {
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t<div class=\"group\">\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t\t");
                              //  bean:message
                              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_5 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                              _jspx_th_bean_message_5.setPageContext(_jspx_page_context);
                              _jspx_th_bean_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_2);
                              _jspx_th_bean_message_5.setKey( "menuItem." + level2MenuItem.getMenuItem().getId() + ".title"  );
                              _jspx_th_bean_message_5.setBundle( level2MenuItem.getMenuItem().getMessageResourcesKey() );
                              int _jspx_eval_bean_message_5 = _jspx_th_bean_message_5.doStartTag();
                              if (_jspx_th_bean_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_5);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t\t</div>\r\n");
                              out.write("\t\t\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_empty_2.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_logic_empty_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_empty_property_name.reuse(_jspx_th_logic_empty_2);
                              out.write("\t\t\r\n");
                              out.write("\t\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_iterate_2.doAfterBody();
                              level2MenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("level2MenuItem");
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              if (_jspx_eval_logic_iterate_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                              }
                              if (_jspx_th_logic_iterate_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_iterate_type_name_id.reuse(_jspx_th_logic_iterate_2);
                              out.write("\t\r\n");
                              out.write("\t\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_notEqual_1.doAfterBody();
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                              } while (true);
                              }
                              if (_jspx_th_logic_notEqual_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                              return;
                              _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_1);
                              out.write("\r\n");
                              out.write("\t\t\t\t\t\t");
                              int evalDoAfterBody = _jspx_th_logic_iterate_1.doAfterBody();
                              level1MenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("level1MenuItem");
                              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                              break;
                            } while (true);
                            if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                              out = _jspx_page_context.popBody();
                          }
                          if (_jspx_th_logic_iterate_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                            return;
                          _jspx_tagPool_logic_iterate_type_name_id.reuse(_jspx_th_logic_iterate_1);
                          out.write("\r\n");
                          out.write("\t\t\t\t\t");
                          int evalDoAfterBody = _jspx_th_logic_notEqual_0.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                      }
                      if (_jspx_th_logic_notEqual_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                        return;
                      _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_0);
                      out.write("\r\n");
                      out.write("\t\t\t\t");
                      int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
                      rootMenuItem = (net.openvpn.als.core.AvailableMenuItem) _jspx_page_context.findAttribute("rootMenuItem");
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_logic_iterate_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_logic_iterate_type_name_id.reuse(_jspx_th_logic_iterate_0);
                  out.write("\r\n");
                  out.write("\t\t\t</div>\r\n");
                  out.write("\t\t\t");
                  out.write("<div id=\"component_pageFooter\">\r\n");
                  out.write("\t<div id=\"component_pageFooterInner\">\r\n");
                  out.write("\t\t<div class=\"separator\"><hr/>\r\n");
                  out.write("\t\t</div> \r\n");
                  out.write("\t\t");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("\r\n");
                  out.write("<div id=\"aboutInfo\">\r\n");
                  out.write("\t");
                  //  security:checkAuthenticated
                  net.openvpn.als.security.tags.CheckAuthenticatedTag _jspx_th_security_checkAuthenticated_0 = (net.openvpn.als.security.tags.CheckAuthenticatedTag) _jspx_tagPool_security_checkAuthenticated_requiresAuthentication.get(net.openvpn.als.security.tags.CheckAuthenticatedTag.class);
                  _jspx_th_security_checkAuthenticated_0.setPageContext(_jspx_page_context);
                  _jspx_th_security_checkAuthenticated_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
                  _jspx_th_security_checkAuthenticated_0.setRequiresAuthentication(true);
                  int _jspx_eval_security_checkAuthenticated_0 = _jspx_th_security_checkAuthenticated_0.doStartTag();
                  if (_jspx_eval_security_checkAuthenticated_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    do {
                      out.write("\r\n");
                      out.write("\t\t");
                      //  logic:notEmpty
                      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_4 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_scope_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
                      _jspx_th_logic_notEmpty_4.setPageContext(_jspx_page_context);
                      _jspx_th_logic_notEmpty_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_checkAuthenticated_0);
                      _jspx_th_logic_notEmpty_4.setScope("session");
                      _jspx_th_logic_notEmpty_4.setName("sessionInfo");
                      int _jspx_eval_logic_notEmpty_4 = _jspx_th_logic_notEmpty_4.doStartTag();
                      if (_jspx_eval_logic_notEmpty_4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                        do {
                          out.write("\t\t\t\t\t\t\r\n");
                          out.write("\t\t\t<div id=\"component_logonStatus\">\r\n");
                          out.write("\t\t\t\t");
                          //  bean:message
                          org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_6 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_arg1_arg0_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                          _jspx_th_bean_message_6.setPageContext(_jspx_page_context);
                          _jspx_th_bean_message_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_4);
                          _jspx_th_bean_message_6.setKey("footer.info");
                          _jspx_th_bean_message_6.setBundle("navigation");
                          _jspx_th_bean_message_6.setArg0( ((SessionInfo)request.getSession().getAttribute(
							Constants.SESSION_INFO)).getUser().getPrincipalName() );
                          _jspx_th_bean_message_6.setArg1( SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(new Date(((SessionInfo)request.getSession().getAttribute(
							Constants.SESSION_INFO)).getLogonTime().getTimeInMillis())) );
                          int _jspx_eval_bean_message_6 = _jspx_th_bean_message_6.doStartTag();
                          if (_jspx_th_bean_message_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                            return;
                          _jspx_tagPool_bean_message_key_bundle_arg1_arg0_nobody.reuse(_jspx_th_bean_message_6);
                          out.write("\r\n");
                          out.write("\t\t\t</div> \r\n");
                          out.write("\t\t");
                          int evalDoAfterBody = _jspx_th_logic_notEmpty_4.doAfterBody();
                          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                            break;
                        } while (true);
                      }
                      if (_jspx_th_logic_notEmpty_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                        return;
                      _jspx_tagPool_logic_notEmpty_scope_name.reuse(_jspx_th_logic_notEmpty_4);
                      out.write('\r');
                      out.write('\n');
                      out.write('	');
                      int evalDoAfterBody = _jspx_th_security_checkAuthenticated_0.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                  }
                  if (_jspx_th_security_checkAuthenticated_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_security_checkAuthenticated_requiresAuthentication.reuse(_jspx_th_security_checkAuthenticated_0);
                  out.write("\r\n");
                  out.write("\t<div id=\"component_sslx_version\">\r\n");
                  out.write("\t");
 if (Util.isNullOrTrimmedBlank(SystemProperties.get("openvpnals.footer.copyright", ""))) { 
                  out.write("\r\n");
                  out.write("\t\t");
                  if (_jspx_meth_bean_message_7(_jspx_th_input_frame_0, _jspx_page_context))
                    return;
                  out.write('\r');
                  out.write('\n');
                  out.write('	');
 } else {
                  out.write("\r\n");
                  out.write("\t\t");
                  out.print( SystemProperties.get("openvpnals.footer.copyright"));
                  out.write('\r');
                  out.write('\n');
                  out.write('	');
 } 
                  out.write("\r\n");
                  out.write("    &nbsp;");
                  out.print( net.openvpn.als.boot.ContextHolder.getContext().getVersion() );
                  out.write("<br/>\r\n");
                  out.write("\t\r\n");
                  out.write("\t\t");
 if(new File("build.xml").exists()) { 
                  out.write("\t\r\n");
                  out.write("\t\t\t");
                  if (_jspx_meth_bean_message_8(_jspx_th_input_frame_0, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t");
 } else { 
                  out.write("\r\n");
                  out.write("\t\t\t");
                  if (_jspx_meth_bean_message_9(_jspx_th_input_frame_0, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t");
 } 
                  out.write("\t\t\t\r\n");
                  out.write("\t</div> \r\n");
                  out.write("\t<div id=\"component_sslx_copyright\">\r\n");
                  out.write("\t");
 if (Util.isNullOrTrimmedBlank(SystemProperties.get("openvpnals.footer.copyright.additional", ""))) { 
                  out.write("\r\n");
                  out.write("\t\t");
                  if (_jspx_meth_bean_message_10(_jspx_th_input_frame_0, _jspx_page_context))
                    return;
                  out.write('\r');
                  out.write('\n');
                  out.write('	');
 } else {
                  out.write("\r\n");
                  out.write("\t\t");
                  out.print( SystemProperties.get("openvpnals.footer.copyright.additional"));
                  out.write('\r');
                  out.write('\n');
                  out.write('	');
 } 
                  out.write("\r\n");
                  out.write("\t</div>\r\n");
                  out.write("</div>");
                  out.write("\r\n");
                  out.write("\t</div>\r\n");
                  out.write("</div>");
                  out.write("\r\n");
                  out.write("\t\t");
                  int evalDoAfterBody = _jspx_th_input_frame_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_input_frame_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_input_frame_titleKey_titleId_styleClass_panelId_bundle.reuse(_jspx_th_input_frame_0);
              out.write('\r');
              out.write('\n');
              out.write('	');
              int evalDoAfterBody = _jspx_th_logic_notEmpty_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_logic_notEmpty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_notEmpty_name.reuse(_jspx_th_logic_notEmpty_0);
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_navigation_menuAvailable_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_navigation_menuAvailable_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_navigation_menuAvailable.reuse(_jspx_th_navigation_menuAvailable_0);
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_bean_message_7(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_7 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_7.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_bean_message_7.setKey("footer.copyright");
    _jspx_th_bean_message_7.setBundle("navigation");
    int _jspx_eval_bean_message_7 = _jspx_th_bean_message_7.doStartTag();
    if (_jspx_th_bean_message_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_7);
    return false;
  }

  private boolean _jspx_meth_bean_message_8(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_8 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_8.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_bean_message_8.setKey("footer.sourceEdition");
    _jspx_th_bean_message_8.setBundle("navigation");
    int _jspx_eval_bean_message_8 = _jspx_th_bean_message_8.doStartTag();
    if (_jspx_th_bean_message_8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_8);
    return false;
  }

  private boolean _jspx_meth_bean_message_9(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_9 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_9.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_bean_message_9.setKey("footer.communityEdition");
    _jspx_th_bean_message_9.setBundle("navigation");
    int _jspx_eval_bean_message_9 = _jspx_th_bean_message_9.doStartTag();
    if (_jspx_th_bean_message_9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_9);
    return false;
  }

  private boolean _jspx_meth_bean_message_10(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_10 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_10.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_bean_message_10.setKey("footer.copyright.additional");
    _jspx_th_bean_message_10.setBundle("navigation");
    int _jspx_eval_bean_message_10 = _jspx_th_bean_message_10.doStartTag();
    if (_jspx_th_bean_message_10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_10);
    return false;
  }
}
