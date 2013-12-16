/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.model;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hse.youtube.subtitle.Subtiltle;
import com.web.hse.model.utils.FormatField;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author blacktiago
 */
public class OntologyModel {

    public Model stressModel = null;
    private ArrayList<String> stressEmotions = new ArrayList<String>();
    private String absoluteDiskPath;

    public OntologyModel(String absoluteDiskPath) {
        stressModel = buildModel(absoluteDiskPath);
        this.absoluteDiskPath = absoluteDiskPath;
    }

    public ArrayList<String> StressEmoQuery() throws Exception {
        String queryString = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT *\n"
                + "WHERE {  \n"
                + "   ?emotion rdfs:subClassOf vm:stressEmotion .  }";
        /*String queryString =
         "PREFIX vm: <http://www.owl-ontologies.com/Ontology1358984648.owl#> "
         + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
         + "SELECT * "
         + "WHERE {  "
         + "    ?emotion rdfs:subClassOf vm:human_StressEmotions ."
         + "      }  ";*/

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = null;
        ResultSet results = null;

// Execute the query and obtain results
        try {
            qe = QueryExecutionFactory.create(query, stressModel);
            results = qe.execSelect();


            for (Iterator iter = results; iter.hasNext();) {
                ResultBinding res = (ResultBinding) iter.next();
                Object x = res.get("emotion");
                stressEmotions.add(FormatField.splitUrl(x.toString()));
            }




        } catch (Exception e) {
            throw new Exception();
        } finally {
            qe.close();

        }

        return stressEmotions;

    }

    public List<QueryVideoRow> videosByEmotion(String emotion) {
        System.out.println("searching for " + emotion);
        List<QueryVideoRow> qvideos = new ArrayList<QueryVideoRow>();

        String qString = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX foaf: <http://xmlns.com/foaf/spec/#>\n"
                + "SELECT ?videos ?url ?scene ?person ?label_emotion ?time ?emotion\n"
                + " WHERE {    ?videos vm:has_a ?s  . \n"
                + "?videos vm:url ?url .\n"
                + "?s rdfs:label ?scene .\n"
                + "?s vm:show_a ?p . \n"
                + "?s vm:frame_at ?time .\n"
                + "?p foaf:name ?person . \n"
                + "?p vm:shows ?emotion .\n"
                + "?emotion rdfs:label ?label_emotion .\n"
                + "?emotion rdfs:label '" + emotion + "' .\n"
                + "} ";
        /*String qString = "PREFIX vm: <http://www.owl-ontologies.com/Ontology1358984648.owl#> "
         + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
         + " SELECT ?videos ?url ?scene ?person ?label_emotion ?time ?emotion"
         + " WHERE { "
         + "  ?videos vm:have_A ?scene . "
         + "  ?videos vm:web_Direction ?url . "
         + "  ?scene vm:show_A ?person . "
         + "  ?scene vm:have ?emotion . "
         + "  ?scene vm:have_Time ?time . "
         + "  ?emotion rdf:type ?label_emotion . "
         + "  ?emotion rdf:type '" + emotion + "' . } "
         + " ORDER BY ?url ";*/

        Query query = QueryFactory.create(qString);
        QueryExecution qe = null;
        ResultSet results = null;

        qe = QueryExecutionFactory.create(query, stressModel);
        results = qe.execSelect();
        int indice = 1;
        for (Iterator iter = results; iter.hasNext();) {
            ResultBinding res = (ResultBinding) iter.next();

            QueryVideoRow video = new QueryVideoRow();

            video.setInd(indice);

            Object videoID = res.get("videos");
            video.setVideoID(FormatField.splitUrl(videoID.toString()));

            Object url = res.get("url");
            video.setUrl(url.toString());

            Object scene = res.get("scene");
            video.setScene(FormatField.splitUrl(scene.toString()));

            Object person = res.get("person");
            video.setPerson(FormatField.splitUrl(person.toString()));

            Object label_emotion = res.get("label_emotion");
            video.setEmotion(label_emotion.toString());

            Object emotionId = res.get("emotion");
            video.setEmotion_id(FormatField.splitUrl(emotionId.toString()));

            Object time = res.get("time");
            video.setTime(time.toString());
            video.setTime_inSeconds(FormatField.timeFormat(time.toString()));

            qvideos.add(video);
            indice++;
        }


        return qvideos;
    }

