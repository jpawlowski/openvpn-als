//Javascript name: My Date Time Picker
//Date created: 16-Nov-2003 23:19
//Scripter: TengYong Ng
//Website: http://www.rainforestnet.com
//Copyright (c) 2003 TengYong Ng
//FileName: DateTimePicker.js
//Version: 0.8
//Contact: contact@rainforestnet.com
// Note: Permission given to use this script in ANY kind of applications if
//       header lines are left unchanged.
//
//
// Heavily modified for use by 3SP (http://3sp.com). No longer runs in 
// a new popup, instead it replaces a DOM element. All styling
// has been moved to CSS and a number of bugs fixed.
// 
// A large amount of refactoring and renaming has also taken place


//Global variables
var Cal;
var calendarWindow;
var monthName=["January", "February", "March", "April", "May", "June","July", 
	"August", "September", "October", "November", "December"];
var weekDayName=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];	

//Configurable parameters
var WeekChar=3;//number of character for week day. if 2 then Mo,Tu,We. if 3 then Mon,Tue,Wed.


/**
 * Toggle the calendar.
 *
 * @param pickerElement ID or element to populate with date picker HTML (required)
 * @param dateInputField ID or element of text field containing date text (required)
 * @param format format of date (required)
 * @param showTime show time picker
 * @param timeMode 12 or 24 for time picker
 * @param showDate show the date picker
 * @param showMonthYear show the month / year header
 * @param showLongMonth show long month name in calendar header
 * @param showMonthYearPicker show the month year picker
 * @param showSeconds show seconds
 * @param dateSeparator date separator character
 */ 
function toggleCalendar(pickerElement, dateInputField, format, showTime, timeMode, showDate, showMonthYear, showLongMonth, showMonthYearPicker, showSeconds, dateSeparator, plusImage, minusImage, closeImage)
{
	// Hide the previous calendar
	if(calendarWindow != null) {
		hideCalendar();
		return;
	}
	
	// Sanitise options
	
	pickerElement=$(pickerElement);
	dateInputField=$(dateInputField);
	
	// Create the 'Calendar object that is used to store everything about the
	// current date time picker
	Cal=new Calendar(new Date());
	Cal.Ctrl=dateInputField;
	Cal.Format=format.toUpperCase();
	Cal.ShowDate=showDate == null || showDate;
	Cal.ShowLongMonth=showLongMonth == null || showLongMonth;
	Cal.ShowMonthYear=showMonthYear == null || showMonthYear;
	Cal.ShowMonthYearPicker=showMonthYearPicker == null || showMonthYearPicker;
	Cal.ShowTime=showTime == null || showTime;
	Cal.TimeMode=timeMode == null ? 12 : timeMode;
	Cal.ShowSeconds=showSeconds == null || showSeconds;
	Cal.DateSeparator=dateSeparator == null ? '/' : dateSeparator;
	Cal.PlusImage=plusImage;
	Cal.MinusImage=minusImage;
	Cal.CloseImage=closeImage;
	
	// Parse the current date
	if (dateInputField.value != "") {
		parseCalendarDateString(dateInputField.value, Cal);		
	}
	
	// Display window and render the contents
	
	calendarWindow=pickerElement;	
	renderCalendar();
	toggleGroupedWindow(calendarWindow, "datePickers", dateInputField, "sw");
}

/**
 * Parse a date string
 *
 * @param dateString
 * @param calendarObject calendar object to store parsed values in
 */
