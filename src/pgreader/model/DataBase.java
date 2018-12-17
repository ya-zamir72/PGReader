/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author damir
 */
public class DataBase {
    private StringProperty dataBaseName = new SimpleStringProperty();
    private StringProperty dataBaseParent = new SimpleStringProperty();
    private ObservableList<Table> dataBaseTables = FXCollections.observableArrayList();
    private boolean opened = false;
    
    public String getDataBaseName() {
        return dataBaseName.get();
    }
    
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName.set(dataBaseName);
    }
    
    public StringProperty dataBaseNameProperty() {
        return dataBaseName;
    }
    
    public String getDataBaseParent() {
        return dataBaseParent.get();
    }
    
    public void setDataBaseParent(String dataBaseParent) {
        this.dataBaseParent.set(dataBaseParent);
    }
    
    public StringProperty dataBaseParentProperty() {
        return dataBaseParent;
    }
        
    public ObservableList<Table> getDataBaseTables() {
        return dataBaseTables;
    }
    
    public void setDataBaseTables(ObservableList dataBaseTables) {
        this.dataBaseTables = dataBaseTables;
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
