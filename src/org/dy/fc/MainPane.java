/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import javafx.beans.property.BooleanProperty;
import org.dy.fc.pane.FilePane;

/**
 *
 * @author dubin
 */
public interface MainPane {
    
    
    FilePane getActivePanel();
    FilePane getInactivePanel();
    void setActivePanel(FilePane p);

    void refreshPanel(FilePane p);
    
    boolean getShowHiddenGlobal();
    void setShowHiddenGlobal(boolean b);
    BooleanProperty showHiddenGlobalProperty();
    
    void swapPanels();
    void goHome();
    
    
}
