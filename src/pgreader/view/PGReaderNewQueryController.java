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
import javafx.collections.ListChangeListener;
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
import pgreader.model.MultiObject;
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
    private TreeView<MultiObject> treeView;
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
    
    /*Заполняет TreeView списоком серверов*/
    private void setTreeViewElements(ObservableList<MultiObject> listMultiObject) {
        
        MultiObject rootElement = new MultiObject("String");
        rootElement.setObject("Список серверов");
        
        TreeItem<MultiObject> rootItem = new TreeItem<MultiObject>(rootElement, new ImageView(mainApp.rootIcon));
        rootItem.setExpanded(true);
        
        ObservableList<TreeItem<MultiObject>> listTree = FXCollections.observableArrayList();
        
        for(MultiObject moServer : listMultiObject) {
            TreeItem<MultiObject> treeItemServer = new TreeItem<MultiObject>(moServer, new ImageView(mainApp.serverIcon));
            listTree.add(treeItemServer);
        }
        rootItem.getChildren().addAll(listTree);
        treeView = new TreeView<MultiObject>(rootItem);
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
    /*При клике запрашивает список бд и добавляет в TreeView*/
    private void clickedElement(MouseEvent event) {
        try {
            if(event.getClickCount() == 2) {
                String typeSelectedElement = treeView.getSelectionModel().getSelectedItem().getValue().getType();
                if(!treeView.getSelectionModel().getSelectedItem().isExpanded()) {
                    if(typeSelectedElement.equals("Server")) {
                        Server selectedServer = (Server) treeView.getSelectionModel().getSelectedItem().getValue().getObject();
                        selectedServer.setServerDataBases(requestListDatabases(selectedServer));
                        ObservableList<TreeItem<MultiObject>> listTreeItemDataBases = FXCollections.observableArrayList();
                        for(DataBase db : selectedServer.getServerDataBases()) {
                            System.out.println(db.getDataBaseName());
                            MultiObject moDataBase = new MultiObject("DataBase");
                            moDataBase.setObject(db);
                            TreeItem<MultiObject> treeItemDataBase = new TreeItem<MultiObject>(moDataBase, new ImageView(mainApp.databaseIcon));
                            listTreeItemDataBases.add(treeItemDataBase);
                        }
                        
                        treeView.getSelectionModel().getSelectedItem().getChildren().clear();
                        treeView.getSelectionModel().getSelectedItem().getChildren().addAll(listTreeItemDataBases);
                        treeView.getSelectionModel().getSelectedItem().setExpanded(true);
                        
                    } else {
                        treeView.getSelectionModel().getSelectedItem().setExpanded(false);
                    }
                    if(typeSelectedElement.equals("DataBase")) {
                        selectedServer = (Server) treeView.getSelectionModel().getSelectedItem().getParent().getValue().getObject();
                        selectedDataBase = (DataBase) treeView.getSelectionModel().getSelectedItem().getValue().getObject();
                    }
                }
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
        
        ObservableList<MultiObject> listMultiObject = FXCollections.observableArrayList();
        
        for(Server element : servers) {
            MultiObject mObject = new MultiObject("Server");
            mObject.setObject((Object) element);
            listMultiObject.add(mObject);
        }
        
        treeView.setEditable(true);
        setTreeViewElements(listMultiObject);
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
