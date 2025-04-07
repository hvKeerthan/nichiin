package com.nichi.nikkie.service;

import com.nichi.nikkie.entity.Nikkei225PAFPrice;
import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NikkeiScraperService {
    private static final String BASE_URL = "https://finance.yahoo.com/quote/";

    private final Nikkei225PAFPriceRepository nikkei225PAFPriceRepository;
    private final ChromeDriver driver;

    public NikkeiScraperService(Nikkei225PAFPriceRepository nikkei225PAFPriceRepository, ChromeDriver driver) {
        this.nikkei225PAFPriceRepository = nikkei225PAFPriceRepository;
        this.driver = driver;
    }

    public void scrapeAndUpdatePrices() {
        System.out.println("Starting data scraping...");

        List<Nikkei225PAFPrice> stockCodes = nikkei225PAFPriceRepository.findAll();

        try {
            for (Nikkei225PAFPrice stock : stockCodes) {
                double price = scrapeStockPrice(stock.getId().getCode());
                if (price > 0) {
                    stock.setPrice(price);
                    nikkei225PAFPriceRepository.save(stock);
                    System.out.println("Updated stock: " + stock.getId().getCode() + " -> " + price);
                } else {
                    System.err.println("Could not retrieve valid price for: " + stock.getId().getCode());
                }
            }

            System.out.println("Scraping completed.");
        } finally {
            if (driver != null) {
                driver.quit();
            }
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
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//fin-streamer[@data-field='regularMarketPreviousClose']")
            ));

            if (priceElement != null) {
                String rawText = priceElement.getText().trim();
                System.out.println("Extracted price text: " + rawText);

                String cleanedText = rawText.replaceAll("[^0-9.]", "");

                if (!cleanedText.isEmpty()) {
                    return Double.parseDouble(cleanedText);
                }
            }
        } catch (Exception e) {
            System.err.println("Error scraping: " + stockUrl + " - " + e.getMessage());
        }
        return -1;
    }
}
