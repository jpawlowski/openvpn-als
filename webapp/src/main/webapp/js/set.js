/*
 * This library contains general functions and initialisation
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

var ie = false;
var firefox = false;
var opera = false;
var ns = false;
var ns6 = false;
var displayInfoMessges = false;
var displayDebugMessges = true;

// Roughly detect the browser

if ((i = navigator.userAgent.indexOf('MSIE')) >= 0) {
   ie = true;
} else if ((i = navigator.userAgent.indexOf('Firefox')) >= 0) {
   firefox = true;
} else if ((i = navigator.userAgent.indexOf('Opera')) >= 0) {
   opera = true;
} else if(navigator.appName.indexOf("Netscape") >= 0 && ((_info.indexOf("Win") > 0 && _info.indexOf("Win16") < 0 && java.lang.System.getProperty("os.version").indexOf("3.5") < 0) || (_info.indexOf("Sun") > 0) || (_info.indexOf("Linux") > 0) || (_info.indexOf("AIX") > 0) || (_info.indexOf("OS/2") > 0) || (_info.indexOf("IRIX") > 0))) {
   ns = true;
   ns6 = ((_ns == true) && (_info.indexOf("Mozilla/5") >= 0));
}

function MSIE_Version()	{ 

	if (navigator.userAgent.indexOf("MSIE") == -1)   
		return 0  
	if (navigator.userAgent.indexOf("MSIE 2") > 0)     
		return 2    
	if (navigator.userAgent.indexOf("MSIE 3") > 0)     
		return 3
	if (navigator.userAgent.indexOf("MSIE 4") > 0)
		return 4
	if (navigator.userAgent.indexOf("MSIE 5") > 0) 
		return 5
	return 6;  
} 

function loadXMLDoc(url) {
	var xmlhttp = null;
	
	// code for Mozilla, etc.
	if (window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}
	// code for IE
	else if (window.ActiveXObject) {
		 xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	if (xmlhttp!=null) {	
		// xmlhttp.onreadystatechange  documentStateChange(elementId, xmlhttp);
		xmlhttp.open("GET",url,false);
		xmlhttp.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
		xmlhttp.send(null);
		if (xmlhttp.readyState==4) {
			if (xmlhttp.status==200) {
				return xmlhttp.responseText;
		  	} else {
				return "Failed to load document. This may be because your Adito session has timed out. " + xmlhttp.statusText;
		  	}
	  	}
	}
	return "Failed to load document" ;
	
}

function pollServer(url) {
	try
	{
		var xmlhttp = null;
		
		// code for Mozilla, etc.
		if (window.XMLHttpRequest) {
			xmlhttp=new XMLHttpRequest();
		}
		// code for IE
		else if (window.ActiveXObject) {
			 xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		if (xmlhttp!=null) {	
			xmlhttp.open("GET",url,false);
			xmlhttp.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
			xmlhttp.send(null);
			if (xmlhttp.readyState==4) {
				if (xmlhttp.status==200) {
					return true;
			  	} else {
					return false;
			  	}
		  	}
		}
	}
	catch(err)
	{
		// alert('errror = ' + err)
	}
	return false;
}

function infoMessage(message) {
	if(displayInfoMessages) {
		if(window.console) console.log(message);
	}
}

function debugMessage(message) {
	if(displayDebugMessges) {
		if(window.console) console.log(message);
	}
}