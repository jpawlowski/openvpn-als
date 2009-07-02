/*
 * toggle_control error fixed by Kjel Delaey (trimentor) - 22/06/2009
 * 
 * This library contains functions for managing 'Windows'. Windows are simply
 * <div>s that may be dragged around, minimized, closed, restored etc.
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
var comboBoxes;
var hideComboBoxesOnDrag = ie && MSIE_Version() < 7; // hide combo boxes upon drag. this is set by simple browser detection
var allComboBoxesHidden = false;
var dragging;
var hideAll = ie && MSIE_Version() < 7; // set to true to hide all combo boxes on dragging any window. Implies beNice
var beNice = false; // only recheck combo boxes on drag start / stop
var oldZIndex;
var intersectingId = "";
var separator;
var separatorBelow;
var fx = false;
var focussed;
var popupBlocked = false;
var tabSelected;
var currentTaskId = -1;

// Add a new method to array
Array.prototype.remove = function (s) {
	for (i = 0; i < this.length; i = i + 1) {
		if (s == this[i]) {
			this.splice(i, 1);
		}
	}
};

// Windows - TODO encapsulate these in an object
var managedWindows = new Array();
var windowGroups = new Object();

// Search for all of the combo boxes onload
if (hideComboBoxesOnDrag) {
	Event.observe(window, "load", function () {
		findAllComboBoxes();
	});
}

// Hide popups on document click
Event.observe(document, "click", function () {
	hidePopups();
}, true);

///////////////////////////////////////////////////////////////////////////
//
// PUBLIC API METHODS 
//
///////////////////////////////////////////////////////////////////////////

/**
 * Add a drop target. This default implementation does not do anything. 
 * Window managers that support dropping must implement it fully.
 *
 * @param element element or element id
 * 
 */
function addDropTarget(element) {
}

/**
 * Add an element as a managed windows. Managed windows are able to 
 * combo boxes when a layer moves over them. This is necessary to get 
 * around an issue in IE where <select> tags render over the top of all
 * layers
 *
 * @param element element id
 */
function addManagedWindow(element) {
	element = $(element);
	managedWindows.push(element);
	findAllComboBoxes();
	maybeCheckComboBoxes();
}

/**
 * Remove an element as a managed window. Managed windows are able to 
 * combo boxes when a layer moves over them. This is necessary to get 
 * around an issue in IE where <select> tags render over the top of all
 * layers
 *
 * @param element element id
 */
function removeManagedWindow(element) {
	element = $(element);
	managedWindows.remove(element);
	comboBoxes = document.getElementsByTagName("select");
	maybeCheckComboBoxes();
}

/**
 * Start dragging an element.
 *
 * @param event event object that started the drag
 * @param id id of element to drag
 */
function makeDraggable(event, id) {
	var control = document.getElementById(id);
	control.style.position = "relative";
	control.style.left = event.x;
	control.style.top = event.y;
	registerDrag(event, id);
}

/**
 * Start dragging an element.
 *
 * @param event event object that started the drag
 * @param id id of element to drag
 */
function registerDrag(event, id) {
	startDrag(event, id, false);
}

/**
 * Start dragging an element and allow it to be dropped into containers
 * that have the specified dropClass.
 *
 * @param event event object that started the drag
 * @param id id of element to drag
 */
function registerDragAndDrop(event, id) {
	startDrag(event, id, true);
}

/**
 * Toggle the visibility of a 'window' element by changing its style.visibility
 * attribute. When visibile the component will be placed just below the
 * second specified elemenet.
 *
 * If the window element covers a native control in IE (select elements),
 * the control will be  hidden until the window is closed or is dragged
 * out of the way.
 *
 * @param element element to toggle
 * @param positionAgainst element to place window below
 * @param popupGroup popup group
 */
function togglePopupBelow(element, positionAgainst) {
	toggleGroupedWindow(element, "popup", positionAgainst, "se");
}

/**
 * Toggle the visibility of a 'window' element by changing its style.visibility
 * attribute. When visibile the component will be placed just to the right of the specified element
 *
 * If the window element covers a native control in IE (select elements),
 * the control will be  hidden until the window is closed or is dragged
 * out of the way.
 *
 * @param element element to toggle
 * @param positionAgainst element to place window below
 * @param popupGroup popup group
 */
function togglePopupBelowLeft(element, positionAgainst) {
	toggleGroupedWindow(element, "popup", positionAgainst, "sw");
}

/**
 * Toggle the visibility of a 'window' element by changing its style.visibility
 * attribute. When visibile the component will be placed just to the right of the specified element
 *
 * If the window element covers a native control in IE (select elements),
 * the control will be  hidden until the window is closed or is dragged
 * out of the way.
 *
 * @param control element to toggle
 * @param positionAgainst element to place window below
 * @param popupGroup popup group
 */
