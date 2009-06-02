package org.apache.jsp.WEB_002dINF.jsp.content.properties;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.properties.PropertyItem;

public final class propertyLabel_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_classname_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
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
      out.write(" \t\t       \r\n");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("propertyItem");
      _jspx_th_tiles_useAttribute_0.setClassname("net.openvpn.als.properties.PropertyItem");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      net.openvpn.als.properties.PropertyItem propertyItem = null;
      propertyItem = (net.openvpn.als.properties.PropertyItem) _jspx_page_context.findAttribute("propertyItem");
      out.write(" \r\n");
      out.write("<a href=\"#\"\tonclick=\"javascript: this.blur(); windowRef = window.open('/help.do?source=property&propertyClass=");
      out.print( propertyItem.getDefinition().getPropertyClass().getName() );
      out.write("&name=");
      out.print( propertyItem.getName() );
      out.write("','help_win','left=20,top=20,width=400,height=480,toolbar=0,resizable=1,menubar=0,scrollbars=1'); windowRef.focus(); return false\"\r\n");
      out.write("\thref=\"#\">\r\n");
      out.write("\t<div class=\"helpIndicator\">\r\n");
      out.write("\t\t");
      out.print( propertyItem.getLabel() );
      out.write("\r\n");
      out.write("\t</div>\r\n");
      out.write("</a>\r\n");
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
}
