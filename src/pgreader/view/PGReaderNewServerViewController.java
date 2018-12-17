/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pgreader.model.Server;

/**
 *
 * @author damir
 */
public class PGReaderNewServerViewController {
    
    @FXML
    private TextField serverHost;
    @FXML
    private TextField serverName;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField serverUser;
    @FXML
    private TextField serverPassword;
    
    private Stage dialogStage;
    private Server server;
    private boolean okClicked = false;
    private ObservableList<Server> listServer;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    public void handleOkClicked() {
        boolean correct = false;
        String host = serverHost.getText().trim();
        String name = serverName.getText().trim();
        String port = serverPort.getText().trim();
        String user = serverUser.getText().trim();
        String password = serverPassword.getText().trim();

        boolean isServer = false;
        for(Server elem : listServer) {
            if(elem.getServerName().equals(name)) {
                isServer = true;
                break;
            }
        }
        
        if(isServer) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Сервер с таким названием существует");
            alert.setContentText("Придумайте другое название");

            alert.showAndWait();
        } else {
            if(host.isEmpty()||name.isEmpty()||port.isEmpty()||user.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не все поля заполнены");
                alert.setContentText("Заполните поля");

                alert.showAndWait();
            } else {
                server.setServerHost(serverHost.getText());
                server.setServerName(serverName.getText());
                server.setServerPort(serverPort.getText());
                server.setServerUser(serverUser.getText());
                server.setServerPassword(serverPassword.getText());
                server.setServerDataBases(FXCollections.observableArrayList());
                okClicked = true;
                dialogStage.close();
            }
        }    
    }
    
    @FXML
    public void handleCancel() {
        dialogStage.close();
    }
    
    public void setListServer(ObservableList<Server> listServer) {
        this.listServer = listServer;
    }
}
