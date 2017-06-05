/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.comparators;

import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/**
 *
 * @author dubin
 */
public class ComparatorDateTimeOld implements Comparator<FileTime>{

    @Override
    public int compare(FileTime o1, FileTime o2) {
        if (o1.toMillis() == -100L) {
            return -1;
        }
        
        if (o2.toMillis() == -100L) {
            return -1;
        }
        return o1.compareTo(o2);
    }
    
}
