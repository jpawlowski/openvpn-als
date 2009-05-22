
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
			
package com.ovpnals.httpunit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

final class HttpTestParser {
    private static final String MATCH_TESTS = "tests";
    private static final String MATCH_TEST = "tests/test";
    private static final String MATCH_TEST_STEP = "tests/test/step";
    private static final String MATCH_ERROR = MATCH_TEST_STEP + "/errors/error";
    private static final String MATCH_MESSAGE = MATCH_TEST_STEP + "/messages/message";
    private static final String MATCH_PARAMETER = MATCH_TEST_STEP + "/parameters/parameter";

    static Collection<HttpTestContainer> generateTests(String[] paths) throws IOException {
        Collection<HttpTestContainer> tests = new HashSet<HttpTestContainer>(paths.length);
        for (String path : paths) {
            tests.add(generateTests(path));
        }
        return tests;
    }

    static HttpTestContainer generateTests(String path) throws IOException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);

            digester.addObjectCreate(MATCH_TESTS, HttpTestContainer.class);
            digester.addSetProperties(MATCH_TESTS, "rootUrl", "rootUrl");
            digester.addSetProperties(MATCH_TESTS, "port", "port");
            digester.addSetProperties(MATCH_TESTS, "defaultUsername", "defaultUsername");
            digester.addSetProperties(MATCH_TESTS, "defaultPassword", "defaultPassword");

            digester.addObjectCreate(MATCH_TEST, HttpTestEntry.class);
            digester.addSetProperties(MATCH_TEST, "name", "name");
            digester.addSetProperties(MATCH_TEST, "authenticated", "authenticated");
            digester.addSetProperties(MATCH_TEST, "username", "username");
            digester.addSetProperties(MATCH_TEST, "password", "password");

            digester.addObjectCreate(MATCH_TEST_STEP, HttpTestEntryStep.class);
            digester.addSetProperties(MATCH_TEST_STEP, "name", "name");
            digester.addSetProperties(MATCH_TEST_STEP, "method", "method");
            digester.addSetProperties(MATCH_TEST_STEP, "url", "url");
            digester.addSetProperties(MATCH_TEST_STEP, "expectedCode", "expectedCode");
            digester.addSetProperties(MATCH_TEST_STEP, "redirectUrl", "redirectUrl");

            digester.addObjectCreate(MATCH_PARAMETER, HttpTestEntryStep.Parameter.class);
            digester.addSetProperties(MATCH_PARAMETER, "key", "key");
            digester.addSetProperties(MATCH_PARAMETER, "value", "value");
            digester.addSetNext(MATCH_PARAMETER, "addParameter");

            digester.addObjectCreate(MATCH_MESSAGE, HttpTestEntryStep.Value.class);
            digester.addSetProperties(MATCH_MESSAGE, "value", "value");
            digester.addSetNext(MATCH_MESSAGE, "addMessage");

            digester.addObjectCreate(MATCH_ERROR, HttpTestEntryStep.Value.class);
            digester.addSetProperties(MATCH_ERROR, "value", "value");
            digester.addSetNext(MATCH_ERROR, "addError");

            digester.addSetNext(MATCH_TEST_STEP, "addStep");
            digester.addSetNext(MATCH_TEST, "addEntry");

            InputStream input = new FileInputStream(path);
            return (HttpTestContainer) digester.parse(input);
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
    }
}