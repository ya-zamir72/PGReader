/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.view;


import java.io.File;
import java.io.StringReader;
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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.F5;
import static javafx.scene.input.KeyCode.F9;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import pgreader.ConditionApp;
import pgreader.MainApp;
import pgreader.model.DataBase;
import pgreader.model.Server;
import pgreader.model.AddedCondition;
import pgreader.model.Column;
import pgreader.model.Condition;
import pgreader.model.Query;
import pgreader.model.Table;
import pgreader.widgets.MaskField;
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
    @FXML
    ComboBox<Table> tableComboBox;
    @FXML
    TableView<Column> columnsTableView;
    @FXML
    TableColumn<Column, StringProperty> columnName;
    @FXML
    TableColumn<Column, Boolean> columnSelect;
    @FXML
    ComboBox<Column> columnsComboBox;
    @FXML
    ComboBox<Condition> conditionComboBox;
    @FXML
    RadioButton withOutConditionRB;
    @FXML
    RadioButton withConditionRB;
    @FXML
    BorderPane conditionBP;
    @FXML
    VBox conditionsVBox;
    @FXML
    TableView<AddedCondition> addedConditionTableView;
    @FXML
    TableColumn<AddedCondition, Boolean> selectAddedConditionTableColumn;
    @FXML
    TableColumn<AddedCondition, String> titleAddedCondition;
    @FXML
    ComboBox<Column> columnSortComboBox;
    @FXML
    RadioButton orderByRB;
    @FXML
    RadioButton orderByDescRB;
    @FXML
    CheckBox orderByCB;
    @FXML
    CheckBox limitCB;
    @FXML
    TextField limitTextField;

    ToggleGroup group = new ToggleGroup();
    ToggleGroup orderGroup = new ToggleGroup();
    
    public String setName;
    private Server setServer;
    private DataBase setDatabase;
    private String setQuery;
    private MainApp mainApp;
    private PGReaderOverviewController parentController;
    private Tab tab;
    private ContextMenu contextMenu = new ContextMenu();
    
    /*Прием данных сервера, бд, запроса*/
    public void setServerDatabase(Query query){
        
        withConditionRB.setToggleGroup(group);
        withOutConditionRB.setToggleGroup(group);
        
        orderByRB.setToggleGroup(orderGroup);
        orderByDescRB.setToggleGroup(orderGroup);
        
        this.setName = query.getQueryName();
        this.setServer = query.getQueryServer();
        this.setDatabase = query.getQueryDataBase();
        this.setQuery = query.getQueryText();
        queryTextArea.setText(query.getQueryText());
        locationLabel.setText(setServer.getServerName() + ":" + setServer.getServerPort() + "/" + setDatabase.getDataBaseName());
        tableComboBox.setItems(setDatabase.getDataBaseTables());
        columnName.setCellValueFactory(new PropertyValueFactory<Column, StringProperty>("columnName"));
        columnSelect.setCellValueFactory(new PropertyValueFactory<Column, Boolean>("select"));
        
        columnsTableView.setEditable(true);
        columnSelect.setCellValueFactory(new Callback<CellDataFeatures<Column, Boolean>, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<Column, Boolean> param) {
                Column col = param.getValue(); 
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(col.getSelect());
                booleanProp.addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) {
                        col.setSelect(newValue);
                    }
                });
                return booleanProp;
            }
        });
        
        columnSelect.setCellFactory(new Callback<TableColumn<Column, Boolean>,
        TableCell<Column, Boolean>>() {
            @Override
            public TableCell<Column, Boolean> call(TableColumn<Column, Boolean> p) {
                CheckBoxTableCell<Column, Boolean> cell = new CheckBoxTableCell<Column, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        /*123*/        
        addedConditionTableView.setEditable(true);
        titleAddedCondition.setCellValueFactory(new PropertyValueFactory<AddedCondition, String>("conditionTitle"));

        selectAddedConditionTableColumn.setCellValueFactory(new PropertyValueFactory<AddedCondition, Boolean>("check"));
        
        selectAddedConditionTableColumn.setCellValueFactory(new Callback<CellDataFeatures<AddedCondition, Boolean>, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<AddedCondition, Boolean> param) {
                AddedCondition col = param.getValue(); 
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(col.getSelect());
                booleanProp.addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) {
                        col.setSelect(newValue);
                    }
                });
                return booleanProp;
            }
        });
        
        selectAddedConditionTableColumn.setCellFactory(new Callback<TableColumn<AddedCondition, Boolean>,
        TableCell<AddedCondition, Boolean>>() {
            @Override
            public TableCell<AddedCondition, Boolean> call(TableColumn<AddedCondition, Boolean> p) {
                CheckBoxTableCell<AddedCondition, Boolean> cell = new CheckBoxTableCell<AddedCondition, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        
        addedConditionTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    showContextMenu(mouseEvent);
                } catch(Exception e) {
                    
                }
            }
        });
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
    /* События для генерации запроса */
    @FXML
    private void handleSelectTable() {
        try {
            conditionBP.setCenter(new AnchorPane());
            conditionComboBox.getItems().clear();
            addedConditionTableView.getItems().clear();
            columnsTableView.setItems(tableComboBox.getSelectionModel().getSelectedItem().getTableColumns());
            columnsComboBox.setItems(tableComboBox.getSelectionModel().getSelectedItem().getTableColumns());
            columnSortComboBox.setItems(tableComboBox.getSelectionModel().getSelectedItem().getTableColumns());
        } catch(Exception e) {
        }
    }
    
    @FXML
    private void handleSelectColumn() {
        try {
            conditionBP.setCenter(new AnchorPane());

            String typeColumn = columnsComboBox.getSelectionModel().getSelectedItem().getColumnType();
            ConditionApp condApp = new ConditionApp();
            ObservableList<Condition> listCond = FXCollections.observableArrayList();
            listCond.addAll(condApp.getCondition(typeColumn));

            conditionComboBox.setItems(listCond);
        } catch(Exception e) {
        }
    }
    
    @FXML
    private void handleSelectCondition() {
        try {
            Condition selectedCond = conditionComboBox.getSelectionModel().getSelectedItem();
            conditionsVBox = new VBox();
            for(int i = 0; i < selectedCond.getCountCond(); i++) {
                conditionsVBox.setPadding(new Insets(10,10,10,10));
                conditionsVBox.setSpacing(5);
                conditionsVBox.getChildren().add(selectedCond.getHBoxConditions().get(i));
                conditionsVBox.setAlignment(Pos.CENTER);
            }
            conditionBP.setCenter(conditionsVBox);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddCondition() {
        try {
            Condition selectedCond = conditionComboBox.getSelectionModel().getSelectedItem();
            ObservableList<String> condString = FXCollections.observableArrayList();
            String type = selectedCond.getType();
            switch (type) {
                case "timestamp without time zone": 
                    for(int i = 0; i < selectedCond.getCountCond(); i++) {
                        HBox condHBox = (HBox) conditionsVBox.getChildren().get(i);
                        VBox condVBox = (VBox) condHBox.getChildren().get(0);
                        MaskField timeMaskField = (MaskField) condVBox.getChildren().get(1);
                        DatePicker datePicker = (DatePicker) condVBox.getChildren().get(2);

                        condString.add(datePicker.getValue().toString() + " " + timeMaskField.getText());
                    }
                    break;
                
                case "time without time zone":
                    for(int i = 0; i < selectedCond.getCountCond(); i++) {
                        HBox condHBox = (HBox) conditionsVBox.getChildren().get(i);
                        VBox condVBox = (VBox) condHBox.getChildren().get(0);
                        MaskField timeMaskField = (MaskField) condVBox.getChildren().get(1);

                        condString.add(timeMaskField.getText());
                    }
                    break;
                    
                case "date":
                    for(int i = 0; i < selectedCond.getCountCond(); i++) {
                        HBox condHBox = (HBox) conditionsVBox.getChildren().get(i);
                        VBox condVBox = (VBox) condHBox.getChildren().get(0);
                        DatePicker datePicker = (DatePicker) condVBox.getChildren().get(1);

                        condString.add(datePicker.getValue().toString());
                    }
                    break;
                    
                default:
                    for(int i = 0; i < selectedCond.getCountCond(); i++) {
                        HBox condHBox = (HBox) conditionsVBox.getChildren().get(i);
                        TextField condTextField = (TextField) condHBox.getChildren().get(1);
                        condString.add(condTextField.getText().trim());
                    }
                    break;
            }
       
            AddedCondition addCond = new AddedCondition();

            String title = "";
            String operand = "";
            if(selectedCond.getCountCond() == 1) {
                title = columnsComboBox.getSelectionModel().getSelectedItem().getColumnName() + 
                                " " + selectedCond.getTitle().toLowerCase() + 
                                " " + condString.get(0);

                operand = "\""+ columnsComboBox.getSelectionModel().getSelectedItem().getColumnName() + "\"" +
                                " " + selectedCond.getOperand() + " " +
                                selectedCond.getCol() + condString.get(0) + selectedCond.getCol();
                addCond.setConditionTitle(title);
                addCond.setConditionString(operand);
            }

            if(selectedCond.getCountCond() == 2) {
                title = columnsComboBox.getSelectionModel().getSelectedItem().getColumnName() +
                        " " + selectedCond.getTitle().toLowerCase() + 
                        " " + condString.get(0) + " и " + condString.get(1);
                operand = "\"" + columnsComboBox.getSelectionModel().getSelectedItem().getColumnName() + "\"" + 
                        " " + selectedCond.getOperand() + " " +
                        selectedCond.getCol() + condString.get(0) + selectedCond.getCol() + 
                        " AND " + selectedCond.getCol() + condString.get(1) + selectedCond.getCol();
            }

            System.out.println(title);
            System.out.println(operand);

            addCond.setConditionTitle(title);
            addCond.setConditionString(operand);

            if(addedConditionTableView.getItems().size() != 0) {
                addedConditionTableView.getItems().add(new AddedCondition("И", "AND"));
            }

            addedConditionTableView.getItems().add(addCond);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не все поля заполнены");
            alert.setContentText("Проверьте поля условий");

            alert.showAndWait();
        }
    }
    
    private void showContextMenu(MouseEvent event) {
        contextMenu.hide();
        if(event.getButton() == MouseButton.SECONDARY) {
            contextMenu.getItems().clear();
            if(addedConditionTableView.getSelectionModel().getSelectedItem().getType().equals("logic")) {
                MenuItem andItem = new MenuItem("И");
                MenuItem orItem = new MenuItem("Или");
                andItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        int index = addedConditionTableView.getSelectionModel().getSelectedIndex();
                        addedConditionTableView.getItems().remove(index);
                        AddedCondition logCond = new AddedCondition("И", "AND");
                        addedConditionTableView.getItems().add(index, logCond);
                    }
                });
                
                orItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        int index = addedConditionTableView.getSelectionModel().getSelectedIndex();
                        addedConditionTableView.getItems().remove(index);
                        AddedCondition logCond = new AddedCondition("Или", "OR");
                        addedConditionTableView.getItems().add(index, logCond);
                    }
                });
                
                contextMenu.getItems().addAll(andItem, orItem);
                contextMenu.show(addedConditionTableView, event.getScreenX(), event.getScreenY());
            }
            if(addedConditionTableView.getSelectionModel().getSelectedItem().getType().equals("no_logic")) {
                MenuItem delItem = new MenuItem("Удалить условие");
                delItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            int index = addedConditionTableView.getSelectionModel().getSelectedIndex();
                            int size = addedConditionTableView.getItems().size();
                            if(size != 1) {
                                if(index != size-1) {
                                    addedConditionTableView.getItems().remove(index);
                                    addedConditionTableView.getItems().remove(index);
                                } else {
                                    addedConditionTableView.getItems().remove(index - 1);
                                    addedConditionTableView.getItems().remove(index - 1);
                                }
                            } else {
                                addedConditionTableView.getItems().remove(0);
                            }
                        } catch(Exception e) {
                            
                        }
                    }
                });

                contextMenu.getItems().addAll(delItem);
                contextMenu.show(addedConditionTableView, event.getScreenX(), event.getScreenY());
            }
        }
    }
    
    @FXML
    private void handleGenerate() {
        String generateQuery = "SELECT ";
        List<String> selectedColumns = new ArrayList<String>();
        for(Column column : tableComboBox.getSelectionModel().getSelectedItem().getTableColumns()) {
            if(column.getSelect()) {
                selectedColumns.add(column.getColumnName());
            }
        }
        if(selectedColumns.isEmpty()) {
            generateQuery += "*";
        } else {
            for(int i = 0; i < selectedColumns.size(); i++) {
                generateQuery += "\"" + selectedColumns.get(i) + "\"";
                if(i+1 != selectedColumns.size()) {
                    generateQuery += ", ";
                }
            }
        }
        generateQuery += " FROM \"" + tableComboBox.getSelectionModel().getSelectedItem().getTableName() + "\"\n";
        if(group.getSelectedToggle() == withConditionRB) {
            generateQuery += "WHERE ";
            boolean check = false;
            for(AddedCondition row : this.addedConditionTableView.getItems()) {
                if(row.getType().equals("logic")) {
                    if(check) {
                        generateQuery += " " + row.getConditionString() + " ";
                        check = false;
                    }
                } else {
                    if(row.getSelect()) {
                        generateQuery += row.getConditionString();
                        check = true;
                    } else {
                        check = false;
                    }
                }
            }
            if(generateQuery.endsWith("AND ")) {
                generateQuery = generateQuery.substring(0, generateQuery.length() - 5);
            }
            if(generateQuery.endsWith("OR ")) {
                generateQuery = generateQuery.substring(0, generateQuery.length() - 4);
            }
        }
        
        if(orderByCB.isSelected() && (columnSortComboBox.getSelectionModel().getSelectedItem() != null)) {
            generateQuery += "ORDER BY \"" + 
                    columnSortComboBox.getSelectionModel().getSelectedItem().getColumnName() + "\"";
            if(orderGroup.getSelectedToggle() == orderByDescRB) {
                generateQuery += " DESC\n";
            } else {
                generateQuery += "\n";
            }
        }
        if((limitCB.isSelected())&&(limitTextField.getText() != null)) {
            generateQuery += "LIMIT " + limitTextField.getText();
        }
        generateQuery += "\n\n";
        queryTextArea.setText(queryTextArea.getText().concat(generateQuery));
    }
    /* События для генерации запроса. КЦ */
    
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
                parentController.handleAddQuery(input);

                tab.setText(newName);    
            }             
        }
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

