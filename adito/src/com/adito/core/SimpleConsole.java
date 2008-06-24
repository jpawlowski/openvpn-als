package com.adito.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


public class SimpleConsole extends JPanel {
    private final static int MAX_BUFFERED_LINES = 1000;
    private JTextPane textPane;
    private int maxLength;
    private ConsoleWriter consoleWriter;

    public SimpleConsole() {
        super(new BorderLayout());
        consoleWriter = new ConsoleWriter();
        //
        textPane = new JTextPane() {
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }

            public void setSize(Dimension d) {
                if (d.width < getParent().getSize().width) {
                    d.width = getParent().getSize().width;
                }
                super.setSize(d);
            }
        };
        textPane.setEditable(false);
        textPane.setFont(new Font("Monospaced", Font.BOLD, 10));
        JScrollPane textScroller = new JScrollPane(textPane) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 140);
            }
        };
        //
        add(textScroller, BorderLayout.CENTER);
        maxLength = 131072;
        checkMaxLength(0);
    }

    private void checkMaxLength(int toAdd) {
        try {
            int z = textPane.getDocument().getLength() + toAdd;
            if (z > maxLength) {
                textPane.getDocument().remove(0, z - maxLength);
            }
        } catch (Throwable ex) {
        }
    }

    public void writeMessage(boolean ok, final String text) {
        consoleWriter.addMessage(ok, text);
    }

    static class WriteLock {
    }

    class ConsoleWriter extends Thread {
        private List consoleBuffer;

        ConsoleWriter() {
            super("ConsoleWriter");
            setDaemon(true);
            consoleBuffer = new ArrayList();
            start();
        }

        public void addMessage(boolean ok, String text) {
            synchronized (consoleBuffer) {
                if (consoleBuffer.size() > MAX_BUFFERED_LINES) {
                    try {
                        consoleBuffer.wait();
                    } catch (InterruptedException e) {
                    }
                }
                consoleBuffer.add(new ConsoleWriterWrapper(ok, text));
            }
            synchronized (this) {
                notifyAll();
            }
        }

        public void run() {
            StringBuffer writeBuffer = new StringBuffer();
            SimpleAttributeSet attr = null;
            int l = 0;
            while (true) {
                synchronized (this) {
                    try {
                        wait(2000);
                    } catch (InterruptedException ie) {
                    }
                }
                while (consoleBuffer.size() > 0) {
                    ConsoleWriterWrapper w = (ConsoleWriterWrapper) consoleBuffer.get(0);
                    String txt = w.text;
                    if (!txt.endsWith("\n")) {
                        txt = txt + "\n";
                    }
                    if (writeBuffer.length() != 0) {
                        writeText(writeBuffer.toString(), attr);
                        writeBuffer.setLength(0);
                        l = 0;
                    }
                    attr = new SimpleAttributeSet();
                    if (w.ok)
                        StyleConstants.setForeground(attr, Color.green.darker());
                    else
                        StyleConstants.setForeground(attr, Color.red.darker());
                    writeBuffer.append(txt);
                    l++;
                    if (l == 5) {
                        writeText(writeBuffer.toString(), attr);
                        writeBuffer.setLength(0);
                        l = 0;
                    }
                    synchronized (consoleBuffer) {
                        consoleBuffer.remove(0);
                        if (consoleBuffer.size() < (MAX_BUFFERED_LINES / 2)) {
                            consoleBuffer.notifyAll();
                        }
                    }
                }
                if (writeBuffer.length() > 0) {
                    writeText(writeBuffer.toString(), attr);
                    writeBuffer.setLength(0);
                    l = 0;
                }
            }
        }

        public void writeText(final String writeTxt, final AttributeSet attr) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        try {
                            checkMaxLength(0);
                            textPane.getDocument().insertString(textPane.getDocument().getLength(), writeTxt, attr);
                            textPane.scrollRectToVisible(textPane.getVisibleRect());
                            textPane.setCaretPosition(textPane.getDocument().getLength());
                        } catch (Throwable ex) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }

        class ConsoleWriterWrapper {
            boolean ok;
            String text;

            ConsoleWriterWrapper(boolean ok, String text) {
                this.ok = ok;
                this.text = text;
            }
        }
    }
}