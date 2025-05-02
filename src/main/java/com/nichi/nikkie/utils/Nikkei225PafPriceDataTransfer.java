package com.nichi.nikkie.utils;

import com.opencsv.CSVReader;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Nikkei225PafPriceDataTransfer {

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: java Nikkei225PafPriceDataTransfer <IN/OUT> <TableName> <FileName> <Date> <ConfigFile>");
            return;
        }

        String operation = args[0];
        String tableName = args[1];
        String fileName = args[2];
        String date = args[3];
        String configFile = args[4];

        try {
            loadDbConfig(configFile);

            if ("IN".equalsIgnoreCase(operation)) {
                bulkInsert(tableName, fileName, date);
            } else if ("OUT".equalsIgnoreCase(operation)) {
                bulkExport(tableName, fileName, date);
            } else {
                System.out.println("Invalid operation. Use IN or OUT.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDbConfig(String configFilePath) throws Exception {
        File configFile = new File(configFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(configFile);
        doc.getDocumentElement().normalize();

        DB_URL = doc.getElementsByTagName("url").item(0).getTextContent();
        DB_USER = doc.getElementsByTagName("username").item(0).getTextContent();
        DB_PASSWORD = doc.getElementsByTagName("password").item(0).getTextContent();
    }

    private static void bulkInsert(String tableName, String fileName, String date) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false);

        String sql = "INSERT INTO " + tableName +
                " (code, classification, code_name, dt, paf, price, sector, updatesource, updatetime) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (code, dt) DO UPDATE SET " +
                "classification = EXCLUDED.classification, " +
                "code_name = EXCLUDED.code_name, " +
                "paf = EXCLUDED.paf, " +
                "price = EXCLUDED.price, " +
                "sector = EXCLUDED.sector, " +
                "updatesource = EXCLUDED.updatesource, " +
                "updatetime = EXCLUDED.updatetime";

        try (
                FileReader fr = new FileReader(fileName);
                CSVReader csvReader = new CSVReader(fr);
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            String[] data;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((data = csvReader.readNext()) != null) {
                if (data.length < 9 || data[0].equalsIgnoreCase("code")) continue;

                pstmt.setString(1, data[0]); // Code


                LocalDate parsedDate = LocalDate.parse(data[3], inputFormatter);
                String formattedDate = parsedDate.format(outputFormatter);
                pstmt.setString(2, formattedDate);

                pstmt.setString(3, data[1]);
                pstmt.setString(4, data[2]);
                pstmt.setBigDecimal(5, new BigDecimal(data[4]));
                pstmt.setBigDecimal(6, new BigDecimal(data[5]));
                pstmt.setString(7, data[6]);
                pstmt.setString(8, data[7]);
                pstmt.setString(9, data[8]);

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            System.out.println("Data inserted/updated successfully.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    private static void bulkExport(String tableName, String fileName, String date) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String sql = "SELECT * FROM " + tableName + " WHERE dt = ?";

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String line = String.join(",",
                        rs.getString("code"),
                        quote(rs.getString("classification")),
                        quote(rs.getString("code_name")),
                        rs.getString("dt"),
                        rs.getBigDecimal("paf").toPlainString(),
                        rs.getBigDecimal("price").toPlainString(),
                        quote(rs.getString("sector")),
                        quote(rs.getString("updatesource")),
                        quote(rs.getString("updatetime"))
                );

                writer.write(line);
                writer.newLine();

                System.out.println("Exported: " + rs.getString("code"));
            }

            System.out.println("Data exported successfully.");
        } finally {
            conn.close();
        }
    }


    private static String quote(String input) {
        return input == null ? "\"\"" : "\"" + input.replace("\"", "\"\"") + "\"";
    }

}
