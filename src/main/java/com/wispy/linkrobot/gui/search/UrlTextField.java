package com.wispy.linkrobot.gui.search;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class UrlTextField extends JTextField {
    public static final Logger LOG = Logger.getLogger(UrlTextField.class);

    public UrlTextField() {
        setToolTipText("Enter base URL for search, e.g. https://example.com/application/");
    }
}