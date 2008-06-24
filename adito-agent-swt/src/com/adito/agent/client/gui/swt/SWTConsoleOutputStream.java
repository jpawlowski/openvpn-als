
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
			
package com.adito.agent.client.gui.swt;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.adito.agent.client.Console;

/**
 * {@link OutputStream} implementation that writes a GUI console component. This
 * may be set as the {@link System#out} stream as a generic GUI console.
 * <p>
 * In order to improve performance, the frame will not be created and output
 * will not be captured until it is first shown (usually as the result of a user
 * action).
 */
public class SWTConsoleOutputStream extends Console {

	// Private instace variables

	private StringBuffer buf = new StringBuffer();
	private Shell shell;
	private Text text;
	private Method deleteMethod;
	private OutputStream oldSysOut;
	private boolean userScrolled;
	private SWTSystemTrayGUI gui;

	/**
	 * Constructor.
	 * 
	 * @param oldSysOut previous system out stream to also write to
	 * @param gui gui
	 */
	public SWTConsoleOutputStream(OutputStream oldSysOut, SWTSystemTrayGUI gui) {
		this.oldSysOut = oldSysOut;
		this.gui = gui;
	}

	/**
	 * Show the console.
	 */
	public void show() {
		if (shell == null) {

			try {
				deleteMethod = StringBuffer.class.getMethod("delete", new Class[] { int.class, int.class }); //$NON-NLS-1$
			} catch (Throwable t) {
			}

			// Create the shell
			shell = new Shell(gui.getDisplay(), SWT.RESIZE | SWT.TITLE);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			shell.setLayout(gridLayout);
			shell.addShellListener(new ShellAdapter() {
				public void shellClosed(ShellEvent e) {
				}
			});
			shell.setText(Messages.getString("ConsoleOutputStream.title")); //$NON-NLS-1$
			shell.setImage(gui.loadImage(SWTSystemTrayGUI.class, "/images/frame-agent.png")); //$NON-NLS-1$

			// Text

			text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			text.setEditable(false);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
			data.horizontalSpan = 3;
			data.verticalSpan = 3;
			data.heightHint = 300;
			data.widthHint = 480;
			text.setLayoutData(data);

			// Clear Button
			final Button clearButton = new Button(shell, SWT.PUSH);
			clearButton.setText(Messages.getString("ConsoleOutputStream.actions.clear")); //$NON-NLS-1$
			data = new GridData();
			data.horizontalAlignment = GridData.END;
			data.horizontalSpan = 2;
			data.grabExcessHorizontalSpace = true;
			clearButton.setLayoutData(data);

			// Close button

			final Button closeButton = new Button(shell, SWT.PUSH);
			closeButton.setText(Messages.getString("ConsoleOutputStream.actions.close"));

			// Button listener

			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					if (event.widget == clearButton) {
						clear();
					} else {
						shell.setVisible(false);
					}
				}
			};
			clearButton.addListener(SWT.Selection, listener);
			closeButton.addListener(SWT.Selection, listener);

			shell.pack();
		}
		text.setText(buf.toString());
		shell.open();
		userScrolled = false;
	}

	void clear() {
		synchronized (buf) {
			buf.setLength(0);
			if (shell.isVisible()) {
				text.setText(buf.toString());
			}
		}
	}

	void append(final String text) {
		try {
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
				if (gui.getDisplay() != null) {
					gui.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (shell != null && shell.isVisible()) {
								SWTConsoleOutputStream.this.text.setText(buf.toString());
								if (!userScrolled) {
									SWTConsoleOutputStream.this.text.setSelection(buf.length());
									// textArea.setCaretPosition(buf.length());
								}
							}
						}
					});
				}
			}
		} catch (Throwable t) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.Console#dispose()
	 */
	public void dispose() {
		if (shell != null) {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					shell.dispose();
				}
			});
		}
	}
}