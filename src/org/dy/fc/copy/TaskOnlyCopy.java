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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dy.fc.TaskExtMin;

/**
 *
 * @author yuri
 */
public class TaskOnlyCopy extends TaskExtMin<FileVisitResult>{
    private static final int DEF_BLOCK_SIZE = 1024 * 4;
    private Path source;
    private Path target;
    private LinkOption linkOption;
    private int blockSize;
    private long fileSize;
    private long writeBytes;
    
    private InputStream is = null;
    private OutputStream os = null;
    
    

    public TaskOnlyCopy(Path source, Path target, int blockSize, LinkOption linkOption) {
        this.source = source;
        this.target = target;
        this.blockSize = blockSize;
        this.linkOption = linkOption;
        try {
            fileSize = Files.size(source);
        } catch (IOException ex) {
            fileSize = 0L;
            Logger.getLogger(TaskOnlyCopy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TaskOnlyCopy(Path source, Path target) {
        this(source, target, DEF_BLOCK_SIZE, LinkOption.NOFOLLOW_LINKS);
    }
    
    private void updateInfo() {
        updateProgress(writeBytes, fileSize);
        updateMessage("" + writeBytes + " / " + fileSize);
    }
    
    @Override
    protected FileVisitResult call() throws Exception {
        if ( (Files.isDirectory(source, linkOption)) && (Files.isDirectory(target, linkOption)) ) {
            Files.copy(source, target, linkOption);
        } else {
            try {
                is = new FileInputStream(source.toFile());
                os = new FileOutputStream(target.toFile());           
                byte[] buffer = new byte[blockSize];
                int length;
                while ((length = is.read(buffer)) > 0) {

                    if (halt) {
                        halt();
                    }
                    if (isCancelled()) {
                        break;
                    }
                    os.write(buffer, 0, length);
                    writeBytes = writeBytes + length;
                    updateInfo();
                }
            } finally {
                is.close();
                os.close();
            }    
        }
        return null;
    }
    
}
