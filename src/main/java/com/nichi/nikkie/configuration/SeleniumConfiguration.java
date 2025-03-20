package com.nichi.nikkie.configuration;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SeleniumConfiguration {

    @Bean
    public ChromeDriver chromeDriver() {
        System.setProperty("webdriver.chrome.driver", "/Users/keerthanhv/downloads/chromedriver-mac-arm64/chromedriver");

        Map<String, Object> prefs = new HashMap<>();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }
}
