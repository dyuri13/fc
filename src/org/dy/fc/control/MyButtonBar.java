/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.control;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author dubin
 */
public class MyButtonBar extends HBox{

    public MyButtonBar(double spacing, Button... buttons) {
        super(spacing);
        setFocusTraversable(false);
        for (Button b : buttons) {
            HBox.setHgrow(b, Priority.ALWAYS);
            b.setMaxWidth(Double.MAX_VALUE);
            b.setFocusTraversable(false);
        }
        getChildren().addAll(buttons);
    }
    
}
