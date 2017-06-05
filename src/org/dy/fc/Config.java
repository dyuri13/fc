/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.util.prefs.Preferences;

/**
 *
 * @author dubin
 */
public class Config {
    private Preferences pref;
    
    private Config() {
        Preferences pref = Preferences.userRoot().node("fc");
    }
    
    public static Config getInstance() {
        return ConfigHolder.INSTANCE;
    }
    
    private static class ConfigHolder {

        private static final Config INSTANCE = new Config();
    }
    
    public Preferences getPref() {
        return pref;
    }
    
}