    private Model buildModel(String filePatch) {
        Model modelr = ModelFactory.createDefaultModel();
        try {
            System.out.println("Getting model file from:"+filePatch);
            InputStream in = FileManager.get().open(filePatch);
            if (in == null) {
                throw new IllegalArgumentException(
                        "File: " + filePatch + " not found");
            }

            System.out.println("----------------------------readding------- -----------------");
            modelr.read(in, null);


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(
                        "File: " + filePatch + " not found");
        }




        return modelr;
    }

    public void updateRow(QueryVideoRow tRow, QueryVideoRow cRow) throws IOException {
        this.deleteVideo(cRow);
        this.createVideo(tRow);
    }

    public void createVideo(QueryVideoRow newEntry) throws IOException {
        String nScene = Normalizer.normalize(newEntry.getScene().trim(), Normalizer.Form.NFD);
        Character fraeId = nScene.charAt(nScene.length() - 1);
        nScene = newEntry.getVideoID() + fraeId;
        String nPerson = newEntry.getPerson().replaceAll("\\s", "").toLowerCase();
        nPerson = newEntry.getVideoID() + nPerson;


        String insertSparql = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX foaf: <http://xmlns.com/foaf/spec/#>\n"
                + "INSERT DATA \n"
                + "{ \n"
                + "   vm:" + newEntry.getVideoID() + " rdf:type 'https://github.com/blacktiago/HSE/ontology/hse.owl#video' . \n"
                + "   vm:" + newEntry.getVideoID() + " vm:url '" + newEntry.getUrl().trim() + "' . \n"
                + "   vm:" + newEntry.getVideoID() + " vm:has_a _:" + nScene + " . \n"
                + "   _:" + nScene + "  vm:show_a _:" + nPerson + " .\n"
                + "   _:" + nPerson + " vm:shows  vm:" + newEntry.getEmotion().toLowerCase() + ". \n"
                + "   vm:" + newEntry.getEmotion().toLowerCase() + " rdfs:label '" + newEntry.getEmotion().toLowerCase() + "'. \n"
                + "   _:" + nPerson + " foaf:name '" + newEntry.getPerson() + "' . \n"
                + "   _:" + nPerson + " rdf:type 'http://xmlns.com/foaf/spec/#Person' . \n"
                + "   _:" + nScene + " rdfs:label '" + newEntry.getScene().trim() + "' . \n"
                + "   _:" + nScene + " rdf:type 'https://github.com/blacktiago/HSE/ontology/hse.owl#frame' . \n"
                + "   _:" + nScene + "  vm:frame_at '" + newEntry.getTime().trim() + "' .\n"
                + "};";

        System.out.println(insertSparql);
        UpdateRequest request = UpdateFactory.create();
        request.add(insertSparql);
        try {
            UpdateAction.execute(request, stressModel);
            File f = new File(this.absoluteDiskPath);
            f.createNewFile();
            PrintWriter pw = new PrintWriter(f);
            stressModel.write(pw);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
        }



    }

    public void deleteVideo(QueryVideoRow newEntry) throws IOException {
        String deleteSparqlPerson = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX foaf: <http://xmlns.com/foaf/spec/#>\n"
                + "DELETE { ?person ?x ?y  } \n"
                + " WHERE \n"
                + "{ \n"
                + "   ?frame  rdfs:label '" + newEntry.getScene() + "' .\n"
                + "   ?frame  vm:frame_at '" + newEntry.getTime() + "' .\n"
                + "   ?frame  vm:show_a ?person .\n"
                + "   ?person  foaf:name '" + newEntry.getPerson() + "' .\n"
                + "   ?person ?x ?y .\n"
                + "} ";

        String deleteSparqlframe = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX foaf: <http://xmlns.com/foaf/spec/#>\n"
                + "DELETE { ?frame ?prop ?y  } \n"
                + " WHERE \n"
                + "{ \n"
                + "   vm:" + newEntry.getVideoID() + " ?prop ?frame . \n"
                + "   ?frame  rdfs:label '" + newEntry.getScene() + "' .\n"
                + "   ?frame  vm:frame_at '" + newEntry.getTime() + "' .\n"
                + "   ?frame ?prop ?y .\n"
                + "}";


        System.out.println(deleteSparqlPerson);
        System.out.println(deleteSparqlframe);
        UpdateRequest request = UpdateFactory.create();
        request.add(deleteSparqlPerson);
        request.add(deleteSparqlframe);
        UpdateAction.execute(request, stressModel);
        File f = new File(this.absoluteDiskPath);
        f.createNewFile();
        PrintWriter pw = new PrintWriter(f);
        stressModel.write(pw);


    }

