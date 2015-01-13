package com.wispy.linkrobot;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author Leonid_Poliakov
 */
public class SimpleLauncher {
    public static final Logger LOG = Logger.getLogger(Launcher.class);

    private Set<String> visitedPages = new HashSet<>();
    private List<String> unVisitedPaged = new LinkedList<>();
    private String rootLink = "https://cloud.epam.com/";
    private int visits = 100;
    private HttpClient client = HttpClientBuilder.create().build();

    public static void main(String[] args) throws Exception {
        new SimpleLauncher().launch();
    }

    private void launch() throws Exception {
        unVisitedPaged.add(rootLink);

        while(!unVisitedPaged.isEmpty() && visits > 0) {
            visits--;
            String urlToVisit = unVisitedPaged.remove(0);
            visitedPages.add(urlToVisit);

            String html = grabHtml(urlToVisit);
            if (html == null) {
                continue;
            }
        }

        String root = ;
        URL url = new URL(root);
        String content = IOUtils.toString(url.openStream(), "UTF-8");

        print("Fetching %s...", root);
        Document doc = Jsoup.parse(content, root);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        print("\nMedia: (%d)", media.size());
        Iterator i$ = media.iterator();

        Element link;
        while (i$.hasNext()) {
            link = (Element) i$.next();
            if (link.tagName().equals("img")) {
                print(" * %s: <%s> %sx%s (%s)", link.tagName(), link.attr("abs:src"), link.attr("width"), link.attr("height"), trim(link.attr("alt"), 20));
            } else {
                print(" * %s: <%s>", link.tagName(), link.attr("abs:src"));
            }
        }

        print("\nImports: (%d)", imports.size());
        i$ = imports.iterator();

        while (i$.hasNext()) {
            link = (Element) i$.next();
            print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        i$ = links.iterator();

        while (i$.hasNext()) {
            link = (Element) i$.next();
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }

    }

    private String grabHtml(String link) throws Exception {
        LOG.info("grabbing " + link);
        HttpHost host = URIUtils.extractHost(URI.create(link));
        HttpRequest request = new HttpGet(link);
        HttpResponse response = client.execute(host, request);
        String contentType = response.getEntity().getContentType().getValue();
        if (!contentType.equals("text/html")) {
            LOG.warn("unknown content " + link + " " + contentType);
            return null;
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            LOG.warn("bad code " + link + " " + statusCode);
            return null;
        }
        return EntityUtils.toString(response.getEntity());
    }

    private void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private String trim(String s, int width) {
        return s.length() > width ? s.substring(0, width - 1) + "." : s;
    }
}
