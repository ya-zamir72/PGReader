/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import pgreader.MainApp;
import pgreader.model.Column;
import pgreader.model.DataBase;
import pgreader.model.Server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import pgreader.model.Query;
import pgreader.model.Table;

/**
 *
 * @author damir
 */
public class PGReaderOverviewController implements Initializable {
   
    @FXML
    private TreeView<String> treeView;
    @FXML
    private BorderPane leftBorderPane;
    @FXML
    public TabPane tabPane;
    private ContextMenu contextMenu = new ContextMenu();

    private MainApp mainApp;
    private ObservableList<Server> elementList = FXCollections.observableArrayList();
    private ObservableList<Query> elementQuery = FXCollections.observableArrayList();
    
    private Server selectedServer = new Server();
    private DataBase selectedDataBase = new DataBase();
    private int countTabs = 0;
    private boolean openedQuery = false;
    
    /*Страшный способ обновления*/
    @FXML
    public void handleAddElement()  {

        TreeItem<String> rootElement = new TreeItem<String>("Обозреватель серверов");
        rootElement.setExpanded(true);
        
        TreeItem<String> serversItem = new TreeItem<String>("Список серверов", new ImageView(mainApp.rootIcon));        

        for(Server elemServer : elementList) {
            TreeItem<String> serverTree = new TreeItem<String>(elemServer.getCustomServerName(), new ImageView(mainApp.serverIcon));
            serverTree.setExpanded(elemServer.getOpened());
            for(DataBase elemDataBase : elemServer.getServerDataBases()) {
                TreeItem<String> databaseTree = new TreeItem<String>(elemDataBase.getDataBaseName(), new ImageView(mainApp.databaseIcon));
                databaseTree.setExpanded(elemDataBase.getOpened());
                for(Table elemTable : elemDataBase.getDataBaseTables()) {
                    TreeItem<String> tableTree = new TreeItem<String>(elemTable.getTableName(), new ImageView(mainApp.tableIcon));
                    tableTree.setExpanded(elemTable.getOpened());
                    for(Column elemColumn : elemTable.getTableColumns()) {
                        TreeItem<String> columnTree = new TreeItem<String>(elemColumn.getCustomColumnName(), new ImageView(mainApp.columnIcon));
                        tableTree.getChildren().add(columnTree);
                    }
                    databaseTree.getChildren().add(tableTree);
                }   
                serverTree.getChildren().add(databaseTree);
            }            
            serversItem.getChildren().add(serverTree);
        }
        
        TreeItem<String> addItem = new TreeItem<String>("Добавить сервер", new ImageView(mainApp.addIcon));        
        serversItem.getChildren().add(addItem);
        serversItem.setExpanded(true);
        
        TreeItem<String> queriesTree = new TreeItem<String>("Сохраненные запросы");
        for(Query elem : elementQuery){
            TreeItem<String> query = new TreeItem<String>(elem.getQueryName(), new ImageView(mainApp.scriptIcon));
            queriesTree.getChildren().add(query);
        }
        
        TreeItem<String> addQueries = new TreeItem<String>("Добавить запрос", new ImageView(mainApp.addIcon));
        
        queriesTree.getChildren().add(addQueries);
        
        rootElement.getChildren().add(serversItem);
        rootElement.getChildren().add(queriesTree);
        
        treeView = new TreeView<String>(rootElement);
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
        leftBorderPane.setCenter(treeView);
    }
    
    @FXML
    private void handleNewServer() {
        Server temp = new Server();
        boolean okClicked = mainApp.showPGReaderNewServerView(temp);
        if(okClicked) {
            mainApp.getListServer().add(temp);
            mainApp.saveServerDataToFile();
            handleAddElement();
        }
    }
    
    public void handleNewQuery() {
        Query temp = new Query();
        boolean okClicked = mainApp.showPGReaderNewQuery(temp);
        if(okClicked) {
            mainApp.getListQuery().add(temp);
            mainApp.saveQueryDataToFile();
            handleNewTabQuery(temp);
            handleAddElement();
        }
    }
    
