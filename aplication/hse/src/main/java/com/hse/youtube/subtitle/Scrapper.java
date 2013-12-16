/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hse.youtube.subtitle;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.DOMException;

/**
 *
 * @author Usuario
 */
public class Scrapper {

    private static final String ytTranscritionTitle = "action-panel-transcript";
    private String urlForScrapp;

    //HtmlPage page = null;
    public ArrayList<Subtiltle> scanVideo(String url) throws IOException, InterruptedException, ScrapSubtitlesException {
        //System.out.flush();
        urlForScrapp = url;
        System.out.println("----------------------------------Web process at " + new Date() + "----------------------------------");
        System.out.print("Browser Status...");


        VirtualBrowser vb = new VirtualBrowser();
        WebClient vBrowser = vb.getBrowserInstance();

        vBrowser.closeAllWindows();
        System.out.println("[OK]");
        ArrayList<Subtiltle> lstCpas = new ArrayList<Subtiltle>();
        System.out.print("Requesting " + url + "...");
        final HtmlPage page = vBrowser.getPage(url);
        System.out.println("[OK]");
        final HtmlDivision div = page.getHtmlElementById("watch7-secondary-actions");

        try {
            for (Iterator<DomElement> it = div.getChildElements().iterator(); it.hasNext();) {
                DomElement ytWebObject = it.next();
                if ("span".equals(ytWebObject.getNodeName())) {
                    final HtmlSpan ytSpan = (HtmlSpan) ytWebObject;
                    if ("button".equals(ytSpan.getFirstChild().getNodeName())) {
                        String attClass = ytSpan.getFirstChild().getAttributes().getNamedItem("data-trigger-for").getNodeValue();

                        if (attClass.equals(ytTranscritionTitle)) {
                            final HtmlButton ytButton = (HtmlButton) ytSpan.getFirstChild();
                            Thread.sleep(15000);
                            ytButton.click();
                            while (!verifyPanelTranscription(page)) {
                                System.out.println("next try in 3 secs...." + ytButton.getOnClickAttribute());
                                Thread.sleep(3000);
                                ytButton.click();
                            }

                            lstCpas = scrapSubtitles(page);

                        }
                    }

                }

            }


        } catch (DOMException e) {
            vBrowser.closeAllWindows();
            throw new ScrapSubtitlesException();
        }

        return lstCpas;

    }

    private boolean verifyPanelTranscription(HtmlPage page) throws ScrapSubtitlesException {
        boolean transcriptionPanelLoaded = false;

        HtmlDivision geneicDiv = page.getHtmlElementById("action-panel-transcript");
        if (geneicDiv != null) {
            transcriptionPanelLoaded = true;

        } else {
            System.out.println("////////no action-panel-transcript");
            return transcriptionPanelLoaded;
        }
        geneicDiv = page.getHtmlElementById("watch-transcript-container");


        if (geneicDiv != null) {
            transcriptionPanelLoaded = true;
        } else {

            System.out.println("////////no watch-transcript-container");
            return transcriptionPanelLoaded;
        }

        int counter = 0;
        for (Iterator<DomNode> it = geneicDiv.getChildren().iterator(); it.hasNext();) {
            DomNode child = it.next();
            System.out.println("Child----->" + child);
            counter++;
        }

        if (counter > 2) {
            transcriptionPanelLoaded = true;
        } else {
            transcriptionPanelLoaded = false;
            return transcriptionPanelLoaded;
        }

        return transcriptionPanelLoaded;
    }

    private ArrayList<Subtiltle> scrapSubtitles(HtmlPage page) throws ScrapSubtitlesException, InterruptedException {
        ArrayList<Subtiltle> lstCaps = new ArrayList<Subtiltle>();
        final HtmlDivision divTranslator = page.getHtmlElementById("transcript-scrollbox");
        if (divTranslator != null) {
            
            while (true) {
                System.out.println("Waiting for subtitles ajax.....");
                int counter = 0;
                for (Iterator it1 = divTranslator.getChildren().iterator();
                        it1.hasNext();) {
                    counter += 1;
                    HtmlDivision divCapps = (HtmlDivision) it1.next();

                   // for (int i = 0; i < 2; i++) {
                        Subtiltle st = new Subtiltle();
                        st.setDataTime(divCapps.getAttribute("data-time"));
                        st.setMaskedTime(divCapps.getFirstChild().asText());
                        st.setText(divCapps.getLastChild().asText());
                        st.setVideoUrl(urlForScrapp);
                        String videoId = urlForScrapp.split("v=")[1];
                        st.setVideoId(videoId);
                        st.setIndice(counter);
                        lstCaps.add(st);

                   // }

                }
                if (counter > 0) {
                    System.out.println("[OK]");
                    System.out.println("----------------------------------COMPLETED at " + new Date() + "----------------------------------");

                    break;
                } else {
                    Thread.sleep(3000);
                }


            }

        } else {
            System.out.println("transcript-scrollbox hss no childs.....");
            throw new ScrapSubtitlesException();
        }
        return lstCaps;

    }
}
