
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMessages;

import com.adito.security.Constants;

/**
 * A special {@link ActionForward} to get around the problem of losing messages
 * and errors when using a redirecting action forward. Redirects are sometimes
 * necessary so that <i>referer</i> can be used for page elements that may be
 * used on any page (agent launching, chaning language or profile etc).
 * 
 * <p>
 * The errors are stored in the session under a unique key which is added to the
 * request path. The request process then uses this unique key to retrieve the
 * messages back from the sessio and place them in the
 */
public class RedirectWithMessages extends ActionForward {

	/**
	 * Request parameter name used to pass the <i>Messages Id</i>. This is also
	 * used to store the current message ID in the session
	 */
	public final static String MESSAGE_ID = "msgId";

	/**
	 * Session attribute name used to store the map of messages.
	 */
	public final static String SESSION_MESSAGES = "sessionMessages";

    /**
     * @param forward
     * @param request
     */
    public RedirectWithMessages(ActionForward forward, HttpServletRequest request) {
        this(forward.getPath(), request);
    }
        
	/**
	 * Constructor.
	 * @param path path
	 * @param request request
	 */
	public RedirectWithMessages(String path, HttpServletRequest request) {
		super(path, true);
        
        // Cannot use tiles with redirect with messages
        if(path.startsWith(".")) {
            throw new IllegalArgumentException("You cannot use a tile with " + getClass().getName());
        }
        
		int id = getNextId(request.getSession());
		// Create map of messages for the request
		Map<String, ActionMessages> messageMap = createMessageMap(id, request);

		// Add the messages
		addMessageFromSession(Globals.MESSAGE_KEY, request, messageMap);
		addMessageFromSession(Globals.ERROR_KEY, request, messageMap);
		addMessageFromSession(Constants.REQ_ATTR_WARNINGS, request, messageMap);
		addMessageFromSession(Constants.BUNDLE_ERRORS_KEY, request, messageMap);
		addMessageFromSession(Constants.BUNDLE_MESSAGES_KEY, request, messageMap);

		// Create a new path containing the ID
		setPath(CoreUtil.addParameterToPath(CoreUtil.removeParameterFromPath(path, MESSAGE_ID), MESSAGE_ID, String.valueOf(id)));
	}

	static void addMessageFromSession(String key, HttpServletRequest request, Map<String, ActionMessages> messageMap) {
		ActionMessages msgs = (ActionMessages) request.getAttribute(key);
		if (msgs != null) {
			messageMap.put(key, msgs);
		}
	}

	static int getNextId(HttpSession session) {
		synchronized (session) {
			Integer i = (Integer) session.getAttribute(MESSAGE_ID);
			if (i == null) {
				i = new Integer(0);
			} else {
				i = new Integer(i.intValue() + 1);
			}
			session.setAttribute(MESSAGE_ID, i);
			return i.intValue();
		}
	}

	/**
	 * Look for the {@link #MESSAGE_ID} parameter and if found, move the
	 * messages from the session back into the request. This should be called by
	 * the request processor.
	 * @param request request
	 */
	@SuppressWarnings("unchecked")
    public static void repopulate(HttpServletRequest request) {
		synchronized (request.getSession()) {
			Map<Integer, Map<String, ActionMessages>> map = (Map<Integer, Map<String, ActionMessages>>) request.getSession().getAttribute(SESSION_MESSAGES);
			if (map != null) {
				String msgId = request.getParameter(MESSAGE_ID);
				if (msgId != null) {
					Integer id = new Integer(msgId);
					Map<String, ActionMessages> messageMap = map.get(id);
					if (messageMap != null) {
						for (String key : messageMap.keySet()) {
							request.setAttribute(key, messageMap.get(key));
						}
						map.remove(id);
					}
				}
				if(map.size() == 0) {
					request.getSession().removeAttribute(SESSION_MESSAGES);
				}
			}
		}
	}
	
	/**
	 * @param request
	 * @param key
	 * @param messages
	 * @param path
	 * @return String
	 */
	public static String addMessages(HttpServletRequest request, String key, ActionMessages messages, String path) {
		int id = getNextId(request.getSession());
		// Create map of messages for the request
		Map<String, ActionMessages> messageMap = createMessageMap(id, request);
		// Add the messages
		messageMap.put(key, messages);
		return CoreUtil.addParameterToPath(path, MESSAGE_ID, String.valueOf(id));
	}
	
	/**
	 * @param id
	 * @param request
	 * @return Map<String, ActionMessages>
	 */
	@SuppressWarnings("unchecked")
    public static Map<String, ActionMessages> createMessageMap(int id, HttpServletRequest request) {
		Map<Integer, Map<String, ActionMessages>> map = (Map<Integer, Map<String, ActionMessages>>) request.getSession().getAttribute(SESSION_MESSAGES);

		// Get all of the messages for this session
		if (map == null) {
			map = new HashMap<Integer, Map<String, ActionMessages>>();
			request.getSession().setAttribute(SESSION_MESSAGES, map);
		}

		// Create map of messages for the request
		Map<String, ActionMessages> messageMap = new HashMap<String, ActionMessages>();
		map.put(new Integer(id), messageMap);
		
		return messageMap;
	}
}