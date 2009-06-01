
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.agent.client.gui.swt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.openvpn.als.agent.client.AgentClientGUI;
import net.openvpn.als.agent.client.PortMonitor;
import net.openvpn.als.agent.client.tunneling.AbstractPortItem;

public class SWTPortMonitor implements PortMonitor {

	private Shell shell;
	private Table table;
	private boolean open;
	private int lastSortColumn = -1;
	private List items;
	private DateFormat dateFormat;
    private Thread updateThread;
    private Button stopButton;

	private static final String[] columnNames = new String[] { Messages.getString("PortModel.type"), Messages.getString("PortModel.name"), Messages.getString("PortModel.localPort"), Messages.getString("PortModel.active"), Messages.getString("PortModel.lastData"), Messages.getString("PortModel.total") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	};
	private static final int[] columnWidths = new int[] { 70, 150, 70, 70, 100, 70 };

	public SWTPortMonitor(final SWTSystemTrayGUI gui) {
		items = new ArrayList();
		dateFormat = SimpleDateFormat.getTimeInstance();
		shell = new Shell(gui.getDisplay(), SWT.RESIZE | SWT.TITLE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		shell.setLayout(gridLayout);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
			}
		});
		shell.setText(Messages.getString("PortMonitor.title")); //$NON-NLS-1$
		shell.setImage(gui.loadImage(SWTSystemTrayGUI.class, "/images/frame-agent.png")); //$NON-NLS-1$
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		shell.setLayoutData(data);

		table = new Table(shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.RESIZE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// for right click
		// table.setMenu(createPopUpMenu());
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
                checkAvailable();
				TableItem[] items = table.getSelection();
				if (items.length > 0) {
					// double click
				}
			}
            
            public void widgetSelected(SelectionEvent e) {
                checkAvailable();
            }
		});
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnNames[i]);
			column.setWidth(columnWidths[i]);
			final int columnIndex = i;
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					sort(columnIndex);
				}
			});
		}

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
			| GridData.VERTICAL_ALIGN_FILL
			| GridData.GRAB_VERTICAL);
		data.horizontalSpan = 3;
		data.verticalSpan = 3;
		data.heightHint = 300;
		data.widthHint = 480;
		table.setLayoutData(data);

		stopButton = new Button(shell, SWT.PUSH);
		stopButton.setText(Messages.getString("PortMonitor.stop"));
		data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		stopButton.setLayoutData(data);

		final Button closeButton = new Button(shell, SWT.PUSH);
		closeButton.setText(Messages.getString("PortMonitor.close"));

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == stopButton) {
					if (gui.confirm(AgentClientGUI.WARNING,
						"Yes",
						"No",
						Messages.getString("PortMonitor.close.title"),
						Messages.getString("PortMonitor.close.text"))) {
						for (Enumeration en = getSelectedPorts().elements(); en.hasMoreElements();) {
							AbstractPortItem t = (AbstractPortItem) en.nextElement();
							t.stop();
						}
					}
				} else {
					setVisible(false);
				}
			}
		};
		stopButton.addListener(SWT.Selection, listener);
		closeButton.addListener(SWT.Selection, listener);

		shell.pack();
        
        checkAvailable();
	}

	/**
	 * Get a list of {@link AbstractPortItem} objects that are currently
	 * selected.
	 * 
	 * @return selected ports
	 */
	public Vector getSelectedPorts() {
		int[] r = table.getSelectionIndices();
		Vector v = new Vector();
		for (int i = 0; i < r.length; i++) {
			v.addElement(items.get(r[i]));
		}
		return v;
	}

	public void addPortItem(final AbstractPortItem portItem) {
		synchronized(items) {
			items.add(portItem);
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					new TableItem(table, SWT.NONE);
					update(items.size() - 1, portItem);
                    checkAvailable();
				}
			});
		}
	}

	public int getIndexForId(int id) {
		synchronized(items) {
			int idx = 0;
			for (Iterator i = items.iterator(); i.hasNext();) {
				AbstractPortItem api = (AbstractPortItem) i.next();
				if (api.getConfiguration().getId() == id) {
					return idx;
				}
				idx++;
			}
			return -1;
		}
	}

	public AbstractPortItem getItemAt(int idx) {
		return (AbstractPortItem) items.get(idx);
	}

	public void removeItemAt(final int idx) {
		items.remove(idx);
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				table.remove(idx, idx);
                checkAvailable();
			}
		});
	}

	public void updateItemAt(final int idx) {
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				update(idx, getItemAt(idx));
                checkAvailable();
			}
		});
	}

	void update(int idx, AbstractPortItem portItem) {
		String[] labels = new String[] { portItem.getType(),
			portItem.getName(),
			String.valueOf(portItem.getLocalPort()),
			String.valueOf(portItem.getActiveTunnelCount()),
			dateFormat.format(new Date(portItem.getDataLastTransferred())),
			String.valueOf(portItem.getTotalTunnelCount()) };
		table.getItem(idx).setText(labels);
		table.redraw();
	}

	public boolean isVisible() {
		return open && shell.isVisible();
	}

	public void setVisible(final boolean visible) {
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (!open) {
					shell.open();
					open = true;
				} else {
					shell.setVisible(visible);
				}
			}
		});
        if(visible && updateThread == null) {
            updateThread = new Thread() {
                public void run() {
                    while(updateThread != null) {
                        try {
                            Thread.sleep(1000);
                        }
                        catch(Exception e) {                            
                        }
                		shell.getDisplay().asyncExec(new Runnable() {
                			public void run() {
                				synchronized(items) {
	                				for(int i = 0 ; i < items.size(); i++) {
	                					update(i, ((AbstractPortItem)items.get(i)));
	                				}
                				}
                			}
                		});
                    }
                }
            };
            updateThread.start();
        }
        else if(!visible && updateThread != null){
            updateThread = null;
        }
	}
    
    private void checkAvailable() {
        stopButton.setEnabled(table.getSelectionCount() > 0);
    }

	private void sort(int column) {
		if (table.getItemCount() <= 1)
			return;

		TableItem[] items = table.getItems();
		String[][] data = new String[items.length][table.getColumnCount()];
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				data[i][j] = items[i].getText(j);
			}
		}

		Arrays.sort(data, new RowComparator(column));

		if (lastSortColumn != column) {
			table.setSortColumn(table.getColumn(column));
			table.setSortDirection(SWT.DOWN);
			for (int i = 0; i < data.length; i++) {
				items[i].setText(data[i]);
			}
			lastSortColumn = column;
		} else {
			// reverse order if the current column is selected again
			table.setSortDirection(SWT.UP);
			int j = data.length - 1;
			for (int i = 0; i < data.length; i++) {
				items[i].setText(data[j--]);
			}
			lastSortColumn = -1;
		}

	}

	/**
	 * To compare entries (rows) by the given column
	 */
	private class RowComparator implements Comparator {
		private int column;

		/**
		 * Constructs a RowComparator given the column index
		 * 
		 * @param col The index (starting at zero) of the column
		 */
		public RowComparator(int col) {
			column = col;
		}

		/**
		 * Compares two rows (type String[]) using the specified column entry.
		 * 
		 * @param obj1 First row to compare
		 * @param obj2 Second row to compare
		 * @return negative if obj1 less than obj2, positive if obj1 greater
		 *         than obj2, and zero if equal.
		 */
		public int compare(Object obj1, Object obj2) {
			String[] row1 = (String[]) obj1;
			String[] row2 = (String[]) obj2;

			return row1[column].compareTo(row2[column]);
		}
	}

	public void dispose() {
		if (!shell.isDisposed()) {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					shell.dispose();
				}
			});
		}
	}

}