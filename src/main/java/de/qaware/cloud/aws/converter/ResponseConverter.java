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
package de.qaware.cloud.aws.converter;

import de.qaware.cloud.TimeSeries;
import de.qaware.cloud.ValueWithUnit;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static de.qaware.cloud.ValueWithUnit.DATE_FORMATTER;

/**
 * Converter for responses from the AWS API
 */
public class ResponseConverter {
    /**
     * Extract all values (amount and unit) from the AWS API response
     *
     * @param result the AWS API response
     * @param key    the key needed to extract the correct data
     * @return a time series with the extracted data
     */
    public TimeSeries extractValues(List<ResultByTime> result, String key) {
        return new TimeSeries().withElements(result.stream()
                .map(data -> new ValueWithUnit(
                        convertStringToDate(data.timePeriod().start()),
                        convertAmountToDouble(data.total().get(key).amount()),
                        data.total().get(key).unit()
                ))
                .collect(Collectors.toList()));
    }

    private LocalDate convertStringToDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalStateException("Failed to convert date string to timestamp", e);
        }
    }

    private double convertAmountToDouble(String amount) {
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Failed to convert amount to double", e);
        }
    }
}
