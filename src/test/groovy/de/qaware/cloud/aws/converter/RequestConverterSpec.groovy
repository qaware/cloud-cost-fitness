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
package de.qaware.cloud.aws.converter

import de.qaware.cloud.TimeRange
import de.qaware.cloud.ValueWithUnit
import software.amazon.awssdk.services.costexplorer.model.DateInterval
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate

class RequestConverterSpec extends Specification {
    @Subject
    RequestConverter converter = new RequestConverter()

    @Unroll
    def "creates date intervals"() {
        given: "a date"
        def date = parseDate("2020-10-10")

        when: "a date interval is created"
        DateInterval interval = converter.createDateIntervalFrom(timeRange, date)

        then: "the start date is correct"
        interval.start() == start
        interval.end() == "2020-10-10"

        where:
        timeRange               | start
        TimeRange.LAST_6_MONTHS | "2020-04-10"
        TimeRange.LAST_30_DAYS  | "2020-09-10"
        TimeRange.LAST_7_DAYS   | "2020-10-03"
        TimeRange.YESTERDAY     | "2020-10-09"
    }

    private static LocalDate parseDate(String date) {
        return LocalDate.parse(date, ValueWithUnit.DATE_FORMATTER);
    }
}
