package org.apache.jsp.WEB_002dINF.theme.default_;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class actionLink_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_define_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_themePath_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEqual_value_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_styleId_onclick_key_href_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_equal_value_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_styleId_showToolTip_onclick_key_bundle;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_define_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_themePath_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEqual_value_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_styleId_onclick_key_href_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_equal_value_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_styleId_showToolTip_onclick_key_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.release();
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_bean_define_id.release();
    _jspx_tagPool_core_themePath_nobody.release();
    _jspx_tagPool_logic_notEqual_value_name.release();
    _jspx_tagPool_input_toolTip_styleId_onclick_key_href_bundle.release();
    _jspx_tagPool_logic_equal_value_name.release();
    _jspx_tagPool_input_toolTip_styleId_showToolTip_onclick_key_bundle.release();
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
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("actionOnclick");
      _jspx_th_tiles_useAttribute_0.setScope("request");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      _jspx_th_tiles_useAttribute_0.setIgnore(true);
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String actionOnclick = null;
      actionOnclick = (java.lang.String) _jspx_page_context.findAttribute("actionOnclick");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("actionPath");
      _jspx_th_tiles_useAttribute_1.setScope("request");
      _jspx_th_tiles_useAttribute_1.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      java.lang.String actionPath = null;
      actionPath = (java.lang.String) _jspx_page_context.findAttribute("actionPath");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_2 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_2.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_2.setParent(null);
      _jspx_th_tiles_useAttribute_2.setName("nameKey");
      _jspx_th_tiles_useAttribute_2.setScope("request");
      _jspx_th_tiles_useAttribute_2.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_2 = _jspx_th_tiles_useAttribute_2.doStartTag();
      if (_jspx_th_tiles_useAttribute_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_2);
      java.lang.String nameKey = null;
      nameKey = (java.lang.String) _jspx_page_context.findAttribute("nameKey");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_3 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_3.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_3.setParent(null);
      _jspx_th_tiles_useAttribute_3.setName("descriptionKey");
      _jspx_th_tiles_useAttribute_3.setScope("request");
      _jspx_th_tiles_useAttribute_3.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_3 = _jspx_th_tiles_useAttribute_3.doStartTag();
      if (_jspx_th_tiles_useAttribute_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_3);
      java.lang.String descriptionKey = null;
      descriptionKey = (java.lang.String) _jspx_page_context.findAttribute("descriptionKey");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_4 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_4.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_4.setParent(null);
      _jspx_th_tiles_useAttribute_4.setName("bundle");
      _jspx_th_tiles_useAttribute_4.setScope("request");
      _jspx_th_tiles_useAttribute_4.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_4 = _jspx_th_tiles_useAttribute_4.doStartTag();
      if (_jspx_th_tiles_useAttribute_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_4);
      java.lang.String bundle = null;
      bundle = (java.lang.String) _jspx_page_context.findAttribute("bundle");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_5 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_5.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_5.setParent(null);
      _jspx_th_tiles_useAttribute_5.setName("actionName");
      _jspx_th_tiles_useAttribute_5.setScope("request");
      _jspx_th_tiles_useAttribute_5.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_5 = _jspx_th_tiles_useAttribute_5.doStartTag();
      if (_jspx_th_tiles_useAttribute_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_5);
      java.lang.String actionName = null;
      actionName = (java.lang.String) _jspx_page_context.findAttribute("actionName");
      out.write(" \r\n");
      out.write("\r\n");
      //  bean:define
      org.apache.struts.taglib.bean.DefineTag _jspx_th_bean_define_0 = (org.apache.struts.taglib.bean.DefineTag) _jspx_tagPool_bean_define_id.get(org.apache.struts.taglib.bean.DefineTag.class);
      _jspx_th_bean_define_0.setPageContext(_jspx_page_context);
      _jspx_th_bean_define_0.setParent(null);
      _jspx_th_bean_define_0.setId("themePath");
      int _jspx_eval_bean_define_0 = _jspx_th_bean_define_0.doStartTag();
      if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_bean_define_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_bean_define_0.doInitBody();
        }
        do {
          if (_jspx_meth_core_themePath_0(_jspx_th_bean_define_0, _jspx_page_context))
            return;
          int evalDoAfterBody = _jspx_th_bean_define_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_bean_define_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_define_id.reuse(_jspx_th_bean_define_0);
      java.lang.String themePath = null;
      themePath = (java.lang.String) _jspx_page_context.findAttribute("themePath");
      out.write('\r');
      out.write('\n');
      //  logic:notEqual
      org.apache.struts.taglib.logic.NotEqualTag _jspx_th_logic_notEqual_0 = (org.apache.struts.taglib.logic.NotEqualTag) _jspx_tagPool_logic_notEqual_value_name.get(org.apache.struts.taglib.logic.NotEqualTag.class);
      _jspx_th_logic_notEqual_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEqual_0.setParent(null);
      _jspx_th_logic_notEqual_0.setName("descriptionKey");
      _jspx_th_logic_notEqual_0.setValue("blank.description");
      int _jspx_eval_logic_notEqual_0 = _jspx_th_logic_notEqual_0.doStartTag();
      if (_jspx_eval_logic_notEqual_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\r');
          out.write('\n');
          out.write('	');
          //  input:toolTip
          net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_styleId_onclick_key_href_bundle.get(net.openvpn.als.input.tags.ToolTipTag.class);
          _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
          _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEqual_0);
          _jspx_th_input_toolTip_0.setHref( actionPath );
          _jspx_th_input_toolTip_0.setKey( descriptionKey );
          _jspx_th_input_toolTip_0.setBundle( bundle );
          _jspx_th_input_toolTip_0.setStyleId( "action_" + actionName );
          _jspx_th_input_toolTip_0.setOnclick( actionOnclick );
          int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
          if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_toolTip_0.doInitBody();
            }
            do {
              out.write("\r\n");
              out.write("\t\t<img alt=\"\" src=\"");
              out.print( themePath  + "/images/actions/" + actionName + ".gif" );
              out.write("\"/>\r\n");
              out.write("\t");
              int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_input_toolTip_styleId_onclick_key_href_bundle.reuse(_jspx_th_input_toolTip_0);
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_logic_notEqual_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEqual_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEqual_value_name.reuse(_jspx_th_logic_notEqual_0);
      out.write("\r\n");
      out.write("\r\n");
      //  logic:equal
      org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_0 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_name.get(org.apache.struts.taglib.logic.EqualTag.class);
      _jspx_th_logic_equal_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_equal_0.setParent(null);
      _jspx_th_logic_equal_0.setName("descriptionKey");
      _jspx_th_logic_equal_0.setValue("blank.description");
      int _jspx_eval_logic_equal_0 = _jspx_th_logic_equal_0.doStartTag();
      if (_jspx_eval_logic_equal_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\r');
          out.write('\n');
          out.write('	');
          //  input:toolTip
          net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_styleId_showToolTip_onclick_key_bundle.get(net.openvpn.als.input.tags.ToolTipTag.class);
          _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
          _jspx_th_input_toolTip_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
          _jspx_th_input_toolTip_1.setShowToolTip(true);
          _jspx_th_input_toolTip_1.setKey( descriptionKey );
          _jspx_th_input_toolTip_1.setBundle( bundle );
          _jspx_th_input_toolTip_1.setStyleId( "action_" + actionName );
          _jspx_th_input_toolTip_1.setOnclick( actionOnclick );
          int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
          if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_toolTip_1.doInitBody();
            }
            do {
              out.write("\r\n");
              out.write("\t\t<img alt=\"\" src=\"");
              out.print( themePath  + "/images/actions/" + actionName + ".gif" );
              out.write("\"/>\r\n");
              out.write("\t");
              int evalDoAfterBody = _jspx_th_input_toolTip_1.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_input_toolTip_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_input_toolTip_styleId_showToolTip_onclick_key_bundle.reuse(_jspx_th_input_toolTip_1);
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_logic_equal_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_equal_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_equal_value_name.reuse(_jspx_th_logic_equal_0);
      out.write("\r\n");
      out.write("\r\n");
 actionOnclick = "";
      out.write('\r');
      out.write('\n');
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

  private boolean _jspx_meth_core_themePath_0(javax.servlet.jsp.tagext.JspTag _jspx_th_bean_define_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:themePath
    net.openvpn.als.core.tags.ThemePathTag _jspx_th_core_themePath_0 = (net.openvpn.als.core.tags.ThemePathTag) _jspx_tagPool_core_themePath_nobody.get(net.openvpn.als.core.tags.ThemePathTag.class);
    _jspx_th_core_themePath_0.setPageContext(_jspx_page_context);
    _jspx_th_core_themePath_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_bean_define_0);
    int _jspx_eval_core_themePath_0 = _jspx_th_core_themePath_0.doStartTag();
    if (_jspx_th_core_themePath_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_themePath_nobody.reuse(_jspx_th_core_themePath_0);
    return false;
  }
}
