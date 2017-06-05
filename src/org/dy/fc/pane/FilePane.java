/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.pane;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.layout.BorderPane;
import org.dy.fc.FileItem;

/**
 *
 * @author dubin
 */
public interface FilePane {
    
    boolean isActive();
    ReadOnlyBooleanProperty activeProperty();
    void setActive();

    FileItem getSelectedFileItem();
    List<FileItem> getSelectedFileItems();
    
    
    void selectCurrentFileItem();

    void selectFileItems(boolean isDirSelect, String mask);
    void unselectFileItems(boolean isDirSelect, String mask);
    void unselectAll();
    
    void invertSelectionAll(boolean isDirSelect);
    
    void refresh();
    void refresh(FileItem select);
    
    void cursorTo(int i);
    void cursorTo(FileItem fi);
    int getCursorIndex();
    
    boolean getShowHidden();
    void setShowHidden(boolean b);
    BooleanProperty showHiddenProperty();
    
    public FileItem getCurrentLocationFileItem();
    public ObjectProperty<FileItem> currentLocationFileItemProperty();
    
    public void goHome();
    
    FileItem searchFileItemByName(String s);
    
}
