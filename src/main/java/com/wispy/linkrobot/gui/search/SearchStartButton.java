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
public class SearchStartButton extends JButton {
    public static final Logger LOG = Logger.getLogger(SearchStartButton.class);

    @Autowired
    private MainFrame mainFrame;

    @Autowired
    private SearchManager searchManager;

    @Autowired
    private UrlTextField urlTextField;

    @PostConstruct
    public void init() {
        setText("Start");

        addActionListener(event -> {
            String url = urlTextField.getText();
            if (StringUtils.isEmpty(url)) {
                JOptionPane.showMessageDialog(mainFrame, "Please, enter a URL to start search", "Input required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            searchManager.start(urlTextField.getText());
            SwingUtilities.invokeLater(mainFrame::updateSearchStatus);
        });
    }

}