package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.security.Constants;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.navigation.AbstractFavoriteItem;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.forms.AbstractResourcesForm;
import net.openvpn.als.boot.Util;

public final class resourcesView_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(5);
    _jspx_dependants.add("/WEB-INF/input.tld");
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/navigation.tld");
    _jspx_dependants.add("/WEB-INF/table.tld");
    _jspx_dependants.add("/WEB-INF/security.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_equal_value_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_property_name_indexId_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_write_property_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerName_disabledStyleClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_table_pageSize_styleClass_pagerName_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_equal_value_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_property_name_indexId_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_write_property_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerName_disabledStyleClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_table_pageSize_styleClass_pagerName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.release();
    _jspx_tagPool_logic_equal_value_name.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_name_beanName_nobody.release();
    _jspx_tagPool_logic_iterate_type_property_name_indexId_id.release();
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.release();
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.release();
    _jspx_tagPool_bean_write_property_name_nobody.release();
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerName_disabledStyleClass_nobody.release();
    _jspx_tagPool_table_pageSize_styleClass_pagerName_nobody.release();
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
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("pager");
      _jspx_th_tiles_useAttribute_0.setScope("request");
      _jspx_th_tiles_useAttribute_0.setClassname("net.openvpn.als.table.Pager");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      net.openvpn.als.table.Pager pager = null;
      pager = (net.openvpn.als.table.Pager) _jspx_page_context.findAttribute("pager");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("messageResourcesKey");
      _jspx_th_tiles_useAttribute_1.setScope("request");
      _jspx_th_tiles_useAttribute_1.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      java.lang.String messageResourcesKey = null;
      messageResourcesKey = (java.lang.String) _jspx_page_context.findAttribute("messageResourcesKey");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_2 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_2.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_2.setParent(null);
      _jspx_th_tiles_useAttribute_2.setName("resourcePrefix");
      _jspx_th_tiles_useAttribute_2.setScope("request");
      _jspx_th_tiles_useAttribute_2.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_2 = _jspx_th_tiles_useAttribute_2.doStartTag();
      if (_jspx_th_tiles_useAttribute_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_2);
      java.lang.String resourcePrefix = null;
      resourcePrefix = (java.lang.String) _jspx_page_context.findAttribute("resourcePrefix");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_3 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_3.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_3.setParent(null);
      _jspx_th_tiles_useAttribute_3.setName("actionLink");
      _jspx_th_tiles_useAttribute_3.setScope("request");
      _jspx_th_tiles_useAttribute_3.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_3 = _jspx_th_tiles_useAttribute_3.doStartTag();
      if (_jspx_th_tiles_useAttribute_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_3);
      java.lang.String actionLink = null;
      actionLink = (java.lang.String) _jspx_page_context.findAttribute("actionLink");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_4 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_4.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_4.setParent(null);
      _jspx_th_tiles_useAttribute_4.setName("selectedView");
      _jspx_th_tiles_useAttribute_4.setScope("request");
      _jspx_th_tiles_useAttribute_4.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_4 = _jspx_th_tiles_useAttribute_4.doStartTag();
      if (_jspx_th_tiles_useAttribute_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_4);
      java.lang.String selectedView = null;
      selectedView = (java.lang.String) _jspx_page_context.findAttribute("selectedView");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_5 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_5.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_5.setParent(null);
      _jspx_th_tiles_useAttribute_5.setIgnore(true);
      _jspx_th_tiles_useAttribute_5.setName("policyLaunching");
      _jspx_th_tiles_useAttribute_5.setScope("request");
      _jspx_th_tiles_useAttribute_5.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_5 = _jspx_th_tiles_useAttribute_5.doStartTag();
      if (_jspx_th_tiles_useAttribute_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.reuse(_jspx_th_tiles_useAttribute_5);
      java.lang.String policyLaunching = null;
      policyLaunching = (java.lang.String) _jspx_page_context.findAttribute("policyLaunching");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_6 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_6.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_6.setParent(null);
      _jspx_th_tiles_useAttribute_6.setIgnore(true);
      _jspx_th_tiles_useAttribute_6.setName("subForm");
      _jspx_th_tiles_useAttribute_6.setScope("request");
      _jspx_th_tiles_useAttribute_6.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_6 = _jspx_th_tiles_useAttribute_6.doStartTag();
      if (_jspx_th_tiles_useAttribute_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_ignore_classname_nobody.reuse(_jspx_th_tiles_useAttribute_6);
      java.lang.String subForm = null;
      subForm = (java.lang.String) _jspx_page_context.findAttribute("subForm");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"");
      out.print( "pager_" + pager.getModel().getId() );
      out.write("\" class=\"filtered_resources\">\t\t\t\t\t\t\t\r\n");
      out.write("\t");
      //  logic:equal
      org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_0 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_name.get(org.apache.struts.taglib.logic.EqualTag.class);
      _jspx_th_logic_equal_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_equal_0.setParent(null);
      _jspx_th_logic_equal_0.setName("selectedView");
      _jspx_th_logic_equal_0.setValue( AbstractResourcesForm.LIST_VIEW );
      int _jspx_eval_logic_equal_0 = _jspx_th_logic_equal_0.doStartTag();
      if (_jspx_eval_logic_equal_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\t\t\r\n");
          out.write("\t\t");
          if (_jspx_meth_tiles_insert_0(_jspx_th_logic_equal_0, _jspx_page_context))
            return;
          out.write("\t\t\r\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_logic_equal_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_equal_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_equal_value_name.reuse(_jspx_th_logic_equal_0);
      out.write('\r');
      out.write('\n');
      out.write('	');
      //  logic:equal
      org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_1 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_name.get(org.apache.struts.taglib.logic.EqualTag.class);
      _jspx_th_logic_equal_1.setPageContext(_jspx_page_context);
      _jspx_th_logic_equal_1.setParent(null);
      _jspx_th_logic_equal_1.setName("selectedView");
      _jspx_th_logic_equal_1.setValue( AbstractResourcesForm.ICONS_VIEW );
      int _jspx_eval_logic_equal_1 = _jspx_th_logic_equal_1.doStartTag();
      if (_jspx_eval_logic_equal_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\r\n");
          out.write("\t\t<div class=\"icons\">\r\n");
          out.write("\t\t\t<ul>\r\n");
          out.write("\t\t\t\t");
          //  logic:iterate
          org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_indexId_id.get(org.apache.struts.taglib.logic.IterateTag.class);
          _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
          _jspx_th_logic_iterate_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_1);
          _jspx_th_logic_iterate_0.setName("pager");
          _jspx_th_logic_iterate_0.setProperty("pageItems");
          _jspx_th_logic_iterate_0.setId("resourceItem");
          _jspx_th_logic_iterate_0.setType("net.openvpn.als.policyframework.ResourceItem");
          _jspx_th_logic_iterate_0.setIndexId("i");
          int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
          if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            net.openvpn.als.policyframework.ResourceItem resourceItem = null;
            java.lang.Integer i = null;
            if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_logic_iterate_0.doInitBody();
            }
            resourceItem = (net.openvpn.als.policyframework.ResourceItem) _jspx_page_context.findAttribute("resourceItem");
            i = (java.lang.Integer) _jspx_page_context.findAttribute("i");
            do {
              out.write("\r\n");
              out.write("\t\t\t\t\t<li>\r\n");
              out.write("\t\t\t\t\t\t<div class=\"icon\" onclick=\"new Effect.Highlight(this);\">\r\n");
              out.write("\t\t\t\t\t\t\t<div class=\"image\">\r\n");
              out.write("\t\t\t\t\t\t\t\t");
              //  input:toolTip
              net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.get(net.openvpn.als.input.tags.ToolTipTag.class);
              _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
              _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
              _jspx_th_input_toolTip_0.setOnclick( resourceItem.getOnClick(-1, request) );
              _jspx_th_input_toolTip_0.setHref( resourceItem.getLink(-1, request) );
              _jspx_th_input_toolTip_0.setWidth("400");
              _jspx_th_input_toolTip_0.setBorderWidth("0");
              _jspx_th_input_toolTip_0.setPadding("0");
              _jspx_th_input_toolTip_0.setOnclick( resourceItem.getOnClick(-1, request) );
              _jspx_th_input_toolTip_0.setHref( resourceItem.getLink(-1, request) );
              _jspx_th_input_toolTip_0.setContentLocation( "/resourceInformation.do?resourceId=" + resourceItem.getResource().getResourceId() + "&resourceType=" + resourceItem.getResource().getResourceType().getResourceTypeId() );
              _jspx_th_input_toolTip_0.setValue( resourceItem.getResource().getResourceDescription() );
              int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
              if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_input_toolTip_0.doInitBody();
                }
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<img border=\"0\" align=\"absmiddle\" src=\"");
                  out.print( resourceItem.getLargeIconPath(request) );
                  out.write("\"/>\r\n");
                  out.write("\t\t\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.reuse(_jspx_th_input_toolTip_0);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t<div class=\"text\">\r\n");
              out.write("\t\t\t\t\t\t\t\t");
              //  input:toolTip
              net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.get(net.openvpn.als.input.tags.ToolTipTag.class);
              _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
              _jspx_th_input_toolTip_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
              _jspx_th_input_toolTip_1.setWidth("400");
              _jspx_th_input_toolTip_1.setBorderWidth("0");
              _jspx_th_input_toolTip_1.setPadding("0");
              _jspx_th_input_toolTip_1.setOnclick( resourceItem.getOnClick(-1, request) );
              _jspx_th_input_toolTip_1.setHref( resourceItem.getLink(-1, request) );
              _jspx_th_input_toolTip_1.setContentLocation( "/resourceInformation.do?resourceId=" + resourceItem.getResource().getResourceId() + "&resourceType=" + resourceItem.getResource().getResourceType().getResourceTypeId() );
              _jspx_th_input_toolTip_1.setValue( resourceItem.getResource().getResourceDescription() );
              int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
              if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_input_toolTip_1.doInitBody();
                }
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
                  if (_jspx_meth_bean_write_0(_jspx_th_input_toolTip_1, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_input_toolTip_1.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_input_toolTip_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.reuse(_jspx_th_input_toolTip_1);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t<div class=\"other\">\r\n");
              out.write("\t\t\t\t\t\t\t\t");
 String ai = resourceItem.getLargeIconAdditionalIcon(request);
								if(!ai.equals("")) { 
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t<img src=\"");
              out.print( resourceItem.getLargeIconAdditionalIcon(request) );
              out.write("\"/>\r\n");
              out.write("\t\t\t\t\t\t\t\t");
 } 
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t</div>\t\t\t\t\t\t\t\t\r\n");
              out.write("\t\t\t\t\t</li>\r\n");
              out.write("\t\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
              resourceItem = (net.openvpn.als.policyframework.ResourceItem) _jspx_page_context.findAttribute("resourceItem");
              i = (java.lang.Integer) _jspx_page_context.findAttribute("i");
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_logic_iterate_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_iterate_type_property_name_indexId_id.reuse(_jspx_th_logic_iterate_0);
          out.write("\t\t\t\t\t\t\r\n");
          out.write("\t\t\t</ul>\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t<div class=\"pager_navigation\">\r\n");
          out.write("\t\t\t");
          if (_jspx_meth_table_navigation_0(_jspx_th_logic_equal_1, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t<div class=\"pager_pages\">\r\n");
          out.write("\t\t\t");
          if (_jspx_meth_table_pageSize_0(_jspx_th_logic_equal_1, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_logic_equal_1.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_equal_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_equal_value_name.reuse(_jspx_th_logic_equal_1);
      out.write("\r\n");
      out.write("</div>\r\n");

String exec = request.getParameter("exec");
exec = exec == null ? (String)request.getAttribute("exec") : exec;
if(exec != null) {

      out.write("\r\n");
      out.write("\t<script language=\"JavaScript\">");
      out.print( exec );
      out.write("</script>\r\n");

}

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

  private boolean _jspx_meth_tiles_insert_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
    _jspx_th_tiles_insert_0.setFlush(false);
    _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/tiles/resourceList.jspf");
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_2(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_3(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_put_4(_jspx_th_tiles_insert_0, _jspx_page_context))
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
    _jspx_th_tiles_put_0.setName("pager");
    _jspx_th_tiles_put_0.setBeanName("pager");
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
    _jspx_th_tiles_put_1.setName("messageResourcesKey");
    _jspx_th_tiles_put_1.setBeanName("messageResourcesKey");
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
    _jspx_th_tiles_put_2.setName("resourcePrefix");
    _jspx_th_tiles_put_2.setBeanName("resourcePrefix");
    int _jspx_eval_tiles_put_2 = _jspx_th_tiles_put_2.doStartTag();
    if (_jspx_th_tiles_put_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_2);
    return false;
  }

  private boolean _jspx_meth_tiles_put_3(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_3 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_3.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_3.setName("policyLaunching");
    _jspx_th_tiles_put_3.setBeanName("policyLaunching");
    int _jspx_eval_tiles_put_3 = _jspx_th_tiles_put_3.doStartTag();
    if (_jspx_th_tiles_put_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_3);
    return false;
  }

  private boolean _jspx_meth_tiles_put_4(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_4 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_4.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_4.setName("subForm");
    _jspx_th_tiles_put_4.setBeanName("subForm");
    int _jspx_eval_tiles_put_4 = _jspx_th_tiles_put_4.doStartTag();
    if (_jspx_th_tiles_put_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_4);
    return false;
  }

  private boolean _jspx_meth_bean_write_0(javax.servlet.jsp.tagext.JspTag _jspx_th_input_toolTip_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:write
    org.apache.struts.taglib.bean.WriteTag _jspx_th_bean_write_0 = (org.apache.struts.taglib.bean.WriteTag) _jspx_tagPool_bean_write_property_name_nobody.get(org.apache.struts.taglib.bean.WriteTag.class);
    _jspx_th_bean_write_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_write_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_1);
    _jspx_th_bean_write_0.setName("resourceItem");
    _jspx_th_bean_write_0.setProperty("resource.resourceDisplayName");
    int _jspx_eval_bean_write_0 = _jspx_th_bean_write_0.doStartTag();
    if (_jspx_th_bean_write_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_write_property_name_nobody.reuse(_jspx_th_bean_write_0);
    return false;
  }

  private boolean _jspx_meth_table_navigation_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:navigation
    net.openvpn.als.table.tags.NavigationTag _jspx_th_table_navigation_0 = (net.openvpn.als.table.tags.NavigationTag) _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerName_disabledStyleClass_nobody.get(net.openvpn.als.table.tags.NavigationTag.class);
    _jspx_th_table_navigation_0.setPageContext(_jspx_page_context);
    _jspx_th_table_navigation_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_1);
    _jspx_th_table_navigation_0.setPagerName("pager");
    _jspx_th_table_navigation_0.setStyleClass("pagerEnabled");
    _jspx_th_table_navigation_0.setSelectedStyleClass("pagerSelected");
    _jspx_th_table_navigation_0.setDisabledStyleClass("pagerDisabled");
    int _jspx_eval_table_navigation_0 = _jspx_th_table_navigation_0.doStartTag();
    if (_jspx_th_table_navigation_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerName_disabledStyleClass_nobody.reuse(_jspx_th_table_navigation_0);
    return false;
  }

  private boolean _jspx_meth_table_pageSize_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:pageSize
    net.openvpn.als.table.tags.PageSizeTag _jspx_th_table_pageSize_0 = (net.openvpn.als.table.tags.PageSizeTag) _jspx_tagPool_table_pageSize_styleClass_pagerName_nobody.get(net.openvpn.als.table.tags.PageSizeTag.class);
    _jspx_th_table_pageSize_0.setPageContext(_jspx_page_context);
    _jspx_th_table_pageSize_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_1);
    _jspx_th_table_pageSize_0.setPagerName("pager");
    _jspx_th_table_pageSize_0.setStyleClass("pagerEnabled");
    int _jspx_eval_table_pageSize_0 = _jspx_th_table_pageSize_0.doStartTag();
    if (_jspx_th_table_pageSize_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_pageSize_styleClass_pagerName_nobody.reuse(_jspx_th_table_pageSize_0);
    return false;
  }
}
