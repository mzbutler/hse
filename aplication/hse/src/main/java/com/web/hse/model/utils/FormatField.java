/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.hse.model.utils;


import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author blacktiago
 */
public class FormatField {

    public static String splitUrl(String Entrada) {
        String separar[] = Entrada.split("#");
        String parte[] = new String[4];
        int j = 0;
        while (j <= separar.length - 1) {
            parte[j] = separar[j];
            j++;
        }
        return parte[j - 1];
    }

    public static String timeFormat(String nu) {
        String separar[] = nu.split(":");
        int seg = 0;
        int num[] = new int[3];
        int j = 0;
        while (j <= separar.length - 1) {
            num[j] = Integer.parseInt(separar[j]);
            if (num[j] < 10) {
                separar[j] = Integer.toString(num[j]);
                separar[j] = ("0" + separar[j]);
            }
            j++;
        }
        if (separar.length == 3) {
            num[0] *= 3600;
            num[1] *= 60;
            seg = num[0] + num[1] + num[2];
        } else {
            if (separar.length == 2) {
                num[0] *= 60;
                seg = num[0] + num[1];
            }
        }

        return Integer.toString(seg);
    }

    public static boolean isValidUrl(String strUrl) {
        UrlValidator urlValidator = new UrlValidator();
        if (strUrl != null && !strUrl.isEmpty()) {
            //valid URL
            if (urlValidator.isValid(strUrl)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
}
