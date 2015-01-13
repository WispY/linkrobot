package com.wispy.linkrobot.gui;

import com.wispy.linkrobot.gui.search.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * @author WispY
 */
@Component
public class MainFrame extends JFrame {
    public static final Logger LOG = Logger.getLogger(MainFrame.class);

    private static final int INNER_SPACING = 5;
    private static final int OUTER_SPACING = 5;

    private JPanel searchPanel;
    private JPanel statusPanel;
    private JPanel resultPanel;

    @Autowired
    private UrlLabel urlLabel;

    @Autowired
    private UrlTextField urlTextField;

    @Autowired
    private SearchStartButton searchStartButton;

    @Autowired
    private SearchPauseButton searchPauseButton;

    @Autowired
    private SearchCancelButton searchCancelButton;

    @PostConstruct
    public void initAll() {
        initFrame();
        initPanels();
        initContent();
    }

    public void initFrame() {
        setTitle("Link Robot");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(300, 200));
    }

    public void initPanels() {
        setLayout(new GridBagLayout());

        searchPanel = new JPanel();
        statusPanel = new JPanel();
        resultPanel = new JPanel();

        GridBagConstraints panelPlacement = new GridBagConstraints();
        panelPlacement.anchor = GridBagConstraints.NORTH;
        panelPlacement.weightx = 1;
        panelPlacement.gridwidth = 1;

        panelPlacement.fill = GridBagConstraints.HORIZONTAL;
        panelPlacement.insets = new Insets(OUTER_SPACING, OUTER_SPACING, INNER_SPACING, OUTER_SPACING);
        panelPlacement.gridy = 0;
        add(searchPanel, panelPlacement);

        panelPlacement.insets = new Insets(INNER_SPACING, OUTER_SPACING, INNER_SPACING, OUTER_SPACING);
        panelPlacement.gridy = 1;
        add(statusPanel, panelPlacement);

        panelPlacement.fill = GridBagConstraints.BOTH;
        panelPlacement.insets = new Insets(INNER_SPACING, OUTER_SPACING, OUTER_SPACING, OUTER_SPACING);
        panelPlacement.gridy = 2;
        panelPlacement.weighty = 1;
        add(resultPanel, panelPlacement);
    }

    private void initContent() {
        initSearch();
        initStatus();
        initResult();
    }

    private void initSearch() {
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints searchPlacement = new GridBagConstraints();
        searchPlacement.weighty = 1;
        searchPlacement.fill = GridBagConstraints.VERTICAL;
        searchPlacement.anchor = GridBagConstraints.CENTER;
        searchPlacement.insets = new Insets(0, 0, 0, INNER_SPACING);
        searchPlacement.gridx = 0;
        searchPanel.add(urlLabel, searchPlacement);

        searchPlacement.weightx = 1;
        searchPlacement.fill = GridBagConstraints.BOTH;
        searchPlacement.gridx = 1;
        searchPanel.add(urlTextField, searchPlacement);

        searchPlacement.weightx = 0;
        searchPlacement.fill = GridBagConstraints.VERTICAL;
        searchPlacement.gridx = 2;
        searchPanel.add(searchStartButton, searchPlacement);

        searchPlacement.gridx = 3;
        searchPanel.add(searchPauseButton, searchPlacement);

        searchPlacement.insets = new Insets(0, 0, 0, 0);
        searchPlacement.gridx = 4;
        searchPanel.add(searchCancelButton, searchPlacement);
    }

    private void initStatus() {
    }

    private void initResult() {
    }

    public void launch() {
        setVisible(true);
    }
}