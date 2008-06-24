
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
			
package com.adito.sample;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.navigation.FavoriteResourceType;
import com.adito.navigation.WrappedFavoriteItem;
import com.adito.policyframework.DefaultResourceType;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;

/**
 * <p>
 * A sample resource.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public interface Sample extends Resource {

    public final static int SAMPLE_RESOURCE_TYPE_ID = 999999;

    public final static ResourceType SAMPLE_RESOURCE_TYPE = new SampleResourceType();

    static class SampleResourceType extends DefaultResourceType implements FavoriteResourceType {

        public SampleResourceType() {
            super(SAMPLE_RESOURCE_TYPE_ID, "sample", PolicyConstants.DELEGATION_CLASS);
        }

        public WrappedFavoriteItem createWrappedFavoriteItem(int resourceId, HttpServletRequest request, String type)
                        throws Exception {
            Resource r = getResourceById(resourceId);
            if (r == null) {
                return null;
            }
            return new WrappedFavoriteItem(new SampleItem((Sample) r, CoreServlet.getServlet().getPolicyDatabase()
                .getPoliciesAttachedToResource(r)), type);
        }

        public Resource getResourceById(int resourceId) throws Exception {
            return SamplePlugin.getDatabase().getSample(resourceId);
        }

        public Resource getResourceByName(String resourceName) throws Exception {
            return SamplePlugin.getDatabase().getSample(resourceName);
        }

        public Resource removeResource(int resourceId, SessionInfo session) throws Exception {
            try {
                Sample resource = SamplePlugin.getDatabase().removeSample(resourceId);
                // #ifdef XTRA
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, SamplePlugin.EVT_SAMPLE_DELETED, resource, session, CoreEvent.STATE_SUCCESSFUL));
                // #endif
                return resource;
            } catch (Exception e) {
                // #ifdef XTRA
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, SamplePlugin.EVT_SAMPLE_DELETED, null, session, CoreEvent.STATE_UNSUCCESSFUL));
                // #endif
                throw e;
            }

        }

        public void updateResource(Resource resource, SessionInfo session) throws Exception {
            try {
                SamplePlugin.getDatabase().updateSample((Sample) resource);
                // #ifdef XTRA
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, SamplePlugin.EVT_SAMPLE_UPDATED, resource, session, CoreEvent.STATE_SUCCESSFUL));
                // #endif
            } catch (Exception e) {
                // #ifdef XTRA
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, SamplePlugin.EVT_SAMPLE_UPDATED, null, session, CoreEvent.STATE_UNSUCCESSFUL));
                // #endif
                throw e;
            }
        }

    }
}