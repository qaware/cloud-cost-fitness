/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
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
