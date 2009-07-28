package com.sshtools.ui.swing.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractWizardModel implements WizardModel {

    private List listeners = new ArrayList();

    public void addWizardModelListener(WizardModelListener listener) {
        listeners.add(listener);        
    }

    public void removeWizardModelListener(WizardModelListener listener) {
        listeners.remove(listener);                
    }
    
    protected void firePageAdded(WizardPage page) {
        for(Iterator i = listeners.iterator(); i.hasNext(); ) {
            ((WizardModelListener)i.next()).pageAdd(page);
        }
    }
    
    protected void firePageRemoved(WizardPage page) {
        for(Iterator i = listeners.iterator(); i.hasNext(); ) {
        	((WizardModelListener)i.next()).pageRemoved(page);
        }
    }
    
    protected void firePageChanged(WizardPage oldPage, WizardPage newPage) {
        for(Iterator i = listeners.iterator(); i.hasNext(); ) {
        	((WizardModelListener)i.next()).pageChanged(oldPage, newPage);
        }
    }
    
}
