/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author damir
 */
public class AddedCondition {
    private Boolean check = true;
    private String conditionTitle;
    private StringProperty titleCond = new SimpleStringProperty();
    private String conditionString;
    private String type = "no_logic";
    
    public AddedCondition(String conditionTitle, String conditionString) {
        this.conditionTitle = conditionTitle;
        this.titleCond.set(conditionTitle);
        this.conditionString = conditionString;
        if(conditionString.equals("AND")||(conditionString.equals("OR"))) {
            type = "logic";
        }
    }
    
    public AddedCondition() {
        
    }
    
    public void setSelect(Boolean check){
        this.check = check;
    }
    
    public Boolean getSelect() {
        return check;
    }
    
    public void setConditionTitle(String title) {
        this.conditionTitle = title;
        this.titleCond.set(title);
    }
    
    public void setConditionString(String string) {
        this.conditionString = string;
    }
    
    public String getConditionString() {
        return conditionString;
    }
    
    public String getConditionTitle() {
        return conditionTitle;
    }
    
    public String getType() {
        return type;
    }
    
    @Override
    public String toString(){
        return conditionTitle;
    }
}
