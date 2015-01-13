package com.wispy.linkrobot;

import com.wispy.linkrobot.gui.MainFrame;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.io.IOException;
import java.util.Iterator;

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

    public static void main2(String[] args) throws IOException {
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        print("Fetching %s...", new Object[]{url});
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        print("\nMedia: (%d)", new Object[]{Integer.valueOf(media.size())});
        Iterator i$ = media.iterator();

        Element link;
        while (i$.hasNext()) {
            link = (Element) i$.next();
            if (link.tagName().equals("img")) {
                print(" * %s: <%s> %sx%s (%s)", new Object[]{link.tagName(), link.attr("abs:src"), link.attr("width"), link.attr("height"), trim(link.attr("alt"), 20)});
            } else {
                print(" * %s: <%s>", new Object[]{link.tagName(), link.attr("abs:src")});
            }
        }

        print("\nImports: (%d)", new Object[]{Integer.valueOf(imports.size())});
        i$ = imports.iterator();

        while (i$.hasNext()) {
            link = (Element) i$.next();
            print(" * %s <%s> (%s)", new Object[]{link.tagName(), link.attr("abs:href"), link.attr("rel")});
        }

        print("\nLinks: (%d)", new Object[]{Integer.valueOf(links.size())});
        i$ = links.iterator();

        while (i$.hasNext()) {
            link = (Element) i$.next();
            print(" * a: <%s>  (%s)", new Object[]{link.attr("abs:href"), trim(link.text(), 35)});
        }

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        return s.length() > width ? s.substring(0, width - 1) + "." : s;
    }
}