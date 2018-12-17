/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;


import java.io.File;
import java.io.StringReader;
import pgreader.model.DataBase;
import pgreader.model.Server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import static javafx.scene.input.KeyCode.F5;
import static javafx.scene.input.KeyCode.F9;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import pgreader.MainApp;
import pgreader.model.Query;
/**
 *
 * @author damir
 */
public class PGReaderTabQueryController {
    
    @FXML
    TextArea queryTextArea;
    @FXML
    Label locationLabel;
    @FXML
    Label statusLabel;
    @FXML
    BorderPane outBorderPane;
            
    public String setName;
    private Server setServer;
    private DataBase setDatabase;
    private String setQuery;
    private MainApp mainApp;
    private PGReaderOverviewController parentController;
    private Tab tab;

    /*Прием данных сервера, бд, запроса*/
    public void setServerDatabase(Query query){
        this.setName = query.getQueryName();
        this.setServer = query.getQueryServer();
        this.setDatabase = query.getQueryDataBase();
        this.setQuery = query.getQueryText();
        queryTextArea.setText(query.getQueryText());
        locationLabel.setText(setServer.getServerName() + ":" + setServer.getServerPort() + "/" + setDatabase.getDataBaseName());
    }
    
    @FXML
    private void handleRun(KeyEvent event) {
        if(event.getCode() == F5) {
            Run(false);
        }
        if(event.getCode() == F9) {
            Run(true);
        }
    }
    
    @FXML
    private void handleKey() {
        if(setQuery.equals(queryTextArea.getText())) {
            if(tab.getText().equals(setName + "*")){
                tab.setText(setName);                 
            }
        } else {
            if(tab.getText().equals(setName)){
                tab.setText(setName + "*");                 
            }
        }
    }
    
    /*Сохранение запроса*/
    public void updateQueryToFile() {
        boolean isQuery = false;
        for(Query elem : mainApp.getListQuery()) {
            if(elem.getQueryName().equals(setName)) {
                isQuery = true;
                break;
            }
        }
        
        if(isQuery) {
            
            setQuery = queryTextArea.getText();
            
            Query input = new Query();
            input.setQueryName(setName);
            input.setQueryText(setQuery);
            input.setQueryServer(setServer);
            input.setQueryDataBase(setDatabase);
            mainApp.updateQueryToFile(input);

            tab.setText(setName);
        } else {
            String newName = mainApp.showPGReaderNewName();
            if(newName != null) {
                setQuery = queryTextArea.getText();
                Query input = new Query();
                input.setQueryName(newName);
                input.setQueryText(setQuery);
                input.setQueryServer(setServer);
                input.setQueryDataBase(setDatabase);
                mainApp.addQueryToFile(input);

                tab.setText(newName);    
            }             
        }
        parentController.handleAddElement();
    }
    
