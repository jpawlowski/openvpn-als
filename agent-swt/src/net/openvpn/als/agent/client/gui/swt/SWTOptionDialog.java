package net.openvpn.als.agent.client.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SWTOptionDialog extends Dialog {
	private Object message;
	private String okText;
	private String cancelText;
	private boolean ok;
	private Shell shell;
	private boolean open;

	/**
	 * Constructor
	 */
	public SWTOptionDialog(Shell parent, int style, String okText, String cancelText, String title, Object message) {
		// Let users override the default styles
		super(parent, style);
		this.okText = okText;
		this.cancelText = cancelText;
		setText(title);
		setMessage(message);

		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
	}

	/**
	 * Gets the message. May be a string or {@link Composite}.
	 * 
	 * @return String
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * Sets the message. May be a string or {@link Composite}.
	 * 
	 * @param message the new message
	 */
	public void setMessage(Object message) {
		this.message = message;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return ok
	 */
	public boolean open() {
		createContents(shell);
		shell.pack();
		SWTUtil.center(shell);
		open = true;
		shell.open();
		Display display = getParent().getDisplay();
		while (open) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return ok;
	}
	
	/**
	 * Close the dialog
	 */
	public void close() {
		if(!shell.isDisposed())
			shell.close();
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, true));

		Composite c = new Composite(shell, 0);
		GridLayout gridLayout = new GridLayout ();
		c.setLayout (gridLayout);

		if (message instanceof String) {
			Label label = new Label(shell, SWT.NONE);
			label.setText((String) message);
			GridData data = new GridData ();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			label.setLayoutData (data);
		} else if (message != null) {
            GridData data = new GridData(GridData.FILL_BOTH);
            ((Composite) message).setLayoutData(data);
        }
		
		Composite c2 = new Composite(shell, 0);
		RowLayout rowLayout = new RowLayout ();
		c2.setLayout (rowLayout);

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(c2, SWT.PUSH);
		ok.setText(okText);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				SWTOptionDialog.this.ok = true;
				open = false;
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		if (cancelText != null) {
			Button cancel = new Button(c2, SWT.PUSH);
			cancel.setText(cancelText);
			cancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					open = false;
				}
			});
		}
		
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		c2.setLayoutData(data);
		
		shell.pack();

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}

	public Shell getShell() {
		return shell;
	}
}
