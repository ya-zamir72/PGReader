/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;

import javafx.fxml.FXML;
import pgreader.MainApp;
import pgreader.model.Query;
import pgreader.model.Server;

/**
 *
 * @author damir
 */

public class RootLayoutViewController {
   
    MainApp mainApp;
    
    @FXML
    private void handleNewServer() {
        Server temp = new Server();
        boolean okClicked = mainApp.showPGReaderNewServerView(temp);
        if(okClicked) {
            mainApp.getListServer().add(temp);
            mainApp.saveServerDataToFile();
            mainApp.overviewController.handleAddElement();
        }
    }
    
    @FXML
    private void handleRun() {
        sqlRun(false);
    }
    
    @FXML
    private void handleRunAndSave() {
        sqlRun(true);
    }
    
    @FXML
    private void handleSaveQuery() {
        String selectedTabName = mainApp.overviewController.tabPane.getSelectionModel().getSelectedItem().getText();
        for(PGReaderTabQueryController tabController : mainApp.tabsController) {
            if(selectedTabName.equals(tabController.setName + "*")) {
                tabController.updateQueryToFile();
            }
        }
    }
    
    private void sqlRun(boolean saveToXml) {
        String selectedTabName = mainApp.overviewController.tabPane.getSelectionModel().getSelectedItem().getText();
        for(PGReaderTabQueryController tabController : mainApp.tabsController) {
            if(selectedTabName.equals(tabController.setName)) {
                tabController.Run(saveToXml);
            }
        }
    }
    
    @FXML
    private void handleNewQuery() {
        Query temp = new Query();
        boolean okClicked = mainApp.showPGReaderNewQuery(temp);
        if(okClicked) {
            mainApp.getListQuery().add(temp);
            mainApp.saveQueryDataToFile();
            mainApp.overviewController.handleNewTabQuery(temp);
            mainApp.overviewController.handleAddElement();
        }
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
