/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.controller;

import com.web.hse.model.OntologyModel;
import com.web.hse.model.QueryVideoRow;
import com.web.hse.model.utils.VideoPager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author blacktiago
 */
@ManagedBean(name = "userStressSession")
@SessionScoped
public class UserStressSession implements Serializable {

    static OntologyModel oModel = null;
    private ArrayList<String> stressEmotions = new ArrayList<String>();
    private String selectedEmotion;
    private List<QueryVideoRow> lstVideos = new ArrayList<QueryVideoRow>();
    private VideoPager gPager;
    private QueryVideoRow currentVideoRow;
    private String currentVideo;

    /**
     * Creates a new instance of UserStressSession
     */
    public UserStressSession() {

        try {
            oModel = new OntologyModel(this.getModelPath());
            stressEmotions = oModel.StressEmoQuery();
        } catch (Exception e) {
            messageToUser();
        }

        if (this.currentVideo == null) {
            currentVideo = "https://www.youtube.com/v/nKIu9yen5nc?enablejsapi=1&playerapiid=ytplayer&version=3";
        }

    }

    public final String getModelPath() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String relativeWebPath = "/resources/emotion_final.rdf";
        String rhCloudPath = "/var/lib/openshift/52ae1e3ae0b8cd97230000ab/app-root/repo/src/main/webapp/resources/emotion_final.rdf";

        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
        if (absoluteDiskPath != null) {
            return absoluteDiskPath;
        } else {
            return rhCloudPath;
        }

    }

    private void messageToUser() {
        ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
        Map sMap;
        sMap = tmpEC.getSessionMap();
        CrudRequest beanStress = (CrudRequest) sMap.get("crudRequest");
        beanStress.talkToUser("Problemas cargando el archivo RDF ");
    }

    public ArrayList<String> getStressEmotions() {


        return stressEmotions;

    }

    public void videosByEmotion() {
        gPager = new VideoPager((ArrayList) oModel.videosByEmotion(getSelectedEmotion()));

        setLstVideos(gPager.pager());
    }

    public void moveUp() {
        gPager.next();
        setLstVideos(gPager.pager());

    }

    public void moveDown() {
        gPager.previous();
        setLstVideos(gPager.pager());

    }

    public boolean nextButtonState() {
        if (gPager != null) {
            return gPager.nextVisible;
        } else {
            return false;
        }

    }

    public boolean previosButtonState() {
        if (gPager != null) {
            return gPager.previousVisible;
        } else {
            return false;
        }

    }

    public void editVideo(QueryVideoRow videoRow) {
        this.setCurrentVideoRow(videoRow);
    }

    /**
     * @return the lstVideos
     */
    public List<QueryVideoRow> getLstVideos() {
        return lstVideos;
    }

    /**
     * @param lstVideos the lstVideos to set
     */
    public void setLstVideos(List<QueryVideoRow> lstVideos) {
        this.lstVideos = lstVideos;
    }

    /**
     * @return the selectedEmotion
     */
    public String getSelectedEmotion() {
        return selectedEmotion;
    }

    /**
     * @param selectedEmotion the selectedEmotion to set
     */
    public void setSelectedEmotion(String selectedEmotion) {
        this.selectedEmotion = selectedEmotion;
    }

    /**
     * @return the currentVideoRow
     */
    public QueryVideoRow getCurrentVideoRow() {
        return currentVideoRow;
    }

    /**
     * @param currentVideoRow the currentVideoRow to set
     */
    public void setCurrentVideoRow(QueryVideoRow currentVideoRow) {
        this.currentVideoRow = currentVideoRow;
    }

    /**
     * @return the currentVideo
     */
    public String getCurrentVideo() {
        return currentVideo;
    }

    /**
     * @param currentVideo the currentVideo to set
     */
    public void setCurrentVideo(String currentVideo) {
        this.currentVideo = currentVideo;
    }
}
