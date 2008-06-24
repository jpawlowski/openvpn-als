
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
			

//
// Modified for use in Adito by 3SP. These portions licensed as above,
// everything else licensed as below.
//  
//

// ========================================================================
// $Id$
// Copyright 2002-2004 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package com.adito.core;


/* ------------------------------------------------------------ */
/** Byte Array Pool
 * Simple pool for recycling byte arrays of a fixed size.
 *
 * @author Greg Wilkins (gregw)
 */
public class ByteArrayPool
{
    public static final int __POOL_SIZE=
        Integer.getInteger("org.mortbay.util.ByteArrayPool.pool_size",8).intValue();
    
    public static final ThreadLocal __pools=new BAThreadLocal();
    public static int __slot;
    
    /* ------------------------------------------------------------ */
    /** Get a byte array from the pool of known size.
     * @param size Size of the byte array.
     * @return Byte array of known size.
     */
    public static byte[] getByteArray(int size)
    {
        byte[][] pool = (byte[][])__pools.get();
        boolean full=true;
        for (int i=pool.length;i-->0;)
        {
            if (pool[i]!=null && pool[i].length==size)
            {
                byte[]b = pool[i];
                pool[i]=null;
                return b;
            }
            else
                full=false;
        }

        if (full)
            for (int i=pool.length;i-->0;)
                pool[i]=null;
        
        return new byte[size];
    }

    /* ------------------------------------------------------------ */
    public static byte[] getByteArrayAtLeast(int minSize)
    {
        byte[][] pool = (byte[][])__pools.get();
        for (int i=pool.length;i-->0;)
        {
            if (pool[i]!=null && pool[i].length>=minSize)
            {
                byte[]b = pool[i];
                pool[i]=null;
                return b;
            }
        }
        
        return new byte[minSize];
    }


    /* ------------------------------------------------------------ */
    public static void returnByteArray(final byte[] b)
    {
        if (b==null)
            return;
        
        byte[][] pool = (byte[][])__pools.get();
        for (int i=pool.length;i-->0;)
        {
            if (pool[i]==null)
            {
                pool[i]=b;
                return;
            }
        }

        // slot.
        int s = __slot++;
        if (s<0)s=-s;
        pool[s%pool.length]=b;
    }

    
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    private static final class BAThreadLocal extends ThreadLocal
    {
        protected Object initialValue()
            {
                return new byte[__POOL_SIZE][];
            }
    }
}
