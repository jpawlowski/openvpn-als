
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
			
package net.openvpn.als.httpunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import com.maverick.http.GetMethod;
import com.maverick.http.HttpClient;
import com.maverick.http.HttpMethod;
import com.maverick.http.HttpResponse;
import com.maverick.http.PostMethod;

/**
 */
final class HttpTestRunner {

    private final HttpClient client;
    private final HttpTestContainer container;
    private final HttpTestEntry entry;

    HttpTestRunner(HttpTestContainer container, HttpTestEntry entry) {
        client = new HttpClient(container.getRootUrl(), container.getPort(), false);
        this.container = container;
        this.entry = entry;
    }

    void setUp() throws Exception {
        if (entry.isAuthenticated()) {
            authenticate(client, container, entry);
        }
    }

    void tearDown() throws Exception {
        logOff(client);
    }

    void runTest() throws Exception {
        for (HttpTestEntryStep step : entry.getSteps()) {
            runStep(client, step);
        }
    }

    private static void authenticate(HttpClient client, HttpTestContainer container, HttpTestEntry entry) throws Exception {
        String username = isEmpty(entry.getUsername()) ? container.getDefaultUsername() : entry.getUsername();
        String password = isEmpty(entry.getPassword()) ? container.getDefaultPassword() : entry.getPassword();

        GetMethod usernameLogon = new GetMethod("/usernameLogon.do");
        usernameLogon.setParameter("username", username);
        usernameLogon.setParameter("password", password);
        HttpResponse usernameLogonResponse = client.execute(usernameLogon);
        assertEquals("Authenticated Failed", 200, usernameLogonResponse.getStatus());

        GetMethod logon = new GetMethod("/logon.do");
        logon.setParameter("username", username);
        logon.setParameter("password", password);
        HttpResponse logonResponse = client.execute(logon);
        assertEquals("Authenticated Failed", 200, logonResponse.getStatus());
    }

    private static void logOff(HttpClient client) throws Exception {
        GetMethod getMethod = new GetMethod("/logoff.do");
        HttpResponse httpResponse = client.execute(getMethod);
        assertEquals("Log Off Failed", 302, httpResponse.getStatus());
        assertRedirect(httpResponse, "showHome.do");
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    private static void runStep(HttpClient client, HttpTestEntryStep step) throws Exception {
        String url = "/" + step.getUrl();
        HttpMethod get = step.isPost() ? new PostMethod(url) : new GetMethod(url);
        for (Map.Entry<String, String> entry : step.getParameters().entrySet()) {
            get.setParameter(entry.getKey(), entry.getValue());
        }

        HttpResponse response = client.execute(get);
        int responseCode = response.getStatus();
        assertEquals("Unexpected Status", step.getExpectedCode(), responseCode);
        assertRedirect(response, step.getRedirectUrl());

        Collection<String> messages = getActionMessages(response, "unitTestMessages");
        assertEquals("The messages differ", step.getMessages(), messages);
        Collection<String> errors = getActionMessages(response, "unitTestErrors");
        assertEquals("The errors differ", step.getErrors(), errors);
    }

    private static void assertRedirect(HttpResponse response, String redirectUrl) {
        String[] headerFields = response.getHeaderFields("location");
        if (redirectUrl != null) {
            if (headerFields == null || headerFields.length == 0) {
                fail("Redirect expected but not found");
            } else {
                String strippedLocation = getStrippedLocation(headerFields[0]);
                if (!redirectUrl.equals(strippedLocation)) {
                    fail("Redirect was incorrect : expected = '" + redirectUrl + "' actual = '" + strippedLocation + "'");
                }
            }
        }
    }

    private static String getStrippedLocation(String value) {
        String strippedPrefix = value.substring(value.lastIndexOf("/") + 1);
        String strippedPostfix = strippedPrefix.substring(0, strippedPrefix.indexOf(".") + 3);
        return strippedPostfix;
    }

    private static Collection<String> getActionMessages(HttpResponse response, String headerName) {
        String headerField = response.getHeaderField(headerName);
        if (headerField == null) {
            return Collections.<String> emptySet();
        }

        Collection<String> messages = new HashSet<String>();
        for (StringTokenizer tokenizer = new StringTokenizer(headerField, ","); tokenizer.hasMoreTokens();) {
            messages.add(tokenizer.nextToken());
        }
        return messages;
    }
}