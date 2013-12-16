/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.model.utils;

import com.hse.youtube.subtitle.Subtiltle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class SubtitlePager {
     private int lim;
    private int ind;
    private int pages;
    private int items = 10;
    public int actualPage = 1;
    public int direction = 1;
    public boolean nextVisible;
    public boolean previousVisible;
    private List<Subtiltle> pagerBaseList;
    
    public SubtitlePager(List<Subtiltle> baseList){
         nextVisible = false;
        previousVisible = false;
        this.pagerBaseList = baseList;
        Collections.sort(this.pagerBaseList, new Comparator<Subtiltle>() {

             @Override
             public int compare(Subtiltle t, Subtiltle t1) {
                 double dif = Double.parseDouble(t.getDataTime()) - Double.parseDouble(t1.getDataTime());
                 return (int) dif;
             }
         });

        if (pagerBaseList.size() > items) {
            pages = pagerBaseList.size() / items;
            nextVisible = true;
            previousVisible = false;
        } else {
            pages = 1;
            items = pagerBaseList.size();
            nextVisible = false;
            previousVisible = false;
        }
        
    }
    
     public List<Subtiltle> pager() {
        if (pagerBaseList.size() > 0 & pages > 1) {
            nextVisible = true;
            previousVisible = true;
        } else {
            nextVisible = false;
            previousVisible = false;
        }


        if ((actualPage * items) < pagerBaseList.size()) {
            lim = (actualPage * items);
            ind = lim - items;
        } else {
            lim = pagerBaseList.size();
            ind = lim - items;
        }


        if (actualPage == pages & pages > 1) {
            nextVisible = false;
            previousVisible = true;
            direction = 0;
        } else {
            if (actualPage == 1 & pages > 1) {
                previousVisible = false;
                nextVisible = true;
                direction = 1;
            }
        }

        return pagerBaseList.subList(ind, lim);



    }

    public void next() {
        this.actualPage++;
    }

    public void previous() {
        this.actualPage--;
    }
    
}
