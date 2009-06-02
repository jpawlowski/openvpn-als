
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
			
package net.openvpn.als.tabs;

/**
 * Interface that supports that HTML <i>Tab Component</i>. If you wish a form
 * to be 'tabbable', you should implement this interface.
 * <p>
 * The displayed tab titles by default are retrieved from resource bundles by
 * using the bundle and resource prefix specified in the &lt;tabSet&gt; tag
 * suffixed by the value returned from {@link #getTabName(int)} and <b>.title</b>.
 * <p>
 * If you wish to return a hard coded title, return a <code>non-null</code>
 * value from {@link #getTabTitle(int)}. This will override the use of
 * resources.
 * 
 * @see net.openvpn.als.tabs.tags.TabHeadingsTag
 * @see net.openvpn.als.tabs.tags.TabSetTag
 * @see net.openvpn.als.tabs.tags.TabTag
 */
public interface TabModel {

    /**
     * Get the number of tabs to display
     * 
     * @return tab count
     */
    public int getTabCount();

    /**
     * Get the name of the tab. This is used together with the the bundle
     * specified in the &lt;tabSet&gt; tag to determine the title from
     * resources.
     * <p>
     * It is also used in the CSS and Javascript to identify each tab and will
     * be the the value used for selected tab so <strong> must not</strong>
     * contain spaces (best to use lowerCamelCase).
     * 
     * @param idx index of tab
     * @return tab name
     */
    public String getTabName(int idx);

    /**
     * Get the selected tab <b>name</i>.
     * 
     * @return selected tab name
     * @see #getTabName(int)
     */
    public String getSelectedTab();

    /**
     * Set the selected tab <b>name</i>.
     * 
     * @param selectedTab selected tab name
     * @see #getTabName(int)
     */
    public void setSelectedTab(String selectedTab);

    /**
     * If you wish to return a hard coded title, return a <code>non-null</code>
     * value here. This will override the use of resources. Return <code>null</code>
     * to get the tab title from resources.
     * 
     * @param idx index of tab
     * @return tab title
     */
    public String getTabTitle(int idx);

    /**
     * Return an alternative bundle to use for tab title message resources.
     * This is useful when tabs are added by plugins. <code>null</code>
     * will be returned if there is no alternative bundle and the default
     * one provided in the JSP should be used.
     *  
     * @param idx tab index
     * @return alternative bundle
     */
    public String getTabBundle(int idx);
}
