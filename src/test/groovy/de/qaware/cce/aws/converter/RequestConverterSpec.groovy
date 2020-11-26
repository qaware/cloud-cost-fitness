package de.qaware.cce.aws.converter

import de.qaware.cce.TimeRange
import software.amazon.awssdk.services.costexplorer.model.DateInterval
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RequestConverterSpec extends Specification {
    RequestConverter converter = new RequestConverter()

    @Unroll
    def "creates date intervals correctly"() {
        given: "a date"
        def date = parseDate("2020-11-26")

        when: "a date interval is created"
        DateInterval interval = converter.createDateIntervalFrom(timeRange, date)

        then: "the start date is correct"
        interval.start() == start
        interval.end() == "2020-11-26"

        where:
        timeRange                 | start
        TimeRange.LAST_SIX_MONTHS | "2020-05-26"
        TimeRange.LAST_MONTH      | "2020-10-26"
        TimeRange.LAST_WEEK       | "2020-11-19"
        TimeRange.YESTERDAY       | "2020-11-25"
    }

    private static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
