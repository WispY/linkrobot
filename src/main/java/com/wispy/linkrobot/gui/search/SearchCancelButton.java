package com.wispy.linkrobot.gui.search;

import com.wispy.linkrobot.gui.MainFrame;
import com.wispy.linkrobot.process.SearchManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class SearchCancelButton extends JButton {
    public static final Logger LOG = Logger.getLogger(SearchCancelButton.class);

    @Autowired
    private MainFrame mainFrame;

    @Autowired
    private SearchManager searchManager;

    @PostConstruct
    public void init() {
        setText("Cancel");

        addActionListener(event -> {
            searchManager.stop();
            SwingUtilities.invokeLater(mainFrame::updateSearchStatus);
        });
    }
}