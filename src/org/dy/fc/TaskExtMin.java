/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.stage.Window;

/**
 *
 * @author dubin
  * 
 * in call must insert
 *      if (halt) {
            halt();
        }
 * @param <T>
 */
public abstract class TaskExtMin<T> extends Task<T>{ //<V>
    volatile protected boolean halt = false;
    volatile private boolean suspended = false;
    protected final Object lock = new Object();
//    protected Window owner = null;
//    protected long maxProgress = -1;
    
    protected double workDone;
    protected double totalWork;
    
    private CountDownLatch cdl = new CountDownLatch(1);
    

    protected void halt() {
        System.out.println("TaskExtMin halt");
        halt = true;
        synchronized (lock) {
            suspended = true;
            while (halt) {
                try {
                    System.out.println("TaskExtMin lock.wait()");
                    cdl.countDown();
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskExtMin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            suspended = false;
            cdl = new CountDownLatch(1);
        }
    }
    
    public synchronized void suspend() {
        System.out.println("TaskExtMin suspend");
        halt = true;
//        workDone = getWorkDone();
//        totalWork = getTotalWork();
//        if ((workDone == -1) || (totalWork == -1)) {
//            updateProgress(0, 0);
//        }
    }
    
    public synchronized void resume() {
        System.out.println("TaskExtMin resume");
        halt = false;
        synchronized (lock) {
            System.out.println("TaskExtMin lock.notify()");
            lock.notify();
        }
//        updateProgress(workDone, totalWork);
    }
    
    public boolean isSuspended() {
        return suspended;
    }
        
    public CountDownLatch getCDL() {
        return cdl;
    }
    
}
