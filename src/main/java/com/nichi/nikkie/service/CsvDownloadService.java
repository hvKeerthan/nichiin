package com.nichi.nikkie.service;

import com.nichi.nikkie.entity.Nikkei225PAFPrice;
import com.nichi.nikkie.entity.Nikkei225PAFPriceId;
import com.nichi.nikkie.mail.MailContent;
import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvDownloadService {

    private final Nikkei225PAFPriceRepository repository;
    private final MailContent mailContent;

    private static final String CSV_URL = "https://indexes.nikkei.co.jp/nkave/archives/file/nikkei_225_price_adjustment_factor_en.csv";

    public CsvDownloadService(Nikkei225PAFPriceRepository repository, MailContent mailContent) {
        this.repository = repository;
        this.mailContent = mailContent;
    }

    @Transactional
    public void downloadAndSaveCsv() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(CSV_URL).openConnection();
            connection.setRequestMethod("GET");

            try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(connection.getInputStream())))) {
                List<String[]> records = reader.readAll();
                boolean isFirstRow = true;

                for (String[] record : records) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue;
                    }

                    if (record.length < 6) {
                        System.err.println("Skipping invalid row: " + String.join(", ", record));
                        continue;
                    }

                    Nikkei225PAFPriceId id = new Nikkei225PAFPriceId(record[0], record[1]);
                    if (repository.existsById(id)) {
                        continue;
                    }

                    Nikkei225PAFPrice entity = Nikkei225PAFPrice.builder()
                            .id(id)
                            .code_name(record[2])
                            .paf(record[3])
                            .classification(record[4])
                            .sector(record[5])
                            .updatesource("etfpcfscript")
                            .updatetime(updateTimestamp())
                            .build();

                    repository.save(entity);
                }
            }
        } catch (Exception e) {
            mailContent.sendDownloadFailedMail();
            e.printStackTrace();
        }
    }

    public File downloadCsvToFile(String destinationPath) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(CSV_URL).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 FileWriter writer = new FileWriter(destinationPath)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }

                System.out.println("✅ CSV downloaded to " + destinationPath);
                return new File(destinationPath);
            }
        } catch (Exception e) {
            mailContent.sendDownloadFailedMail();
            e.printStackTrace();
            return null;
        }
    }

    public String updateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
    }

    public List<Nikkei225PAFPrice> getSortedStockData() {
        return repository.findAllSortedByCode();
    }

}
