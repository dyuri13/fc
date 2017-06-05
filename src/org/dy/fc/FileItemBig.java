/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author dubin
 */
public class FileItemBig {
    
    private final Path p;
    private final String name;
    private final String type;
    private long size;
    private FileTime dateTime;
    private boolean hidden;
    private final ImageView icon;
    private boolean selected = false;
    
    private boolean regularFile = false;
    

    public FileItemBig(String s){
        this(Paths.get(s));
    }

    public FileItemBig(URI uri) {
        this(Paths.get(uri));
    }
    
    public FileItemBig(Path p) {
        this.p = p;
        
        name = (p.getFileName() != null ? p.getFileName().toString() : p.toString());
        
        try {
            size = Files.size(p);
        } catch (IOException ex) {
            size = -1L;
            Logger.getLogger(FileItemBig.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        try {
            dateTime = Files.getLastModifiedTime(p, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException ex) {
            dateTime = null;
            Logger.getLogger(FileItemBig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            hidden = Files.isHidden(p);
        } catch (IOException ex) {
            hidden = false;
            Logger.getLogger(FileItemBig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        type = _getType(p);
        
        if ("..".equals(getFileName())) {
            icon = IconManager.getInstance().getIcon("parent");
        } else {
            icon = IconManager.getInstance().getIconfromSystemBySwing(p);;
        }
        
    }
    
    public String getFileName() {
        return name;
    }
    
    
    public Path getPath() {
        return p;
    }
    
    private String getExt(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');
        if (i > 0) {
            ext = s.substring(i+1);
        }        
        return ext;
    }
    
    public String getType() {
        return type;
    }
    
    private String _getType(Path p) {
        final String DIR = "DIR";
        final String FILE = "FILE";
        final String LINK = "LINK";
        final String UP = "UP";
        final String UNKNOWN = "UNKNOWN";
        
        String result = "";
        if (Files.isSymbolicLink(p)) {
            result = result + LINK;
            if (Files.isDirectory(p)) {
                result = result + "/" + DIR;
            } else {
                if (Files.isRegularFile(p)) {
                    result = result + "/" + FILE;
                }
            }
            
        } else {
            if (Files.isDirectory(p)) {
                String name = (p.getFileName() != null ? p.getFileName().toString() : "");
                if ("..".equals(name)) {
                    result = result + UP;
                } else {
                    result = result + DIR;
                }
                
            }
            
            if (Files.isRegularFile(p)) {
                result = result + getExt(p.getFileName().toString());
            }
            
        }
        
        return result;
    }
    
    private String toNumInUnits(long bytes) {
        int u = 0;
        for (;bytes > 1024*1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024)
            u++;
        return String.format("%.1f %cB", bytes/1024f, " kMGTPE".charAt(u));
    }    
    
    public long getSize() {
        return size;
    }
    
    public FileTime getDateTime() {
        return dateTime;
    }
    

    public boolean isHidden() {
        return hidden;
    }
    
    public ImageView getIcon() {
        return icon;
    }
    
    public FileItemBig getThis() {
        return this;
    }
    

    public boolean isRegularFile(LinkOption... options) {
        return Files.isRegularFile(p, options);
    }

    public boolean isDirectory(LinkOption... options) {
        return Files.isDirectory(p, options);
    }
    
    public FileItemBig getParent() {
        return new FileItemBig(p.getParent());
    }
    
    public FileItemBig resolve(String s) {
        return new FileItemBig(p.resolve(s));
    }
    
    public File toFile() {
        return p.toFile();
    }
    
/*
    private Image getImagefromSwing(Path item) {
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(item.toFile());
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bi.getGraphics(), 0, 0);
        Image im = SwingFXUtils.toFXImage(bi, null);
        return im;
    }
*/
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean b) {
        selected = b;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof FileItemBig) {
            FileItemBig fi = (FileItemBig) obj;
            try {
                if ( (Files.isSameFile(fi.getPath(), this.getPath())) && (fi.getFileName().equals(this.getFileName())) ) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException ex) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 89 * hash + Objects.hashCode(this.dateTime);
        hash = 89 * hash + (this.hidden ? 1 : 0);
        hash = 89 * hash + (this.selected ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.icon);
        return hash;
    }
    
@Override
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[");
//        sb.append(" name : ");
//        sb.append(getName());
//        sb.append(", size : ");
//        sb.append(getSize());
//        sb.append(", hidden : ");
//        sb.append(isHidden());
//        sb.append(", dateTime : ");
//        sb.append(getDateTime());
//        sb.append(", icon : ");
//        sb.append(getIcon());
//        sb.append(" ]");
//        return sb.toString();
        return p.toString();
    }    
    
    
}
