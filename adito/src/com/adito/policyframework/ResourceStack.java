
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
			
package com.adito.policyframework;

import java.util.Stack;

import javax.servlet.http.HttpSession;

import com.adito.security.Constants;


/**
 * Holds a stack of edited resources
 */
public class ResourceStack extends Stack<Resource> {

	/**
	 * Push a resource to the edit stack
	 * 
	 * @param session session
	 * @param resource resource
	 */
	public static void pushToEditingStack(HttpSession session, Resource resource) {
		ResourceStack stack = (ResourceStack)session.getAttribute(Constants.EDITING_RESOURCE_STACK);
		if(stack == null) {
			stack = new ResourceStack();
			session.setAttribute(Constants.EDITING_RESOURCE_STACK, stack);			
		}
		
		// A refresh on an edit page could cause a resource to be pushed more than once, this check prevents that
		if(!stack.contains(resource)) {		
			stack.push(resource);
		}
	}

	/**
	 * Pop a resource from the edit stack
	 * 
	 * @param session session
	 * @return resource
	 */
	public static Resource popFromEditingStack(HttpSession session) {
		ResourceStack stack = (ResourceStack)session.getAttribute(Constants.EDITING_RESOURCE_STACK);
		if(stack != null) {
			Resource r = stack.pop();
			if(stack.isEmpty()) {
				session.removeAttribute(Constants.EDITING_RESOURCE_STACK);
			}
			return r;
		}
		return null;
	}

	/**
	 * Peek at the item at the top of the stack
	 * 
	 * @param session session
	 * @return resource
	 */
	public static Resource peekEditingStack(HttpSession session) {
		ResourceStack stack = (ResourceStack)session.getAttribute(Constants.EDITING_RESOURCE_STACK);
		if(stack == null) {
			return null;			
		}
		return stack.peek();
	}

	/**
	 * Get if the stack is empty
	 * 
	 * @param session session
	 * @return empty
	 */
	public static boolean isEmpty(HttpSession session) {
		return session.getAttribute(Constants.EDITING_RESOURCE_STACK) == null;
	}
}
