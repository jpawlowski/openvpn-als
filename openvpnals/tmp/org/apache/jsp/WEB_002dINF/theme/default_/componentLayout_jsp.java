package org.apache.jsp.WEB_002dINF.theme.default_;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.StringWriter;
import java.io.PrintWriter;

public final class componentLayout_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_page_property_id_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_flush_attribute_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_bean_page_property_id_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_flush_attribute_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_bean_page_property_id_nobody.release();
    _jspx_tagPool_tiles_insert_flush_attribute_nobody.release();
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
      //  bean:page
      javax.servlet.http.HttpSession sessionObj = null;
      org.apache.struts.taglib.bean.PageTag _jspx_th_bean_page_0 = (org.apache.struts.taglib.bean.PageTag) _jspx_tagPool_bean_page_property_id_nobody.get(org.apache.struts.taglib.bean.PageTag.class);
      _jspx_th_bean_page_0.setPageContext(_jspx_page_context);
      _jspx_th_bean_page_0.setParent(null);
      _jspx_th_bean_page_0.setId("sessionObj");
      _jspx_th_bean_page_0.setProperty("session");
      int _jspx_eval_bean_page_0 = _jspx_th_bean_page_0.doStartTag();
      sessionObj = (javax.servlet.http.HttpSession) _jspx_page_context.findAttribute("sessionObj");
      if (_jspx_th_bean_page_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      sessionObj = (javax.servlet.http.HttpSession) _jspx_page_context.findAttribute("sessionObj");
      _jspx_tagPool_bean_page_property_id_nobody.reuse(_jspx_th_bean_page_0);
      out.write('\r');
      out.write('\n');
      //  bean:page
      javax.servlet.ServletRequest requestObj = null;
      org.apache.struts.taglib.bean.PageTag _jspx_th_bean_page_1 = (org.apache.struts.taglib.bean.PageTag) _jspx_tagPool_bean_page_property_id_nobody.get(org.apache.struts.taglib.bean.PageTag.class);
      _jspx_th_bean_page_1.setPageContext(_jspx_page_context);
      _jspx_th_bean_page_1.setParent(null);
      _jspx_th_bean_page_1.setId("requestObj");
      _jspx_th_bean_page_1.setProperty("request");
      int _jspx_eval_bean_page_1 = _jspx_th_bean_page_1.doStartTag();
      requestObj = (javax.servlet.ServletRequest) _jspx_page_context.findAttribute("requestObj");
      if (_jspx_th_bean_page_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      requestObj = (javax.servlet.ServletRequest) _jspx_page_context.findAttribute("requestObj");
      _jspx_tagPool_bean_page_property_id_nobody.reuse(_jspx_th_bean_page_1);
      out.write("\r\n");
      out.write("\r\n");
 try { 
      out.write('\r');
      out.write('\n');
      if (_jspx_meth_tiles_insert_0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
 } catch(Throwable contentException) {
	System.err.println("----> Error occured processing JSP content");
	contentException.printStackTrace();
    // May contain sensitive information
	// Util.dumpSessionAttributes(session);							
	// Util.dumpRequestAttributes(request);
	// Util.dumpRequestParameters(request);
	System.err.println("<---- End of JSP content error");
	StringWriter sw = new StringWriter();
	contentException.printStackTrace(new PrintWriter(sw));
	
      out.write("\r\n");
      out.write("<pre> ");
      out.print( sw.toString() );
      out.write(" </pre>\r\n");

	} 
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

  private boolean _jspx_meth_tiles_insert_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_flush_attribute_nobody.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_0.setParent(null);
    _jspx_th_tiles_insert_0.setFlush(false);
    _jspx_th_tiles_insert_0.setAttribute("content");
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_th_tiles_insert_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_insert_flush_attribute_nobody.reuse(_jspx_th_tiles_insert_0);
    return false;
  }
}
