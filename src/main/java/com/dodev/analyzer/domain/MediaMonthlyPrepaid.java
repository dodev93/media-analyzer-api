package com.dodev.analyzer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MediaMonthlyPrepaid {

    private Long mediaId;
    private Double value;
    private BigDecimal unitPrice;

}
