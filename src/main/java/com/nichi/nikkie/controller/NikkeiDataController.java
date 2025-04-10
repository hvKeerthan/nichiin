//package com.nichi.nikkie.controller;
//
//import com.nichi.nikkie.entity.Nikkei225PAFPrice;
//import com.nichi.nikkie.repository.Nikkei225PAFPriceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@RequestMapping("/api/nikkei-data")
//@RequiredArgsConstructor
//public class NikkeiDataController {
//
//    private final Nikkei225PAFPriceRepository repository;
//
//    @GetMapping
//    public NikkeiResponse getNikkeiData() {
//        List<Nikkei225PAFPrice> stocks = repository.findAll();
//
//        double adjustedPrice = stocks.stream()
//                .mapToDouble(stock -> stock.getPrice() * Double.parseDouble(stock.getPaf()))
//                .sum();
//
//        double nikkeiIndex = adjustedPrice / 1000;
//
//        return new NikkeiResponse(stocks, adjustedPrice, nikkeiIndex);
//    }
//
//    record NikkeiResponse(List<Nikkei225PAFPrice> stocks, double adjustedPrice, double nikkeiIndex) {}
//}
