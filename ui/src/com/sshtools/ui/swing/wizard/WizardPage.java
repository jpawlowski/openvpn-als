package com.sshtools.ui.swing.wizard;

import java.awt.Component;

public interface WizardPage {
    public String getPageName();
    public Component getView();
    public String getTitle();
    public void shown(JWizard wizard);
    public void added(WizardModel model);
    public void validatePage() throws WizardPageValidateException;
}
