package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class infoMessages_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_messagesPresent_message;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_messages_message_id_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_write_name_filter_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_messagesPresent_message = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_messages_message_id_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_write_name_filter_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.release();
    _jspx_tagPool_logic_messagesPresent_message.release();
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle.release();
    _jspx_tagPool_core_messages_message_id_bundle.release();
    _jspx_tagPool_bean_write_name_filter_nobody.release();
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
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setIgnore(true);
      _jspx_th_tiles_useAttribute_0.setName("resourceBundle");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String resourceBundle = null;
      resourceBundle = (java.lang.String) _jspx_page_context.findAttribute("resourceBundle");
      out.write('\r');
      out.write('\n');
      //  logic:messagesPresent
      org.apache.struts.taglib.logic.MessagesPresentTag _jspx_th_logic_messagesPresent_0 = (org.apache.struts.taglib.logic.MessagesPresentTag) _jspx_tagPool_logic_messagesPresent_message.get(org.apache.struts.taglib.logic.MessagesPresentTag.class);
      _jspx_th_logic_messagesPresent_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_messagesPresent_0.setParent(null);
      _jspx_th_logic_messagesPresent_0.setMessage("true");
      int _jspx_eval_logic_messagesPresent_0 = _jspx_th_logic_messagesPresent_0.doStartTag();
      if (_jspx_eval_logic_messagesPresent_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\r');
          out.write('\n');
          out.write('	');
          //  input:frame
          net.openvpn.als.input.tags.FrameTag _jspx_th_input_frame_0 = (net.openvpn.als.input.tags.FrameTag) _jspx_tagPool_input_frame_titleKey_styleClass_panelId_bundle.get(net.openvpn.als.input.tags.FrameTag.class);
          _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
          _jspx_th_input_frame_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_messagesPresent_0);
          _jspx_th_input_frame_0.setPanelId("infoMessages");
          _jspx_th_input_frame_0.setTitleKey("messages.information");
          _jspx_th_input_frame_0.setBundle("navigation");
          _jspx_th_input_frame_0.setStyleClass("component_messageBox");
          int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
          if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_frame_0.doInitBody();
            }
            do {
              out.write("\r\n");
              out.write("\t\t<div id=\"info_messages\" class=\"messages\">\r\n");
              out.write("\t\t\t");
              //  core:messages
              net.openvpn.als.core.tags.BundleMessagesTag _jspx_th_core_messages_0 = (net.openvpn.als.core.tags.BundleMessagesTag) _jspx_tagPool_core_messages_message_id_bundle.get(net.openvpn.als.core.tags.BundleMessagesTag.class);
              _jspx_th_core_messages_0.setPageContext(_jspx_page_context);
              _jspx_th_core_messages_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
              _jspx_th_core_messages_0.setBundle( resourceBundle );
              _jspx_th_core_messages_0.setId("msg");
              _jspx_th_core_messages_0.setMessage("true");
              int _jspx_eval_core_messages_0 = _jspx_th_core_messages_0.doStartTag();
              if (_jspx_eval_core_messages_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                java.lang.String msg = null;
                if (_jspx_eval_core_messages_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_core_messages_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_core_messages_0.doInitBody();
                }
                msg = (java.lang.String) _jspx_page_context.findAttribute("msg");
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t<div class=\"text\">\r\n");
                  out.write("\t\t\t\t\t");
                  if (_jspx_meth_bean_write_0(_jspx_th_core_messages_0, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t\t\t</div>\r\n");
                  out.write("\t\t\t");
                  int evalDoAfterBody = _jspx_th_core_messages_0.doAfterBody();
                  msg = (java.lang.String) _jspx_page_context.findAttribute("msg");
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_core_messages_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_core_messages_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_core_messages_message_id_bundle.reuse(_jspx_th_core_messages_0);
              out.write("\r\n");
              out.write("\t\t</div>\r\n");
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
          out.write('\r');
          out.write('\n');
          int evalDoAfterBody = _jspx_th_logic_messagesPresent_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_messagesPresent_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_messagesPresent_message.reuse(_jspx_th_logic_messagesPresent_0);
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

  private boolean _jspx_meth_bean_write_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_messages_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:write
    org.apache.struts.taglib.bean.WriteTag _jspx_th_bean_write_0 = (org.apache.struts.taglib.bean.WriteTag) _jspx_tagPool_bean_write_name_filter_nobody.get(org.apache.struts.taglib.bean.WriteTag.class);
    _jspx_th_bean_write_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_write_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_messages_0);
    _jspx_th_bean_write_0.setFilter(false);
    _jspx_th_bean_write_0.setName("msg");
    int _jspx_eval_bean_write_0 = _jspx_th_bean_write_0.doStartTag();
    if (_jspx_th_bean_write_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_write_name_filter_nobody.reuse(_jspx_th_bean_write_0);
    return false;
  }
}
