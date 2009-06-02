package org.apache.jsp.WEB_002dINF.jsp.content.navigation;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import net.openvpn.als.security.Constants;
import org.apache.struts.Globals;
import java.util.Locale;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.boot.Util;

public final class launchViaAgentAppletContent_jspf extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/WEB-INF/core.tld");
    _jspx_dependants.add("/WEB-INF/security.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_security_agentAvailability_requiresClient;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_core_clientProxyURL_nobody;

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_security_agentAvailability_requiresClient = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_core_clientProxyURL_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_security_agentAvailability_requiresClient.release();
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.release();
    _jspx_tagPool_core_clientProxyURL_nobody.release();
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
 LaunchSession launchSession = (LaunchSession)request.getAttribute(Constants.REQ_ATTR_LAUNCH_SESSION);
   if(launchSession == null) {
   	   throw new IllegalStateException("No launch session.");
   }
   String ticket = (String)launchSession.getAttribute(Constants.LAUNCH_ATTR_AGENT_TICKET);
   ExtensionDescriptor ed = (ExtensionDescriptor)launchSession.getAttribute(Constants.LAUNCH_ATTR_AGENT_EXTENSION);
   String returnTo = (String)launchSession.getAttribute(Constants.LAUNCH_ATTR_AGENT_RETURN_TO); 
      out.write("\r\n");
      out.write("<div class=\"page_launchViaAgentApplet\" id=\"");
      out.print( "page_launchViaAgentApplet_" + ed.getId() );
      out.write("\">\r\n");
      out.write("\t");
      //  security:agentAvailability
      net.openvpn.als.security.tags.AgentAvailabilityTag _jspx_th_security_agentAvailability_0 = (net.openvpn.als.security.tags.AgentAvailabilityTag) _jspx_tagPool_security_agentAvailability_requiresClient.get(net.openvpn.als.security.tags.AgentAvailabilityTag.class);
      _jspx_th_security_agentAvailability_0.setPageContext(_jspx_page_context);
      _jspx_th_security_agentAvailability_0.setParent(null);
      _jspx_th_security_agentAvailability_0.setRequiresClient(false);
      int _jspx_eval_security_agentAvailability_0 = _jspx_th_security_agentAvailability_0.doStartTag();
      if (_jspx_eval_security_agentAvailability_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\r\n");
          out.write("\t\t<div class=\"dialog_content\">\r\n");
          out.write("\t\t\t<IE:clientCaps ID=\"oClientCaps\" />\r\n");
          out.write("\t\t\t<script language=\"JavaScript\" type=\"text/javascript\">\r\n");
          out.write("\t\t\tif(!navigator.javaEnabled()) {\r\n");
          out.write("\t\t\t\tdocument.writeln('<div class=\"noJava\">");
          out.print( Util.escapeForJavascriptString(CoreUtil.getMessageResources(request.getSession(), "navigation").getMessage(((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY)), "launchAgent.noJVM"))  );
          out.write("</div></applet>');\r\n");
          out.write("\t\t\t}\r\n");
          out.write("\t\t\telse if(ns == true && ns6 == false) {\r\n");
          out.write("\t\t\t\tdocument.writeln('<embed ' +\r\n");
          out.write("\t\t\t\t    'type=\"application/x-java-applet;version=1.5\" \\\r\n");
          out.write("\t\t\t        CODE=\"net.openvpn.als.agent.client.launcher.AgentLauncher\" \\\r\n");
          out.write("\t\t\t        ARCHIVE=\"/fs/apps/agent/launcher.jar,/fs/apps/agent/launcher-en.jar\" \\\r\n");
          out.write("\t\t\t        NAME=\"AgentLauncher\" \\\r\n");
          out.write("\t\t\t        WIDTH=\"250\" \\\r\n");
          out.write("\t\t\t        HEIGHT=\"64\" \\\r\n");
          out.write("\t\t\t        ALIGN=\"middle\" \\\r\n");
          out.write("\t\t\t        VSPACE=\"0\" \\\r\n");
          out.write("\t\t\t        HSPACE=\"0\" \\\r\n");
          out.write("\t\t\t        cabbase=\"/fs/apps/agent/launcher.cab\" \\\r\n");
          out.write("\t\t\t        ticket=\"");
          out.print( ticket );
          out.write("\" \\\r\n");
          out.write("\t\t\t        autoStart=\"true\" \\\r\n");
          out.write("\t\t\t        returnTo=\"");
          out.print( returnTo );
          out.write(" \" \\\r\n");
          out.write("\t\t\t        debug=\"");
          if (_jspx_meth_core_getProperty_0(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\" \\\r\n");
          out.write("\t\t\t        cleanOnExit=\"");
          if (_jspx_meth_core_getProperty_1(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\" \\\r\n");
          out.write("\t\t\t        dir=\"");
          if (_jspx_meth_core_getProperty_2(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\" \\\r\n");
          out.write("\t\t\t        timeout=\"");
          if (_jspx_meth_core_getProperty_3(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\" \\\r\n");
          out.write("\t\t\t        proxyURL=\"");
          if (_jspx_meth_core_clientProxyURL_0(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\" \\\r\n");
          out.write("\t\t\t\t\textensionId=\"");
          out.print( ed.getId() );
          out.write("\" \\\r\n");
          out.write("\t\t\t\t\tuserAgent=\"");
          out.print( request.getHeader("User-Agent") );
          out.write("\" \\\r\n");
          out.write("\t\t\t\t\tlauncherImage=\"");
          out.print( ed.getApplicationBundle().getId() +"/" + ed.getLargeIcon() );
          out.write("\" \\\r\n");
          out.write("\t\t\t        locale=\"");
          out.print( ((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY)).toString() );
          out.write("\" \\\r\n");
          out.write("\t\t\t        monitor=\"true\" \\\r\n");
          out.write("\t\t\t\t    scriptable=\"false\" \\\r\n");
          out.write("\t\t\t\t    pluginspage=\"http://java.sun.com/products/plugin/index.html#download\"><noembed><xmp>');\r\n");
          out.write("\t\t   \t}\r\n");
          out.write("\t\t    else {\r\n");
          out.write("\t\t    \tvar t = '<applet CODE=\"net.openvpn.als.agent.client.launcher.AgentLauncher\" ';\r\n");
          out.write("\t\t    \tt += ' CODEBASE=\"/fs/apps/agent\" ';\r\n");
          out.write("\t\t    \tt += ' ARCHIVE=\"launcher.jar,launcher-en.jar\"';\r\n");
          out.write("\t\t    \tt += ' WIDTH=\"250\" HEIGHT=\"64\" NAME=\"AgentLauncher\" ALIGN=\"middle\" VSPACE=\"0\" HSPACE=\"0\">';\r\n");
          out.write("\t\t    \tt += '<param name=\"CODE\" value=\"net.openvpn.als.agent.client.launcher.AgentLauncher\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"ARCHIVE\" value=\"/fs/apps/agent/launcher.jar,/fs/apps/agent/launcher-en.jar\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"NAME\" value=\"AgentLauncher\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"type\" value=\"application/x-java-applet;version=1.5\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"scriptable\" value=\"false\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"cabbase\" value=\"/fs/apps/agent/launcher.cab\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"ticket\" value=\"");
          out.print( ticket );
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"autoStart\" value=\"true\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"returnTo\" value=\"");
          out.print( returnTo );
          out.write(" \"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"debug\" value=\"");
          if (_jspx_meth_core_getProperty_4(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"cleanOnExit\" value=\"");
          if (_jspx_meth_core_getProperty_5(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"dir\" value=\"");
          if (_jspx_meth_core_getProperty_6(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"timeout\" value=\"");
          if (_jspx_meth_core_getProperty_7(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"proxyURL\" value=\"");
          if (_jspx_meth_core_clientProxyURL_1(_jspx_th_security_agentAvailability_0, _jspx_page_context))
            return;
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"monitor\" value=\"true\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"extensionId\" value=\"");
          out.print( ed.getId() );
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"userAgent\" value=\"");
          out.print( request.getHeader("User-Agent") );
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"launcherImage\" value=\"");
          out.print( ed.getApplicationBundle().getId() +"/" + ed.getLargeIcon()  );
          out.write("\"/>';\r\n");
          out.write("\t\t\t    t += '<param name=\"locale\" value=\"");
          out.print( ((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY)).toString() );
          out.write("\"/>';\r\n");
          out.write("\t\t    \tt += '<div class=\"noJava\">';\r\n");
          out.write("\t\t    \tt += '");
          out.print( Util.escapeForJavascriptString((CoreUtil.getMessageResources(request.getSession(), "navigation").getMessage(((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY)), "launchAgent.noJVM")), true, false) );
          out.write("';\r\n");
          out.write("\t\t    \tt += '</div></applet>';\r\n");
          out.write("\t\t    \tdocument.writeln(t);\r\n");
          out.write("\t\t   \t}\r\n");
          out.write("\t\t\t</script>\r\n");
          out.write("\t\t</div>\r\n");
          out.write("\t");
          int evalDoAfterBody = _jspx_th_security_agentAvailability_0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_security_agentAvailability_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
        return;
      _jspx_tagPool_security_agentAvailability_requiresClient.reuse(_jspx_th_security_agentAvailability_0);
      out.write("\r\n");
      out.write("</div>\r\n");
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

  private boolean _jspx_meth_core_getProperty_0(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_0 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_0.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_0.setUserProfile(true);
    _jspx_th_core_getProperty_0.setPropertyName("client.debug");
    int _jspx_eval_core_getProperty_0 = _jspx_th_core_getProperty_0.doStartTag();
    if (_jspx_th_core_getProperty_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_0);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_1(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_1 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_1.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_1.setUserProfile(true);
    _jspx_th_core_getProperty_1.setPropertyName("client.cleanOnExit");
    int _jspx_eval_core_getProperty_1 = _jspx_th_core_getProperty_1.doStartTag();
    if (_jspx_th_core_getProperty_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_1);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_2(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_2 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_2.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_2.setUserProfile(true);
    _jspx_th_core_getProperty_2.setPropertyName("client.cacheDirectory");
    int _jspx_eval_core_getProperty_2 = _jspx_th_core_getProperty_2.doStartTag();
    if (_jspx_th_core_getProperty_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_2);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_3(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_3 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_3.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_3.setUserProfile(true);
    _jspx_th_core_getProperty_3.setPropertyName("client.registration.synchronization.timeout");
    int _jspx_eval_core_getProperty_3 = _jspx_th_core_getProperty_3.doStartTag();
    if (_jspx_th_core_getProperty_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_3);
    return false;
  }

  private boolean _jspx_meth_core_clientProxyURL_0(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:clientProxyURL
    net.openvpn.als.core.tags.ClientProxyURLTag _jspx_th_core_clientProxyURL_0 = (net.openvpn.als.core.tags.ClientProxyURLTag) _jspx_tagPool_core_clientProxyURL_nobody.get(net.openvpn.als.core.tags.ClientProxyURLTag.class);
    _jspx_th_core_clientProxyURL_0.setPageContext(_jspx_page_context);
    _jspx_th_core_clientProxyURL_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    int _jspx_eval_core_clientProxyURL_0 = _jspx_th_core_clientProxyURL_0.doStartTag();
    if (_jspx_th_core_clientProxyURL_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_clientProxyURL_nobody.reuse(_jspx_th_core_clientProxyURL_0);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_4(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_4 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_4.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_4.setUserProfile(true);
    _jspx_th_core_getProperty_4.setPropertyName("client.debug");
    int _jspx_eval_core_getProperty_4 = _jspx_th_core_getProperty_4.doStartTag();
    if (_jspx_th_core_getProperty_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_4);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_5(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_5 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_5.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_5.setUserProfile(true);
    _jspx_th_core_getProperty_5.setPropertyName("client.cleanOnExit");
    int _jspx_eval_core_getProperty_5 = _jspx_th_core_getProperty_5.doStartTag();
    if (_jspx_th_core_getProperty_5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_5);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_6(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_6 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_6.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_6.setUserProfile(true);
    _jspx_th_core_getProperty_6.setPropertyName("client.cacheDirectory");
    int _jspx_eval_core_getProperty_6 = _jspx_th_core_getProperty_6.doStartTag();
    if (_jspx_th_core_getProperty_6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_6);
    return false;
  }

  private boolean _jspx_meth_core_getProperty_7(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:getProperty
    net.openvpn.als.core.tags.GetPropertyTag _jspx_th_core_getProperty_7 = (net.openvpn.als.core.tags.GetPropertyTag) _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.get(net.openvpn.als.core.tags.GetPropertyTag.class);
    _jspx_th_core_getProperty_7.setPageContext(_jspx_page_context);
    _jspx_th_core_getProperty_7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    _jspx_th_core_getProperty_7.setUserProfile(true);
    _jspx_th_core_getProperty_7.setPropertyName("client.registration.synchronization.timeout");
    int _jspx_eval_core_getProperty_7 = _jspx_th_core_getProperty_7.doStartTag();
    if (_jspx_th_core_getProperty_7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_getProperty_userProfile_propertyName_nobody.reuse(_jspx_th_core_getProperty_7);
    return false;
  }

  private boolean _jspx_meth_core_clientProxyURL_1(javax.servlet.jsp.tagext.JspTag _jspx_th_security_agentAvailability_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  core:clientProxyURL
    net.openvpn.als.core.tags.ClientProxyURLTag _jspx_th_core_clientProxyURL_1 = (net.openvpn.als.core.tags.ClientProxyURLTag) _jspx_tagPool_core_clientProxyURL_nobody.get(net.openvpn.als.core.tags.ClientProxyURLTag.class);
    _jspx_th_core_clientProxyURL_1.setPageContext(_jspx_page_context);
    _jspx_th_core_clientProxyURL_1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_security_agentAvailability_0);
    int _jspx_eval_core_clientProxyURL_1 = _jspx_th_core_clientProxyURL_1.doStartTag();
    if (_jspx_th_core_clientProxyURL_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE)
      return true;
    _jspx_tagPool_core_clientProxyURL_nobody.reuse(_jspx_th_core_clientProxyURL_1);
    return false;
  }
}
