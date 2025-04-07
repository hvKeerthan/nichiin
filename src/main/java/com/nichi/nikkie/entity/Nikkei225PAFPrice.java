package com.nichi.nikkie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nikkei225PAFPrice {

    @EmbeddedId
    private Nikkei225PAFPriceId id;

    private String code_name;
    private String paf;
    private String classification;
    private String sector;
    private Double price;

    @Column(nullable = true)
    private String updatesource;

    private String updatetime;


}
