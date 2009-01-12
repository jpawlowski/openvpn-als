package com.sshtools.ui.swing.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.FolderBar;
import com.sshtools.ui.swing.ResourceIcon;

public class JWizard extends JPanel implements WizardModelListener {
    
    private WizardModel model;
    private FolderBar titleBar;
    private JPanel pageContainer;
    private CardLayout pageCardLayout;
    private JPanel sidePanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private AppAction nextAction, previousAction, finishAction, cancelAction;
    private JPanel southPanel;
    private JButton nextButton, finishButton, previousButton;
    private List listeners = new ArrayList();
    private JLabel errorLabel;

    public JWizard(WizardModel model) {
        super(new BorderLayout());
        createActions();
        createComponents();
        buildMainPanel();
        setModel(model);
    }
    
    public void setModel(WizardModel model) {
        if(this.model != null) {
            this.model.removeWizardModelListener(this);
        }
        this.model = model;
        this.model.addWizardModelListener(this);
        pageContainer.invalidate();
        rebuildModel();
        pageContainer.validate();
        pageContainer.repaint();
        if(model.currentPage() != null) {
            showPage(model.currentPage());
        }
    }
    
    public WizardModel getModel() {
        return model;
    }
    
    public void setSidePanel(JPanel sidePanel) {
        this.sidePanel = sidePanel;
        topPanel.invalidate();
        rebuildTopPanel();
        topPanel.validate();
        topPanel.repaint();
    }

    public void addWizardListener(WizardListener listener) {
        listeners.add(listener);        
    }

    public void removeWizardListener(WizardListener listener) {
        listeners.remove(listener);                
    }
    
    protected void fireWizardCanceled() {
        for(Iterator i = listeners.iterator(); i.hasNext(); ) {
            ((WizardListener)i.next()).wizardCanceled();
        }
    }
    
    protected void fireWizardFinished() {
        for(Iterator i = listeners.iterator(); i.hasNext(); ) {
        	((WizardListener)i.next()).wizardFinished();
        }
    }
    
    
    void createActions() {
        nextAction = new NextAction();
        previousAction = new PreviousAction();
        finishAction = new FinishAction();
        cancelAction = new CancelAction();
    }
    
    void showPage(WizardPage page) {
        setErrorText(null);
        titleBar.setText(page.getTitle());
        pageCardLayout.show(pageContainer, page.getPageName());
        setAvailableActions();
        page.shown(this);
    }
    
    void setAvailableActions() {
        nextAction.setEnabled(model != null && model.hasNextPage());
        nextButton.setVisible(nextAction.isEnabled());
        finishButton.setVisible(model != null && !model.hasNextPage());
        previousAction.setEnabled(model != null && model.hasPreviousPage());
        nextButton.setDefaultCapable(nextButton.isVisible());
        previousButton.setDefaultCapable(!nextButton.isVisible());
    }

