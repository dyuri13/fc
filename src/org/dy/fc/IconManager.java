/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author dubin
 */
public class IconManager {
    
    private final String EXT = ".png";
    private final String PATH = "/icons/";
    private final static Map<String, ImageView> CACHE = new HashMap<>();;
    private static IconManager INSTANCE;

    
    private IconManager() {
    }
    
    public static IconManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IconManager();
        }
        return INSTANCE;
    }
    
    
    public ImageView getIcon(String name) {
        String fullName = PATH + name + EXT;
        
        //ImageView imageView = CACHE.get(fullName);
        ImageView imageView = null;
        
        if (imageView == null) {
//            System.out.println("-------------------");
//            System.out.println("not in cache " + fullName);
//            System.out.println("cache.size() :  " + CACHE.size());
//            System.out.println("cache :  " + CACHE);
//            System.out.println("-------------------");
            InputStream is = getClass().getResourceAsStream(fullName);

            if (is != null)  {
                imageView = new ImageView(new Image(is, 16, 16, false, false));
//                System.out.println("-------------------");
//                System.out.println("new ImageView : " + imageView);
                //CACHE.put(fullName, imageView);
                
//                System.out.println("put in cache " + fullName);
//                System.out.println("cache.size() :  " + CACHE.size());
//                System.out.println("cache :  " + CACHE);
//                System.out.println("-------------------");
            } else {
//                System.out.println("-------------------");
//                System.out.println("getResourceAsStyream null? : " + is);
//                System.out.println("imageView : " + imageView);
//                  System.out.println("-------------------");
            }
        } else { // not in CACHE
//            System.out.println("-------------------");
//            System.out.println("in cache " + fullName);
//            System.out.println("cache.size() :  " + CACHE.size());
//            System.out.println("cache :  " + CACHE);
//            System.out.println("-------------------");
        }
        //System.out.println("IconManager : fullName = " + fullName + " imageView = " + imageView);
        return imageView;
    }
    
    
    private ImageView getCompositeIcon(ImageView background, ImageView foreground) {
        
        foreground.setBlendMode(BlendMode.DIFFERENCE);
        
        Group blend = new Group(background, foreground);
        
        WritableImage image = blend.snapshot(new SnapshotParameters(), null);
        
        return new ImageView(image);
    }
    
    public ImageView getCompositeIcon(String background, String foreground) {
        return getCompositeIcon(getIcon(background), getIcon(foreground));
    }
    
    public ImageView getIconfromSystemBySwing(Path item) {
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(item.toFile());
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bi.getGraphics(), 0, 0);
        Image im = SwingFXUtils.toFXImage(bi, null);
        return new ImageView(im);
    }

    
}
