/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dubin
 */
public class FileUtils {
    
    public static boolean isReadOnly(Path p) {
        boolean result = false;
        FileStore fs = null;
        DosFileAttributes dfa;
        
        try {
            fs = Files.getFileStore(p);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        
        if ( (fs != null) && (fs.supportsFileAttributeView(DosFileAttributeView.class)) ) {
            try {
		dfa = Files.readAttributes(p, DosFileAttributes.class);
                result = dfa.isReadOnly();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                result = false;
            }
            
        }
        return result;
    }
    
    public static boolean setReadOnly(Path p, boolean set) {
        boolean result;
        try {
            Files.setAttribute(p, "dos:readonly", set);
            result = true;
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        return result;
    }
    
    public static void main(String[] args) {
        
    }
    
}
