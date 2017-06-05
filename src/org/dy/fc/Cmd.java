/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author yuri
 */
public interface Cmd {
    public List<FileItem> ls(Path p) throws IOException;
    public void ls(Path p, List<FileItem> list) throws IOException;
    public long getSize();
    public long getNum();
}
