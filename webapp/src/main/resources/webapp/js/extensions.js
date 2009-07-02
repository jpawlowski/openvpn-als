/*
 * This library contains general object extensions
 *
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

/**
 * Test a string for equality, ignoring case
 *
 * @param string string to test
 * @return boolean indicating equality
 */
if (!String.prototype.equalsIgnoreCase) {
	String.prototype.equalsIgnoreCase = function(otherString) {
		return this.toLowerCase() == otherString.toLowerCase();
	};
}

/**
 * Test if a string ends with another
 *
 * @param string string to test
 * @return boolean indicating equality
 */
if (!String.prototype.endsWith) {
	String.prototype.endsWith = function(suffix) {
	    var startPos = this.length - suffix.length;
	    return startPos < 0 ? false : (this.lastIndexOf(suffix, startPos) == startPos);
	};
}

/**
 * Test if a string starts with another
 *
 * @param string string to test
 * @return boolean indicating equality
 */
if (!String.prototype.startsWith) {
	String.prototype.startsWith = function(prefix) {
	    return this.indexOf(prefix, 0) == 0;
	};
}

/**
 * Remove an element from an array
 *
 * @param string string to test
 * @return boolean indicating equality
 */
if (!Array.prototype.remove) {
	Array.prototype.remove = function(element) {
		var a = new Array();
		for(var i = this.length - 1; i >= 0; i--) {
			if(this[i] != element) {
				a.push(element);
			}
			pop();
		}
		for(var i = a.length - 1; i >= 0; i--) {
			push(a.pop());
		}
		return element;
	};
}

// Array.splice() - Remove or replace several elements and return any deleted elements
if( typeof Array.prototype.splice==='undefined' ) {
 Array.prototype.splice = function( a, c ) {
  var i = 0, e = arguments, d = this.copy(), f = a, l = this.length;
  if( !c ) { c = l - a; }
  for( i; i < e.length - 2; i++ ) { this[a + i] = e[i + 2]; }
  for( a; a < l - c; a++ ) { this[a + e.length - 2] = d[a - c]; }
  this.length -= c - e.length + 2;
  return d.slice( f, f + c );
 };
}