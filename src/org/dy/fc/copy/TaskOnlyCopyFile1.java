/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.copy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dy.fc.TaskExtMin;

/**
 *
 * @author yuri
 */
public class TaskOnlyCopyFile1 extends TaskExtMin<FileOpInfo>{
    private static final int DEF_BLOCK_SIZE = 1024 * 4;
    private Path source;
    private Path target;
    private int blockSize;
    private long fileSize;
    private long writeBytes;
    
    private InputStream is = null;
    private OutputStream os = null;
    
    private FileOpInfo op;
    

    public TaskOnlyCopyFile1(Path source, Path target, int blockSize) {
        this.source = source;
        this.target = target;
        this.blockSize = blockSize;
        op = new FileOpInfo();
        op.setOpName("Copy");
        op.setSource(source);
        op.setTarget(target);
        try {
            fileSize = Files.size(source);
        } catch (IOException ex) {
            fileSize = 0L;
            Logger.getLogger(TaskOnlyCopyFile1.class.getName()).log(Level.SEVERE, null, ex);
        }
        op.setWorkAll(fileSize);
    }

    public TaskOnlyCopyFile1(Path source, Path target) {
        this(source, target, DEF_BLOCK_SIZE);
    }
    
    private void updateInfo() {
        updateProgress(writeBytes, fileSize);
        updateTitle(source.getFileName().toString());
//        updateMessage("" + writeBytes + " / " + fileSize);
        updateMessage(target.toString());
        op.setWorkDone(writeBytes);
        updateValue(op);
    }
    
    @Override
    protected FileOpInfo call() throws Exception {
        try {
            is = new FileInputStream(source.toFile());
            os = new FileOutputStream(target.toFile());           
            byte[] buffer = new byte[blockSize];
            int length;
            while ((length = is.read(buffer)) > 0) {
                updateInfo();
                if (halt) {
                    halt();
                }
                if (isCancelled()) {
                    break;
                }
                os.write(buffer, 0, length);
                writeBytes = writeBytes + length;
            }
        } finally {
            is.close();
            os.close();
        }    
        return null;
    }
    
}
