package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.core.CoreScript;

public final class pageHeader_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
    _jspx_dependants.add("/WEB-INF/core.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_themePath_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_pageScripts_position_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_themePath_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_pageScripts_position_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_core_themePath_nobody.release();
    _jspx_tagPool_core_pageScripts_position_nobody.release();
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
      out.write("<!-- Page header -->\r\n");
      out.write("<head>\r\n");
      out.write("\t");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("resourcePrefix");
      _jspx_th_tiles_useAttribute_0.setScope("request");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String resourcePrefix = null;
      resourcePrefix = (java.lang.String) _jspx_page_context.findAttribute("resourcePrefix");
      out.write('\r');
      out.write('\n');
      out.write('	');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("resourceBundle");
      _jspx_th_tiles_useAttribute_1.setScope("request");
      _jspx_th_tiles_useAttribute_1.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      java.lang.String resourceBundle = null;
      resourceBundle = (java.lang.String) _jspx_page_context.findAttribute("resourceBundle");
      out.write("\r\n");
      out.write("\t<title>");
      //  bean:message
      org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
      _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
      _jspx_th_bean_message_0.setParent(null);
      _jspx_th_bean_message_0.setKey( resourcePrefix + ".title" );
      _jspx_th_bean_message_0.setBundle( resourceBundle );
      int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
      if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
      out.write("</title>\t\r\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href='");
      if (_jspx_meth_core_themePath_0(_jspx_page_context))
        return;
      out.write("/style.jsp'/>\t\t\r\n");
      out.write("\t");
      //  core:pageScripts
      net.openvpn.als.core.tags.PageScriptsTag _jspx_th_core_pageScripts_0 = (net.openvpn.als.core.tags.PageScriptsTag) _jspx_tagPool_core_pageScripts_position_nobody.get(net.openvpn.als.core.tags.PageScriptsTag.class);
      _jspx_th_core_pageScripts_0.setPageContext(_jspx_page_context);
      _jspx_th_core_pageScripts_0.setParent(null);
      _jspx_th_core_pageScripts_0.setPosition( String.valueOf(CoreScript.PAGE_HEADER) );
      int _jspx_eval_core_pageScripts_0 = _jspx_th_core_pageScripts_0.doStartTag();
      if (_jspx_th_core_pageScripts_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_core_pageScripts_position_nobody.reuse(_jspx_th_core_pageScripts_0);
      out.write("\t\r\n");
      out.write("</head>");
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

  private boolean _jspx_meth_core_themePath_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:themePath
    net.openvpn.als.core.tags.ThemePathTag _jspx_th_core_themePath_0 = (net.openvpn.als.core.tags.ThemePathTag) _jspx_tagPool_core_themePath_nobody.get(net.openvpn.als.core.tags.ThemePathTag.class);
    _jspx_th_core_themePath_0.setPageContext(_jspx_page_context);
    _jspx_th_core_themePath_0.setParent(null);
    int _jspx_eval_core_themePath_0 = _jspx_th_core_themePath_0.doStartTag();
    if (_jspx_th_core_themePath_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_themePath_nobody.reuse(_jspx_th_core_themePath_0);
    return false;
  }
}