function togglePopupBelowRight(element, positionAgainst) {
	toggleGroupedWindow(element, "popup", positionAgainst, "se");
}

/**
 * Toggle the visibility of a 'window' element by changing its style.visibility
 * attribute. 
 *
 * If the window element covers a native control in IE (select elements),
 * the control will be  hidden until the window is closed or is dragged
 * out of the way.
 *
 * @param control element to toggle
 */
function toggleWindow(element) {
	toggleGroupedWindow(element, "");
}

/** 
 * Toggle the visiblity of a popup window. No more than one popup window
 * is dislayed at a time. Any currently visible popups will be hidden when
 * another is made visible.
 *
 * @param control popup to  hide or display
 */
function togglePopup(element) {
	toggleGroupedWindow(element, "popup");
}

/** 
 * Hide the popup window. No more than one popup window
 * is dislayed at a time. Any currently visible popups will be hidden when
 * another is made visible.
 *
 * @param control popup to  hide or display
 */
function hidePopup(element) {
	hideGroupedWindow(element, "popup");
}

/**
 * Hide a window' element by changing its style.visibility
 * attribute. 
 *
 * @param control element to toggle
 * @param windowGroup group name of window
 */
function hideGroupedWindow(control, windowGroup) {
	hideControl(control);
	if (hideComboBoxesOnDrag) {
		managedWindows.remove(control);
		delete windowGroups[control.id];
		if (!isUndefined(comboBoxes) && comboBoxes.length > 0) {
			for (var i = 0; i < comboBoxes.length; i = i + 1) {
				comboBoxes[i].style.visibility = "visible";
			}
		}
	}
}

/**
 * Toggle the visibility of a 'window' element by changing its style.visibility
 * attribute. 
 *
 * If the window element covers a native control in IE (select elements),
 * the control will be  hidden until the window is closed or is dragged
 * out of the way.
 *
 * Any other windows with the same group name will be closed when this
 * window is opened
 *
 * @param control element to toggle
 * @param windowGroup group name of window
 */
function toggleGroupedWindow(control, windowGroup, positionAgainst, placement) {
	control = $(control);
	if (!isUndefined(control.style)) {
		if (control.visible()) {
			debugMessage("Hiding grouped window " + control.id + " in group " + windowGroup + " (" + control.style.display + ")");
			hideGroupedWindow(control, windowGroup);
			removeManagedWindow(control.id);
			return false;
		} else {
			debugMessage("Showing grouped window " + control.id + " in group " + windowGroup);
			// Hide other windows in same group	
			if (windowGroup != "") {
				for (j in windowGroups) {
					if (windowGroups[j] == windowGroup) {
						windowControl = document.getElementById(j);
						if (windowControl != control && windowControl.style.display != "none") {
							toggleGroupedWindow(windowControl, windowGroup);
						}
					}
				}
				windowGroups[control.id] = windowGroup;
			}
			
			// Make this window visible	
			if (positionAgainst) {
				positionAgainst=$(positionAgainst);
				debugMessage("Moving to " + placement + " against " + positionAgainst.id);
				/* We have to 'show' the component but keep it invisible so its width can be retrieved. The
				 * component is then hidden ready to be made visible again by the 
				 * effect
				 */
				control.style.visibility = "hidden";
				control.show();
				if (placement == "sw") {
					control.style.top = (getElementPositionY(positionAgainst) + getHeight(positionAgainst) + 4) + "px";
					control.style.left = (getElementPositionX(positionAgainst) - getWidth(control) + getWidth(positionAgainst) + 4) + "px";
				} else {
					if (placement == "se") {
						control.style.top = (getElementPositionY(positionAgainst) + getHeight(positionAgainst) + 4) + "px";
						control.style.left = (getElementPositionX(positionAgainst) + getWidth(positionAgainst) + 4) + "px";
					}
				}
				control.hide();
				control.style.visibility = "visible";
			}
			
			// Make the component appear
			if (fx) {
				new Effect.Appear(control, {duration:0.2});
			} else {
				control.show();
			}
			
			// Watch out for placing over <select> tags in IE
			addManagedWindow(control.id);
			
			return true;
		}
	}
}

/**
 * Set the selected tab in the currently displayed tab set. The selected tabs
 * contents will be made visible and the deselected tabs contents will be
 * hidden. The tab headings will also be adjusted accordingly.
 *
 * Any current open managed windows will also be hidden.
 *
 * @param selectTab name of tab to select
 * @param deselectTabs Array of tab names to deselect
 */
