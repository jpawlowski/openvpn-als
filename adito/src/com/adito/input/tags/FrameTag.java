
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
			
package com.adito.input.tags;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseFieldTag;
import org.apache.struts.util.MessageResources;

import com.adito.core.CoreUtil;
import com.adito.core.Panel;
import com.adito.core.PanelManager;

public class FrameTag extends BaseFieldTag {

	public final static String FRAME_NORMAL = "normal";
	public final static String FRAME_MINIMIZED = "minimized";
	public final static String FRAME_COLLAPSED = "collapsed";
	public final static String FRAME_CLOSED = "closed";

	final static Log log = LogFactory.getLog(FrameTag.class);

	// Protected instance variables

	protected String titleKey;
	protected String title;
	protected String titleClass;
	protected String titleId;
	protected boolean expander;
	protected String panelId;

	/**
	 * The message resources for this package.
	 */
	protected static MessageResources messages = MessageResources.getMessageResources("org.apache.struts.taglib.bean.LocalStrings");

	/**
	 * Constructor
	 */
	public FrameTag() {
	}

	/**
	 * Set the messsage resources key to use for the title
	 * 
	 * @param titleKey title message resources key
	 */
	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	/**
	 * Set the text
	 * 
	 * @param title title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set title class
	 * 
	 * @param titleClass title
	 */
	public void setTitleClass(String titleClass) {
		this.titleClass = titleClass;
	}

	/**
	 * Set title id
	 * 
	 * @param titleId title id
	 */
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	/**
	 * Set if this frame should have an expander
	 * 
	 * @param expander expander
	 */
	public void setExpander(boolean expander) {
		this.expander = expander;
	}

	/**
	 * Set if the panel id
	 * 
	 * @param expander expander
	 */
	public void setPanelId(String panelId) {
		this.panelId = panelId;
		if (getStyleId() == null) {
			setStyleId("component_" + panelId);
		}
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
		titleKey = null;
		title = null;
		expander = false;
		panelId = null;
		titleClass = null;
		titleId = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		if (getBundle() == null || getBundle().equals("")) {
			setBundle("navigation");
		}
		return EVAL_BODY_BUFFERED;
	}

