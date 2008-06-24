package com.adito.agent.client;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.maverick.http.HttpAuthenticator;
import com.adito.agent.client.tunneling.AbstractPortItem;

public class DummyGUI implements AgentClientGUI {

    public void init(Agent agent) {
    }

    public void showIdle() {
    }

    public void showDisconnected() {
    }

    public void showTx() {
    }

    public void showRx() {
    }

    public void showTxRx() {
    }

    public void setInfo(String info) {
    }

    public boolean confirm(int dialogType, String okText, String cancelText, String title, String message) {
        return true;
    }

    public boolean error(String okText, String cancelText, String title, String message, Throwable ex) {
        return true;
    }

    public void addFileMenuItem(AgentAction action) {
    }

    public void addFileMenuSeparator() {
    }

    public void popup(ActionCallback callback, String message, String title, String imageName, int timeout) {
    }

    public TaskProgress createTaskProgress(String message, String note, long maxValue, boolean allowCancel) {
        return new DummyTaskProgress();
    }

    public PortMonitor getPortMonitor() {
        return new DummyPortMonitor();
    }

    public Console getConsole() {
        return new DummyConsole();
    }

    public boolean promptForCredentials(boolean proxy, HttpAuthenticator authenticator) {
        return true;
    }

	public void dispose() {		
	}
	
	public boolean isMenuExists(String name) {
		return false;
	}

	public void addMenu(String name) {		
	}
	
	public void clearMenu(String name) {		
	}

	public void removeMenu(String menu) {		
	}

	public void addMenuItem(String parentName, AgentAction action) {		
	}

	public void addMenuSeperator(String name) {		
	}

	public void openBrowser(String path) {		
	}
    
    class DummyConsole extends Console {

        public void show() {            
        }

        public void write(int b) throws IOException {            
        }

		public void dispose() {			
		}
        
    }
    
    class DummyPortMonitor implements PortMonitor {
        
        private Vector portItems = new Vector();
        private boolean visible;

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) { 
            this.visible = visible;
        }

        public void addPortItem(AbstractPortItem portItem) {
            portItems.addElement(portItem);
        }

        public synchronized int getIndexForId(int id) {
            int idx = 0;
            for(Enumeration e = portItems.elements(); e.hasMoreElements(); ) {
                AbstractPortItem api = ((AbstractPortItem)e.nextElement());
                if(api.getConfiguration().getId() == id) {
                    return idx;
                }
                idx++;
            }
            return -1;
        }

        public AbstractPortItem getItemAt(int idx) {
            return (AbstractPortItem)portItems.elementAt(idx);
        }

        public void removeItemAt(int idx) {
            portItems.removeElementAt(idx);
        }

        public void updateItemAt(int idx) {
        }

		public void dispose() {			
		}
        
    }

    class DummyTaskProgress implements TaskProgress {

        public void updateValue(long value) {
        }

        public void setMessage(String string) {
        }

        public void dispose() {
        }

    }

}