function setSelectedTab(selectTab, deselectTabs) {
	for (i = 0; i < deselectTabs.length; i = i + 1) {
		var tabPanel = $("tab_panel_" + deselectTabs[i]);
		if (!tabPanel) {
			alert("Could not find tab panel " + deselectTabs[i] + ". This may happen if the struts form provides a tab count that does not match the tab tags in the JSP file.");
			return;
		}
		$("tab_item_" + deselectTabs[i]).className = "deselectedTab";
		$("tab_link_" + deselectTabs[i]).className = "";
		tabPanel.className = "tabPanelHidden";
	}
	var tabPanel = $("tab_panel_" + selectTab);
	if (!tabPanel) {
		alert("Could not find tab panel " + selectTab + ". This may happen if the struts form provides a tab count that does not match the tab tags in the JSP file.");
		return;
	}
	$("tab_item_" + selectTab).className = "selectedTab";
	$("tab_link_" + selectTab).className = "currentTab";
	tabPanel.className = "tabPanel";
	tabSelected = selectTab;
	if(document.forms[0] && document.forms[0].selectedTab)
		document.forms[0].selectedTab.value = selectTab;
	hidePopups();
}

/**
 * Hide a control, using effect if enable
 */
function hideControl(control) {
	control = $(control);  	
	if (fx) {
		new Effect.Fade(control, {duration:0.2});
	} else {
		control.hide();
	}
}

/**
 * Hide all popups windows (e.g. popups)
 */
function hidePopups() {
	for (j in windowGroups) {
		if (windowGroups[j] == 'popup') {
			windowControl = document.getElementById(j);			
			if (windowControl.visible()) {
				toggleWindow(windowControl);
			}
		}
	}
}

/**
 * Find all combo boxes
 */
function findAllComboBoxes() {
	comboBoxes = document.getElementsByTagName("select");	
	for (var i = 0; i < comboBoxes.length; i = i + 1) {
		Element.extend(comboBoxes[i]);
	}
}

/** 
 * Execute a script in the global context. This installs all functions
 * defined in this script into the global scope, unless they are
 * explicitly created in different scopes.
 *
 * @param script the source of the JavaScript to evaluate
 */    
function installScript( script )
{
    if (!script)
        return;
    //  Internet Explorer has a funky execScript method that makes this easy
    if (window.execScript)
        window.execScript( script );
    else
        window.setTimeout( script, 0 );
}

///////////////////////////////////////////////////////////////////////////
//
// SUPPORTING METHODS
//
///////////////////////////////////////////////////////////////////////////
function startDrag(event, id, elementWillDrop) {
	// If we are still dragging something else then don't register the new drag
	if (dragging != null) {
		return;
	}
	
	// This default implementation doesn't support dropping 
	if(elementWillDrop) {
		return;
	}
	
    
    // Initialise
	control = $(id);
	if (!control) {
		alert("Could not find element to drag.");
	}
	oldZIndex = control.style.zIndex;
	originalPosition = control.style.position;
	
	// Get the element that fired the event and make sure its from the element we expect
	var eventElement = event.target;
	if (ie || opera) {
		eventElement = window.event.srcElement;
	} else {
		while (eventElement && eventElement.nodeType != eventElement.ELEMENT_NODE) {
			eventElement = eventElement.parentNode;
		}
	}
	if (!eventElement || eventElement.id.endsWith("_minimize") || eventElement.id.endsWith("_restore") || eventElement.id.endsWith("_close")) {
		// Dragging not support or tring to drag a frame control
		return;
	}
    
    //	Calculate real event
	var adjEventX;
	var adjEventY;
	var absoluteElement = false;

	// Make the element absolute	
	if (control.style.position != "absolute") {
		adjEventX = calcRealX(event.clientX);
		adjEventY = calcRealY(event.clientY);
		x = getElementPositionX(eventElement);
		y = getElementPositionY(eventElement) - (getEventY(event) - getElementPositionY(control)) - getOffsetY();
		control.style.position = "absolute";
		setX(control, x);
		setY(control, y);
	} else {
		adjEventX = event.clientX;
		adjEventY = event.clientY;
		absoluteElement = true;
	}
	
	// Fix the size for the duration of the drag	
	var originalWidth = control.style.width;
	var originalHeight = control.style.height;
	setWidth(control, getWidth(control));
	setHeight(control, getHeight(control));
		
	// Create the drable object
	var dragable = new Object();
	dragable.elNode = control;
	dragable.elNode.style.zIndex = 1000;
	dragable.cursorStartX = adjEventX;
	dragable.cursorStartY = adjEventY;
	dragable.elStartLeft = getX(control);
	dragable.elStartTop = getY(control);
	dragable.originalLeft = dragable.elStartLeft;
	dragable.originalTop = dragable.elStartTop;
	dragable.originalPosition = originalPosition;
	dragable.originalParent = control.parentNode;
	dragable.originalWidth = originalWidth;
	dragable.originalHeight = originalHeight;
	dragable.absoluteElement = absoluteElement;
	
	// Make the new object the current drag object
	dragging = dragable;
	
	// Register events
	if (ie) {
		document.attachEvent("onmousemove", enableDragging);
		document.attachEvent("onmouseup", stopDragging);
	} else {
		document.addEventListener("mousemove", enableDragging, true);
		document.addEventListener("mouseup", stopDragging, true);
	}   
    
    // Hide any <select> tags if required    
	if (hideComboBoxesOnDrag) {
		if (beNice || hideAll) {
			if (hideAll) {
				hideAllComboBoxes();
			} else {
				checkComboBoxes();
			}
		}
	}
	finishEvent(event);
}

