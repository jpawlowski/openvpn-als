
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.input.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseFieldTag;
import org.apache.struts.util.MessageResources;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.profile.ProfilePropertyKey;
import com.ovpnals.security.LogonControllerFactory;

/**
 * Tag this inserts a component that allows the user to select a date and / or
 * time.
 */
public class DateTimePickerTag extends BaseFieldTag {

    final static Log log = LogFactory.getLog(DateTimePickerTag.class);

    // Protected instance variables

    protected String dateTimeId;
    protected String pattern;
    protected boolean twentyFourHour;
    protected boolean showTime;
    protected boolean disabled;
    protected String fragment;
    protected String dateSeparator;
    protected boolean showDate;
    protected boolean showMonthYear;
    protected boolean showLongMonth;
    protected boolean showMonthYearPicker;
    protected boolean showSeconds;

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages = MessageResources.getMessageResources("org.apache.struts.taglib.bean.LocalStrings");

    /**
     * Constructor
     */
    public DateTimePickerTag() {
    	release();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    public int doEndTag() throws JspException {
        if(fragment != null) {
            TagUtils.getInstance().write(pageContext, fragment);
        }
        pattern = null;
        return (EVAL_PAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
     */
    public void release() {
        super.release();
        disabled = false;
    	twentyFourHour = true;
    	showTime = true;
    	pattern = null;
    	dateTimeId = null;
    	showDate = true;
    	showLongMonth = true;
    	showMonthYear = false;
    	showMonthYearPicker = true;
    	showSeconds = true;
    	dateSeparator = "/";
    	fragment = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        if (disabled)
            return SKIP_PAGE;
            
        if(getBundle() == null || getBundle().equals("")) {
            setBundle("navigation");
        }
        fragment = generateFragment(); 
        return (EVAL_BODY_AGAIN);
    }
    
    String generateFragment() throws JspException {
        StringBuffer buf = new StringBuffer();
        // <a href="javascript:toggleCalendar('dateFromPicker','dateFrom','ddmmyyyy',
        //true,23)"><img src="/images/cal.gif" width="16" height="16" 
        //border="0" alt="Pick a date"></a><div 
        //style="position:absolute;display: none;overflow:visible;
        //top:4px;width: 220px;left:4px;z-index:900;" id="dateFromPicker">
        //<div/></div>
        buf.append("<a href=\"#\" onclick=\"toggleCalendar('");
        buf.append("dateTimePicker_" + dateTimeId);
        buf.append("','");
        buf.append(dateTimeId);
        buf.append("','");
        String datePattern = pattern;
        if(datePattern == null) {
        	datePattern = Property.getProperty(
    				new ProfilePropertyKey("ui.dateFormat", 
    						LogonControllerFactory.getInstance().getSessionInfo(
    								(HttpServletRequest)pageContext.getRequest())));
        	if(datePattern.contains("-")) {
        		dateSeparator = "-";
        	}
        	else if(datePattern.contains("/")) {
        		dateSeparator = "/";
        	}
        	datePattern = datePattern.replace(dateSeparator, "").toUpperCase();
        }
        buf.append(datePattern);
        buf.append("',");
        buf.append(showTime);
        buf.append(",");
        buf.append(twentyFourHour ? "24" : "12");
        buf.append(",");
        buf.append(showDate);
        buf.append(",");
        buf.append(showMonthYear);
        buf.append(",");
        buf.append(showLongMonth);
        buf.append(",");
        buf.append(showMonthYearPicker);
        buf.append(",");
        buf.append(showSeconds);
        buf.append(",'");
        buf.append(dateSeparator);
        buf.append("','");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/year-up.gif','");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/year-down.gif','");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/close.gif');\"><img src=\"");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/cal.gif\" border=\"0\"></a><div style=\"position: ");
        buf.append("absolute;display:none;overflow:visible;top:4px;width:220px;left:4px;z-index:900\"");
        buf.append(" id=\"");
        buf.append("dateTimePicker_" + dateTimeId);
        buf.append("\"><div>&nbsp</div></div>");
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseHandlerTag#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

	/**
	 * Set the ID of the date / time text field that should be read from
	 * and populated with the selected date.
	 * 
	 * @param dateTimeId date / time text field id
	 */
	public void setDateTimeId(String dateTimeId) {
		this.dateTimeId = dateTimeId;
	}

	/**
	 * Set the pattern. The JavaScript portion of this tag currently
	 * supports dd/MM/yy, dd/MM/yyyy, MM/dd/yy, MM/dd/yyyy, dd-MMM-yyyy,
	 * dd-MMM-yy, MMM-dd-yyyy and MMM-dd-yy
	 * 
	 * @param pattern pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Set whether the time picker should be displayed.
	 * 
	 * @param showTime show time picker 
	 */
	public void setShowTime(String showTime) {
		this.showTime = Boolean.parseBoolean(showTime);
	}

	/**
	 * Set whether the time picker should be in 24 hour
	 * format. If 12 hour format then the component will
	 * display an AM / PM selection component.
	 * 
	 * @param twentyFourHour twent four hour format
	 */
	public void setTwentyFourHour(String twentyFourHour) {
		this.twentyFourHour = Boolean.parseBoolean(twentyFourHour);
	}

	/**
	 * Set whether the date picker should be displayed.
	 * 
	 * @param showDate show date picker 
	 */
	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}

	/**
	 * Set whether the long month name should be displayed.
	 * 
	 * @param showLongMonth show long month name
	 */
	public void setShowLongMonth(boolean showLongMonth) {
		this.showLongMonth = showLongMonth;
	}

	/**
	 * Set wther the month / year subtitle should be displayed.
	 * 
	 * @param showMonthYear show month year subtitle
	 */
	public void setShowMonthYear(boolean showMonthYear) {
		this.showMonthYear = showMonthYear;
	}

	/**
	 * Set whether the month / year picker should be displayed
	 * 
	 * @param showMonthYearPicker show month year picker
	 */
	public void setShowMonthYearPicker(boolean showMonthYearPicker) {
		this.showMonthYearPicker = showMonthYearPicker;
	}

	/**
	 * Set whether the time picker should displayed seconds.
	 * 
	 * @param showSeconds show seconds in time picker
	 */
	public void setShowSeconds(boolean showSeconds) {
		this.showSeconds = showSeconds;
	}

	/**
	 * Set the character to use for the date separator.
	 * 
	 * @param dateSeparator date separator character
	 */
	public void setDateSeparator(String dateSeparator) {
		this.dateSeparator = dateSeparator;
	}
}
