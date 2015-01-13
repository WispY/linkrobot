package com.wispy.linkrobot.bean;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static com.wispy.linkrobot.bean.LinkCheckState.*;

/**
 * @author Leonid_Poliakov
 */
public enum LinkCategory {
    GOOD(new Color(34, 115, 31), FOUND),
    BAD(new Color(155, 30, 30), NOT_FOUND, SERVER_ERROR, BAD_REQUEST, OTHER_HTTP_ERROR),
    UNKNOWN(new Color(187, 138, 40), UNKNOWN_CONTENT),
    IGNORED(new Color(92, 92, 92), DUPLICATED, EXTERNAL);

    private Color color;
    private List<LinkCheckState> states;

    private LinkCategory(Color color, LinkCheckState... states) {
        this.color = color;
        this.states = Arrays.asList(states);
    }

    public List<LinkCheckState> getStates() {
        return states;
    }
}