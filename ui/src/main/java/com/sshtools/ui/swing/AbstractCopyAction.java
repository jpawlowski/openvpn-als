package com.sshtools.ui.swing;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;


public abstract class AbstractCopyAction extends AbstractAction {
    public AbstractCopyAction() {
        putValue(Action.NAME, "Copy");
        putValue(Action.SMALL_ICON, new ResourceIcon(AbstractCopyAction.class, "/images/actions/copy-16x16.png"));
        putValue(Action.SHORT_DESCRIPTION, "Copy");
        putValue(Action.LONG_DESCRIPTION, "Copy the selection from the text and place it in the clipboard");
        putValue(Action.MNEMONIC_KEY, new Integer('c'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
    }
}