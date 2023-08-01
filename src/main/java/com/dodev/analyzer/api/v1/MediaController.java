package com.dodev.analyzer.api.v1;

import com.dodev.analyzer.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media/{mediaId}")
public class MediaController {

    private final ForecastProvider forecastProvider;
    private final MediaAnalyzerService mediaAnalyzerService;

    public MediaController(MediaAnalyzerService mediaAnalyzerService) {
        this.mediaAnalyzerService = mediaAnalyzerService;
        this.forecastProvider = new ForecastProvider();
    }

    @RequestMapping("/state")
    public ResponseEntity<MediaSummaryPackage> getStates(@PathVariable("mediaId") Long mediaId,
                                                          @RequestParam("from") LocalDate from,
                                                          @RequestParam("to") LocalDate to) {

        MediaSummaryPackage mediaSummaryPackage = new MediaSummaryPackage();

        List<MediaState> mediaStates = mediaAnalyzerService.getMediaStates(mediaId, from, to);
        List<BillingPeriod> billingPeriods = mediaAnalyzerService.getBillingPeriods(from, to);

        List<MediaSummary> summaryList = prepareStates(mediaStates, billingPeriods);

        mediaSummaryPackage.setType(mediaId + "");
        mediaSummaryPackage.setFrom(from);
        mediaSummaryPackage.setTo(to);

        mediaSummaryPackage.setSummary(summaryList);

        return ResponseEntity.ok(mediaSummaryPackage);
    }

    @RequestMapping("/summary")
    public ResponseEntity<Summary> getSummary(@PathVariable("mediaId") Long mediaId,
                                              @RequestParam("from") LocalDate from,
                                              @RequestParam("to") LocalDate to) {

        MediaState mediaState = mediaAnalyzerService.getLastMediaState(mediaId);
        BillingPeriod billingPeriod = mediaAnalyzerService.getBillingPeriod(LocalDate.now());

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(Arrays.asList(billingPeriod));
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaMonthlyPrepaid mediaMonthlyPrepaid = selectMediaMonthlyPrepaid(mediaId, billingPeriod);

        Summary summary = prepareSummary(mediaPaidValueCalculator, mediaState, mediaMonthlyPrepaid.getUnitPrice().doubleValue());

        return ResponseEntity.ok(summary);
    }

    private MediaMonthlyPrepaid selectMediaMonthlyPrepaid(Long mediaId, BillingPeriod billingPeriod) {
        List<MediaMonthlyPrepaid> prepaids = billingPeriod.getPrepaids();
        MediaMonthlyPrepaid mediaMonthlyPrepaid = null;
        for (MediaMonthlyPrepaid prepaid : prepaids) {
            if (prepaid.getMediaId().equals(mediaId)) {
                mediaMonthlyPrepaid = prepaid;
                break;
            }
        }
        return mediaMonthlyPrepaid;
    }

    @RequestMapping("/statistics")
    public ResponseEntity<MediaStatistics> getStatistics(@PathVariable("mediaId") Long mediaId) {

        List<MediaState> states = mediaAnalyzerService.getMediaStates(mediaId, null, null);
        BillingPeriod billingPeriod = mediaAnalyzerService.getBillingPeriod(LocalDate.now());

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(Arrays.asList(billingPeriod));
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaMonthlyPrepaid mediaMonthlyPrepaid = selectMediaMonthlyPrepaid(mediaId, billingPeriod);

        MediaStatistics statistics = prepareStatistics(mediaPaidValueCalculator,
                                                       mediaMonthlyPrepaid,
                                                       states);

        return ResponseEntity.ok(statistics);
    }

    private MediaStatistics prepareStatistics(MediaPaidValueCalculator calculator, MediaMonthlyPrepaid mediaMonthlyPrepaid, List<MediaState> states) {
        MediaStatistics statistics = new MediaStatistics();

        MediaState actualState = states.get(states.size() - 1);
        double unitPrice = mediaMonthlyPrepaid.getUnitPrice().doubleValue();

        statistics.setForecast(forecastProvider.prepareForecast(states, mediaMonthlyPrepaid.getValue(), unitPrice));
        statistics.setSummary(prepareSummary(calculator, actualState, unitPrice));

        return statistics;
    }

    private List<MediaSummary> prepareStates(List<MediaState> mediaStates, List<BillingPeriod> billingPeriods) {

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(billingPeriods);
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaState actualState = mediaStates.get(mediaStates.size() - 1);
        if (actualState.getDate().isBefore(LocalDate.now()))
            mediaStates.add(new MediaState(actualState.getMediaId(), LocalDate.now(), null));

        List<MediaSummary> summaryList = mediaStates.stream()
                                                    .map(ms -> prepareState(mediaPaidValueCalculator, ms))
                                                    .collect(Collectors.toList());

        return summaryList;
    }

    private MediaSummary prepareState(MediaPaidValueCalculator mediaPaidValueCalculator, MediaState ms) {
        MediaSummary summary = new MediaSummary();
        summary.setDate(ms.getDate());
        summary.setUsed(ms.getValue());
        summary.setPaid(mediaPaidValueCalculator.ofDay(ms.getDate()));
        return summary;
    }

    private Summary prepareSummary(MediaPaidValueCalculator mediaPaidValueCalculator, MediaState ms, Double unitPrice) {
        Summary summary = new Summary();
        summary.setLastStateDate(ms.getDate());
        summary.setUsed(ms.getValue());
        summary.setPaidToday(mediaPaidValueCalculator.ofDay(LocalDate.now()));
        Double paidForLastUsed = mediaPaidValueCalculator.ofDay(ms.getDate());
        summary.setPaidLastState(paidForLastUsed);
        summary.setOverused(summary.getUsed() - paidForLastUsed);
        summary.setOverpaid(summary.getOverused() * unitPrice);
        return summary;
    }

}