/*
 * Invoked by event when mouse is released to stop dragging whatever
 * currently being dragged
 *
 * @param event mouse release event
 */
function stopDragging(event) {
	debugMessage("Stopping dragging " + dragging.elNode.id);
	if (ie) {
		document.detachEvent("onmousemove", enableDragging);
		document.detachEvent("onmouseup", stopDragging);
	} else {
		document.removeEventListener("mousemove", enableDragging, true);
		document.removeEventListener("mouseup", stopDragging, true);
	}
	x = dragging.elStartLeft + calcRealX(getEventX(event)) - dragging.cursorStartX;
	y = dragging.elStartTop + calcRealY(getEventY(event)) - dragging.cursorStartY;
	cv = x + "," + y;
	dragging.elNode.style.zIndex = oldZIndex;
	writeSessionCookie("frame_" + dragging.elNode.id + "_pos", cv);
	maybeCheckComboBoxes();
	dragging = null;
}

/**
 * Get a array of visible elements of a specified type
 *
 * @param parentElement parent element
 * @param tagName tag name
 */
function getVisibleElementsByTagName(parentElement, tagName) {
	var nels = new Array();
	for (i = 0; i < parentElement.childNodes.length; i++) {
		child = $(parentElement.childNodes[i]);
		if (child.nodeName.equalsIgnoreCase(tagName) && child.style.display != "none") {
			nels.push(child);
		}
	}
	return nels;
}

/**
 * Get a array of visible elements of a specified className
 *
 * @param parentElement parent element
 * @param elementClassName class name
 */
function getVisibleElementsByClassName(parentElement, elementClassName) {
	var nels = new Array();
	for (i = 0; i < parentElement.childNodes.length; i++) {
		child = $(parentElement.childNodes[i]);
		if (child.nodeType == child.ELEMENT_NODE && child.hasClassName(elementClassName) && child.style.display != "none") {
			nels.push(child);
		}
	}
	return nels;
}

/**
 * Center an absolutely positioned element in the middle of the
 * viewport.
 *
 * @param elelemt element
 */
function centerElement(element) {
	element = $(element);
	var rw = getWidth(element);
	var rh = getHeight(element);
	var vp = getViewportSize();
	setX(element, (vp.width - rw) / 2);
	setY(element, (vp.height - rh) / 2);
}

/*
 * Adjust an X coordinate taking into account any browser quirks
 *
 * @param x unadjusted x coordinate
 * @return adjusted x coordinate
 */
function calcRealX(x) {
	return getOffsetX() + x;
}

/*
 * Adjust a Y coordinate taking into account any browser quirks
 *
 * @param y unadjusted y coordinate
 * @return adjusted y coordinate
 */
function calcRealY(y) {
	return getOffsetY() + y;
}

/**
 * Get the Y offset of the current viewport from the top of the page
 *
 * @return y offset
 */
function getOffsetY() {
	if (ie) {
		return window.scrollY ? window.scrollY : 0;
	} else {
		return document.documentElement.scrollTop +document.body.scrollTop;
	}
}

/**
 * Get the X offset of the current viewport from the top of the page
 *
 * @return x offset
 */
function getOffsetX() {
	if (ie) {
		return window.scrollX ? window.scrollX : 0;
	} else {
		return document.documentElement.scrollLeft + document.body.scrollLeft;
	}
}

/**
 * Get the size of the current viewport as an Object
 * with two attributes, width and height.
 *
 * @return viewport size
 */