function parseCalendarDateString(dateString, calendarObject) {
	var Sp1;//Index of Date Separator 1
	var Sp2;//Index of Date Separator 2 
	var tSp1;//Index of Time Separator 1
	var tSp1;//Index of Time Separator 2
	var strMonth;
	var strDate;
	var strYear;
	var intMonth;
	var YearPattern;
	var strHour;
	var strMinute;
	var strSecond;
	
	//parse month
	Sp1=dateString.indexOf(Cal.DateSeparator,0)
	Sp2=dateString.indexOf(Cal.DateSeparator,(parseInt(Sp1)+1));
		
	if(Cal.ShowDate) {
		if ( calendarObject.Format.toUpperCase()=="DDMMYYYY" || 
			 calendarObject.Format.toUpperCase()=="DDMMMYYYY" ||
			 calendarObject.Format.toUpperCase()=="DDMMYY" ||
			 calendarObject.Format.toUpperCase()=="DDMMMYY" ) {
			strMonth=dateString.substring(Sp1+1,Sp2);
			strDate=dateString.substring(0,Sp1);
		}
		else if ( calendarObject.Format.toUpperCase()=="MMDDYYYY" || 
			   calendarObject.Format.toUpperCase()=="MMMDDYYYY" || 
			   calendarObject.Format.toUpperCase()=="MMDDYY" ||
			   calendarObject.Format.toUpperCase()=="MMMDDYY") {
			strMonth=dateString.substring(0,Sp1);
			strDate=dateString.substring(Sp1+1,Sp2);
		}
		if (isNaN(strMonth))
			intMonth=calendarObject.GetMonthIndex(strMonth);
		else
			intMonth=parseInt(strMonth,10)-1;	
		if ((parseInt(intMonth,10)>=0) && (parseInt(intMonth,10)<12))
			calendarObject.Month=intMonth;
		
		//parse Date
		if ((parseInt(strDate,10)<=calendarObject.GetMonDays()) && (parseInt(strDate,10)>=1))
			calendarObject.Date=strDate;
	
		//parse year
		if(calendarObject.Format.toUpperCase().indexOf("YYYY") != -1) {
			YearPattern=/^\d{4}$/;
			strYear=dateString.substring(Sp2+1,Sp2+5);
			if (YearPattern.test(strYear)) {
				calendarObject.Year=parseInt(strYear,10);
			}
		}
		else {
			YearPattern=/^\d{2}$/;
			strYear=dateString.substring(Sp2+1,Sp2+3);
			if (YearPattern.test(strYear)) {
				calendarObject.Year=parseInt( ( Math.round(new Date().getFullYear() / 100) ) + strYear, 10 );
			}
		}
		
	}

	//parse time
	if (calendarObject.ShowTime==true) {
		tSp1=dateString.indexOf(":",0)
		tSp2=-1;
		if(Cal.ShowSeconds) {
			tSp2=dateString.indexOf(":",(parseInt(tSp1)+1));
		}
		if(tSp2==-1) {
			tSp2=dateString.length;
		}
		strHour=dateString.substring(tSp1,(tSp1)-2);
		calendarObject.SetHour(strHour);
		strMinute=dateString.substring(tSp1+1,tSp2);
		calendarObject.SetMinute(strMinute);
		if(Cal.ShowSeconds) {
			strSecond=dateString.substring(tSp2+1,tSp2+3);
			calendarObject.SetSecond(strSecond);
		}
		else {
			calendarObject.SetSecond('00');
		}
	}	
}

/**
 * Render calendar HTML
 */
