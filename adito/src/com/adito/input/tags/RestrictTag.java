package com.adito.input.tags;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseFieldTag;

import com.adito.boot.Util;


/**
 * Restrict the width of some plain text, breaking it up into lines
 * separated by &lt;br&gt; tags.
 */
public class RestrictTag extends BaseFieldTag {

	final static Log log = LogFactory.getLog(FrameTag.class);

	// Protected instance variables

	protected int width;


	/**
	 * Set the width to restrict to
	 * 
	 * @param width width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
		TagUtils.getInstance().write(pageContext, generateFragment());
		return (EVAL_PAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	public void release() {
		super.release();
		width = 40;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	String generateFragment() throws JspException {
		StringBuffer buf = new StringBuffer();
		String content = Util.trimBoth(bodyContent.getString());
		while(true) {
			// If content left is less than width then just append and finish
			if(content.length() <= width) {
				buf.append(content);
				break;
			}
			else {
				// Find the last break character
				
				char[] chars = { '\n', ' ', '-', '.', ',' };
				int sidx = -1;
				for(int i = 0 ; i < chars.length ; i++) {
					sidx = Math.max(content.lastIndexOf(chars[i], width - 1), sidx);					
				}
				
				// If no break char then just break at the full width
				if(sidx == -1) {
					buf.append(content.substring(0, width));
					buf.append("<br/>");
					content = content.substring(width);
				}
				else {
					// Other break at the break character
					buf.append(content.substring(0, sidx));
					buf.append("<br/>");
					content = Util.trimBoth(content.substring(sidx));
				}
			}
		}
		return buf.toString();
	}
}