function getViewportSize() {
	var size = new Object();
	if (typeof window.innerWidth != "undefined") {
		size.width = window.innerWidth;
		size.height = window.innerHeight;
	} else {
		if (typeof document.documentElement != "undefined" && typeof document.documentElement.clientWidth != "undefined" && document.documentElement.clientWidth != 0) {
			size.width = document.documentElement.clientWidth;
			size.height = document.documentElement.clientHeight;
		} else {
			size.width = document.getElementsByTagName("body")[0].clientWidth;
			size.height = document.getElementsByTagName("body")[0].clientHeight;
		}
	}
	return size;
}

/*
 * Get the X cordinate of an element relative to its parent
 *
 * @param control element
 * @return relative x coordinate
 */
function getX(control) {
	if (isUndefined(control) || isUndefined(control.style)) {
		return 0;
	} else {
		var i = parseInt(control.style.left, 10);
		return isNaN(i) ? 0 : i;
	}
}

/*
 * Set the X cordinate of an element relative to its parent
 *
 * @param control element
 * @param relative x coordinate
 */
function setX(control, x) {
	if (isUndefined(control) || isUndefined(control.style)) {
		return;
	} else {
		control.style.left = x + "px";
	}
}

/*
 * Set the Y cordinate of an element relative to its parent
 *
 * @param control element
 * @param relative y coordinate
 */
function setY(control, y) {
	if (isUndefined(control) || isUndefined(control.style)) {
		return;
	} else {
		control.style.top = y + "px";
	}
}

/*
 * Get the Y cordinate of an element relative to its parent
 *
 * @param control element
 * @return relative y coordinate
 */
function getY(control) {
	if (isUndefined(control)) {
		return 0;
	} else {
		var i = parseInt(control.style.top, 10);
		return isNaN(i) ? 0 : i;
	}
}

/*
 * Get the width of an element
 *
 * @param control element
 * @return width
 */
function getWidth(control) {
	return control.offsetWidth;
}

/*
 * Set the width of an element
 *
 * @param control element
 * @param width
 */
function setWidth(control, width) {
	control.style.width = width + "px";
}

/*
 * Get the height of an element
 *
 * @param control element
 * @return height
 */
function getHeight(control) {
	return control.offsetHeight;
}

/*
 * Set the height of an element
 *
 * @param control element
 * @param height
 */
function setHeight(control, height) {
	control.style.height = height + "px";
}

/**
 * Consume drag event, ensuring no other components tries to 
 * 
 * @param event event
 */
function finishEvent(event) {
	if (ie) {
		window.event.cancelBubble = true;
		window.event.returnValue = false;
	} else {
		event.preventDefault();
	}
}

function getCoordinates(e) {
	if (document.all) {
		e = window.event;
	}
	ele = (document.all) ? e.srcElement : e.target;
	x = (document.all) ? e.offsetX : e.clientX;
	y = (document.all) ? e.offsetY : e.clientY;
	var result = new Object();
	result.x = x;
	result.y = y;
	return result;
}

/**
 * Get the X coordinate that an event occured at
 *
 * @param evt event
 * @return x event coordinate
 */
function getEventX(evt) {
	return evt.clientX;
}

/**
 * Get the Y coordinate that an event occured at
 *
 * @param evt event
 * @return y event coordinate
 */
function getEventY(evt) {
	return evt.clientY;
}

/**
  * Retrieve the coordinates of the given event relative to the center
  * of the widget.
  *
  * @param event
  *  A mouse-related DOM event.
  * @param reference
  *  A DOM element whose position we want to transform the mouse coordinates to.
  * @return
  *    A hash containing keys 'x' and 'y'.
  */
function getRelativeCoordinates(event, reference) {
	var x, y;
	event = event || window.event;
	var el = event.target || event.srcElement;
	if (!window.opera && typeof event.offsetX != "undefined") {
    // Use offset coordinates and find common offsetParent
		var pos = {x:event.offsetX, y:event.offsetY};
    // Send the coordinates upwards through the offsetParent chain.
		var e = el;
		while (e) {
			e.mouseX = pos.x;
			e.mouseY = pos.y;
			pos.x += e.offsetLeft;
			pos.y += e.offsetTop;
			e = e.offsetParent;
		}
    // Look for the coordinates starting from the reference element.
		var e = reference;
		var offset = {x:0, y:0};
		while (e) {
			if (typeof e.mouseX != "undefined") {
				x = e.mouseX - offset.x;
				y = e.mouseY - offset.y;
				break;
			}
			offset.x += e.offsetLeft;
			offset.y += e.offsetTop;
			e = e.offsetParent;
		}
    // Reset stored coordinates
		e = el;
		while (e) {
			e.mouseX = undefined;
			e.mouseY = undefined;
			e = e.offsetParent;
		}
	} else {
    // Use absolute coordinates
		var pos = getAbsolutePosition(reference);
		x = event.pageX - pos.x;
		y = event.pageY - pos.y;
	}
  // Subtract distance to middle
	return {x:x, y:y};
}