function renderCalendar() {
	var vCalHeader;
	var vCalData;
	var vCalTime;
	var i;
	var j;
	var SelectStr;
	var vDayCount=0;
	var vFirstDay;
	
	var nodeText = "<div class=\"component_dateTimePicker\"><form name='Calendar'>";

	nodeText+="<table class=\"dateTimeView\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width='100%' align=\"center\" valign=\"top\">\n";
	
	if(Cal.ShowMonthYearPicker) {
		//Month Selector
		nodeText+="<thead><tr>\n<td colspan=\"7\">";
		nodeText+="<div class=\"monthMenu\"><select name=\"MonthSelector\" onChange=\"javascript:Cal.SwitchMth(this.selectedIndex);renderCalendar();\">\n";
		for (i=0;i<12;i++)
		{
			if (i==Cal.Month)
				SelectStr="Selected";
			else
				SelectStr="";	
			nodeText+="<option "+SelectStr+" value >"+monthName[i]+"\n";
		}
		nodeText+="</select></div>";
		
		//Year selector
		nodeText+="\n<div class=\"year\">";
		if(Cal.MinusImage == null) {
			nodeText += "<a class=\"down\" href=\"javascript:Cal.DecYear();renderCalendar()\">&lt;&lt;</a>"
		}
		else {
			nodeText += "<a href=\"javascript:Cal.DecYear();renderCalendar()\"><img class=\"down\" src=\"" + Cal.MinusImage + "\"/></a>"
		}
		nodeText +="<span class=\"yearNumber\">"+Cal.Year + "</span>";
		if(Cal.PlusImage == null) {
			nodeText += "<a class=\"up\" href=\"javascript:Cal.IncYear();renderCalendar()\">&gt;&gt;</a>"
		}
		else {
			nodeText += "<a href=\"javascript:Cal.IncYear();renderCalendar()\"><img class=\"up\" src=\"" + Cal.PlusImage + "\"/></a>"
		}
		nodeText +="</div>";
		
		// Toolbar
		nodeText+="<div class=\"toolbar\">";
		if(Cal.CloseImage == null) {
			nodeText+="<a href=\"javascript: hideCalendar();\">X</a>";
		}
		else {
			nodeText+="<a href=\"javascript: hideCalendar();\"><img src=\"" + Cal.CloseImage + "\"/></a>";
		}
		nodeText+="</div>";
		
		
		// End month year picker
		nodeText+="</td></tr></thead>";
	}
	
	nodeText+="<tbody>";
	
	//Calendar header shows Month and Year
	if (Cal.ShowMonthYear)
		nodeText+="<tr class=\"monthYear\"><td colspan=\"7\">"+Cal.GetMonthName(Cal.ShowLongMonth)+" "+Cal.Year+"</td></tr>\n";
		
	//Week day header
	if(Cal.ShowDate) {
		nodeText+="<tr  class=\"weekHeader\">";
		for (i=0;i<7;i++)
		{
			nodeText+="<td class=\"day" + weekDayName[i] + "\">"+weekDayName[i].substr(0,WeekChar)+"</td>";
		}
		nodeText+="</tr>";	
		
		//Calendar detail
		CalDate=new Date(Cal.Year,Cal.Month);
		CalDate.setDate(1);
		vFirstDay=CalDate.getDay();
		vCalData="<tr class=\"week\">";
		for (i=0;i<vFirstDay;i++) {
			vCalData=vCalData+generateDayCell();
			vDayCount=vDayCount+1;
		}
		var dtToday = new Date();
		for (j=1;j<=Cal.GetMonDays();j++) {
			var strCell;
			vDayCount=vDayCount+1;
			selected = j==Cal.Date;
			if ((j==dtToday.getDate())&&(Cal.Month==dtToday.getMonth())&&(Cal.Year==dtToday.getFullYear())) {		
				strCell=generateDayCell(j,true,'today', selected);//Highlight today's date
			}
			else {	 
				if (vDayCount%7==0)
					strCell=generateDayCell(j,false,'saturday', selected);
				else if ((vDayCount+6)%7==0)
					strCell=generateDayCell(j,false,'sunday', selected);
				else
					strCell=generateDayCell(j,null,'weekday', selected);
			}						
			vCalData=vCalData+strCell;
	
			if((vDayCount%7==0)&&(j<Cal.GetMonDays()))
			{
				vCalData=vCalData+"</tr>\n<tr class=\"week\">";
			}
		}
		nodeText = nodeText + vCalData + "</tr></tbody>";	
	}
	else {
		nodeText = nodeText + '</tbody>';
	}
	
	//Time picker
	if (Cal.ShowTime) {
		var showHour;
		showHour=Cal.getShowHour();		
		vCalTime="<tfoot><tr class=\"timePicker\">\n<td colspan=\"7\">";
		vCalTime+="<div class=\"timePickerBlock\">";
		vCalTime+="<input id=\"datePickerHH\" onclick=\"this.focus(); this.select();\" type='text' ";
		vCalTime+="name='hour' maxlength=2 size=1 value="+showHour;
		vCalTime+=" onchange=\"javascript:";
		vCalTime+="if(!Cal.SetHour(Cal.FormatDayOrMonthNumber(this.value))) { event.cancelBubble = true } \">";
		vCalTime+=" : ";
		vCalTime+="<input onclick=\"this.focus(); this.select();\" ";
		vCalTime+="type='text' name='minute' maxlength=2 size=1 ";
		vCalTime+="value="+Cal.Minutes+" onchange=\"javascript:";
		vCalTime+="if(!Cal.SetMinute(Cal.FormatDayOrMonthNumber(this.value))) { event.cancelBubble = true } \">";
		if(Cal.ShowSeconds) {
			vCalTime+=" : ";
			vCalTime+="<input onclick=\"this.focus(); this.select();\" ";
			vCalTime+="type='text' name='second' maxlength=2 size=1 ";
			vCalTime+="value="+Cal.Seconds+" onchange=\"javascript:";
			vCalTime+="if(!Cal.SetSecond(Cal.FormatDayOrMonthNumber(this.value))) { event.cancelBubble = true } \">";
		}
		if (Cal.TimeMode==12) {
			var SelectAm =(parseInt(Cal.Hours,10)<12)? "Selected":"";
			var SelectPm =(parseInt(Cal.Hours,10)>=12)? "Selected":"";

			vCalTime+="<select name=\"ampm\" onchange=\"javascript:Cal.SetAmPm(this.options[this.selectedIndex].value);\">";
			vCalTime+="<option "+SelectAm+" value=\"AM\">AM</option>";
			vCalTime+="<option "+SelectPm+" value=\"PM\">PM<option>";
			vCalTime+="</select>";
		}			
		vCalTime+="<a onclick=\"javascript:$('"+Cal.Ctrl.id+"').value=Cal.FormatAsString(); hideCalendar(); return false\">Ok</a>";
		vCalTime+="</div></td></tr></tfoot>";
		vCalTime += "<script type=\"text/javascript\">";
		vCalTime += "$('datePickerHH').focus();";
		vCalTime += "$('datePickerHH').select();";
		vCalTime += "</script>";
		nodeText += vCalTime;
	}
	
	nodeText += "</table>";
	nodeText += "</form></div>";
	
	/* We cannot just use Element.replace() because IE does not like it
	 * so a new element must be created, appended to the actual element
	 * and its text replaced
	 */
	var newDiv = document.createElement('div');
	Element.extend(newDiv);
	newDiv.innerHTML = nodeText;
	if ( calendarWindow.hasChildNodes() ){
	    while ( calendarWindow.childNodes.length >= 1 ) {
			calendarWindow.removeChild( calendarWindow.firstChild );       
	    } 
    }
	calendarWindow.appendChild(newDiv);
}

