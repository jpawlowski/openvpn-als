<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.io.PrintWriter"%>
<jsp:directive.page import="com.ovpnals.security.SessionInfo"/>
<jsp:directive.page import="com.ovpnals.core.CoreServlet"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="com.ovpnals.security.Constants"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="com.ovpnals.boot.SystemProperties"/>

<html>
	<body>
	<%
	if(!"true".equals(SystemProperties.get("ovpnals.enableInfoPages", "false"))) {
		throw new Exception("Access denied");
	}
%>
		<h1>Threads</h1>
		<h1>Thread Dump</h1>
		<pre>
		<%
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		while(true) {
			ThreadGroup tg2 = tg.getParent();
			if(tg2 == null) {
				break;
			}
			tg = tg2;
		}
		Thread[] ta = new Thread[tg.activeCount()];
        tg.enumerate(ta,true);
        int realCount = 0;
        for(int i=0;i<ta.length;i++) {
        	if(ta[i] != null) {        	
        		realCount++;
       		}
   		}
   		%>
   		<h3>Total threads: <%= String.valueOf(realCount) %></h3>
   		<% StringBuffer buf = new StringBuffer("\n");
   		   for(int i=0;i<ta.length;i++){
   				if(ta[i] != null) {
   				    buf.append("[");
   					buf.append(ta[i].getId());
   				    buf.append("] ");
   					buf.append(ta[i].getName());
   					buf.append(" (");
   					buf.append(ta[i].getPriority());
   					buf.append(")\n");
					StackTraceElement[] st = ta[i].getStackTrace();
					for(int j = 0 ; j < st.length; j++) {
						buf.append("     ");
						buf.append(st[j].getClassName());
						buf.append(".");
						buf.append(st[j].getMethodName());
						buf.append("(");
						buf.append(st[j].getLineNumber());
						buf.append(")\n");
					}					
   				}
   			}
   		%><%= buf.toString() %>
		</pre>
	</body>
</html>