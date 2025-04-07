package com.nichi.nikkie.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@Component
public class XMLMapperConfiguration {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;
    private String hibernateDialect;
    private String ddlAuto;


    @PostConstruct
    public void loadConfig() {
        try {

            String xmlFilePath = System.getProperty("config.xml");
            System.out.println(xmlFilePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            File file = new File(xmlFilePath);
            document = builder.parse(file);
            System.out.println("File loaded successfully " + file.getAbsoluteFile());

            document.getDocumentElement().normalize();
            Element values = (Element) document.getElementsByTagName("values").item(0);

            dbUrl = values.getElementsByTagName("url").item(0).getTextContent();
            dbUsername = values.getElementsByTagName("username").item(0).getTextContent();
            dbPassword = values.getElementsByTagName("password").item(0).getTextContent();
            dbDriver = values.getElementsByTagName("driver-class-name").item(0).getTextContent();
            hibernateDialect = values.getElementsByTagName("hibernate-dialect").item(0).getTextContent();
            ddlAuto = values.getElementsByTagName("ddl-auto").item(0).getTextContent();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }

    public String getDdlAuto() {
        return ddlAuto;
    }
}
