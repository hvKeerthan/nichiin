package com.nichi.nikkie.service;

import com.nichi.nikkie.entity.Nikkei225PAFPrice;
import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import com.nichi.nikkie.repository.NikkeiStockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NikkeiScraperService {
    private static final String BASE_URL = "https://www.reuters.com/markets/companies/";

    private final Nikkei225PAFPriceRepository nikkei225PAFPriceRepository;
    private final NikkeiStockRepository nikkeiStockRepository;



    @PostConstruct
    public void run() {
        scrapeAndUpdatePrices();
    }

    public void scrapeAndUpdatePrices() {
        System.out.println("Starting data scraping...");
        List<Nikkei225PAFPrice> stockCodes = nikkei225PAFPriceRepository.findAll();

        try {
            for (Nikkei225PAFPrice stock : stockCodes) {
                System.out.println("Scraping stock: " + stock.getId().getCode());

                String stockUrl = BASE_URL + stock.getId().getCode() + ".T";
                ChromeDriver driver = null;

                try {
                    driver = new ChromeDriver();
                    driver.manage().deleteAllCookies();
                    driver.get("about:blank");
                    Thread.sleep(1000);
                    driver.get(stockUrl);
                    Thread.sleep(3000);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement priceElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//dt[contains(text(),'Previous Close')]/following-sibling::dd")));

                    if (priceElement != null) {
                        double price = Double.parseDouble(priceElement.getText().replace(",", ""));
                        stock.setPrice(price);
                        nikkei225PAFPriceRepository.save(stock);
                        System.out.println("Updated: " + stock.getId().getCode() + " -> " + price);
                    }
                } catch (Exception e) {
                    System.err.println("Error scraping: " + stockUrl + " - " + e.getMessage());
                } finally {
                    if (driver != null) {
                        driver.quit();
                    }
                }
            }
        } finally {
            System.out.println("Scraping completed.");
        }

    }
}

