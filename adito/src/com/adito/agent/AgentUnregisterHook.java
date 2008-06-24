/**
 * 
 */
package com.adito.agent;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.security.SessionInfo;

public class AgentUnregisterHook implements HttpSessionBindingListener {

	final static String AGENT_UNREGISTER_HOOK = "agentUnregister";

	final static Log log = LogFactory.getLog(AgentUnregisterHook.class);

	private SessionInfo session;

	private AgentUnregisterHook(SessionInfo session) {
		this.session = session;
		if (session.getHttpSession().getAttribute(AGENT_UNREGISTER_HOOK) != null) {
			throw new IllegalStateException(
					"May only be one agent unregister hook in any session.");
		}
		session.getHttpSession().setAttribute(AGENT_UNREGISTER_HOOK, this);
	}

	public void valueBound(HttpSessionBindingEvent event) {
		if (log.isInfoEnabled())
			log.info("New session agent hook.");
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		if (log.isInfoEnabled())
			log.info("Session invalidate. Deregistering agent");
		DefaultAgentManager.getInstance().unregisterAgent(session);
	}

	public static void register(SessionInfo session) {
		if(session.getHttpSession() != null && session.getHttpSession().getAttribute(AGENT_UNREGISTER_HOOK) == null) {
			new AgentUnregisterHook(session);
		}		
	}

}