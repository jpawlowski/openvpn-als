package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.navigation.NavigationManager;
import net.openvpn.als.table.TableItemActionMenuTree;
import java.util.Iterator;
import net.openvpn.als.core.MenuItem;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.core.AvailableMenuItem;
import java.util.List;
import net.openvpn.als.table.TableItemAction;
import java.util.ArrayList;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.boot.Util;

public final class tableItemActionBar_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_define_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_themePath_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_styleId_onclick_href_contentLocation_additionalAttributeValue_additionalAttributeName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_equal_value_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_target_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_define_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_themePath_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_styleId_onclick_href_contentLocation_additionalAttributeValue_additionalAttributeName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_equal_value_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_target_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.release();
    _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody.release();
    _jspx_tagPool_bean_define_id.release();
    _jspx_tagPool_core_themePath_nobody.release();
    _jspx_tagPool_input_toolTip_width_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.release();
    _jspx_tagPool_input_toolTip_width_styleId_onclick_href_contentLocation_additionalAttributeValue_additionalAttributeName.release();
    _jspx_tagPool_logic_equal_value_name.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_name_beanName_nobody.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_input_toolTip_target_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.release();
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
      _jspx_th_tiles_useAttribute_1.setName("rowIndex");
      _jspx_th_tiles_useAttribute_1.setScope("request");
      _jspx_th_tiles_useAttribute_1.setClassname("java.lang.Integer");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      java.lang.Integer rowIndex = null;
      rowIndex = (java.lang.Integer) _jspx_page_context.findAttribute("rowIndex");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_2 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_2.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_2.setParent(null);
      _jspx_th_tiles_useAttribute_2.setName("policyLaunching");
      _jspx_th_tiles_useAttribute_2.setScope("request");
      _jspx_th_tiles_useAttribute_2.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_2 = _jspx_th_tiles_useAttribute_2.doStartTag();
      if (_jspx_th_tiles_useAttribute_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_2);
      java.lang.String policyLaunching = null;
      policyLaunching = (java.lang.String) _jspx_page_context.findAttribute("policyLaunching");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_3 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_3.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_3.setParent(null);
      _jspx_th_tiles_useAttribute_3.setIgnore(true);
      _jspx_th_tiles_useAttribute_3.setId("actionsTableId");
      _jspx_th_tiles_useAttribute_3.setName("actionsTableId");
      _jspx_th_tiles_useAttribute_3.setScope("request");
      _jspx_th_tiles_useAttribute_3.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_3 = _jspx_th_tiles_useAttribute_3.doStartTag();
      if (_jspx_th_tiles_useAttribute_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody.reuse(_jspx_th_tiles_useAttribute_3);
      java.lang.String actionsTableId = null;
      actionsTableId = (java.lang.String) _jspx_page_context.findAttribute("actionsTableId");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_4 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_4.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_4.setParent(null);
      _jspx_th_tiles_useAttribute_4.setIgnore(true);
      _jspx_th_tiles_useAttribute_4.setId("displayBlanks");
      _jspx_th_tiles_useAttribute_4.setName("displayBlanks");
      _jspx_th_tiles_useAttribute_4.setScope("request");
      _jspx_th_tiles_useAttribute_4.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_4 = _jspx_th_tiles_useAttribute_4.doStartTag();
      if (_jspx_th_tiles_useAttribute_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_scope_name_ignore_id_classname_nobody.reuse(_jspx_th_tiles_useAttribute_4);
      java.lang.String displayBlanks = null;
      displayBlanks = (java.lang.String) _jspx_page_context.findAttribute("displayBlanks");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  bean:define
      org.apache.struts.taglib.bean.DefineTag _jspx_th_bean_define_0 = (org.apache.struts.taglib.bean.DefineTag) _jspx_tagPool_bean_define_id.get(org.apache.struts.taglib.bean.DefineTag.class);
      _jspx_th_bean_define_0.setPageContext(_jspx_page_context);
      _jspx_th_bean_define_0.setParent(null);
      _jspx_th_bean_define_0.setId("themePath");
      int _jspx_eval_bean_define_0 = _jspx_th_bean_define_0.doStartTag();
      if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_bean_define_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_bean_define_0.doInitBody();
        }
        do {
          if (_jspx_meth_core_themePath_0(_jspx_th_bean_define_0, _jspx_page_context))
            return;
          int evalDoAfterBody = _jspx_th_bean_define_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_bean_define_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_bean_define_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_define_id.reuse(_jspx_th_bean_define_0);
      java.lang.String themePath = null;
      themePath = (java.lang.String) _jspx_page_context.findAttribute("themePath");
      out.write("\r\n");
      out.write("<div class=\"tableItemActionBar\">\r\n");
 
	actionsTableId = actionsTableId == null ? pager.getModel().getId() : actionsTableId;
	TableItemActionMenuTree tree = (TableItemActionMenuTree)
		NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
	MenuItem parent = tree.getMenuItem(actionsTableId);
	if(parent == null) {

      out.write("\r\n");
      out.write("\tError. No table item action parent for ");
      out.print( actionsTableId );
      out.write('\r');
      out.write('\n');
 	} else { 
		List available = tree.rebuildMenus(parent, request);
		List important = new ArrayList();
		List unimportant = new ArrayList();
		for(Iterator i = available.iterator(); i.hasNext(); ) {
			AvailableTableItemAction ami = (AvailableTableItemAction)i.next();			
			ami.init(pager, rowIndex.intValue());
			TableItemAction def = (TableItemAction)ami.getMenuItem();
			if(def.isImportant()) {
				important.add(ami);
			}
			else {
				unimportant.add(ami);
			}	
		}		
		for(Iterator i = important.iterator(); i.hasNext(); ) {
			AvailableTableItemAction ami = (AvailableTableItemAction)i.next();
			String descriptionKey = "tableItemAction." + actionsTableId + "." + ami.getMenuItem().getId() + ".description";
			TableItemAction def = (TableItemAction)ami.getMenuItem();

      out.write("\r\n");
      out.write("\t\t\t<span class=\"importantAction\">\r\n");

			if(ami.isEnabled()) {
				String contentLocation = ami.getToolTipContentLocation();
				String width = String.valueOf(ami.getToolTipWidth());
				if(contentLocation.equals("")) {

      out.write("\t\t\t\r\n");
      out.write("\t\t\t\t");
      //  input:toolTip
      net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.get(net.openvpn.als.input.tags.ToolTipTag.class);
      _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
      _jspx_th_input_toolTip_0.setParent(null);
      _jspx_th_input_toolTip_0.setWidth( width );
      _jspx_th_input_toolTip_0.setAdditionalAttributeName( ami.getAdditionalAttributeName() );
      _jspx_th_input_toolTip_0.setAdditionalAttributeValue( ami.getAdditionalAttributeValue() );
      _jspx_th_input_toolTip_0.setHref( ami.getPath() );
      _jspx_th_input_toolTip_0.setKey( descriptionKey );
      _jspx_th_input_toolTip_0.setBundle( ami.getMenuItem().getMessageResourcesKey() );
      _jspx_th_input_toolTip_0.setStyleId( "action_" + ami.getMenuItem().getId() );
      _jspx_th_input_toolTip_0.setOnclick( ami.getOnClick() );
      int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
      if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_input_toolTip_0.doInitBody();
        }
        do {
          out.write("\r\n");
          out.write("\t\t\t\t\t <img alt=\"\" src=\"");
          out.print( themePath  + "/images/actions/" + ami.getMenuItem().getId() + ".gif" );
          out.write("\"/>\r\n");
          out.write("\t\t\t\t");
          int evalDoAfterBody = _jspx_th_input_toolTip_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_input_toolTip_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_input_toolTip_width_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.reuse(_jspx_th_input_toolTip_0);
      out.write('\r');
      out.write('\n');

				} else { 
      out.write("\r\n");
      out.write("\t\t\t\t");
      //  input:toolTip
      net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_styleId_onclick_href_contentLocation_additionalAttributeValue_additionalAttributeName.get(net.openvpn.als.input.tags.ToolTipTag.class);
      _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
      _jspx_th_input_toolTip_1.setParent(null);
      _jspx_th_input_toolTip_1.setWidth(width );
      _jspx_th_input_toolTip_1.setAdditionalAttributeName( ami.getAdditionalAttributeName() );
      _jspx_th_input_toolTip_1.setContentLocation( contentLocation );
      _jspx_th_input_toolTip_1.setAdditionalAttributeValue( ami.getAdditionalAttributeValue() );
      _jspx_th_input_toolTip_1.setHref( ami.getPath() );
      _jspx_th_input_toolTip_1.setStyleId( "action_" + ami.getMenuItem().getId() );
      _jspx_th_input_toolTip_1.setOnclick( ami.getOnClick() );
      int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
      if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_input_toolTip_1.doInitBody();
        }
        do {
          out.write("\r\n");
          out.write("\t\t\t\t\t <img alt=\"\" src=\"");
          out.print( themePath  + "/images/actions/" + ami.getMenuItem().getId() + ".gif" );
          out.write("\"/>\r\n");
          out.write("\t\t\t\t");
          int evalDoAfterBody = _jspx_th_input_toolTip_1.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_input_toolTip_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_input_toolTip_width_styleId_onclick_href_contentLocation_additionalAttributeValue_additionalAttributeName.reuse(_jspx_th_input_toolTip_1);
      out.write('\r');
      out.write('\n');
				}
			} else { 
				if(displayBlanks == null || "true".equals(displayBlanks)) { 
      out.write("\t\t\t\t\t\r\n");
      out.write("\t\t\t\t\t<img class=\"blankAction\" alt=\"\" src=\"");
      out.print( themePath  + "/images/actions/blank.gif" );
      out.write("\"/>\r\n");
				}
			} 
      out.write("\t\t\t\r\n");
      out.write("\t\t\t</span>\r\n");

		}

      out.write("\r\n");
      out.write("\t\t");
      if (_jspx_meth_logic_equal_0(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');

		List newUnimportant = new ArrayList();
		for(Iterator i = unimportant.iterator(); i.hasNext(); ) {
			AvailableTableItemAction ami = (AvailableTableItemAction)i.next();
			if(ami.isEnabled()) {
				newUnimportant.add(ami);
			}
		}
		if(newUnimportant.size() > 0) { 
			String popupScript = "togglePopupBelowLeft(document.getElementById('unimportantActions_" + pager.getModel().getId() + "_" + rowIndex + "'), document.getElementById('unimportantActionsTrigger_" + pager.getModel().getId() + "_" + rowIndex + "')); event.cancelBubble = true;"; 
      out.write("\r\n");
      out.write("\t\t\t<span class=\"unimportantActionsTrigger\" id=\"");
      out.print( "unimportantActionsTrigger_" + pager.getModel().getId() + "_" + rowIndex  );
      out.write("\">\r\n");
      out.write("\t\t\t\t<a onclick=\"");
      out.print( popupScript );
      out.write("\" href=\"javascript: void(0);\">");
      if (_jspx_meth_bean_message_0(_jspx_page_context))
        return;
      out.write("</a>\r\n");
      out.write("\t\t\t</span>\r\n");
      out.write("\t\t\t<div class=\"unimportantActions\" style=\"display: none;\" id=\"");
      out.print( "unimportantActions_" + pager.getModel().getId() + "_" + rowIndex );
      out.write("\">\r\n");
      out.write("\t\t\t   <div class=\"unimportantActionsInner\">\r\n");
      out.write("\t\t\t\t\t<div class=\"text\">\r\n");
      out.write("\t\t\t\t\t");
      //  bean:message
      org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
      _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
      _jspx_th_bean_message_1.setParent(null);
      _jspx_th_bean_message_1.setKey( "tableItemActionBar.additionalActions" );
      _jspx_th_bean_message_1.setBundle("navigation");
      int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
      if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
      out.write(" <img border=\"0\" href=\"");
      out.print( "javascript:"+ popupScript );
      out.write("\" src=\"");
      out.print( themePath + "/images/close-reverse.gif" );
      out.write("\"/>\r\n");
      out.write("\t\t\t\t\t</div>\r\n");

			for(Iterator i = newUnimportant.iterator(); i.hasNext(); ) {
				AvailableTableItemAction ami = (AvailableTableItemAction)i.next();
				String nameKey = "tableItemAction." + actionsTableId + "." + ami.getMenuItem().getId() + ".name";
				String descriptionKey = "tableItemAction." + actionsTableId + "." + ami.getMenuItem().getId() + ".description";
				TableItemAction def = (TableItemAction)ami.getMenuItem();

      out.write("\r\n");
      out.write("\t\t\t\t<div class=\"unimportantAction\">\r\n");
      out.write("\t\t\t\t\t");
      //  input:toolTip
      net.openvpn.als.input.tags.ToolTipTag _jspx_th_input_toolTip_2 = (net.openvpn.als.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_target_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.get(net.openvpn.als.input.tags.ToolTipTag.class);
      _jspx_th_input_toolTip_2.setPageContext(_jspx_page_context);
      _jspx_th_input_toolTip_2.setParent(null);
      _jspx_th_input_toolTip_2.setAdditionalAttributeName( ami.getAdditionalAttributeName() );
      _jspx_th_input_toolTip_2.setAdditionalAttributeValue( ami.getAdditionalAttributeValue() );
      _jspx_th_input_toolTip_2.setHref( ami.getPath() );
      _jspx_th_input_toolTip_2.setKey( descriptionKey );
      _jspx_th_input_toolTip_2.setBundle( ami.getMenuItem().getMessageResourcesKey() );
      _jspx_th_input_toolTip_2.setStyleId( "action_" + ami.getMenuItem().getId() );
      _jspx_th_input_toolTip_2.setOnclick( ( ami.getOnClick().equals("") ? "" : ( ami.getOnClick() + ";" ) ) + "hidePopup(document.getElementById('unimportantActions_" + pager.getModel().getId() + "_" +  rowIndex + "'));" );
      _jspx_th_input_toolTip_2.setTarget( ami.getMenuItem().getTarget() == null ? "_self" : ami.getMenuItem().getTarget() );
      int _jspx_eval_input_toolTip_2 = _jspx_th_input_toolTip_2.doStartTag();
      if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_input_toolTip_2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_input_toolTip_2.doInitBody();
        }
        do {
          out.write("\r\n");
          out.write("\t\t\t\t\t\t<img alt=\"\" src=\"");
          out.print( themePath  + "/images/actions/" + ami.getMenuItem().getId() + ".gif" );
          out.write("\"/>\r\n");
          out.write("\t\t\t\t\t\t");
          //  bean:message
          org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
          _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
          _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_2);
          _jspx_th_bean_message_2.setKey( nameKey );
          _jspx_th_bean_message_2.setBundle( ami.getMenuItem().getMessageResourcesKey() );
          int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
          if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
          out.write("\r\n");
          out.write("\t\t\t\t\t");
          int evalDoAfterBody = _jspx_th_input_toolTip_2.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_input_toolTip_2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_input_toolTip_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_input_toolTip_target_styleId_onclick_key_href_bundle_additionalAttributeValue_additionalAttributeName.reuse(_jspx_th_input_toolTip_2);
      out.write("\r\n");
      out.write("\t\t\t\t</div>\r\n");

			}		

      out.write("\t\t\t\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t</div>\r\n");

	  	}
	}

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

  private boolean _jspx_meth_core_themePath_0(javax.servlet.jsp.tagext.JspTag _jspx_th_bean_define_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:themePath
    net.openvpn.als.core.tags.ThemePathTag _jspx_th_core_themePath_0 = (net.openvpn.als.core.tags.ThemePathTag) _jspx_tagPool_core_themePath_nobody.get(net.openvpn.als.core.tags.ThemePathTag.class);
    _jspx_th_core_themePath_0.setPageContext(_jspx_page_context);
    _jspx_th_core_themePath_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_bean_define_0);
    int _jspx_eval_core_themePath_0 = _jspx_th_core_themePath_0.doStartTag();
    if (_jspx_th_core_themePath_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_themePath_nobody.reuse(_jspx_th_core_themePath_0);
    return false;
  }

  private boolean _jspx_meth_logic_equal_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  logic:equal
    org.apache.struts.taglib.logic.EqualTag _jspx_th_logic_equal_0 = (org.apache.struts.taglib.logic.EqualTag) _jspx_tagPool_logic_equal_value_name.get(org.apache.struts.taglib.logic.EqualTag.class);
    _jspx_th_logic_equal_0.setPageContext(_jspx_page_context);
    _jspx_th_logic_equal_0.setParent(null);
    _jspx_th_logic_equal_0.setName("policyLaunching");
    _jspx_th_logic_equal_0.setValue("true");
    int _jspx_eval_logic_equal_0 = _jspx_th_logic_equal_0.doStartTag();
    if (_jspx_eval_logic_equal_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\t\t\t\t\t\t\r\n");
        out.write("\t\t\t");
        if (_jspx_meth_tiles_insert_0(_jspx_th_logic_equal_0, _jspx_page_context))
          return true;
        out.write("\t\t\r\n");
        out.write("\t\t");
        int evalDoAfterBody = _jspx_th_logic_equal_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_logic_equal_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_logic_equal_value_name.reuse(_jspx_th_logic_equal_0);
    return false;
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
    _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/tiles/policyLaunch.jspf");
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\t\t\r\n");
        out.write("\t\t\t\t");
        if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t");
        if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
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
    _jspx_th_tiles_put_1.setName("rowIndex");
    _jspx_th_tiles_put_1.setBeanName("rowIndex");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }

  private boolean _jspx_meth_bean_message_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent(null);
    _jspx_th_bean_message_0.setKey("tableItemActionBar.more");
    _jspx_th_bean_message_0.setBundle("navigation");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }
}
