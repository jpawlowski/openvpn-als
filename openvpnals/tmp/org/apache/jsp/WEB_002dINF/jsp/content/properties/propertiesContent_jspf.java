package org.apache.jsp.WEB_002dINF.jsp.content.properties;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.properties.forms.AbstractPropertiesForm;

public final class propertiesContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
    _jspx_dependants.add("/WEB-INF/core.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_define_type_name_id_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_form_autocomplete_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_bean_define_type_name_id_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_form_autocomplete_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_bean_define_type_name_id_nobody.release();
    _jspx_tagPool_core_form_autocomplete_action.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
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
      out.write("\t\r\n");
      out.write("<div id=\"page_properties\">\r\n");
      out.write("\t");
      //  bean:define
      org.apache.struts.taglib.bean.DefineTag _jspx_th_bean_define_0 = (org.apache.struts.taglib.bean.DefineTag) _jspx_tagPool_bean_define_type_name_id_nobody.get(org.apache.struts.taglib.bean.DefineTag.class);
      _jspx_th_bean_define_0.setPageContext(_jspx_page_context);
      _jspx_th_bean_define_0.setParent(null);
      _jspx_th_bean_define_0.setId("form");
      _jspx_th_bean_define_0.setName("form");
      _jspx_th_bean_define_0.setType("net.openvpn.als.properties.forms.AbstractPropertiesForm");
      int _jspx_eval_bean_define_0 = _jspx_th_bean_define_0.doStartTag();
      if (_jspx_th_bean_define_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_define_type_name_id_nobody.reuse(_jspx_th_bean_define_0);
      net.openvpn.als.properties.forms.AbstractPropertiesForm form = null;
      form = (net.openvpn.als.properties.forms.AbstractPropertiesForm) _jspx_page_context.findAttribute("form");
      out.write('\r');
      out.write('\n');
      out.write('	');
      //  bean:define
      org.apache.struts.taglib.bean.DefineTag _jspx_th_bean_define_1 = (org.apache.struts.taglib.bean.DefineTag) _jspx_tagPool_bean_define_type_name_id_nobody.get(org.apache.struts.taglib.bean.DefineTag.class);
      _jspx_th_bean_define_1.setPageContext(_jspx_page_context);
      _jspx_th_bean_define_1.setParent(null);
      _jspx_th_bean_define_1.setId("actionMapping");
      _jspx_th_bean_define_1.setName("actionMapping");
      _jspx_th_bean_define_1.setType("org.apache.struts.action.ActionMapping");
      int _jspx_eval_bean_define_1 = _jspx_th_bean_define_1.doStartTag();
      if (_jspx_th_bean_define_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_define_type_name_id_nobody.reuse(_jspx_th_bean_define_1);
      org.apache.struts.action.ActionMapping actionMapping = null;
      actionMapping = (org.apache.struts.action.ActionMapping) _jspx_page_context.findAttribute("actionMapping");
      out.write('\r');
      out.write('\n');
      out.write('	');
      //  core:form
      net.openvpn.als.core.tags.FormTag _jspx_th_core_form_0 = (net.openvpn.als.core.tags.FormTag) _jspx_tagPool_core_form_autocomplete_action.get(net.openvpn.als.core.tags.FormTag.class);
      _jspx_th_core_form_0.setPageContext(_jspx_page_context);
      _jspx_th_core_form_0.setParent(null);
      _jspx_th_core_form_0.setAction( String.valueOf(((AbstractPropertiesForm)form).getUpdateAction()) );
      _jspx_th_core_form_0.setAutocomplete("OFF");
      int _jspx_eval_core_form_0 = _jspx_th_core_form_0.doStartTag();
      if (_jspx_eval_core_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\t\t              \t\t\t\r\n");
          out.write("\t\t");
          if (_jspx_meth_tiles_insert_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write('\r');
          out.write('\n');
          out.write('	');
          int evalDoAfterBody = _jspx_th_core_form_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_core_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_core_form_autocomplete_action.reuse(_jspx_th_core_form_0);
      out.write("\r\n");
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

  private boolean _jspx_meth_tiles_insert_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/content/properties/propertyFormContent.jspf");
    _jspx_th_tiles_insert_0.setFlush(false);
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t");
        int evalDoAfterBody = _jspx_th_tiles_insert_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_tiles_insert_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_insert_page_flush.reuse(_jspx_th_tiles_insert_0);
    return false;
  }

  private boolean _jspx_meth_tiles_put_0(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_0 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_0.setName("form");
    _jspx_th_tiles_put_0.setBeanName("form");
    int _jspx_eval_tiles_put_0 = _jspx_th_tiles_put_0.doStartTag();
    if (_jspx_th_tiles_put_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_0);
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
    _jspx_th_tiles_put_1.setName("actionMapping");
    _jspx_th_tiles_put_1.setBeanName("actionMapping");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }
}
