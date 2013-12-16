/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.controller;

import static com.web.hse.controller.UserStressSession.oModel;
import com.web.hse.model.OntologyModel;
import com.web.hse.model.QueryVideoRow;
import com.web.hse.model.utils.FormatField;
import com.web.hse.model.utils.VideoPager;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Usuario
 */
@ManagedBean(name = "crudRequest")
@SessionScoped
public class CrudRequest {

    /**
     * Creates a new instance of CrudRequest
     */
    //private String relativeWebPath = "/resources/emotion_final.rdf";
    private String crudModePnlState;
    private String crudActionPnlState;
    private String actionMode;
    private String uiMessage;
    private String visibleUiMessage;
    private String visibleUiConfirm;
    private String visibleUiAction;
    private String visibleUiDeleted;
    private String visibleNewPanel;
    private String visiblePupUp;
    private boolean videoFieldState;
    private boolean fieldEditState;
    private String rowUrl;
    private QueryVideoRow currentVideoRow;
    private QueryVideoRow tempVideoRow;
    private List<QueryVideoRow> lstChieldEmotions;
    FacesContext fcontext;
    private String ytPanelControls;
    private String emotionRecords;

    public CrudRequest() {
        this.fcontext = FacesContext.getCurrentInstance();

        crudActionPnlState = "none";
        crudModePnlState = "inline";
        visibleUiMessage = "none";
        visibleUiConfirm = "block";
        visibleUiAction = "none";
        visibleUiDeleted = "none";
        visibleNewPanel = "none";
        visiblePupUp = "none";
        videoFieldState = true;
        fieldEditState = true;
        ytPanelControls = "none";

        currentVideoRow = new QueryVideoRow();


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

    public String editNode(QueryVideoRow selectedRow) {
        this.ytPanelControls = "block";
        setCurrentVideoRow(selectedRow);
        this.emotionRecords = this.currentVideoRow.getScene() + "|" + this.currentVideoRow.getPerson()
                + "|" + this.currentVideoRow.getEmotion()
                + "|" + this.currentVideoRow.getTime() + "|" + this.currentVideoRow.getTime_inSeconds()
                + "|" + this.currentVideoRow.getVideoID() + "|" + this.currentVideoRow.getUrl();
        tempVideoRow = new QueryVideoRow();
        tempVideoRow.setEmotion(selectedRow.getEmotion());
        tempVideoRow.setPerson(selectedRow.getPerson());
        tempVideoRow.setTime(selectedRow.getTime());
        tempVideoRow.setUrl(selectedRow.getUrl());
        tempVideoRow.setScene(selectedRow.getScene());
        tempVideoRow.setVideoID(selectedRow.getVideoID());
        tempVideoRow.setEmotion_id(selectedRow.getEmotion_id());
        return prepareWebUi("UP");

    }

    public void deleteNode(QueryVideoRow selectedRow) {
        this.currentVideoRow = selectedRow;

        prepareWebUi("DL");


    }

    public void applyNode() {

        if ("UP".equals(this.actionMode)) {
            String[] lstRowsED = this.emotionRecords.split(",");
            boolean sucessfullyApplyED = false;
            for (int i = 0; i < lstRowsED.length; i++) {

                String[] lstCols = lstRowsED[i].split("\\|");
                inicializeFormRow(lstCols);

                if (validaterowHasChanged()) {
                    try {
                        if (validateFields(this.getCurrentVideoRow())) {
                            UserStressSession.oModel.updateRow(currentVideoRow, getTempVideoRow());
                            sucessfullyApplyED = true;
                        } else {
                            ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
                            Map sMap;
                            sMap = tmpEC.getSessionMap();
                            UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
                            int ind = beanStress.getLstVideos().indexOf(this.currentVideoRow);
                            beanStress.getLstVideos().get(ind).setEmotion(this.getTempVideoRow().getEmotion());
                            beanStress.getLstVideos().get(ind).setPerson(this.getTempVideoRow().getPerson());
                            beanStress.getLstVideos().get(ind).setScene(this.getTempVideoRow().getScene());
                            beanStress.getLstVideos().get(ind).setTime(this.getTempVideoRow().getTime());
                            beanStress.getLstVideos().get(ind).setTime_inSeconds(this.getTempVideoRow().getTime_inSeconds());
                            beanStress.getLstVideos().get(ind).setUrl(this.getTempVideoRow().getUrl());
                            this.setVisibleUiAction("block");
                            sucessfullyApplyED = false;
                            break;
                        }

                    } catch (Exception e) {
                        talkToUser("Problems updating RDF file " + e.getMessage());
                        this.setVisibleUiAction("block");
                    }


                } else {
                    this.setVisibleUiConfirm("none");//ocultando bonton OK
                    this.setVisibleUiAction("block");//ocultar back cancel
                    this.setVisibleUiDeleted("none");//ocultar botones delete modal
                    talkToUser("The record has not been modified...");

                }


            }
            if (sucessfullyApplyED) {
                recargarModelo(this.currentVideoRow.getEmotion());
                this.prepareWebUi("CL");
                this.setVisibleUiConfirm("block");//mostrar bonton OK modal
                this.setVisibleUiAction("none");//ocultar back cancel modal
                this.setVisibleUiDeleted("none");//ocultar botones delete modal
                talkToUser("successfully processed!");
            } else {
                this.setVisibleUiAction("block");
                talkToUser("Problems creating record, your file may be inconsistent.");
            }
        }
        if ("AD".equals(this.actionMode)) {

            String[] lstRows = this.emotionRecords.split(",");
            boolean sucessfullyApply = false;
            for (int i = 0; i < lstRows.length; i++) {

                String[] lstCols = lstRows[i].split("\\|");
                inicializeFormRow(lstCols);

                if (!validateCompleteRow()) {
                    try {
                        if (validateFields(this.getCurrentVideoRow())) {
                            UserStressSession.oModel.createVideo(currentVideoRow);
                            sucessfullyApply = true;
                        } else {
                            this.setVisibleUiAction("block");
                            sucessfullyApply = false;
                            break;
                        }

                    } catch (Exception e) {
                        sucessfullyApply = false;
                        talkToUser("Problems creating record " + e.getMessage());
                        this.setVisibleUiAction("block");
                        break;
                    }
                } else {
                    this.setVisibleUiConfirm("none");//ocultando bonton OK
                    this.setVisibleUiAction("block");//ocultar back cancel
                    this.setVisibleUiDeleted("none");//ocultar botones delete modal
                    talkToUser("All fields are required...");
                    sucessfullyApply = false;
                    break;
                }
            }

            if (sucessfullyApply) {
                recargarModelo(this.currentVideoRow.getEmotion());
                this.prepareWebUi("CL");
                this.setVisibleUiConfirm("block");//mostrar bonton OK modal
                this.setVisibleUiAction("none");//ocultar back cancel modal
                this.setVisibleUiDeleted("none");//ocultar botones delete modal                
                talkToUser("successfully processed!");
            } else {
                this.setVisibleUiAction("block");
                talkToUser("Problems creating record, your file may be inconsistent.");
            }



            /*if ("AD".equals(this.actionMode)) {
             boolean rowUnComplete = false;

             if ("".equals(currentVideoRow.getUrl())) {
             rowUnComplete = true;
             } else {
             if ("".equals(currentVideoRow.getVideoID())) {
             rowUnComplete = true;
             } else {
             if ("".equals(currentVideoRow.getScene())) {
             rowUnComplete = true;
             } else {
             if ("".equals(currentVideoRow.getTime())) {
             rowUnComplete = true;
             } else {
             if ("".equals(currentVideoRow.getPerson())) {
             rowUnComplete = true;
             } else {
             if ("".equals(currentVideoRow.getPerson())) {
             rowUnComplete = true;
             }

             }
             }
             }
             }
             }
             if (rowUnComplete == false) {
             try {
             if (validateFields(this.getCurrentVideoRow())) {
             UserStressSession.oModel.createVideo(currentVideoRow);
             recargarModelo(currentVideoRow.getEmotion());
             this.prepareWebUi("CL");
             this.setVisibleUiConfirm("block");//mostrar bonton OK modal
             this.setVisibleUiAction("none");//ocultar back cancel modal
             this.setVisibleUiDeleted("none");//ocultar botones delete modal
             talkToUser("successfully processed!");
             } else {
             this.setVisibleUiAction("block");
             }

             } catch (Exception e) {
             talkToUser("Problems creating record " + e.getMessage());
             this.setVisibleUiAction("block");
             }
             } else {
             this.setVisibleUiConfirm("none");//ocultando bonton OK
             this.setVisibleUiAction("block");//ocultar back cancel
             this.setVisibleUiDeleted("none");//ocultar botones delete modal
             talkToUser("All fields are required...");
             }

             }*/
        }
        if ("DL".equals(this.actionMode)) {
            try {
                UserStressSession.oModel.deleteVideo(currentVideoRow);
            } catch (Exception e) {
                talkToUser("Problems deleting registry " + e.getMessage());
                this.setVisibleUiAction("block");
            }

            recargarModelo(currentVideoRow.getEmotion());
            this.prepareWebUi("CL");
            this.setVisibleUiConfirm("block");
            this.setVisibleUiAction("none");
            this.setVisibleUiDeleted("none");
            talkToUser("successfully processed!");

        }



    }

    public String prepareWebUi(String action) {
        if ("UP".equals(action)) {
            clearModalMessage();//limpiar mensage modal
            this.setActionMode("UP");//cambiar a modo agregar   
            this.setVisiblePupUp("none");//activando pop up
            this.setVisibleNewPanel("inline-block");//mostrando panel nuevo/edit
            this.setCrudActionPnlState("inline");//mostrar apply & cancel
            this.setCrudModePnlState("none");//ocultando boton nuevo & cancelar
            this.setCrudModePnlState("none");//ocultando boton nuevo & cancelar            
            this.setFieldEditState(false);//habilitar campor para edicion
            this.setVideoFieldState(true);//habilitando campos video y url;
            this.setVisibleUiDeleted("none");//mostrar proceed / cancel
            this.setVisibleUiConfirm("none");
            this.setVisibleUiAction("none");

            String videoId = this.currentVideoRow.getUrl().split("v=")[1];
            ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
            Map sMap;
            sMap = tmpEC.getSessionMap();
            UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
            beanStress.setCurrentVideo("https://www.youtube.com/v/" + videoId + "?enablejsapi=1&playerapiid=ytplayer&version=3");
            setYtPanelControls("block");


            return "video";

        }

        if ("AD".equals(action)) {
            clearModalMessage();//limpiar mensage modal
            this.setCurrentVideoRow(new QueryVideoRow());//crear nuevo registro
            this.setActionMode("AD");//cambiar a modo agregar            
            this.setVisiblePupUp("none");//activando pop up
            this.setVisibleNewPanel("inline-block");//mostrando panel nuevo
            this.setCrudActionPnlState("inline");//mostrar apply & cancel
            this.setCrudModePnlState("none");//ocultando boton nuevo & cancelar
            this.setFieldEditState(false);//habilitar campor para edicion
            this.setVideoFieldState(false);//habilitando campos video y url;
            this.setVisibleUiDeleted("none");//mostrar proceed / cancel
            this.setVisibleUiConfirm("none");
            this.setVisibleUiAction("none");
            return "video";
        }
        if ("CL".equals(action)) {
            clearModalMessage();//limpiar mensage modal
            this.setActionMode(null);//cambiar a modo agregar  
            this.setCurrentVideoRow(null);//crear nuevo registro
            this.setCrudActionPnlState("none");//none apply & cancel
            this.setCrudModePnlState("inline");//mostrando boton nuevo & cancelar
            this.setFieldEditState(true);//inhabilitar campor para edicion
            this.setVideoFieldState(true);//inhabilitando campos video y url;
            this.setVisibleNewPanel("none");//mostrando panel nuevo
            this.setVisibleUiDeleted("none");//mostrar proceed / cancel
            this.setVisibleUiConfirm("none");
            this.setVisibleUiAction("none");
            this.setVisiblePupUp("none");
            this.emotionRecords = "";
            return "index";

        }
        /* if ("AP".equals(action)) {
         this.setActionMode(null);//cambiar a modo agregar  
         this.setCrudActionPnlState("none");//none apply & cancel
         this.setCrudModePnlState("inline");//mostrando boton nuevo & cancelar
         this.setCurrentVideoRow(null);//crear nuevo registro
         this.setFieldEditState(true);//habilitar campor para edicion
         this.setVisibleNewPanel("none");//mostrando panel nuevo
         this.setVisibleUiDeleted("none");//mostrar proceed / cancel
         this.setVisibleUiConfirm("none");
         this.setVisibleUiAction("none");
         return "#";

         }*/
        if ("DL".equals(action)) {
            clearModalMessage();//limpiar mensage modal
            this.setActionMode("DL");//cambiar a modo agregar  
            this.setCrudActionPnlState("none");//none apply & cancel
            this.setCrudModePnlState("none");//mostrando boton nuevo & cancelar
            this.setFieldEditState(true);//inhabilitar campor para edicion
            this.setVideoFieldState(true);//inhabilitando campos video y url;
            this.setVisibleUiDeleted("block");//mostrar proceed / cancel
            this.setVisibleUiAction("none");
            this.setVisiblePupUp("none");
            this.setVisibleUiConfirm("none");
            talkToUser("Are you sure you want to delete this record?");
            return "#";
        }

        return "#";

    }

    /**
     * @return the crudModePnlState
     */
    public String getCrudModePnlState() {
        return crudModePnlState;
    }

    /**
     * @param crudModePnlState the crudModePnlState to set
     */
    public void setCrudModePnlState(String crudModePnlState) {
        this.crudModePnlState = crudModePnlState;
    }

    /**
     * @return the crudActionPnlState
     */
    public String getCrudActionPnlState() {
        return crudActionPnlState;
    }

    /**
     * @param crudActionPnlState the crudActionPnlState to set
     */
    public void setCrudActionPnlState(String crudActionPnlState) {
        this.crudActionPnlState = crudActionPnlState;
    }

    /**
     * @return the fieldEditState
     */
    public boolean isFieldEditState() {
        return fieldEditState;
    }

    /**
     * @param fieldEditState the fieldEditState to set
     */
    public void setFieldEditState(boolean fieldEditState) {
        this.fieldEditState = fieldEditState;
    }

    /**
     * @return the rowUrl
     */
    public String getRowUrl() {
        return rowUrl;
    }

    /**
     * @param rowUrl the rowUrl to set
     */
    public void setRowUrl(String rowUrl) {
        this.rowUrl = rowUrl;
    }

    /**
     * @return the actionMode
     */
    public String getActionMode() {
        return actionMode;
    }

    /**
     * @param actionMode the actionMode to set
     */
    public void setActionMode(String actionMode) {
        this.actionMode = actionMode;
    }

    /**
     * @return the tempVideoRow
     */
    public QueryVideoRow getTempVideoRow() {
        return tempVideoRow;
    }

    /**
     * @param tempVideoRow the tempVideoRow to set
     */
    public void setTempVideoRow(QueryVideoRow tempVideoRow) {
        this.tempVideoRow = tempVideoRow;
    }

    /**
     * @return the uiMessage
     */
    public String getUiMessage() {
        return uiMessage;
    }

    /**
     * @param uiMessage the uiMessage to set
     */
    public void setUiMessage(String uiMessage) {
        this.uiMessage = uiMessage;
    }

    public void talkToUser(String message) {
        this.setVisibleNewPanel("none");
        this.setVisiblePupUp("inline");
        this.setVisibleUiMessage("inline-block");
        this.setUiMessage(message);

    }

    /**
     * @return the visibleUiMessage
     */
    public String getVisibleUiMessage() {
        return visibleUiMessage;
    }

    /**
     * @param visibleUiMessage the visibleUiMessage to set
     */
    public void setVisibleUiMessage(String visibleUiMessage) {
        this.visibleUiMessage = visibleUiMessage;
    }

    /**
     * @return the visibleUiConfirm
     */
    public String getVisibleUiConfirm() {
        return visibleUiConfirm;
    }

    /**
     * @param visibleUiConfirm the visibleUiConfirm to set
     */
    public void setVisibleUiConfirm(String visibleUiConfirm) {
        this.visibleUiConfirm = visibleUiConfirm;
    }

    /**
     * @return the visibleUiAction
     */
    public String getVisibleUiAction() {
        return visibleUiAction;
    }

    /**
     * @param visibleUiAction the visibleUiAction to set
     */
    public void setVisibleUiAction(String visibleUiAction) {
        this.visibleUiAction = visibleUiAction;
    }

    private void recargarModelo(String emotion) {
        /*FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);*/
        ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
        Map sMap;
        sMap = tmpEC.getSessionMap();
        UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
        UserStressSession.oModel = new OntologyModel(beanStress.getModelPath());
        
        beanStress.setSelectedEmotion(emotion);
        VideoPager gPager = new VideoPager(oModel.videosByEmotion(beanStress.getSelectedEmotion()));
        beanStress.setLstVideos(gPager.pager());
    }

    /**
     * @return the videoFieldState
     */
    public boolean isVideoFieldState() {
        return videoFieldState;
    }

    /**
     * @param videoFieldState the videoFieldState to set
     */
    public void setVideoFieldState(boolean videoFieldState) {
        this.videoFieldState = videoFieldState;
    }

    /**
     * @return the visibleUiDeleted
     */
    public String getVisibleUiDeleted() {
        return visibleUiDeleted;
    }

    /**
     * @param visibleUiDeleted the visibleUiDeleted to set
     */
    public void setVisibleUiDeleted(String visibleUiDeleted) {
        this.visibleUiDeleted = visibleUiDeleted;
    }

    /**
     * @return the visibleNewPanel
     */
    public String getVisibleNewPanel() {
        return visibleNewPanel;
    }

    /**
     * @param visibleNewPanel the visibleNewPanel to set
     */
    public void setVisibleNewPanel(String visibleNewPanel) {
        this.visibleNewPanel = visibleNewPanel;
    }

    /**
     * @return the visiblePupUp
     */
    public String getVisiblePupUp() {
        return visiblePupUp;
    }

    /**
     * @param visiblePupUp the visiblePupUp to set
     */
    public void setVisiblePupUp(String visiblePupUp) {
        this.visiblePupUp = visiblePupUp;
    }

    private void clearModalMessage() {
        this.setUiMessage(null);
        this.setVisiblePupUp("none");
        this.setVisibleUiMessage("none");
    }

    private boolean validateFields(QueryVideoRow currentVideoRow) {
        boolean fieldsOk = false;
        System.out.println(currentVideoRow.getEmotion());
        if (currentVideoRow.getUrl().contains("v=")) {
            if (!currentVideoRow.getUrl().contains(" ")) {
                try {
                    FormatField.timeFormat(currentVideoRow.getTime());
                    fieldsOk = true;
                } catch (Exception e) {
                    System.out.println("Formato de tiempo incorrecto: correcto" + e.getMessage());
                    talkToUser("Bad time format: " + e.getMessage());
                    fieldsOk = false;
                }
            } else {
                fieldsOk = false;
                talkToUser("Invalid video url");
            }
        } else {
            fieldsOk = false;
            talkToUser("Invalid video url");
        }
        return fieldsOk;
    }

    public void capture() {
        if (FormatField.isValidUrl(this.currentVideoRow.getUrl())) {
            try {
                System.out.println("Url >>>>>>>>>" + this.currentVideoRow.getUrl());
                String videoId = this.currentVideoRow.getUrl().split("v=")[1];
                this.currentVideoRow.setVideoID(videoId);
                this.setVideoFieldState(true);
                this.setVisibleUiAction("none");
                this.setVisiblePupUp("none");

                //seteando el video en proceso
                ExternalContext tmpEC = FacesContext.getCurrentInstance().getExternalContext();
                Map sMap;
                sMap = tmpEC.getSessionMap();
                UserStressSession beanStress = (UserStressSession) sMap.get("userStressSession");
                beanStress.setCurrentVideo("https://www.youtube.com/v/" + videoId + "?enablejsapi=1&playerapiid=ytplayer&version=3");
                setYtPanelControls("block");

                //Preparing child records
                //setLstChieldEmotions(new ArrayList<QueryVideoRow>());


            } catch (Exception e) {
                this.currentVideoRow.setUrl(null);
                this.currentVideoRow.setVideoID(null);
                this.talkToUser("Error.. Please insert a valid youtube url.");
                this.setVisibleUiAction("block");
                this.setVideoFieldState(false);
                setYtPanelControls("none");
            }


        } else {
            this.currentVideoRow.setUrl(null);
            this.currentVideoRow.setVideoID(null);
            this.talkToUser("Error.. Please insert a valid youtube url.");
            this.setVisibleUiAction("block");
            this.setVideoFieldState(false);
            setYtPanelControls("none");
        }
    }

    /**
     * @return the lstChieldEmotions
     */
    public List<QueryVideoRow> getLstChieldEmotions() {
        return lstChieldEmotions;
    }

    /**
     * @param lstChieldEmotions the lstChieldEmotions to set
     */
    public void setLstChieldEmotions(List<QueryVideoRow> lstChieldEmotions) {
        this.lstChieldEmotions = lstChieldEmotions;
    }

    /**
     * @return the ytPanelControls
     */
    public String getYtPanelControls() {
        return ytPanelControls;
    }

    /**
     * @param ytPanelControls the ytPanelControls to set
     */
    public void setYtPanelControls(String ytPanelControls) {
        this.ytPanelControls = ytPanelControls;
    }

    /**
     * @return the emotionRecords
     */
    public String getEmotionRecords() {
        return emotionRecords;
    }

    /**
     * @param emotionRecords the emotionRecords to set
     */
    public void setEmotionRecords(String emotionRecords) {
        this.emotionRecords = emotionRecords;
    }

    private boolean validateCompleteRow() {
        boolean rowUnComplete = false;

        if ("".equals(currentVideoRow.getUrl())) {
            rowUnComplete = true;
        } else {
            if ("".equals(currentVideoRow.getVideoID())) {
                rowUnComplete = true;
            } else {
                if ("".equals(currentVideoRow.getScene())) {
                    rowUnComplete = true;
                } else {
                    if ("".equals(currentVideoRow.getTime())) {
                        rowUnComplete = true;
                    } else {
                        if ("".equals(currentVideoRow.getPerson())) {
                            rowUnComplete = true;
                        } else {
                            if ("".equals(currentVideoRow.getPerson())) {
                                rowUnComplete = true;
                            }

                        }
                    }
                }
            }
        }
        return rowUnComplete;
    }

    private void inicializeFormRow(String[] lstCols) {

        this.currentVideoRow = new QueryVideoRow();
        this.currentVideoRow.setVideoID(lstCols[5]);
        this.currentVideoRow.setUrl(lstCols[6]);
        this.currentVideoRow.setScene(lstCols[0]);
        this.currentVideoRow.setPerson(lstCols[1]);
        this.currentVideoRow.setEmotion(lstCols[2]);
        this.currentVideoRow.setTime(lstCols[3]);
        this.currentVideoRow.setTime_inSeconds(lstCols[4]);
    }

    private boolean validaterowHasChanged() {
        boolean rowHasChanged = false;
        if (!currentVideoRow.getEmotion().equals(getTempVideoRow().getEmotion())) {
            rowHasChanged = true;
        } else {
            if (!currentVideoRow.getPerson().equals(getTempVideoRow().getPerson())) {
                rowHasChanged = true;
            } else {
                if (!currentVideoRow.getTime().equals(getTempVideoRow().getTime())) {
                    rowHasChanged = true;
                } else {
                    if (!currentVideoRow.getScene().equals(getTempVideoRow().getScene())) {
                        rowHasChanged = true;
                    }
                }
            }
        }
        return rowHasChanged;
    }
}
