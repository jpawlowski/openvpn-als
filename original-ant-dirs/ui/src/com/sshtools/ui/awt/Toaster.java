package com.sshtools.ui.awt;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * UI component that display <i>toaster</i> style messages. When a request to
 * display a message is made, the message will appear at a specified location on
 * the users desktop. It will stay visible for a specified amount of time. If
 * more messages are sent in the meantime, they will appear in a new window
 * either just above or just below the last messages.
 * 
 * @author Brett Smith <a href="mailto: brett@3sp.com">&lt;brett@3sp.com&gt;</a>
 */
public class Toaster {

	/**
	 * Default timeout
	 */
	public final static int DEFAULT_TIMEOUT = 10000;

	/**
	 * Toaster appears at the bottom right of the screen with messages flowing
	 * up from bottom to top
	 */
	public final static int BOTTOM_RIGHT = 0;

	/**
	 * Toaster appears at the bottom left of the screen with messages flowing up
	 * from bottom to top
	 */
	public final static int BOTTOM_LEFT = 1;

	/**
	 * Toaster appears at the top left of the screen with messages flowing down
	 * from top to bottom
	 */
	public final static int TOP_LEFT = 2;

	/**
	 * Toaster appears at the top right of the screen with messages flowing down
	 * from top to bottom
	 */
	public final static int TOP_RIGHT = 3;

	/*
	 * Configurable statics
	 */

	/**
	 * Default background color for new toasters
	 */
	public static Color BACKGROUND_COLOR = null;

	/**
	 * Default foreground color for new toasters
	 */
	public static Color FOREGROUND_COLOR = null;

	/**
	 * Default border color for new toasters
	 */
	public static Color BORDER_COLOR = null;

	/**
	 * Default text font for new toasters
	 */
	public static Font TEXT_FONT = null;

	/**
	 * Default title font for new toasters
	 */
	public static Font TITLE_FONT = null;

