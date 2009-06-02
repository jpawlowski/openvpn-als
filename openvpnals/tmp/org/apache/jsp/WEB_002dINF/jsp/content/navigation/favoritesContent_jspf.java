package org.apache.jsp.WEB_002dINF.jsp.content.navigation;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.security.WebDAVAuthenticationModule;
import net.openvpn.als.security.Constants;
import net.openvpn.als.navigation.WrappedFavoriteItem;
import net.openvpn.als.navigation.AbstractFavoriteItem;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.boot.Util;
import net.openvpn.als.policyframework.forms.AbstractResourcesForm;

public final class favoritesContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(3);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/table.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_form_onsubmit_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_hidden_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_type_name_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_equal_value_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_table_columnHeader_styleClass_pagerProperty_pagerName_page_columnIndex;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_property_name_indexId_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_padding_onclick_href_contentLocation_borderWidth;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_write_property_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_value_name_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_form_onsubmit_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_hidden_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_type_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_equal_value_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_table_columnHeader_styleClass_pagerProperty_pagerName_page_columnIndex = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_property_name_indexId_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_padding_onclick_href_contentLocation_borderWidth = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_write_property_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_value_name_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_core_form_onsubmit_method_action.release();
    _jspx_tagPool_html_hidden_property_nobody.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_type_name_beanName_nobody.release();
    _jspx_tagPool_logic_equal_value_property_name.release();
    _jspx_tagPool_table_columnHeader_styleClass_pagerProperty_pagerName_page_columnIndex.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_logic_iterate_type_property_name_indexId_id.release();
    _jspx_tagPool_input_toolTip_width_padding_onclick_href_contentLocation_borderWidth.release();
    _jspx_tagPool_bean_write_property_name_nobody.release();
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.release();
    _jspx_tagPool_tiles_put_name_beanName_nobody.release();
    _jspx_tagPool_tiles_put_value_name_nobody.release();
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody.release();
    _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody.release();
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.release();
    _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.release();
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
      out.write(" \r\n");
      out.write("\r\n");
      out.write("<div id=\"page_favorites\">  \r\n");
      out.write("\t");
      //  core:form
      net.openvpn.als.core.tags.FormTag _jspx_th_core_form_0 = (net.openvpn.als.core.tags.FormTag) _jspx_tagPool_core_form_onsubmit_method_action.get(net.openvpn.als.core.tags.FormTag.class);
      _jspx_th_core_form_0.setPageContext(_jspx_page_context);
      _jspx_th_core_form_0.setParent(null);
      _jspx_th_core_form_0.setOnsubmit("setActionTarget('filter')");
      _jspx_th_core_form_0.setMethod("post");
      _jspx_th_core_form_0.setAction("/showFavorites.do");
      int _jspx_eval_core_form_0 = _jspx_th_core_form_0.doStartTag();
      if (_jspx_eval_core_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\t\t\r\n");
          out.write("\t\t");
          if (_jspx_meth_html_hidden_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t");
          if (_jspx_meth_html_hidden_1(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t");
          if (_jspx_meth_html_hidden_2(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\r\n");
          out.write("\t\t<div class=\"dialog_content\">\r\n");
          out.write("\t\t\t");
          if (_jspx_meth_tiles_insert_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t\r\n");
          out.write("\t\t<div class=\"filtered_resources\">\t\r\n");
          out.write("\t\t\t\t\t\t\t\t\r\n");
          out.write("\t\t\t");
          //  logic:equal
          org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_0 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_property_name.get(org.apache.struts.taglib.logic.EqualTag.class);
          _jspx_th_logic_equal_0.setPageContext(_jspx_page_context);
          _jspx_th_logic_equal_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
          _jspx_th_logic_equal_0.setName("favoritesForm");
          _jspx_th_logic_equal_0.setProperty("selectedView");
          _jspx_th_logic_equal_0.setValue( AbstractResourcesForm.LIST_VIEW );
          int _jspx_eval_logic_equal_0 = _jspx_th_logic_equal_0.doStartTag();
          if (_jspx_eval_logic_equal_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\t\t\r\n");
              out.write("\t\t\t\t<table class=\"resource_table\">\r\n");
              out.write("\t\t\t\t\t<thead>\r\n");
              out.write("\t\t\t\t\t\t<tr>\r\n");
              out.write("\t\t\t\t\t\t\t<td class=\"name\">\r\n");
              out.write("\t\t\t\t\t\t\t\t");
              if (_jspx_meth_table_columnHeader_0(_jspx_th_logic_equal_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t\t<td class=\"actions\">\r\n");
              out.write("\t\t\t\t\t\t\t\t");
              if (_jspx_meth_bean_message_1(_jspx_th_logic_equal_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t</tr>\r\n");
              out.write("\t\t\t\t\t</thead>\r\n");
              out.write("\t\t\t\t\t<tbody>\r\n");
              out.write("\t\t\t\t\t\t");
boolean highlight = true;
              out.write("\t\t\t\t\r\n");
              out.write("\t\t\t\t\t\t");
              //  logic:equal
              org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_1 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_property_name.get(org.apache.struts.taglib.logic.EqualTag.class);
              _jspx_th_logic_equal_1.setPageContext(_jspx_page_context);
              _jspx_th_logic_equal_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
              _jspx_th_logic_equal_1.setName("favoritesForm");
              _jspx_th_logic_equal_1.setProperty("model.empty");
              _jspx_th_logic_equal_1.setValue("true");
              int _jspx_eval_logic_equal_1 = _jspx_th_logic_equal_1.doStartTag();
              if (_jspx_eval_logic_equal_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                do {
                  out.write("\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t<tr \r\n");
                  out.write("\t\t      \t\t\t\t\tonmouseover=\"");
                  out.print( "this.className = 'selected';" );
                  out.write("\"\r\n");
                  out.write("\t\t      \t\t\t\t\tonmouseout=\"");
                  out.print( "this.className = '" + ( highlight ? "highlight" : "lowlight" ) + "';" );
                  out.write("\" class=\"");
                  out.print( highlight ? "highlight" : "lowlight" );
                  out.write("\">           \r\n");
                  out.write("\t\t\t\t\t\t  \t\t<td class=\"tableMessage\" colspan=\"2\">\t\t   \r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
                  if (_jspx_meth_bean_message_2(_jspx_th_logic_equal_1, _jspx_page_context))
                    return;
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t</td>\r\n");
                  out.write("\t\t\t\t\t\t\t</tr>\r\n");
                  out.write("\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_logic_equal_1.doAfterBody();
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
              }
              if (_jspx_th_logic_equal_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_logic_equal_value_property_name.reuse(_jspx_th_logic_equal_1);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t");
              //  logic:iterate
              org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_indexId_id.get(org.apache.struts.taglib.logic.IterateTag.class);
              _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
              _jspx_th_logic_iterate_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
              _jspx_th_logic_iterate_0.setName("favoritesForm");
              _jspx_th_logic_iterate_0.setProperty("pager.pageItems");
              _jspx_th_logic_iterate_0.setId("wrappedFavoriteItem");
              _jspx_th_logic_iterate_0.setType("net.openvpn.als.navigation.WrappedFavoriteItem");
              _jspx_th_logic_iterate_0.setIndexId("i");
              int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
              if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                net.openvpn.als.navigation.WrappedFavoriteItem wrappedFavoriteItem = null;
                java.lang.Integer i = null;
                if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_logic_iterate_0.doInitBody();
                }
                wrappedFavoriteItem = (net.openvpn.als.navigation.WrappedFavoriteItem) _jspx_page_context.findAttribute("wrappedFavoriteItem");
                i = (java.lang.Integer) _jspx_page_context.findAttribute("i");
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t<tr \r\n");
                  out.write("\t\t      \t\t\t\t\tonmouseover=\"");
                  out.print( "this.className = 'selected';" );
                  out.write("\"\r\n");
                  out.write("\t\t      \t\t\t\t\tonmouseout=\"");
                  out.print( "this.className = '" + ( highlight ? "highlight" : "lowlight" ) + "';" );
                  out.write("\" class=\"");
                  out.print( highlight ? "highlight" : "lowlight" );
                  out.write("\">           \r\n");
                  out.write("\t\t\t\t\t\t\t\t<td class=\"name\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<a onclick=\"");
                  out.print( wrappedFavoriteItem.getFavoriteItem().getOnClick(-1, request) );
                  out.write("\" href=\"");
                  out.print( wrappedFavoriteItem.getFavoriteItem().getFavoriteLink(-1, request) );
                  out.write("\" value=\"");
                  out.print( wrappedFavoriteItem.getFavoriteItem().getResource().getResourceDescription() );
                  out.write("\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t\t<img border=\"0\" align=\"absmiddle\" src=\"");
                  out.print( wrappedFavoriteItem.getFavoriteItem().getSmallIconPath(request) );
                  out.write("\"/>\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t</a>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
                  //  input:toolTip
                  net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_padding_onclick_href_contentLocation_borderWidth.get(net.openvpn.als.input.tags.ToolTipTag.class);
                  _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
                  _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
                  _jspx_th_input_toolTip_0.setOnclick( wrappedFavoriteItem.getFavoriteItem().getOnClick(-1, request) );
                  _jspx_th_input_toolTip_0.setHref( wrappedFavoriteItem.getFavoriteItem().getFavoriteLink(-1, request) );
                  _jspx_th_input_toolTip_0.setContentLocation( "/resourceInformation.do?resourceId=" + wrappedFavoriteItem.getFavoriteItem().getResource().getResourceId() + "&resourceType=" +  wrappedFavoriteItem.getFavoriteItem().getResource().getResourceType().getResourceTypeId() );
                  _jspx_th_input_toolTip_0.setWidth("400");
                  _jspx_th_input_toolTip_0.setBorderWidth("0");
                  _jspx_th_input_toolTip_0.setPadding("0");
                  int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
                  if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_input_toolTip_0.doInitBody();
                    }
                    do {
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t\t");
                      if (_jspx_meth_bean_write_0(_jspx_th_input_toolTip_0, _jspx_page_context))
                        return;
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t");
                      int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_input_toolTip_width_padding_onclick_href_contentLocation_borderWidth.reuse(_jspx_th_input_toolTip_0);
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t</td>\r\n");
                  out.write("\t\t\t\t\t\t\t\t<td class=\"actions\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
                  if (_jspx_meth_tiles_insert_1(_jspx_th_logic_iterate_0, _jspx_page_context))
                    return;
                  out.write("\t\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t\t</td>\r\n");
                  out.write("\t\t\t\t\t\t\t</tr>\r\n");
                  out.write("\t\t\t\t\t\t\t");
highlight = !highlight;
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
                  wrappedFavoriteItem = (net.openvpn.als.navigation.WrappedFavoriteItem) _jspx_page_context.findAttribute("wrappedFavoriteItem");
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
              out.write("\r\n");
              out.write("\t\t\t\t\t</tbody>\t\t\t\r\n");
              out.write("\t\t\t\t\t<tfoot>\t\t\t\r\n");
              out.write("\t\t\t\t\t\t<tr>\r\n");
              out.write("\t\t\t\t\t\t\t<td colspan=\"2\">\r\n");
              out.write("\t\t\t\t\t\t\t\t<div class=\"pager_navigation\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t");
              if (_jspx_meth_table_navigation_0(_jspx_th_logic_equal_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t\t<div class=\"pager_pages\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t");
              if (_jspx_meth_table_pageSize_0(_jspx_th_logic_equal_0, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t</tr>\r\n");
              out.write("\t\t\t\t\t</tfoot>\r\n");
              out.write("\t\t\t\t</table>\r\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_equal_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_logic_equal_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_equal_value_property_name.reuse(_jspx_th_logic_equal_0);
          out.write("\r\n");
          out.write("\t\t\t");
          //  logic:equal
          org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_2 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_property_name.get(org.apache.struts.taglib.logic.EqualTag.class);
          _jspx_th_logic_equal_2.setPageContext(_jspx_page_context);
          _jspx_th_logic_equal_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
          _jspx_th_logic_equal_2.setName("favoritesForm");
          _jspx_th_logic_equal_2.setProperty("selectedView");
          _jspx_th_logic_equal_2.setValue( AbstractResourcesForm.ICONS_VIEW );
          int _jspx_eval_logic_equal_2 = _jspx_th_logic_equal_2.doStartTag();
          if (_jspx_eval_logic_equal_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\t\t\r\n");
              out.write("\t\t\t\t<div class=\"icons\">\r\n");
              out.write("\t\t\t\t\t<ul>\r\n");
              out.write("\t\t\t\t\t\t");
              //  logic:iterate
              org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_1 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_indexId_id.get(org.apache.struts.taglib.logic.IterateTag.class);
              _jspx_th_logic_iterate_1.setPageContext(_jspx_page_context);
              _jspx_th_logic_iterate_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_2);
              _jspx_th_logic_iterate_1.setName("favoritesForm");
              _jspx_th_logic_iterate_1.setProperty("pager.pageItems");
              _jspx_th_logic_iterate_1.setId("wrappedFavoriteItem");
              _jspx_th_logic_iterate_1.setType("net.openvpn.als.navigation.WrappedFavoriteItem");
              _jspx_th_logic_iterate_1.setIndexId("i");
              int _jspx_eval_logic_iterate_1 = _jspx_th_logic_iterate_1.doStartTag();
              if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                net.openvpn.als.navigation.WrappedFavoriteItem wrappedFavoriteItem = null;
                java.lang.Integer i = null;
                if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_logic_iterate_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_logic_iterate_1.doInitBody();
                }
                wrappedFavoriteItem = (net.openvpn.als.navigation.WrappedFavoriteItem) _jspx_page_context.findAttribute("wrappedFavoriteItem");
                i = (java.lang.Integer) _jspx_page_context.findAttribute("i");
                do {
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t<li>\r\n");
                  out.write("\t\t\t\t\t\t\t\t<div class=\"icon\" onclick=\"new Effect.Highlight(this);\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<div class=\"image\">\t\t\t\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t\t");
                  //  input:toolTip
                  net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.get(net.openvpn.als.input.tags.ToolTipTag.class);
                  _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
                  _jspx_th_input_toolTip_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
                  _jspx_th_input_toolTip_1.setOnclick( wrappedFavoriteItem.getFavoriteItem().getOnClick(-1, request) );
                  _jspx_th_input_toolTip_1.setHref( wrappedFavoriteItem.getFavoriteItem().getFavoriteLink(-1, request) );
                  _jspx_th_input_toolTip_1.setContentLocation( "/resourceInformation.do?resourceId=" + wrappedFavoriteItem.getFavoriteItem().getResource().getResourceId() + "&resourceType=" + wrappedFavoriteItem.getFavoriteItem().getResource().getResourceType().getResourceTypeId() );
                  _jspx_th_input_toolTip_1.setWidth("400");
                  _jspx_th_input_toolTip_1.setBorderWidth("0");
                  _jspx_th_input_toolTip_1.setPadding("0");
                  _jspx_th_input_toolTip_1.setOnclick( wrappedFavoriteItem.getFavoriteItem().getOnClick(-1, request) );
                  _jspx_th_input_toolTip_1.setHref( wrappedFavoriteItem.getFavoriteItem().getFavoriteLink(-1, request) );
                  _jspx_th_input_toolTip_1.setValue( wrappedFavoriteItem.getFavoriteItem().getResource().getResourceDescription() );
                  int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
                  if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_input_toolTip_1.doInitBody();
                    }
                    do {
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t\t\t<img border=\"0\" align=\"absmiddle\" src=\"");
                      out.print( wrappedFavoriteItem.getFavoriteItem().getLargeIconPath(request) );
                      out.write("\"/>\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t\t");
                      int evalDoAfterBody = _jspx_th_input_toolTip_1.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_input_toolTip_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_input_toolTip_width_value_padding_onclick_onclick_href_href_contentLocation_borderWidth.reuse(_jspx_th_input_toolTip_1);
                  out.write("\t\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<div class=\"text\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t\t");
                  //  input:toolTip
                  net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_2 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.get(net.openvpn.als.input.tags.ToolTipTag.class);
                  _jspx_th_input_toolTip_2.setPageContext(_jspx_page_context);
                  _jspx_th_input_toolTip_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
                  _jspx_th_input_toolTip_2.setContentLocation( "/resourceInformation.do?resourceId=" + wrappedFavoriteItem.getFavoriteItem().getResource().getResourceId() + "&resourceType=" + wrappedFavoriteItem.getFavoriteItem().getResource().getResourceType().getResourceTypeId() );
                  _jspx_th_input_toolTip_2.setWidth("400");
                  _jspx_th_input_toolTip_2.setBorderWidth("0");
                  _jspx_th_input_toolTip_2.setPadding("0");
                  _jspx_th_input_toolTip_2.setOnclick( wrappedFavoriteItem.getFavoriteItem().getOnClick(-1, request) );
                  _jspx_th_input_toolTip_2.setHref( wrappedFavoriteItem.getFavoriteItem().getFavoriteLink(-1, request) );
                  _jspx_th_input_toolTip_2.setValue( wrappedFavoriteItem.getFavoriteItem().getResource().getResourceDescription() );
                  int _jspx_eval_input_toolTip_2 = _jspx_th_input_toolTip_2.doStartTag();
                  if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                    if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                      out = _jspx_page_context.pushBody();
                      _jspx_th_input_toolTip_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                      _jspx_th_input_toolTip_2.doInitBody();
                    }
                    do {
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t\t\t");
                      if (_jspx_meth_bean_write_1(_jspx_th_input_toolTip_2, _jspx_page_context))
                        return;
                      out.write("\r\n");
                      out.write("\t\t\t\t\t\t\t\t\t\t");
                      int evalDoAfterBody = _jspx_th_input_toolTip_2.doAfterBody();
                      if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                        break;
                    } while (true);
                    if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                      out = _jspx_page_context.popBody();
                  }
                  if (_jspx_th_input_toolTip_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                    return;
                  _jspx_tagPool_input_toolTip_width_value_padding_onclick_href_contentLocation_borderWidth.reuse(_jspx_th_input_toolTip_2);
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t<div class=\"other\">\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
 
									String ai = wrappedFavoriteItem.getFavoriteItem().getLargeIconAdditionalIcon(request);
									if(!ai.equals("")) { 
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t\t<img src=\"");
                  out.print( wrappedFavoriteItem.getFavoriteItem().getLargeIconAdditionalIcon(request) );
                  out.write("\"/>\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
 } 
                  out.write("\r\n");
                  out.write("\t\t\t\t\t\t\t\t\t</div>\r\n");
                  out.write("\t\t\t\t\t\t\t\t</div>\t\t\t\t\t\t\t\t\r\n");
                  out.write("\t\t\t\t\t\t\t</li>\r\n");
                  out.write("\t\t\t\t\t\t");
                  int evalDoAfterBody = _jspx_th_logic_iterate_1.doAfterBody();
                  wrappedFavoriteItem = (net.openvpn.als.navigation.WrappedFavoriteItem) _jspx_page_context.findAttribute("wrappedFavoriteItem");
                  i = (java.lang.Integer) _jspx_page_context.findAttribute("i");
                  if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                    break;
                } while (true);
                if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
                  out = _jspx_page_context.popBody();
              }
              if (_jspx_th_logic_iterate_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_logic_iterate_type_property_name_indexId_id.reuse(_jspx_th_logic_iterate_1);
              out.write("\t\t\t\t\t\t\r\n");
              out.write("\t\t\t\t\t</ul>\r\n");
              out.write("\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t<div class=\"pager_navigation\">\r\n");
              out.write("\t\t\t\t\t");
              if (_jspx_meth_table_navigation_1(_jspx_th_logic_equal_2, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t<div class=\"pager_pages\">\r\n");
              out.write("\t\t\t\t\t");
              if (_jspx_meth_table_pageSize_1(_jspx_th_logic_equal_2, _jspx_page_context))
                return;
              out.write("\r\n");
              out.write("\t\t\t\t</div>\r\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_equal_2.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_logic_equal_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_equal_value_property_name.reuse(_jspx_th_logic_equal_2);
          out.write("\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_core_form_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_core_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_core_form_onsubmit_method_action.reuse(_jspx_th_core_form_0);
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

  private boolean _jspx_meth_html_hidden_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_0 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_0.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_hidden_0.setProperty("selectedItem");
    int _jspx_eval_html_hidden_0 = _jspx_th_html_hidden_0.doStartTag();
    if (_jspx_th_html_hidden_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_0);
    return false;
  }

  private boolean _jspx_meth_html_hidden_1(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_1 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_1.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_hidden_1.setProperty("actionTarget");
    int _jspx_eval_html_hidden_1 = _jspx_th_html_hidden_1.doStartTag();
    if (_jspx_th_html_hidden_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_1);
    return false;
  }

  private boolean _jspx_meth_html_hidden_2(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_2 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_2.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_hidden_2.setProperty("selectedView");
    int _jspx_eval_html_hidden_2 = _jspx_th_html_hidden_2.doStartTag();
    if (_jspx_th_html_hidden_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_2);
    return false;
  }

  private boolean _jspx_meth_tiles_insert_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_tiles_insert_0.setFlush(false);
    _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/tiles/filterAndView.jspf");
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\r\n");
        out.write("\t\t\t\t");
        if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t");
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
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_0 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_type_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_0);
    _jspx_th_tiles_put_0.setName("actionLink");
    _jspx_th_tiles_put_0.setBeanName("actionLink");
    _jspx_th_tiles_put_0.setType("java.lange.String");
    int _jspx_eval_tiles_put_0 = _jspx_th_tiles_put_0.doStartTag();
    if (_jspx_th_tiles_put_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_type_name_beanName_nobody.reuse(_jspx_th_tiles_put_0);
    return false;
  }

  private boolean _jspx_meth_table_columnHeader_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:columnHeader
    net.openvpn.als.table.tags.ColumnHeaderTag _jspx_th_table_columnHeader_0 = (net.openvpn.als.table.tags.ColumnHeaderTag) _jspx_tagPool_table_columnHeader_styleClass_pagerProperty_pagerName_page_columnIndex.get(net.openvpn.als.table.tags.ColumnHeaderTag.class);
    _jspx_th_table_columnHeader_0.setPageContext(_jspx_page_context);
    _jspx_th_table_columnHeader_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
    _jspx_th_table_columnHeader_0.setPage("");
    _jspx_th_table_columnHeader_0.setPagerName("favoritesForm");
    _jspx_th_table_columnHeader_0.setPagerProperty("pager");
    _jspx_th_table_columnHeader_0.setColumnIndex("0");
    _jspx_th_table_columnHeader_0.setStyleClass("columnHeader");
    int _jspx_eval_table_columnHeader_0 = _jspx_th_table_columnHeader_0.doStartTag();
    if (_jspx_eval_table_columnHeader_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_table_columnHeader_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_table_columnHeader_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_table_columnHeader_0.doInitBody();
      }
      do {
        out.write("\t\t\t\t\t\t\t\r\n");
        out.write("\t\t\t\t\t\t\t\t\t");
        if (_jspx_meth_bean_message_0(_jspx_th_table_columnHeader_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t\t\t\t");
        int evalDoAfterBody = _jspx_th_table_columnHeader_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_table_columnHeader_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_table_columnHeader_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_columnHeader_styleClass_pagerProperty_pagerName_page_columnIndex.reuse(_jspx_th_table_columnHeader_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_table_columnHeader_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_table_columnHeader_0);
    _jspx_th_bean_message_0.setKey("favorites.name");
    _jspx_th_bean_message_0.setBundle("navigation");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
    _jspx_th_bean_message_1.setKey("favorites.actions");
    _jspx_th_bean_message_1.setBundle("navigation");
    int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
    if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
    return false;
  }

  private boolean _jspx_meth_bean_message_2(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_1);
    _jspx_th_bean_message_2.setKey("favorites.noFavorites.text");
    _jspx_th_bean_message_2.setBundle("navigation");
    int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
    if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
    return false;
  }

  private boolean _jspx_meth_bean_write_0(javax.servlet.jsp.tagext.JspTag _jspx_th_input_toolTip_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:write
    org.apache.struts.taglib.bean.WriteTag _jspx_th_bean_write_0 = (org.apache.struts.taglib.bean.WriteTag) _jspx_tagPool_bean_write_property_name_nobody.get(org.apache.struts.taglib.bean.WriteTag.class);
    _jspx_th_bean_write_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_write_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_0);
    _jspx_th_bean_write_0.setName("wrappedFavoriteItem");
    _jspx_th_bean_write_0.setProperty("favoriteItem.resource.resourceName");
    int _jspx_eval_bean_write_0 = _jspx_th_bean_write_0.doStartTag();
    if (_jspx_th_bean_write_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_write_property_name_nobody.reuse(_jspx_th_bean_write_0);
    return false;
  }

  private boolean _jspx_meth_tiles_insert_1(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_iterate_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_1 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_1.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
    _jspx_th_tiles_insert_1.setFlush(false);
    _jspx_th_tiles_insert_1.setPage("/WEB-INF/jsp/tiles/tableItemActionBar.jspf");
    int _jspx_eval_tiles_insert_1 = _jspx_th_tiles_insert_1.doStartTag();
    if (_jspx_eval_tiles_insert_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\r\n");
        out.write("\t\t\t\t\t\t\t\t\t\t");
        if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_1, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t\t\t\t\t\t");
        if (_jspx_meth_tiles_put_2(_jspx_th_tiles_insert_1, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t\t\t\t\t\t");
        if (_jspx_meth_tiles_put_3(_jspx_th_tiles_insert_1, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t\t\t\t\t");
        int evalDoAfterBody = _jspx_th_tiles_insert_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_tiles_insert_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_insert_page_flush.reuse(_jspx_th_tiles_insert_1);
    return false;
  }

  private boolean _jspx_meth_tiles_put_1(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_1 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_1.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_1);
    _jspx_th_tiles_put_1.setName("pager");
    _jspx_th_tiles_put_1.setBeanName("favoritesForm");
    _jspx_th_tiles_put_1.setBeanProperty("pager");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanProperty_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }

  private boolean _jspx_meth_tiles_put_2(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_2 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_name_beanName_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_2.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_1);
    _jspx_th_tiles_put_2.setName("rowIndex");
    _jspx_th_tiles_put_2.setBeanName("i");
    int _jspx_eval_tiles_put_2 = _jspx_th_tiles_put_2.doStartTag();
    if (_jspx_th_tiles_put_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_2);
    return false;
  }

  private boolean _jspx_meth_tiles_put_3(javax.servlet.jsp.tagext.JspTag _jspx_th_tiles_insert_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:put
    org.apache.struts.taglib.tiles.PutTag _jspx_th_tiles_put_3 = (org.apache.struts.taglib.tiles.PutTag) _jspx_tagPool_tiles_put_value_name_nobody.get(org.apache.struts.taglib.tiles.PutTag.class);
    _jspx_th_tiles_put_3.setPageContext(_jspx_page_context);
    _jspx_th_tiles_put_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_tiles_insert_1);
    _jspx_th_tiles_put_3.setName("policyLaunching");
    _jspx_th_tiles_put_3.setValue("true");
    int _jspx_eval_tiles_put_3 = _jspx_th_tiles_put_3.doStartTag();
    if (_jspx_th_tiles_put_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_value_name_nobody.reuse(_jspx_th_tiles_put_3);
    return false;
  }

  private boolean _jspx_meth_table_navigation_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:navigation
    net.openvpn.als.table.tags.NavigationTag _jspx_th_table_navigation_0 = (net.openvpn.als.table.tags.NavigationTag) _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody.get(net.openvpn.als.table.tags.NavigationTag.class);
    _jspx_th_table_navigation_0.setPageContext(_jspx_page_context);
    _jspx_th_table_navigation_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
    _jspx_th_table_navigation_0.setSelectedStyleClass("pagerSelected");
    _jspx_th_table_navigation_0.setPagerName("favoritesForm");
    _jspx_th_table_navigation_0.setPagerProperty("pager");
    _jspx_th_table_navigation_0.setStyleClass("pagerEnabled");
    _jspx_th_table_navigation_0.setDisabledStyleClass("pagerDisabled");
    int _jspx_eval_table_navigation_0 = _jspx_th_table_navigation_0.doStartTag();
    if (_jspx_th_table_navigation_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody.reuse(_jspx_th_table_navigation_0);
    return false;
  }

  private boolean _jspx_meth_table_pageSize_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:pageSize
    net.openvpn.als.table.tags.PageSizeTag _jspx_th_table_pageSize_0 = (net.openvpn.als.table.tags.PageSizeTag) _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody.get(net.openvpn.als.table.tags.PageSizeTag.class);
    _jspx_th_table_pageSize_0.setPageContext(_jspx_page_context);
    _jspx_th_table_pageSize_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_0);
    _jspx_th_table_pageSize_0.setPagerName("favoritesForm");
    _jspx_th_table_pageSize_0.setPagerProperty("pager");
    _jspx_th_table_pageSize_0.setStyleClass("pagerEnabled");
    int _jspx_eval_table_pageSize_0 = _jspx_th_table_pageSize_0.doStartTag();
    if (_jspx_th_table_pageSize_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody.reuse(_jspx_th_table_pageSize_0);
    return false;
  }

  private boolean _jspx_meth_bean_write_1(javax.servlet.jsp.tagext.JspTag _jspx_th_input_toolTip_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:write
    org.apache.struts.taglib.bean.WriteTag _jspx_th_bean_write_1 = (org.apache.struts.taglib.bean.WriteTag) _jspx_tagPool_bean_write_property_name_nobody.get(org.apache.struts.taglib.bean.WriteTag.class);
    _jspx_th_bean_write_1.setPageContext(_jspx_page_context);
    _jspx_th_bean_write_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_2);
    _jspx_th_bean_write_1.setName("wrappedFavoriteItem");
    _jspx_th_bean_write_1.setProperty("favoriteItem.resource.resourceName");
    int _jspx_eval_bean_write_1 = _jspx_th_bean_write_1.doStartTag();
    if (_jspx_th_bean_write_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_write_property_name_nobody.reuse(_jspx_th_bean_write_1);
    return false;
  }

  private boolean _jspx_meth_table_navigation_1(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:navigation
    net.openvpn.als.table.tags.NavigationTag _jspx_th_table_navigation_1 = (net.openvpn.als.table.tags.NavigationTag) _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody.get(net.openvpn.als.table.tags.NavigationTag.class);
    _jspx_th_table_navigation_1.setPageContext(_jspx_page_context);
    _jspx_th_table_navigation_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_2);
    _jspx_th_table_navigation_1.setSelectedStyleClass("pagerSelected");
    _jspx_th_table_navigation_1.setPagerName("favoritesForm");
    _jspx_th_table_navigation_1.setPagerProperty("pager");
    _jspx_th_table_navigation_1.setStyleClass("pagerEnabled");
    _jspx_th_table_navigation_1.setDisabledStyleClass("pagerDisabled");
    int _jspx_eval_table_navigation_1 = _jspx_th_table_navigation_1.doStartTag();
    if (_jspx_th_table_navigation_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_navigation_styleClass_selectedStyleClass_pagerProperty_pagerName_disabledStyleClass_nobody.reuse(_jspx_th_table_navigation_1);
    return false;
  }

  private boolean _jspx_meth_table_pageSize_1(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_equal_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  table:pageSize
    net.openvpn.als.table.tags.PageSizeTag _jspx_th_table_pageSize_1 = (net.openvpn.als.table.tags.PageSizeTag) _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody.get(net.openvpn.als.table.tags.PageSizeTag.class);
    _jspx_th_table_pageSize_1.setPageContext(_jspx_page_context);
    _jspx_th_table_pageSize_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_equal_2);
    _jspx_th_table_pageSize_1.setPagerName("favoritesForm");
    _jspx_th_table_pageSize_1.setPagerProperty("pager");
    _jspx_th_table_pageSize_1.setStyleClass("pagerEnabled");
    int _jspx_eval_table_pageSize_1 = _jspx_th_table_pageSize_1.doStartTag();
    if (_jspx_th_table_pageSize_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_table_pageSize_styleClass_pagerProperty_pagerName_nobody.reuse(_jspx_th_table_pageSize_1);
    return false;
  }
}
