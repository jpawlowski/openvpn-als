
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
			
package com.adito.networkplaces;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.policyframework.ResourceType;
import com.adito.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

public class NetworkPlacesTests extends AbstractTestPolicyEnabledResource<NetworkPlace> {

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("adito-community-network-places");
    }

    @Override
    public NetworkPlace getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultNetworkPlace(getDefaultRealm().getRealmID(), -1, "", "", "", "", "/", 0, "", "", 0, false, true, false, false, false, calendar, calendar);
    }

    @Override
    public NetworkPlace getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultNetworkPlace(getDefaultRealm().getRealmID(), -1, "scheme", "root", "Root access to windows box.", "", "C:/", 0, "", "", 0, false, true, false, false, false, calendar, calendar);
    }

    @Override
    public NetworkPlace getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultNetworkPlace(getDefaultRealm().getRealmID(), -1, null, null, null, null, null, 0, null, null, 0, false, true, false, false, false, calendar, calendar);
    }

    @Override
    public NetworkPlace createResource(NetworkPlace networkPlace) throws Exception {
        return getNetworkPlaceService().createNetworkPlace(networkPlace.getScheme(),
            networkPlace.getResourceName(), networkPlace.getResourceDescription(),
            networkPlace.getHost(), networkPlace.getPath(), networkPlace.getPort(),
            networkPlace.getUsername(), networkPlace.getPassword(),
            networkPlace.isAllowRecursive(), networkPlace.isReadOnly(),
            networkPlace.isNoDelete(), networkPlace.isShowHidden(), networkPlace.isAutoStart(), networkPlace.getRealmID());
    }

    @Override
    public NetworkPlace updateResource(NetworkPlace networkPlace) throws Exception {
        getNetworkPlaceService().updateNetworkPlace(networkPlace.getResourceId(),
            networkPlace.getScheme(), networkPlace.getResourceName(),
            networkPlace.getResourceDescription(), networkPlace.getHost(),
            networkPlace.getPath(), networkPlace.getPort(), networkPlace.getUsername(),
            networkPlace.getPassword(), networkPlace.isAllowRecursive(),
            networkPlace.isReadOnly(), networkPlace.isNoDelete(),
            networkPlace.isShowHidden(), networkPlace.isAutoStart());
        return getNetworkPlaceService().getNetworkPlace(networkPlace.getResourceId());
    }

    @Override
    public ResourceType getResourceType() throws Exception {
        return NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE;
    }

    @Override
    public NetworkPlace deleteResource(NetworkPlace resource) throws Exception {
        return getNetworkPlaceService().deleteNetworkPlace(resource.getResourceId());
    }

    @Override
    public NetworkPlace getResource(NetworkPlace resource) throws Exception {
        return getNetworkPlaceService().getNetworkPlace(resource.getResourceId());
    }

    protected static NetworkPlaceDatabase getNetworkPlaceService() throws Exception {
        return NetworkPlaceDatabaseFactory.getInstance();
    }

    @Test
    public void createUncommonNetworkPlace() throws Exception {
        DefaultNetworkPlace networkPlace = new DefaultNetworkPlace(getDefaultRealm().getRealmID(), -1, "�$%24D", "root-6078��", "Root access to windn�-ows box.",
                        "�P�$%", "dsf!�", 0, "��0�0s3d0asd2qwd6", "��0�0s3d0asd2qwd633��$�%�$E", 0, true, false, true, true, true,
                        Calendar.getInstance(), Calendar.getInstance());
        assertEquals("There should not be any NetworkPlace", 0, getNetworkPlaceService().getNetworkPlaces().size());
        NetworkPlace createdNetworkPlace = createResource(networkPlace);
        assertEquals("There should be only one NetworkPlace", 1, getNetworkPlaceService().getNetworkPlaces().size());
        deleteResource(createdNetworkPlace);
        assertEquals("There should not be any NetworkPlace", 0, getNetworkPlaceService().getNetworkPlaces().size());
    }

    @Override
    public List<NetworkPlace> getAllResources() throws Exception {
        return getNetworkPlaceService().getNetworkPlaces();
    }
}