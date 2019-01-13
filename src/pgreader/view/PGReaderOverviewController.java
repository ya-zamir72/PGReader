/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import pgreader.model.MultiObject;
import pgreader.model.Query;
import pgreader.model.Table;

/**
 *
 * @author damir
 */
public class PGReaderOverviewController implements Initializable {
   
    @FXML
    private TreeView<MultiObject> treeView;
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
    
    /*Заполнение TreeView серверами и прочими объектами*/
    @FXML
    public void handleAddElement(ObservableList<Server> listServer, ObservableList<Query> listQuery)  {
        
        MultiObject rootItem = new MultiObject("String");
        rootItem.setObject("Обозреватель");
        TreeItem<MultiObject> rootElement = new TreeItem<MultiObject>(rootItem);
        rootElement.setExpanded(true);
        
        MultiObject serverItems = new MultiObject("String");
        serverItems.setObject("Список серверов");
        TreeItem<MultiObject> serverElements = new TreeItem<MultiObject>(serverItems, new ImageView(mainApp.rootIcon));
        serverElements.setExpanded(true);
        
        for(Server server : listServer) {
            MultiObject serverItem = new MultiObject("Server");
            serverItem.setObject(server);
            TreeItem<MultiObject> serverElement = new TreeItem<MultiObject>(serverItem, new ImageView(mainApp.serverIcon));
            serverElements.getChildren().add(serverElement);
        }
        
        MultiObject addServerItem = new MultiObject("String");
        addServerItem.setObject("Добавить сервер");
        TreeItem<MultiObject> addServerElement = new TreeItem(addServerItem, new ImageView(mainApp.addIcon));
        serverElements.getChildren().add(addServerElement);
        
        MultiObject queryItems = new MultiObject("String");
        queryItems.setObject("Сохраненные запросы");
        TreeItem<MultiObject> queryElements = new TreeItem<MultiObject>(queryItems);
        queryElements.setExpanded(true);
        
        for(Query query : listQuery) {
            MultiObject queryItem = new MultiObject("Query");
            queryItem.setObject(query);
            TreeItem<MultiObject> queryElement = new TreeItem<MultiObject>(queryItem, new ImageView(mainApp.scriptIcon));
            queryElements.getChildren().add(queryElement);
        }
        
        MultiObject addQueryItem = new MultiObject("String");
        addQueryItem.setObject("Добавить запрос");
        TreeItem<MultiObject> addQueryElement = new TreeItem(addQueryItem, new ImageView(mainApp.addIcon));
        queryElements.getChildren().add(addQueryElement);
        
        rootElement.getChildren().addAll(serverElements, queryElements);
        
        treeView = new TreeView<MultiObject>(rootElement);
        treeView.setEditable(true);
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
    
    /*События при клике по элементам TreeView*/
    private void clickedElement(MouseEvent mouseEvent) {
        try {
            if(mouseEvent.getClickCount() == 1) {
                contextMenu.hide();
            }
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                if(mouseEvent.getClickCount() == 2) {
                    String typeSelectedElement = treeView.getSelectionModel().getSelectedItem().getValue().getType();

                    if(!treeView.getSelectionModel().getSelectedItem().isExpanded()) {
                        if(typeSelectedElement.equals("Server")) {
                            Server selectedServer = (Server) treeView.getSelectionModel().getSelectedItem().getValue().getObject();
                            selectedServer.setServerDataBases(requestListDatabases(selectedServer));
                            ObservableList<TreeItem<MultiObject>> listTreeItemDataBases = FXCollections.observableArrayList();
                            for(DataBase db : selectedServer.getServerDataBases()) {
                                MultiObject moDataBase = new MultiObject("DataBase");
                                moDataBase.setObject(db);
                                TreeItem<MultiObject> treeItemDataBase = new TreeItem<MultiObject>(moDataBase, new ImageView(mainApp.databaseIcon));
                                listTreeItemDataBases.add(treeItemDataBase);
                            }

                            treeView.getSelectionModel().getSelectedItem().getChildren().clear();
                            treeView.getSelectionModel().getSelectedItem().getChildren().addAll(listTreeItemDataBases);
                            treeView.getSelectionModel().getSelectedItem().setExpanded(true);   
                        }

                        if(typeSelectedElement.equals("DataBase")) {
                            Server selectedServer = (Server) treeView.getSelectionModel().getSelectedItem().getParent().getValue().getObject();
                            DataBase selectedDataBase = (DataBase) treeView.getSelectionModel().getSelectedItem().getValue().getObject();
                            selectedDataBase.setDataBaseTables(requestListTables(selectedServer));
                            ObservableList<TreeItem<MultiObject>> listTreeItemTables = FXCollections.observableArrayList();
                            for(Table tb : selectedDataBase.getDataBaseTables()) {
                                MultiObject moTable = new MultiObject("Table");
                                moTable.setObject(tb);
                                TreeItem<MultiObject> treeItemTable = new TreeItem<MultiObject>(moTable, new ImageView(mainApp.tableIcon));
                                for(Column col : tb.getTableColumns()) {
                                    MultiObject moColumn = new MultiObject("Column");
                                    moColumn.setObject(col);
                                    TreeItem<MultiObject> treeItemColumn = new TreeItem<MultiObject>(moColumn, new ImageView(mainApp.columnIcon));
                                    treeItemTable.getChildren().add(treeItemColumn);
                                }
                                listTreeItemTables.add(treeItemTable);
                            }

                            treeView.getSelectionModel().getSelectedItem().getChildren().clear();
                            treeView.getSelectionModel().getSelectedItem().getChildren().addAll(listTreeItemTables);
                            treeView.getSelectionModel().getSelectedItem().setExpanded(true);
                            
                            countTabs++;
                            Query temp = new Query();
                            temp.setQueryServer(selectedServer);
                            temp.setQueryDataBase(selectedDataBase);
                            temp.setQueryName("Query " + countTabs);
                            temp.setQueryText("");
                            handleNewTabQuery(temp);
                        }
                    } else {
                        treeView.getSelectionModel().getSelectedItem().setExpanded(openedQuery);
                    }
                    if(typeSelectedElement.equals("Query")) {
                        Query selectedQuery = (Query) treeView.getSelectionModel().getSelectedItem().getValue().getObject();
                        handleNewTabQuery(selectedQuery);
                    }
                    if(typeSelectedElement.equals("String")) {
                        if(treeView.getSelectionModel().getSelectedItem().getValue().getObject().toString().equals("Добавить сервер")) {
                            handleNewServer();
                        }
                        if(treeView.getSelectionModel().getSelectedItem().getValue().getObject().toString().equals("Добавить запрос")) {
                            handleNewQuery();
                        }
                    }
                } 
            }
            if(mouseEvent.getButton() == MouseButton.SECONDARY) {
                setContextMenu(mouseEvent);
            }
        } catch(Exception e) {
            
        }
    }
    
