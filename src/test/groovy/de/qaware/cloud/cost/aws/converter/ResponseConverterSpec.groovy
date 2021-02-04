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
package de.qaware.cloud.cost.aws.converter

import de.qaware.cloud.cost.ValueWithUnit
import software.amazon.awssdk.services.costexplorer.model.DateInterval
import software.amazon.awssdk.services.costexplorer.model.MetricValue
import software.amazon.awssdk.services.costexplorer.model.ResultByTime
import spock.lang.Specification
import spock.lang.Subject

class ResponseConverterSpec extends Specification {
    @Subject
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

    def "extracts values with dates"() {
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
