/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author dubin
 */
public class MainTopBar extends ToolBar{
    
    private MainPanel mp;
    public TextField tfSearch = new TextField("");

    public MainTopBar(MainPanel mp) {

        this.mp = mp;
        ImageView showHiddenOn = IconManager.getInstance().getIcon("showhidden_on");
        ImageView showHiddenOff = IconManager.getInstance().getIcon("showhidden_off");
        
        Button tbShowHidden = new Button("", showHiddenOn);
        tbShowHidden.setFocusTraversable(false);
        
        Tooltip ttShow = new Tooltip("Click to show hidden files");
        Tooltip ttHide = new Tooltip("Click to hide hidden files");
        tbShowHidden.setTooltip( (mp.getShowHiddenGlobal()? ttHide : ttShow) );
        tbShowHidden.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mp.setShowHiddenGlobal(!mp.getShowHiddenGlobal());
                tbShowHidden.setTooltip( (mp.getShowHiddenGlobal() ? ttHide : ttShow) );
                tbShowHidden.setGraphic( (mp.getShowHiddenGlobal() ? showHiddenOn : showHiddenOff) );
            }
        });
        
        mp.showHiddenGlobalProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                tbShowHidden.setTooltip( (mp.getShowHiddenGlobal() ? ttHide : ttShow) );
                tbShowHidden.setGraphic( (mp.getShowHiddenGlobal() ? showHiddenOn : showHiddenOff) );
            }
        });
       
//        Button bHome = new Button("", IconManager.getInstance().getIcon("home"));
//        bHome.setFocusTraversable(false);
//        bHome.setTooltip(new Tooltip("Go to home folder"));
//        bHome.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                mp.goHome();
//            }
//        });
        
        
        
        Button bSwapPanels = new Button("swap");
        bSwapPanels.setFocusTraversable(false);
        bSwapPanels.setTooltip(new Tooltip("Swap panels"));
        bSwapPanels.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mp.swapPanels();
            }
        });
        
        
        Button b3 = new Button("3");
        b3.setFocusTraversable(false);
        
//        TextField tfSearch = new TextField("");
        tfSearch.setFocusTraversable(false);
        tfSearch.setPrefWidth(100);
        tfSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    FileItem fi = mp.getActivePanel().searchFileItemByName(newValue);

                    if (fi != null) {
                        mp.getActivePanel().cursorTo(fi);
                    } else {
                        tfSearch.setText(oldValue);
                    }
                }
            }
        });
        
        getItems().addAll(tbShowHidden, bSwapPanels, b3, tfSearch);

    }
    
    
    
    
}
