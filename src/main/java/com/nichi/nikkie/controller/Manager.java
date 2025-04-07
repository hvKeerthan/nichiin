package com.nichi.nikkie.controller;

import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import com.nichi.nikkie.service.CsvDownloadService;
import com.nichi.nikkie.service.NikkeiScraperService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Manager {

    @Autowired
    public CsvDownloadService csvDownloadService;

    @Autowired
    public NikkeiScraperService nikkeiScraperService;

    @Autowired
    private Nikkei225PAFPriceRepository repository;

    @PostConstruct
    public void run() throws InterruptedException {
        Thread.sleep(1000);
        repository.deleteAll(); // Delete all existing records
        System.out.println("Deleted all existing records from the database.");
        Thread.sleep(3000);
        csvDownloadService.downloadAndSaveCsv();
        nikkeiScraperService.scrapeAndUpdatePrices();
    }
}
