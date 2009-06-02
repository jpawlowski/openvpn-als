package org.apache.jsp.WEB_002dINF.jsp.content.extensions;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.core.DefaultPanel;
import java.util.Iterator;
import net.openvpn.als.core.PanelManager;
import net.openvpn.als.core.Panel;
import net.openvpn.als.extensions.forms.ConfigureExtensionsForm;

public final class configureExtensions_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/tabs.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_empty_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tabs_tabSet_resourcePrefix_name_bundle;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tabs_tabHeadings_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tabs_tab_tabName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_logic_empty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tabs_tabSet_resourcePrefix_name_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tabs_tabHeadings_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tabs_tab_tabName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_logic_empty_property_name.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_tabs_tabSet_resourcePrefix_name_bundle.release();
    _jspx_tagPool_tabs_tabHeadings_nobody.release();
    _jspx_tagPool_tabs_tab_tabName.release();
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
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"page_configureExtensions\">\r\n");
      out.write("\t");
      if (_jspx_meth_logic_empty_0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\t\t");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("actionLink");
      _jspx_th_tiles_useAttribute_0.setScope("request");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String actionLink = null;
      actionLink = (java.lang.String) _jspx_page_context.findAttribute("actionLink");
      out.write(" \t\r\n");
      out.write("\t\t");
      //  tabs:tabSet
      net.openvpn.als.tabs.tags.TabSetTag _jspx_th_tabs_tabSet_0 = (net.openvpn.als.tabs.tags.TabSetTag) _jspx_tagPool_tabs_tabSet_resourcePrefix_name_bundle.get(net.openvpn.als.tabs.tags.TabSetTag.class);
      _jspx_th_tabs_tabSet_0.setPageContext(_jspx_page_context);
      _jspx_th_tabs_tabSet_0.setParent(null);
      _jspx_th_tabs_tabSet_0.setName("configureExtensionsForm");
      _jspx_th_tabs_tabSet_0.setBundle("extensions");
      _jspx_th_tabs_tabSet_0.setResourcePrefix("extensions.tab");
      int _jspx_eval_tabs_tabSet_0 = _jspx_th_tabs_tabSet_0.doStartTag();
      if (_jspx_eval_tabs_tabSet_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_tabs_tabSet_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_tabs_tabSet_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_tabs_tabSet_0.doInitBody();
        }
        do {
          out.write("\r\n");
          out.write("\t\t\t");
          if (_jspx_meth_tabs_tabHeadings_0(_jspx_th_tabs_tabSet_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\t");
 
			for(Iterator i = PanelManager.getInstance().getPanels(ConfigureExtensionsForm.EXTENSIONS_TAB_ID, request, response, DefaultPanel.MAIN_LAYOUT).iterator(); i.hasNext(); ) { 
				Panel p = (Panel)i.next(); 
			
          out.write("\r\n");
          out.write("\t\t\t");
          //  tabs:tab
          net.openvpn.als.tabs.tags.TabTag _jspx_th_tabs_tab_0 = (net.openvpn.als.tabs.tags.TabTag) _jspx_tagPool_tabs_tab_tabName.get(net.openvpn.als.tabs.tags.TabTag.class);
          _jspx_th_tabs_tab_0.setPageContext(_jspx_page_context);
          _jspx_th_tabs_tab_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tabs_tabSet_0);
          _jspx_th_tabs_tab_0.setTabName( p.getId() );
          int _jspx_eval_tabs_tab_0 = _jspx_th_tabs_tab_0.doStartTag();
          if (_jspx_eval_tabs_tab_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_tabs_tab_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_tabs_tab_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_tabs_tab_0.doInitBody();
            }
            do {
              out.write("\r\n");
              out.write("\t\t\t\t");
              //  tiles:insert
              org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
              _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
              _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tabs_tab_0);
              _jspx_th_tiles_insert_0.setFlush(false);
              _jspx_th_tiles_insert_0.setPage( p.getTileIncludePath(pageContext) );
              int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
              if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_tiles_insert_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_tiles_insert_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_tiles_insert_page_flush.reuse(_jspx_th_tiles_insert_0);
              out.write("\r\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_tabs_tab_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_tabs_tab_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_tabs_tab_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_tabs_tab_tabName.reuse(_jspx_th_tabs_tab_0);
          out.write("\r\n");
          out.write("\t\t\t");
	
			} 
			
          out.write("\r\n");
          out.write("\t\t");
          int evalDoAfterBody = _jspx_th_tabs_tabSet_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_tabs_tabSet_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_tabs_tabSet_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tabs_tabSet_resourcePrefix_name_bundle.reuse(_jspx_th_tabs_tabSet_0);
      out.write("\t\r\n");
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

  private boolean _jspx_meth_logic_empty_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  logic:empty
    org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_0 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
    _jspx_th_logic_empty_0.setPageContext(_jspx_page_context);
    _jspx_th_logic_empty_0.setParent(null);
    _jspx_th_logic_empty_0.setName("configureExtensionsForm");
    _jspx_th_logic_empty_0.setProperty("descriptor");
    int _jspx_eval_logic_empty_0 = _jspx_th_logic_empty_0.doStartTag();
    if (_jspx_eval_logic_empty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("\t\t<div id=\"notConnected\" class=\"dialog\">\r\n");
        out.write("\t\t\t<table>\r\n");
        out.write("\t\t\t\t<tr>\r\n");
        out.write("\t\t\t\t\t<td align=\"left\">\r\n");
        out.write("\t\t\t\t\t\t");
        if (_jspx_meth_bean_message_0(_jspx_th_logic_empty_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t</td>\r\n");
        out.write("\t\t\t\t\t<td>    \r\n");
        out.write("\t\t\t\t\t\t<input type=\"button\"\r\n");
        out.write("\t\t\t\t\t\t\t\tonclick=\"self.location = '/showExtensionStore.do?action=list&connect';\"\r\n");
        out.write("\t\t\t\t\t\t\t\tvalue=\"");
        if (_jspx_meth_bean_message_1(_jspx_th_logic_empty_0, _jspx_page_context))
          return true;
        out.write("\" \r\n");
        out.write("\t\t\t\t\t\t/>\r\n");
        out.write("\t\t\t\t\t</td>\r\n");
        out.write("\t\t\t\t</tr>\r\n");
        out.write("\t\t\t</table>\r\n");
        out.write("\t\t</div>\r\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_logic_empty_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_logic_empty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_logic_empty_property_name.reuse(_jspx_th_logic_empty_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_empty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_0);
    _jspx_th_bean_message_0.setKey("extensionStore.notConnected.text");
    _jspx_th_bean_message_0.setBundle("extensions");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_empty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_0);
    _jspx_th_bean_message_1.setKey("extensionStore.notConnected.connect");
    _jspx_th_bean_message_1.setBundle("extensions");
    int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
    if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
    return false;
  }

  private boolean _jspx_meth_tabs_tabHeadings_0(javax.servlet.jsp.tagext.JspTag _jspx_th_tabs_tabSet_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tabs:tabHeadings
    net.openvpn.als.tabs.tags.TabHeadingsTag _jspx_th_tabs_tabHeadings_0 = (net.openvpn.als.tabs.tags.TabHeadingsTag) _jspx_tagPool_tabs_tabHeadings_nobody.get(net.openvpn.als.tabs.tags.TabHeadingsTag.class);
    _jspx_th_tabs_tabHeadings_0.setPageContext(_jspx_page_context);
    _jspx_th_tabs_tabHeadings_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tabs_tabSet_0);
    int _jspx_eval_tabs_tabHeadings_0 = _jspx_th_tabs_tabHeadings_0.doStartTag();
    if (_jspx_th_tabs_tabHeadings_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tabs_tabHeadings_nobody.reuse(_jspx_th_tabs_tabHeadings_0);
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
    _jspx_th_tiles_put_0.setName("actionLink");
    _jspx_th_tiles_put_0.setBeanName("actionLink");
    int _jspx_eval_tiles_put_0 = _jspx_th_tiles_put_0.doStartTag();
    if (_jspx_th_tiles_put_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_0);
    return false;
  }
}
