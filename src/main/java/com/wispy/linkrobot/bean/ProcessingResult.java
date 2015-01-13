package com.wispy.linkrobot.bean;

import java.util.List;

/**
 * @author Leonid_Poliakov
 */
public class ProcessingResult {
    private String checkedUrl;
    private List<String> innerUrls;

    public String getCheckedUrl() {
        return checkedUrl;
    }

    public void setCheckedUrl(String checkedUrl) {
        this.checkedUrl = checkedUrl;
    }

    public List<String> getInnerUrls() {
        return innerUrls;
    }

    public void setInnerUrls(List<String> innerUrls) {
        this.innerUrls = innerUrls;
    }
}