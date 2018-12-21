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
    private RootLayoutViewController layoutController;
    private FileApp fileApp = new FileApp();
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
            layoutController = loader.getController();
            layoutController.setMainApp(this);
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
            layoutController.setPGReaderOverviewController(overviewController);
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
        listServer.addAll(fileApp.loadServerDataFromFile(filePath));
    }

    public void saveServerDataToFile() {
        fileApp.saveServerDataToFile(listServer, filePath);
    }
    
    public void loadQueryDataFromFile() {
        listQuery.addAll(fileApp.loadQueryDataFromFile(filePathQuery));
    }
    
    public void saveQueryDataToFile() {
        fileApp.saveQueryDataToFile(listQuery, filePathQuery);
    }
    
    public void updateQueryToFile(Query query) {
        fileApp.updateQueryToFile(query, filePathQuery, listQuery);
    }
    
    public void addQueryToFile(Query query){
        listQuery.add(query);
        saveQueryDataToFile();
    }
    
    public void removeServer(Server server) {
        listServer.remove(server);
        saveServerDataToFile();
    }
    
    public void removeQuery(Query query) {
        listQuery.remove(query);
        saveQueryDataToFile();
    }
}
