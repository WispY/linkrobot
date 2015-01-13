package com.wispy.linkrobot.process;

import com.wispy.linkrobot.bean.ProcessingResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Leonid_Poliakov
 */
@Component
public class SearchManager {
    public static final Logger LOG = Logger.getLogger(SearchManager.class);

    private State state;
    private String rootUrl;
    private Worker worker;
    private Lock lock;
    private List<String> checkedUrls;
    private List<String> uncheckedUrls;

    @PostConstruct
    public void init() {
        state = State.WAITING;
        lock = new ReentrantLock();
    }

    public void start(String url) {
        locked(() -> {
            state = State.PROCESSING;
            rootUrl = url;
            checkedUrls = new LinkedList<>();
            uncheckedUrls = new LinkedList<>();
            uncheckedUrls.add(rootUrl);
            worker = new Worker();
            worker.execute();
            return null;
        });
    }

    public void pause() {
        locked(() -> {
            state = State.PAUSED;
            worker.cancel(false);
            worker = null;
            return null;
        });
    }

    public void resume() {
        locked(() -> {
            state = State.PROCESSING;
            worker = new Worker();
            worker.execute();
            return null;
        });
    }

    public void stop() {
        locked(() -> {
            state = State.WAITING;
            rootUrl = null;
            worker.cancel(false);
            worker = null;
            checkedUrls = new LinkedList<>();
            uncheckedUrls = new LinkedList<>();
            return null;
        });
    }

    public State getState() {
        return state;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public String getCurrentUrl() {
        return worker.getCurrentUrl();
    }

    public String getNextUrl() {
        return locked(() -> {
            Iterator<String> iterator = uncheckedUrls.iterator();
            if (!iterator.hasNext()) {
                return null;
            }
            String url = iterator.next();
            iterator.remove();
            return url;
        });
    }

    public void processResult(ProcessingResult result) {
        locked(() -> {
            checkedUrls.add(result.getCheckedUrl());
            for (String innerUrl : result.getInnerUrls()) {
                if (!checkedUrls.contains(innerUrl) && !uncheckedUrls.contains(innerUrl)) {
                    uncheckedUrls.add(innerUrl);
                }
            }
            return null;
        });
    }

    private <V> V locked(Callable<V> callable) {
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            LOG.error("Error processing locking task", e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static enum State {
        WAITING, PAUSED, PROCESSING, DONE
    }

    private class Worker extends SwingWorker<Void, ProcessingResult> {
        private String currentUrl;

        private String getCurrentUrl() {
            return currentUrl;
        }

        @Override
        protected Void doInBackground() throws Exception {
            while (!isCancelled() && (currentUrl = getNextUrl()) != null) {
                ProcessingResult result = processUrl(currentUrl);
                processResult(result);
                publish(result);
            }
            return null;
        }

        private ProcessingResult processUrl(String url) {
            ProcessingResult result = new ProcessingResult();
            result.setCheckedUrl(url);
            result.setInnerUrls(Arrays.asList(rootUrl + "error", rootUrl + "home"));
            return result;
        }

        @Override
        protected void process(List<ProcessingResult> chunks) {
            for (ProcessingResult chunk : chunks) {
                LOG.info("processed " + chunk.getCheckedUrl());
            }
        }

        @Override
        protected void done() {
            state = State.DONE;
            worker = null;
        }
    }
}