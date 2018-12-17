/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pgreader.MainApp;
import pgreader.model.DataBase;
import pgreader.model.Query;
import pgreader.model.Server;
import pgreader.model.Table;

/**
 *
 * @author damir
 */
public class PGReaderNewQueryController {
    
    @FXML
    private BorderPane borderPane;
    @FXML 
    private TreeView<String> treeView;
    @FXML
    private TextField textFieldName;

    private MainApp mainApp;
    private Stage dialogStage;
    private ObservableList<Server> servers = FXCollections.observableArrayList();
    private ObservableList<Query> queries;
    private Server selectedServer;
    private DataBase selectedDataBase;
    private Query temp;
    private boolean okClicked = false;
    
    /*Страшный способ заполнения TreeView*/
    private void setTreeViewElements() {
        TreeItem<String> rootItem = new TreeItem<String>("Список серверов", new ImageView(mainApp.rootIcon));
        rootItem.setExpanded(true);
        for(Server server : servers) {
            TreeItem<String> serverTree = new TreeItem<String>(server.getCustomServerName(), new ImageView(mainApp.serverIcon));
            serverTree.setExpanded(server.getOpened());
            for(DataBase database : server.getServerDataBases()) {
                TreeItem<String> databaseTree = new TreeItem<String>(database.getDataBaseName(), new ImageView(mainApp.databaseIcon));
                databaseTree.setExpanded(database.getOpened());
                for(Table table : database.getDataBaseTables()) {
                    TreeItem<String> tableTree = new TreeItem<String>(table.getTableName(), new ImageView(mainApp.tableIcon));
                    databaseTree.getChildren().add(tableTree);
                }   
                serverTree.getChildren().add(databaseTree);
            }            
            rootItem.getChildren().add(serverTree);
        }
        treeView = new TreeView<String>(rootItem);
        treeView.setShowRoot(false);
        treeView.setOnMouseClicked( new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    clickedElement(mouseEvent);
                } catch(Exception e) {
                    
                }
            }
        });
        borderPane.setCenter(treeView);
    }
    
    /*Страшный способ обработки клика*/
    private void clickedElement(MouseEvent mouseEvent) {
        try {
            if(mouseEvent.getClickCount() == 1) {
                for(Server searchServer : servers) {
                    if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchServer.getCustomServerName())) {
                        searchServer.switchOpened();
                        treeView.getSelectionModel().getSelectedItem().setExpanded(searchServer.getOpened());
                    } else {
                        if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals(searchServer.getCustomServerName())) {
                            for(DataBase searchDataBase : searchServer.getServerDataBases()) {
                                if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchDataBase.getDataBaseName())) {
                                    searchDataBase.switchOpened();
                                    treeView.getSelectionModel().getSelectedItem().setExpanded(searchDataBase.getOpened());
                                }
                            }
                        }
                    }
                }
                treeView.getSelectionModel().select(-1);
            }
            if(mouseEvent.getClickCount() == 2) {
                for(Server searchServer : servers) {
                    if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchServer.getCustomServerName())) {
                        searchServer.setServerDataBases(requestListDatabases(searchServer));
                        searchServer.setOpened(true);
                        break;
                    }
                    if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals(searchServer.getCustomServerName())) {
                        selectedServer = searchServer;
                        for(DataBase searchDataBase : searchServer.getServerDataBases()) {
                            if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchDataBase.getDataBaseName())) {
                                selectedDataBase = searchDataBase;
                                searchDataBase.setDataBaseTables(requestListTables(searchServer));
                                searchDataBase.setOpened(true);
                            }
                        }
                        break;
                    }
                }
                treeView.getSelectionModel().select(-1);
                setTreeViewElements();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /*Запрос списка бд сервера*/
    private ObservableList<DataBase> requestListDatabases (Server searchServer) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.
                    getConnection("jdbc:postgresql://" + searchServer.getServerHost() + ":" + searchServer.getServerPort() + "/postgres", 
                            searchServer.getServerUser(), 
                            searchServer.getServerPassword());
            c.setAutoCommit(false);

            ObservableList<DataBase> dataBases = FXCollections.observableArrayList();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false;");
            while (rs.next()) {
                String dataBaseName = rs.getString("datname");
                DataBase dataBase = new DataBase();
                dataBase.setDataBaseName(dataBaseName);
                dataBase.setDataBaseParent(searchServer.getCustomServerName());
                dataBases.add(dataBase);

            }
            rs.close();
            stmt.close();
            c.close();

            return dataBases;
        } catch(Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
    
    /*Запрос списка таблиц бд*/
    private ObservableList<Table> requestListTables(Server searchServer) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.
                    getConnection("jdbc:postgresql://" 
                            + searchServer.getServerHost() + ":" 
                            + searchServer.getServerPort() + "/" 
                            + treeView.getSelectionModel().getSelectedItem().getValue(), 
                    searchServer.getServerUser(), 
                    searchServer.getServerPassword());
            c.setAutoCommit(false);

            ObservableList<Table> tables = FXCollections.observableArrayList();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema NOT IN('information_schema','pg_catalog');");
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                Table table = new Table();
                table.setTableName(tableName);
                table.setTableParent(searchServer.getCustomServerName());
                tables.add(table);
            }
            rs.close();
            stmt.close();
            c.close();
            
            return tables;
        } catch(Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
        
    @FXML
    private void handleOkClicked() {
        String newName = textFieldName.getText().trim();
        boolean isQuery = false;
        for(Query elem : queries) {
            if(elem.getQueryName().equals(newName)) {
                isQuery = true;
                break;
            }
        }
        if((selectedServer == null)&&(selectedDataBase == null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Вы не выбрали сервер и базу данных");
            alert.setContentText("Выберите сервер и базу данных из списка");

            alert.showAndWait();
        } else {
            if(isQuery) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Запрос с таким названием существует");
                alert.setContentText("Придумайте другое название");

                alert.showAndWait();
            } else {
                if(!newName.isEmpty()) {
                    temp.setQueryServer(selectedServer);
                    temp.setQueryDataBase(selectedDataBase);
                    temp.setQueryName(newName);
                    temp.setQueryText("");
                    okClicked = true;
                    dialogStage.close();
                }
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        servers = mainApp.getListServer();
        for(Server elem : servers) {
            elem.setOpened(false);
            elem.setServerDataBases(FXCollections.observableArrayList());
        }
        queries = mainApp.getListQuery();
        setTreeViewElements();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public boolean isOkClicked(){
        return okClicked;
    }
    
    public void setQuery(Query temp){
        this.temp = temp;
    }

}
