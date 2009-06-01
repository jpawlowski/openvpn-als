
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.agent.client.gui.awt;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import com.sshtools.ui.awt.UIUtil;

/**
 * The ProgressBar class allows you to create non-modal progress bars using only
 * the java.awt classes. All you have to do is create an instance of a
 * ProgressBar, update the progress using the updateValue method as you're doing
 * work, and use the dispose method to close the ProgressBar dialog when you're
 * done. The ProgressBar also includes an optional Cancel button, so the user
 * can cancel the action while your process is running (if you use this button,
 * make sure you check the isCancelClicked method occassionally to see if the
 * user clicked Cancel).
 * <p>
 * This class has been implemented here as an inner class, but there's no reason
 * why it couldn't be a class all by itself. The progress bar component itself
 * is actually an inner class to this inner class. Read the comments there to
 * see how it works.
 * <p> * 
 * Make sure you import java.awt.* and java.awt.event.*
 * <p>
 * 
 * Julian Robichaux -- http://www.nsftools.com
 */
public class ProgressBar {
    /**
     * Display nothing in the progress bar
     */
    public final int BOXMSG_NONE = 0;
    /**
     * Display the percent complete (like "20%")
     */
    public final int BOXMSG_PERCENT = 1;
    /**
     * Display the number complete (like "4 of 10")
     */
    public final int BOXMSG_NUMBERS = 2;
    

    private Frame parent;
    private Window pbar;
    private Label mess;
    private ProgressBox pbox;
    private Button cancel;
    private boolean shouldCancel;

    /**
     * This version of the constructor creates a ProgressBar that includes a
     * Cancel button
     * 
     * @param parentComponent 
     * @param message 
     * @param title 
     * @param maxValue 
     */
    public ProgressBar(Component parentComponent, String message, String title, long maxValue) {
        this(parentComponent, message, title, maxValue, true);
    }

