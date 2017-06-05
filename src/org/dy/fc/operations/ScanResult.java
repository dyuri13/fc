/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.operations;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 *
 * @author dubin
 */
public class ScanResult {

    private final LongProperty numItems = new SimpleLongProperty(0);

    public long getNumItems() {
        return numItems.get();
    }

    public void setNumItems(long value) {
        numItems.set(value);
    }

    public LongProperty numItemsProperty() {
        return numItems;
    }
    
    
//    private long numItems;
//    private long numDirs;
//    private long size;
//    private boolean complete = false;
    
/*    private final LongProperty numDirs = new SimpleLongProperty(0);

    public long getNumDirs() {
        return numDirs.get();
    }

    public void setNumDirs(long value) {
        numDirs.set(value);
    }

    public LongProperty numDirsProperty() {
        return numDirs;
    }
*/
    private final LongProperty size = new SimpleLongProperty(0);

    public long getSize() {
        return size.get();
    }

    public void setSize(long value) {
        size.set(value);
    }

    public LongProperty sizeProperty() {
        return size;
    }
    private final BooleanProperty complete = new SimpleBooleanProperty(false);

    public boolean isComplete() {
        return complete.get();
    }

    public void setComplete(boolean value) {
        complete.set(value);
    }

    public BooleanProperty completeProperty() {
        return complete;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").
                append("numItems : ").
                append(getNumItems()).
                append(", numDirs : ").
//                append(getNumDirs()).
                append(", size : ").
                append(getSize()).
                append(", complete : ").
                append(isComplete()).
                append("]");
        
        return sb.toString();
    }


    public static void main(String[] args) {
        ScanResult sr = new ScanResult();
        System.out.println(sr);
        sr.setNumItems(100);
//        sr.setNumDirs(10);
        sr.setSize(1000000000);
        sr.setComplete(true);
        System.out.println(sr);
    }
    
    
    
    
    
}
