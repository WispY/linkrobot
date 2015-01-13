package com.wispy.linkrobot.gui.search;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class StatusLabel extends JLabel {
    public static final Logger LOG = Logger.getLogger(StatusLabel.class);

    public StatusLabel() {
        setText("Initializing...");
    }
}