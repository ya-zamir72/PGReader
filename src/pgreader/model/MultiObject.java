/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgreader.model;

/**
 *
 * @author damir
 */
public class MultiObject {
    
    private Server moServer;
    private DataBase moDataBase;
    private Table moTable;
    private Column moColumn;
    private Query moQuery;
    private String moString;

    private String typeName;
    
    public MultiObject(String typeName) {
        this.typeName = typeName;
    }
    
    public Object getObject() {
        switch(typeName) {
            case "Server"   : return moServer;
            case "DataBase" : return moDataBase;
            case "Table"    : return moTable;
            case "Column"   : return moColumn;
            case "Query"    : return moQuery;
            case "String"   : return moString;
            default         : return null;
        }
    }

    public void setObject(Object obj) {
        switch(typeName) {
            case "Server"   : this.moServer = (Server) obj;
                              break;
            case "DataBase" : this.moDataBase = (DataBase) obj;
                              break;  
            case "Table"    : this.moTable = (Table) obj;
                              break;
            case "Column"   : this.moColumn = (Column) obj;
                              break;
            case "Query"    : this.moQuery = (Query) obj;
                              break;
            case "String"   : this.moString = (String) obj;
                              break;  
        }
    }
    
    public String getType() {
        return typeName;
    }
    
    @Override
    public String toString() {
        switch(typeName) {
            case "Server"   : return moServer.getCustomServerName();
            case "DataBase" : return moDataBase.getDataBaseName();
            case "Table"    : return moTable.getTableName();
            case "Column"   : return moColumn.getCustomColumnName();
            case "Query"    : return moQuery.getQueryName();
            case "String"   : return moString.toString();
            default         : return null;
        }
    }
}