	String generateFragment() throws JspException {
		String framePosition = CoreUtil.getCookieValue("frame_" + getStyleId() + "_pos",
			(HttpServletRequest) pageContext.getRequest(),
			"");
		StringBuffer buf = new StringBuffer();
		Panel p = panelId == null ? null : PanelManager.getInstance().getPanel(panelId);
		if (p == null && getStyleId() == null) {
			throw new JspException("Frame tag requires either panelId or styleId attributes");
		}
		String frameState = CoreUtil.getCookieValue("frame_" + getStyleId(),
			(HttpServletRequest) pageContext.getRequest(),
			p != null ? p.getDefaultFrameState() : FRAME_NORMAL);
		buf.append("<div ");
		buf.append("id=\"");
		buf.append(getStyleId());
		buf.append("\"");
		if (getStyleClass() != null) {
			buf.append(" class=\"");
			buf.append(getStyleClass());
			buf.append("\"");
		}
		if (!framePosition.equals("") || frameState.equals(FRAME_CLOSED)) {
			buf.append(" style=\"");
			if (!framePosition.equals("")) {
				try {
					StringTokenizer t = new StringTokenizer(framePosition, ",");
					int x = Integer.parseInt(t.nextToken());
					int y = Integer.parseInt(t.nextToken());
					buf.append("left: ");
					buf.append(x);
					buf.append("px; top: ");
					buf.append(y);
					buf.append("px;");
				} catch (Exception e) {
				}
			}
			if (frameState.equals("closed") && (p == null || p.isCloseable())) {
				buf.append("display: none;");
			}
			buf.append("\" ");
		}
		buf.append(">");
		buf.append("<div id=\"");
		buf.append(getStyleId());
		buf.append("Container\"");
		if (frameState.equals(FRAME_COLLAPSED)) {
			buf.append(" style=\"position: relative; left: -17.2em\"");
		}
		buf.append(">");

		if (titleKey != null) {
			title = TagUtils.getInstance().message(pageContext, getBundle(), getLocale(), titleKey, new String[] {});
		}

		if (title != null && !title.equals("")) {
			buf.append("<div class=\"titleBar\"");
			if (p != null && p.isDragable()) {
				if (p.isDropable()) {
					buf.append(" onmousedown=\"registerDragAndDrop(event,'");
				} else {
					buf.append(" onmousedown=\"registerDrag(event, \'");
				}
				buf.append(getStyleId());
				buf.append("')\"");
			}
			buf.append(">");
			buf.append("<div class=\"titleInner\">");
			if (expander && (p == null || p.isCloseable())) {
				buf.append("<div class=\"expander\">");
				buf.append("<img id=\"");
				buf.append(getStyleId());
				buf.append("_collapse");
				buf.append("\" onclick=\"frameCollapse('");
				buf.append(getStyleId());
				buf.append("');\" src=\"");
				buf.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/collapse.gif");
				buf.append("\"/>");
				buf.append("</div>");
			}
			buf.append("<div class=\"");
			buf.append(titleClass != null ? titleClass : "title");
			buf.append("\"");
			if (titleId != null) {
				buf.append(" id=\"");
				buf.append(titleId);
				buf.append("\"");
			}
			buf.append(">");
			buf.append(title);
			buf.append("</div>");
			if (p == null || p.isCloseable() || p.isMinimizable()) {
				buf.append("<div class=\"actions\">");
				if (p == null || p.isMinimizable()) {
					buf.append("<img id=\"");
					buf.append(getStyleId());
					buf.append("_minimize");
					buf.append("\" onclick=\"frameMinimize('");
					buf.append(getStyleId());
					buf.append("');\" src=\"");
					buf.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/minimize.gif");
					if (frameState.equals(FRAME_MINIMIZED)) {
						buf.append("\" style=\"display: none");
					}
					buf.append("\"/>");
					buf.append("<img id=\"");
					buf.append(getStyleId());
					buf.append("_restore");
					buf.append("\" onclick=\"frameRestore('");
					buf.append(getStyleId());
					buf.append("');\" src=\"");
					buf.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/maximize.gif");
					if (frameState.equals(FRAME_NORMAL)) {
						buf.append("\" style=\"display: none");
					}
					buf.append("\"/>");
				}
				if (p == null || p.isCloseable()) {
					buf.append("<img id=\"");
					buf.append(getStyleId());
					buf.append("_close");
					buf.append("\" onclick=\"frameClose('");
					buf.append(getStyleId());
					buf.append("');\" src=\"");
					buf.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/close.gif");
					if (frameState.equals(FRAME_CLOSED)) {
						buf.append("\" style=\"display: none\"");
					}
					buf.append("\"/>");
				}
				if (expander) {
					buf.append("<img id=\"");
					buf.append(getStyleId());
					buf.append("_expand");
					buf.append("\" onclick=\"frameExpand('");
					buf.append(getStyleId());
					buf.append("');\" src=\"");
					buf.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/expand.gif");
					if (!frameState.equals(FRAME_COLLAPSED)) {
						buf.append("\" style=\"display: none");
					}
					buf.append("\"/>");
				}
				buf.append("</div>");
			}
			buf.append("</div>");
			buf.append("</div>");
		}
		buf.append("<div class=\"frameContent\">");
		buf.append("<div class=\"frameContentContainer\" id=\"");
		buf.append(getStyleId() + "Content");
		if (frameState.equals(FRAME_MINIMIZED)) {
			buf.append("\" style=\"display: none");
		}
		buf.append("\">");
		buf.append(bodyContent.getString());
		buf.append("</div>");
		buf.append("</div>");
		buf.append("</div>");
		buf.append("</div>");
		if(getStyleId() != null) {
			buf.append("<script language=\"JavaScript\">addDropTarget('");
			buf.append(getStyleId());
			buf.append("');</script>");
		}
		return buf.toString();
	}
}