	/*
	 * Intialise depending on environment
	 */
	static {
		try {
			if ("false".equals(System.getProperty("toaster.loadStyleFromUIManager", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				throw new Exception(Messages.getString("Toaster.disabled")); //$NON-NLS-1$
			}
			Class clazz = Class.forName("javax.swing.UIManager"); //$NON-NLS-1$
			Method colorMethod = clazz.getMethod("getColor", new Class[] { Object.class }); //$NON-NLS-1$
			Method fontMethod = clazz.getMethod("getFont", new Class[] { Object.class }); //$NON-NLS-1$
			BACKGROUND_COLOR = (Color) colorMethod.invoke(null, new Object[] { "ToolTip.background" }); //$NON-NLS-1$
			FOREGROUND_COLOR = (Color) colorMethod.invoke(null, new Object[] { "ToolTip.foreground" }); //$NON-NLS-1$
			BORDER_COLOR = (Color) colorMethod.invoke(null, new Object[] { "ToolTip.foreground" }); //$NON-NLS-1$
			TEXT_FONT = (Font) fontMethod.invoke(null, new Object[] { "ToolTip.font" }); //$NON-NLS-1$
			TITLE_FONT = ((Font) fontMethod.invoke(null, new Object[] { "Label.font" })); //$NON-NLS-1$
			TITLE_FONT = new Font(TITLE_FONT.getName(), Font.BOLD, TITLE_FONT.getSize());
		} catch (Exception e) {
			BACKGROUND_COLOR = Color.white;
			FOREGROUND_COLOR = Color.black;
			BORDER_COLOR = Color.black;
			TEXT_FONT = new Font("Arial", Font.PLAIN, 10); //$NON-NLS-1$
			TITLE_FONT = new Font("Arial", Font.BOLD, 11); //$NON-NLS-1$
		}
	}

	// Private instance variables

	private Vector messages;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color borderColor;
	private Font textFont;
	private Font titleFont;
	private int position;
	private float textAlign;
	private Dimension popupSize;
	private MagicThread magicThread;

	// Private static variables
	private static Frame sharedFrame;

	/**
	 * Constructor.
	 * 
	 * @param position
	 * @param popupSize popup size
	 * @see #setPosition(int)
	 */
	public Toaster(int position, Dimension popupSize) {
		messages = new Vector();
		backgroundColor = BACKGROUND_COLOR;
		foregroundColor = FOREGROUND_COLOR;
		borderColor = BORDER_COLOR;
		textFont = TEXT_FONT;
		titleFont = TITLE_FONT;
		this.popupSize = popupSize;
		this.position = position;
		textAlign = Canvas.CENTER_ALIGNMENT;
	}

	/**
	 * Set the text alignment. May be one of
	 * {@link java.awt.Component#LEFT_ALIGNMENT},
	 * {@link java.awt.Component#CENTER_ALIGNMENT} or
	 * {@link java.awt.Component#RIGHT_ALIGNMENT}.
	 * 
	 * @param textAlign text alignment
	 */
	public void setTextAlign(float textAlign) {
		this.textAlign = textAlign;
	}

	/**
	 * Set the size of the popups. This will only take affect on new messages.
	 * 
	 * @param popupSize popup size
	 */
	public void setPopupSize(Dimension popupSize) {
		this.popupSize = popupSize;
	}

	/**
	 * Get the size of the popups.
	 * 
	 * @return popup size
	 */
	public Dimension getPopupSize() {
		return popupSize;
	}

	/**
	 * Set the position of messages. Can be one of {@link #TOP_LEFT},
	 * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} or {@link #BOTTOM_RIGHT}.
	 * 
	 * @param position position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Get the position of messages. Can be one of {@link #TOP_LEFT},
	 * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} or {@link #BOTTOM_RIGHT}.
	 * 
	 * @return position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Get the background color
	 * 
	 * @return background color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Set the background color. This will only take affect on new messages.
	 * 
	 * @param backgroundColor background color
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Get the border color
	 * 
	 * @return border color
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Set the border color. This will only take affect on new messages.
	 * 
	 * @param borderColor border color
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Get the foreground color
	 * 
	 * @return foreground color
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Set the foreground color. This will only take affect on new messages.
	 * 
	 * @param foregroundColor
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Get the text text
	 * 
	 * @return text font
	 */
	public Font getTextFont() {
		return textFont;
	}

	/**
	 * Set the text font. This will only take affect on new messages.
	 * 
	 * @param textFont text font
	 */
	public void setTextFont(Font textFont) {
		this.textFont = textFont;
	}

	/**
	 * Get the title font. This will only take affect on new messages.
	 * 
	 * @return title font
	 */
	public Font getTitleFont() {
		return titleFont;
	}

	/**
	 * Set the title font. This will only take affect on new messages.
	 * 
	 * @param titleFont title font
	 */
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	/**
	 * Popup a new message for 10 seconds.
	 * 
	 * @param callback invoked when message is clicked
	 * @param message message to display
	 * @param title title of message
	 */
	public synchronized void popup(ActionListener callback, String message, String title) {
		popup(callback, message, title, null);
	}

	/**
	 * Popup a new message for 10 seconds with an option image.
	 * 
	 * @param callback invoked when message is clicked
	 * @param message message to display
	 * @param title title of message
	 * @param image image or <code>null</code>
	 */
	public synchronized void popup(ActionListener callback, String message, String title, Image image) {
		popup(callback, message, title, image, -1);
	}

	/**
	 * Popup a new message.
	 * 
	 * @param callback invoked when message is clicked
	 * @param message message to display
	 * @param title title of message
	 * @param timeout time to display message for
	 * @param image image or <code>null</code>
	 */
	public void popup(ActionListener callback, String message, String title, Image image, int timeout) {
		if (timeout == -1) {
			timeout = DEFAULT_TIMEOUT;
		}

		// Create the new message window and add it to our Vector
		MessageWindow window = new MessageWindow(popupSize, callback, message, title, image, timeout);
		window.pack();

		messages.addElement(window);

		// Stop the magic thread
		if (magicThread != null) {
			magicThread.stopMagic();
			magicThread = null;
		}

		// Reposition and show the messages windows
		repositionPopups();
	}

	void repositionPopups() {

		synchronized (messages) {
			// Get the screeb suze
			Dimension d = null;
			try {
				Object genv = Class.forName("java.awt.GraphicsEnvironment") //$NON-NLS-1$
								.getMethod("getLocalGraphicsEnvironment", new Class[] {}).invoke(null, new Object[] {}); //$NON-NLS-1$
				Object[] devices = (Object[]) genv.getClass().getMethod("getScreenDevices", new Class[] {}).invoke(genv, //$NON-NLS-1$
					new Object[] {});
				Object mode = devices[0].getClass().getMethod("getDisplayMode", new Class[] {}).invoke(devices[0], new Object[] {}); //$NON-NLS-1$
				d = new Dimension(((Integer) mode.getClass().getMethod("getWidth", new Class[] {}).invoke(mode, new Object[] {})) //$NON-NLS-1$
				.intValue(), ((Integer) mode.getClass().getMethod("getHeight", new Class[] {}).invoke(mode, //$NON-NLS-1$
					new Object[] {})).intValue());
			} catch (Exception e) {
				d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			}

			// Calculate the starting position
			int wy = 0;
			switch (position) {
				case TOP_LEFT:
				case TOP_RIGHT:
					wy = 16;
					break;
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT:
					wy = d.height - 48;
			}

			// Set the target positions on all of the windows
			for (Enumeration e = messages.elements(); e.hasMoreElements();) {
				MessageWindow w = (MessageWindow) e.nextElement();

				// Calculate the x (and y where appropriate) position
				int wx = 0;
				switch (position) {
					case TOP_LEFT:
						wx = 16;
						break;
					case BOTTOM_LEFT:
						wy = wy - w.getPreferredSize().height;
						wx = 16;
						break;
					case TOP_RIGHT:
						wx = d.width - popupSize.width - 16;
						break;
					case BOTTOM_RIGHT:
						wy = wy - w.getPreferredSize().height;
						wx = d.width - popupSize.width - 16;
						break;
				}
				int owy = wy;

				// Increment the y position
				switch (position) {
					case TOP_LEFT:
					case TOP_RIGHT:
						wy += 16;
						break;
					case BOTTOM_LEFT:
					case BOTTOM_RIGHT:
						wy -= 16;
						break;
				}

				// Show the window if it is not visible
				if (!w.isVisible()) {
					w.setLocation(wx, owy);
					w.setTargetY(owy);
					w.setVisible(true);
				} else {
					w.setTargetY(owy);
				}
			}
		}

	}

	/*
	 * Hide a popup, remove it from the list and move other popups to their new
	 * position
	 */
	void hideAndRemove(MessageWindow messageWindow) {
		synchronized (messages) {
			messageWindow.setVisible(false);
			messages.removeElement(messageWindow);
			repositionPopups();

			// Start the magic thread if its not running
			if (messages.size() != 0 && (magicThread == null || !magicThread.isAlive())) {
				magicThread = new MagicThread();
			}
		}
	}

	/*
	 * Get a shared parent frame
	 */
	static Frame getSharedFrame() {
		if (sharedFrame == null) {
			sharedFrame = new Frame();
		}
		return sharedFrame;
	}

	/**
	 * Test.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// #ifdef JAVA2
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		// #endif
		Toaster.BACKGROUND_COLOR = Color.white;
		Toaster.FOREGROUND_COLOR = Color.black;
		Toaster.BORDER_COLOR = Color.black;
		// Toaster t = new Toaster(BOTTOM_RIGHT, new Dimension(160, 160));
		// t.popup(null, "This is a test message blah blah blah blah", "Brett
		// says",
		// UIUtil.loadImage(Toaster.class, "/images/error-48x48.png"));
		// t.popup(null, "This is a test message 2", "Pete says");
		// t.popup(null, "This is a test message 3", "Ash says");
		// t.popup(null, "This is a multi\nline test message 3", "Lee Says");
		final Toaster t = new Toaster(BOTTOM_RIGHT, new Dimension(260, 60));
		t.setTextAlign(Canvas.LEFT_ALIGNMENT);
		t.popup(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		}, "This is a test message blah blah blah blah", "Brett says", UIUtil.loadImage(Toaster.class, "/images/error-48x48.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ActionListener a = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				t.popup(null, "This is a test message blah blah blah blah", "JB says", UIUtil.loadImage(Toaster.class, //$NON-NLS-1$ //$NON-NLS-2$
					"/home/brett/Desktop/Modules/vpn-client/client/images/idle.gif")); //$NON-NLS-1$
				// System.exit(0);
			}
		};
		t.popup(a, "This is a test message blah blah blah blah", "JB says", UIUtil.loadImage(Toaster.class, //$NON-NLS-1$ //$NON-NLS-2$
			"/home/brett/Desktop/Modules/vpn-client/client/images/idle.gif"), 2000); //$NON-NLS-1$
		t.popup(a, "This is a test message 2", "Pete says", null, 7000); //$NON-NLS-1$ //$NON-NLS-2$
		// t.popup(null, "This is a test message 3", "Ash says");
		Thread.sleep(1000);
		t.popup(a, "This is a multi\nline test message 3", "Lee Says"); //$NON-NLS-1$ //$NON-NLS-2$
		t.popup(a,
			"This is a test message blah blah blah blah that should push the size of the text box over the fixed dimensions provided. If it does not something has gone horibbly horibbly wrong. arrrrrrrrggggggggggh!!!!!!!!!!!!!", //$NON-NLS-1$
			"JB says", UIUtil.loadImage(Toaster.class, //$NON-NLS-1$
				"/home/brett/Desktop/Modules/vpn-client/client/images/idle.gif"), 5000); //$NON-NLS-1$
		t.popup(a,
			"This is a multiline test message blah\nblah blah blah that\nshould push the size of the text\nbox over the fixed dimensions\nprovided. If it does not something\nhas gone horibbly horibbly wrong. arrrrrrrrggggggggggh!!!!!!!!!!!!!", //$NON-NLS-1$
			"JB says", UIUtil.loadImage(Toaster.class, //$NON-NLS-1$
				"/home/brett/Desktop/Modules/vpn-client/client/images/idle.gif"), 8000); //$NON-NLS-1$
	}

	class MagicThread extends Thread {

		boolean run = true;
		int moved;

		MagicThread() {
			super("MagicThread"); //$NON-NLS-1$
			start();
		}

		public void run() {
			moved = 1;
			while (run && moved > 0) {
				try {
					Method invokeAndWaitMethod = Class.forName("java.awt.EventQueue").getMethod("invokeAndWait", new Class[] { Runnable.class }); //$NON-NLS-1$ //$NON-NLS-2$
					Runnable r = new Runnable() {
						public void run() {
							moved = doMove();
						}
					};
					invokeAndWaitMethod.invoke(null, new Object[] { r });
				} catch (Exception e) {
					moved = doMove();
				}
				yield();
				try {
					sleep(5);
				} catch (InterruptedException e1) {
				}
			}
		}

		public void stopMagic() {
			run = false;
			interrupt();
		}

		int doMove() {

			synchronized (messages) {
				int moved = 0;
				int ly, lx, wy;
				MessageWindow w;
				Enumeration e;
				for (e = messages.elements(); e.hasMoreElements();) {
					w = (MessageWindow) e.nextElement();
					ly = w.getLocation().y;
					lx = w.getLocation().x;
					wy = w.getTargetY();
					if (ly > wy) {
						ly = Math.max(ly - 4, wy);
						w.setLocation(lx, ly);
						moved++;
					} else if (ly < wy) {
						ly = Math.min(ly + 4, wy);
						w.setLocation(lx, ly);
						moved++;
					}
				}
				return moved;
			}
		}
	}

	class MessageWindow extends Window implements MouseListener {

		ActionListener callback;
		Dimension preferredSize;
		int targetY;

		MessageWindow(Dimension preferredSize, ActionListener callback, String message, String title, Image image, final int timeout) {
			this(preferredSize, callback, message,  title, image, timeout, null);
		}

		MessageWindow(Dimension preferredSize, ActionListener callback, String message, String title, Image image, final int timeout, String actionText) {
			super(Toaster.getSharedFrame());
			this.preferredSize = preferredSize;
			this.callback = callback;
			// #ifdef JAVA2
			try {
				Method m = getClass().getMethod("setAlwaysOnTop", new Class[] { boolean.class }); //$NON-NLS-1$
				m.invoke(this, new Object[] { Boolean.TRUE });
			} catch (Exception e) {
			}
			// #endif
			BorderPanel p = new BorderPanel(new GridBagLayout());
			p.insets = new Insets(4, 4, 4, 4);
			p.setBorderColor(borderColor);
			p.setBackground(backgroundColor);
			p.setForeground(foregroundColor);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;

			Label tl = new Label(title);
			tl.setFont(titleFont);
			UIUtil.gridBagAdd(p, tl, gbc, GridBagConstraints.REMAINDER);

			WrappingLabel l = new WrappingLabel();
			l.setText(message);
			l.setVAlignStyle(Canvas.TOP_ALIGNMENT);
			l.setBackground(backgroundColor);
			l.setForeground(foregroundColor);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1.0;
			l.setFont(textFont);
			l.setHAlignStyle(textAlign);
			gbc.weightx = 1.0;
			if (image != null) {
				Panel pi = new Panel(new BorderLayout(4, 0));
				pi.setBackground(backgroundColor);
				pi.setForeground(foregroundColor);
				UIUtil.waitFor(image, this);
				ImageCanvas c = new ImageCanvas(image);
				c.setValign(Canvas.TOP_ALIGNMENT);
				pi.add(c, BorderLayout.WEST);
				pi.add(l, BorderLayout.CENTER);
				UIUtil.gridBagAdd(p, pi, gbc, GridBagConstraints.REMAINDER);
			} else {
				UIUtil.gridBagAdd(p, l, gbc, GridBagConstraints.REMAINDER);
			}

			p.addMouseListener(this);
			l.addMouseListener(this);
			tl.addMouseListener(this);

			add(p);

			if (timeout > 0) {
				Thread t = new Thread() {
					public void run() {
						try {
							Thread.sleep(timeout);
						} catch (InterruptedException ie) {
						}
						hideAndRemove(MessageWindow.this);
					}
				};
				t.start();
			}
			
			if(actionText != null) {
				Label l2 = new Label(actionText);
				l2.setAlignment(Label.CENTER);
				l2.setFont(new Font(titleFont.getName(), Font.PLAIN, 10));
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.weightx = 1.0;
				UIUtil.gridBagAdd(p, l2, gbc, GridBagConstraints.REMAINDER);
			}
		}

		public void setTargetY(int targetY) {
			this.targetY = targetY;
		}

		public int getTargetY() {
			return targetY;
		}

		public Dimension getPreferredSize() {
			Dimension s = super.getPreferredSize();
			if (s.height > preferredSize.height) {
				return new Dimension(preferredSize.width, s.height);
			}
			return preferredSize;
		}

		public void mouseClicked(MouseEvent e) {
			if (callback != null) {
				callback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "clicked")); //$NON-NLS-1$
			}
			hideAndRemove(this);
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

}
