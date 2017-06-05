/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;


import com.sun.javafx.tk.Toolkit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author dubin
 */
public class PlatformUtil {
    
    static public void runAndWait(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        
        try {
            FutureTask<Void> future = new FutureTask<>(runnable, null);
            Platform.runLater(future);
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static public <V> V runAndWait(final Callable<V> callable) {
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        try {
            FutureTask<V> future = new FutureTask<>(callable);
            Platform.runLater(future);
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static public void waitForPaintPulse() {
        PlatformUtil.runAndWait( () -> {
            Toolkit.getToolkit().firePulse();
        });
    }
    
}