/**
 * Render the HTML for a single day cell in the calendar date picker
 *
 * @param date date
 * @param highlight highlight the date
 * @param cellStyle cell style
 */
function generateDayCell(date,highLight,cellStyle,selected){
	// Generate script to select date
	var script = '';	
	if(date != null) {
		script += "Cal.Date=" + date;
		script += ";$('"+Cal.Ctrl.id+"').value=Cal.FormatAsString();";
	}
	script += "if(!Cal.ShowTime) { hideCalendar(); } else { renderCalendar(); }; return false";
	
	var buf="<td width=\"14%\" onclick=\"" + script + "\">";
	buf += "<a onclick=\"" + script + "\">";
	if(date != null) {
		buf += "<span class=\"" + cellStyle + "\" >";
		buf += "<span " + ( highLight ? "class=\"highlight\"" : "") + ">";
		if(selected) {
			buf += "<span class=\"selected\">";
		}
		buf +=date;
		if(selected) {
			buf += "</span>";
		}
		buf += "</span></span>";
	}
	buf += "</a></td>";
	return buf;
}

/**
 * Hide the currently visible calendar
 */
function hideCalendar() {
	hideGroupedWindow(calendarWindow, "datePickers");
	removeManagedWindow(calendarWindow);
	calendarWindow = null;
}

/////////////////////////////////////////////////////////////////////////
// Supporting classes
/////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////
// Calendar
//
// Used to hold all attributes of the current date picker component
// including the current date and time and any optios provided when
// the calendar was created (such as whether to show the time 
// component, the time mode etc).
//
// A number of methods are also available on this object to be used
// for manipulating and accessing the attributes
//////////////////////////////////////////////////////////////////////////

