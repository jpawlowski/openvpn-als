<%@ page contentType="text/javascript;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:directive.page import="net.openvpn.als.boot.SystemProperties"/>

/* The server absolutely requires cookies to be able to function correctly
 * This script fragment detects if they are available and directs to an 
 * information page if not.
 */
 
if(!(document.cookie && document.cookie.indexOf("<%= SystemProperties.get("cookie", "SSLX_SSESHID") %>") > -1)) {
	window.location = '/noCookies.do';
}