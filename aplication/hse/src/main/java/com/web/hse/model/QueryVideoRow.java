/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.model;


/**
 *
 * @author blacktiago
 */
public class QueryVideoRow{

  private Integer ind;
  private String url;
  private String scene;
  private String emotion;
  private String time;
  private String time_inSeconds;
  private String person;
  private String rowStyle;
  private String videoID;
  private String emotion_id;

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the emotion
   */
  public String getEmotion() {
    return emotion;
  }

  /**
   * @param emotion the emotion to set
   */
  public void setEmotion(String emotion) {
    this.emotion = emotion;
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
   * @return the scene
   */
  public String getScene() {
    return scene;
  }

  /**
   * @param scene the scene to set
   */
  public void setScene(String scene) {
    this.scene = scene;
  }

  /**
   * @return the person
   */
  public String getPerson() {
    return person;
  }

  /**
   * @param person the person to set
   */
  public void setPerson(String person) {
    this.person = person;
  }

  /**
   * @return the time_inSeconds
   */
  public String getTime_inSeconds() {
    return time_inSeconds;
  }

  /**
   * @param time_inSeconds the time_inSeconds to set
   */
  public void setTime_inSeconds(String time_inSeconds) {
    this.time_inSeconds = time_inSeconds;
  }

  /**
   * @return the ind
   */
  public Integer getInd() {
    return ind;
  }

  /**
   * @param ind the ind to set
   */
  public void setInd(Integer ind) {
    this.ind = ind;
  }

  /**
   * @return the rowStyle
   */
  public String getRowStyle() {
    
    if((this.ind%2)==0){
      return "rowResultA";
    }else{
      return "rowResultB";
    }
  }

    /**
     * @return the videoID
     */
    public String getVideoID() {
        return videoID;
    }

    /**
     * @param videoID the videoID to set
     */
    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    /**
     * @return the emotion_id
     */
    public String getEmotion_id() {
        return emotion_id;
    }

    /**
     * @param emotion_id the emotion_id to set
     */
    public void setEmotion_id(String emotion_id) {
        this.emotion_id = emotion_id;
    }
}
