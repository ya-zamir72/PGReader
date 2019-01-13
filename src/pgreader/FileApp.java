package pgreader;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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
import pgreader.model.Server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author damir
 */
public class FileApp {
    
    public ObservableList<Server> loadServerDataFromFile(String filePath) {
        try {
            ObservableList<Server> listServer = FXCollections.observableArrayList();
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
            return listServer;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ошибка чтения");

            alert.showAndWait();
            return FXCollections.observableArrayList();
        }
    }

    public void saveServerDataToFile(ObservableList<Server> listServer, String filePath) {
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
                saveServerDataToFile(listServer, filePath);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Документ не сохранен!");

            alert.showAndWait();
        }
    }

public ObservableList<Query> loadQueryDataFromFile(String filePath) {
    try {
        ObservableList<Query> listQuery = FXCollections.observableArrayList();
        if(new File(filePath).exists()) {
            File xmlFile = new File(filePath);
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
        return listQuery;
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ошибка чтения");

        alert.showAndWait();
        return FXCollections.observableArrayList();
    }
}

public void saveQueryDataToFile(ObservableList<Query> listQuery, String filePath) {
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
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);

            System.out.println("Документ сохранен!");
        } else {
            File queries = new File(filePath);
            queries.createNewFile();
            saveQueryDataToFile(listQuery, filePath);
        }
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Документ не сохранен!");

        alert.showAndWait();
    }
}

public void updateQueryToFile(Query query, String filePath, ObservableList<Query> listQuery) {
    try {
        if(new File(filePath).exists()) {
            File xmlFile = new File(filePath);
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
            StreamResult result = new StreamResult(new File(filePath));

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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ошибка чтения");
        e.printStackTrace();
        alert.showAndWait();

    }
}

    
}
