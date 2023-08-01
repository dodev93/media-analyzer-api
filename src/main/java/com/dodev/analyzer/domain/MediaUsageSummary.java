package com.dodev.analyzer.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class MediaUsageSummary {

    private Long mediaId;
    private LocalDate date;
    private Double used;
    private Double paid;

}
