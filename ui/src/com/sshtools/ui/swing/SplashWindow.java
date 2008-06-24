package com.sshtools.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * Swing component that can be used to show a <i>splash</i> image, useful for
 * branding an application and displaying something pretty for the user to look
 * while the application starts up. <br><br>The splash window will stay
 * visible until either the application invokes the
 * {@link SplashWindow.dismiss}method or amount of time specified elapses -
 * whichever is the greater.
 *
 * @author $Author: brett $
 */
public class SplashWindow
    extends JWindow
    implements ActionListener {

  // Private instance variables
  private JPanel mainPanel;
  private Timer timer;
  private boolean appDismissed;
  private boolean timerExpired;

  /**
   * Construct a new splash
   *
   * @param parent parent frame
   * @param img image to use for splash
   * @param timeout time to wait for splash to disapper
   */
  public SplashWindow(JFrame parent, Image img, int timeout) {
    this(parent, img, timeout, null);
  }

  /**
   * Construct a new splash
   *
   * @param parent parent frame
   * @param img image to use for splash
   * @param timeout time to wait for splash to disapper
   * @param accesoryComponent accesory component
   */
  public SplashWindow(JFrame parent, Image img, int timeout,
                      JComponent accessoryComponent) {
    super(parent);
    //
    ImageIcon image = new ImageIcon(img);
    int w = image.getIconWidth() + 5;
    int h = image.getIconHeight() + 5;
    //  Main panel
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setOpaque(false);
    //  Accesory component (if specified)
    if (accessoryComponent != null) {
      mainPanel.add(accessoryComponent, BorderLayout.SOUTH);
      //  Image part
    }
    JLabel p = new JLabel(image);
    p.setBorder(null);
    mainPanel.add(p, BorderLayout.CENTER);
    //  This panel
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(mainPanel);
    //  Timeout after specified time
    timer = new Timer(0, null);
    timer.setRepeats(false);
    timer.setInitialDelay(timeout);
    //  Centre the splash
    pack();
    UIUtil.positionComponent(SwingConstants.CENTER, this);
  }

  /**
   * Set the background color of the splash
   *
   * @param b background color
   */
  public void setBackground(Color b) {
    getContentPane().setBackground(b);
    mainPanel.setBackground(b);
  }

  /**
   * Set the border
   *
   * @param border the border
   */
  public void setBorder(Border b) {
    mainPanel.setBorder(b);
    UIUtil.positionComponent(SwingConstants.CENTER, this);
  }

  /**
   * Get the border
   *
   * @return the border
   */
  public Border getBorder() {
    return mainPanel.getBorder();
  }

  /**
   * Remove the splash image from the screen
   */
  public void dismiss() {
    if (!timerExpired) {
      appDismissed = true;
    }
    else {
      dismissImpl();
    }
  }

  private void dismissImpl() {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              dispose();
              
          }
      });
  }

  /*
   * (non-Javadoc)
   *
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
    if (appDismissed) {
      dismissImpl();
    }
    else {
      timerExpired = true;
    }
  }

  /* (non-Javadoc)
   * @see java.awt.Component#show()
   */
  public void show() {
    timer.addActionListener(this);
    timer.start();
    super.show();
  }

  /* (non-Javadoc)
   * @see java.awt.Component#hide()
   */
  public void hide() {
    timer.stop();
    timer.removeActionListener(this);
    super.hide();
  }
}