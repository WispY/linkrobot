package com.wispy.linkrobot.bean;

/**
 * @author Leonid_Poliakov
 */
public enum LinkCheckState {
    FOUND,
    NOT_FOUND, SERVER_ERROR, BAD_REQUEST, OTHER_HTTP_ERROR,
    UNKNOWN_CONTENT,
    DUPLICATED, EXTERNAL
}