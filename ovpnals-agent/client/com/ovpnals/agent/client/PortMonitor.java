package com.ovpnals.agent.client;

import com.ovpnals.agent.client.tunneling.AbstractPortItem;

/**
 * Interface for port monitor  
 */
public interface PortMonitor {
	public boolean isVisible();
	public void setVisible(boolean visible);
	public void addPortItem(AbstractPortItem portItem);
	public int getIndexForId(int id);
	public AbstractPortItem getItemAt(int idx);
	public void removeItemAt(int idx);
	public void updateItemAt(int idx);
	public void dispose();
}