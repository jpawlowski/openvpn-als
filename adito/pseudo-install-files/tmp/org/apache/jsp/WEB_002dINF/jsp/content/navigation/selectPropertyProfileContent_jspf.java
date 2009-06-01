package org.apache.jsp.WEB_002dINF.jsp.content.navigation;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.adito.core.forms.CoreForm;

public final class selectPropertyProfileContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
    _jspx_dependants.add("/WEB-INF/core.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_form_styleClass_method_action;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_hidden_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_select_property;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_optionsCollection_value_property_label_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_html_checkbox_property_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_notEmpty_property_name;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_logic_empty_property_name;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_core_form_styleClass_method_action = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_hidden_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_select_property = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_optionsCollection_value_property_label_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_html_checkbox_property_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_notEmpty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_logic_empty_property_name = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_core_form_styleClass_method_action.release();
    _jspx_tagPool_html_hidden_property_nobody.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_html_select_property.release();
    _jspx_tagPool_html_optionsCollection_value_property_label_nobody.release();
    _jspx_tagPool_html_checkbox_property_nobody.release();
    _jspx_tagPool_logic_notEmpty_property_name.release();
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

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<div id=\"page_select_property_profile\">\n");
      out.write("\t");
      //  core:form
      com.adito.core.tags.FormTag _jspx_th_core_form_0 = (com.adito.core.tags.FormTag) _jspx_tagPool_core_form_styleClass_method_action.get(com.adito.core.tags.FormTag.class);
      _jspx_th_core_form_0.setPageContext(_jspx_page_context);
      _jspx_th_core_form_0.setParent(null);
      _jspx_th_core_form_0.setStyleClass("dialog_form");
      _jspx_th_core_form_0.setMethod("post");
      _jspx_th_core_form_0.setAction("/selectPropertyProfile.do");
      int _jspx_eval_core_form_0 = _jspx_th_core_form_0.doStartTag();
      if (_jspx_eval_core_form_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write('\n');
          out.write('	');
          out.write('	');
          if (_jspx_meth_html_hidden_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write(" \n");
          out.write("\t\t");
          if (_jspx_meth_html_hidden_1(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t\t<div class=\"dialog_content\">\n");
          out.write("\t\t\t<table class=\"dialog_form_table\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
          out.write("\t\t\t\t<tbody>\n");
          out.write("\t\t\t\t\t<tr class=\"formCombo\">\n");
          out.write("\t\t\t\t\t\t<td class=\"label\">\n");
          out.write("\t\t\t\t\t\t\t");
          if (_jspx_meth_bean_message_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write(" \n");
          out.write("\t\t\t\t\t\t</td>\n");
          out.write("\t\t\t\t\t\t<td class=\"value\">\n");
          out.write("\t\t\t\t\t\t\t");
          if (_jspx_meth_html_select_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t\t\t\t\t\t</td>\n");
          out.write("\t\t\t\t\t</tr>\n");
          out.write("\t\t\t\t\t<tr class=\"formCheckbox\">\n");
          out.write("\t\t\t\t\t\t<td colspan=\"2\" class=\"value\">\n");
          out.write("\t\t\t\t\t\t\t");
          if (_jspx_meth_bean_message_1(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write(" \n");
          out.write("\t\t\t\t\t\t\t");
          if (_jspx_meth_html_checkbox_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t\t\t\t\t\t</td>\n");
          out.write("\t\t\t\t\t</tr>\n");
          out.write("\t\t\t\t</tbody>\n");
          out.write("\t\t\t\t<tfoot>\n");
          out.write("\t\t\t\t\t<tr>\n");
          out.write("\t\t\t\t\t\t<td colspan=\"2\">\t\t\t\t\t\t\t\n");
          out.write("\t\t\t\t\t\t\t<div class=\"button_bar\">\n");
          out.write("\t\t\t\t\t\t\t\t<div class=\"formButton\">\t\n");
          out.write("\t\t\t\t\t\t\t\t\t<input class=\"ok\" type=\"submit\" value=\"");
          if (_jspx_meth_bean_message_2(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\"/>\n");
          out.write("\t\t\t\t\t\t\t\t</div>\t\t\n");
          out.write("\t\t\t\t\t\t\t\t<div class=\"formButton\">\n");
          out.write("\t\t\t\t\t\t\t\t\t");
          //  logic:notEmpty
          org.apache.struts.taglib.logic.NotEmptyTag _jspx_th_logic_notEmpty_0 = (org.apache.struts.taglib.logic.NotEmptyTag) _jspx_tagPool_logic_notEmpty_property_name.get(org.apache.struts.taglib.logic.NotEmptyTag.class);
          _jspx_th_logic_notEmpty_0.setPageContext(_jspx_page_context);
          _jspx_th_logic_notEmpty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
          _jspx_th_logic_notEmpty_0.setName("profileSelectionForm");
          _jspx_th_logic_notEmpty_0.setProperty("referer");
          int _jspx_eval_logic_notEmpty_0 = _jspx_th_logic_notEmpty_0.doStartTag();
          if (_jspx_eval_logic_notEmpty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n");
              out.write("\t\t\t\t\t\t\t\t\t\t<input class=\"cancel\" onclick=\"");
              out.print( "self.location = '" + ((CoreForm)request.getAttribute("profileSelectionForm")).getReferer() + "'" );
              out.write("\" type=\"button\" value=\"");
              if (_jspx_meth_bean_message_3(_jspx_th_logic_notEmpty_0, _jspx_page_context))
                return;
              out.write("\"/>\n");
              out.write("\t\t\t\t\t\t\t\t\t");
              int evalDoAfterBody = _jspx_th_logic_notEmpty_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_logic_notEmpty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_logic_notEmpty_property_name.reuse(_jspx_th_logic_notEmpty_0);
          out.write("\t\t\t\t\t\t\t\n");
          out.write("\t\t\t\t\t\t\t\t\t");
          if (_jspx_meth_logic_empty_0(_jspx_th_core_form_0, _jspx_page_context))
            return;
          out.write("\t\n");
          out.write("\t\t\t\t\t\t\t\t</div>\n");
          out.write("\t\t\t\t\t\t\t</div>\n");
          out.write("\t\t\t\t\t\t</td>\n");
          out.write("\t\t\t\t\t</tr>\n");
          out.write("\t\t\t\t</tfoot>\n");
          out.write("\t\t\t</table>\n");
          out.write("\t\t</div>\t\t\t\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_core_form_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_core_form_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_core_form_styleClass_method_action.reuse(_jspx_th_core_form_0);
      out.write("\n");
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
    _jspx_th_html_hidden_0.setProperty("_charset_");
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
    _jspx_th_html_hidden_1.setProperty("referer");
    int _jspx_eval_html_hidden_1 = _jspx_th_html_hidden_1.doStartTag();
    if (_jspx_th_html_hidden_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_hidden_property_nobody.reuse(_jspx_th_html_hidden_1);
    return false;
  }

  private boolean _jspx_meth_bean_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_bean_message_0.setKey("selectPropertyProfile.profile.label");
    _jspx_th_bean_message_0.setBundle("navigation");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }

  private boolean _jspx_meth_html_select_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:select
    org.apache.struts.taglib.html.SelectTag _jspx_th_html_select_0 = (org.apache.struts.taglib.html.SelectTag) _jspx_tagPool_html_select_property.get(org.apache.struts.taglib.html.SelectTag.class);
    _jspx_th_html_select_0.setPageContext(_jspx_page_context);
    _jspx_th_html_select_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_select_0.setProperty("selectedPropertyProfile");
    int _jspx_eval_html_select_0 = _jspx_th_html_select_0.doStartTag();
    if (_jspx_eval_html_select_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_html_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_html_select_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_html_select_0.doInitBody();
      }
      do {
        out.write("\n");
        out.write("\t\t\t\t\t\t\t\t");
        if (_jspx_meth_html_optionsCollection_0(_jspx_th_html_select_0, _jspx_page_context))
          return true;
        out.write("\n");
        out.write("\t\t\t\t\t\t\t");
        int evalDoAfterBody = _jspx_th_html_select_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_html_select_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
        out = _jspx_page_context.popBody();
    }
    if (_jspx_th_html_select_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_select_property.reuse(_jspx_th_html_select_0);
    return false;
  }

  private boolean _jspx_meth_html_optionsCollection_0(javax.servlet.jsp.tagext.JspTag _jspx_th_html_select_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:optionsCollection
    org.apache.struts.taglib.html.OptionsCollectionTag _jspx_th_html_optionsCollection_0 = (org.apache.struts.taglib.html.OptionsCollectionTag) _jspx_tagPool_html_optionsCollection_value_property_label_nobody.get(org.apache.struts.taglib.html.OptionsCollectionTag.class);
    _jspx_th_html_optionsCollection_0.setPageContext(_jspx_page_context);
    _jspx_th_html_optionsCollection_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_html_select_0);
    _jspx_th_html_optionsCollection_0.setValue("resourceId");
    _jspx_th_html_optionsCollection_0.setLabel("label");
    _jspx_th_html_optionsCollection_0.setProperty("propertyProfiles");
    int _jspx_eval_html_optionsCollection_0 = _jspx_th_html_optionsCollection_0.doStartTag();
    if (_jspx_th_html_optionsCollection_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_optionsCollection_value_property_label_nobody.reuse(_jspx_th_html_optionsCollection_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_bean_message_1.setKey("selectPropertyProfile.profile.makeDefault");
    _jspx_th_bean_message_1.setBundle("navigation");
    int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
    if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
    return false;
  }

  private boolean _jspx_meth_html_checkbox_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  html:checkbox
    org.apache.struts.taglib.html.CheckboxTag _jspx_th_html_checkbox_0 = (org.apache.struts.taglib.html.CheckboxTag) _jspx_tagPool_html_checkbox_property_nobody.get(org.apache.struts.taglib.html.CheckboxTag.class);
    _jspx_th_html_checkbox_0.setPageContext(_jspx_page_context);
    _jspx_th_html_checkbox_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_html_checkbox_0.setProperty("makeDefault");
    int _jspx_eval_html_checkbox_0 = _jspx_th_html_checkbox_0.doStartTag();
    if (_jspx_th_html_checkbox_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_html_checkbox_property_nobody.reuse(_jspx_th_html_checkbox_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_2(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_bean_message_2.setKey("selectPropertyProfile.select.label");
    _jspx_th_bean_message_2.setBundle("navigation");
    int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
    if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
    return false;
  }

  private boolean _jspx_meth_bean_message_3(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_notEmpty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_notEmpty_0);
    _jspx_th_bean_message_3.setKey("selectAuthenticationScheme.cancel");
    _jspx_th_bean_message_3.setBundle("security");
    int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
    if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_3);
    return false;
  }

  private boolean _jspx_meth_logic_empty_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_form_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  logic:empty
    org.apache.struts.taglib.logic.EmptyTag _jspx_th_logic_empty_0 = (org.apache.struts.taglib.logic.EmptyTag) _jspx_tagPool_logic_empty_property_name.get(org.apache.struts.taglib.logic.EmptyTag.class);
    _jspx_th_logic_empty_0.setPageContext(_jspx_page_context);
    _jspx_th_logic_empty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_form_0);
    _jspx_th_logic_empty_0.setName("profileSelectionForm");
    _jspx_th_logic_empty_0.setProperty("referer");
    int _jspx_eval_logic_empty_0 = _jspx_th_logic_empty_0.doStartTag();
    if (_jspx_eval_logic_empty_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n");
        out.write("\t\t\t\t\t\t\t\t\t\t<input class=\"cancel\" onclick=\"self.location = '/logoff.do'\" type=\"button\" value=\"");
        if (_jspx_meth_bean_message_4(_jspx_th_logic_empty_0, _jspx_page_context))
          return true;
        out.write("\"/>\n");
        out.write("\t\t\t\t\t\t\t\t\t");
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

  private boolean _jspx_meth_bean_message_4(javax.servlet.jsp.tagext.JspTag _jspx_th_logic_empty_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_4 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_4.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_logic_empty_0);
    _jspx_th_bean_message_4.setKey("selectAuthenticationScheme.cancel");
    _jspx_th_bean_message_4.setBundle("security");
    int _jspx_eval_bean_message_4 = _jspx_th_bean_message_4.doStartTag();
    if (_jspx_th_bean_message_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_4);
    return false;
  }
}
