
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
			
package com.sshtools.ui.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

public class PrintView extends BoxView {
    protected int m_firstOnPage = 0;
    protected int m_lastOnPage = 0;
    protected int m_pageIndex = 0;

    public PrintView(Element elem, View root, int w, int h) {
        super(elem, Y_AXIS);
        setParent(root);
        setSize(w, h);
        layout(w, h);
    }

    public boolean paintPage(Graphics g, int hPage, int pageIndex) {
        if (pageIndex > m_pageIndex) {
            m_firstOnPage = m_lastOnPage + 1;
            if (m_firstOnPage >= getViewCount()) {
                return false;
            }
            m_pageIndex = pageIndex;
        }
        int yMin = getOffset(Y_AXIS, m_firstOnPage);
        int yMax = yMin + hPage;
        Rectangle rc = new Rectangle();
        for (int k = m_firstOnPage; k < getViewCount(); k++) {
            rc.x = getOffset(X_AXIS, k);
            rc.y = getOffset(Y_AXIS, k);
            rc.width = getSpan(X_AXIS, k);
            rc.height = getSpan(Y_AXIS, k);
            if ((rc.y + rc.height) > yMax) {
                break;
            }
            m_lastOnPage = k;
            rc.y -= yMin;
            paintChild(g, rc, k);
        }
        return true;
    }
}