function getElementPositionX(control) {
	var offsetTrail = control;
	var offsetLeft = 0;
	while (offsetTrail) {
		offsetLeft += offsetTrail.offsetLeft;
		offsetTrail = offsetTrail.offsetParent;
	}
	if (navigator.userAgent.indexOf("Mac") != -1 && typeof document.body.leftMargin != "undefined") {
		offsetLeft += document.body.leftMargin;
	}
	return offsetLeft;
}

function getElementPositionY(control) {
	var offsetTrail = control;
	var offsetTop = 0;
	while (offsetTrail) {
		offsetTop += offsetTrail.offsetTop;
		offsetTrail = offsetTrail.offsetParent;
	}
	if (navigator.userAgent.indexOf("Mac") != -1 && typeof document.body.leftMargin != "undefined") {
		offsetTop += document.body.topMargin;
	}
	return offsetTop;
}

function enableDragging(event) {
	var realX = calcRealX(getEventX(event));
	var realY = calcRealY(getEventY(event));
	x = dragging.elStartLeft + realX - dragging.cursorStartX;
	y = dragging.elStartTop + realY - dragging.cursorStartY;
	dragging.elNode.style.left = x + "px";
	dragging.elNode.style.top = y + "px";
	if (hideComboBoxesOnDrag) {
		if (!allComboBoxesHidden) {
			if (!beNice) {
				checkComboBoxes();
			}
		}
	}
	finishEvent(event);
}

function hideAllComboBoxes() {
	for (var i = 0; i < comboBoxes.length; i = i + 1) {
		comboBoxes[i].style.visibility = "hidden";
	}
	allComboBoxesHidden = true;
}

function maybeCheckComboBoxes() {
	if (hideComboBoxesOnDrag) {
		checkComboBoxes();
	}
}

function lastElementInParent(element) {
	element = $(element);
	return element.parentNode.lastChild == element;
}

function showIntersecting(intersecting, below) {
	separator = $(document.createElement("div"));
	separator.className = "intersectingMarker";
	separator.setAttribute("style", "width: " + getWidth(intersecting) + "px;");
	if (below) {
		insertAfter(intersecting.parentNode, separator, intersecting);
	} else {
		intersecting.parentNode.insertBefore(separator, intersecting);
	}
}

function hideIntersecting(intersecting) {
	separator.remove();
}

function insertAfter(parent, node, referenceNode) {
	parent.insertBefore(node, referenceNode.nextSibling);
}

function elementIntersects(control1, control2) {
	if (!isUndefined(control1.style) && !isUndefined(control1.style.top)) {
		var cx = calcRealX(findX(control1));
		var cy = calcRealY(findY(control1));
		var cwidth = getWidth(control1);
		var cheight = getHeight(control1);
		var hidden = false;
		var kx = getX(control2);
		var ky = getY(control2);
		var kwidth = getWidth(control2);
		var kheight = getHeight(control2);
		return intersects(kx, ky, kwidth, kheight, cx, cy, cwidth, cheight);
	}
	return false;
}

function elementsOverlap(control1, control2) {
	if (!isUndefined(control1.style) && !isUndefined(control1.style.top)) {
		var cx = calcRealX(findX(control1));
		var cy = calcRealY(findY(control1));
		var cwidth = getWidth(control1);
		var cheight = getHeight(control1);
		var hidden = false;
		var kx = getX(control2);
		var ky = getY(control2);
		return kx >= cx & kx <= cx + cwidth && ky >= cy && ky <= cy + cheight;
	}
	return false;
}

