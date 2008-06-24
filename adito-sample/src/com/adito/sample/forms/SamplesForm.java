
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
			
package com.adito.sample.forms;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.CoreServlet;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.sample.Sample;
import com.adito.sample.SampleItem;
import com.adito.security.SessionInfo;

/**
 * <p>
 * Form providing the list of samples to the jspf.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SamplesForm extends AbstractResourcesForm {

    static Log log = LogFactory.getLog(SamplesForm.class);

    /**
     * Constructor
     */
    public SamplesForm() {
        super("samples");
    }

    /**
     * <p>
     * Initialise the pager and the items and the ability to sort.
     * 
     * @param samples List of SampleDatabase to be added.
     * @param session The sessin information.
     */
    public void initialise(List samples, SessionInfo session, String defaultSortColumnId) {
        super.initialize(session.getHttpSession(), defaultSortColumnId);
        try {
            for (Iterator i = samples.iterator(); i.hasNext();) {
                Sample sample = (Sample) i.next();
                List policies = CoreServlet.getServlet().getPolicyDatabase().getPoliciesAttachedToResource(sample);
                SampleItem si = new SampleItem(sample, policies);
                si.setFavoriteType(getFavoriteType(si.getResource().getResourceId()));
                getModel().addItem(si);
            }
            checkSort();
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            log.error("Failed to initialise resources form.", t);
        }
    }
}
