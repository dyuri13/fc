/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dy.fc.copy;

import java.nio.file.Path;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author yuri
 */
public class FileOpInfo {

    private final StringProperty name = new SimpleStringProperty();

    public String getOpName() {
        return name.get();
    }

    public void setOpName(String value) {
        name.set(value);
    }

    public StringProperty opNameProperty() {
        return name;
    }
    private final ObjectProperty<Path> source = new SimpleObjectProperty<>();

    public Path getSource() {
        return source.get();
    }

    public void setSource(Path value) {
        source.set(value);
    }

    public ObjectProperty sourceProperty() {
        return source;
    }
    private final ObjectProperty<Path> target = new SimpleObjectProperty<>();

    public Path getTarget() {
        return target.get();
    }

    public void setTarget(Path value) {
        target.set(value);
    }

    public ObjectProperty targetProperty() {
        return target;
    }
    private final LongProperty workDone = new SimpleLongProperty();

    public long getWorkDone() {
        return workDone.get();
    }

    public void setWorkDone(long value) {
        workDone.set(value);
    }

    public LongProperty workDoneProperty() {
        return workDone;
    }
    private final LongProperty workAll = new SimpleLongProperty();

    public long getWorkAll() {
        return workAll.get();
    }

    public void setWorkAll(long value) {
        workAll.set(value);
    }

    public LongProperty workAllProperty() {
        return workAll;
    }
    
    
    
}
