
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
			
package com.adito.agent.client.gui.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.sshtools.ui.awt.UIUtil;
import com.adito.agent.client.Console;

/**
 * {@link OutputStream} implementation that writes a GUI console component. This
 * may be set as the {@link System#out} stream as a generic GUI console.
 * <p>
 * In order to improve performance, the frame will not be created and output
 * will not be captured until it is first shown (usually as the result of a user
 * action).
 */
public class AWTConsoleOutputStream extends Console {

    // Private instace variables

    private StringBuffer buf = new StringBuffer();
    private Frame frame;
    private TextArea textArea;
    private Method deleteMethod;
    private OutputStream oldSysOut;
    private boolean userScrolled;

    /**
     * Constructor.
     * 
     * @param oldSysOut previous system out stream to also write to
     */
    public AWTConsoleOutputStream(OutputStream oldSysOut) {
        this.oldSysOut = oldSysOut;
    }

    /**
     * Show the console.
     */
    public void show() {
        if (textArea == null) {

            try {
                deleteMethod = StringBuffer.class.getMethod("delete", new Class[] { int.class, int.class }); //$NON-NLS-1$
            } catch (Throwable t) {
            }
            textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setBackground(Color.white);
            textArea.setForeground(Color.black);
            Panel panel = new Panel(new BorderLayout());
            panel.setBackground(Color.gray);
            panel.setForeground(Color.black);
            panel.add(textArea, BorderLayout.CENTER);
            Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.gray);
            buttonPanel.setForeground(Color.black);
            Button clear = new Button(Messages.getString("ConsoleOutputStream.actions.clear")); //$NON-NLS-1$
            clear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    clear();
                }
            });
            buttonPanel.add(clear);
            Button close = new Button(Messages.getString("ConsoleOutputStream.actions.close")); //$NON-NLS-1$
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    frame.setVisible(false);
                }
            });
            buttonPanel.add(close);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            frame = new Frame(Messages.getString("ConsoleOutputStream.title")); //$NON-NLS-1$
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    frame.setVisible(false);

                }
            });
            frame.setIconImage(UIUtil.loadImage(getClass(), "/images/frame-agent.gif")); //$NON-NLS-1$
            frame.add(panel);
            frame.pack();
            frame.setLocation(100, 100);
            frame.setSize(300, 400);
        }
        textArea.setText(buf.toString());
        frame.setVisible(true);
        frame.toFront();
        textArea.setCaretPosition(buf.length());
        userScrolled = false;
    }

    void clear() {
        synchronized (buf) {
            buf.setLength(0);
            if (frame.isVisible()) {
                textArea.setText(buf.toString());
                textArea.setCaretPosition(buf.length());
            }
        }
    }

    void append(String text) {
        synchronized (buf) {
            buf.append(text);
            if (buf.length() > 65535) {
                if (deleteMethod != null) {
                    try {
                        deleteMethod.invoke(buf, new Object[] { new Integer(0), new Integer(buf.length() - 65535) });
                    } catch (Throwable t) {
                        String newBuf = buf.toString().substring(buf.length() - 65535);
                        buf.setLength(0);
                        buf.append(newBuf);
                    }
                } else {
                    String newBuf = buf.toString().substring(buf.length() - 65535);
                    buf.setLength(0);
                    buf.append(newBuf);
                }
            }
            if (frame != null && frame.isVisible()) {
                textArea.setText(buf.toString());
                if (!userScrolled) {
                    textArea.setCaretPosition(buf.length());
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        append(String.valueOf((char) b));
        if (oldSysOut != null) {
            oldSysOut.write(b);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        append(new String(buf, off, len));
        if (oldSysOut != null) {
            oldSysOut.write(buf, off, len);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        super.flush();
        if (oldSysOut != null) {
            oldSysOut.flush();
        }
    }

	public void dispose() {
		if(frame != null)
			frame.dispose();		
	}
}