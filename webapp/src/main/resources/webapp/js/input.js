/*
 * This library contains functions that provide support for Adito's
 * extenteded input component tags such as 'Multi Entry', 'Multi Select', 
 * 'Color'.
 *
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
 
var colordelim = "000|003|006|009|00C|00F|030|033|036|039|03C|03F|060|063|066|069|06C|06F|090|093|096|099|09C|09F|0C0|0C3|0C6|0C9|0CC|0CF|0F0|0F3|0F6|0F9|0FC|0FF|300|303|306|309|30C|30F|330|333|336|339|33C|33F|360|363|366|369|36C|36F|390|393|396|399|39C|39F|3C0|3C3|3C6|3C9|3CC|3CF|3F0|3F3|3F6|3F9|3FC|3FF|600|603|606|609|60C|60F|630|633|636|639|63C|63F|660|663|666|669|66C|66F|690|693|696|699|69C|69F|6C0|6C3|6C6|6C9|6CC|6CF|6F0|6F3|6F6|6F9|6FC|6FF|900|903|906|909|90C|90F|930|933|936|939|93C|93F|960|963|966|969|96C|96F|990|993|996|999|99C|99F|9C0|9C3|9C6|9C9|9CC|9CF|9F0|9F3|9F6|9F9|9FC|9FF|C00|C03|C06|C09|C0C|C0F|C30|C33|C36|C39|C3C|C3F|C60|C63|C66|C69|C6C|C6F|C90|C93|C96|C99|C9C|C9F|CC0|CC3|CC6|CC9|CCC|CCF|CF0|CF3|CF6|CF9|CFC|CFF|F00|F03|F06|F09|F0C|F0F|F30|F33|F36|F39|F3C|F3F|F60|F63|F66|F69|F6C|F6F|F90|F93|F96|F99|F9C|F9F|FC0|FC3|FC6|FC9|FCC|FCF|FF0|FF3|FF6|FF9|FFC|FFF" ;
var hexvals = new Array( "0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F" ) ;
var colors = colordelim.split( "|" ) ;

function dismissGlobalWarning(messageKey, control) {
	control = $(control);
	var url='/ajaxDismissGlobalWarning.do?messageKey=' + messageKey;
	new Ajax.Request(url, {
	  	method: 'get',
	  	onSuccess: function(transport) {
	  		hideGlobalWarnings(control);
	  		return true;
	  	},
	  	onFailure: function(transport) {	
	  		hideGlobalWarnings(control);
		}
	}); 	
}

function hideGlobalWarnings(control) {
	hideControl(control);
	visibleGlobalWarnings--;
	if(visibleGlobalWarnings < 1) {
		hideControl('component_warnings');
		$('component_warnings').remove();
		
	}
}

function showChooser(chooserId) {
	chooserId.style.display = '';
}

function choose(chooserId, chooserFieldId, chosenColor) {
   
	var form = document.forms[0];
	var chooserField = form.elements[chooserFieldId];
	document.getElementById(chooserId).style.display = 'none';
	chooserField.value = chosenColor;
}

function buildChooser(chooserId, document, ncols, chooserFieldId) {
    document.writeln( "<table style=\"background: #EEEEEE; padding: 2px; border-width: 1px; border-color: #000000; border-style: solid;\"" ) ;

    for( var i = 0 ; i < colors.length ; i++ ) {
		if( (i % ncols) == 0 ) {
		    if(i > 0) {
		    document.write( "\n</tr>" ) ;
		    }
		    document.write( "\n<tr>" ) ;
		}
		var rgb = colors[i].split( "" ) ;
		var bgcol = "#"+rgb[0]+rgb[0]+rgb[1]+rgb[1]+rgb[2]+rgb[2] ;
		document.write( "<td style=\"width: 8px ; height: 8px; background: " + bgcol + "\" ; border-width: 1px; border-style: border-color: #000000\" onclick=\"choose('" + chooserId + "','" + 
				chooserFieldId + "','" + bgcol + "');\"/>") ;
    }
    document.writeln("</tr>\n<tr>");    
    var grayincr = 255.0 / (ncols-1) ;
    var grayval = 0.0 ;
    for( var i = 0 ; i < ncols ; i++ ) {
		var igray = Math.round( grayval ) ;
		var graystr = (igray < 16) ? "0"+hexvals[igray] :
			hexvals[Math.floor(igray/16)]+hexvals[igray%16] ;
		var bgcol = "#"+graystr+graystr+graystr ;
		document.write( "<td style=\"width: 8px ; height: 8px; background: " + bgcol + "\" ; border-width: 1px; border-style: border-color: #000000\" onclick=\"choose('" + chooserId + "','" + 
				chooserFieldId + "','" + bgcol + "');\"/>") ;
		grayval += grayincr ;
    }
    document.writeln("</tr>");
    document.writeln("</table>");
}

function set(action) {
	document.forms[0].action.value=action;
    return true;
}   
  
function setActionTarget(actionTarget) {
    return setFormActionTarget(actionTarget, document.forms[0]);
}   
  
function setFormActionTarget(actionTarget, actionForm) {
	actionForm.actionTarget.value=actionTarget;
    return true;
}   

function setSort(sortField) {
	document.forms[0].sortField.value=sortField;
    return true;
}   
                           
function setActionTargetAndAction(actionTarget, action) {
    document.forms[0].actionTarget.value=actionTarget;     
    document.forms[0].action=action;
    return true;
}  

function setActionTargetAndFileName(actionTarget, fileName) {
    document.forms[0].actionTarget.value=actionTarget;     
    document.forms[0].fileName=fileName;
    return true;
} 

function multiSelectRebuildEntryField(fieldControl, targetControl) {
	var v = '';
	for(i = 0 ; i < targetControl.length; i++) {
		if(v.length > 0) {
			v += '\n';
		}
		v += targetControl.options[i].value;
	}
	fieldControl.value = v;
}	

function multiSelectAddOption(theSel, theText, theValue) {
	var newOpt = new Option(theText, theValue);
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
}

function multiSelectDeleteOption(theSel, theIndex)	{ 
	var selLength = theSel.length;
	if(selLength > 0) {
		theSel.options[theIndex] = null;
	}
}

function multiSelectSelectValue(fieldControl, sourceControl, targetControl) {
	multiSelectMoveValue(sourceControl, targetControl);
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiSelectAllSelectValue(fieldControl, sourceControl, targetControl) {
	multiSelectMoveAll(sourceControl, targetControl);
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiSelectAllDeselectValue(fieldControl, sourceControl, targetControl) {
	multiSelectMoveAll(targetControl, sourceControl);
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiSelectDeselectValue(fieldControl, sourceControl, targetControl) {
	multiSelectMoveValue(targetControl, sourceControl);
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiMoveUp(fieldControl, targetControl) {
	var selLength = targetControl.length;
	for(i=selLength-1; i >= 1; i--) {
		if(targetControl.options[i].selected) {
			var selText = targetControl.options[i - 1].text;
			var selValue = targetControl.options[i - 1].value;
			targetControl.options[i - 1].text =  targetControl.options[i].text
			targetControl.options[i - 1].value =  targetControl.options[i].value
			targetControl.options[i].text =  selText
			targetControl.options[i].value =  selValue;
			targetControl.selectedIndex  = i - 1;
			i = 0;
		}
	}
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiMoveDown(fieldControl, targetControl) {
	var selLength = targetControl.length;
	for(i=selLength - 2; i >= 0; i--) {
		if(targetControl.options[i].selected) {
			var selText = targetControl.options[i + 1].text;
			var selValue = targetControl.options[i + 1].value;
			targetControl.options[i + 1].text =  targetControl.options[i].text
			targetControl.options[i + 1].value =  targetControl.options[i].value
			targetControl.options[i].text =  selText
			targetControl.options[i].value =  selValue;
			targetControl.selectedIndex  = i + 1;
			i = 0;
		}
	}
	multiSelectRebuildEntryField(fieldControl, targetControl);
}

function multiSelectMoveValue(moveFrom, moveTo) {
	var selLength = moveFrom.length;
	var selectedText = new Array();
	var selectedValues = new Array();
	var selectedCount = 0;  
	var i;
	for(i=selLength-1; i >= 0; i--) {
		if(moveFrom.options[i].selected) {
			selectedText[selectedCount] = moveFrom.options[i].text;
			selectedValues[selectedCount] = moveFrom.options[i].value;
			multiSelectDeleteOption(moveFrom, i);
			selectedCount++;
		}
	}
	for(i=selectedCount-1; i >=0; i--) {
		multiSelectAddOption(moveTo, selectedText[i], selectedValues[i]);
	}
}

function multiSelectMoveAll(moveFrom, moveTo) {
	var selLength = moveFrom.length;
	var selectedText = new Array();
	var selectedValues = new Array();
	var selectedCount = 0;  
	var i;
	for(i=selLength-1; i >= 0; i--) {
		selectedText[selectedCount] = moveFrom.options[i].text;
		selectedValues[selectedCount] = moveFrom.options[i].value;
		multiSelectDeleteOption(moveFrom, i);
		selectedCount++;
	}
	for(i=selectedCount-1; i >=0; i--) {
		multiSelectAddOption(moveTo, selectedText[i], selectedValues[i]);
	}
}

function multiEntryRebuildEntryField(fieldControl, targetControl) {
	var v = '';
	for(i = 0 ; i < targetControl.length; i++) {
		if(v.length > 0) {
			v += '\n';
		}
		v += targetControl.options[i].value;
	}
	fieldControl.value = v;
}

function multiEntryAddEntry(fieldControl, targetControl, entryControl, targetUnique) {
	if(entryControl.value != null && entryControl.value.replace(/^\s+|\s+$/, '') != '') {
		if(targetUnique == 'true') {
			found = false;
			for(i = 0 ; i < targetControl.options.length && !found; i++) {
				found = targetControl.options[i].value == entryControl.value;
			}
			if(found) {			
				return;
			}
		}
		multiEntryAddOption(targetControl, entryControl.value, entryControl.value);
		entryControl.value = '';
		multiEntryRebuildEntryField(fieldControl, targetControl);
	}
}

function multiEntryRemoveSelectedEntry(fieldControl, targetControl, entryControl) { 	
	var selLength = targetControl.length;
	for(i=selLength-1; i >= 0; i--) {
		if(targetControl.options[i].selected) {
		    entryControl.value = targetControl.options[i].value;
			targetControl.options[i] = null;
		}
	}
	multiEntryRebuildEntryField(fieldControl, targetControl);
}


function multiEntryAddOption(theSel, theText, theValue) {
	var newOpt = new Option(theText, theValue);
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
}

function setCheckedValue(radioObj, newValue) {
	if(!radioObj)
		return;
	var radioLength = radioObj.length;
	if(radioLength == undefined) {
		radioObj.checked = (radioObj.value == newValue.toString());
		return;
	}
	for(var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if(radioObj[i].value == newValue.toString()) {
			radioObj[i].checked = true;
		}
	}
}   

function toolTipDynamic(contentLocation) {
	return escape(loadXMLDoc(contentLocation));
}

function toolTipStatic(content) {
	return escape(content);
} 

function CaretPosition() {
	var start = null;
	var end = null;
}

function getCaretPosition(oField) {
	// Initialise the CaretPosition object
	var oCaretPos = new CaretPosition();

	// IE support
	if(document.selection) {
		// Focus on the text box
		oField.focus();

		// This returns us an object containing
		// information about the currently selected text
		var oSel = document.selection.createRange();

		// Find out the length of the selected text
		// (you'll see why below)
		var selectionLength = oSel.text.length;

		// Move the selection start to 0 position.
		//
		// This is where it gets interesting, and this is why
		// some have claimed you can't get the caret positions
		// in IE.
		//
		// IE has no 'selectionStart' or 'selectionEnd' property,
		// so we can not get or set this value directly. We can
		// only move the caret positions relative to where they
		// currently are (this should make more sense when you read
		// the next line of code).
		//
		// Note, that even though we have moved the start
		// position on our object in memory, this is not reflected
		// in the browser until we call oSel.select() (which we're
		// not going to do here).
		//
		// Also note, the start position will never be a negative
		// number, no matter how far we try to move it back.
		oSel.moveStart ('character', -oField.value.length);

		// This is where it should start to make sense. We now know
		// our start caret position is the length of the currently
		// selected text minus the original selection length
		// (think about it).
		oCaretPos.start = oSel.text.length - selectionLength;

		// Since the start of the selection is at the start of the
		// text, we know that the length of the selection is also
		// the index of the end caret position.
		oCaretPos.end = oSel.text.length;
	}
	// Firefox support
	else if(oField.selectionStart || oField.selectionStart == '0') {
		// This is a whole lot easier in Firefox
		oCaretPos.start = oField.selectionStart;
		oCaretPos.end = oField.selectionEnd;
	}

	// Return results
	return (oCaretPos);
}

function setCaretPosition(oField, iCaretStart, iCaretEnd) {
	// IE Support
	if (document.selection) {
		// Focus on the text box
		oField.focus();

		// This returns us an object containing
		// information about the currently selected text
		var oSel = document.selection.createRange();

		// Since we don't know where the caret positions
		// currently are (see comments in getCaretPosition() for
		// further information on this), move them to position 0.
		//
		// Note, the caret positions will never be a negative
		// number, no matter how far we try to move them back.
		oSel.moveStart ('character', -oField.value.length);
		oSel.moveEnd ('character', -oField.value.length);

		// Now we know the caret positions are at index 0, move
		// them forward to the desired position (move end caret
		// position first - actually not sure if moving start
		// caret position first affects the end caret position -
		// it might).
		//
		// Note, we allow for a null end caret position and just
		// default it to the same as the start caret position.
		if(iCaretEnd != null)
			oSel.moveEnd ('character', iCaretEnd);
		else
			oSel.moveEnd ('character', iCaretStart);

		oSel.moveStart ('character', iCaretStart);

		// Everything thus far has just been on our object in
		// memory - this line actually updates the browser
		oSel.select();
	}
	// Firefox support
	else if(oField.selectionStart || oField.selectionStart == '0') {
		oField.selectionStart = iCaretStart;

		if(iCaretEnd != null)
			oField.selectionEnd = iCaretEnd;
		else
			oField.selectionEnd = iCaretStart;
	
		oField.focus();
	}
}
