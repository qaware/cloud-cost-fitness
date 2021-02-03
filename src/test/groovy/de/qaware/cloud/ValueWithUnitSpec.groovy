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
package de.qaware.cloud

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static de.qaware.cloud.ValueWithUnit.DATE_FORMATTER

class ValueWithUnitSpec extends Specification {
    def "compares values correctly"() {
        given: "two values"
        def value1 = new ValueWithUnit(100.0, "Birnen")
        def value2 = new ValueWithUnit(42.0, "Birnen")
        def value3 = new ValueWithUnit(42.0, "Birnen")

        expect: "they are compared as expected"
        value1 > value2
        value2 == value3
    }

    def "does not compare values if the units do not match"() {
        given: "two values"
        def value1 = new ValueWithUnit(42.0, "USD")
        def value2 = new ValueWithUnit(40.0, "EUR")

        when: "they are compared"
        value1 < value2

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "checks if a value is less than the other"() {
        given: "two values"
        def value1 = new ValueWithUnit(100.0, "Birnen")
        def value2 = new ValueWithUnit(42.0, "Birnen")

        expect: "the correct relation"
        !value1.lessThan(value2)
        value2.lessThan(value1)
        !value2.lessThan(value2)
        value1.lessThan(101.0)
        !value1.lessThan(100.0)
    }

    def "correctly adds to values"() {
        given: "two values"
        def date = LocalDate.parse("2020-11-09", DATE_FORMATTER)
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
        def date1 = LocalDate.parse("2020-11-09", DATE_FORMATTER)
        def value1 = new ValueWithUnit(date1, 42.0, "USD")
        def value2 = new ValueWithUnit(date2, 40.0, unit2)

        when: "they are added"
        def result = value1.add(value2)

        then: "the sum has the correct value"
        thrown ArithmeticException

        where:
        date2                                         | unit2
        null                                          | "USD"
        LocalDate.parse("2020-11-09", DATE_FORMATTER) | "EUR"
    }

    @Unroll
    def "returns the time interval length correctly"() {
        given: "a values"
        def value = new ValueWithUnit(LocalDate.parse("2020-11-09", DATE_FORMATTER), LocalDate.parse(dateTo, DATE_FORMATTER), 42.0, "USD")

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
        def dateFrom = LocalDate.parse("2020-11-09", DATE_FORMATTER)
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
