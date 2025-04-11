package com.nichi.nikkie.service;

import com.nichi.nikkie.entity.Nikkei225PAFPrice;
import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class NikkeiScraperService {
    private static final String BASE_URL = "https://finance.yahoo.com/quote/";
    private static final Logger logger = Logger.getLogger(NikkeiScraperService.class.getName());
    String configFile = System.getProperty("config.xml");


    private final Nikkei225PAFPriceRepository nikkei225PAFPriceRepository;
    private final ChromeDriver driver;

    public NikkeiScraperService(Nikkei225PAFPriceRepository nikkei225PAFPriceRepository, ChromeDriver driver) {
        this.nikkei225PAFPriceRepository = nikkei225PAFPriceRepository;
        this.driver = driver;
    }

    public void scrapeAndUpdatePrices() {
        logger.info("Starting data scraping...");

        List<Nikkei225PAFPrice> stockCodes = nikkei225PAFPriceRepository.findAll();

        try {
            for (Nikkei225PAFPrice stock : stockCodes) {
                double price = scrapeStockPrice(stock.getId().getCode());
                if (price > 0) {
                    stock.setPrice(price);
                    nikkei225PAFPriceRepository.save(stock);
                    logger.info("Updated stock: " + stock.getId().getCode() + " -> " + price);
                } else {
                    logger.severe("Could not retrieve valid price for: " + stock.getId().getCode());
                }
            }

            logger.info("Scraping completed.");


            double adjustedPrice = stockCodes.stream()
                    .filter(s -> s.getPrice() != null && s.getPaf() != null && !s.getPaf().isEmpty())
                    .mapToDouble(s -> s.getPrice() * Double.parseDouble(s.getPaf()))
                    .sum();

            double divisor = loadDivisorFromXML();

            if (divisor > 0) {
                double nikkei225Price = adjustedPrice / divisor;
                logger.info("===============================================================");
                logger.info(String.format("Adjusted Price: %.2f    Nikkei 225 Price: %.2f", adjustedPrice, nikkei225Price));
                logger.info("===============================================================\n");
            } else {
                logger.severe("Invalid divisor: 0 or not found.");
            }

        } catch (Exception e) {
            logger.severe("Exception during scraping: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private double scrapeStockPrice(String stockCode) {
        String stockUrl = BASE_URL + stockCode + ".T";
        try {
            driver.manage().deleteAllCookies();
            driver.get("about:blank");
            Thread.sleep(1000);
            driver.get(stockUrl);

            new WebDriverWait(driver, Duration.ofSeconds(45)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//fin-streamer[@data-field='regularMarketPreviousClose']")
            ));

            if (priceElement != null) {
                String rawText = priceElement.getText().trim();
                logger.info("Extracted price text for " + stockCode + ": " + rawText);

                String cleanedText = rawText.replaceAll("[^0-9.]", "");
                if (!cleanedText.isEmpty()) {
                    return Double.parseDouble(cleanedText);
                }
            }
        } catch (Exception e) {
            logger.severe("Error scraping: " + stockUrl + " - " + e.getMessage());
        }
        return -1;
    }

    private double loadDivisorFromXML() {
        try {
            File xmlFile = new File(configFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList valuesList = doc.getElementsByTagName("values");
            if (valuesList.getLength() > 0) {
                Element valuesElement = (Element) valuesList.item(0);
                NodeList divisorList = valuesElement.getElementsByTagName("divisor");

                if (divisorList.getLength() > 0) {
                    String divisorText = divisorList.item(0).getTextContent().trim();
                    return Double.parseDouble(divisorText);
                }
            }
        } catch (Exception e) {
            logger.severe("Error loading divisor from XML: " + e.getMessage());
        }
        return 0;
    }

    @PreDestroy
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("ChromeDriver closed successfully.");
            } catch (Exception e) {
                logger.severe("Error closing WebDriver: " + e.getMessage());
            }
        }
    }
}