    /*
     * Subtitles
     */
    public void createSubtitles(List<Subtiltle> newEntry) throws IOException {

        for (Iterator<Subtiltle> it = newEntry.iterator(); it.hasNext();) {
            Subtiltle querySubtitleRow = it.next();
            String insertSparql = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                    + "INSERT DATA \n"
                    + "{ \n"
                    + "   vm:" + querySubtitleRow.getVideoId() + " rdf:type 'https://github.com/blacktiago/HSE/ontology/hse.owl#video' . \n"
                    + "   vm:" + querySubtitleRow.getVideoId() + " vm:url  '" + querySubtitleRow.getVideoUrl() + "' . \n"
                    + "   vm:" + querySubtitleRow.getVideoId() + " vm:cc _:cc . \n"
                    + "   _:cc rdf:type 'https://github.com/blacktiago/HSE/ontology/hse.owl#caption' . \n"
                    + "   _:cc vm:text  \"" + querySubtitleRow.getText() + "\" . \n"
                    + "   _:cc vm:cc_at  '" + querySubtitleRow.getMaskedTime() + "' . \n"
                    + "   _:cc vm:cc_at_seconds  '" + querySubtitleRow.getDataTime() + "' . \n"
                    + "};";

            System.out.println(insertSparql);
            UpdateRequest request = UpdateFactory.create();
            request.add(insertSparql);
            UpdateAction.execute(request, stressModel);

        }



        try {

            File f = new File(this.absoluteDiskPath);
            f.createNewFile();
            PrintWriter pw = new PrintWriter(f);
            stressModel.write(pw);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
        }

    }

    public ArrayList<Subtiltle> subtitlesByVideo(String videioId) {
        System.out.println("searching for " + videioId);
        ArrayList<Subtiltle> qccs = new ArrayList<Subtiltle>();

        String qString = "PREFIX vm: <https://github.com/blacktiago/HSE/ontology/hse.owl#> \n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "SELECT  ?cc ?ccText ?time ?timeSeconds \n"
                + " WHERE {   \n"
                + "  vm:" + videioId + " vm:cc ?cc . \n"
                + "  ?cc rdf:type 'https://github.com/blacktiago/HSE/ontology/hse.owl#caption' . \n"
                + "  ?cc vm:text  ?ccText . \n"
                + "  ?cc vm:cc_at  ?time . \n"
                + "  ?cc vm:cc_at_seconds  ?timeSeconds . \n"
                + "} \n"
                + "ORDER BY ?timeSeconds";


        Query query = QueryFactory.create(qString);
        QueryExecution qe = null;
        ResultSet results = null;

        qe = QueryExecutionFactory.create(query, stressModel);
        results = qe.execSelect();
        int indice = 1;
        for (Iterator iter = results; iter.hasNext();) {
            ResultBinding res = (ResultBinding) iter.next();

            Subtiltle cc = new Subtiltle();
            cc.setIndice(indice);

            cc.setVideoId(videioId);

            //Object ccID = res.get("cc");
            //video.setVideoID(FormatField.splitUrl(videoID.toString()));

            Object ccText = res.get("ccText");
            cc.setText(ccText.toString());

            Object ccMaskedTime = res.get("time");
            cc.setMaskedTime(ccMaskedTime.toString());

            Object ccDataTime = res.get("timeSeconds");
            cc.setDataTime(ccDataTime.toString());

            qccs.add(cc);

            indice++;
        }


        return qccs;

    }
}
