
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import net.openvpn.als.boot.SystemProperties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class JUnitHttpTestRunner {
    /**
     * @return Test
     */
    public static Test suite() {
        try {

            Collection<HttpTestContainer> containers = HttpTestParser.generateTests(getTestFiles());
            TestSuite suite = new TestSuite();
            for (HttpTestContainer container : containers) {
                for (HttpTestEntry entry : container.getEntries()) {
                    suite.addTest(createTestCase(container, entry));
                }
            }
            return suite;
        } catch (IOException e) {
            e.printStackTrace();
            return new TestSuite();
        }
    }

    private static String[] getTestFiles() throws FileNotFoundException {
        String location = SystemProperties.get("test.location", "");
        if (location.length() == 0) {
            throw new FileNotFoundException("No Test Files Found");
        }

        Collection<String> fileNames = new HashSet<String>();
        for (StringTokenizer tokenizer = new StringTokenizer(location, ";"); tokenizer.hasMoreTokens();) {
            fileNames.addAll(getTestFiles(tokenizer.nextToken()));
        }
        return fileNames.toArray(new String[fileNames.size()]);
    }

    private static Collection<String> getTestFiles(String location) {
        File locationDir = new File(location);
        File[] files = locationDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith("-tests.xml");
            }
        });

        Collection<String> fileNames = new HashSet<String>();
        for (File file : files) {
            fileNames.add(file.getAbsolutePath());
        }
        return fileNames;
    }

    private static TestCase createTestCase(HttpTestContainer container, HttpTestEntry entry) {
        final HttpTestRunner runner = new HttpTestRunner(container, entry);
        return new TestCase(entry.getName()) {
            protected void setUp() throws Exception {
                runner.setUp();
            }

            @Override
            protected void tearDown() throws Exception {
                runner.tearDown();
            }

            @Override
            protected void runTest() throws Throwable {
                runner.runTest();
            }
        };
    }
}