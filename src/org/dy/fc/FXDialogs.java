/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import javafx.scene.control.Alert;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author yuri
 */
public class FXDialogs {

    public static void showInformation(String title, String header, String message) {
        showInformation(null, title, header, message);
    }
    
    public static void showInformation(Window window, String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        if (window != null) {
            alert.initOwner(window);
        }
        
        alert.showAndWait();
    }

    public static void showWarning(String title, String header, String message) {
        showWarning(null, title, header, message);
    }
    
    public static void showWarning(Window window, String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        if (window != null) {
            alert.initOwner(window);
        }
        
        alert.showAndWait();
    }

    public static void showError(String title, String header, String message) {
        showError(null, title, header, message);
    }
    
    public static void showError(Window window, String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        
        if (window != null) {
            alert.initOwner(window);
        }

        alert.showAndWait();
    }
    

    public static void showException(String title, String header, String message, Exception exception) {
        showException(null, title, header, message, exception);
    }
    
    public static void showException(Window owner, String title, String header, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        
        if (owner != null) {
            alert.initOwner(owner);
            //System.out.println("Owner");
        }
        
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Details:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    public static boolean showConfirm(Window window, String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        if (window != null) {
            alert.initOwner(window);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        
        return (result.get() == ButtonType.OK);
    }

    public static boolean showConfirm(Window window, String title, String header, String message, ButtonType ok, ButtonType cancel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ok, cancel);
        
        alert.initStyle(StageStyle.DECORATED);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        if (window != null) {
            alert.initOwner(window);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        
        return ( (result.get() == ButtonType.OK) || (result.get() == ButtonType.APPLY) || (result.get() == ButtonType.YES));
    }
    
    public static String showConfirm(Window owner, Alert.AlertType alertType, String title, String header, String content, String textOnCancelButton, String... buttonText) {
        Alert alert = new Alert(alertType);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        alert.getButtonTypes().clear();
        for (String bText : buttonText) {
            ButtonType b = new ButtonType(bText);
            alert.getButtonTypes().add(b);
        }
        alert.getButtonTypes().add(new ButtonType(textOnCancelButton, ButtonData.CANCEL_CLOSE));
        
        Optional<ButtonType> result = alert.showAndWait();
        
        
        return result.get().getText();
    }
    

    public static String showTextInput(String title, String header, String message, String defaultValue) {
        return showTextInput(null, title, header, message, defaultValue);
    }
    
    public static String showTextInput(Window window, String title, String header, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        if (window != null) {
            dialog.initOwner(window);
        }
        
        dialog.initStyle(StageStyle.DECORATED);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(message);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }    
}
