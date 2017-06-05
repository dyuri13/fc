/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author dubin
 */
public class CmdIo implements Cmd{

    private long allNum = 0;
    private long allSize = 0;
    
    @Override
    public List<FileItem> ls(Path p) throws IOException {
        long num = 0;
        long size = 0;
        
        File[] fList = p.toFile().listFiles();
        List<FileItem> list = new ArrayList<>();
        
        if (p.getParent() != null) {
            list.add(new FileItem(p.resolve("..")));
            num++;
        }
        
        for (File f : fList) {
            FileItem fi = new FileItem(f.getPath());
            list.add(fi);
            num++;
            if (f.isFile()) {
                size = size + f.length();
            }
        }
        allNum = num;
        allSize = size;
        return list;
    }

    @Override
    public void ls(Path p, List<FileItem> list) throws IOException {
        long num = 0;
        long size = 0;
        
        long start = 0;
            

        start = System.currentTimeMillis();
        
        
        File[] fList = p.toFile().listFiles();
        list.clear();
        
        if (p.getParent() != null) {
            list.add(new FileItem(p.resolve("..")));
            num++;
        }
        
        for (File f : fList) {
            FileItem fi = new FileItem(f.getPath());
            list.add(fi);
            num++;
            if (f.isFile()) {
                size = size + f.length();
            }
        }
        allNum = num;
        allSize = size;
        System.out.println("ls io time : " + (System.currentTimeMillis() - start));
    }

    @Override
    public long getSize() {
        return allSize;
    }

    @Override
    public long getNum() {
        return allNum;
    }
    
}
