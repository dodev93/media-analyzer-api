package com.dodev.analyzer.domain;

import java.time.LocalDate;
import java.util.List;

public interface MediaAnalyzerService {

    MediaDetails getMediaDetails(Long mediaId);

    List<MediaState> getMediaStates(Long mediaId, LocalDate from, LocalDate to);

    MediaState getLastMediaState(Long mediaId);

    BillingPeriod getBillingPeriod(LocalDate date);

    List<BillingPeriod> getBillingPeriods(LocalDate from, LocalDate to);



}
