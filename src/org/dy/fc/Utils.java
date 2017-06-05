/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.image.ImageView;

/**
 *
 * @author yuri
 */
public class Utils {

    static IconManager im = IconManager.getInstance();
    
    static public String getTypeName(Path p) {
        final String DIR = "DIR";
        final String FILE = "FILE";
        final String LINK = "LINK";
        final String UP = "UP";
        final String UNKNOWN = "UNKNOWN";
        
        StringBuilder sb = new StringBuilder();
        
        if (Files.isSymbolicLink(p)) {
            sb.append(LINK);
            if (Files.isDirectory(p)) {
                sb.append("/").append(DIR);
            } else {
                if (Files.isRegularFile(p)) {
                    sb.append("/").append(FILE);
                }
            }
            
        } else {
            if (Files.isDirectory(p)) {
                String name = (p.getFileName() != null ? p.getFileName().toString() : "");
                if ("..".equals(name)) {
                    sb.append(UP);
                } else {
                    sb.append(DIR);
                }
                
            }
            
            if (Files.isRegularFile(p)) {
                //sb.append(FILE);
                sb.append(getExt(p.getFileName().toString()));
            }
            
        }
        
        return sb.toString();
    }
    
    public static String getExt(String s) {
        int i = s.lastIndexOf(".");
        if ( (i > 0) && (i < s.length())) {
            return s.substring(++i);
        }
        return "";
    }
    
    public static String toNumInUnits(long bytes) {
        int u = 0;
        for (;bytes > 1024*1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024)
            u++;
        return String.format("%.1f %cB", bytes/1024f, " kMGTPE".charAt(u));
    }    

    public static ImageView getImageView(Path f) {
        //IconManager im = IconManager.getInstance();
        ImageView imageView = null;
        
        if (Files.isSymbolicLink(f)) {
            if (Files.isDirectory(f)) {
                return (im.getCompositeIcon("folder", "link"));
            } else {
                if (Files.isRegularFile(f)) {
                    return (im.getCompositeIcon("file", "link"));
                } else {
                    return (im.getIcon("link"));
                }
            }
        }

        if (Files.isDirectory(f)) {
            if ("..".equals(f.getFileName())) {
                imageView = im.getIcon("parent");
            } else {
                imageView = im.getIcon("folder");
            }
        }
        
        if (Files.isRegularFile(f)) {
            imageView = im.getIcon(getExt(f.getFileName().toString()));
            if (imageView == null) {
                imageView = im.getIcon("file");
            }
            
        }

        return imageView;
    }
    
}