/**
 * Constructor
 *
 * @param pDate 'Date' object used to initialis the calendar. This will
 *              be the initial date displayed
 */
function Calendar(pDate)
{
	//Properties
	this.Date=pDate.getDate();//selected date
	this.Month=pDate.getMonth();//selected month number
	this.Year=pDate.getFullYear();//selected year in 4 digits
	this.Hours=pDate.getHours();	
	
	if (pDate.getMinutes()<10)
		this.Minutes="0"+pDate.getMinutes();
	else
		this.Minutes=pDate.getMinutes();
	
	if (pDate.getSeconds()<10)
		this.Seconds="0"+pDate.getSeconds();
	else		
		this.Seconds=pDate.getSeconds();
		
	this.Format="ddMMyyyy";
	this.ShowTime=false;
	if (pDate.getHours()<12)
		this.AMorPM="AM";
	else
		this.AMorPM="PM";	
}

function GetMonthIndex(shortMonthName)
{
	for (i=0;i<12;i++)
	{
		if (monthName[i].substring(0,3).toUpperCase()==shortMonthName.toUpperCase())
		{	return i;}
	}
}
Calendar.prototype.GetMonthIndex=GetMonthIndex;

function IncYear()
{	Cal.Year++;}
Calendar.prototype.IncYear=IncYear;

function DecYear()
{	Cal.Year--;}
Calendar.prototype.DecYear=DecYear;
	
function SwitchMth(intMth)
{	Cal.Month=intMth;}
Calendar.prototype.SwitchMth=SwitchMth;

function SetHour(intHour)
{	
	var MaxHour;
	var MinHour;
	if (Cal.TimeMode==24)
	{	MaxHour=23;MinHour=0}
	else if (Cal.TimeMode==12)
	{	MaxHour=12;MinHour=1}
	else
		alert("TimeMode can only be 12 or 24");		
	var HourExp=new RegExp("^\\d\\d$");
	if (HourExp.test(intHour) && (parseInt(intHour,10)<=MaxHour) && (parseInt(intHour,10)>=MinHour))
	{	
		if ((Cal.TimeMode==12) && (Cal.AMorPM=="PM"))
		{
			if (parseInt(intHour,10)==12)
				Cal.Hours=12;
			else	
				Cal.Hours=parseInt(intHour,10)+12;
		}	
		else if ((Cal.TimeMode==12) && (Cal.AMorPM=="AM"))
		{
			if (intHour==12)
				intHour-=12;
			Cal.Hours=parseInt(intHour,10);
		}
		else if (Cal.TimeMode==24)
			Cal.Hours=parseInt(intHour,10);	
		return true;
	}
	alert("Invalid hour. Must be between " + MinHour + " and " + MaxHour + ".");
	return false;
}
Calendar.prototype.SetHour=SetHour;

function SetMinute(intMin)
{
	var MinExp=new RegExp("^\\d\\d$");
	if (MinExp.test(intMin) && (intMin<60)) {
		Cal.Minutes=intMin;
		return true;
	}
	alert("Invalid minute '" + intMin + "'. Must be between 0 and 60");
	return false;
}
Calendar.prototype.SetMinute=SetMinute;

function SetSecond(intSec)
{	
	var SecExp=new RegExp("^\\d\\d$");
	if (SecExp.test(intSec) && (intSec<60)) {
		Cal.Seconds=intSec;
		return true;
	}
	alert("Invalid second '"+ intSec + "'. Must be between 0 and 60");
	return false;
}
Calendar.prototype.SetSecond=SetSecond;

function SetAmPm(pvalue)
{
	this.AMorPM=pvalue;
	if (pvalue=="PM")
	{
		this.Hours=(parseInt(this.Hours,10))+12;
		if (this.Hours==24)
			this.Hours=12;
	}	
	else if (pvalue=="AM")
		this.Hours-=12;	
}
Calendar.prototype.SetAmPm=SetAmPm;

