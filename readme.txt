*************
* Adito VPN *
*************

What is Adito VPN?
------------------

Adito is an open-source, browser-based SSL VPN solution. It's a remote access solution that provides users and businesses alike with a means of securely accessing network resources from outside the network perimeter using only a standard web browser. Go to Adito homepage at http://sourceforge.net/projects/adito for the latest releases, development versions, documentation and help. This package contains all of the source and most of the tools required to build and customise Adito. For installation help take a look at the file INSTALL.
Adito is a fork of the "SSL-Explorer Community Edition" 1.0.0-rc17, which was discontinued by the firm (3sp) that had developed it. A commercial offering called SSL-Explorer Enterprise Edition with added functionality was also available. Latest versions of SSL-Explorer are closed-source. The exact reasons for this switch in direction are not clear. Because the entire SSL-Explorer codebase was written by 3sp it they had the right to close it without violating the GPL license. New versions of SSL-Explorer are entirely proprietary.

Adito was forked from SSL-Explorer 1.0.0-rc17 for several reasons:

- To keep the already robust and functional open source codebase from decaying
- To reform SSL-Explorer (now Adito) from one company's product and a brand into a true community project
- To add new, exiting functionality
- To integrate existing functionality (e.g. sslexplorer-pam) into the program without the need to maintain it out of the source tree  

SSL-Explorer Community Edition had to modified a bit to avoid legal issues. The name - SSL-Explorer - is 3sp's trademark, so it had to changed. In fact all references to SSL-Explorer have been removed entirely. This means that any SSL-Explorer extensions have to be modified slightly to work on Adito, though it's pretty trivial. SSL-Explorer also contained non-free icons. These have been replaced with free Tango icons (http://tango.freedesktop.org/Tango_Icon_Library) and icons based on them.

Adito is licensed under the GNU General Public License, just like SSL-Explorer Community Edition. GPL is a license that allows the free use of the software for commercial/non-commercial usage, however you must not incorporate code from this software into a commercial product, unless your commercial product is also to be licensed under the terms of the GPL. See adito/notes/LICENSE.txt for the full license text. Adito also includes other components under licenses other than the GPL (see adito/notes).


Running Adito
-------------

Once Adito has been built, running it is really simple. Just enter the Adito directory (e.g. /opt/adito-0.9.0) and do a 

 user@server:/opt/adito-0.9.0$ ant start
 
If you've configured Adito to listen on any port lower than 1024, you have to run this command as root.

Adito modules
-------------

* build-tools

Includes ant-contrib (http://ant-contrib.sourceforge.net/) and codeswitcher (an extended version of HSQLDB's Java source pre-processor), used by the build system.
						
* adito

This is the core of the Adito system including the default server implementation and the web application	itself.
						
* adito-agent

There are further pre-requisites for building your Adito Agent. See "Building the Agent" also in the document. 
						
* adito-agent-awt

Provides an AWT based user interface for the Agent.
						
* adito-agent-swt

Provides an SWT based user interface for the Agent.
						
* maverick-crypto

Contains classes and utilities used for cryptography. Used by the server, the agent.

* maverick-ssl

SSL,HTTP and HTTPS implementation. Used by both the server and the agent.
						
*maverick-multiplex

Stream multiplexing library.
						
* maverick-util

Stream general utilities library.

* adito-commons-vfs

A version of commons-vfs.

* ui

General purpose AWT components and utilities, used by the agent.
						
* commons-logging-java1

A Java 1.1 back-port of commons-logging (http://jakarta.apache.org/commons/logging/).
						
* adito-sample

A module that demonstrates creating a plugin for Adito. This sample amongst other things adds a new resource type.
                        
* adito-community-tunnels
						
A module that adds the SSL-Tunnels feature. This is also required for adito-community-applications 
						
* adito-community-applications

This module adds application extension as well as the application shortcuts user interface.						
						
* adito-community-web-forwards

This module adds all 3 types of web forwards.
						
* adito-community-network-places

This module adds the network places user interface as well a the default VFS store implementations (FTP, SMB and File).				
						
* adito-community-activedirectory

The module adds an Active Directory user database.
						
						                        						
Included 3rd Party Components
-----------------------------

The following components are included in the distribution and used by 
Adito.

* Struts 1.2.8

Adito's user interface is built using the Struts framework (http://struts.apache.org/).
						
* Jetty 5.0

This provides the web server in the default server implementation.
						
* HSQLDB 1.8.0

Provides SQL based storage for Adito's configuration.						
						
* jcifs-1.2.13

JCIFS is an Open Source client library that implements the CIFS/SMB networking protocol in 100% Java. CIFS is the standard file sharing protocol on the Microsoft Windows platform (e.g. Map Network Drive ...)
						
* Many more 3rd party libraries and components.			
						
  
Working with the Adito source
-----------------------------

There are four main source directories:
* boot

Contains the Java source for classes needed by the boot environment. While Adito is largely a standard web application, it requires some additional features not provided by the a standard servlet container. The 'boot' classes provide this and are used by both the server implementation and the web application. 

* server

Contains the Java source for the default server implementation. This is based on Jetty 5.0 (http://jetty.mortbay.org).

* src

Contains the Java source for the actual application and web based user interface.

* webapp

Contains all of the HTML resources (including images and CSS) as well as the JSP files.    				

If you make any changes to any modules Java source (this includes Java class
files themselves and any other resources that exist in the source directories
included message resources, XML files and property files), you may recompile 
either by running the 'compile' target in the root of this distribution or the
individual 'compile' targets in each modules build file. For example,

 $ cd /opt/adito-0.9.0
 $ ant compile

If you make changes to any of the adito-community-* modules, then
you should run the 'deploy-community-extensions' target :

 $ cd /opt/adito-0.9.0
 $ any deploy-community-extensions

You can generally change the JSP files and HTML resources while the server is running, a simple page 
refresh will usually do the trick.
