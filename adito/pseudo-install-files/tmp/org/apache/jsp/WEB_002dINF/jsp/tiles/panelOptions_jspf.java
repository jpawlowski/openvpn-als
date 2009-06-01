package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import com.adito.core.PanelManager;
import com.adito.core.Panel;
import com.adito.core.CoreUtil;
import com.adito.input.tags.FrameTag;
import com.adito.language.LanguagePackManager;
import java.util.Locale;
import org.apache.struts.Globals;
import com.adito.language.Language;
import java.util.List;
import com.adito.properties.PropertyProfile;
import com.adito.security.Constants;
import com.adito.core.DefaultPanel;

public final class panelOptions_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(3);
    _jspx_dependants.add("/WEB-INF/input.tld");
    _jspx_dependants.add("/WEB-INF/navigation.tld");
    _jspx_dependants.add("/WEB-INF/core.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_titleKey_styleClass_panelId;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_bean_message_key_bundle_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_navigation_inUserConsole;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_key_href_bundle;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_bean_message_key_bundle_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_navigation_inUserConsole = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_key_href_bundle = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_input_frame_titleKey_styleClass_panelId.release();
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.release();
    _jspx_tagPool_bean_message_key_bundle_nobody.release();
    _jspx_tagPool_navigation_inUserConsole.release();
    _jspx_tagPool_input_toolTip_key_href_bundle.release();
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
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\t\t\t\t\t\t\t\n");
      //  input:frame
      com.adito.input.tags.FrameTag _jspx_th_input_frame_0 = (com.adito.input.tags.FrameTag) _jspx_tagPool_input_frame_titleKey_styleClass_panelId.get(com.adito.input.tags.FrameTag.class);
      _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
      _jspx_th_input_frame_0.setParent(null);
      _jspx_th_input_frame_0.setTitleKey("messages.panelOptions");
      _jspx_th_input_frame_0.setStyleClass("component_messageBox");
      _jspx_th_input_frame_0.setPanelId("panelOptions");
      int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
      if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_input_frame_0.doInitBody();
        }
        do {
          out.write("\t\n");
          out.write("\n");
          out.write("\t");

	Locale l = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
	l = l == null ? Locale.getDefault() : l;
	
          out.write('\n');
          out.write('\n');
          out.write('	');
          //  core:checkPropertyEquals
          com.adito.core.tags.CheckPropertyEqualsTag _jspx_th_core_checkPropertyEquals_0 = (com.adito.core.tags.CheckPropertyEqualsTag) _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyEqualsTag.class);
          _jspx_th_core_checkPropertyEquals_0.setPageContext(_jspx_page_context);
          _jspx_th_core_checkPropertyEquals_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_core_checkPropertyEquals_0.setPropertyValue("true");
          _jspx_th_core_checkPropertyEquals_0.setPropertyName("ui.allowLanguageSelection");
          _jspx_th_core_checkPropertyEquals_0.setRegExp(false);
          int _jspx_eval_core_checkPropertyEquals_0 = _jspx_th_core_checkPropertyEquals_0.doStartTag();
          if (_jspx_eval_core_checkPropertyEquals_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n");
              out.write("\t\t<div class=\"language\">\n");
              out.write("\t\t\t");
              if (_jspx_meth_bean_message_0(_jspx_th_core_checkPropertyEquals_0, _jspx_page_context))
                return;
              out.write("<br/>\n");
              out.write("\t\t\t<select id=\"languageSelector\" onchange=\"");
              out.print( "self.location = '/selectLanguage.do?referer=' + window.location.pathname + '&locale=' + document.getElementById('languageSelector').value" );
              out.write("\" name=\"selectedLanguage\">\n");
              out.write("\t\t\t");

			for(Iterator i = LanguagePackManager.getInstance().languages(true); i.hasNext(); ) {
				Language lang = (Language)i.next();
			
              out.write("\n");
              out.write("\t\t\t\t<option ");
              out.print( lang.isLocale(l) ? "selected " : "" );
              out.write("value=\"");
              out.print( lang.getCode() );
              out.write('"');
              out.write('>');
              out.print( lang.getDescription()  );
              out.write("</option>\n");
              out.write("\t\t\t");

			}
			 
              out.write("\n");
              out.write("\t\t\t</select>\n");
              out.write("\t\t</div>\t\n");
              out.write("\t");
              int evalDoAfterBody = _jspx_th_core_checkPropertyEquals_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_core_checkPropertyEquals_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyEquals_0);
          out.write('\n');
          out.write(' ');
          out.write('	');
 	List profiles = (List)session.getAttribute(Constants.PROFILES);
 		if(profiles != null) { 
 	
          out.write('\n');
          out.write('	');
          //  navigation:inUserConsole
          com.adito.navigation.tags.InUserConsoleTag _jspx_th_navigation_inUserConsole_0 = (com.adito.navigation.tags.InUserConsoleTag) _jspx_tagPool_navigation_inUserConsole.get(com.adito.navigation.tags.InUserConsoleTag.class);
          _jspx_th_navigation_inUserConsole_0.setPageContext(_jspx_page_context);
          _jspx_th_navigation_inUserConsole_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          int _jspx_eval_navigation_inUserConsole_0 = _jspx_th_navigation_inUserConsole_0.doStartTag();
          if (_jspx_eval_navigation_inUserConsole_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            do {
              out.write("\n");
              out.write("\t\t<div class=\"profile\">\n");
              out.write("\t\t\t");
              if (_jspx_meth_bean_message_1(_jspx_th_navigation_inUserConsole_0, _jspx_page_context))
                return;
              out.write("<br/>\n");
              out.write("\t\t\t<select id=\"profileSelector\" onchange=\"");
              out.print( "self.location = '/selectPropertyProfile.do?referer=' + window.location.pathname + '&selectedPropertyProfile=' + document.getElementById('profileSelector').value" );
              out.write("\" name=\"selectedProfile\">\n");
              out.write("\t\t\t\t \t");
 	PropertyProfile selected = (PropertyProfile)session.getAttribute(Constants.SELECTED_PROFILE);
				 		if(selected == null) {
				 			selected = (PropertyProfile)profiles.get(0);
				 		}
				  		if(profiles != null) {
				  			for(Iterator i = profiles.iterator(); i.hasNext(); ) { 
				  				PropertyProfile profile = (PropertyProfile)i.next(); 
              out.write("\n");
              out.write("\t\t\t\t\t\t \t\t\t\t<option value=\"");
              out.print( profile.getResourceId() );
              out.write('"');
              out.write('"');
              out.write(' ');
              out.print( profile.getResourceId() == selected.getResourceId() ? " selected=\"selected\"" : "" );
              out.write('>');
              out.print( profile.getResourceName());
              out.write("</option>\n");
              out.write("\t\t\t\t\t");
		}
						}  
              out.write("\n");
              out.write("\t\t\t</select>\n");
              out.write("\t\t</div>\n");
              out.write("\t");
              int evalDoAfterBody = _jspx_th_navigation_inUserConsole_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
          }
          if (_jspx_th_navigation_inUserConsole_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_navigation_inUserConsole.reuse(_jspx_th_navigation_inUserConsole_0);
          out.write('\n');
          out.write('	');
	} 
          out.write("\t\n");
          out.write("\t\n");
          out.write("\t");
          if (_jspx_meth_core_checkPropertyEquals_1(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write('\n');
          out.write('	');
          if (_jspx_meth_navigation_inUserConsole_1(_jspx_th_input_frame_0, _jspx_page_context))
            return;
          out.write("\n");
          out.write("\t<ul>\n");
          out.write("\t");
 
	for(Iterator i = PanelManager.getInstance().getPanels(-1, request, response, DefaultPanel.MAIN_LAYOUT).iterator(); i.hasNext(); ) {
		Panel p = (Panel)i.next(); 
		if(p.isCloseable() && !p.getId().equals("panelOptions") && p.getPlacement() != Panel.STATUS_TAB) {
			String frameState = CoreUtil.getCookieValue("frame_component_" + p.getId(), request, "normal");			
	
          out.write("\n");
          out.write("\t\t<li>\t\t\t\n");
          out.write("\t\t\t");
 if(frameState.equals(FrameTag.FRAME_CLOSED)) { 
          out.write("\n");
          out.write("\t\t\t\t<input id=\"");
          out.print( "component_" + p.getId() + "_toggle" );
          out.write("\" onclick=\"");
          out.print( "frameToggle('component_" + p.getId()+ "')" );
          out.write("\" type=\"checkbox\"/>\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
          out.write("                ");
          //  input:toolTip
          com.adito.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (com.adito.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_key_href_bundle.get(com.adito.input.tags.ToolTipTag.class);
          _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
          _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_input_toolTip_0.setKey("panel." + p.getId() + ".description" );
          _jspx_th_input_toolTip_0.setBundle( p.getBundle() );
          _jspx_th_input_toolTip_0.setHref( "javascript: frameToggle('component_" + p.getId() + "')" );
          int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
          if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_toolTip_0.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t\t\t\t");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_2 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_2.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_0);
              _jspx_th_bean_message_2.setKey("panel." + p.getId() + ".title" );
              _jspx_th_bean_message_2.setBundle( p.getBundle() );
              int _jspx_eval_bean_message_2 = _jspx_th_bean_message_2.doStartTag();
              if (_jspx_th_bean_message_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_2);
              out.write("\n");
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
          _jspx_tagPool_input_toolTip_key_href_bundle.reuse(_jspx_th_input_toolTip_0);
          out.write("\n");
          out.write("\t\t\t\t\n");
          out.write("\t\t\t");
 } else { 
          out.write("\n");
          out.write("\t\t\t\t<input id=\"");
          out.print( "component_" + p.getId() + "_toggle" );
          out.write("\" checked=\"checked\" onclick=\"");
          out.print( "frameToggle('component_" + p.getId() + "')" );
          out.write("\" type=\"checkbox\"/>\n");
          out.write("                ");
          //  input:toolTip
          com.adito.input.tags.ToolTipTag _jspx_th_input_toolTip_1 = (com.adito.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_key_href_bundle.get(com.adito.input.tags.ToolTipTag.class);
          _jspx_th_input_toolTip_1.setPageContext(_jspx_page_context);
          _jspx_th_input_toolTip_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
          _jspx_th_input_toolTip_1.setKey("panel." + p.getId() + ".description" );
          _jspx_th_input_toolTip_1.setBundle( p.getBundle() );
          _jspx_th_input_toolTip_1.setHref( "javascript: frameToggle('component_" + p.getId() + "')" );
          int _jspx_eval_input_toolTip_1 = _jspx_th_input_toolTip_1.doStartTag();
          if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_toolTip_1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_toolTip_1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_toolTip_1.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t\t\t\t");
              //  bean:message
              org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_3 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
              _jspx_th_bean_message_3.setPageContext(_jspx_page_context);
              _jspx_th_bean_message_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_toolTip_1);
              _jspx_th_bean_message_3.setKey("panel." + p.getId() + ".title" );
              _jspx_th_bean_message_3.setBundle( p.getBundle() );
              int _jspx_eval_bean_message_3 = _jspx_th_bean_message_3.doStartTag();
              if (_jspx_th_bean_message_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
                return;
              _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_3);
              out.write("\n");
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
          _jspx_tagPool_input_toolTip_key_href_bundle.reuse(_jspx_th_input_toolTip_1);
          out.write("\n");
          out.write("\t\t\t");
 } 
          out.write("\n");
          out.write("\t\t</li>\n");
          out.write("\t");
 
		}
	}
	
          out.write("\n");
          out.write("\t</ul>\n");
          int evalDoAfterBody = _jspx_th_input_frame_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
          out = _jspx_page_context.popBody();
      }
      if (_jspx_th_input_frame_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_input_frame_titleKey_styleClass_panelId.reuse(_jspx_th_input_frame_0);
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

  private boolean _jspx_meth_bean_message_0(javax.servlet.jsp.tagext.JspTag _jspx_th_core_checkPropertyEquals_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_0 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_0.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_core_checkPropertyEquals_0);
    _jspx_th_bean_message_0.setKey("panelOptions.language");
    _jspx_th_bean_message_0.setBundle("navigation");
    int _jspx_eval_bean_message_0 = _jspx_th_bean_message_0.doStartTag();
    if (_jspx_th_bean_message_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_0);
    return false;
  }

  private boolean _jspx_meth_bean_message_1(javax.servlet.jsp.tagext.JspTag _jspx_th_navigation_inUserConsole_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  bean:message
    org.apache.struts.taglib.bean.MessageTag _jspx_th_bean_message_1 = (org.apache.struts.taglib.bean.MessageTag) _jspx_tagPool_bean_message_key_bundle_nobody.get(org.apache.struts.taglib.bean.MessageTag.class);
    _jspx_th_bean_message_1.setPageContext(_jspx_page_context);
    _jspx_th_bean_message_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_navigation_inUserConsole_0);
    _jspx_th_bean_message_1.setKey("panelOptions.profile");
    _jspx_th_bean_message_1.setBundle("navigation");
    int _jspx_eval_bean_message_1 = _jspx_th_bean_message_1.doStartTag();
    if (_jspx_th_bean_message_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_bean_message_key_bundle_nobody.reuse(_jspx_th_bean_message_1);
    return false;
  }

  private boolean _jspx_meth_core_checkPropertyEquals_1(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:checkPropertyEquals
    com.adito.core.tags.CheckPropertyEqualsTag _jspx_th_core_checkPropertyEquals_1 = (com.adito.core.tags.CheckPropertyEqualsTag) _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.get(com.adito.core.tags.CheckPropertyEqualsTag.class);
    _jspx_th_core_checkPropertyEquals_1.setPageContext(_jspx_page_context);
    _jspx_th_core_checkPropertyEquals_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    _jspx_th_core_checkPropertyEquals_1.setPropertyValue("true");
    _jspx_th_core_checkPropertyEquals_1.setPropertyName("ui.allowLanguageSelection");
    _jspx_th_core_checkPropertyEquals_1.setRegExp(false);
    int _jspx_eval_core_checkPropertyEquals_1 = _jspx_th_core_checkPropertyEquals_1.doStartTag();
    if (_jspx_eval_core_checkPropertyEquals_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n");
        out.write("\t\t<hr/>\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_core_checkPropertyEquals_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_core_checkPropertyEquals_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_checkPropertyEquals_regExp_propertyValue_propertyName.reuse(_jspx_th_core_checkPropertyEquals_1);
    return false;
  }

  private boolean _jspx_meth_navigation_inUserConsole_1(javax.servlet.jsp.tagext.JspTag _jspx_th_input_frame_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  navigation:inUserConsole
    com.adito.navigation.tags.InUserConsoleTag _jspx_th_navigation_inUserConsole_1 = (com.adito.navigation.tags.InUserConsoleTag) _jspx_tagPool_navigation_inUserConsole.get(com.adito.navigation.tags.InUserConsoleTag.class);
    _jspx_th_navigation_inUserConsole_1.setPageContext(_jspx_page_context);
    _jspx_th_navigation_inUserConsole_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
    int _jspx_eval_navigation_inUserConsole_1 = _jspx_th_navigation_inUserConsole_1.doStartTag();
    if (_jspx_eval_navigation_inUserConsole_1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\n");
        out.write("\t\t<hr/>\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_navigation_inUserConsole_1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_navigation_inUserConsole_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_navigation_inUserConsole.reuse(_jspx_th_navigation_inUserConsole_1);
    return false;
  }
}
