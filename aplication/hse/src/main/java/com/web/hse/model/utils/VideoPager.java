package com.web.hse.model.utils;

import com.web.hse.model.QueryVideoRow;
import java.util.List;

public class VideoPager {

    private int lim;
    private int ind;
    private int pages;
    private int items = 10;
    public int actualPage = 1;
    public int direction = 1;
    public boolean nextVisible;
    public boolean previousVisible;
    private List<QueryVideoRow> pagerBaseList;

    public VideoPager(List<QueryVideoRow> baseList) {
        nextVisible = false;
        previousVisible = false;
        this.pagerBaseList = baseList;

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

    public List<QueryVideoRow> pager() {
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
