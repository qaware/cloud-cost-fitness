/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws.converter;

import de.qaware.cce.TimeSeries;
import de.qaware.cce.ValueWithUnit;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

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
            return LocalDate.parse(date, ValueWithUnit.DATE_FORMATTER);
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
