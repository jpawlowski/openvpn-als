package org.apache.jsp.WEB_002dINF.jsp.content.properties;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.boot.PropertyDefinitionCategory;
import java.util.Iterator;
import com.sun.syndication.feed.rss.Category;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.boot.Util;
import net.openvpn.als.properties.forms.PropertiesForm;
import net.openvpn.als.properties.forms.AbstractPropertiesForm;

public final class propertyFormContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(3);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/tabs.tld");
    _jspx_dependants.add("/WEB-INF/navigation.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_hidden_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_iterate_type_property_name_id;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_themePath_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_insert_page_flush;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_put_name_beanName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_navigation_menuNotAvailable;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_empty_property_name;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_hidden_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_iterate_type_property_name_id = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_themePath_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_insert_page_flush = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_tiles_put_name_beanName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_navigation_menuNotAvailable = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_empty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_classname_nobody.release();
    _jspx_tagPool_html_hidden_property_nobody.release();
    _jspx_tagPool_logic_notEmpty_property_name.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_logic_iterate_type_property_name_id.release();
    _jspx_tagPool_core_themePath_nobody.release();
    _jspx_tagPool_tiles_insert_page_flush.release();
    _jspx_tagPool_tiles_put_name_beanName_nobody.release();
    _jspx_tagPool_navigation_menuNotAvailable.release();
    _jspx_tagPool_logic_empty_property_name.release();
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
      out.write("\t\t       \r\n");
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setName("form");
      _jspx_th_tiles_useAttribute_0.setClassname("net.openvpn.als.properties.forms.AbstractPropertiesForm");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      net.openvpn.als.properties.forms.AbstractPropertiesForm form = null;
      form = (net.openvpn.als.properties.forms.AbstractPropertiesForm) _jspx_page_context.findAttribute("form");
      out.write(' ');
      out.write('\r');
      out.write('\n');
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_1 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_1.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_1.setParent(null);
      _jspx_th_tiles_useAttribute_1.setName("actionMapping");
      _jspx_th_tiles_useAttribute_1.setClassname("org.apache.struts.action.ActionMapping");
      int _jspx_eval_tiles_useAttribute_1 = _jspx_th_tiles_useAttribute_1.doStartTag();
      if (_jspx_th_tiles_useAttribute_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_classname_nobody.reuse(_jspx_th_tiles_useAttribute_1);
      org.apache.struts.action.ActionMapping actionMapping = null;
      actionMapping = (org.apache.struts.action.ActionMapping) _jspx_page_context.findAttribute("actionMapping");
      out.write(" \r\n");
      out.write("\r\n");
      if (_jspx_meth_html_hidden_0(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');
      if (_jspx_meth_html_hidden_1(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');
      if (_jspx_meth_html_hidden_2(_jspx_page_context))
        return;
      out.write(' ');
      out.write('\r');
      out.write('\n');
      if (_jspx_meth_html_hidden_3(_jspx_page_context))
        return;
      out.write('	');
      out.write('\r');
      out.write('\n');
      if (_jspx_meth_html_hidden_4(_jspx_page_context))
        return;
      out.write("\t\t\r\n");
      //  logic:notEmpty
      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_0 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
      _jspx_th_logic_notEmpty_0.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEmpty_0.setParent(null);
      _jspx_th_logic_notEmpty_0.setName("form");
      _jspx_th_logic_notEmpty_0.setProperty("subCategories");
      int _jspx_eval_logic_notEmpty_0 = _jspx_th_logic_notEmpty_0.doStartTag();
      if (_jspx_eval_logic_notEmpty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("   \r\n");
          out.write("\t<div class=\"titled_dialog_content\">\r\n");
          out.write("\t\t<table>\r\n");
          out.write("\t\t\t<thead>\r\n");
          out.write("\t\t\t\t<tr>\r\n");
          out.write("\t\t\t\t\t<td>                         \r\n");
          out.write("\t\t\t\t\t\t");
          if (_jspx_meth_bean_message_0(_jspx_th_logic_notEmpty_0, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\t\t\t</td>\r\n");
          out.write("\t\t\t\t</tr>                          \r\n");
          out.write("\t\t\t</thead>\r\n");
          out.write("\t\t\t<tbody>\r\n");
          out.write("\t\t\t\t<tr>\r\n");
          out.write("\t\t\t\t\t<td>\r\n");
          out.write("\t\t\t\t\t\t<div id=\"subCategories\"> \r\n");
          out.write("\t\t\t\t\t\t\t<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n");
          out.write("\t\t\t\t\t\t\t\t");
          //  logic:iterate
          org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_0 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
          _jspx_th_logic_iterate_0.setPageContext(_jspx_page_context);
          _jspx_th_logic_iterate_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
          _jspx_th_logic_iterate_0.setId("subCategory");
          _jspx_th_logic_iterate_0.setName("form");
          _jspx_th_logic_iterate_0.setProperty("subCategories");
          _jspx_th_logic_iterate_0.setType("net.openvpn.als.boot.PropertyDefinitionCategory");
          int _jspx_eval_logic_iterate_0 = _jspx_th_logic_iterate_0.doStartTag();
          if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            net.openvpn.als.boot.PropertyDefinitionCategory subCategory = null;
            if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_logic_iterate_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_logic_iterate_0.doInitBody();
            }
            subCategory = (net.openvpn.als.boot.PropertyDefinitionCategory) _jspx_page_context.findAttribute("subCategory");
            do {
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t");
 	String key = "category." + subCategory.getId() + ".name";
											String descriptionKey = "category." + subCategory.getId() + ".description";
										
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t\t<tr>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t<td class=\"link\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t<a href=\"");
              out.print( "?actionTarget=displayCategory&newSelectedCategory=" + subCategory.getId() );
              out.write("\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"image\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t\t<img src='");
              if (_jspx_meth_core_themePath_0(_jspx_th_logic_iterate_0, _jspx_page_context))
                return;
              out.write("/images/");
              out.print( subCategory.getImagePath() );
              out.write("'/>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"text\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t\t");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
              _jspx_th_bean_message_1.setKey( key );
              _jspx_th_bean_message_1.setBundle( subCategory.getBundle() );
              int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
              if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t\t</div>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t</a>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t<td class=\"description\">\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t\t");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
              _jspx_th_bean_message_2.setKey( descriptionKey );
              _jspx_th_bean_message_2.setBundle( subCategory.getBundle() );
              int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
              if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
              out.write("\r\n");
              out.write("\t\t\t\t\t\t\t\t\t\t</td>\r\n");
              out.write("\t\t\t\t\t\t\t\t\t</tr>\r\n");
              out.write("\t\t\t\t\t\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_iterate_0.doAfterBody();
              subCategory = (net.openvpn.als.boot.PropertyDefinitionCategory) _jspx_page_context.findAttribute("subCategory");
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_logic_iterate_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_logic_iterate_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_iterate_type_property_name_id.reuse(_jspx_th_logic_iterate_0);
          out.write("\t\t\t\t\t\t\t\t\t\t\r\n");
          out.write("\t\t\t\t\t\t\t</table>\r\n");
          out.write("\t\t\t\t\t\t</div>\r\n");
          out.write("\t\t\t\t\t</td>\r\n");
          out.write("\t\t\t\t</tr>\r\n");
          out.write("\t\t\t</tbody>\r\n");
          out.write("\t\t</table>\r\n");
          out.write("\t</div>\r\n");
          int evalDoAfterBody = _jspx_th_logic_notEmpty_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEmpty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_0);
      out.write("\t\t\t\r\n");
      out.write("\r\n");
      //  logic:notEmpty
      org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_1 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
      _jspx_th_logic_notEmpty_1.setPageContext(_jspx_page_context);
      _jspx_th_logic_notEmpty_1.setParent(null);
      _jspx_th_logic_notEmpty_1.setName("form");
      _jspx_th_logic_notEmpty_1.setProperty("propertyItems");
      int _jspx_eval_logic_notEmpty_1 = _jspx_th_logic_notEmpty_1.doStartTag();
      if (_jspx_eval_logic_notEmpty_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write(" \t\t\t\r\n");
          out.write("\t");
 boolean disabled = !form.getEnabled(); 
          out.write("\t\t\t\t\r\n");
          out.write("\t<div class=\"tabSet\">         \r\n");
          out.write("\t\t<div class=\"tabHeadings\">\r\n");
          out.write("\t\t    <ul>\t\t\t    \r\n");
          out.write("\t\t\t\t");
          //  logic:iterate
          org.apache.struts.taglib.logic.IterateTag _jspx_th_logic_iterate_1 = (org.apache.struts.taglib.logic.IterateTag) _jspx_tagPool_logic_iterate_type_property_name_id.get(org.apache.struts.taglib.logic.IterateTag.class);
          _jspx_th_logic_iterate_1.setPageContext(_jspx_page_context);
          _jspx_th_logic_iterate_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
          _jspx_th_logic_iterate_1.setId("category");
          _jspx_th_logic_iterate_1.setName("form");
          _jspx_th_logic_iterate_1.setProperty("categoryDefinitions");
          _jspx_th_logic_iterate_1.setType("net.openvpn.als.boot.PropertyDefinitionCategory");
          int _jspx_eval_logic_iterate_1 = _jspx_th_logic_iterate_1.doStartTag();
          if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            net.openvpn.als.boot.PropertyDefinitionCategory category = null;
            if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_logic_iterate_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_logic_iterate_1.doInitBody();
            }
            category = (net.openvpn.als.boot.PropertyDefinitionCategory) _jspx_page_context.findAttribute("category");
            do {
              out.write("\r\n");
              out.write("\t\t\t\t\t");
 	boolean selected = form.getSelectedCategory() == category.getId() ; 
						String key = "category." + category.getId() + ".name"; 
              out.write("\r\n");
              out.write("\t\t\t\t\t<li class=\"");
              out.print( selected  ? "selectedTab" : "hiddenTab" );
              out.write("\">\r\n");
              out.write("\t\t\t\t\t\t<a ");
              out.print( selected ? "class=\"currentTab\"" : "" );
              out.write(" href=\"javascript: setCategory('");
              out.print( String.valueOf(category.getId()) );
              out.write("')\">");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_1);
              _jspx_th_bean_message_3.setKey( key );
              _jspx_th_bean_message_3.setBundle( category.getBundle() );
              int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
              if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_3);
              out.write("</a>\t\t\t\t\t\t\t\t\t\r\n");
              out.write("\t\t\t\t\t</li>\r\n");
              out.write("\t\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_iterate_1.doAfterBody();
              category = (net.openvpn.als.boot.PropertyDefinitionCategory) _jspx_page_context.findAttribute("category");
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_logic_iterate_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_logic_iterate_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_iterate_type_property_name_id.reuse(_jspx_th_logic_iterate_1);
          out.write("\t\t\t\t\r\n");
          out.write("\t\t    </ul>\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t<div class=\"tabPanel\">\t\r\n");
          out.write("\t\t\t<div class=\"tabContent\">\t\r\n");
          out.write("\t\t\t\t<div id=\"propertyItems\">\r\n");
          out.write("\t\t\t\t\t");
          if (_jspx_meth_tiles_insert_0(_jspx_th_logic_notEmpty_1, _jspx_page_context))
            return;
          out.write("\r\n");
          out.write("\t\t\t\t</div>\r\n");
          out.write("\t\t\t</div>\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t</div>\r\n");
          out.write("\t\t\t\t\t\r\n");
          out.write("\t<div class=\"button_bar\">\r\n");
          out.write("\t\t<div class=\"formButton\">\r\n");
          out.write("\t\t\t<input class=\"save\" ");
          out.print( disabled ? "disabled" : "" );
          out.write(" type=\"submit\"\r\n");
          out.write("\t\t\t\tonclick=\"setActionTarget('commit');\"\r\n");
          out.write("\t\t\t\tvalue=\"");
          if (_jspx_meth_bean_message_4(_jspx_th_logic_notEmpty_1, _jspx_page_context))
            return;
          out.write("\" />\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t<div class=\"formButton\">\r\n");
          out.write("\t\t\t<input class=\"reset\" ");
          out.print( disabled ? "disabled" : "" );
          out.write(" type=\"submit\"\r\n");
          out.write("\t\t\t\tonclick=\"setActionTarget('reset');\"\r\n");
          out.write("\t\t\t\tvalue=\"");
          if (_jspx_meth_bean_message_5(_jspx_th_logic_notEmpty_1, _jspx_page_context))
            return;
          out.write("\" />\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t\t<div class=\"formButton\">\r\n");
          out.write("\t\t\t<input class=\"cancel\" type=\"submit\"\r\n");
          out.write("\t\t\t\tonclick=\"setActionTarget('cancel');\"\r\n");
          out.write("\t\t\t\tvalue=\"");
          if (_jspx_meth_bean_message_6(_jspx_th_logic_notEmpty_1, _jspx_page_context))
            return;
          out.write("\" />\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t</div>\r\n");
          int evalDoAfterBody = _jspx_th_logic_notEmpty_1.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_logic_notEmpty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_1);
      out.write("\r\n");
      out.write("\r\n");
      if (_jspx_meth_navigation_menuNotAvailable_0(_jspx_page_context))
        return;
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

  private boolean _jspx_meth_html_hidden_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_0 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_0.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_0.setParent(null);
    _jspx_th_html_hidden_0.setProperty("selectedCategory");
    int _jspx_eval_html_hidden_0 = _jspx_th_html_hidden_0.doStartTag();
    if (_jspx_th_html_hidden_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_0);
    return false;
  }

  private boolean _jspx_meth_html_hidden_1(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_1 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_1.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_1.setParent(null);
    _jspx_th_html_hidden_1.setProperty("newSelectedCategory");
    int _jspx_eval_html_hidden_1 = _jspx_th_html_hidden_1.doStartTag();
    if (_jspx_th_html_hidden_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_1);
    return false;
  }

  private boolean _jspx_meth_html_hidden_2(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_2 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_2.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_2.setParent(null);
    _jspx_th_html_hidden_2.setProperty("parentCategory");
    int _jspx_eval_html_hidden_2 = _jspx_th_html_hidden_2.doStartTag();
    if (_jspx_th_html_hidden_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_2);
    return false;
  }

  private boolean _jspx_meth_html_hidden_3(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_3 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_3.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_3.setParent(null);
    _jspx_th_html_hidden_3.setProperty("actionTarget");
    int _jspx_eval_html_hidden_3 = _jspx_th_html_hidden_3.doStartTag();
    if (_jspx_th_html_hidden_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_3);
    return false;
  }

  private boolean _jspx_meth_html_hidden_4(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:hidden
    org.apache.struts.taglib.html.HiddenTag _jspx_th_html_hidden_4 = (org.apache.struts.taglib.html.HiddenTag) _jspx_tagPool_html_hidden_property_nobody.get(org.apache.struts.taglib.html.HiddenTag.class);
    _jspx_th_html_hidden_4.setPageContext(_jspx_page_context);
    _jspx_th_html_hidden_4.setParent(null);
    _jspx_th_html_hidden_4.setProperty("forwardTo");
    int _jspx_eval_html_hidden_4 = _jspx_th_html_hidden_4.doStartTag();
    if (_jspx_th_html_hidden_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_4);
    return false;
  }

  private boolean _jspx_meth_bean_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
    _jspx_th_bean_message_0.setKey("properties.categories.title");
    _jspx_th_bean_message_0.setBundle("properties");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }

  private boolean _jspx_meth_core_themePath_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_iterate_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:themePath
    net.openvpn.als.core.tags.ThemePathTag _jspx_th_core_themePath_0 = (net.openvpn.als.core.tags.ThemePathTag) _jspx_tagPool_core_themePath_nobody.get(net.openvpn.als.core.tags.ThemePathTag.class);
    _jspx_th_core_themePath_0.setPageContext(_jspx_page_context);
    _jspx_th_core_themePath_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_iterate_0);
    int _jspx_eval_core_themePath_0 = _jspx_th_core_themePath_0.doStartTag();
    if (_jspx_th_core_themePath_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_themePath_nobody.reuse(_jspx_th_core_themePath_0);
    return false;
  }

  private boolean _jspx_meth_tiles_insert_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  tiles:insert
    org.apache.struts.taglib.tiles.InsertTag _jspx_th_tiles_insert_0 = (org.apache.struts.taglib.tiles.InsertTag) _jspx_tagPool_tiles_insert_page_flush.get(org.apache.struts.taglib.tiles.InsertTag.class);
    _jspx_th_tiles_insert_0.setPageContext(_jspx_page_context);
    _jspx_th_tiles_insert_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
    _jspx_th_tiles_insert_0.setPage("/WEB-INF/jsp/content/properties/propertyContent.jspf");
    _jspx_th_tiles_insert_0.setFlush(false);
    int _jspx_eval_tiles_insert_0 = _jspx_th_tiles_insert_0.doStartTag();
    if (_jspx_eval_tiles_insert_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("\t\t\t\t\t\t");
        if (_jspx_meth_tiles_put_0(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t\t");
        if (_jspx_meth_tiles_put_1(_jspx_th_tiles_insert_0, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\t\t\t\t\t");
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
    _jspx_th_tiles_put_0.setName("form");
    _jspx_th_tiles_put_0.setBeanName("form");
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
    _jspx_th_tiles_put_1.setName("actionMapping");
    _jspx_th_tiles_put_1.setBeanName("actionMapping");
    int _jspx_eval_tiles_put_1 = _jspx_th_tiles_put_1.doStartTag();
    if (_jspx_th_tiles_put_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_tiles_put_name_beanName_nobody.reuse(_jspx_th_tiles_put_1);
    return false;
  }

  private boolean _jspx_meth_bean_message_4(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_4 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_4.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
    _jspx_th_bean_message_4.setKey("properties.save");
    _jspx_th_bean_message_4.setBundle("properties");
    int _jspx_eval_bean_message_4 = _jspx_th_bean_message_4.doStartTag();
    if (_jspx_th_bean_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_4);
    return false;
  }

  private boolean _jspx_meth_bean_message_5(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_5 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_5.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
    _jspx_th_bean_message_5.setKey("properties.reset");
    _jspx_th_bean_message_5.setBundle("properties");
    int _jspx_eval_bean_message_5 = _jspx_th_bean_message_5.doStartTag();
    if (_jspx_th_bean_message_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_5);
    return false;
  }

  private boolean _jspx_meth_bean_message_6(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_6 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_6.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_1);
    _jspx_th_bean_message_6.setKey("properties.cancel");
    _jspx_th_bean_message_6.setBundle("properties");
    int _jspx_eval_bean_message_6 = _jspx_th_bean_message_6.doStartTag();
    if (_jspx_th_bean_message_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_6);
    return false;
  }

  private boolean _jspx_meth_navigation_menuNotAvailable_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  navigation:menuNotAvailable
    net.openvpn.als.navigation.tags.MenuNotAvailableTag _jspx_th_navigation_menuNotAvailable_0 = (net.openvpn.als.navigation.tags.MenuNotAvailableTag) _jspx_tagPool_navigation_menuNotAvailable.get(net.openvpn.als.navigation.tags.MenuNotAvailableTag.class);
    _jspx_th_navigation_menuNotAvailable_0.setPageContext(_jspx_page_context);
    _jspx_th_navigation_menuNotAvailable_0.setParent(null);
    int _jspx_eval_navigation_menuNotAvailable_0 = _jspx_th_navigation_menuNotAvailable_0.doStartTag();
    if (_jspx_eval_navigation_menuNotAvailable_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write('\r');
        out.write('\n');
        out.write('	');
        if (_jspx_meth_logic_notEmpty_2(_jspx_th_navigation_menuNotAvailable_0, _jspx_page_context))
          return true;
        out.write('\r');
        out.write('\n');
        int evalDoAfterBody = _jspx_th_navigation_menuNotAvailable_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_navigation_menuNotAvailable_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_navigation_menuNotAvailable.reuse(_jspx_th_navigation_menuNotAvailable_0);
    return false;
  }

  private boolean _jspx_meth_logic_notEmpty_2(javax.servlet.jsp.tagext.JspTag _jspx_th_navigation_menuNotAvailable_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  logic:notEmpty
    org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_2 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
    _jspx_th_logic_notEmpty_2.setPageContext(_jspx_page_context);
    _jspx_th_logic_notEmpty_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_navigation_menuNotAvailable_0);
    _jspx_th_logic_notEmpty_2.setName("form");
    _jspx_th_logic_notEmpty_2.setProperty("subCategories");
    int _jspx_eval_logic_notEmpty_2 = _jspx_th_logic_notEmpty_2.doStartTag();
    if (_jspx_eval_logic_notEmpty_2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("   \r\n");
        out.write("\t\t");
        if (_jspx_meth_logic_empty_0(_jspx_th_logic_notEmpty_2, _jspx_page_context))
          return true;
        out.write('\r');
        out.write('\n');
        out.write('	');
        int evalDoAfterBody = _jspx_th_logic_notEmpty_2.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_logic_notEmpty_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_2);
    return false;
  }

  private boolean _jspx_meth_logic_empty_0(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  logic:empty
    org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_0 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
    _jspx_th_logic_empty_0.setPageContext(_jspx_page_context);
    _jspx_th_logic_empty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_2);
    _jspx_th_logic_empty_0.setName("form");
    _jspx_th_logic_empty_0.setProperty("propertyItems");
    int _jspx_eval_logic_empty_0 = _jspx_th_logic_empty_0.doStartTag();
    if (_jspx_eval_logic_empty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write(" \r\n");
        out.write("\t\t\t<div class=\"button_bar\">\t\t\t\r\n");
        out.write("\t\t\t\t<div class=\"formButton\">\r\n");
        out.write("\t\t\t\t\t<input class=\"done\" type=\"submit\"\r\n");
        out.write("\t\t\t\t\t\tonclick=\"setActionTarget('configureProfiles');\"\r\n");
        out.write("\t\t\t\t\t\tvalue=\"");
        if (_jspx_meth_bean_message_7(_jspx_th_logic_empty_0, _jspx_page_context))
          return true;
        out.write("\" />\r\n");
        out.write("\t\t\t\t</div>\r\n");
        out.write("\t\t\t</div>\r\n");
        out.write("\t\t");
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

  private boolean _jspx_meth_bean_message_7(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_empty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_7 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_7.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_0);
    _jspx_th_bean_message_7.setKey("properties.done");
    _jspx_th_bean_message_7.setBundle("properties");
    int _jspx_eval_bean_message_7 = _jspx_th_bean_message_7.doStartTag();
    if (_jspx_th_bean_message_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_7);
    return false;
  }
}
