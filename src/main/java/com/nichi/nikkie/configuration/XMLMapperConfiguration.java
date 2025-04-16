package com.nichi.nikkie.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@Component
@Getter
public class XMLMapperConfiguration {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;
    private String hibernateDialect;
    private String ddlAuto;
    private String mailHost;
    private int mailPort;
    private String mailUsername;
    private String mailPassword;
    private String mailProtocol;
    private boolean mailAuth;
    private boolean mailEnable;
    private String mailTo;
    private String mailCc;
    private String mailSub;
    private String mailSuccessContent;
    private String mailErrorContent;
    private String mailDbErrorContent;
    private String mailScrapeSuccessContent;
    private String mailScrapeErrorContent;


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
            mailHost = values.getElementsByTagName("mail-host").item(0).getTextContent();
            mailPort = Integer.parseInt(values.getElementsByTagName("mail-port").item(0).getTextContent());
            mailUsername = values.getElementsByTagName("mail-username").item(0).getTextContent();
            mailPassword = values.getElementsByTagName("mail-password").item(0).getTextContent();
            mailProtocol = values.getElementsByTagName("mail-protocol").item(0).getTextContent();
            mailAuth = Boolean.parseBoolean(values.getElementsByTagName("mail-auth").item(0).getTextContent());
            mailEnable = Boolean.parseBoolean(values.getElementsByTagName("mail-enable").item(0).getTextContent());
            mailTo = values.getElementsByTagName("mail-to").item(0).getTextContent();
            mailCc = values.getElementsByTagName("mail-cc").item(0).getTextContent();
            mailSub = values.getElementsByTagName("mail-sub").item(0).getTextContent();
            mailSuccessContent = values.getElementsByTagName("mail-success-content").item(0).getTextContent();
            mailErrorContent = values.getElementsByTagName("mail-error-content").item(0).getTextContent();
            mailDbErrorContent = values.getElementsByTagName("mail-dberror-content").item(0).getTextContent();
            mailScrapeSuccessContent = values.getElementsByTagName("mail-scrapesuccess-content").item(0).getTextContent();
            mailScrapeErrorContent = values.getElementsByTagName("mail-scrapeerror-content").item(0).getTextContent();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