function getShowHour()
{
	var finalHour;
    if (Cal.TimeMode==12)
    {
    	if (parseInt(this.Hours,10)==0)
		{
			this.AMorPM="AM";
			finalHour=parseInt(this.Hours,10)+12;	
		}
		else if (parseInt(this.Hours,10)==12)
		{
			this.AMorPM="PM";
			finalHour=12;
		}		
		else if (this.Hours>12)
		{
			this.AMorPM="PM";
			if ((this.Hours-12)<10)
				finalHour="0"+((parseInt(this.Hours,10))-12);
			else
				finalHour=parseInt(this.Hours,10)-12;	
		}
		else
		{
			this.AMorPM="AM";
			if (this.Hours<10)
				finalHour="0"+parseInt(this.Hours,10);
			else
				finalHour=this.Hours;	
		}
	}
	else if (Cal.TimeMode==24)
	{
		if (this.Hours<10)
			finalHour="0"+parseInt(this.Hours,10);
		else	
			finalHour=this.Hours;
	}	
	return finalHour;	
}				
Calendar.prototype.getShowHour=getShowHour;		

function GetMonthName(IsLong)
{
	var Month=monthName[this.Month];
	if (IsLong)
		return Month;
	else
		return Month.substr(0,3);
}
Calendar.prototype.GetMonthName=GetMonthName;

function GetMonDays()//Get number of days in a month
{
	var DaysInMonth=[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if (this.IsLeapYear())
	{
		DaysInMonth[1]=29;
	}	
	return DaysInMonth[this.Month];	
}
Calendar.prototype.GetMonDays=GetMonDays;

function IsLeapYear()
{
	if ((this.Year%4)==0)
	{
		if ((this.Year%100==0) && (this.Year%400)!=0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	else
	{
		return false;
	}
}
Calendar.prototype.IsLeapYear=IsLeapYear;

function FormatDate(pDate)
{
	var yearText = this.Year;
	if(this.Format.toUpperCase().indexOf("YYYY") == -1) {
		yearText = ('' + yearText).substr(2);
	} 
	if (this.Format.toUpperCase()=="DDMMYYYY" || this.Format.toUpperCase()=="DDMMYY")
		return (FormatDayOrMonthNumber(pDate)+this.DateSeparator+FormatDayOrMonthNumber(this.Month+1)+this.DateSeparator+yearText);
	else if (this.Format.toUpperCase()=="DDMMMYYYY" || this.Format.toUpperCase()=="DDMMMYY")
		return (FormatDayOrMonthNumber(pDate)+this.DateSeparator+this.GetMonthName(false)+this.DateSeparator+yearText);
	else if (this.Format.toUpperCase()=="MMDDYYYY" || this.Format.toUpperCase()=="MMDDYY")
		return (FormatDayOrMonthNumber(this.Month+1)+this.DateSeparator+FormatDayOrMonthNumber(pDate)+this.DateSeparator+yearText);
	else if (this.Format.toUpperCase()=="MMMDDYYYY" || this.Format.toUpperCase()=="MMMDDYY")
		return (this.GetMonthName(false)+this.DateSeparator+FormatDayOrMonthNumber(pDate)+this.DateSeparator+yearText);			
}
Calendar.prototype.FormatDate=FormatDate;	

function FormatDayOrMonthNumber(nDate) {
    var s = nDate + '';
	if(s.length < 2) {
		return '0' + s;
	}
	return s;
}
Calendar.prototype.FormatDayOrMonthNumber=FormatDayOrMonthNumber;	

function FormatAsString() {
	var vTimeStr = "";
	if(this.ShowDate) {
		vTimeStr += this.FormatDate(this.Date);
	}
	if (this.ShowTime) {
		if(vTimeStr != "") {
			vTimeStr += " ";
		}
		vTimeStr += this.getShowHour() + ":" + this.Minutes;
		if(this.ShowSeconds) {
			vTimeStr += this.Seconds;
		}
		if (Cal.TimeMode==12) {
			vTimeStr += this.AMorPM;
		}
	}	
	return vTimeStr;
}
Calendar.prototype.FormatAsString=FormatAsString;	
