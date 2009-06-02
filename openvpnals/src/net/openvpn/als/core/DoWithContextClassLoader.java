
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
			
package net.openvpn.als.core;


public abstract class DoWithContextClassLoader extends Thread {
    
    private Object value;
    private Exception exception;
    
    public DoWithContextClassLoader(ClassLoader classLoader)  {
        super("DoWithContextClassLoader-" + classLoader.getClass().getName());
        setContextClassLoader(classLoader);
    }   
    
    public Object doWith() throws Exception {
        start();
        join();
        if(exception != null) {
            throw exception;
        }
        return value;
    }
    
    public void run() {
        try {
            value = doRun();
        }
        catch(Exception e) {
            exception = e;
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    public abstract Object doRun() throws Exception;
}