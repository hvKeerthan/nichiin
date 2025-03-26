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
public class NikkeiScraperService {
    private static final String BASE_URL = "https://www.reuters.com/markets/companies/";

    private final Nikkei225PAFPriceRepository nikkei225PAFPriceRepository;
    private final NikkeiStockRepository nikkeiStockRepository;
    private final ChromeDriver driver;

    public NikkeiScraperService(Nikkei225PAFPriceRepository nikkei225PAFPriceRepository, NikkeiStockRepository nikkeiStockRepository, ChromeDriver driver) {
        this.nikkei225PAFPriceRepository = nikkei225PAFPriceRepository;
        this.nikkeiStockRepository = nikkeiStockRepository;
        this.driver = driver;
    }


//    @PostConstruct
//    public void run() {
//        scrapeAndUpdatePrices();
//    }

    public void scrapeAndUpdatePrices() {
        System.out.println("Starting data scraping...");
        List<Nikkei225PAFPrice> stockCodes = nikkei225PAFPriceRepository.findAll();

        try {
            for (Nikkei225PAFPrice stock : stockCodes) {
                System.out.println("Scraping stock: " + stock.getId().getCode());

                String stockUrl = BASE_URL + stock.getId().getCode() + ".T";

                try {
                    driver.manage().deleteAllCookies();
                    driver.get("about:blank");
                    Thread.sleep(1000);
                    driver.get(stockUrl);
                    Thread.sleep(3000);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                    WebElement priceElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//dt[contains(text(),'Previous Close')]/following-sibling::dd")));

                    if (priceElement != null) {
                        double price = Double.parseDouble(priceElement.getText().replace(",", ""));
                        Nikkei225PAFPrice existingStock = nikkei225PAFPriceRepository.findByCode(stock.getId().getCode());

                        if (existingStock != null) {
                            if (!existingStock.getId().getDt().equals(stock.getId().getDt())) {
                                // dt is different, delete old record and insert new one
                                nikkei225PAFPriceRepository.deleteByCode(stock.getId().getCode());
                                stock.setPrice(price);
                                nikkei225PAFPriceRepository.save(stock);
                                System.out.println("Deleted old record and updated new dt and Price: " + stock.getId().getCode() + " -> " + price);
                            } else if (existingStock.getPrice() == null) {
                                // Price is null, update only the price
                                existingStock.setPrice(price);
                                nikkei225PAFPriceRepository.save(existingStock);
                                System.out.println("Updated Price (was null): " + stock.getId().getCode() + " -> " + price);
                            } else {
                                System.out.println("Skipping update for: " + stock.getId().getCode() + " (Same dt, price exists)");
                            }
                        } else {
                            // New stock entry, save it
                            stock.setPrice(price);
                            nikkei225PAFPriceRepository.save(stock);
                            System.out.println("Inserted new stock: " + stock.getId().getCode() + " -> " + price);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error scraping: " + stockUrl + " - " + e.getMessage());
                }
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
            System.out.println("Scraping completed.");
        }
    }


}

