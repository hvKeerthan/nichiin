package com.nichi.nikkie.service;

import com.nichi.nikkie.entity.Nikkei225PAFPrice;
import com.nichi.nikkie.entity.Nikkei225PAFPriceId;
import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class CsvDownloadService {

    private final Nikkei225PAFPriceRepository repository;

    private static final String CSV_URL = "https://indexes.nikkei.co.jp/nkave/archives/file/nikkei_225_price_adjustment_factor_en.csv";

    public CsvDownloadService(Nikkei225PAFPriceRepository repository) {
        this.repository = repository;
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
                            .build();

                    repository.save(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
