package com.dodev.analyzer.api.v1;

import com.dodev.analyzer.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final ForecastProvider forecastProvider;
    private final MediaAnalyzerService mediaAnalyzerService;

    public MediaController(MediaAnalyzerService mediaAnalyzerService) {
        this.mediaAnalyzerService = mediaAnalyzerService;
        this.forecastProvider = new ForecastProvider();
    }

    @GetMapping("")
    public ResponseEntity<List<Summary>> getMedia(@RequestParam(value = "from", required = false) LocalDate from,
                                                  @RequestParam(value = "to", required = false) LocalDate to) {
        List<Summary> summaries = new ArrayList<>();
        summaries.add(getSummary(1l, null, null).getBody());
        summaries.add(getSummary(2l, null, null).getBody());
        summaries.add(getSummary(3l, null, null).getBody());
        summaries.add(getSummary(4l, null, null).getBody());

        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{mediaId}/state")
    public ResponseEntity<MediaSummaryPackage> getStates(@PathVariable("mediaId") Long mediaId,
                                                          @RequestParam("from") LocalDate from,
                                                          @RequestParam("to") LocalDate to) {

        MediaSummaryPackage mediaSummaryPackage = new MediaSummaryPackage();

        List<MediaState> mediaStates = mediaAnalyzerService.getMediaStates(mediaId, from, to);
        List<BillingPeriod> billingPeriods = mediaAnalyzerService.getBillingPeriods(from, to);

        MediaDetails media = mediaAnalyzerService.getMediaDetails(mediaId);
        List<MediaSummary> summaryList = prepareStates(media, mediaStates, billingPeriods);

        mediaSummaryPackage.setType(mediaId + "");
        mediaSummaryPackage.setFrom(from);
        mediaSummaryPackage.setTo(to);

        mediaSummaryPackage.setSummary(summaryList);

        return ResponseEntity.ok(mediaSummaryPackage);
    }

    @GetMapping("/{mediaId}/summary")
    public ResponseEntity<Summary> getSummary(@PathVariable("mediaId") Long mediaId,
                                              @RequestParam("from") LocalDate from,
                                              @RequestParam("to") LocalDate to) {

        MediaState mediaState = mediaAnalyzerService.getLastMediaState(mediaId);
        BillingPeriod billingPeriod = mediaAnalyzerService.getBillingPeriod(LocalDate.now());

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(Arrays.asList(billingPeriod));
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaMonthlyPrepaid mediaMonthlyPrepaid = billingPeriod.selectMediaPrepaid(mediaId);

        MediaDetails media = mediaAnalyzerService.getMediaDetails(mediaId);
        Summary summary = prepareSummary(media, mediaPaidValueCalculator, mediaState, mediaMonthlyPrepaid.getUnitPrice().doubleValue());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{mediaId}/statistics")
    public ResponseEntity<MediaStatistics> getStatistics(@PathVariable("mediaId") Long mediaId) {

        List<MediaState> states = mediaAnalyzerService.getMediaStates(mediaId, null, null);
        BillingPeriod billingPeriod = mediaAnalyzerService.getBillingPeriod(LocalDate.now());

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(Arrays.asList(billingPeriod));
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaMonthlyPrepaid mediaMonthlyPrepaid = billingPeriod.selectMediaPrepaid(mediaId);

        MediaStatistics statistics = prepareStatistics(mediaId,
                                                       mediaPaidValueCalculator,
                                                       mediaMonthlyPrepaid,
                                                       states);

        return ResponseEntity.ok(statistics);
    }

    private MediaStatistics prepareStatistics(Long mediaId, MediaPaidValueCalculator calculator, MediaMonthlyPrepaid mediaMonthlyPrepaid, List<MediaState> states) {
        MediaStatistics statistics = new MediaStatistics();

        MediaState actualState = states.get(states.size() - 1);
        double unitPrice = mediaMonthlyPrepaid.getUnitPrice().doubleValue();

        MediaDetails media = mediaAnalyzerService.getMediaDetails(mediaId);
        statistics.setForecast(forecastProvider.prepareForecast(states, mediaMonthlyPrepaid.getValue(), unitPrice));
        statistics.setSummary(prepareSummary(media, calculator, actualState, unitPrice));

        return statistics;
    }

    private List<MediaSummary> prepareStates(MediaDetails mediaDetails, List<MediaState> mediaStates, List<BillingPeriod> billingPeriods) {

        BillingPeriodProvider billingPeriodProvider = new BillingPeriodProviderImpl(billingPeriods);
        MediaPaidValueCalculator mediaPaidValueCalculator = new MediaPaidValueCalculator(billingPeriodProvider);

        MediaState actualState = mediaStates.get(mediaStates.size() - 1);
        if (actualState.getDate().isBefore(LocalDate.now()))
            mediaStates.add(new MediaState(actualState.getMediaId(), LocalDate.now(), null));

        List<MediaSummary> summaryList = mediaStates.stream()
                                                    .map(ms -> prepareState(mediaDetails, mediaPaidValueCalculator, ms))
                                                    .collect(Collectors.toList());

        return summaryList;
    }

    private MediaSummary prepareState(MediaDetails mediaDetails, MediaPaidValueCalculator mediaPaidValueCalculator, MediaState ms) {
        MediaSummary summary = new MediaSummary();
        summary.setDate(ms.getDate());
        summary.setUsed(ms.getValue());
        summary.setPaid(mediaPaidValueCalculator.ofDay(mediaDetails.getId(), ms.getDate()));
        return summary;
    }

    private Summary prepareSummary(MediaDetails mediaDetails, MediaPaidValueCalculator mediaPaidValueCalculator,
                                   MediaState ms, Double unitPrice) {
        Summary summary = new Summary();
        summary.setMedia(mediaDetails);
        summary.setLastStateDate(ms.getDate());
        summary.setUsed(ms.getValue());
        if (mediaDetails != null && MediaDetails.PaymentType.PREPAID.equals(mediaDetails.getPaymentType())) {
            summary.setPaidToday(mediaPaidValueCalculator.ofDay(mediaDetails.getId(), LocalDate.now()));
            Double paidForLastUsed = mediaPaidValueCalculator.ofDay(mediaDetails.getId(), ms.getDate());
            summary.setPaidLastState(paidForLastUsed);
            summary.setOverused(summary.getUsed() - paidForLastUsed);
            summary.setOverpaid(summary.getOverused() * unitPrice);
        }
        return summary;
    }

}