    @FXML
    public void handleNewServer() {
        Server temp = new Server();
        boolean okClicked = mainApp.showPGReaderNewServerView(temp);
        if(okClicked) {
            mainApp.getListServer().add(temp);
            mainApp.saveServerDataToFile();
            MultiObject newServer = new MultiObject("Server");
            newServer.setObject(temp);
            TreeItem<MultiObject> treeItemNewServer = new TreeItem<MultiObject>(newServer, new ImageView(mainApp.serverIcon));
            treeView.getRoot().getChildren().get(0).getChildren().add(treeView.getRoot().getChildren().get(0).getChildren().size() - 1, treeItemNewServer);
        }
    }
    
    public void handleAddQuery(Query temp) {
        MultiObject newQuery = new MultiObject("Query");
        newQuery.setObject(temp);
        TreeItem<MultiObject> treeItemNewQuery = new TreeItem<MultiObject>(newQuery, new ImageView(mainApp.scriptIcon));
        treeView.getRoot().getChildren().get(1).getChildren().add(treeView.getRoot().getChildren().get(1).getChildren().size() - 1, treeItemNewQuery);
    }
    
    public void handleNewQuery() {
        Query temp = new Query();
        boolean okClicked = mainApp.showPGReaderNewQuery(temp);
        if(okClicked) {
            mainApp.getListQuery().add(temp);
            mainApp.saveQueryDataToFile();
            
            MultiObject newQuery = new MultiObject("Query");
            newQuery.setObject(temp);
            TreeItem<MultiObject> treeItemNewQuery = new TreeItem<MultiObject>(newQuery, new ImageView(mainApp.scriptIcon));
            treeView.getRoot().getChildren().get(1).getChildren().add(treeView.getRoot().getChildren().get(1).getChildren().size() - 1, treeItemNewQuery);
            handleNewTabQuery(temp);
            
        }
    }
    
    private void setContextMenu(MouseEvent mouseEvent) {
        String typeSelectedElement = treeView.getSelectionModel().getSelectedItem().getValue().getType();
        contextMenu.getItems().clear();

        if(typeSelectedElement.equals("Server")) {
            MenuItem delItem = new MenuItem("Удалить сервер");
            delItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        mainApp.removeServer((Server)treeView.getSelectionModel().getSelectedItem().getValue().getObject());
                        treeView.getRoot().getChildren().get(0).getChildren().remove(treeView.getSelectionModel().getSelectedItem());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }   
            });
            
            contextMenu.getItems().add(delItem);
            contextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else
            if(typeSelectedElement.equals("Query")) {
                
                MenuItem delItem = new MenuItem("Удалить запрос");
                delItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            mainApp.removeQuery((Query)treeView.getSelectionModel().getSelectedItem().getValue().getObject());
                            treeView.getRoot().getChildren().get(1).getChildren().remove(treeView.getSelectionModel().getSelectedItem());
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }   
                });
                contextMenu.getItems().add(delItem);
                contextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Сервер не доступен");
            alert.setContentText("Проверьте соединение");

            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Сервер не доступен");
            alert.setContentText("Проверьте соединение");

            alert.showAndWait();
            return FXCollections.observableArrayList();
        }
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        elementList = mainApp.getListServer();
        elementQuery = mainApp.getListQuery();     
        handleAddElement(mainApp.getListServer(), mainApp.getListQuery());
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
