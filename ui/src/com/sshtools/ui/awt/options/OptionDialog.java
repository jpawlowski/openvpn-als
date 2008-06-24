package com.sshtools.ui.awt.options;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sshtools.ui.awt.ImageCanvas;
import com.sshtools.ui.awt.MultilineLabel;
import com.sshtools.ui.awt.UIUtil;

public class OptionDialog extends Panel {

    public static String INFORMATION_ICON = "/images/information-48x48.png"; //$NON-NLS-1$
    public static String WARNING_ICON = "/images/warning-48x48.png"; //$NON-NLS-1$
    public static String QUESTION_ICON = "/images/question-48x48.png"; //$NON-NLS-1$
    public static String ERROR_ICON = "/images/error-48x48.png"; //$NON-NLS-1$


    public static final int INFORMATION = 0;

    public static final int QUESTION = 1;

    public static final int WARNING = 2;

    public static final int ERROR = 3;

    public static final int UNCATEGORISED = 99;

    public static boolean useDialogForPrompt = true;

    public static final Option CHOICE_YES = new Option(Messages.getString("OptionDialog.yes")); //$NON-NLS-1$

    public static final Option CHOICE_SHOW = new Option(Messages.getString("OptionDialog.show")); //$NON-NLS-1$

    public static final Option CHOICE_HIDE = new Option(Messages.getString("OptionDialog.hide")); //$NON-NLS-1$

    public static final Option CHOICE_NO = new Option(Messages.getString("OptionDialog.no")); //$NON-NLS-1$

    public static final Option CHOICE_OK = new Option(Messages.getString("OptionDialog.ok")); //$NON-NLS-1$

    public static final Option CHOICE_YES_TO_ALL = new Option(Messages.getString("OptionDialog.yesToAll")); //$NON-NLS-1$

    public static final Option CHOICE_CANCEL = new Option(Messages.getString("OptionDialog.cancel")); //$NON-NLS-1$

    public static final Option CHOICES_YES_NO[] = { CHOICE_YES, CHOICE_NO };

    public static final Option CHOICES_OK_CANCEL[] = { CHOICE_OK, CHOICE_CANCEL };

    public static final Option CHOICES_OK[] = { CHOICE_OK };
    
    private Object lock_;
    private boolean dismissed;
    private Option selected;
    private OptionCallback callback;
    private Dialog dialog;

    public OptionDialog(int type, Object text, Option choices[], OptionCallback callback) {
        this(type, text, choices, callback, null);
    }
    
