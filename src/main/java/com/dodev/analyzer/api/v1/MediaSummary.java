package com.dodev.analyzer.api.v1;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MediaSummary {

    private LocalDate date;
    private Double used;
    private Double paid;

}
