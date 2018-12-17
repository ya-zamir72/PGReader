/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author damir
 */
public class Query {
    
    StringProperty queryName = new SimpleStringProperty();
    StringProperty queryText = new SimpleStringProperty();
    Server queryServer = new Server();
    DataBase queryDataBase = new DataBase();
     
   public String getQueryName() {
        return queryName.get();
    }
    
    public void setQueryName(String queryName) {
        this.queryName.set(queryName);
    }
    
    public StringProperty queryNameProperty() {
        return queryName;
    }
    
    public String getQueryText() {
        return queryText.get();
    }
    
    public void setQueryText(String queryText) {
        this.queryText.set(queryText);
    }
    
    public StringProperty queryTextProperty() {
        return queryText;
    }
    
    public Server getQueryServer() {
        return queryServer;
    }
    
    public void setQueryServer(Server queryServer) {
        this.queryServer = queryServer;
    }
    
    public DataBase getQueryDataBase() {
        return queryDataBase;
    }
    
    public void setQueryDataBase(DataBase queryDataBase) {
        this.queryDataBase = queryDataBase;
    }
    
}
