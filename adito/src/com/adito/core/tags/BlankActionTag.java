package com.adito.core.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.tiles.TilesUtil;

/**
 */
public class BlankActionTag extends BodyTagSupport {

    private boolean blank = false;
    private int tiles = 1;

    /**
     * Save the associated label from the body content (if any).
     * 
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() == 0) {
                 blank = true;
            }
            else
            {
                blank = false;
            }
        }
        return (EVAL_BODY_INCLUDE);

    }

    public int doEndTag() throws JspException {
        String value = bodyContent.getString().trim();
        if (blank){
            try {
                for (int i = 0; i < tiles; i++) {
                    TilesUtil.doInclude("/WEB-INF/theme/default/blankActionLink.jspf", pageContext);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new JspException(e1);
            } catch (ServletException e2) {
                e2.printStackTrace();
                throw new JspException(e2);
            }
        }
        else{
            TagUtils
            .getInstance()
            .write(pageContext, value);
        }
        return (EVAL_PAGE);
    }

    public int getTiles() {
        return tiles;
    }

    public void setTiles(int tiles) {
        this.tiles = tiles;
    }
}
