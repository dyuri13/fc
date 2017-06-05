/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import org.dy.fc.MainPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.dy.fc.util.Os;

/**
 *
 * @author dubin
 */
public class MainMenu extends MenuBar{
    private MainPanel mp;

    public MainMenu(MainPanel mp) {
        this.mp = mp;
        setUseSystemMenuBar(true);
        init();
    }
    
    private void init() {
        
        initMenuFile();
        initMenuEdit();
        initMenuView();
        initMenuHelp();
    }
    
    private void initMenuFile() {
        Menu menuFile = new Menu("File");
        
        MenuItem fileChoiceDiskLeft = new MenuItem("Choice disk left");
        fileChoiceDiskLeft.setAccelerator(new KeyCodeCombination(KeyCode.F1, KeyCodeCombination.ALT_DOWN));
        fileChoiceDiskLeft.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                mp.showCBRLeft();
            }
        });
        menuFile.getItems().add(fileChoiceDiskLeft); 

        MenuItem fileChoiceDiskRight = new MenuItem("Choice disk right");
        fileChoiceDiskRight.setAccelerator(new KeyCodeCombination(KeyCode.F2, KeyCodeCombination.ALT_DOWN));
        fileChoiceDiskRight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                mp.showCBRRight();
            }
        });
        menuFile.getItems().add(fileChoiceDiskRight); 
        
        if (!Os.isMac()) {
            MenuItem fileQuit = new MenuItem("Quit");
            fileQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.SHORTCUT_DOWN));
            fileQuit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    mp.quit();
                }
            });
            menuFile.getItems().add(fileQuit); 
        }

       getMenus().add(menuFile);
    }
    
    private void initMenuEdit() {
        Menu menuEdit = new Menu("Edit");
        
        
        getMenus().add(menuEdit);
    }
    
    private void initMenuView() {
        Menu menuView = new Menu("View");
        
        CheckMenuItem viewShowHide = new CheckMenuItem("Show hiddden files");
        viewShowHide.selectedProperty().bindBidirectional(mp.showHiddenGlobalProperty());
        menuView.getItems().add(viewShowHide);
        
        MenuItem viewSwapPanles = new MenuItem("Swap panels");
        viewSwapPanles.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mp.swapPanels();
            }
        });
        menuView.getItems().add(viewSwapPanles);
        
        
        getMenus().add(menuView);
        
    }
    
    private void initMenuHelp() {
        Menu menuHelp = new Menu("Help");
        
        
        getMenus().add(menuHelp);
        
    }
    
    
}
