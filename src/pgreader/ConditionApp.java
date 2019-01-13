/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pgreader.model.Condition;

/**
 *
 * @author damir
 */
public class ConditionApp {
    
    private ObservableList<Condition> listCondition = FXCollections.observableArrayList(); 
    
    public ConditionApp() {
        listCondition.add(new Condition("integer", "=", "Равно", true));
        listCondition.add(new Condition("integer", "<=", "Меньше либо равно", true));
        listCondition.add(new Condition("integer", ">=", "Больше либо равно", true));
        listCondition.add(new Condition("text", "LIKE", "Равно"));
        ObservableList<String> labels = FXCollections.observableArrayList();
        labels.add("От");
        labels.add("До");
        listCondition.add(new Condition("integer", "BETWEEN", "Между", true, labels));
        labels = FXCollections.observableArrayList();
        labels.add("Начало периода");
        labels.add("Конец периода");
        listCondition.add(new Condition("timestamp without time zone", "BETWEEN", "Между", true, labels));
        listCondition.add(new Condition("timestamp without time zone", "=", "Равно", true));
        listCondition.add(new Condition("time without time zone", "BETWEEN", "Между", true, labels));
        listCondition.add(new Condition("time without time zone", "=", "Равно", true));
        listCondition.add(new Condition("uuid", "=", "Равно", true));
        listCondition.add(new Condition("date", "=", "Равно", true));
        listCondition.add(new Condition("date", "BETWEEN", "Между", true, labels));


    }
    
    public ObservableList<Condition> getCondition(String type) {
        ObservableList<Condition> outList = FXCollections.observableArrayList();
        for(Condition cond : listCondition) {
            if(cond.getType().equals(type)) {
                outList.add(cond);
            }
        }
        return outList;
    }
}
