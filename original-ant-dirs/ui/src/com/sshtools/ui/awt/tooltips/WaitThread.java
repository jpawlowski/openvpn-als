/*
 */
package com.sshtools.ui.awt.tooltips;

import java.awt.Component;
import java.awt.Frame;

import com.sshtools.ui.awt.UIUtil;

class WaitThread extends Thread {
    TipWindow tipWindow;
    Component component;
    int tx = -1;
    int ty = -1;
    String text;
    Frame lastSharedFrame;

    WaitThread() {
        super("ToolTip"); //$NON-NLS-1$

    }

    synchronized void requestToolTip(Component component, String text) {
        requestToolTip(component, -1, -1, text);
    }

    synchronized void requestToolTip(Component component, int x, int y, String text) {
        //    if(!isAlive()) {
        //      start();
        //    }
        this.component = component;
        this.tx = x;
        this.ty = y;
        this.text = text;
        if (component == null || text == null) {
            dismissToolTip();
        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                synchronized (this) {
                    if (component != null) {
                        if (tipWindow == null || lastSharedFrame == null
                                        || lastSharedFrame != ToolTipManager.getInstance().getSharedFrame()) {
                            lastSharedFrame = ToolTipManager.getInstance().getSharedFrame();
                            Frame f = UIUtil.getFrameAncestor(component);
                            if (tipWindow != null) {
                                tipWindow.dispose();
                            }
                            f = f == null ? ToolTipManager.getInstance().getSharedFrame() : f;
                            tipWindow = new TipWindow(f);
                        }
                        tipWindow.popup(tx, ty, component, text);
                        component = null;
                        text = null;
                    } else {
                        if (tipWindow != null && tipWindow.isOutOfDate()) {
                            dismissToolTip();
                        }
                    }
                }
            } catch (InterruptedException ie) {

            }
        }
    }

    /**
     *  
     */
    public void dismissToolTip() {
        if (tipWindow != null) {
            tipWindow.dismiss();
        }
    }
}