function checkComboBoxes() {
//	alert('managed: ' + managedWindows.length  + ' for ' + comboBoxes.length + ' combos');
	if (managedWindows.length == 0) {
		for (var i = 0; i < comboBoxes.length; i = i + 1) {
			comboBoxes[i].style.visibility = "visible";
		}
	} else {
		for (var i = 0; i < comboBoxes.length; i = i + 1) {
			if (!isUndefined(comboBoxes[i].style) && !isUndefined(comboBoxes[i].style.top)) {
				var cx = calcRealX(findX(comboBoxes[i]));
				var cy = calcRealY(findY(comboBoxes[i]));
				var cwidth = getWidth(comboBoxes[i]);
				var cheight = getHeight(comboBoxes[i]);
				var hidden = false;
				for (var j = 0; !hidden && j < managedWindows.length; j = j + 1) {
					if (!isNull(managedWindows[j].style)) {
						var kx = getX(managedWindows[j]);
						var ky = getY(managedWindows[j]);
						var kwidth = getWidth(managedWindows[j]);
						var kheight = getHeight(managedWindows[j]);
						debugMessage('intersects(' + kx + ', ' + ky + ',' + kwidth + ',' + kheight + ',' + cx + ',' + cy + ',' + cwidth + ',' + cheight);
						if (intersects(kx, ky, kwidth, kheight, cx, cy, cwidth, cheight)) {
							debugMessage(comboBoxes[i].name + ' intersects ' + managedWindows[j].id + ' (' + comboBoxes[i].style.zIndex + ' / ' + managedWindows[j].style.zIndex + ')');
							if(!comboBoxes[i].descendantOf(managedWindows[j])) {
								if(!comboBoxes[i].style.zIndex || managedWindows[j].style.zIndex > comboBoxes[i].style.zIndex) {
									hidden = true;
									debugMessage('Hiding');
									if (isUndefined(comboBoxes[i].style) || comboBoxes[i].style.visibility != "hidden") {
										comboBoxes[i].style.visibility = "hidden";
									}
								}
							}
							else {
								debugMessage("Skipping, is decendant");
							}
						}
					}
				}
				if (!hidden && (isNull(comboBoxes[i].style) || comboBoxes[i].style.visibility != "visible")) {
					comboBoxes[i].style.visibility = "visible";
				}
			}
		}
	}
	allComboBoxesHidden = false;
}

function intersects(x1, y1, width1, height1, x2, y2, width2, height2) {
	if (width2 <= 0 || height2 <= 0 || width1 <= 0 || height1 <= 0) {
		return false;
	}
	width2 += x2;
	height2 += y2;
	width1 += x1;
	height1 += y1;
	return ((width2 < x2 || width2 > x1) && (height2 < y2 || height2 > y1) && (width1 < x1 || width1 > x2) && (height1 < y1 || height1 > y2));
}

function indexOf(element) {
	element = $(element);
	var children = element.parentNode.childNodes;
	for (var i = 0; i < children.length; i++) {
		if (children[i] == element) {
			return i;
		}
	}
	return -1;
}

function findX(control) {
	var x = 0;
	if (control.offsetParent) {
		while (control.offsetParent) {
			x += control.offsetLeft;
			control = control.offsetParent;
		}
	} else {
		if (control.x) {
			x += control.x;
		}
	}
	return x;
}

function findY(control) {
	var y = 0;
	if (control.offsetParent) {
		while (control.offsetParent) {
			y += control.offsetTop;
			control = control.offsetParent;
		}
	} else {
		if (control.y) {
			y += control.y;
		}
	}
	return y;
}

function opacity(id, opacStart, opacEnd, millisec) {
    //speed for each frame
	var speed = Math.round(millisec / 100);
	var timer = 0;

    //determine the direction for the blending, if start and end are the same nothing happens
	if (opacStart > opacEnd) {
		for (i = opacStart; i >= opacEnd; i--) {
			setTimeout("changeOpac(" + i + ",'" + id + "')", (timer * speed));
			timer++;
		}
		timer++;
	} else {
		if (opacStart < opacEnd) {
			for (i = opacStart; i <= opacEnd; i++) {
				setTimeout("changeOpac(" + i + ",'" + id + "')", (timer * speed));
				timer++;
			}
		}
	}
}

//change the opacity for different browsers
function changeOpac(opacity, id) {
	changeOpacStyle(opacity, $(id).style);
}

function changeOpacStyle(opacity, style) {
	style.opacity = (opacity / 100);
	style.MozOpacity = (opacity / 100);
	style.KhtmlOpacity = (opacity / 100);
	style.filter = "alpha(opacity=" + opacity + ")";
}

function shiftOpacity(id, millisec) {
    //if an element is invisible, make it visible, else make it ivisible
	if (document.getElementById(id).style.opacity == 0) {
		opacity(id, 0, 100, millisec);
	} else {
		opacity(id, 100, 0, millisec);
	}
}

function frameShow(frameId) {
	element = $(frameId);
	if (this.element) {
		$(frameId + "Content").style.display = "block";
		minimizeElement = $(frameId + "_minimize");
		if (this.minimizeElement) {
			minimizeElement.style.display = "inline";
		}
		restoreElement = $(frameId + "_minimize");
		if (this.restoreElement) {
			$(frameId + "_restore").style.display = "none";
		}
		closeElement = $(frameId + "_close");
		if (this.closeElement) {
			$(frameId + "_close").style.display = "inline";
		}
		if (fx) {
			new Effect.BlindDown(frameId, {duration:0.4, afterFinish:maybeCheckComboBoxes});
		} else {
			element.show();
		}
	}
	control = $(frameId + "_toggle");
	if (this.control) {
		control.checked = true;
	}
	writeSessionCookie("frame_" + frameId, "normal");
}

