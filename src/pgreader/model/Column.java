/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author damir
 */
public class Column {
    Boolean select = false;
    private StringProperty columnName = new SimpleStringProperty();
    private StringProperty columnType = new SimpleStringProperty();
    private StringProperty columnParent = new SimpleStringProperty();
    
    private boolean opened = false;
    
    public void setSelect(Boolean isSelect) {
        this.select = isSelect;
    }
    
    public Boolean getSelect() {
        return select;
    }
    
    public String getColumnName() {
        return columnName.get();
    }
    
    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }
   
    public StringProperty columnNameProperty() {
        return columnName;
    }
    
    public String getColumnType() {
        return columnType.get();
    }
    
    public void setColumnType(String columnType) {
        this.columnType.set(columnType);
    }
    
    public StringProperty columnTypeProperty() {
        return columnType;
    }
    
    public String getColumnParent() {
        return columnParent.get();
    }
    
    public void setColumnParent(String columnParent) {
        this.columnParent.set(columnParent);
    }
    
    public StringProperty columnParentProperty() {
        return columnParent;
    }
    
    public String getCustomColumnName() {
        return columnName.get() + " / " + columnType.get();
    }
    
    public boolean getOpened() {
        return opened;
    }
    
    public void switchOpened() {
        opened = !opened;
    }
    
    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
