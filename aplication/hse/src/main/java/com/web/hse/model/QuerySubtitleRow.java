/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.model;

/**
 *
 * @author Usuario
 */
public class QuerySubtitleRow {
    private String videoId;
    private String text;
    private String time;
    private String time_in_seconds;

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the time_in_seconds
     */
    public String getTime_in_seconds() {
        return time_in_seconds;
    }

    /**
     * @param time_in_seconds the time_in_seconds to set
     */
    public void setTime_in_seconds(String time_in_seconds) {
        this.time_in_seconds = time_in_seconds;
    }

    /**
     * @return the videoId
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * @param videoId the videoId to set
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
