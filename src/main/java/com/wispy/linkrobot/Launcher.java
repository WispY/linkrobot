package com.wispy.linkrobot;

import com.wispy.linkrobot.gui.MainFrame;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

/**
 * @author WispY
 */
public class Launcher {
    public static final Logger LOG = Logger.getLogger(Launcher.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Launcher::launch);
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }

        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        MainFrame frame = context.getBean(MainFrame.class);
        frame.launch();
    }

}