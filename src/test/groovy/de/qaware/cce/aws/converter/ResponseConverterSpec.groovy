/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws.converter

import de.qaware.cce.ValueWithUnit
import software.amazon.awssdk.services.costexplorer.model.DateInterval
import software.amazon.awssdk.services.costexplorer.model.MetricValue
import software.amazon.awssdk.services.costexplorer.model.ResultByTime
import spock.lang.Specification

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
        result.getElements()[0].getDate().format(ValueWithUnit.DATE_FORMATTER)  == "2020-08-01"
        result.getElements()[0].getValue() == 10.0d
        result.getElements()[0].getUnit() == "EUR"
        result.getElements()[1].getDate().format(ValueWithUnit.DATE_FORMATTER)  == "2020-09-01"
        result.getElements()[1].getValue() == -1.0d
        result.getElements()[1].getUnit() == "USD"
    }
}
