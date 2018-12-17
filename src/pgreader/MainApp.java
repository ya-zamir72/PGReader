/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pgreader.model.Server;
import pgreader.view.PGReaderNewServerViewController;
import pgreader.view.PGReaderOverviewController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pgreader.model.DataBase;
import pgreader.model.Query;
import pgreader.view.PGReaderNewNameController;
import pgreader.view.PGReaderNewQueryController;
import pgreader.view.PGReaderTabQueryController;
import pgreader.view.RootLayoutViewController;



/**
 *
 * @author damir
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Server> listServer = FXCollections.observableArrayList();
    private ObservableList<Query> listQuery = FXCollections.observableArrayList();
    private String filePath = "src/pgreader/data/servers.xml";
    private String filePathQuery = "src/pgreader/data/query.xml";
    
    private final int sz = 24;
    public final Image rootIcon = new Image(getClass().getResourceAsStream("ico/iconRoot.png"), sz, sz, false, false);
    public final Image serverIcon = new Image(getClass().getResourceAsStream("ico/iconServer.png"), sz, sz, false, false);
    public final Image databaseIcon = new Image(getClass().getResourceAsStream("ico/iconDataBase.png"), sz, sz, false, false);
    public final Image tableIcon = new Image(getClass().getResourceAsStream("ico/iconTable.png"), sz, sz, false, false);
    public final Image columnIcon = new Image(getClass().getResourceAsStream("ico/iconColumn.png"), sz, sz, false, false);
    public final Image addIcon = new Image(getClass().getResourceAsStream("ico/iconAdd.png"), sz, sz, false, false);
    public final Image scriptIcon = new Image(getClass().getResourceAsStream("ico/iconScript.png"), sz, sz, false, false);


    
    public PGReaderOverviewController overviewController;
    public String selectedTab;
    public ObservableList<PGReaderTabQueryController> tabsController = FXCollections.observableArrayList();
            
    public MainApp() {
        loadServerDataFromFile();
        loadQueryDataFromFile();
    }
    
    public ObservableList<Server> getListServer() {
        return listServer;
    }
    
    public ObservableList<Query> getListQuery() {
        return listQuery;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PGReader");
        
        initRootLayout();
        showPGReaderOverview();
    }
    
    public void initRootLayout() {
        try { 
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/RootLayoutView.fxml"));
            rootLayout = loader.load();
            RootLayoutViewController controller = loader.getController();
            controller.setMainApp(this);
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showPGReaderOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PGReaderOverview.fxml"));
            AnchorPane pgreaderOverview = (AnchorPane) loader.load();
            overviewController = loader.getController();
            overviewController.setMainApp(this);
            rootLayout.setCenter(pgreaderOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean showPGReaderNewServerView(Server temp) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PGReaderNewServerView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialog = new Stage();
            dialog.resizableProperty().set(false);
            dialog.setTitle("Добавить сервер");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            
            PGReaderNewServerViewController controller = loader.getController();
            controller.setDialogStage(dialog);
            controller.setListServer(listServer);
            controller.setServer(temp);
            
            dialog.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showPGReaderNewQuery(Query temp) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PGReaderNewQueryView.fxml"));
            BorderPane page = (BorderPane) loader.load();
            
            Stage dialog = new Stage();
            dialog.resizableProperty().set(false);
            dialog.setTitle("Добавить запрос");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            
            PGReaderNewQueryController controller = loader.getController();
            controller.setDialogStage(dialog);
            controller.setQuery(temp);
            controller.setMainApp(this);
            
            dialog.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String showPGReaderNewName() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PGReaderNewName.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialog = new Stage();
            dialog.resizableProperty().set(false);
            dialog.setTitle("Сохранить запрос");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            
            PGReaderNewNameController controller = loader.getController();
            controller.setListQuery(listQuery);
            controller.setDialogStage(dialog);
            
            dialog.showAndWait();
            if(controller.isOkClicked()) {
                return controller.getNewName();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void loadServerDataFromFile() {
        try {
            if(new File(filePath).exists()) {
                File xmlFile = new File(filePath);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(xmlFile);

                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Server");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (Node.ELEMENT_NODE == node.getNodeType()) {
                        Server addServer = new Server();
                        Element element = (Element) node;
                        addServer.setServerName(element.getElementsByTagName("ServerName").item(0).getTextContent());
                        addServer.setServerHost(element.getElementsByTagName("ServerHost").item(0).getTextContent());
                        addServer.setServerPort(element.getElementsByTagName("ServerPort").item(0).getTextContent());
                        addServer.setServerUser(element.getElementsByTagName("ServerUser").item(0).getTextContent());
                        addServer.setServerPassword(element.getElementsByTagName("ServerPassword").item(0).getTextContent());
                        addServer.setServerDataBases(FXCollections.observableArrayList());
                        listServer.add(addServer);
                    }
                }
            }

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ошибка чтения");

            alert.showAndWait();
        }
    }

    public void saveServerDataToFile() {
        try {            
            if(new File(filePath).exists()) {
                Document document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();

                Element servers = document.createElement("Servers");
                document.appendChild(servers);

                for(Server server : listServer) {
                    Element serverElem = document.createElement("Server");
                    servers.appendChild(serverElem);

                    Element serverName = document.createElement("ServerName");
                    serverName.setTextContent(server.getServerName());
                    serverElem.appendChild(serverName);

                    Element serverHost = document.createElement("ServerHost");
                    serverHost.setTextContent(server.getServerHost());
                    serverElem.appendChild(serverHost);

                    Element serverPort = document.createElement("ServerPort");
                    serverPort.setTextContent(server.getServerPort());
                    serverElem.appendChild(serverPort);

                    Element serverUser = document.createElement("ServerUser");
                    serverUser.setTextContent(server.getServerUser());
                    serverElem.appendChild(serverUser);

                    Element serverPassword = document.createElement("ServerPassword");
                    serverPassword.setTextContent(server.getServerPassword());
                    serverElem.appendChild(serverPassword);

                }
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File(filePath));

                transformer.transform(source, result);

                System.out.println("Документ сохранен!");
            } else {
                File servers = new File(filePath);
                servers.createNewFile();
                saveServerDataToFile();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Документ не сохранен!");

            alert.showAndWait();
        }
    }
    
    public void loadQueryDataFromFile() {
        try {
            if(new File(filePathQuery).exists()) {
                File xmlFile = new File(filePathQuery);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(xmlFile);

                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Query");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (Node.ELEMENT_NODE == node.getNodeType()) {
                        Query addQuery = new Query();
                        Element element = (Element) node;
                        addQuery.setQueryName(element.getElementsByTagName("QueryName").item(0).getTextContent());
                        addQuery.setQueryText(element.getElementsByTagName("QueryText").item(0).getTextContent());
                        
                        Server addServer = new Server();
                        addServer.setServerName(element.getElementsByTagName("ServerName").item(0).getTextContent());
                        addServer.setServerHost(element.getElementsByTagName("ServerHost").item(0).getTextContent());
                        addServer.setServerPort(element.getElementsByTagName("ServerPort").item(0).getTextContent());
                        addServer.setServerUser(element.getElementsByTagName("ServerUser").item(0).getTextContent());
                        addServer.setServerPassword(element.getElementsByTagName("ServerPassword").item(0).getTextContent());
                        addServer.setServerDataBases(FXCollections.observableArrayList());
                        
                        addQuery.setQueryServer(addServer);
                        
                        DataBase addDatabase = new DataBase();
                        addDatabase.setDataBaseName(element.getElementsByTagName("DatabaseName").item(0).getTextContent());
                        addDatabase.setDataBaseTables(FXCollections.observableArrayList());
                        
                        addQuery.setQueryDataBase(addDatabase);
                        
                        listQuery.add(addQuery);
                    }
                }
            }

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ошибка чтения");

            alert.showAndWait();
        }
    }
    
    public void saveQueryDataToFile() {
        try {            
            if(new File(filePath).exists()) {
                Document document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();

                Element queries = document.createElement("Queries");
                document.appendChild(queries);

                for(Query query : listQuery) {
                    Element queryElem = document.createElement("Query");
                    queries.appendChild(queryElem);

                    Element queryName = document.createElement("QueryName");
                    queryName.setTextContent(query.getQueryName());
                    queryElem.appendChild(queryName);

                    Element queryText = document.createElement("QueryText");
                    queryText.setTextContent(query.getQueryText());
                    queryElem.appendChild(queryText);

                    Element queryServer = document.createElement("Server");
                    queryElem.appendChild(queryServer);
                    
                    Element queryServerName = document.createElement("ServerName");
                    queryServerName.setTextContent(query.getQueryServer().getServerName());
                    queryServer.appendChild(queryServerName);

                    Element queryServerHost = document.createElement("ServerHost");
                    queryServerHost.setTextContent(query.getQueryServer().getServerHost());
                    queryServer.appendChild(queryServerHost);

                    Element queryServerPort = document.createElement("ServerPort");
                    queryServerPort.setTextContent(query.getQueryServer().getServerPort());
                    queryServer.appendChild(queryServerPort);

                    Element queryServerUser = document.createElement("ServerUser");
                    queryServerUser.setTextContent(query.getQueryServer().getServerUser());
                    queryServer.appendChild(queryServerUser);
                    
                    Element queryServerPassword = document.createElement("ServerPassword");
                    queryServerPassword.setTextContent(query.getQueryServer().getServerPassword());
                    queryServer.appendChild(queryServerPassword);

                    Element queryDatabase = document.createElement("Database");
                    queryServer.appendChild(queryDatabase);
                    
                    Element queryDatabaseName = document.createElement("DatabaseName");
                    queryDatabaseName.setTextContent(query.getQueryDataBase().getDataBaseName());
                    queryDatabase.appendChild(queryDatabaseName);
                }
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File(filePathQuery));

                transformer.transform(source, result);

                System.out.println("Документ сохранен!");
            } else {
                File servers = new File(filePathQuery);
                servers.createNewFile();
                saveServerDataToFile();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Документ не сохранен!");

            alert.showAndWait();
        }
    }
    
    public void updateQueryToFile(Query query) {
        try {
            if(new File(filePathQuery).exists()) {
                File xmlFile = new File(filePathQuery);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(xmlFile);

                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Query");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (Node.ELEMENT_NODE == node.getNodeType()) {
                        Element element = (Element) node;
                        if(element.getElementsByTagName("QueryName").item(0).getTextContent().equals(query.getQueryName())) {
                            element.getElementsByTagName("QueryText").item(0).setTextContent(query.getQueryText());
                            break;
                        }
                    }
                }
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filePathQuery));

                transformer.transform(source, result);

                System.out.println("Документ сохранен!");
            }
            for(Query elem : listQuery) {
                if(elem.getQueryName().equals(query.getQueryName())) {
                    elem.setQueryText(query.getQueryText());
                    break;
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ошибка чтения");
            e.printStackTrace();
            alert.showAndWait();
            
        }
    }
    
    public void addQueryToFile(Query query){
        listQuery.add(query);
        saveQueryDataToFile();
    }
    
    public void removeServer(int i) {
        listServer.remove(i);
        saveServerDataToFile();
    }
    
    public void removeQuery(int i) {
        listQuery.remove(i);
        saveQueryDataToFile();
    }
}
