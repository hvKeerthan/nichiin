package com.nichi.nikkie.controller;

import com.nichi.nikkie.service.CsvDownloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    private final CsvDownloadService csvDownloadService;

    public CsvController(CsvDownloadService csvDownloadService) {
        this.csvDownloadService = csvDownloadService;
    }

    @GetMapping("/download")
    public ResponseEntity<String> processCsv() {
        csvDownloadService.downloadAndSaveCsv();
        return ResponseEntity.ok("CSV data processed and saved successfully!");
    }
}