package de.qaware.cce.aws.converter;

import de.qaware.cce.TimeRange;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RequestConverter {
    /**
     * Creates a date interval for the AWS API
     *
     * @param timeRange the time range for the beginning of the interval
     * @return a date interval from the past to now
     */
    public DateInterval createDateInterval(TimeRange timeRange) {
        LocalDate startDate = LocalDate.now();
        String startDateString = "";

        switch (timeRange) {
            case LAST_MONTH:
                startDateString = startDate.minusMonths(1).format(DateTimeFormatter.ISO_DATE);
                break;
            case LAST_SIX_MONTHS:
                startDateString = startDate.minusMonths(6).format(DateTimeFormatter.ISO_DATE);
                break;
            case LAST_WEEK:
                startDateString = startDate.minusDays(7).format(DateTimeFormatter.ISO_DATE);
                break;
        }

        String endDateString = LocalDate.now()
                .format(DateTimeFormatter.ISO_DATE);
        return DateInterval.builder()
                .start(startDateString)
                .end(endDateString)
                .build();
    }
}
