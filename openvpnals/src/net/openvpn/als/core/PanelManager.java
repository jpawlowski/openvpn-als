
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows <i>Panels</i> to be register.
 * 
 * @see net.openvpn.als.core.Panel
 */
public class PanelManager {

    // Private instance variables
    private Map<String, Panel> panels;

    // Private statics
    private static PanelManager instance;

    /*
     * Private constructor to prevent instantiation
     */
    private PanelManager() {
        super();
        panels = new HashMap<String, Panel>();
    }

    /**
     * Add a panel
     * 
     * @param panel panel to add
     */
    public void addPanel(Panel panel) {
        panels.put(panel.getId(), panel);
    }

    /**
     * Remove a panel
     * 
     * @param id id of panel to remove
     */
    public void removePanel(String id) {
        panels.remove(id);
    }

    /**
     * Get an instance of the key store import type manager.
     * 
     * @return key store import type manager
     */
    public static PanelManager getInstance() {
        if (instance == null) {
            instance = new PanelManager();
        }
        return instance;
    }

    /**
     * Get a panel give its id. No check if the panel is available is made. If
     * no such panel exists <code>null</code> will be returned.
     * 
     * @param id panel id
     * @return panel
     */
    public Panel getPanel(String id) {
        return (Panel) panels.get(id);
    }

    /**
     * Get a sort list of registered {@link Panel} objects to display for the
     * given placement based on the current state. The
     * {@link Panel#isAvailable(HttpServletRequest, HttpServletResponse, String)} method
     * will be called and only if this returns <i>true</i> will the panel be in
     * the list.
     * 
     * @param placement placement
     * @param request request
     * @param response response
     * @param layout the layout the page is currently in.
     * @return list of appropriate panels
     */
    public List getPanels(int placement, HttpServletRequest request, HttpServletResponse response, String layout) {
        List a = new ArrayList();
        for (Iterator i = panels.values().iterator(); i.hasNext();) {
            Panel p = (Panel) i.next();
            if ((placement == -1 || p.getPlacement() == placement) && p.isAvailable(request, response, "main")) {
                a.add(p);
            }
        }
        Collections.sort(a, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                Panel p1 = (Panel) arg0;
                Panel p2 = (Panel) arg1;
                return new Integer(p1.getWeight()).compareTo(new Integer(p2.getWeight()));
            }

        });
        return a;
    }

    /**
     * Get a sorted list of registered of {@link Panel} objects to display for the
     * given placement based on the current state. The
     * {@link Panel#isAvailable(HttpServletRequest, HttpServletResponse, String)} method
     * will be called and only if this returns <i>true</i> will the panel be in
     * the list.
     * 
     * @param placement placement
     * @param request request
     * @param response response
     * @param containerId the div id that contains these panels (used for drag and drop)
     * @param layout the layout the page is currently in.
     * @return list of appropriate panels
     */
    public List<Panel> getPanels(int placement, HttpServletRequest request, HttpServletResponse response, String containerId, String layout) {
        Map<String, PanelWrapper> a = new HashMap<String, PanelWrapper>();
        for (Panel p : panels.values()) {
            if ((placement == -1 || p.getPlacement() == placement) && p.isAvailable(request, response, layout)) {                
                a.put(p.getId(), new PanelWrapper(p, p.getWeight()));
            }
        }

        if (containerId != null) {
            /*
             * Now go through this and remove and panels that may have been
             * moved to another container
             */
            for (PanelWrapper p : new ArrayList<PanelWrapper>(a.values())) {
                String cv = CoreUtil.getCookieValue("frame_component_" + p.getPanel().getId() + "_pos", request, "");

                // If the cookie value is a number then it is absolutely placed
                if (!cv.equals("")) {
                    try {
                        Integer.parseInt(cv);
                        continue;
                    } catch (NumberFormatException nfe) {
                        try {
                            int idx = cv.lastIndexOf('_');
                            String parent = cv.substring(0, idx);
                            if(!parent.equals(containerId) && p.getPanel().isDropable()) {
                                a.remove(p.getPanel().getId());
                            }

                        } catch (Exception e) {
//                            e.printStackTrace();
                        }

                    }
                }
            }
            
            /*
             * Now go through all the moved panels and add any that should
             * be in this container
             */
            Cookie[] c = request.getCookies();
            if(c != null) {
                for(int i = 0 ; i <c.length ; i++) {
                    if(c[i].getName().startsWith("frame_component_") && c[i].getName().endsWith("_pos")) {
                        String cv = c[i].getValue();
                        try {
                            Integer.parseInt(cv);
                            continue;
                        } catch (NumberFormatException nfe) {
                            try {
                                int idx = cv.lastIndexOf('_');
                                String parent = cv.substring(0, idx);
                                int order = Integer.parseInt(cv.substring(idx + 1));
                                if(parent.equals(containerId)) {
                                    String pnId = c[i].getName().substring(16, c[i].getName().length() - 4);
                                    Panel pn = getPanel(pnId);
                                    if(pn != null && pn.isDropable()) {
                                        if (pn.isAvailable(request, response, layout)) {
                                            PanelWrapper w = a.get(pnId);
                                            if(w == null) {
                                                a.put(pnId, w = new PanelWrapper(pn, order));
                                            } else {
                                                w.setWeight(order);
                                            }  
                                            
                                        }
                                    }
                                }

                            } catch (Exception e) {
//                                e.printStackTrace();
                            }

                        }
                        
                    }
                }
            }
        }
        List<PanelWrapper> l = new ArrayList<PanelWrapper>(a.values());
        Collections.sort(l, new Comparator<PanelWrapper>() {
            public int compare(PanelWrapper arg0, PanelWrapper arg1) {
                return new Integer(arg0.getWeight()).compareTo(new Integer(arg1.getWeight()));
            }
        });            
        List<Panel> ls = new ArrayList<Panel>();
        for(PanelWrapper w : l) {
            ls.add(w.getPanel());
        }
        return ls;
    }

}
