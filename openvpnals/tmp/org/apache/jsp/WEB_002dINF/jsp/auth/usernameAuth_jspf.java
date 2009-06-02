package org.apache.jsp.WEB_002dINF.jsp.auth;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.security.forms.LogonForm;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import org.apache.struts.taglib.TagUtils;
import net.openvpn.als.core.CoreUtil;

public final class usernameAuth_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(3);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/jsp/content/security/loginPageInfo.jspf");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_styleId;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_getProperty_propertyName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_form_styleClass_method_focus_autocomplete_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_hidden_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_text_styleId_property_maxlength_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_submit_styleClass;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEqual_value_property_name;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_styleId = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_getProperty_propertyName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_form_styleClass_method_focus_autocomplete_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_hidden_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_text_styleId_property_maxlength_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_submit_styleClass = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEqual_value_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
    _jspx_tagPool_input_frame_styleId.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.release();
    _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.release();
    _jspx_tagPool_core_getProperty_propertyName_nobody.release();
    _jspx_tagPool_core_form_styleClass_method_focus_autocomplete_action.release();
    _jspx_tagPool_html_hidden_property_nobody.release();
    _jspx_tagPool_html_text_styleId_property_maxlength_nobody.release();
    _jspx_tagPool_html_submit_styleClass.release();
    _jspx_tagPool_logic_notEqual_value_property_name.release();
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


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html;charset=UTF-8");
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
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("resourceBundle");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String resourceBundle = null;
      resourceBundle = (java.lang.String) _jspx_page_context.findAttribute("resourceBundle");
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("resourcePrefix");
      _jspx_th_tiles_useAttribute_1.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      java.lang.String resourcePrefix = null;
      resourcePrefix = (java.lang.String) _jspx_page_context.findAttribute("resourcePrefix");
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_2 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_2.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_2.setParent(null);
      _jspx_th_tiles_useAttribute_2.setName("infoImage");
      _jspx_th_tiles_useAttribute_2.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_2 = _jspx_th_tiles_useAttribute_2.doStartTag();
      if (_jspx_th_tiles_useAttribute_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_2);
      java.lang.String infoImage = null;
      infoImage = (java.lang.String) _jspx_page_context.findAttribute("infoImage");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!-- If you change this file don't forget to change the one in Brandless Logon as well -->\r\n");
      out.write("\r\n");
      out.write("<div id=\"login_page_info\">\r\n");
      //  input:frame
      net.openvpn.als.input.tags.FrameTag _jspx_th_input_frame_0 = (net.openvpn.als.input.tags.FrameTag) _jspx_tagPool_input_frame_styleId.get(net.openvpn.als.input.tags.FrameTag.class);
      _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
      _jspx_th_input_frame_0.setParent(null);
      _jspx_th_input_frame_0.setStyleId("component_pageInfo");
      int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
      if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_input_frame_0.doInitBody();
        }
        do {
          out.write("\r\n");
          out.write("\t<table align=\"center\">\r\n");
          out.write("\t\t<tr align=\"center\">\r\n");
          out.write("\t\t\t<td valign=\"top\">\r\n");
          out.write("\t        ");
 String welcomeLogo = Property.getProperty(new SystemConfigKey("loginPage.welcomeLogo"));
	           if(welcomeLogo.equals("default")) {
	           	   welcomeLogo = CoreUtil.getThemePath(session) + "/images/info/" + infoImage;
	       	
          out.write("\r\n");
          out.write("\t       \t\t<img class=\"infoImage\" src=\"");
          out.print( welcomeLogo );
          out.write("\" />\r\n");
          out.write("\t\t\t</td>\r\n");
          out.write("\t       \t");
 
	           } 
	           else {
	           		welcomeLogo = "/icons/" + welcomeLogo;
	       	
          out.write("\r\n");
          out.write("\t\t\t\t<img class=\"customLoginInfoImage\" src=\"");
          out.print( welcomeLogo );
          out.write("\" />\r\n");
          out.write("\t\t\t</td>\r\n");
          out.write("\t\t</tr>\r\n");
          out.write("\t\t<tr align=\"center\">\r\n");
          out.write("\t       \t");
 }
          out.write("\r\n");
          out.write("\t\t\t<td>\r\n");
          out.write("\t\t\t\t<h1>\t\t\t\t\t\r\n");
          out.write("\t\t\t\t\t");
          //  bean:message
          org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
          _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
          _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_bean_message_0.setKey( resourcePrefix + ".subtitle" );
          _jspx_th_bean_message_0.setBundle( resourceBundle );
          int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
          if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
          out.write("\t\r\n");
          out.write("\t\t\t\t</h1>\r\n");
          out.write("\t\t\t\t");
          //  core:checkPropertyEquals
          net.openvpn.als.core.tags.CheckPropertyEqualsTag _jspx_th_core_checkPropertyEquals_0 = (net.openvpn.als.core.tags.CheckPropertyEqualsTag) _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.get(net.openvpn.als.core.tags.CheckPropertyEqualsTag.class);
          _jspx_th_core_checkPropertyEquals_0.setPageContext(_jspx_page_context);
          _jspx_th_core_checkPropertyEquals_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_core_checkPropertyEquals_0.setRegExp(false);
          _jspx_th_core_checkPropertyEquals_0.setPropertyValue("");
          _jspx_th_core_checkPropertyEquals_0.setPropertyName("loginPage.welcomeTitle");
          int _jspx_eval_core_checkPropertyEquals_0 = _jspx_th_core_checkPropertyEquals_0.doStartTag();
          if (_jspx_eval_core_checkPropertyEquals_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\t\r\n");
              out.write("\t\t\t\t\t");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyEquals_0);
              _jspx_th_bean_message_1.setKey( resourcePrefix + ".description" );
              _jspx_th_bean_message_1.setBundle( resourceBundle );
              int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
              if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
              out.write("\r\n");
              out.write("\t\t\t\t");
              int evalDoAfterBody = _jspx_th_core_checkPropertyEquals_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_core_checkPropertyEquals_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyEquals_0);
          out.write("\t\t\t\t\t\r\n");
          out.write("\t\t\t\t");
          if (_jspx_meth_core_checkPropertyNotEquals_0(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write("\t\t\t\t\r\n");
          out.write("\t\t\t</td>\r\n");
          out.write("\t\t</tr>\r\n");
          out.write("\t</table>\t\r\n");
          out.write("\t<script language=\"javascript1.1\" type=\"text/javascript\">\r\n");
          out.write("\t<!--\r\n");
          out.write("\t\tif (!document.cookie) {\r\n");
          out.write("\t\t\tdocument.writeln(\"<span class='smallText'><p align='center' class='warningText'>");
          if (_jspx_meth_bean_message_2(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write("</p></span>\");\r\n");
          out.write("\t\t\tdocument.writeln(\"<br/>\");\r\n");
          out.write("\t\t}\t\t \r\n");
          out.write("\t// -->\r\n");
          out.write("\t</script>\t\r\n");
          out.write("\t");
          if (_jspx_meth_core_checkPropertyNotEquals_1(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_input_frame_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_input_frame_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_input_frame_styleId.reuse(_jspx_th_input_frame_0);
      out.write("\r\n");
      out.write("</div>");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"page_login_user_password\">\r\n");
      out.write("\t");
      //  core:form
      net.openvpn.als.core.tags.FormTag _jspx_th_core_form_0 = (net.openvpn.als.core.tags.FormTag) _jspx_tagPool_core_form_styleClass_method_focus_autocomplete_action.get(net.openvpn.als.core.tags.FormTag.class);
      _jspx_th_core_form_0.setPageContext(_jspx_page_context);
      _jspx_th_core_form_0.setParent(null);
      _jspx_th_core_form_0.setStyleClass("dialog_form");
      _jspx_th_core_form_0.setAutocomplete("OFF");
      _jspx_th_core_form_0.setMethod("post");
      _jspx_th_core_form_0.setAction("/showLogon.do");
      _jspx_th_core_form_0.setFocus("username");
      int _jspx_eval_core_form_0 = _jspx_th_core_form_0.doStartTag();
      if (_jspx_eval_core_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\r\n");
          out.write("\t\t");
          if (_jspx_meth_html_hidden_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t<noScript>\r\n");
          out.write("\t\t\t<table align=\"center\">\r\n");
          out.write("\t\t\t\t<tr>\r\n");
          out.write("\t\t\t\t\t<td>\r\n");
          out.write("\t\t\t\t\t\t<img class=\"infoImage\" src=\"");
          out.print( CoreUtil.getThemePath(session) + "/images/info/warning.gif" );
          out.write("\" />\r\n");
          out.write("\r\n");
          out.write("\t\t\t\t\t</td>\r\n");
          out.write("\t\t\t\t\t<td>\r\n");
          out.write("\t\t\t\t\t\t<h3>\t\t\t\t\t\r\n");
          out.write("\t\t\t\t\t\t\t");
          if (_jspx_meth_bean_message_3(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write(" \r\n");
          out.write("\t\t\t\t\t\t</h3>\r\n");
          out.write("\t\t\t\t\t</td>\r\n");
          out.write("\t\t\t\t</tr>\r\n");
          out.write("\t\t\t</table>\t\r\n");
          out.write("\r\n");
          out.write("\r\n");
          out.write("\t\t</noScript>\r\n");
          out.write("\t\t<div class=\"dialog_content\">\r\n");
          out.write("\t\t\t<div class=\"inner\">\r\n");
          out.write("\t\t\t\t<table class=\"dialog_form_table\" id=\"centered\">\r\n");
          out.write("\t\t\t\t\t<tbody>\r\n");
          out.write("\t\t\t\t\t\t<tr>\r\n");
          out.write("\t\t\t\t\t\t\t<td class=\"value\">\r\n");
          out.write("\t\t\t\t\t\t\t\t<label for=\"username\">\r\n");
          out.write("\t\t\t\t\t\t\t\t\t");
          if (_jspx_meth_bean_message_4(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write(" \r\n");
          out.write("\t\t\t\t\t\t\t\t</label>\r\n");
          out.write("\t\t\t\t\t\t\t\t<br/>\t\r\n");
          out.write("\t\t\t\t\t\t\t\t<input type=\"hidden\" id=\"javascriptElement\" name=\"javaScript\" value=\"false\"/>\t\t\t\t\t\t\t\t\t\r\n");
          out.write("\t\t\t\t\t\t\t\t");
          if (_jspx_meth_html_text_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\t\t\t\t\t</td>\r\n");
          out.write("\t\t\t\t\t\t</tr>\r\n");
          out.write("\t\t\t\t\t</tbody>\r\n");
          out.write("\t\t\t\t\t<tfoot>\r\n");
          out.write("\t\t\t\t\t\t<tr>\r\n");
          out.write("\t\t\t\t\t\t\t<td colspan=\"2\">\t\t\t\t\t\t\t\r\n");
          out.write("\t\t\t\t\t\t\t\t<div class=\"button_bar\">\r\n");
          out.write("\t\t\t\t\t\t\t\t\t<div class=\"formButton\">\r\n");
          out.write("\t\t\t\t\t\t\t\t\t\t");
          if (_jspx_meth_html_submit_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
          out.write("\t\t\t\t\t\t\t\t</div>\r\n");
          out.write("\t\t\t\t\t\t\t</td>\t\t\t\t\t\t                        \r\n");
          out.write("\t\t\t\t\t\t</tr>\r\n");
          out.write("\t\t\t\t\t</tfoot>\r\n");
          out.write("\t\t\t\t</table>\r\n");
          out.write("\t\t\t</div>\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_core_form_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_core_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_core_form_styleClass_method_focus_autocomplete_action.reuse(_jspx_th_core_form_0);
      out.write(" \r\n");
      out.write("</div>\r\n");
      //  logic:notEqual
      org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_0 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_property_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
      _jspx_th_logic_notEqual_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEqual_0.setParent(null);
      _jspx_th_logic_notEqual_0.setName("logonForm");
      _jspx_th_logic_notEqual_0.setProperty("sessionLocked");
      _jspx_th_logic_notEqual_0.setValue("true");
      int _jspx_eval_logic_notEqual_0 = _jspx_th_logic_notEqual_0.doStartTag();
      if (_jspx_eval_logic_notEqual_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write(" \r\n");
          out.write("\t");
          //  core:checkPropertyNotEquals
          net.openvpn.als.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_2 = (net.openvpn.als.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(net.openvpn.als.core.tags.CheckPropertyNotEqualsTag.class);
          _jspx_th_core_checkPropertyNotEquals_2.setPageContext(_jspx_page_context);
          _jspx_th_core_checkPropertyNotEquals_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_0);
          _jspx_th_core_checkPropertyNotEquals_2.setRegExp(false);
          _jspx_th_core_checkPropertyNotEquals_2.setPropertyValue("none");
          _jspx_th_core_checkPropertyNotEquals_2.setPropertyName("loginPage.welcomeMessageType");
          int _jspx_eval_core_checkPropertyNotEquals_2 = _jspx_th_core_checkPropertyNotEquals_2.doStartTag();
          if (_jspx_eval_core_checkPropertyNotEquals_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\r\n");
              out.write("\t\t<div id=\"welcomeMessage\">\r\n");
              out.write("\t\t\t\t<table class=\"dialog_form_table\">\r\n");
              out.write("\t\t\t\t\t\t");
              //  core:checkPropertyNotEquals
              net.openvpn.als.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_3 = (net.openvpn.als.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(net.openvpn.als.core.tags.CheckPropertyNotEqualsTag.class);
              _jspx_th_core_checkPropertyNotEquals_3.setPageContext(_jspx_page_context);
              _jspx_th_core_checkPropertyNotEquals_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyNotEquals_2);
              _jspx_th_core_checkPropertyNotEquals_3.setRegExp(false);
              _jspx_th_core_checkPropertyNotEquals_3.setPropertyValue("noicon");
              _jspx_th_core_checkPropertyNotEquals_3.setPropertyName("loginPage.welcomeMessageType");
              int _jspx_eval_core_checkPropertyNotEquals_3 = _jspx_th_core_checkPropertyNotEquals_3.doStartTag();
              if (_jspx_eval_core_checkPropertyNotEquals_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t<tr class=\"formBlock\"> \r\n");
                  out.write("\t\t\t\t\t\t\t\t<td class=\"label\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<img src=\"");
                  out.print( CoreUtil.getThemePath(session) + "/images/dialog/" + Property.getProperty(new SystemConfigKey("loginPage.welcomeMessageType")) + ".gif" );
                  out.write("\"/>\r\n");
                  out.write("\t\t\t\t\t\t\t\t</td>\r\n");
                  out.write("\t\t\t\t\t\t\t</tr>\r\n");
                  out.write("\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_3.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_core_checkPropertyNotEquals_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_3);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t<tr class=\"formBlock\">\r\n");
              out.write("\t\t\t\t\t\t\t<td class=\"");
              out.print( Property.getProperty(new SystemConfigKey("loginPage.welcomeMessageAlign")) );
              out.write("\">     \r\n");
              out.write("\t\t\t\t\t\t\t\t");
              out.print( TagUtils.getInstance().filter(Property.getProperty(new SystemConfigKey("loginPage.welcomeMessage"))) );
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t</tr>\r\n");
              out.write("\t\t\t\t</table>\r\n");
              out.write("\t\t</div>\r\n");
              out.write("\t");
              int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_2.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_core_checkPropertyNotEquals_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_2);
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_logic_notEqual_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEqual_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_0);
      out.write("\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("<!--\r\n");
      out.write("\t$('javascriptElement').value = 'true';\r\n");
      out.write("-->\r\n");
      out.write("</script>");
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

  private boolean _jspx_meth_core_checkPropertyNotEquals_0(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:checkPropertyNotEquals
    net.openvpn.als.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_0 = (net.openvpn.als.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(net.openvpn.als.core.tags.CheckPropertyNotEqualsTag.class);
    _jspx_th_core_checkPropertyNotEquals_0.setPageContext(_jspx_page_context);
    _jspx_th_core_checkPropertyNotEquals_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_core_checkPropertyNotEquals_0.setRegExp(false);
    _jspx_th_core_checkPropertyNotEquals_0.setPropertyValue("");
    _jspx_th_core_checkPropertyNotEquals_0.setPropertyName("loginPage.welcomeTitle");
    int _jspx_eval_core_checkPropertyNotEquals_0 = _jspx_th_core_checkPropertyNotEquals_0.doStartTag();
    if (_jspx_eval_core_checkPropertyNotEquals_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\r\n");
        out.write("\t\t\t\t\t");
        if (_jspx_meth_core_getProperty_0(_jspx_th_core_checkPropertyNotEquals_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t");
        int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_core_checkPropertyNotEquals_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_0);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_checkPropertyNotEquals_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_0 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_0.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyNotEquals_0);
    _jspx_th_core_getProperty_0.setPropertyName("loginPage.welcomeTitle");
    int _jspx_eval_core_getProperty_0 = _jspx_th_core_getProperty_0.doStartTag();
    if (_jspx_th_core_getProperty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_propertyName_nobody.reuse(_jspx_th_core_getProperty_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_2(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_bean_message_2.setKey("login.cookieWarning");
    _jspx_th_bean_message_2.setBundle("security");
    int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
    if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
    return false;
  }

  private boolean _jspx_meth_core_checkPropertyNotEquals_1(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:checkPropertyNotEquals
    net.openvpn.als.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_1 = (net.openvpn.als.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(net.openvpn.als.core.tags.CheckPropertyNotEqualsTag.class);
    _jspx_th_core_checkPropertyNotEquals_1.setPageContext(_jspx_page_context);
    _jspx_th_core_checkPropertyNotEquals_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_core_checkPropertyNotEquals_1.setRegExp(false);
    _jspx_th_core_checkPropertyNotEquals_1.setPropertyValue("");
    _jspx_th_core_checkPropertyNotEquals_1.setPropertyName("loginPage.siteName");
    int _jspx_eval_core_checkPropertyNotEquals_1 = _jspx_th_core_checkPropertyNotEquals_1.doStartTag();
    if (_jspx_eval_core_checkPropertyNotEquals_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\r\n");
        out.write("\t\t<div id=\"siteName\">\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_core_getProperty_1(_jspx_th_core_checkPropertyNotEquals_1, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t</div>\t\r\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_core_checkPropertyNotEquals_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_1);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_1(javax.servlet.jsp.tagext.JspTag _jspx_th_core_checkPropertyNotEquals_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_1 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_1.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyNotEquals_1);
    _jspx_th_core_getProperty_1.setPropertyName("loginPage.siteName");
    int _jspx_eval_core_getProperty_1 = _jspx_th_core_getProperty_1.doStartTag();
    if (_jspx_th_core_getProperty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_propertyName_nobody.reuse(_jspx_th_core_getProperty_1);
    return false;
  }

  private boolean _jspx_meth_html_hidden_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_0 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_0.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_hidden_0.setProperty("_charset_");
    int _jspx_eval_html_hidden_0 = _jspx_th_html_hidden_0.doStartTag();
    if (_jspx_th_html_hidden_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_3(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_bean_message_3.setKey("login.disabled.java.script");
    _jspx_th_bean_message_3.setBundle("security");
    int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
    if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_3);
    return false;
  }

  private boolean _jspx_meth_bean_message_4(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_4 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_4.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_bean_message_4.setKey("login.username");
    _jspx_th_bean_message_4.setBundle("security");
    int _jspx_eval_bean_message_4 = _jspx_th_bean_message_4.doStartTag();
    if (_jspx_th_bean_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_4);
    return false;
  }

  private boolean _jspx_meth_html_text_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:text
    org.apache.struts.taglib.html.TextTag _jspx_th_html_text_0 = (org.apache.struts.taglib.html.TextTag) _jspx_tagPool_html_text_styleId_property_maxlength_nobody.get(org.apache.struts.taglib.html.TextTag.class);
    _jspx_th_html_text_0.setPageContext(_jspx_page_context);
    _jspx_th_html_text_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_text_0.setProperty("username");
    _jspx_th_html_text_0.setMaxlength("50");
    _jspx_th_html_text_0.setStyleId("password");
    int _jspx_eval_html_text_0 = _jspx_th_html_text_0.doStartTag();
    if (_jspx_th_html_text_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_text_styleId_property_maxlength_nobody.reuse(_jspx_th_html_text_0);
    return false;
  }

  private boolean _jspx_meth_html_submit_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:submit
    org.apache.struts.taglib.html.SubmitTag _jspx_th_html_submit_0 = (org.apache.struts.taglib.html.SubmitTag) _jspx_tagPool_html_submit_styleClass.get(org.apache.struts.taglib.html.SubmitTag.class);
    _jspx_th_html_submit_0.setPageContext(_jspx_page_context);
    _jspx_th_html_submit_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_submit_0.setStyleClass("ok");
    int _jspx_eval_html_submit_0 = _jspx_th_html_submit_0.doStartTag();
    if (_jspx_eval_html_submit_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_html_submit_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_html_submit_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_html_submit_0.doInitBody();
      }
      do {
        if (_jspx_meth_bean_message_5(_jspx_th_html_submit_0, _jspx_page_context))
          return true;
        int evalDoAfterBody = _jspx_th_html_submit_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_html_submit_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_html_submit_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_submit_styleClass.reuse(_jspx_th_html_submit_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_5(javax.servlet.jsp.tagext.JspTag _jspx_th_html_submit_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_5 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_5.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_html_submit_0);
    _jspx_th_bean_message_5.setKey("login.login");
    _jspx_th_bean_message_5.setBundle("security");
    int _jspx_eval_bean_message_5 = _jspx_th_bean_message_5.doStartTag();
    if (_jspx_th_bean_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_5);
    return false;
  }
}
