package de.qaware.cce.aws.data

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ValueWithUnitSpec extends Specification {
    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    def "compares values correctly"() {
        given: "two values"
        def value1 = new ValueWithUnit(null, 100.0, "Birnen")
        def value2 = new ValueWithUnit(null, 42.0, "Birnen")
        def value3 = new ValueWithUnit(null, 42.0, "Birnen")

        expect: "they are compared as expected"
        value1 > value2
        value2 == value3
    }

    def "does not compare values if the units do not match"() {
        given: "two values"
        def value1 = new ValueWithUnit(null, 42.0, "USD")
        def value2 = new ValueWithUnit(null, 40.0, "EUR")

        when: "they are compared"
        value1 < value2

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "correctly adds to values"() {
        given: "two values"
        def date = LocalDate.parse("2020-11-09", FORMATTER)
        def value1 = new ValueWithUnit(date, 42.0, "USD")
        def value2 = new ValueWithUnit(date, 40.0, "USD")

        when: "they are added"
        def result = value1.add(value2)

        then: "the sum has the correct value"
        result.value == 82.0d
    }

    @Unroll
    def "throws an exception if date or unit does not match when adding to values"() {
        given: "two values"
        def date1 = LocalDate.parse("2020-11-09", FORMATTER)
        def value1 = new ValueWithUnit(date1, 42.0, "USD")
        def value2 = new ValueWithUnit(date2, 40.0, unit2)

        when: "they are added"
        def result = value1.add(value2)

        then: "the sum has the correct value"
        thrown ArithmeticException

        where:
        date2                                    | unit2
        null                                     | "USD"
        LocalDate.parse("2020-11-09", FORMATTER) | "EUR"
    }

    @Unroll
    def "returns the time interval length correctly"() {
        given: "a values"
        def value = new ValueWithUnit(LocalDate.parse("2020-11-09", FORMATTER), LocalDate.parse(dateTo, FORMATTER), 42.0, "USD")

        expect: "the number of days to be extracted correctly"
        value.getNumDays() == numDays

        where:
        dateTo       | numDays
        "2020-11-09" | 1
        "2020-11-10" | 2
        "2020-12-01" | 23
    }

    @Unroll
    def "converts nicely to String"() {
        given: "a value at a given date"
        def dateFrom = LocalDate.parse("2020-11-09", FORMATTER)
        def value = new ValueWithUnit(dateFrom, dateFrom.plusDays(numDays), 42.0, "Birnen")

        expect: "it is converted to String"
        value.toString() == result

        where:
        numDays | result
        0       | "2020-11-09 : 42.0 Birnen"
        1       | "2020-11-09 to 2020-11-10 : 42.0 Birnen"
        30      | "2020-11-09 to 2020-12-09 : 42.0 Birnen"
    }

    def "converts to String if date is null"() {
        given: "a value with null date"
        def value = new ValueWithUnit(null, 42.0, "Birnen")

        when: "it is converted to String"
        def result = value.toString()

        then: "it looks nice and correct"
        result == "42.0 Birnen"
    }

    @Unroll
    def "rounds when converting to String"() {
        given: "a value with many digits"
        def value = new ValueWithUnit(null, 42.12645678, unit)

        expect: "the value is rounded correctly"
        value.toString() == result

        where:
        unit     | result
        "Birnen" | "42.12645678 Birnen"
        "EUR"    | "42.13 EUR"
    }
}
