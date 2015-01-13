package com.wispy.linkrobot.gui.search;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class SearchStartButton extends JButton {
    public static final Logger LOG = Logger.getLogger(SearchStartButton.class);

    public SearchStartButton() {
        setText("Start");
    }
}