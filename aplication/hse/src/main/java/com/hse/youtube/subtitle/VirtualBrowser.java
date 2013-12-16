/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hse.youtube.subtitle;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 *
 * @author Usuario
 */
public class VirtualBrowser {
    private static WebClient vc = new WebClient();
    
    public synchronized WebClient getBrowserInstance(){
        return vc;
    }
    
}
