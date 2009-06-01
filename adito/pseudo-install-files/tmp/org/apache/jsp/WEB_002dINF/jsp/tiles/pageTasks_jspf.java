package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class pageTasks_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_name_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_target_key_href_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_logic_notEmpty_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_name_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_target_key_href_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_logic_notEmpty_name.release();
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle.release();
    _jspx_tagPool_logic_iterate_type_name_id.release();
    _jspx_tagPool_input_toolTip_target_key_href_bundle.release();
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
      //  logic:notEmpty
      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_0 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
      _jspx_th_logic_notEmpty_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEmpty_0.setParent(null);
      _jspx_th_logic_notEmpty_0.setName("pageTasks");
      int _jspx_eval_logic_notEmpty_0 = _jspx_th_logic_notEmpty_0.doStartTag();
      if (_jspx_eval_logic_notEmpty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('	');
          out.write('\n');
          out.write('	');
          //  input:frame
          com.adito.input.tags.FrameTag _jspx_th_input_frame_0 = (com.adito.input.tags.FrameTag) _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle.get(com.adito.input.tags.FrameTag.class);
          _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
          _jspx_th_input_frame_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
          _jspx_th_input_frame_0.setTitleKey("messages.pageTasks");
          _jspx_th_input_frame_0.setBundle("navigation");
          _jspx_th_input_frame_0.setStyleClass("component_messageBox");
          _jspx_th_input_frame_0.setPanelId("pageTasks");
          int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
          if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_frame_0.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t<div class=\"messages\">\n");
              out.write("\t\t\t");
              //  logic:iterate
              org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
              _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
              _jspx_th_logic_iterate_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
              _jspx_th_logic_iterate_0.setId("rootMenuItem");
              _jspx_th_logic_iterate_0.setName("pageTasks");
              _jspx_th_logic_iterate_0.setType("com.adito.core.AvailableMenuItem");
              int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
              if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                com.adito.core.AvailableMenuItem rootMenuItem = null;
                if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_logic_iterate_0.doInitBody();
                }
                rootMenuItem = (com.adito.core.AvailableMenuItem) _jspx_page_context.findAttribute("rootMenuItem");
                do {
                  out.write("\t\t\t\n");
                  out.write("\t\t\t\t<div class=\"text\">\n");
                  out.write("\t\t\t\t\t");
                  //  input:toolTip
                  com.adito.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (com.adito.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_target_key_href_bundle.get(com.adito.input.tags.ToolTipTag.class);
                  _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
                  _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
                  _jspx_th_input_toolTip_0.setTarget( rootMenuItem.getMenuItem().getTarget() );
                  _jspx_th_input_toolTip_0.setKey( "pageTask." + rootMenuItem.getMenuItem().getId() + ".description"  );
                  _jspx_th_input_toolTip_0.setBundle( rootMenuItem.getMenuItem().getMessageResourcesKey() );
                  _jspx_th_input_toolTip_0.setHref( rootMenuItem.getPath() );
                  int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
                  if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_input_toolTip_0.doInitBody();
                    }
                    do {
                      out.write("\n");
                      out.write("\t\t\t\t\t\t");
                      //  bean:message
                      org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
                      _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
                      _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_0);
                      _jspx_th_bean_message_0.setKey( "pageTask." + rootMenuItem.getMenuItem().getId() + ".title"  );
                      _jspx_th_bean_message_0.setBundle( rootMenuItem.getMenuItem().getMessageResourcesKey() );
                      int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
                      if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                        return;
                      _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
                      out.write("\n");
                      out.write("\t\t\t\t\t");
                      int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_input_toolTip_target_key_href_bundle.reuse(_jspx_th_input_toolTip_0);
                  out.write("\n");
                  out.write("\t\t\t\t</div>\n");
                  out.write("\t\t\t");
                  int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
                  rootMenuItem = (com.adito.core.AvailableMenuItem) _jspx_page_context.findAttribute("rootMenuItem");
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_logic_iterate_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_logic_iterate_type_name_id.reuse(_jspx_th_logic_iterate_0);
              out.write("\n");
              out.write("\t\t</div>\n");
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
          _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle.reuse(_jspx_th_input_frame_0);
          out.write('	');
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
}
