package org.apache.jsp.WEB_002dINF.jsp.content.properties;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.StringTokenizer;

public final class propertyContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_hidden_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_property_name_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_hidden_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_property_name_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
    _jspx_tagPool_html_hidden_property_nobody.release();
    _jspx_tagPool_logic_iterate_type_property_name_id.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_value_name_nobody.release();
    _jspx_tagPool_tiles_put_name_beanName_nobody.release();
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
      out.write("\r\n");
      out.write("\r\n");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("form");
      _jspx_th_tiles_useAttribute_0.setClassname("net.openvpn.als.properties.forms.AbstractPropertiesForm");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      net.openvpn.als.properties.forms.AbstractPropertiesForm form = null;
      form = (net.openvpn.als.properties.forms.AbstractPropertiesForm) _jspx_page_context.findAttribute("form");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("actionMapping");
      _jspx_th_tiles_useAttribute_1.setClassname("org.apache.struts.action.ActionMapping");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      org.apache.struts.action.ActionMapping actionMapping = null;
      actionMapping = (org.apache.struts.action.ActionMapping) _jspx_page_context.findAttribute("actionMapping");
      out.write(" \r\n");
      out.write("\r\n");
      out.write("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\r\n");
      out.write("\t");
 boolean propertyDisabled = !form.getEnabled(); 
      out.write("\t\r\n");
      out.write("\t");
      if (_jspx_meth_html_hidden_0(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');
      out.write('	');
      if (_jspx_meth_html_hidden_1(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');
      out.write('	');
 int idx = 0; 
      out.write('\r');
      out.write('\n');
      out.write('	');
      //  logic:iterate
      org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
      _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_iterate_0.setParent(null);
      _jspx_th_logic_iterate_0.setType("net.openvpn.als.properties.PropertyItemImpl");
      _jspx_th_logic_iterate_0.setName( actionMapping.getName() );
      _jspx_th_logic_iterate_0.setProperty("propertyItems");
      _jspx_th_logic_iterate_0.setId("propertyItem");
      int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
      if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        net.openvpn.als.properties.PropertyItemImpl propertyItem = null;
        if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_logic_iterate_0.doInitBody();
        }
        propertyItem = (net.openvpn.als.properties.PropertyItemImpl) _jspx_page_context.findAttribute("propertyItem");
        do {
          out.write("\r\n");
          out.write("\t\t");
 
			if(form.getSelectedCategory() == -1 ||
				form.getSelectedCategory() == propertyItem.getCategory()) {
		
          out.write("\t\t\r\n");
          out.write("\t\t\r\n");
          out.write("\t\t\t");
          //  tiles:insert
          org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
          _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
          _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
          _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/content/properties/propertyRow.jspf");
          _jspx_th_tiles_insert_0.setFlush(false);
          int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
          if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\r\n");
              out.write("\t\t\t\t");
              //  tiles:put
              org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_0 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_value_name_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
              _jspx_th_tiles_put_0.setPageContext(_jspx_page_context);
              _jspx_th_tiles_put_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
              _jspx_th_tiles_put_0.setName("rowIdx");
              _jspx_th_tiles_put_0.setValue( Integer.valueOf(idx) );
              int _jspx_eval_tiles_put_0 = _jspx_th_tiles_put_0.doStartTag();
              if (_jspx_th_tiles_put_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_tiles_put_value_name_nobody.reuse(_jspx_th_tiles_put_0);
              out.write("\r\n");
              out.write("\t\t\t\t");
              if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t");
              if (_jspx_meth_tiles_put_2(_jspx_th_tiles_insert_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t");
              //  tiles:put
              org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_3 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_value_name_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
              _jspx_th_tiles_put_3.setPageContext(_jspx_page_context);
              _jspx_th_tiles_put_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
              _jspx_th_tiles_put_3.setName("propertyDisabled");
              _jspx_th_tiles_put_3.setValue( Boolean.valueOf(propertyDisabled) );
              int _jspx_eval_tiles_put_3 = _jspx_th_tiles_put_3.doStartTag();
              if (_jspx_th_tiles_put_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_tiles_put_value_name_nobody.reuse(_jspx_th_tiles_put_3);
              out.write("\r\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_tiles_insert_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_tiles_insert_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_tiles_insert_page_flush.reuse(_jspx_th_tiles_insert_0);
          out.write("\r\n");
          out.write("\t\t");
 	idx++;
		} 
          out.write('\r');
          out.write('\n');
          out.write('	');
          int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
          propertyItem = (net.openvpn.als.properties.PropertyItemImpl) _jspx_page_context.findAttribute("propertyItem");
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_logic_iterate_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_iterate_type_property_name_id.reuse(_jspx_th_logic_iterate_0);
      out.write("\t\r\n");
      out.write("</table>\r\n");
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

  private boolean _jspx_meth_html_hidden_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_0 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_0.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_0.setParent(null);
    _jspx_th_html_hidden_0.setProperty("referer");
    int _jspx_eval_html_hidden_0 = _jspx_th_html_hidden_0.doStartTag();
    if (_jspx_th_html_hidden_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_0);
    return false;
  }

  private boolean _jspx_meth_html_hidden_1(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_1 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_1.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_1.setParent(null);
    _jspx_th_html_hidden_1.setProperty("_charset_");
    int _jspx_eval_html_hidden_1 = _jspx_th_html_hidden_1.doStartTag();
    if (_jspx_th_html_hidden_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_1);
    return false;
  }

  private boolean _jspx_meth_tiles_put_1(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_1 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_1.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_1.setName("propertyItem");
    _jspx_th_tiles_put_1.setBeanName("propertyItem");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }

  private boolean _jspx_meth_tiles_put_2(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_2 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_2.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_2.setName("form");
    _jspx_th_tiles_put_2.setBeanName("form");
    int _jspx_eval_tiles_put_2 = _jspx_th_tiles_put_2.doStartTag();
    if (_jspx_th_tiles_put_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_2);
    return false;
  }
}