    void buildMainPanel() {
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(titleBar, BorderLayout.NORTH);
        
        JPanel pageArea = new JPanel(new BorderLayout());
        pageArea.add(errorLabel, BorderLayout.NORTH);
        pageArea.add(pageContainer, BorderLayout.CENTER);
        
        centerPanel.add(pageArea, BorderLayout.CENTER);
        
        topPanel = new JPanel(new BorderLayout());
        rebuildTopPanel();
        
        add(topPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    public void setMainInsets(Insets insets) {
        pageContainer.setBorder(insets == null ? null : BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
    }
    
    void createComponents() {
        titleBar = new FolderBar();
        titleBar.setFont(titleBar.getFont().deriveFont(Font.BOLD).deriveFont((float)(titleBar.getFont().getSize() * 2)));
        pageContainer = new JPanel(pageCardLayout = new CardLayout());
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        buttonBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        buttonBar.add(new JButton(cancelAction));
        buttonBar.add(previousButton = new JButton(previousAction));
        buttonBar.add(nextButton = new JButton(nextAction));
        buttonBar.add(finishButton = new JButton(finishAction));
        southPanel = new JPanel(new BorderLayout());
        southPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
        southPanel.add(buttonBar, BorderLayout.SOUTH);
        errorLabel = new JLabel(new ResourceIcon(getClass(), "/images/error-16x16.png"));
        errorLabel.setHorizontalAlignment(JLabel.LEFT);
        errorLabel.setVisible(false);
    }
    
    void rebuildTopPanel() {
        topPanel.removeAll();
        topPanel.add(centerPanel, BorderLayout.CENTER);
        if(sidePanel != null) {
            topPanel.add(sidePanel, BorderLayout.WEST);
        }
    }
    
    void rebuildModel() {
        pageContainer.removeAll();
        for(Iterator i = model.getPages().iterator(); i.hasNext(); ) {
            addPage((WizardPage)i.next());
        }
    }
    
    void setErrorText(String errorText) {
        if(errorText == null) {
            errorLabel.setVisible(false);
        }
        else {
            errorLabel.setVisible(true);
            errorLabel.setText(errorText);
        }
    }

    private void addPage(WizardPage page) {
        pageContainer.add(page.getView(), page.getPageName());
    }

    public void pageAdd(WizardPage page) {
        addPage(page);
    }

    public void pageChanged(WizardPage newPage, WizardPage oldPage) {
        showPage(newPage);
    }

    public void pageRemoved(WizardPage page) {
        // TODO
    }

    class NextAction extends AppAction {

        public NextAction() {
            putValue(Action.NAME, "Next");
            putValue(Action.SMALL_ICON, new ResourceIcon(NextAction.class, "/images/next-16x16.png"));
            putValue(Action.SHORT_DESCRIPTION, "Next Page");
            putValue(Action.LONG_DESCRIPTION, "Move to the next page");
            putValue(Action.MNEMONIC_KEY, new Integer('n'));
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                model.nextPage();
                setErrorText(null);
            } catch (WizardPageValidateException e) {
                Toolkit.getDefaultToolkit().beep();
                setErrorText(e.getMessage());
            }
        }
    }
    
    class PreviousAction extends AppAction {

        public PreviousAction() {
            putValue(Action.NAME, "Previous");
            putValue(Action.SMALL_ICON, new ResourceIcon(PreviousAction.class, "/images/previous-16x16.png"));
            putValue(Action.SHORT_DESCRIPTION, "Previous Page");
            putValue(Action.LONG_DESCRIPTION, "Move to the previous page");
            putValue(Action.MNEMONIC_KEY, new Integer('p'));
        }

        public void actionPerformed(ActionEvent evt) {
            model.previousPage();
        }
    }
    
    class FinishAction extends AppAction {

        public FinishAction() {
            putValue(Action.NAME, "Finish");
            putValue(Action.SMALL_ICON, new ResourceIcon(FinishAction.class, "/images/finish-16x16.png"));
            putValue(Action.SHORT_DESCRIPTION, "Finish the wizard");
            putValue(Action.LONG_DESCRIPTION, "Finish the wizard");
            putValue(Action.MNEMONIC_KEY, new Integer('f'));
        }

        public void actionPerformed(ActionEvent evt) {
            fireWizardFinished();
        }
    }
    
    class CancelAction extends AppAction {

        public CancelAction() {
            putValue(Action.NAME, "Cancel");
            putValue(Action.SMALL_ICON, new ResourceIcon(CancelAction.class, "/images/cancel-16x16.png"));
            putValue(Action.SHORT_DESCRIPTION, "Cancel the wizard");
            putValue(Action.LONG_DESCRIPTION, "Cancel the wizard");
            putValue(Action.MNEMONIC_KEY, new Integer('c'));
        }

        public void actionPerformed(ActionEvent evt) {
            fireWizardCanceled();
        }
    }
}
