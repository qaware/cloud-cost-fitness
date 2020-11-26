package de.qaware.cce.aws.converter;

import de.qaware.cce.TimeSeries;
import de.qaware.cce.ValueWithUnit;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
            return LocalDate.parse(date, FORMATTER);
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
