package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.adito.security.Constants;
import com.adito.core.BundleActionMessage;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;

public final class pageInfo_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_styleId;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_themePath_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_styleId = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_themePath_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
    _jspx_tagPool_logic_notEmpty_name.release();
    _jspx_tagPool_input_frame_styleId.release();
    _jspx_tagPool_core_themePath_nobody.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
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
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_3 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_3.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_3.setParent(null);
      _jspx_th_tiles_useAttribute_3.setName("info");
      _jspx_th_tiles_useAttribute_3.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_3 = _jspx_th_tiles_useAttribute_3.doStartTag();
      if (_jspx_th_tiles_useAttribute_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_3);
      java.lang.String info = null;
      info = (java.lang.String) _jspx_page_context.findAttribute("info");
      out.write('\n');
      out.write('\n');
      //  logic:notEmpty
      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_0 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
      _jspx_th_logic_notEmpty_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEmpty_0.setParent(null);
      _jspx_th_logic_notEmpty_0.setName("info");
      int _jspx_eval_logic_notEmpty_0 = _jspx_th_logic_notEmpty_0.doStartTag();
      if (_jspx_eval_logic_notEmpty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          //  input:frame
          com.adito.input.tags.FrameTag _jspx_th_input_frame_0 = (com.adito.input.tags.FrameTag) _jspx_tagPool_input_frame_styleId.get(com.adito.input.tags.FrameTag.class);
          _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
          _jspx_th_input_frame_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
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
              out.write("\t    <table>\n");
              out.write("\t      <tr>\n");
              out.write("\t        <td valign=\"top\">\n");
              out.write("\t\t      <img class=\"infoImage\" align=\"left\" src=\"");
              if (_jspx_meth_core_themePath_0(_jspx_th_input_frame_0, _jspx_page_context))
                return;
              out.write("/images/info/");
              out.print( infoImage );
              out.write("\" />\n");
              out.write("\t\t    </td>\n");
              out.write("\t\t    <td>\n");
              out.write("\t\t      <h1>\n");
              out.write("\t\t\t    ");
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
              out.write("\n");
              out.write("\t\t      </h1>\n");
              out.write("\t\t      ");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
              _jspx_th_bean_message_1.setKey( resourcePrefix + ".description" );
              _jspx_th_bean_message_1.setBundle( resourceBundle );
              int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
              if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
              out.write("\n");
              out.write("\t\t    </td>\n");
              out.write("\t\t  </tr>\n");
              out.write("\t\t</table>\n");
              out.write("\t");
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
          out.write('\n');
          int evalDoAfterBody = _jspx_th_logic_notEmpty_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEmpty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEmpty_name.reuse(_jspx_th_logic_notEmpty_0);
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

  private boolean _jspx_meth_core_themePath_0(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:themePath
    com.adito.core.tags.ThemePathTag _jspx_th_core_themePath_0 = (com.adito.core.tags.ThemePathTag) _jspx_tagPool_core_themePath_nobody.get(com.adito.core.tags.ThemePathTag.class);
    _jspx_th_core_themePath_0.setPageContext(_jspx_page_context);
    _jspx_th_core_themePath_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    int _jspx_eval_core_themePath_0 = _jspx_th_core_themePath_0.doStartTag();
    if (_jspx_th_core_themePath_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_themePath_nobody.reuse(_jspx_th_core_themePath_0);
    return false;
  }
}
