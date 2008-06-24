/*
 */
package com.sshtools.ui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;

public class SideBarTabber extends JPanel implements Tabber {

    private TabToolBar toolBar;
    private Vector tabs;
    private Vector actions;
    private FolderBar folderBar;
    private JPanel viewPane;
    private CardLayout layout;
    private ScrollingPanel scrolling;

    /**
     * 
     */
    public SideBarTabber() {
        super(new BorderLayout());
        tabs = new Vector();
        actions = new Vector();
        toolBar = new TabToolBar() {
            public int getFixedWidth() {
                return getFixedToolBarWidth();
            }
        };
        scrolling = new ScrollingPanel(toolBar);
        scrolling.setBorder(BorderFactory.createLoweredBevelBorder());
        folderBar = new FolderBar(" ", new EmptyIcon(32, 32));
        folderBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(0, 0, 4, 0)));
        toolBar.setFolderBar(folderBar);

        JPanel centerPane = new JPanel(new BorderLayout());
        centerPane.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        viewPane = new JPanel(layout = new CardLayout());
        centerPane.add(folderBar, BorderLayout.NORTH);
        centerPane.add(viewPane, BorderLayout.CENTER);
        add(scrolling, BorderLayout.WEST);
        add(centerPane, BorderLayout.CENTER);
    }

    public int getFixedToolBarWidth() {
        return toolBar.getFixedWidth();
    }

    public void setFixedToolBarWidth(int fixedWidth) {
        toolBar.setFixedWidth(fixedWidth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#getTabAt(int)
     */
    public Tab getTabAt(int i) {
        return (Tab) tabs.elementAt(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#getComponent()
     */
    public Component getComponent() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#removeAllTabs()
     */
    public void removeAllTabs() {
        tabs.clear();
        actions.clear();
        viewPane.invalidate();
        viewPane.removeAll();
        folderBar.setAction(null);
        toolBar.removeAllActions();
        viewPane.validate();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#applyTabs()
     */
    public void applyTabs() {
        for (int i = 0; i < tabs.size(); i++) {
            try {
                ((Tab) tabs.elementAt(i)).applyTab();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#validateTabs()
     */
    public boolean validateTabs() {
        for (int i = 0; i < tabs.size(); i++) {
            Tab t = (Tab) tabs.elementAt(i);

            if (!t.validateTab()) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#addTab(com.sshtools.appframework.ui.Tab)
     */
    public void addTab(Tab tab) {
        addTab(tab, false);
    }

    /**
     * @param tab
     * @param sel
     */
    public void addTab(Tab tab, boolean sel) {
        String c = (tab.getTabCategory() == null) ? "Unknown" : tab.getTabCategory();
        TabAction action = new TabAction(tab.getTabIcon(),
                        tab.getTabLargeIcon(),
                        tab.getTabTitle(),
                        tab.getTabToolTipText(),
                        tab.getTabMnemonic(),
                        layout,
                        viewPane,
                        c);
        tabs.addElement(tab);
        actions.addElement(action);
        viewPane.add(tab.getTabComponent(), tab.getTabTitle());
        toolBar.addAction(action);

        if (sel || tabs.size() == 1) {
            layout.show(viewPane, tab.getTabTitle());
            folderBar.setAction(action);
            toolBar.setSelectedContext(c);
        }

        scrolling.setAvailableActions();
    }

    // Supporting classes

    class TabAction extends AppAction {
        CardLayout layout;
        JPanel viewPane;

        TabAction(Icon icon, Icon largeIcon, String name, String description, int mnemonic, CardLayout layout, JPanel viewPane,
                  String category) {
            super(name);
            putValue(AppAction.LARGE_ICON, largeIcon);
            putValue(AppAction.SMALL_ICON, icon);
            putValue(AppAction.LONG_DESCRIPTION, description);
            putValue(AppAction.CATEGORY, category);
            this.layout = layout;
            this.viewPane = viewPane;
        }

        public boolean checkAvailable() {
            return true;
        }

        public void actionPerformed(ActionEvent evt) {
        	folderBar.setAction(this);
            layout.show(viewPane, (String) getValue(AppAction.NAME));
        }
    }

    /**
     * @return
     */
    public int getTabCount() {
        return tabs.size();
    }

    public void setSelectedTabClass(Class selectedTabClass) {
        if (selectedTabClass != null) {
            for (Iterator i = tabs.iterator(); i.hasNext();) {
                Tab tab = (Tab) i.next();
                if (tab.getClass().equals(selectedTabClass)) {
                    String c = (tab.getTabCategory() == null) ? "Unknown" : tab.getTabCategory();
                    layout.show(viewPane, tab.getTabTitle());                    ;
                    folderBar.setAction((Action)actions.get(tabs.indexOf(tab)));
                    toolBar.setSelectedContext(c);
                    return;

                }
            }
        }
    }
}