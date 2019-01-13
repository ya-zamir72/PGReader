package pgreader.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pgreader.model.Column;

/**
 *
 * @author damir
 */
public class Table {
    private StringProperty tableName = new SimpleStringProperty();
    private StringProperty tableParent = new SimpleStringProperty();
    private ObservableList<Column> tableColumns = FXCollections.observableArrayList();
    private boolean opened = false;
    
    public String getTableName() {
        return tableName.get();
    }
    
    public void setTableName(String tableName) {
        this.tableName.set(tableName);
    }
    
    public StringProperty tableNameProperty() {
        return tableName;
    }
    
    public String getTableParent() {
        return tableParent.get();
    }
    
    public void setTableParent(String tableParent) {
        this.tableParent.set(tableParent);
    }
    
    public StringProperty tableParentProperty() {
        return tableParent;
    }
        
    public ObservableList<Column> getTableColumns() {
        return tableColumns;
    }
    
    public void setTableColumns(ObservableList tableColumns) {
        this.tableColumns = tableColumns;
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
    
    @Override
    public String toString() {
        return tableName.get();
    }
}
