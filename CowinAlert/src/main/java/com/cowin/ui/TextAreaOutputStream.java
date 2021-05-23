package com.cowin.ui;

import javax.swing.*;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();

    public TextAreaOutputStream(final JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) {
        if (b == '\r')
            return;
        if (b == '\n') {
            final String text = sb + "\n";
            SwingUtilities.invokeLater(() -> textArea.append(text));
            sb.setLength(0);
            return;
        }
        sb.append((char) b);
    }
}