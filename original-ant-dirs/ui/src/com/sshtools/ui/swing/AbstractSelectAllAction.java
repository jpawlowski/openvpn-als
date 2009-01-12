package com.sshtools.ui.swing;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;


public abstract class AbstractSelectAllAction extends AbstractAction {
    public AbstractSelectAllAction() {
        putValue(Action.SMALL_ICON, new EmptyIcon(16, 16));
        putValue(Action.NAME, "Select All");
        putValue(Action.SHORT_DESCRIPTION, "Select All");
        putValue(Action.LONG_DESCRIPTION, "Select all items in the context");
        putValue(Action.MNEMONIC_KEY, new Integer('a'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
    }
}