    public OptionDialog(int type, Object text, Option choices[], OptionCallback callback, Component buttonBarAccessory) {
        super(new BorderLayout());
        lock_ = new Object();
        dismissed = false;
        this.callback = callback;
        Panel titlePanel = new Panel(new FlowLayout(0));
        Image icon = null;
        switch (type) {
        case 0:
            // '\0'
            icon = UIUtil.loadImage(getClass(), INFORMATION_ICON);
            break;
        case 2:
            // '\002'
            icon = UIUtil.loadImage(getClass(), WARNING_ICON);
            break;
        case 1:
            // '\001'
            icon = UIUtil.loadImage(getClass(), QUESTION_ICON);
            break;
        case 99:
            break;
        default:
            icon = UIUtil.loadImage(getClass(), ERROR_ICON);
            break;
        }
        if (icon != null) {
            UIUtil.waitFor(icon, this);
        }
        Panel textPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        if (text instanceof Component) {
            UIUtil.gridBagAdd(textPanel, (Component) text, gbc, 0);
        } else {
            StringTokenizer st = new StringTokenizer(String.valueOf(text), "\n"); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String n = st.nextToken().trim();
                if (n.length() > 0) {
                    UIUtil.gridBagAdd(textPanel, new Label(n), gbc, 0);
                }
            }
        }
        add(textPanel, "Center"); //$NON-NLS-1$
        Panel choicePanel = new Panel(new FlowLayout(buttonBarAccessory == null ? FlowLayout.CENTER : FlowLayout.RIGHT));
        OptionWrapper choice = new OptionWrapper();
        choice.idx = -1;
        for (int i = 0; choices != null && i < choices.length; i++) {
            //      ImageButton b = new ImageButton(choices[i].getIcon(), choices[i]
            //          .getText(), choices[i].getText());
            Button b = new Button(choices[i].getText()) {
                public Dimension getMinimumSize() {
                    return new Dimension(60, super.getMinimumSize().height);
                }

                public Dimension getPreferredSize() {
                    return getMinimumSize();
                }
            };
            choicePanel.add(b);
            b.addActionListener(new BlockingActionListener(choices[i]));
        }
        if (icon != null) {
            add(new ImageCanvas(icon), "West"); //$NON-NLS-1$
        }
        if(buttonBarAccessory != null) {
            Panel p= new Panel(new GridBagLayout());
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.anchor = GridBagConstraints.WEST;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
            gbc2.insets = new Insets(2, 4, 2, 2);
            UIUtil.gridBagAdd(p, buttonBarAccessory, gbc2, GridBagConstraints.RELATIVE);
            gbc2.anchor = GridBagConstraints.EAST;
            gbc2.weightx = 1.0;
            UIUtil.gridBagAdd(p, choicePanel, gbc2, GridBagConstraints.REMAINDER);
            add(p, BorderLayout.SOUTH);
        }
        else {
            add(choicePanel, "South"); //$NON-NLS-1$
        }
    }

    public static void main(String[] args) {
        Frame f = new Frame();
        OptionDialog.error(f, "Test", "This is a test error", new Exception("Test")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void choice(Option choice) {
        selected = choice;
        if (dialog != null)
            dialog.dispose();
        else
            synchronized (lock_) {
                dismissed = true;
                lock_.notify();
            }
    }

    public Option dialogPrompt(Component parent, String title) {
        java.awt.Frame f = parent == null ? null : (parent instanceof Frame ? (Frame) parent : UIUtil.getFrameAncestor(parent));
        if (f == null) {
            f = UIUtil.getSharedFrame();
        }
        dialog = new Dialog(f, title, true);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dialog.dispose();
            }
        });
        dialog.setLayout(new BorderLayout());
        dialog.add(this);
        dialog.pack();
        Dimension s = dialog.getSize();
        dialog.setSize(s.width + 16, s.height + 16);
        UIUtil.positionComponent(UIUtil.CENTER, dialog);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return selected;
    }

    public static char[] promptForAuthentication(Component parent, String title) {
        return promptForAuthentication(parent, title, Messages.getString("OptionDialog.password")); //$NON-NLS-1$
    }

    public static char[] promptForAuthentication(Component parent, String title, String label) {
        String t = promptForText(parent, title, "", null, '*', label); //$NON-NLS-1$
        return t != null ? t.toCharArray() : null;
    }

    public static String promptForText(Component parent, String title, String defaultText, Component accessory, char echoCharacter,
                    String label) {
        return promptForText(parent, title, defaultText, accessory, echoCharacter, label, -1, "South"); //$NON-NLS-1$
    }

    public static String promptForText(Component parent, String title, String defaultText, Component accessory, char echoCharacter,
                    String label, int textWidth, String accesoryPosition) {
        Panel p = new Panel(new BorderLayout());
        Panel middle = new Panel(new FlowLayout());
        middle.add(new Label(label));
        TextField text = new TextField(defaultText, textWidth == -1 ? 15 : textWidth);
        middle.add(text);
        p.add(middle, "Center"); //$NON-NLS-1$
        if (echoCharacter != ' ')
            text.setEchoChar(echoCharacter);
        text.requestFocus();
        final OptionDialog dialog = new OptionDialog(QUESTION, p, CHOICES_OK_CANCEL, null);
        text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dialog.choice(CHOICE_OK);
            }
        });
        if (accessory != null)
            p.add(accessory, accesoryPosition);
        if (dialog.dialogPrompt(parent, title) == CHOICE_OK)
            return text.getText();
        else
            return null;
    }

    public static Option prompt(Component parent, int type, String title, Object text, Option choices[]) {
        return prompt(parent, type, title, text, choices, null);
    }

    public static Option prompt(Component parent, int type, String title, Object text, Option choices[], OptionCallback callback) {
        return prompt(parent, type, title, text, choices, callback, null);
    }
    
    public static Option prompt(Component parent, int type, String title, Object text, Option choices[], OptionCallback callback, Component buttonBarAccesory) {
        return new OptionDialog(type, text, choices, callback, buttonBarAccesory).dialogPrompt(parent, title);
    }

    /**
     * Show an error message with detail
     * 
     * @param parent
     * @param title
     * @param exception
     */
    public static void error(Component parent, String title, Throwable exception) {
        error(parent, title, null, exception);
    }

    /**
     * Show an error message with detail
     * 
     * @param parent
     * @param title
     * @param exception
     */
    public static void error(Component parent, String title, String message) {
        error(parent, title, message, null);
    }

    /**
     * Show an error message with toggable detail
     * 
     * @param parent
     * @param mesg
     * @param title
     * @param exception
     */
    public static void error(Component parent, String title, String mesg, Throwable exception) {
        error(parent, title, mesg, exception, null);
    }

    /**
     * Show an error message with toggable detail
     * 
     * @param parent
     * @param mesg
     * @param title
     * @param exception
     */
    public static Option error(Component parent, String title, String mesg, Throwable exception, Option[] options) {
        boolean details = false;
        if (exception != null) {
            exception.printStackTrace();
        }
        while (true) {
            Vector optlist = new Vector();
            int detailsIdx = -1;
            if (options != null) {
                for (int i = 0; i < options.length; i++) {
                    optlist.addElement(options[i]);
                }
            }
            if (exception != null) {
                detailsIdx = optlist.size();
                if (details) {
                    optlist.addElement(CHOICE_HIDE);
                } else {
                    optlist.addElement(CHOICE_SHOW);
                }
            }
            if (options == null) {
                optlist.addElement(CHOICE_OK);
            }
            Option[] opts = new Option[optlist.size()];
            for (int i = 0; i < optlist.size(); i++) {
                opts[i] = (Option) optlist.elementAt(i);
            }

            StringBuffer buf = new StringBuffer();
            if (mesg != null) {
                buf.append(mesg + "\n"); //$NON-NLS-1$
            }
            appendException(exception, 0, buf, details);
            Component message;
            if (details) {
                MultilineLabel text = new MultilineLabel(buf.toString());
                message = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
                message.setSize(new Dimension(520, 400));
                ((ScrollPane) message).add(text);

            } else {
                message = new MultilineLabel(buf.toString());
            }
            Option opt = prompt(parent, ERROR, title, message, opts);
            if (opt == CHOICE_HIDE || opt == CHOICE_SHOW) {
                details = !details;
            } else {
                if (options != null) {
                    return opt;
                } else {
                    return null;
                }
            }
        }
    }

    protected static void appendException(Throwable exception, int level, StringBuffer buf, boolean details) {
        try {
            if (((exception != null) && (exception.getMessage() != null)) && (exception.getMessage().length() > 0)) {
                if (details && (level > 0)) {
                    buf.append("\n \nCaused by ...\n"); //$NON-NLS-1$
                }
                buf.append(exception.getMessage());
            }
            if (details) {
                if (exception != null) {
                    if ((exception.getMessage() != null) && (exception.getMessage().length() == 0)) {
                        buf.append("\n \nCaused by ..."); //$NON-NLS-1$
                    } else {
                        buf.append("\n \n"); //$NON-NLS-1$
                    }
                }
                StringWriter sw = new StringWriter();
                if (exception != null) {
                    exception.printStackTrace(new PrintWriter(sw));
                }
                buf.append(sw.toString());
            }
            try {
                java.lang.reflect.Method method = exception.getClass().getMethod("getCause", new Class[] {}); //$NON-NLS-1$
                Throwable cause = (Throwable) method.invoke(exception, (Object[]) null);
                if (cause != null) {
                    appendException(cause, level + 1, buf, details);
                }
            } catch (Exception e) {
            }
        } catch (Throwable ex) {
        }
    }

    public static void info(Component parent, String title, String message) {
        prompt(parent, 0, title, message, CHOICES_OK);
    }

    static class OptionWrapper {
        int idx;

        OptionWrapper() {
        }
    }

    class BlockingActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (callback != null && !callback.allowClose(choice))
                return;
            selected = choice;
            if (dialog != null)
                dialog.dispose();
            else
                synchronized (lock_) {
                    dismissed = true;
                    lock_.notify();
                }
        }

        Option choice;

        BlockingActionListener(Option choice) {
            this.choice = choice;
        }
    }
}