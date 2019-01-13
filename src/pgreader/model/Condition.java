/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pgreader.widgets.MaskField;

/**
 *
 * @author damir
 */
public class Condition {

    private String typeElement;
    private String operand;
    private String title;
    private String oneCol = "'";
    private String doubleCol = "\"";
    private boolean col;
    private ObservableList<String> nameCond;
    private ObservableList<HBox> conditionsHBox;
    
    public Condition(String typeElement, String operand, String title) {
        this.typeElement = typeElement;
        this.operand = operand;
        this.title = title;
        Label nameCondLabel = new Label("Введите значение");
        TextField textFieldCond = new TextField();
        textFieldCond.setId("condition0");
        HBox condHBox = new HBox(nameCondLabel, textFieldCond);
        condHBox.setSpacing(5);
        condHBox.setAlignment(Pos.CENTER);
        conditionsHBox = FXCollections.observableArrayList();
        conditionsHBox.add(condHBox);
    }
    
    public Condition(String typeElement, String operand, String title, boolean colBool, ObservableList<String> nameCond) {
        this.typeElement = typeElement;
        this.operand = operand;
        this.title = title;
        this.nameCond = nameCond;
        this.col = colBool;
        conditionsHBox = FXCollections.observableArrayList();
        switch (typeElement) {
            case "timestamp without time zone":
                for(int i = 0; i < nameCond.size(); i++) {
                    Label nameCondLabel = new Label(nameCond.get(i));
                    MaskField condMaskField = new MaskField();
                    condMaskField.setMask("DD:DD:DD.DDD");
                    condMaskField.setPlaceholder("00:00:00.000");
                    DatePicker condDate = new DatePicker();
                    VBox vBox = new VBox(nameCondLabel, condMaskField, condDate);
                    vBox.setSpacing(5);
                    HBox condHBox = new HBox(vBox);
                    condHBox.setSpacing(5);
                    condHBox.setAlignment(Pos.CENTER);
                    conditionsHBox.add(condHBox);
                }
                break;
            case "time without time zone":
                for(int i = 0; i < nameCond.size(); i++) {
                    Label nameCondLabel = new Label(nameCond.get(i));
                    MaskField condMaskField = new MaskField();
                    condMaskField.setMask("DD:DD:DD.DDD");
                    condMaskField.setPlaceholder("00:00:00.000");
                    VBox vBox = new VBox(nameCondLabel, condMaskField);
                    vBox.setSpacing(5);
                    HBox condHBox = new HBox(vBox);
                    condHBox.setSpacing(5);
                    condHBox.setAlignment(Pos.CENTER);
                    conditionsHBox.add(condHBox);
                }
                break;
            case "date": 
                for(int i = 0; i < nameCond.size(); i++) {
                    Label nameCondLabel = new Label(nameCond.get(i));
                    DatePicker condDate = new DatePicker();
                    VBox vBox = new VBox(nameCondLabel, condDate);
                    vBox.setSpacing(5);
                    HBox condHBox = new HBox(vBox);
                    condHBox.setSpacing(5);
                    condHBox.setAlignment(Pos.CENTER);
                    conditionsHBox.add(condHBox);
                }
                break;
            default:
                for(String label : nameCond) {
                    Label nameCondLabel = new Label(label);
                    TextField condTextField = new TextField();
                    HBox condHBox = new HBox(nameCondLabel, condTextField);
                    condHBox.setSpacing(5);
                    condHBox.setAlignment(Pos.CENTER);
                    conditionsHBox.add(condHBox);
                }
                break;
        }
    }
    
    
    public Condition(String typeElement, String operand, String title, boolean colBool) {
        this.typeElement = typeElement;
        this.operand = operand;
        this.title = title;
        this.nameCond = nameCond;
        this.col = colBool;
        conditionsHBox = FXCollections.observableArrayList();
        Label nameCondLabel = new Label("Введите значение");
        HBox condHBox;
        VBox vBox;
        MaskField condMaskField = new MaskField();
        DatePicker condDate;

        switch (typeElement) {
            case "timestamp without time zone":
                condMaskField.setMask("DD:DD:DD.DDD");
                condMaskField.setPlaceholder("00:00:00.000");
                condDate = new DatePicker();
                vBox = new VBox(nameCondLabel, condMaskField, condDate);
                vBox.setSpacing(5);
                condHBox = new HBox(vBox);
                condHBox.setSpacing(5);
                condHBox.setAlignment(Pos.CENTER);
                conditionsHBox.add(condHBox);
                break;
            case "time without time zone":
                condMaskField.setMask("DD:DD:DD.DDD");
                condMaskField.setPlaceholder("00:00:00.000");
                vBox = new VBox(nameCondLabel, condMaskField);
                vBox.setSpacing(5);
                condHBox = new HBox(vBox);
                condHBox.setSpacing(5);
                condHBox.setAlignment(Pos.CENTER);
                conditionsHBox.add(condHBox);
                break;
            case "date":
                condDate = new DatePicker();
                vBox = new VBox(nameCondLabel, condDate);
                vBox.setSpacing(5);
                condHBox = new HBox(vBox);
                condHBox.setSpacing(5);
                condHBox.setAlignment(Pos.CENTER);
                conditionsHBox.add(condHBox);
                break;
            default:
                TextField textFieldCond = new TextField();
                textFieldCond.setId("condition0");
                condHBox = new HBox(nameCondLabel, textFieldCond);
                condHBox.setSpacing(5);
                condHBox.setAlignment(Pos.CENTER);
                conditionsHBox.add(condHBox);
                break;
        }
    }
    
    public String getType() {
        return typeElement;
    }
    
    public String getOperand() {
        return operand;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getCol() {
        if(col) {
            return oneCol;
        } else {
            return doubleCol;
        }
    }
    
    public int getCountCond() {
        return conditionsHBox.size();
    } 
    
    public ObservableList<HBox> getHBoxConditions() {
        return conditionsHBox;
    }
    
    @Override
    public String toString() {
        return title;
    }
    
}
