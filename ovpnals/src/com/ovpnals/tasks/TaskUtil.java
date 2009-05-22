
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.tasks;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;

/**
 * A utility which wraps a path command to show a progress bar. 
 */
public class TaskUtil {

    /**
     * Get a session based progress bar wrapped path, useful where there is a session, normally a onClick.
     * 
     * @param path the path of the action
     * @param bundle the message resource bundle id
     * @param name the name of the progress bar
     * @param session The session associated with the on click action
     * @param width The total width of the progress bar.
     * @param height the total height of the progress bar, i.e. a single bar 100, but a double bar would be 200.
     * @return String the wrapped String
     */
    public static String getTaskPathOnClick(String path, String bundle, String name, HttpSession session, int width, int height) {
        ServletContext servletContext = session.getServletContext();
        Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
        return getTaskPath(path, bundle, name, servletContext, locale, width, height, true);
    }

    /**
     * Get a context based progress bar wrapped path, useful where there is no session, normally a href.
     * 
     * @param path the path of the action
     * @param bundle the message resource bundle id
     * @param name the name of the progress bar
     * @param context the servlet context
     * @param width The total width of the progress bar.
     * @param height the total height of the progress bar, i.e. a single bar 100, but a double bar would be 200.
     * @param isOnClick weather the path is to be used in an onClick.
     * @return String the wrapped String
     */
    public static String getTaskPath(String path, String bundle, String name, ServletContext context, int width, int height, boolean isOnClick) {
        return getTaskPath(path, bundle, name, context, Locale.getDefault(), width, height, isOnClick);
    }

    /**
     * @param path the path of the action
     * @param bundle the message resource bundle id
     * @param name the name of the progress bar
     * @param context the servlet context
     * @param locale The locale for the path.
     * @param width The total width of the progress bar.
     * @param height the total height of the progress bar, i.e. a single bar 100, but a double bar would be 200.
     * @param isOnClick weather the path is to be used in an onClick.
     * @return String the wrapped String
     */
    private static String getTaskPath(String path, String bundle, String name, ServletContext context, Locale locale, int width,
                                     int height, boolean isOnClick) {
        MessageResources messageResources = CoreUtil.getMessageResources(context, bundle);
        String title = messageResources == null ? "<Unknown>" : messageResources.getMessage(locale, "taskProgress." + name
                        + ".title");
        StringBuilder taskString = new StringBuilder("Modalbox.show('");
        taskString.append(title);
        taskString.append("', '/taskProgress.do?url=");
        taskString.append(Util.urlEncode(path));
        taskString.append("&bundle=");
        taskString.append(bundle);
        taskString.append("&name=");
        taskString.append(name);
        taskString.append("', { width: ");
        taskString.append(width);
        taskString.append(", height: ");
        taskString.append(height);
        taskString.append(", overlayClose: false });");
        if (isOnClick){
            taskString.append(" return false;");
        }
        return  taskString.toString();
    }
}