package de.qaware.cce.aws.converter

import software.amazon.awssdk.services.costexplorer.model.DateInterval
import software.amazon.awssdk.services.costexplorer.model.MetricValue
import software.amazon.awssdk.services.costexplorer.model.ResultByTime
import spock.lang.Specification

import java.time.format.DateTimeFormatter

class ResponseConverterSpec extends Specification {
    ResponseConverter converter = new ResponseConverter()
    List<ResultByTime> resultsByTime

    def setup() {
        resultsByTime = [
                ResultByTime.builder()
                    .timePeriod(DateInterval.builder()
                                .start("2020-08-01")
                                .end("2020-08-02")
                                .build())
                        .total(Collections.singletonMap("blended",
                                MetricValue.builder()
                                .amount("10")
                                .unit("EUR")
                                .build()))
                        .build(),
                ResultByTime.builder()
                        .timePeriod(DateInterval.builder()
                                .start("2020-09-01")
                                .end("2020-09-02")
                                .build())
                        .total(Collections.singletonMap("blended",
                                MetricValue.builder()
                                        .amount("-1")
                                        .unit("USD")
                                        .build()))
                        .build()
        ]
    }

    def "extracts values with dates correctly"() {
        when: "the converter is invoked"
        def result = converter.extractValues(resultsByTime, "blended")

        then: "the dates and values are parsed correctly"
        result.getElements()[0].getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  == "2020-08-01"
        result.getElements()[0].getValue() == 10.0d
        result.getElements()[0].getUnit() == "EUR"
        result.getElements()[1].getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  == "2020-09-01"
        result.getElements()[1].getValue() == -1.0d
        result.getElements()[1].getUnit() == "USD"
    }
}