function frameMinimize(frameId) {
	if (fx) {
		new Effect.BlindUp(frameId + "Content", {duration:0.4, afterFinish:maybeCheckComboBoxes});
	} else {
		$(frameId + "Content").hide();
	}
	$(frameId + "_minimize").style.display = "none";
	$(frameId + "_restore").style.display = "inline";
	writeSessionCookie("frame_" + frameId, "minimized");
}

function frameRestore(frameId) {
	$(frameId + "_restore").style.display = "none";
	$(frameId + "_minimize").style.display = "inline";
	writeSessionCookie("frame_" + frameId, "normal");
	if (fx) {
		new Effect.BlindDown(frameId + "Content", {duration:0.4, afterFinish:maybeCheckComboBoxes});
	} else {
		$(frameId + "Content").show();
	}
}

function frameClose(frameId) {
	frame = $(frameId);
	writeSessionCookie("frame_" + frameId, "closed");
	if (frame) {
		var originalWidth = frame ? getWidth(frame) : 128;
		if (fx) {
			new Effect.BlindUp(frameId, {duration:0.4, afterFinish:maybeCheckComboBoxes});
		} else {
			$(frameId).hide();
		}
	}
	toggle_control = $(frameId + "_toggle");
	if(!isNull(toggle_control)){
		if (!isUndefined(toggle_control) && toggle_control.checked) {
			toggle_control.checked = false;
		}
	}	
}

function frameVisible(frameId) {
	var cookieVal = getCookieValue("frame_" + frameId);
	frame = $(frameId);
	if ((cookieVal && "closed" == cookieVal) || (!cookieVal && frame && !frame.visible())) {
		return false;
	}
	return true;
}

function frameToggle(frameId) {
	if (frameVisible(frameId)) {
		frameClose(frameId);
	} else {
		frameShow(frameId);
	}
}

updateTask = function(taskId) {
	currentTaskId = taskId;
	doUpdateTask();
}

doUpdateTask = function() {
	var url='/taskProgress.do?actionTarget=update&id=' + currentTaskId;
	new Ajax.Request(url, {
	  	method: 'get',
	  	onSuccess: function(transport) {
	  		var state = updateProgressBarsFromXML(transport.responseXML);
	  		if(!state) {
		  		setTimeout(doUpdateTask, 1500);
		  	}
		  	else {
		  		taskComplete(state);
		  	}
	  		return true;
	  	},
	  	onFailure: function(transport) {
	  		taskComplete();
		}
	}); 	
}

function delayExecution(millis) {
	var date = new Date();
	var curDate = null;
	do { curDate = new Date(); }
	while(curDate-date < millis);
} 

updateProgressBarsFromXML = function(responseXML) {
  	var xmlDoc=responseXML.documentElement;
   	var taskComplete = 'true' == xmlDoc.getElementsByTagName('taskComplete')[0].firstChild.nodeValue;
   	var progressBars = xmlDoc.getElementsByTagName('progressBar');   
   	for(var i = 0 ; i < progressBars.length; i++) {
   		var barId = progressBars[i].getElementsByTagName('progressBarId')[0].firstChild.nodeValue;
   		var min = progressBars[i].getElementsByTagName('progressBarMin')[0].firstChild.nodeValue;
   		var max = progressBars[i].getElementsByTagName('progressBarMax')[0].firstChild.nodeValue;
   		var val = progressBars[i].getElementsByTagName('progressBarValue')[0].firstChild.nodeValue;
   		var note = progressBars[i].getElementsByTagName('progressBarNote')[0].firstChild.nodeValue;
	 	var total = max - min;
	 	var percentage = Math.round( ( ( val - min )  / total ) * 100 )
	 	var cells = Math.round( ( percentage + 1 ) / 2 );
		for(var j = 0 ; j < 50 ; j++) {
			if(j < cells) {
				$('progress_' + barId + '_' + j).className = 'progressCellOn';
			}
			else {
				$('progress_' + barId + '_' + j).className = 'progressCellOff';
			}
		}
		$('progress_percentage_' + barId).update( percentage + '%');
		$('progress_note_' + barId).update(note);
	}
   	if(taskComplete) {
   		var onFinish = xmlDoc.getElementsByTagName('onFinish')[0].firstChild.nodeValue
   		if(onFinish) {
   			return onFinish;
   		}
   		else {
   			return '';
   		}
   	}
	return false;
}

taskComplete = function(gotoLoc) {
    window.status = 'Task Complete';
    Modalbox.hide();
    if(gotoLoc != '') {
    	x = unescape(gotoLoc);
    	window.location = x;
    }
}
