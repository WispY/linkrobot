package com.wispy.linkrobot.gui.util;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author WispY
 */
public class GhostText implements FocusListener, DocumentListener, PropertyChangeListener {
    public static final Logger LOG = Logger.getLogger(GhostText.class);

    private final JTextField textfield;
    private boolean isEmpty;
    private Color ghostColor;
    private Color foregroundColor;
    private final String ghostText;

    private GhostText(final JTextField textfield, String ghostText) {
        this.textfield = textfield;
        this.ghostText = ghostText;
        this.ghostColor = Color.GRAY;
        textfield.addFocusListener(this);
        registerListeners();
        updateState();
        if (!this.textfield.hasFocus()) {
            focusLost(null);
        }
    }

    public void delete() {
        unregisterListeners();
        textfield.removeFocusListener(this);
    }

    private void registerListeners() {
        textfield.getDocument().addDocumentListener(this);
        textfield.addPropertyChangeListener("foreground", this);
    }

    private void unregisterListeners() {
        textfield.getDocument().removeDocumentListener(this);
        textfield.removePropertyChangeListener("foreground", this);
    }

    public Color getGhostColor() {
        return ghostColor;
    }

    public void setGhostColor(Color ghostColor) {
        this.ghostColor = ghostColor;
    }

    private void updateState() {
        isEmpty = textfield.getText().length() == 0;
        foregroundColor = textfield.getForeground();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                textfield.setText("");
                textfield.setForeground(foregroundColor);
                textfield.setFont(textfield.getFont().deriveFont(Font.PLAIN));
            } finally {
                registerListeners();
            }
        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                textfield.setText(ghostText);
                textfield.setForeground(ghostColor);
                textfield.setFont(textfield.getFont().deriveFont(Font.ITALIC));
            } finally {
                registerListeners();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        updateState();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        updateState();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        updateState();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        updateState();
    }

    public static void ghostify(JTextField field, String ghostText) {
        new GhostText(field, ghostText);
    }
}