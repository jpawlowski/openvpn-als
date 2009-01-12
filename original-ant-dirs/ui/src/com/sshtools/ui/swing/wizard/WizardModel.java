package com.sshtools.ui.swing.wizard;

import java.util.List;

public interface WizardModel {
    public WizardPage nextPage() throws WizardPageValidateException;
    public boolean hasNextPage();
    public boolean hasPreviousPage();
    public WizardPage currentPage();  
    public WizardPage previousPage();
    public void gotoPage(WizardPage page);
    public List getPages();
    public void addWizardModelListener(WizardModelListener listener);
    public void removeWizardModelListener(WizardModelListener listener);
    public WizardPage getPage(String name);
}
