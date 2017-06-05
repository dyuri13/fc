/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Window;

/**
 *
 * @author yuri
 */

public class AskActionInTask {
    private Window owner;
//    private Exception ex;
    private String text = "";
    private String[] buttonTexts;
    private Alert.AlertType alertType;

//    public AskActionInTask(Window owner, Exception ex, String... buttonTexts) {
//        this.owner = owner;
//        this.ex = ex;
//        this.buttonTexts = buttonTexts;        
//    }

    public AskActionInTask(Window owner, Alert.AlertType alertType, String text, String... buttonTexts) {
        this.owner = owner;
        this.text = text;
        this.buttonTexts = buttonTexts;
        this.alertType = alertType;
    }
    
    public String get() {
        String result = "";
        FutureTask<String> ft = new FutureTask(new Ask());
        Platform.runLater(ft);
        try {
            result = ft.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(AskActionInTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(AskActionInTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    class Ask implements Callable<String> {

        @Override
        public String call() throws Exception {
            String result = FXDialogs.showConfirm(owner, alertType, 
                    "Error", null, text, "Abort", buttonTexts);
            return result;
        }
        
    }
    
}