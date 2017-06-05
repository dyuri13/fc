/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.dy.fc.MainPanel;
import org.dy.fc.control.MyButtonBar;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 *
 * @author dubin
 */
public class MainBottomBar extends StackPane{
    private MainPanel mp;
    
    public MainBottomBar(MainPanel mp) {
        this.mp = mp;
        
        Button bView = new Button("F3 - View");
        Button bEdit = new Button("F4 - Edit");
        Button bCopy = new Button("F5 - Copy");
        Button bMove = new Button("F6 - Move");
        
        Button bMkDir = new Button("F7 - MkDir");
        bMkDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mp.doMakeDir(mp.getScene().getWindow());
            }
        });
                
        Button bDelete = new Button("F8 - Delete");
        
        MyButtonBar b = new MyButtonBar(0, bView, bEdit, bCopy, bMove, bMkDir, bDelete);
        
        getChildren().add(b);
    }
    
    
}