    /*Вывод результата запросов*/
    public void Run(boolean saveToXML){
        String request = queryTextArea.getText();
        if(queryTextArea.getSelectedText().length() != 0) {
            request = queryTextArea.getSelectedText();
        }
        String[] requests = request.split("\n");
        List<String> listReq = new ArrayList<String>();
        int i = -1;
        for(String req : requests) {
            if(req.isEmpty()) {
                continue;
            }
            if(req.contains("select")||req.contains("SELECT")){
                listReq.add(req);
                i++;
                continue;
            }
            listReq.set(i, listReq.get(i) + " " + req);
        }
        i = 1;

        long start = System.currentTimeMillis();

        List<TableView> tables = sqlSelect(listReq, saveToXML);

        long timeWorkCode = System.currentTimeMillis() - start;
        long hours = (long) (timeWorkCode / 36e5);
        long min = (long) ((timeWorkCode % 36e5)/6e4);
        long sec = (long) ((timeWorkCode % 6e4)/1000);
        long mil = timeWorkCode % 1000;

        if(tables != null) {
            statusLabel.setTextFill(Color.BLUE);
            String file = "";
            if(saveToXML) {
                file = "Результаты сохранены в файл. ";
            }
            statusLabel.setText(file + "Время запроса: " + hours + ":" + min + ":" + sec + "." + mil);
            if(tables.size() == 1){
                outBorderPane.setCenter(tables.get(0));
            } else {
                TabPane tabPane = new TabPane();
                for(TableView table : tables) {
                    Tab tab = new Tab();
                    tab.setText("Table " + i);
                    i++;
                    tab.setContent(table);
                    tabPane.getTabs().add(tab);
                }
                outBorderPane.setCenter(tabPane);
            }
        } else {
            Label answer = new Label("Произошла ошибка!");
            answer.setTextFill(Color.RED);
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5,5,5,5));
            hBox.getChildren().add(answer);
            outBorderPane.setCenter(hBox);
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Ошибка запроса");
        }
    }
    
    /*Запрос результатов запроса в виде таблиц*/
    private List<TableView> sqlSelect(List<String> requests, boolean saveToXML) {
        List<TableView> listTables = new ArrayList<TableView>(); 
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.
                getConnection("jdbc:postgresql://" + setServer.getServerHost() + ":" + setServer.getServerPort() + "/" + setDatabase.getDataBaseName(), 
                setServer.getServerUser(), 
                setServer.getServerPassword());
            c.setAutoCommit(false);

            int countReq = 0;
            for(String request : requests) {
                countReq++;
                ObservableList<ObservableList> data = FXCollections.observableArrayList();
                TableView outTable = new TableView();
                ObservableList<DataBase> dataBases = FXCollections.observableArrayList();
                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(request);
                
                
                for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    final int j = i;
                    
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                    col.setPrefWidth(100);
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>() {
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            try {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            } catch(Exception e) {
                                e.printStackTrace();
                                return new SimpleStringProperty("");
                            }
                        }
                    });
                    System.out.println(i);

                    outTable.getColumns().addAll(col);
                }
                
                if(saveToXML) {
                    Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
                    document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();
        
                    Element tableName = document.createElement(rs.getMetaData().getTableName(1));
                    document.appendChild(tableName);
                    int count = 0;
                    while(rs.next()) {
                        
                        ObservableList<String> row = FXCollections.observableArrayList();
                        
                        count++;
                        Element rows = document.createElement("Row");
                        rows.setAttribute("i", Integer.toString(count));
                        tableName.appendChild(rows);
                        for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            
                            row.add(getString(rs.getObject(i), rs.getMetaData().getColumnTypeName(i)));
                            
                            Pattern pattern = Pattern.compile("<[?]xml(.*?)[?]>");
                            Matcher matcher = pattern.matcher(getString(rs.getObject(i), rs.getMetaData().getColumnTypeName(i)));
                            if(matcher.find()){
                                Element rowParent = document.createElement(rs.getMetaData().getColumnName(i));
                                rows.appendChild(rowParent);
                                
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder = factory.newDocumentBuilder();
                                Document stringXML = builder.parse(new InputSource(new StringReader(rs.getString(i))));
                                NodeList nodeList = stringXML.getChildNodes();
                                for(int x = 0; x < nodeList.getLength(); x++) {
                                    rowParent.appendChild(document.adoptNode(nodeList.item(x).cloneNode(true)));
                                }
                            } else {
                                Element elem = document.createElement(rs.getMetaData().getColumnName(i));
                                elem.setTextContent(getString(rs.getObject(i), rs.getMetaData().getColumnTypeName(i)));                                
                                rows.appendChild(elem);
                            }
                        }
                        data.add(row);
                    }
                    Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                    DOMSource source = new DOMSource(document);
                    String fileNameResult = "src/pgreader/result/RequestResult"
                            + rs.getMetaData().getTableName(1).substring(0, 1).toUpperCase()
                            + rs.getMetaData().getTableName(1).substring(1)
                            + countReq;
                    int s = 0;
                    String sub = "";
                    while(new File(fileNameResult + sub + ".xml").exists()) {
                        s++;
                        sub = "(" + s + ")";
                    }
                    File res = new File(fileNameResult + sub + ".xml");
                    res.createNewFile();
                    StreamResult result = new StreamResult(res);
                    transformer.transform(source, result);
                } else {
                    while (rs.next()) {
                        ObservableList<String> row = FXCollections.observableArrayList();
                        try {
                            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                row.add(getString(rs.getObject(i), rs.getMetaData().getColumnTypeName(i)));
                            }    
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        
                        data.add(row);
                    }
                }
                outTable.setItems(data);
                listTables.add(outTable);
                rs.close();
            }
            stmt.close();
            c.close();
            return listTables;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /*Преобразования сложных типов в строку*/
    private String getString(Object obj, String type) throws ParseException {
        String result = "";
        
        if(obj != null) {
        switch(type) {
            case "time without time zone":      DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
                                                result = df.format((Date)obj);
                                                break;
            case "timestamp without time zone": DateFormat dFull = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
                                                result = dFull.format((Date)obj);
                                                break;
            default:                            result = obj.toString();
                                                break;
            }
        }
        return result;
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public void setPGReaderOverview(PGReaderOverviewController parentController) {
        this.parentController = parentController;
    }
    
    public void setSelfTab(Tab tab){
        this.tab = tab;
    }
}

