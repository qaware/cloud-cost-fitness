/*
 *    Copyright (C) 2021 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.cloud.cost.aws.converter;

import de.qaware.cloud.cost.TimeRange;
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
