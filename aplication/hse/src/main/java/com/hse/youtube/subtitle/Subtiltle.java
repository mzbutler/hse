package com.hse.youtube.subtitle;

import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class Subtiltle implements Serializable{
    private String text;
    private String maskedTime;
    private String dataTime;
    private String videoId;
    private String videoUrl;
    private int indice;

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
     * @return the maskedTime
     */
    public String getMaskedTime() {
        return maskedTime;
    }

    /**
     * @param maskedTime the maskedTime to set
     */
    public void setMaskedTime(String maskedTime) {
        this.maskedTime = maskedTime;
    }

    /**
     * @return the dataTime
     */
    public String getDataTime() {
        return dataTime;
    }

    /**
     * @param dataTime the dataTime to set
     */
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
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

    /**
     * @return the videoUrl
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * @param videoUrl the videoUrl to set
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }
    
    public String getRowStyle() {
    
    if((this.indice%2)==0){
      return "rowResultA";
    }else{
      return "rowResultB";
    }
  }

   
}
