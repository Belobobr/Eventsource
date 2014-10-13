package com.google.todotxt.backend;

/**
 * Created by Newshka on 05.10.2014.
 */
public class MeetFilter {

    private String type;

    public MeetFilter() {
    }

    public MeetFilter(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
