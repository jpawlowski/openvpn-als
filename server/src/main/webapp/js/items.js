/*
 * This library contains functions for dealing with Adito
 * entities that are not resources.
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
 * Get information on an item
 *
 * @param item item identity
 */
function itemInformation(item) {	
    document.forms[0].actionTarget.value='information';     
    document.forms[0].selectedItem.value=item;
    document.forms[0].submit();
}

/**
 * Go to a specified step in the current wizard
 *
 * @param step step number
 */
function gotoStep(step) {
	document.forms[0].gotoStep.value=step;
	setActionTarget('gotoStep');
	document.forms[0].submit();	
}

/**
 * Show the AJAX confirmation page
 */
function showConfirm(path) {
	var body = document.getElementsByTagName('body')[0];
	var confirmContainer = document.createElement('div');
	confirmContainer.id = 'confirmContainer';
	confirmContainer.style.display = 'block';
	confirmContainer.style.position = 'absolute';
  	confirmContainer.hide();
	body.appendChild(confirmContainer);
	confirmContainer.innerHTML = loadXMLDoc(unescape(path));
	centerElement(confirmContainer);
  	confirmContainer.show();
}