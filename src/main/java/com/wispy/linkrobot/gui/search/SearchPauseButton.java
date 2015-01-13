package com.wispy.linkrobot.gui.search;

import com.wispy.linkrobot.gui.MainFrame;
import com.wispy.linkrobot.process.SearchManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * @author WispY
 */
@Component
public class SearchPauseButton extends JButton {
    public static final Logger LOG = Logger.getLogger(SearchPauseButton.class);

    @Autowired
    private MainFrame mainFrame;

    @Autowired
    private SearchManager searchManager;

    @PostConstruct
    public void init() {
        setText("Pause");

        addActionListener(event -> {
            searchManager.pause();
            SwingUtilities.invokeLater(mainFrame::updateSearchStatus);
        });
    }
}