    /*Страшный способ обновления обработки клика*/
    private void clickedElement(MouseEvent mouseEvent) {
        try {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
                if(mouseEvent.getClickCount() == 1) {
                    if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals("Список серверов")) {
                        for(Server searchServer : elementList) {
                            if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchServer.getCustomServerName())) {
                                searchServer.switchOpened();
                                treeView.getSelectionModel().getSelectedItem().setExpanded(searchServer.getOpened());
                                break;
                            }
                        }
                    }
                    if(treeView.getSelectionModel().getSelectedItem().getParent().getParent().getValue().equals("Список серверов")) {
                        for(Server searchServer : elementList) {
                            if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals(searchServer.getCustomServerName())) {
                                for(DataBase searchDataBase : searchServer.getServerDataBases()) {
                                    if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchDataBase.getDataBaseName())) {
                                        searchDataBase.switchOpened();
                                        treeView.getSelectionModel().getSelectedItem().setExpanded(searchDataBase.getOpened());
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if(treeView.getSelectionModel().getSelectedItem().getParent().getParent().getParent().getValue().equals("Список серверов")) {
                        for(Server searchServer : elementList) {
                            if(treeView.getSelectionModel().getSelectedItem().getParent().getParent().getValue().equals(searchServer.getCustomServerName())) {
                                for(DataBase searchDataBase : searchServer.getServerDataBases()) {
                                    if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals(searchDataBase.getDataBaseName())) {
                                        for(Table searchTable : searchDataBase.getDataBaseTables()) {
                                            if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchTable)) {
                                                searchTable.switchOpened();
                                                treeView.getSelectionModel().getSelectedItem().setExpanded(searchTable.getOpened());    
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if(mouseEvent.getClickCount() == 2) {
                    if(treeView.getSelectionModel().getSelectedItem().getValue() == "Добавить сервер") {
                        handleNewServer();
                    }
                    if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals("Сохраненные запросы")) {
                        if(treeView.getSelectionModel().getSelectedItem().getValue().equals("Добавить запрос")) {
                            handleNewQuery();
                            return;
                        }
                        for(Query queryElem : elementQuery) {
                            if(treeView.getSelectionModel().getSelectedItem().getValue().equals(queryElem.getQueryName())) {
                                System.out.println(queryElem.getQueryName());
                                handleNewTabQuery(queryElem);
                                break;
                            }
                        }
                    }
                    for(Server searchServer : elementList) {
                        if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchServer.getCustomServerName())) {
                            searchServer.setServerDataBases(requestListDatabases(searchServer));
                            searchServer.setOpened(true);
                            break;
                        }
                        if(treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals(searchServer.getCustomServerName())) {
                            for(DataBase searchDataBase : searchServer.getServerDataBases()) {
                                if(treeView.getSelectionModel().getSelectedItem().getValue().equals(searchDataBase.getDataBaseName())) {
                                    searchDataBase.setDataBaseTables(requestListTables(searchServer));
                                    searchDataBase.setOpened(true);
                                    countTabs++;
                                    Query temp = new Query();
                                    temp.setQueryServer(searchServer);
                                    temp.setQueryDataBase(searchDataBase);
                                    temp.setQueryName("Query " + countTabs);
                                    temp.setQueryText("");
                                    handleNewTabQuery(temp);
                                }
                            }
                            break;
                        }
                    }
                    handleAddElement();
                    treeView.getSelectionModel().select(-1);
                }
            }
            if(mouseEvent.getButton() == MouseButton.SECONDARY) {
                setContextMenu(mouseEvent);
            }
        } catch(Exception e) {
            e.printStackTrace();
            treeView.getSelectionModel().select(-1);
        }
    }
    
    private void setContextMenu(MouseEvent mouseEvent) {
        if((treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals("Список серверов"))&&
                (!treeView.getSelectionModel().getSelectedItem().getValue().equals("Добавить сервер"))) {
            contextMenu.getItems().clear();
            MenuItem delItem = new MenuItem("Удалить сервер");
            delItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        removeServer(treeView.getSelectionModel().getSelectedItem().getValue());
                        treeView.getSelectionModel().select(-1);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }   
            });
            
            contextMenu.getItems().add(delItem);
            contextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else {
            if((treeView.getSelectionModel().getSelectedItem().getParent().getValue().equals("Сохраненные запросы"))&&
                    (!treeView.getSelectionModel().getSelectedItem().getValue().equals("Добавить запрос"))) {
                contextMenu.getItems().clear();
                MenuItem delItem = new MenuItem("Удалить запрос");
                delItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            removeQuery(treeView.getSelectionModel().getSelectedItem().getValue());
                            treeView.getSelectionModel().select(-1);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }   
                });
                contextMenu.getItems().add(delItem);
                contextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        }
    }
    
    private void removeServer(String nameServer) {
        int i = 0;
        for(Server server : elementList) {
            if(server.getCustomServerName().equals(nameServer)){
                mainApp.removeServer(i);
                elementList = mainApp.getListServer();
                handleAddElement();
                break;
            }
            i++;
        }
    }
    
    private void removeQuery(String nameQuery) {
        int i = 0;
        for(Query query : elementQuery) {
            if(query.getQueryName().equals(nameQuery)){
                mainApp.removeQuery(i);
                elementQuery = mainApp.getListQuery();
                handleAddElement();
                break;
            }
            i++;
        }
    }

    /*Открытие новой вкладки*/
    public void handleNewTabQuery(Query query) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PGReaderTabQuery.fxml"));
            AnchorPane pgreaderTabQuery = (AnchorPane) loader.load();
            PGReaderTabQueryController controller = loader.getController();
            String selectedName = query.getQueryName();
            controller.setServerDatabase(query);
            controller.setPGReaderOverview(this);
            controller.setMainApp(mainApp);
            mainApp.tabsController.add(controller);
            Tab tabQuery = new Tab();
            tabQuery.setText(selectedName);
            tabQuery.setContent(pgreaderTabQuery);
            controller.setSelfTab(tabQuery);
            tabPane.getTabs().add(tabQuery);
            SingleSelectionModel<Tab> tabs = tabPane.getSelectionModel();
            tabs.select(tabQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*Запрос списка баз данных с сервера*/
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
    
    /*Запрос списка таблиц с колонками из базы данных*/
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

            for(Table tbl : tables) {
                ObservableList<Column> columns = FXCollections.observableArrayList();
                stmt = c.createStatement();
                rs = stmt.executeQuery("SELECT column_name, column_default, data_type FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '" + tbl.getTableName() + "'");
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String columnType = rs.getString("data_type");
                    Column column = new Column();
                    column.setColumnName(columnName);
                    column.setColumnType(columnType);
                    column.setColumnParent(tbl.getTableName());
                    columns.add(column);
                }
                tbl.setTableColumns(columns);
            }
            c.close();
            
            return tables;
        } catch(Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        elementList = mainApp.getListServer();
        elementQuery = mainApp.getListQuery();
        handleAddElement();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
