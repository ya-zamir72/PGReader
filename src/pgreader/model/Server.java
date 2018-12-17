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
public class Server {
    private StringProperty serverName = new SimpleStringProperty();
    private StringProperty serverHost = new SimpleStringProperty();
    private StringProperty serverPort = new SimpleStringProperty();
    private StringProperty serverUser = new SimpleStringProperty();
    private StringProperty serverPassword = new SimpleStringProperty();
    private ObservableList<DataBase> serverDataBases = FXCollections.observableArrayList();
    private boolean opened = false;
    
    public String getServerName() {
        return serverName.get();
    }
    
    public void setServerName(String serverName) {
        this.serverName.set(serverName);
    }
    
    public StringProperty serverNameProperty() {
        return serverName;
    }
    
    public String getServerHost() {
        return serverHost.get();
    }
    
    public void setServerHost(String serverHost) {
        this.serverHost.set(serverHost);
    }
    
    public StringProperty serverHostProperty() {
        return serverHost;
    }
    
    public String getServerPort() {
        return serverPort.get();
    }
    
    public void setServerPort(String serverPort) {
        this.serverPort.set(serverPort);
    }
    
    public StringProperty serverPortProperty() {
        return serverPort;
    }
    
    public String getServerUser() {
        return serverUser.get();
    }
    
    public void setServerUser(String serverUser) {
        this.serverUser.set(serverUser);
    }
    
    public StringProperty serverUserProperty() {
        return serverUser;
    }
    
    public String getServerPassword() {
        return serverPassword.get();
    }
    
    public void setServerPassword(String serverPassword) {
        this.serverPassword.set(serverPassword);
    }
    
    public StringProperty serverPasswordProperty() {
        return serverPassword;
    }
    
    public ObservableList<DataBase> getServerDataBases() {
        return serverDataBases;
    }
    
    public void setServerDataBases(ObservableList serverDataBases) {
        this.serverDataBases = serverDataBases;
    }
    
    public String getCustomServerName() {
        return serverName.get() + " (" + serverHost.get() + ":" + serverPort.get() + ")";
    }
    
    public boolean getOpened() {
        return opened;
    }
    
    public void switchOpened() {
        opened = opened ? false : true;
    }
    
    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
