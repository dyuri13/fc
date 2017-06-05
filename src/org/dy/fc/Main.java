/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author yuri
 */
public class Main extends Application {
    private MainPanel mp;
    
    @Override
    public void start(Stage primaryStage) {
        mp = new MainPanel();
//        MainPanel root = new MainPanel();
//        root.getChildren().add(mp);
        
        Scene scene = new Scene(mp, 1280, 700);
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mp.quit();
                event.consume();
            }
        });
        
        primaryStage.setTitle("fc");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
