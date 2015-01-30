package com.wispy.linkrobot.console;

import com.itextpdf.text.pdf.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author Leonid_Poliakov
 */
public class Launcher {
    public static final Logger LOG = Logger.getLogger(Launcher.class);
    private static final String MAIL_TO_PREFIX = "mailto:";

    private Set<Page> visitedPages = new HashSet<>();
    private List<Page> unVisitedPaged = new LinkedList<>();
    private List<Page> badPages = new LinkedList<>();
    private List<Page> externalPages = new LinkedList<>();
    private List<Page> mailTos = new LinkedList<>();
    private Page root = new Page("http://localhost:8080/site");
    private long sleep = 1;

    public static void main(String[] args) throws Exception {
        new Launcher().launch();
    }

    private void launch() throws Exception {
        long time = System.currentTimeMillis();
        unVisitedPaged.add(root);

        while (!unVisitedPaged.isEmpty()) {
            Thread.sleep(sleep);
            Page pageToVisit = unVisitedPaged.remove(0);
            visitedPages.add(pageToVisit);

            LOG.info(visitedPages.size() + "/" + unVisitedPaged.size() + " visiting " + pageToVisit);
            List<Page> pages = parsePage(pageToVisit);
            if (pages == null) {
                continue;
            }
            int newLinksCount = 0;
            for (Page page : pages) {
                if (page.url.contains("#")) {
                    page.url = page.url.substring(0, page.url.indexOf("#"));
                }
                if (page.url.startsWith(MAIL_TO_PREFIX)) {
                    mailTos.add(page);
                } else if (!visitedPages.contains(page) && !unVisitedPaged.contains(page)) {
                    if (page.url.startsWith(root.url)) {
                        unVisitedPaged.add(page);
                        newLinksCount++;
                    } else {
                        externalPages.add(page);
                    }
                } else {
                    LOG.debug("duplicate " + page);
                }
            }
            LOG.info("got " + newLinksCount + " new links");
        }
        time = System.currentTimeMillis() - time;
        LOG.info("done in " + time / 1000 + " seconds");
        Collections.sort(externalPages);
        Collections.sort(badPages);
        Collections.sort(mailTos);
        for (Page page : externalPages) {
            LOG.warn("external " + page.url + "\t" + page.parent + "\t" + page.name);
        }
        for (Page page : badPages) {
            LOG.warn("bad code " + page.code + "\t" + page.url + "\t" + page.parent + "\t" + page.name);
        }
        for (Page page : mailTos) {
            LOG.warn("mail to  " + page.url + "\t" + page.parent + "\t" + page.name);
        }
    }

    private List<Page> parsePage(Page page) throws Exception {
        URL url = new URL(page.url);
        URLConnection connection = url.openConnection();
        connection.connect();
        if (!(connection instanceof HttpURLConnection)) {
            LOG.warn("bad url connection type " + connection + " " + page);
            return null;
        }
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        int statusCode = httpConnection.getResponseCode();
        if (statusCode != 200) {
            page.code = statusCode;
            badPages.add(page);
            LOG.warn("bad code " + page + " " + statusCode);
            return null;
        }
        String contentType = httpConnection.getContentType();
        if (contentType == null) {
            LOG.warn("unknown content (null) " + page);
            return null;
        } else if (contentType.contains("text/html")) {
            return parseHtml(page, IOUtils.toString(httpConnection.getInputStream()));
        } else if (contentType.contains("application/pdf")) {
            return parsePdf(page, httpConnection.getInputStream());
        } else {
            LOG.warn("unknown content " + page + " " + contentType);
            return null;
        }
    }

    private List<Page> parsePdf(Page page, InputStream content) throws Exception {
        PdfReader reader = new PdfReader(content);
        int numberOfPages = reader.getNumberOfPages();
        List<Page> childLinks = new LinkedList<>();
        for (int pageIndex = 1; pageIndex <= numberOfPages; pageIndex++) {
            ArrayList<PdfAnnotation.PdfImportedLink> links = reader.getLinks(pageIndex);
            if (links == null) {
                continue;
            }
            for (PdfAnnotation.PdfImportedLink link : links) {
                PdfObject annotationParameter = link.getParameters().get(PdfName.A);
                if (annotationParameter instanceof PdfDictionary) {
                    PdfDictionary annotationDictionary = (PdfDictionary) annotationParameter;
                    childLinks.add(new Page(annotationDictionary.getAsString(PdfName.URI).toUnicodeString(), "Page " + pageIndex, page.url));
                }
            }
        }
        LOG.debug("parsed " + childLinks.size() + " links from " + page.url);
        return childLinks;
    }

    private List<Page> parseHtml(Page page, String content) {
        String parentUrl = page.url;
        Document doc = Jsoup.parse(content, parentUrl);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        LOG.debug("parsed " + media.size() + " media from " + parentUrl);
        LOG.debug("parsed " + imports.size() + " imports from " + parentUrl);
        LOG.debug("parsed " + links.size() + " links from " + parentUrl);
        List<Page> childLinks = new LinkedList<>();
        for (Element element : imports) {
            childLinks.add(new Page(element.attr("abs:href"), element.tagName(), parentUrl));
        }
        for (Element element : media) {
            childLinks.add(new Page(element.attr("abs:src"), element.tagName(), parentUrl));
        }
        for (Element element : links) {
            childLinks.add(new Page(element.attr("abs:href"), element.text(), parentUrl));
        }
        if (content.contains("404: Topic not found.")) {
            page.code = 404;
            badPages.add(page);
        }
        return childLinks;
    }

    class Page implements Comparable<Page> {
        private String url;
        private String name;
        private String parent;
        private int code;

        Page(String url) {
            this.url = url;
        }

        Page(String url, String name, String parent) {
            this.url = url;
            this.name = name;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Page)) return false;
            Page page = (Page) o;
            return url.equals(page.url);
        }

        @Override
        public int hashCode() {
            return url.hashCode();
        }

        @Override
        public int compareTo(Page other) {
            return url.compareTo(other.url);
        }
    }

}