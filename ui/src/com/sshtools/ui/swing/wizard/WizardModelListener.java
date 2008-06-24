package com.sshtools.ui.swing.wizard;

public interface WizardModelListener {
    public void pageAdd(WizardPage page);
    public void pageRemoved(WizardPage page);
    public void pageChanged(WizardPage newPage, WizardPage oldPage);

}
