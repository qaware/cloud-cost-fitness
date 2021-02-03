/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cloud.aws.converter;

import de.qaware.cloud.TimeRange;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Converter for request towards AWS API
 */
public class RequestConverter {
    /**
     * Creates a date interval for the AWS API
     *
     * @param timeRange the time range for the beginning of the interval
     * @return a date interval from the past to now
     */
    public DateInterval createDateInterval(TimeRange timeRange) {
        return createDateIntervalFrom(timeRange, LocalDate.now());
    }

    protected DateInterval createDateIntervalFrom(TimeRange timeRange, LocalDate fromDate) {
        String startDateString = "";

        switch (timeRange) {
            case LAST_6_MONTHS:
                startDateString = fromDate.minusMonths(6).format(DateTimeFormatter.ISO_DATE);
                break;
            case LAST_30_DAYS:
                startDateString = fromDate.minusMonths(1).format(DateTimeFormatter.ISO_DATE);
                break;
            case LAST_7_DAYS:
                startDateString = fromDate.minusDays(7).format(DateTimeFormatter.ISO_DATE);
                break;
            case YESTERDAY:
                startDateString = fromDate.minusDays(1).format(DateTimeFormatter.ISO_DATE);
        }

        String endDateString = fromDate.format(DateTimeFormatter.ISO_DATE);
        return DateInterval.builder()
                .start(startDateString)
                .end(endDateString)
                .build();
    }
}
