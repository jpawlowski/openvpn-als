/*
 * This library contains functions for dealing with Adito
 * resources.
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
 * Set the currently selected resource view to 'List'. 
 * @param submittingForm the containing form
 */
function viewAsListMultiForm(submittingForm) {	
	setFormActionTarget('viewList', submittingForm);
    submittingForm.submit();
    return true;
} 

/**
 * Set the currently selected resource view to 'Icons'.
 * @param submittingForm the containing form
 */
function viewAsIconsMultiForm(submittingForm) {	
	setFormActionTarget('viewIcons', submittingForm);
    submittingForm.submit();
    return true;
}

/**
 * Policy selected
 *
 * @param policyId policy id
 * @param policyName policyName
 */
function policySelected(policyId, policyName) {
	policyPopupWin.close();
	var newOpt = new Option(policyName, policyId + '');
	var selLength = policyPopupTargetControl.length;
	policyPopupTargetControl.options[selLength] = newOpt;
	multiEntryRebuildEntryField(policyPopupFieldControl, policyPopupTargetControl);
}

/**
 * Add a policy
 *
 * @param fieldControl policy field
 * @param targetControl target control
 * @param entryControl entry control
 */
function addPolicy(fieldControl, targetControl, entryControl) {
	policyPopupFieldControl = fieldControl;
	policyPopupTargetControl = targetControl;
	policyPopupEntryControl = entryControl;
	policyPopupWin = window.open('policyPopup.do','policy_popup','left=20,top=20,width=420,height=420,toolbar=0,resizable=1,menubar=0,scrollbars=1,location=0');
	policyPopupWin.focus();
}
			
/**
 * Set the selected category 
 *
 * @param category category
 */           
function setCategory(category) {
    document.forms[0].newSelectedCategory.value=category;     
    document.forms[0].actionTarget.value='changeSelectedCategory';     
    return document.forms[0].submit();
}       