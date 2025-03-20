package com.nichi.nikkie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nikkei225PAFPriceId implements Serializable {
    private String dt;
    private String code;
}