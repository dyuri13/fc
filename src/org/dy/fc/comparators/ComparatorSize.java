/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.comparators;

import org.dy.fc.FileItem;
import java.nio.file.Files;
import java.util.Comparator;

/**
 *
 * @author dubin
 */
public class ComparatorSize implements Comparator<FileItem>{

    private boolean folderFirst = true;
    
    @Override
    public int compare(FileItem o1, FileItem o2) {
        if ("..".equals(o1.getName())) {
            return -1;
        }
        
        if ("..".equals(o2.getName())) {
            return -1;
        }
        
        if ( (Files.isDirectory(o1.getPath())) &&  (Files.isDirectory(o2.getPath()))) {
            return o1.getName().compareTo(o2.getName());
        }
        
        if (Files.isDirectory(o1.getPath()) && (!Files.isDirectory(o2.getPath())) ) {
            return -1;
        }

        if (Files.isDirectory(o2.getPath()) && (!Files.isDirectory(o1.getPath())) ) {
            return 1;
        }
        
        if (o1.getSize() < o2.getSize()) {
            return -1;
        } else {
            return (o1.getSize() > o2.getSize() ? 1 : 0);
        }
    }
    
}
