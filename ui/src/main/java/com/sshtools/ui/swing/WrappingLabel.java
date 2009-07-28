package com.sshtools.ui.swing;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;

/**
 * VWrappingLabel is based on Symantec's class WrappingLabel; however, this
 * class can format the text vertically, too. It also wraps text at newlines
 * embedded in the label's text.
 * 
 * @see symantec.awt.WrappingLabel
 * @author Paul F. Williams (mailto:paul@criterioninc.com) Criterion, Inc.
 *         (http://www.criterioninc.com)
 * @author Kyle Morris (mailto:morriskg@nexusfusion.com)
 * 
 */

public class WrappingLabel extends JComponent {
    // --------------------------------------------------
    // constants
    // --------------------------------------------------

    // --------------------------------------------------
    // class variables
    // --------------------------------------------------

    // --------------------------------------------------
    // member variables
    // --------------------------------------------------
    protected String text;
    protected float m_nHAlign;
    protected float m_nVAlign;
    protected int baseline;
    protected FontMetrics fm;

    // --------------------------------------------------
    // constructors
    // --------------------------------------------------

    public WrappingLabel() {
        this("");
    }

    public WrappingLabel(String s) {
        this(s, Canvas.LEFT_ALIGNMENT, Canvas.CENTER_ALIGNMENT);
    }

    public WrappingLabel(String s, float nHorizontal, float nVertical) {
        setText(s);
        setHAlignStyle(nHorizontal);
        setVAlignStyle(nVertical);
    }

    // --------------------------------------------------
    // accessor members
    // --------------------------------------------------

    public float getHAlignStyle() {
        return m_nHAlign;
    }

    public float getVAlignStyle() {
        return m_nVAlign;
    }

    public String getText() {
        return text;
    }

    public void setHAlignStyle(float a) {
        m_nHAlign = a;
        invalidate();
    }

    public void setVAlignStyle(float a) {
        m_nVAlign = a;
        invalidate();
    }

    public void setText(String s) {
        text = s;
        repaint();
    }

    // --------------------------------------------------
    // member methods
    // --------------------------------------------------

    public String paramString() {
        return "";
    }

    public void paintComponent(Graphics g) {
        if (text != null) {
            Dimension d;
            int currentY = 0;
            Vector lines;

            // Set up some class variables
            fm = getFontMetrics(getFont());
            baseline = fm.getMaxAscent();

            // Get the maximum height and width of the current control
            d = getSize();

            lines = breakIntoLines(text, d.width);

            // if (m_nVAlign == V_ALIGN_CENTER)
            if (m_nVAlign == Canvas.CENTER_ALIGNMENT) {
                int center = (d.height / 2);
                currentY = center - ((lines.size() / 2) * fm.getHeight());
            }
            // else if (m_nVAlign == V_ALIGN_BOTTOM)
            else if (m_nVAlign == Canvas.BOTTOM_ALIGNMENT) {
                currentY = d.height - (lines.size() * fm.getHeight());
            }

            // now we have broken into substrings, print them
            Enumeration elements = lines.elements();
            while (elements.hasMoreElements()) {
                drawAlignedString(g, (String) (elements.nextElement()), 0, currentY, d.width);
                currentY += fm.getHeight();
            }

            // We're done with the font metrics...
            fm = null;
        }
    }

    protected Vector breakIntoLines(String s, int width) {
        int fromIndex = 0;
        int pos = 0;
        int bestpos;
        String largestString;
        Vector lines = new Vector();

        // while we haven't run past the end of the string...
        while (fromIndex != -1) {
            // Automatically skip any spaces at the beginning of the line
            while (fromIndex < text.length() && text.charAt(fromIndex) == ' ') {
                ++fromIndex;
                // If we hit the end of line
                // while skipping spaces, we're done.
                if (fromIndex >= text.length())
                    break;
            }

            // fromIndex represents the beginning of the line
            pos = fromIndex;
            bestpos = -1;
            largestString = null;

            while (pos >= fromIndex) {

                System.out.println("Pos: " + pos + " from index: " + fromIndex + " lines: " + lines.size());

                boolean bHardNewline = false;
                int newlinePos = text.indexOf('\n', pos);
                int spacePos = text.indexOf(' ', pos);

                if (newlinePos != -1 && // there is a newline and either
                                ((spacePos == -1) || // 1. there is no space,
                                // or
                                (spacePos != -1 && newlinePos < spacePos)))
                // 2. the newline is first
                {
                    pos = newlinePos;
                    bHardNewline = true;
                } else {
                    pos = spacePos;
                    bHardNewline = false;
                }

                // Couldn't find another space?
                if (pos == -1) {
                    s = text.substring(fromIndex);
                } else {
                    s = text.substring(fromIndex, pos);
                }

                // If the string fits, keep track of it.
                if (fm.stringWidth(s) < width) {
                    largestString = s;
                    bestpos = pos;

                    // If we've hit the end of the
                    // string or a newline, use it.
                    if (bHardNewline)
                        bestpos++;
                    if (pos == -1 || bHardNewline)
                        break;
                } else {
                    break;
                }

                ++pos;
            }

            if (largestString == null) {
                // Couldn't wrap at a space, so find the largest line
                // that fits and print that. Note that this will be
                // slightly off -- the width of a string will not necessarily
                // be the sum of the width of its characters, due to kerning.
                int totalWidth = 0;
                int oneCharWidth = 0;

                pos = fromIndex;

                while (pos < text.length()) {
                    oneCharWidth = fm.charWidth(text.charAt(pos));
                    if ((totalWidth + oneCharWidth) >= width)
                        break;
                    totalWidth += oneCharWidth;
                    System.out.println("Total width: " + totalWidth + " pos: " + pos + " for " + text.length());
                    ++pos;
                }
                
                if(pos - fromIndex == 0) {
                    // Cant fit anything!
                    break;
                }

                lines.addElement(text.substring(fromIndex, pos));
                fromIndex = pos;
            } else {
                lines.addElement(largestString);
                fromIndex = bestpos;
            }
        }

        return lines;
    }

    protected void drawAlignedString(Graphics g, String s, int x, int y, int width) {
        int drawx;
        int drawy;

        drawx = x;
        drawy = y + baseline;

        if (m_nHAlign != Canvas.LEFT_ALIGNMENT) {
            int sw;

            sw = fm.stringWidth(s);

            if (m_nHAlign == Canvas.CENTER_ALIGNMENT) {
                drawx += (width - sw) / 2;
            } else if (m_nHAlign == Canvas.RIGHT_ALIGNMENT) {
                drawx = drawx + width - sw;
            }
        }

        g.drawString(s, drawx, drawy);
    }
}