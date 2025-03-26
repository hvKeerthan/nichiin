package com.nichi.nikkie.controller;

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

    @PostConstruct
    public void run(){
        csvDownloadService.downloadAndSaveCsv();
        nikkeiScraperService.scrapeAndUpdatePrices();
    }
}
