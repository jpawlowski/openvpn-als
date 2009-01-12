package com.sshtools.ui.swing.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sshtools.ui.swing.UIUtil;

public class WizardDialog extends JDialog {
    
    private boolean finished = false;
    
    public WizardDialog(Frame parent, String title, WizardModel model, JPanel sidePanel, Insets insets, boolean modal, GraphicsConfiguration config) {
        super(parent, title, modal, config);
        init(model, sidePanel, insets);
    }
    
    public WizardDialog(Dialog parent, String title, WizardModel model, JPanel sidePanel, Insets insets, boolean modal, GraphicsConfiguration config) {
        super(parent, title, modal, config);
        init(model, sidePanel, insets);
    }

    private void init(WizardModel model, JPanel sidePanel, Insets insets) {
        JWizard wizard = new JWizard(model);
        if(insets != null) {
            wizard.setMainInsets(insets);
        }
        if(sidePanel != null) {
            wizard.setSidePanel(sidePanel);
        }
        setLayout(new BorderLayout());
        add(wizard, BorderLayout.CENTER);
        pack();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                canceled();
            }
        });
        wizard.addWizardListener(new WizardListener() {
            public void wizardCanceled() {
                canceled();
            }

            public void wizardFinished() {
                finished();
            }
            
        });
        UIUtil.positionComponent(UIUtil.CENTER, this);
        setResizable(false);
    }
    
    public boolean isFinished() {
        return finished;
    }

    private void canceled() {
        dispose();
    }
    
    private void finished() {
        finished = true;
        dispose();
    }
    
    public static boolean wizardDialog(Component parent, String title, WizardModel model, JPanel sidePanel, Insets insets, boolean modal) {
        WizardDialog dialog = null;
        Window window = null;
        if(parent != null) {
            if(parent instanceof Frame || parent instanceof Dialog) {
                window = (Window)parent;
            }
            else {
                window = SwingUtilities.getWindowAncestor(parent);
            }
            if(window instanceof Dialog) {
                dialog = new WizardDialog((Dialog)window, title, model, sidePanel, insets, modal, window.getGraphicsConfiguration());
            }
            else {
                dialog = new WizardDialog((Frame)window, title, model, sidePanel, insets, modal, window.getGraphicsConfiguration());
            }
        }
        else {
            dialog = new WizardDialog((Frame)null, title, model, sidePanel, insets, modal, null);
        }
        dialog.setVisible(true);
        return dialog.isFinished();
    }
}
