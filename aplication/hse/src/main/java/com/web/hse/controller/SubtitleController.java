/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.controller;

import com.hse.youtube.subtitle.ScrapSubtitlesException;
import com.hse.youtube.subtitle.Scrapper;
import javax.faces.bean.ManagedBean;
import com.hse.youtube.subtitle.Subtiltle;
import com.web.hse.model.OntologyModel;
import com.web.hse.model.utils.SubtitlePager;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author Usuario
 */
@ManagedBean
@SessionScoped
public class SubtitleController implements Serializable {

    private String url;
    private List<Subtiltle> lstCapps;
    private SubtitlePager gPager;

    /**
     * Creates a new instance of SubtitleController
     */
    public SubtitleController() {
        lstCapps = new ArrayList<Subtiltle>();
    }

    public void scraptVideo() {
        try {

            loadVideo();
            gPager = new SubtitlePager(searchCCByVideoID(getUrl().split("v=")[1]));

            this.setLstCapps(gPager.pager());
            if (this.lstCapps != null & this.lstCapps.isEmpty()) {
                Scrapper ytSrapper = new Scrapper();
                gPager = new SubtitlePager(ytSrapper.scanVideo(getUrl()));
                this.setLstCapps(gPager.pager());
                persistCCs();
            }




        } catch (IOException ex) {
            Logger.getLogger(SubtitleController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SubtitleController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScrapSubtitlesException ex) {
            Logger.getLogger(SubtitleController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void persistCCs() {
        if (this.lstCapps != null & this.lstCapps.size() > 0) {
            try {
                ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
                Map sMap;
                sMap = tmpEC.getSessionMap();
                UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
                UserStressSession.oModel = new OntologyModel(beanStress.getModelPath());

                UserStressSession.oModel.createSubtitles(this.lstCapps);
            } catch (IOException ex) {
                Logger.getLogger(SubtitleController.class.getName()).log(Level.SEVERE, null, ex);

            }

        }
    }

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
     * @return the lstCapps
     */
    public List<Subtiltle> getLstCapps() {
        return lstCapps;
    }

    /**
     * @param lstCapps the lstCapps to set
     */
    public void setLstCapps(List<Subtiltle> lstCapps) {
        this.lstCapps = lstCapps;
    }

    private List<Subtiltle> searchCCByVideoID(String videoID) {

        ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
        Map sMap;
        sMap = tmpEC.getSessionMap();
        UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
        UserStressSession.oModel = new OntologyModel(beanStress.getModelPath());
        return UserStressSession.oModel.subtitlesByVideo(videoID);


    }

    private void loadVideo() {
        ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
        Map sMap;
        sMap = tmpEC.getSessionMap();
        UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
        beanStress.setCurrentVideo("https://www.youtube.com/v/" + this.url.split("v=")[1] + "?enablejsapi=1&playerapiid=ytplayer&version=3");


    }

    public void moveUp() {
        gPager.next();
        setLstCapps(gPager.pager());

    }

    public void moveDown() {
        gPager.previous();
        setLstCapps(gPager.pager());

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
}
