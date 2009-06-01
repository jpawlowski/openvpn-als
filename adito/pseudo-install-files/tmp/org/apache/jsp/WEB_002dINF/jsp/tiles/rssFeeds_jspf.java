package org.apache.jsp.WEB_002dINF.jsp.tiles;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.adito.rss.Feed;
import java.util.Iterator;
import com.sun.syndication.feed.synd.SyndEntry;
import com.adito.rss.FeedManager;
import com.adito.core.CoreUtil;
import com.adito.extensions.store.ExtensionStore;

public final class rssFeeds_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(3);
    _jspx_dependants.add("/WEB-INF/navigation.tld");
    _jspx_dependants.add("/WEB-INF/security.tld");
    _jspx_dependants.add("/WEB-INF/input.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_navigation_inUserConsole_requires;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_frame_title_styleClass_panelId;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_input_toolTip_width_value_href;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_navigation_inUserConsole_requires = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_frame_title_styleClass_panelId = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_input_toolTip_width_value_href = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.release();
    _jspx_tagPool_navigation_inUserConsole_requires.release();
    _jspx_tagPool_input_frame_title_styleClass_panelId.release();
    _jspx_tagPool_input_toolTip_width_value_href.release();
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
      //  tiles:useAttribute
      org.apache.struts.taglib.tiles.UseAttributeTag _jspx_th_tiles_useAttribute_0 = (org.apache.struts.taglib.tiles.UseAttributeTag) _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.get(org.apache.struts.taglib.tiles.UseAttributeTag.class);
      _jspx_th_tiles_useAttribute_0.setPageContext(_jspx_page_context);
      _jspx_th_tiles_useAttribute_0.setParent(null);
      _jspx_th_tiles_useAttribute_0.setIgnore(true);
      _jspx_th_tiles_useAttribute_0.setName("rssFeed");
      _jspx_th_tiles_useAttribute_0.setClassname("java.lang.String");
      int _jspx_eval_tiles_useAttribute_0 = _jspx_th_tiles_useAttribute_0.doStartTag();
      if (_jspx_th_tiles_useAttribute_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_tiles_useAttribute_name_ignore_classname_nobody.reuse(_jspx_th_tiles_useAttribute_0);
      java.lang.String rssFeed = null;
      rssFeed = (java.lang.String) _jspx_page_context.findAttribute("rssFeed");
      out.write("\t\t\t\n");
      //  navigation:inUserConsole
      com.adito.navigation.tags.InUserConsoleTag _jspx_th_navigation_inUserConsole_0 = (com.adito.navigation.tags.InUserConsoleTag) _jspx_tagPool_navigation_inUserConsole_requires.get(com.adito.navigation.tags.InUserConsoleTag.class);
      _jspx_th_navigation_inUserConsole_0.setPageContext(_jspx_page_context);
      _jspx_th_navigation_inUserConsole_0.setParent(null);
      _jspx_th_navigation_inUserConsole_0.setRequires(false);
      int _jspx_eval_navigation_inUserConsole_0 = _jspx_th_navigation_inUserConsole_0.doStartTag();
      if (_jspx_eval_navigation_inUserConsole_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\t\t\t\n");
          out.write("\t\t");
 
			Feed feed = FeedManager.getInstance().getFeed(rssFeed);
			
			// TODO internationalise
			if(feed!=null && feed.getStatus() != Feed.STATUS_FAILED_TO_LOAD && feed.getStatus() != Feed.STATUS_LOADING) {
				String title = CoreUtil.getMessage(session, "navigation", "panel.rssFeeds.loading");
				if(feed.getStatus() == Feed.STATUS_FAILED_TO_LOAD) {
					title = CoreUtil.getMessage(session, "navigation", "panel.rssFeeds.failed");
				}
				else if(feed.getStatus() == Feed.STATUS_LOADING) {
					title = CoreUtil.getMessage(session, "navigation", "panel.rssFeeds.loading");
				}
				else {
					title = feed.getFeed().getTitle();
				}
		
          out.write("\n");
          out.write("\t\t\t");
          //  input:frame
          com.adito.input.tags.FrameTag _jspx_th_input_frame_0 = (com.adito.input.tags.FrameTag) _jspx_tagPool_input_frame_title_styleClass_panelId.get(com.adito.input.tags.FrameTag.class);
          _jspx_th_input_frame_0.setPageContext(_jspx_page_context);
          _jspx_th_input_frame_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_navigation_inUserConsole_0);
          _jspx_th_input_frame_0.setTitle( title );
          _jspx_th_input_frame_0.setStyleClass("component_messageBox");
          _jspx_th_input_frame_0.setPanelId("rssFeeds");
          int _jspx_eval_input_frame_0 = _jspx_th_input_frame_0.doStartTag();
          if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_input_frame_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_input_frame_0.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t\t\t<div class=\"messages\">\n");
              out.write("\t\t\t\t\t");

						if(feed.getFeed() != null && feed.getFeed().getEntries() != null) {
							for(Iterator e = feed.getFeed().getEntries().iterator(); e.hasNext(); ) {
								SyndEntry entry = (SyndEntry)e.next();
					
              out.write("\n");
              out.write("\t\t\t\t\t\t\t<div class=\"text\">\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
              out.write("\t\t\t\t                ");
              //  input:toolTip
              com.adito.input.tags.ToolTipTag _jspx_th_input_toolTip_0 = (com.adito.input.tags.ToolTipTag) _jspx_tagPool_input_toolTip_width_value_href.get(com.adito.input.tags.ToolTipTag.class);
              _jspx_th_input_toolTip_0.setPageContext(_jspx_page_context);
              _jspx_th_input_toolTip_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_input_frame_0);
              _jspx_th_input_toolTip_0.setWidth("300");
              _jspx_th_input_toolTip_0.setHref( "javascript:windowRef = window.open('" + ( entry.getUri() == null ? "#" : entry.getUri() ) + "','external_resources_win','left=20,top=20,width=720,height=500,toolbar=1,resizable=1,menubar=1,scrollbars=1'); windowRef.focus()" );
              _jspx_th_input_toolTip_0.setValue( entry.getDescription().getValue() );
              int _jspx_eval_input_toolTip_0 = _jspx_th_input_toolTip_0.doStartTag();
              if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                if (_jspx_eval_input_toolTip_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
                  out = _jspx_page_context.pushBody();
                  _jspx_th_input_toolTip_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
                  _jspx_th_input_toolTip_0.doInitBody();
                }
                do {
                  out.write("\n");
                  out.write("\t\t\t\t\t\t\t\t\t");
                  out.print( entry.getTitle() == null ? "" : entry.getTitle() );
                  out.write("\n");
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
              _jspx_tagPool_input_toolTip_width_value_href.reuse(_jspx_th_input_toolTip_0);
              out.write("\n");
              out.write("\t\t\t\t\t\t\t</div>\n");
              out.write("\t\t\t\t\t");

							}
						}
					
              out.write("\n");
              out.write("\t\t\t\t</div>\n");
              out.write("\t\t\t");
              int evalDoAfterBody = _jspx_th_input_frame_0.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_input_frame_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE)
              out = _jspx_page_context.popBody();
          }
          if (_jspx_th_input_frame_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
            return;
          _jspx_tagPool_input_frame_title_styleClass_panelId.reuse(_jspx_th_input_frame_0);
          out.write('\n');
          out.write('	');
          out.write('	');

		   }
		
          out.write('\n');
          int evalDoAfterBody = _jspx_th_navigation_inUserConsole_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_navigation_inUserConsole_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_navigation_inUserConsole_requires.reuse(_jspx_th_navigation_inUserConsole_0);
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
