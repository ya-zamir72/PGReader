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
import static javafx.scene.control.Alert.AlertType.ERROR;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pgreader.MainApp;
import pgreader.model.Query;

/**
 *
 * @author damir
 */
public class PGReaderNewNameController {
    
    @FXML
    private TextField newNameTextField;
    
    private MainApp mainApp;
    private Stage dialogStage;
    private boolean okClicked = false;
    private String newName;
    private ObservableList<Query> listQuery;
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    public String getNewName() {
        return newName;
    }
    
    @FXML
    public void handleOkClicked() {
        newName = newNameTextField.getText().trim();
        boolean isQuery = false;
        for(Query elem : listQuery) {
            if(elem.getQueryName().equals(newName)) {
                isQuery = true;
                break;
            }
        }
        if(isQuery) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Запрос с таким названием существует");
            alert.setContentText("Придумайте другое название");

            alert.showAndWait();
        } else {
            if(!newName.isEmpty()) {
                okClicked = true;
                dialogStage.close();
            }
        }
    }
    
    @FXML
    public void handleCancel() {
        newName = null;
        dialogStage.close();
    }
    
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public void setListQuery(ObservableList<Query> listQuery) {
        this.listQuery = listQuery;
    }
}
