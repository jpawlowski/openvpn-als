package org.apache.jsp.WEB_002dINF.jsp.content.security;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.adito.security.Constants;
import com.adito.security.AuthenticationScheme;
import com.adito.core.CoreUtil;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import org.apache.struts.taglib.TagUtils;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;

public final class loginContent_jspf extends org.apache.jasper.runtime.HttpJspBase
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
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEqual_value_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_equal_value_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_define_type_name_id_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_arg0_nobody;

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
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEqual_value_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_equal_value_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_define_type_name_id_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_arg0_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
    _jspx_tagPool_input_frame_styleId.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.release();
    _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.release();
    _jspx_tagPool_core_getProperty_propertyName_nobody.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.release();
    _jspx_tagPool_logic_notEqual_value_property_name.release();
    _jspx_tagPool_logic_equal_value_property_name.release();
    _jspx_tagPool_bean_define_type_name_id_nobody.release();
    _jspx_tagPool_bean_message_key_bundle_arg0_nobody.release();
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

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<div id=\"page_login\">\n");
      out.write("\t");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
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
      out.write("\n");
      out.write("\n");
      out.write("<!-- If you change this file don't forget to change the one in Brandless Logon as well -->\n");
      out.write("\n");
      out.write("<div id=\"login_page_info\">\n");
      //  input:frame
      com.adito.input.tags.FrameTag _jspx_th_input_frame_0 = (com.adito.input.tags.FrameTag) _jspx_tagPool_input_frame_styleId.get(com.adito.input.tags.FrameTag.class);
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
          out.write("\n");
          out.write("\t<table align=\"center\">\n");
          out.write("\t\t<tr align=\"center\">\n");
          out.write("\t\t\t<td valign=\"top\">\n");
          out.write("\t        ");
 String welcomeLogo = Property.getProperty(new SystemConfigKey("loginPage.welcomeLogo"));
	           if(welcomeLogo.equals("default")) {
	           	   welcomeLogo = CoreUtil.getThemePath(session) + "/images/info/" + infoImage;
	       	
          out.write("\n");
          out.write("\t       \t\t<img class=\"infoImage\" src=\"");
          out.print( welcomeLogo );
          out.write("\" />\n");
          out.write("\t\t\t</td>\n");
          out.write("\t       \t");
 
	           } 
	           else {
	           		welcomeLogo = "/icons/" + welcomeLogo;
	       	
          out.write("\n");
          out.write("\t\t\t\t<img class=\"customLoginInfoImage\" src=\"");
          out.print( welcomeLogo );
          out.write("\" />\n");
          out.write("\t\t\t</td>\n");
          out.write("\t\t</tr>\n");
          out.write("\t\t<tr align=\"center\">\n");
          out.write("\t       \t");
 }
          out.write("\n");
          out.write("\t\t\t<td>\n");
          out.write("\t\t\t\t<h1>\t\t\t\t\t\n");
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
          out.write("\t\n");
          out.write("\t\t\t\t</h1>\n");
          out.write("\t\t\t\t");
          //  core:checkPropertyEquals
          com.adito.core.tags.CheckPropertyEqualsTag _jspx_th_core_checkPropertyEquals_0 = (com.adito.core.tags.CheckPropertyEqualsTag) _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyEqualsTag.class);
          _jspx_th_core_checkPropertyEquals_0.setPageContext(_jspx_page_context);
          _jspx_th_core_checkPropertyEquals_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_core_checkPropertyEquals_0.setRegExp(false);
          _jspx_th_core_checkPropertyEquals_0.setPropertyValue("");
          _jspx_th_core_checkPropertyEquals_0.setPropertyName("loginPage.welcomeTitle");
          int _jspx_eval_core_checkPropertyEquals_0 = _jspx_th_core_checkPropertyEquals_0.doStartTag();
          if (_jspx_eval_core_checkPropertyEquals_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\t\n");
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
              out.write("\n");
              out.write("\t\t\t\t");
              int evalDoAfterBody = _jspx_th_core_checkPropertyEquals_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_core_checkPropertyEquals_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyEquals_0);
          out.write("\t\t\t\t\t\n");
          out.write("\t\t\t\t");
          if (_jspx_meth_core_checkPropertyNotEquals_0(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write("\t\t\t\t\n");
          out.write("\t\t\t</td>\n");
          out.write("\t\t</tr>\n");
          out.write("\t</table>\t\n");
          out.write("\t<script language=\"javascript1.1\" type=\"text/javascript\">\n");
          out.write("\t<!--\n");
          out.write("\t\tif (!document.cookie) {\n");
          out.write("\t\t\tdocument.writeln(\"<span class='smallText'><p align='center' class='warningText'>");
          if (_jspx_meth_bean_message_2(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write("</p></span>\");\n");
          out.write("\t\t\tdocument.writeln(\"<br/>\");\n");
          out.write("\t\t}\t\t \n");
          out.write("\t// -->\n");
          out.write("\t</script>\t\n");
          out.write("\t");
          if (_jspx_meth_core_checkPropertyNotEquals_1(_jspx_th_input_frame_0, _jspx_page_context))
            return;
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
      out.write("\n");
      out.write("</div>");
      out.write("\n");
      out.write("\t<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\t\n");
      out.write("\t\t<tr>\n");
      out.write("\t\t\t<td align=\"center\" >\n");
      out.write("\t\t\t\n");
      out.write("\t\t\t\t");
      //  tiles:insert
      org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
      _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_insert_0.setParent(null);
      _jspx_th_tiles_insert_0.setPage( ((AuthenticationScheme)session.getAttribute(Constants.AUTH_SESSION)).currentAuthenticationModule().getInclude() );
      _jspx_th_tiles_insert_0.setFlush(false);
      int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
      if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("                  \n");
          out.write("\t\t\t\t  \t");
          if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t\t\t\t  \t");
          if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t\t\t\t");
          int evalDoAfterBody = _jspx_th_tiles_insert_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_tiles_insert_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_insert_page_flush.reuse(_jspx_th_tiles_insert_0);
      out.write("\n");
      out.write("\t\t\t</td>\n");
      out.write("\t\t</tr>\n");
      out.write("\t\t");
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
          out.write(" \n");
          out.write("\t\t\t");
          //  logic:notEqual
          org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_1 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_property_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
          _jspx_th_logic_notEqual_1.setPageContext(_jspx_page_context);
          _jspx_th_logic_notEqual_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_0);
          _jspx_th_logic_notEqual_1.setName("logonForm");
          _jspx_th_logic_notEqual_1.setProperty("hasMoreAuthenticationSchemes");
          _jspx_th_logic_notEqual_1.setValue("false");
          int _jspx_eval_logic_notEqual_1 = _jspx_th_logic_notEqual_1.doStartTag();
          if (_jspx_eval_logic_notEqual_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write(" \t\t        \t\t\n");
              out.write("\t\t\t \t");
              //  logic:equal
              org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_0 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_property_name.get(org.apache.struts.taglib.logic.EqualTag.class);
              _jspx_th_logic_equal_0.setPageContext(_jspx_page_context);
              _jspx_th_logic_equal_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_1);
              _jspx_th_logic_equal_0.setName("authSession");
              _jspx_th_logic_equal_0.setProperty("currentModuleIndex");
              _jspx_th_logic_equal_0.setValue("0");
              int _jspx_eval_logic_equal_0 = _jspx_th_logic_equal_0.doStartTag();
              if (_jspx_eval_logic_equal_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n");
                  out.write("\t\t\t \t\t");
                  //  bean:define
                  org.apache.struts.taglib.bean.DefineTag _jspx_th_bean_define_0 = (org.apache.struts.taglib.bean.DefineTag) _jspx_tagPool_bean_define_type_name_id_nobody.get(org.apache.struts.taglib.bean.DefineTag.class);
                  _jspx_th_bean_define_0.setPageContext(_jspx_page_context);
                  _jspx_th_bean_define_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
                  _jspx_th_bean_define_0.setId("logonForm");
                  _jspx_th_bean_define_0.setName("logonForm");
                  _jspx_th_bean_define_0.setType("com.adito.security.forms.LogonForm");
                  int _jspx_eval_bean_define_0 = _jspx_th_bean_define_0.doStartTag();
                  if (_jspx_th_bean_define_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_bean_define_type_name_id_nobody.reuse(_jspx_th_bean_define_0);
                  com.adito.security.forms.LogonForm logonForm = null;
                  logonForm = (com.adito.security.forms.LogonForm) _jspx_page_context.findAttribute("logonForm");
                  out.write("\n");
                  out.write("\t\t\t\t\t<tr>\n");
                  out.write("\t\t\t\t\t\t<td height=\"8\"/>\n");
                  out.write("\t\t\t\t\t</tr>\n");
                  out.write("\t\t\t\t\t<tr>\n");
                  out.write("\t\t\t\t\t\t<td align=\"center\" >\n");
                  out.write("\t\t\t\t\t\t\t <table width=\"100%\" border=\"0\">\n");
                  out.write("\t\t\t\t\t\t\t \t<tr>\n");
                  out.write("\t\t\t\t\t\t\t \t\t<td align=\"center\" class=\"smallText\">\n");
                  out.write("\t\t\t\t\t\t\t             ");
                  //  bean:message
                  org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_arg0_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                  _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
                  _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
                  _jspx_th_bean_message_3.setKey("login.selectAuthenticationScheme");
                  _jspx_th_bean_message_3.setBundle("security");
                  _jspx_th_bean_message_3.setArg0( logonForm.getUsername() == null ? "" : Util.urlEncode(logonForm.getUsername()) );
                  int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
                  if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_bean_message_key_bundle_arg0_nobody.reuse(_jspx_th_bean_message_3);
                  out.write("            \n");
                  out.write("\t\t\t\t\t\t\t        </td>\n");
                  out.write("\t\t\t\t\t\t\t    </tr>\n");
                  out.write("\t\t\t\t\t\t\t  </table>\n");
                  out.write("\t\t\t\t\t\t</td>\n");
                  out.write("\t\t\t\t\t</tr>\n");
                  out.write("\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_logic_equal_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_logic_equal_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_logic_equal_value_property_name.reuse(_jspx_th_logic_equal_0);
              out.write("\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_notEqual_1.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_logic_notEqual_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_1);
          out.write('\n');
          out.write('	');
          out.write('	');
          int evalDoAfterBody = _jspx_th_logic_notEqual_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEqual_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_0);
      out.write("\n");
      out.write("\t</table>\n");
      out.write("\t");
      //  logic:notEqual
      org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_2 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_property_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
      _jspx_th_logic_notEqual_2.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEqual_2.setParent(null);
      _jspx_th_logic_notEqual_2.setName("logonForm");
      _jspx_th_logic_notEqual_2.setProperty("sessionLocked");
      _jspx_th_logic_notEqual_2.setValue("true");
      int _jspx_eval_logic_notEqual_2 = _jspx_th_logic_notEqual_2.doStartTag();
      if (_jspx_eval_logic_notEqual_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write(" \n");
          out.write("\t\t");
          //  core:checkPropertyNotEquals
          com.adito.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_2 = (com.adito.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyNotEqualsTag.class);
          _jspx_th_core_checkPropertyNotEquals_2.setPageContext(_jspx_page_context);
          _jspx_th_core_checkPropertyNotEquals_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_2);
          _jspx_th_core_checkPropertyNotEquals_2.setRegExp(false);
          _jspx_th_core_checkPropertyNotEquals_2.setPropertyValue("none");
          _jspx_th_core_checkPropertyNotEquals_2.setPropertyName("loginPage.welcomeMessageType");
          int _jspx_eval_core_checkPropertyNotEquals_2 = _jspx_th_core_checkPropertyNotEquals_2.doStartTag();
          if (_jspx_eval_core_checkPropertyNotEquals_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n");
              out.write("\t\t\t<div id=\"welcomeMessage\">\n");
              out.write("\t\t\t\t<table class=\"dialog_form_table\">\n");
              out.write("\t\t\t\t\t\t");
              //  core:checkPropertyNotEquals
              com.adito.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_3 = (com.adito.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyNotEqualsTag.class);
              _jspx_th_core_checkPropertyNotEquals_3.setPageContext(_jspx_page_context);
              _jspx_th_core_checkPropertyNotEquals_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyNotEquals_2);
              _jspx_th_core_checkPropertyNotEquals_3.setRegExp(false);
              _jspx_th_core_checkPropertyNotEquals_3.setPropertyValue("noicon");
              _jspx_th_core_checkPropertyNotEquals_3.setPropertyName("loginPage.welcomeMessageType");
              int _jspx_eval_core_checkPropertyNotEquals_3 = _jspx_th_core_checkPropertyNotEquals_3.doStartTag();
              if (_jspx_eval_core_checkPropertyNotEquals_3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\n");
                  out.write("\t\t\t\t\t\t\t<tr class=\"formBlock\"> \n");
                  out.write("\t\t\t\t\t\t\t\t<td class=\"label\">\n");
                  out.write("\t\t\t\t\t\t\t\t\t<img src=\"");
                  out.print( CoreUtil.getThemePath(session) + "/images/dialog/" + Property.getProperty(new SystemConfigKey("loginPage.welcomeMessageType")) + ".gif" );
                  out.write("\"/>\n");
                  out.write("\t\t\t\t\t\t\t\t</td>\n");
                  out.write("\t\t\t\t\t\t\t</tr>\n");
                  out.write("\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_3.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_core_checkPropertyNotEquals_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_3);
              out.write("\n");
              out.write("\t\t\t\t\t\t<tr class=\"formBlock\">\n");
              out.write("\t\t\t\t\t\t\t<td class=\"");
              out.print( Property.getProperty(new SystemConfigKey("loginPage.welcomeMessageAlign")) );
              out.write("\">     \n");
              out.write("\t\t\t\t\t\t\t\t");
              out.print( TagUtils.getInstance().filter(Property.getProperty(new SystemConfigKey("loginPage.welcomeMessage"))) );
              out.write("\n");
              out.write("\t\t\t\t\t\t\t</td>\n");
              out.write("\t\t\t\t\t\t</tr>\n");
              out.write("\t\t\t\t</table>\n");
              out.write("\n");
              out.write("\t\t\t</div>\n");
              out.write("\t\t");
              int evalDoAfterBody = _jspx_th_core_checkPropertyNotEquals_2.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_core_checkPropertyNotEquals_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyNotEquals_2);
          out.write('\n');
          out.write('	');
          int evalDoAfterBody = _jspx_th_logic_notEqual_2.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEqual_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEqual_value_property_name.reuse(_jspx_th_logic_notEqual_2);
      out.write("\n");
      out.write("</div>");
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
    com.adito.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_0 = (com.adito.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyNotEqualsTag.class);
    _jspx_th_core_checkPropertyNotEquals_0.setPageContext(_jspx_page_context);
    _jspx_th_core_checkPropertyNotEquals_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_core_checkPropertyNotEquals_0.setRegExp(false);
    _jspx_th_core_checkPropertyNotEquals_0.setPropertyValue("");
    _jspx_th_core_checkPropertyNotEquals_0.setPropertyName("loginPage.welcomeTitle");
    int _jspx_eval_core_checkPropertyNotEquals_0 = _jspx_th_core_checkPropertyNotEquals_0.doStartTag();
    if (_jspx_eval_core_checkPropertyNotEquals_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\n");
        out.write("\t\t\t\t\t");
        if (_jspx_meth_core_getProperty_0(_jspx_th_core_checkPropertyNotEquals_0, _jspx_page_context))
          return true;
        out.write("\n");
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
    com.adito.core.tags.GetPropertyTag _jspx_th_core_getProperty_0 = (com.adito.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_propertyName_nobody.get(com.adito.core.tags.GetPropertyTag.class);
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
    com.adito.core.tags.CheckPropertyNotEqualsTag _jspx_th_core_checkPropertyNotEquals_1 = (com.adito.core.tags.CheckPropertyNotEqualsTag) _jspx_tagPool_core_checkPropertyNotEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyNotEqualsTag.class);
    _jspx_th_core_checkPropertyNotEquals_1.setPageContext(_jspx_page_context);
    _jspx_th_core_checkPropertyNotEquals_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_core_checkPropertyNotEquals_1.setRegExp(false);
    _jspx_th_core_checkPropertyNotEquals_1.setPropertyValue("");
    _jspx_th_core_checkPropertyNotEquals_1.setPropertyName("loginPage.siteName");
    int _jspx_eval_core_checkPropertyNotEquals_1 = _jspx_th_core_checkPropertyNotEquals_1.doStartTag();
    if (_jspx_eval_core_checkPropertyNotEquals_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\n");
        out.write("\t\t<div id=\"siteName\">\n");
        out.write("\t\t\t");
        if (_jspx_meth_core_getProperty_1(_jspx_th_core_checkPropertyNotEquals_1, _jspx_page_context))
          return true;
        out.write("\n");
        out.write("\t\t</div>\t\n");
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
    com.adito.core.tags.GetPropertyTag _jspx_th_core_getProperty_1 = (com.adito.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_propertyName_nobody.get(com.adito.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_1.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyNotEquals_1);
    _jspx_th_core_getProperty_1.setPropertyName("loginPage.siteName");
    int _jspx_eval_core_getProperty_1 = _jspx_th_core_getProperty_1.doStartTag();
    if (_jspx_th_core_getProperty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_propertyName_nobody.reuse(_jspx_th_core_getProperty_1);
    return false;
  }

  private boolean _jspx_meth_tiles_put_0(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_0 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_0.setName("sessionLockUser");
    _jspx_th_tiles_put_0.setBeanName("sessionObj");
    _jspx_th_tiles_put_0.setBeanProperty("attribute(sessionLocked)");
    int _jspx_eval_tiles_put_0 = _jspx_th_tiles_put_0.doStartTag();
    if (_jspx_th_tiles_put_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.reuse(_jspx_th_tiles_put_0);
    return false;
  }

  private boolean _jspx_meth_tiles_put_1(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_1 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_1.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_1.setName("authSession");
    _jspx_th_tiles_put_1.setBeanName("sessionObj");
    _jspx_th_tiles_put_1.setBeanProperty("attribute(authSession)");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }
}
