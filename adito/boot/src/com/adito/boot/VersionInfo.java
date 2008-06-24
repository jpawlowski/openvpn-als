
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
			
package com.adito.boot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Get the version of Adito in use. If running in a development
 * environment this will be retrieved from the build.properties file, otherwise.
 * the build process should have replaced the static {@link #VERSION} constant
 * with the real version.
 * <p>
 * Also contains a utility class that may be used to by other software
 * components to represent a version.
 * 
 * @author Brett Smith <a href="mailto: brett@localhost">&lt;brett@localhost&gt;</a>
 */
public class VersionInfo {

    private final static String VERSION = "0.9.0";

    private static Version version;
    private static boolean developmentVersion;

    static {
        if (VERSION.startsWith("999.")) {
            developmentVersion = true;
            Properties p = new Properties();
            InputStream in = null;
            try {
                in = new FileInputStream("build.properties");
                p.load(in);
                version = new Version(p.getProperty("version.major", "999") + "." + p.getProperty("version.minor", "999") + "."
                                + p.getProperty("version.build", "999") + p.getProperty("version.tag"));
            } catch (IOException ioe) {
                version = new Version("0.9.0");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        } else {
            version = new Version(VERSION);
        }
    }

    /**
     * Determine if this is a development version. This is true if the 
     * {@link #VERSION} has not been replaced (i.e. is 0.9.0).
     * 
     * @return is development version
     */
    public static boolean isDevelopmentVersion() {
        return developmentVersion;
    }

    /**
     * Get the current Adito version
     * 
     * @return version
     */
    public static Version getVersion() {
        return version;
    }

    /**
     * Represents the version number of a software component such as 
     * Adito itself or perhaps an extension.
     * <p>
     * The object may be constructed from a dotted version string. An
     * optional <i>tag</i> element may also be provided by  appending an
     * underscore (_) then the tag. 
     * <p>
     * For example
     * <p><code>0.1.14_alpha</code></p> would give a major version of 0,
     * a minor version of 1, a build version of 14 and a tag of 'alpha'.
     * 
     * @author Brett Smith <a href="mailto: brett@localhost">&lt;brett@localhost&gt;</a>
     */
    public static class Version implements Comparable {
        int minor;
        int major;
        int build;
        String tag;
        int tagOffset;

        /**
         * Constructor
         * 
         * @param version version string (see class description)
         */
        public Version(String version) {
            StringTokenizer t = new StringTokenizer(version, ".");
            try {
                major = Integer.parseInt(t.nextToken());
                minor = Integer.parseInt(t.nextToken());

                if (t.hasMoreTokens()) {
                    String s = "";
                    while (t.hasMoreTokens()) {
                        String token = (String) t.nextElement();
                        s = s + token + (t.hasMoreTokens()? "_" : "");
                    }
                    int pos = s.indexOf('_');
                    if (pos > -1) {
                        build = Integer.parseInt(s.substring(0, pos));
                        tag = s.substring(pos);
                    } else {
                        build = Integer.parseInt(s);
                        tag = "";
                    }
                } else {
                    build = 0;
                    tag = "";
                }
            } catch (Throwable ex) {
                major = 999;
                minor = 999;
                build = 999;
                tag = "";
            }
            
            // The tag may have meaning in version comparsions
            tagOffset = 0;
            tag = tag.toUpperCase();
            
            // Release candidate phase (up to 100 releases with RCx as tag)
            if(!tag.equals("")) {
                if(tag.startsWith("_RC")) {
                    // Tags may hold dates for nightly build
                    int toIdx = tag.indexOf('_', 1);
                    if(toIdx == -1) {
                        tagOffset = -100 + Integer.parseInt(tag.substring(3));
                    }
                    else {
                        tagOffset = -100 + Integer.parseInt(tag.substring(3, toIdx));
                    }
                }
                else {
                    // Subrelease (tag is a number)
                    try {
                        tagOffset = Integer.parseInt(tag.substring(1));
                    }
                    catch(NumberFormatException nfe) {
                        // Tag has no meaning to comparison
                    }               
                }
            }
            
        }

        /**
         * Constructor.
         * 
         * @param major major version
         * @param minor minor version
         * @param build build version
         */
        public Version(int major, int minor, int build) {
            this.major = major;
            this.minor = minor;
            this.build = build;
        }

        /**
         * Compare to versions. Version are equal if the major, minor and
         * build elements are the same. The tag element is not taken into
         * account.
         *  
         * @param o other version
         * @return comparison
         */
        public int compareTo(Object o) {
            return new Integer(hashCode()).compareTo(new Integer(o.hashCode()));
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return (int)(((long)major * 1000000000l) + ((long)minor * 1000000l) + ((long)build * 1000l) + tagOffset);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return major + "." + minor + "." + build + tag;
        }

        /**
         * Get the major version number.
         * 
         * @return major version number
         */
        public int getMajor() {
            return major;
        }

        /**
         * Get the minor version number.
         * 
         * @return minor version number
         */
        public int getMinor() {
            return minor;
        }

        /**
         * Get the build version number.
         * 
         * @return minor version number
         */
        public int getBuild() {
            return build;
        }

        /**
         * Get the tag.
         * 
         * @return tag
         */
        public String getTag() {
            return tag;
        }
    }
}
