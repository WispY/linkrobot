package com.wispy.linkrobot.gui.search;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class SearchCancelButton extends JButton {
    public static final Logger LOG = Logger.getLogger(SearchCancelButton.class);

    public SearchCancelButton() {
        setText("Cancel");
    }
}