    /**
     * When you create a new instance of a ProgressBar, it sets up the progress
     * bar and immediately displays it. The message parameter is a label that
     * displays just above the progress bar, the maxValue parameter indicates
     * what value should be considered 100% on the progress bar, and the
     * allowCancel parameter indicates whether or not a Cancel button should be
     * displayed at the bottom of the dialog.
     * 
     * @param parentComponent 
     * @param message 
     * @param title 
     * @param maxValue 
     * @param allowCancel 
     */
    public ProgressBar(Component parentComponent, String message, String title, long maxValue, boolean allowCancel) {
        shouldCancel = false;
        // this is the invisible Frame we'll be using to call all the Dialog

        parent = UIUtil.getFrameAncestor(parentComponent);
        if (parent == null) {
            pbar = new Frame(title);
        } else {
            if (!parent.isVisible()) {
                parent.setVisible(true);
            }
            parent.toFront();
            pbar = new Dialog(parent, title);
        }

        if (parent != null) {
            parent.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    parent.setVisible(false);
                    dispose();
                }
            });
        }

        // add the message to the top of the dialog
        Panel top = new Panel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        mess = new Label(message);
        top.add(mess);
        pbar.add("North", top); //$NON-NLS-1$

        // add the progress bar to the middle of the dialog
        Panel middle = new Panel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        pbox = new ProgressBox(maxValue);
        middle.add(pbox);
        pbar.add("Center", middle); //$NON-NLS-1$

        // add the Cancel button to the bottom of the dialog (if allowCancel is
        // true)
        if (allowCancel) {
            Panel bottom = new Panel(new FlowLayout(FlowLayout.CENTER, 1, 1));
            cancel = new Button(Messages.getString("ProgressBar.cancel")); //$NON-NLS-1$
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // pbar.dispose();
                    shouldCancel = true;
                }
            });
            bottom.add(cancel);
            pbar.add("South", bottom); //$NON-NLS-1$
        }

        // display the ProgressBar dialog
        Dimension d = pbar.getToolkit().getScreenSize();
        pbar.setLocation(d.width / 3, d.height / 3); // center the
                                                        // ProgressBar (sort of)
        pbar.pack(); // organize all its components
        if (pbar instanceof Dialog) {
            ((Dialog) pbar).setResizable(false); // make sure the user can't
                                                    // resize it
        } else {
            ((Frame) pbar).setResizable(false); // make sure the user can't
                                                // resize it
        }
        pbar.setVisible(true); // and display it
        pbar.toFront(); // give the ProgressBar focus

    }

    /**
     * The updateValue method allows you to update the value used by the
     * progress bar in order to calculate the percent complete on the bar.
     * Percent complete is the value parameter passed to this method divided by
     * the maxValue you passed in when you initially instantiated the
     * ProgressBar.
     * 
     * @param value 
     */
    public void updateValue(long value) {
        pbox.updateValue(value);
    }

    /**
     * The updateMaxValue method allows you to update the maximum value used by
     * the progress bar in order to calculate the percent complete on the bar.
     * @param value 
     */
    public void updateMaxValue(long value) {
        pbox.updateMaxValue(value);
    }

    /**
     * The getCurrentValue method returns the current value used by the progress
     * bar to determine the percent complete.
     * 
     * @return current value
     */
    public long getCurrentValue() {
        return pbox.getCurrentValue();
    }

    /**
     * The getMaxValue method returns the maximum value used by the progress bar
     * to determine the percent complete (once the current value equals the
     * maximum value, we're at 100%)
     * 
     * @return max value
     */
    public long getMaxValue() {
        return pbox.getMaxValue();
    }

    /**
     * The setBarText method sets the value of the dispText field in the
     * progress bar, which indicates what kind of message should be displayed in
     * the bar. You'll normally use a value of BOXMSG_NONE, BOXMSG_PERCENT, or
     * BOXMSG_NUMBERS for this value.
     * 
     * @param boxMsgValue text
     */
    public void setBarText(int boxMsgValue) {
        pbox.setBarMsg(boxMsgValue);
    }

    /**
     * Set the message
     * 
     * @param message message
     */
    public void setMessage(String message) {
        mess.setText(message);
        // parent.pack();
    }

    /**
     * The dispose method removes the ProgressBar from the screen display.
     */
    public void dispose() {
        // use this when you're ready to clean up
        try {
            pbar.dispose();
        } catch (Exception e) {
        }

        // TODO brett - This closes the vpn window? any reason for this?
        // try {
        // parent.dispose();
        // }
        // catch (Exception e) {}
    }

    /**
     * The isCancelClicked method indicates whether or not the user clicked the
     * Cancel button. Normally, when you realize that the user has clicked
     * Cancel, you'll want to call the dispose method to stop displaying the
     * ProgressBar.
     * 
     * @return cancel click
     */
    public boolean isCancelClicked() {
        return shouldCancel;
    }

    /**
     * The ProgressBox is the actual awt component that displays a progress bar.
     * It's implemented here as an inner class of the ProgressBar class, but it
     * can be a separate class too. Just make sure you import java.awt.*
     * 
     * Julian Robichaux -- http://www.nsftools.com
     */
    class ProgressBox extends Canvas {
        /**
         * Display nothing in the progress bar
         */
        public final int MSG_NONE = 0; 
        
        /**
         * Display the percent complete like "20%")
         */
        public final int MSG_PERCENT = 1; 
        
        /**
         * Display the number complete (like "4 of 10")
         */
        public final int MSG_NUMBERS = 2;

        private long maxVal, currentVal;
        private int cols, width, height, dispText;
        private Color barClr, borderClr, textClr;

        /**
         * Constructor.
         *
         * @param maxValue max value
         */
        public ProgressBox(long maxValue) {
            this(maxValue, 40);
        }

        /**
         * Constructor.
         *
         * @param maxValue max value
         * @param width width
         */
        public ProgressBox(long maxValue, int width) {
            // one unit of width for this component is the width of
            // the letter 'X' in the current font (so a width of 10 is
            // a width of 'XXXXXXXXXX' using the current font)
            maxVal = maxValue;
            currentVal = 0;
            cols = width;
            dispText = MSG_PERCENT;
            barClr = Color.decode("#D8DFEE"); // make the progress bar light //$NON-NLS-1$
                                                // blue
            borderClr = Color.gray; // make the bar border gray
            textClr = Color.darkGray; // make the text dark gray
        }

        protected void measure() {
            // get the global values we use in relation to our current font
            FontMetrics fm = this.getFontMetrics(this.getFont());
            if (fm == null) {
                return;
            }
            width = fm.stringWidth("X") * cols; //$NON-NLS-1$
            height = fm.getHeight() + 4;
        }

        public void addNotify() {
            // automatically invoked after our Canvas is created but
            // before it's displayed (FontMetrics aren't available until
            // super.addNotify() has been called)
            super.addNotify();
            measure();
        }

        public Dimension getPreferredSize() {
            // called by the LayoutManager to find out how big we want to be
            return new Dimension(width + 4, height + 4);
        }

        public Dimension getMinimumSize() {
            // called by the LayoutManager to find out what our bare minimum
            // size requirements are
            return getPreferredSize();
        }

        /**
         * Update value
         * 
         * @param value value
         */
        public void updateValue(long value) {
            // change the currentVal, which is used to determine what our
            // percent
            // complete is, and update the progress bar
            currentVal = value;
            this.repaint();
        }

        /**
         * Update max value
         * 
         * @param value max value
         */
        public void updateMaxValue(long value) {
            // change the maxVal, which is used to determine what our percent
            // complete is ((currentVal / maxVal) * 100 = percent complete),
            // and update the progress bar
            maxVal = value;
            this.repaint();
        }

        /**
         * Get current value
         * 
         * @return current value
         */
        public long getCurrentValue() {
            // return the currentVal
            return currentVal;
        }

        /**
         * Get max value
         * 
         * @return max value
         */
        public long getMaxValue() {
            // return the maxVal
            return maxVal;
        }

        /**
         * Set text 
         * 
         * @param msgValue text
         */
        public void setBarMsg(int msgValue) {
            // change the dispText value, which is used to determine what text,
            // if any, is displayed in the progress bar (use either MSG_NONE,
            // MSG_PERCENT, or MSG_NUMBERS)
            dispText = msgValue;
        }

        /**
         * Set the colors
         * 
         * @param barColor bar color
         * @param borderColor border color
         * @param textColor text color
         */
        public void setColors(Color barColor, Color borderColor, Color textColor) {
            // set the colors used by the progress bar components
            if (barColor != null) {
                barClr = barColor;
            }
            if (borderColor != null) {
                borderClr = borderColor;
            }
            if (textColor != null) {
                textClr = textColor;
            }
        }

        public void paint(Graphics g) {
            // draw the actual progress bar to the screen
            // this is the bar itself
            g.setColor(barClr);
            if (currentVal > 0 && maxVal > 0)
                g.fillRect(0, 0, (int) ((currentVal * width) / maxVal), height);

            // this is the border around the bar
            g.setColor(borderClr);
            g.drawRect(0, 0, width, height);

            // this is the text to display (if any)
            g.setColor(textClr);
            if (dispText == MSG_PERCENT) {
                if (currentVal > 0 && maxVal > 0) {
                    centerText(String.valueOf((int) ((currentVal * 100) / maxVal)) + "%", g, width, height); //$NON-NLS-1$
                } else if (currentVal == 0) {
                    centerText("0%", g, width, height); //$NON-NLS-1$

                }
            } else if (dispText == MSG_NUMBERS) {
                centerText(MessageFormat.format(Messages.getString("ProgressBar.numbers"), new Object[] { new Long(currentVal), new Long(maxVal) } ), g, width, height); //$NON-NLS-1$
            }
        }

        private void centerText(String s, Graphics g, int w, int h) {
            // from the centerText method in "Java Examples in a Nutshell"
            FontMetrics fm = this.getFontMetrics(this.getFont());
            if (fm == null) {
                return;
            }
            int sWidth = fm.stringWidth(s);
            int sx = (w - sWidth) / 2;
            int sy = (h - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(s, sx, sy);
        }

    } // end of the ProgressBox class

} // end of the ProgressBar class
