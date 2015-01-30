package com.wispy.linkrobot.console;

import org.apache.log4j.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Leonid_Poliakov
 */
public class LoadBalancer {
    public static final Logger LOG = Logger.getLogger(LoadBalancer.class);
    private String server = "http://10.6.131.226/";
    private int threadCount = 20;
    private long ddosStart;
    private int requestCount;
    private int requestCheckStart = 100;
    private boolean checkStarted;

    public static void main(String args[]) throws Exception {
        new LoadBalancer().launch();
    }

    private void launch() throws Exception {
        requestCount = 0;
        for (int i = 0; i < threadCount; i++) {
            new Thread(this::ddos).start();
        }
    }

    private void ddos() {
        try {
            URL url = new URL(server);
            while (true) {
                long timer = System.currentTimeMillis();
                URLConnection connection = url.openConnection();
                connection.connect();
                if (!(connection instanceof HttpURLConnection)) {
                    LOG.warn("bad url connection type " + connection.getClass());
                    continue;
                }
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int statusCode = httpConnection.getResponseCode();

                requestCount++;
                if (checkStarted) {
                    double requestsPerSecond = requestCount / ((System.currentTimeMillis() - ddosStart) / 1000.0);
                    LOG.info("speed " + Math.round(requestsPerSecond * 10) / 10.0 + " r/s");
                } else {
                    if (requestCount > requestCheckStart) {
                        requestCount = 0;
                        checkStarted = true;
                        ddosStart = System.currentTimeMillis();
                    }
                }
                LOG.info(statusCode + " after " + (System.currentTimeMillis() - timer) + " ms");
            }
        } catch (Exception e) {
            LOG.error("error", e);
            return;
        }
    }
}