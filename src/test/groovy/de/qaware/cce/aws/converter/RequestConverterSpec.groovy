package de.qaware.cce.aws.converter

import de.qaware.cce.TimeRange
import spock.lang.Specification

class RequestConverterSpec extends Specification {
    RequestConverter converter = new RequestConverter()

    def "creates a date interval of the past month"() {
        when: "the date interval is created"
        def interval = converter.createDateInterval(TimeRange.LAST_MONTH)

        then: "the interval is one month long"
        def start = interval.start() =~ /([0-9]{4})-([0-9]{2})-([0-9]{2})/
        def end = interval.end() =~ /([0-9]{4})-([0-9]{2})-([0-9]{2})/
        if (end[0][2] == "01") {
            start[0][1] as int == (end[0][1] as int) - 1
            start[0][2] == "12"
        } else {
            start[0][1] == end[0][1]
            start[0][2] as int == (end[0][2] as int) - 1
        }
    }
}
