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
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate

class TimeSeriesSpec extends Specification {
    @Subject
    TimeSeries elements

    def setup() {
        elements = new TimeSeries().withElements([
                new ValueWithUnit(getDate("2020-11-09"), 1.0, "USD"),
                new ValueWithUnit(getDate("2020-11-10"), -99.812341324, "USD")
        ])
    }

    def "returns the number of elements"() {
        expect: "the number of elements to be correct"
        elements.size() == 2
    }

    def "converts the series nicely to String"() {
        when: "the elements are converted to String"
        def result = elements.toString()

        then: "it looks nice"
        result == "2020-11-09 : 1.0 USD\n2020-11-10 : -99.81 USD\n"
    }

    def "determines the minimum"() {
        expect: "the minimum is computed correctly"
        elements.min() == elements.getElements()[1]
    }

    def "determines the maximum"() {
        expect: "the maximum is computed correctly"
        elements.max() == elements.getElements()[0]
    }

    def "determines the average"() {
        when: "the average is computed"
        def result = elements.mean()

        then: "the average is computed correctly"
        result.getValue() == -49.406170662d
        result.getDateFrom() == getDate("2020-11-09")
        result.getDateTo() == getDate("2020-11-10")
        result.getUnit() == "USD"
    }

    def "determines the average if one date is null"() {
        when: "one date is null"
        elements.getElements().get(0).setDate(null)

        and: "the average is computed"
        def result = elements.mean()

        then: "the average is computed correctly"
        result.getValue() == -49.406170662d
        result.getDate() == null
        result.getUnit() == "USD"
    }

    def "throws an exception if the value list is empty when requesting the minimum"() {
        when: "the minimum is requested"
        elements.withElements([]).min()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the value list is empty when requesting the maximum"() {
        when: "the maximum is requested"
        elements.withElements([]).max()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the value list is empty when requesting the average"() {
        when: "the average is requested"
        elements.withElements([]).mean()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the units do not match when requesting the minimum"() {
        given: "different units"
        elements.getElements().get(1).setUnit("EUR")

        when: "the minimum is requested"
        elements.min()

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "throws an exception if the units do not match when requesting the maximum"() {
        given: "different units"
        elements.getElements().get(1).setUnit("EUR")

        when: "the maximum is requested"
        elements.max()

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "throws an exception if the units do not match when requesting the average"() {
        given: "different units"
        elements.getElements().get(1).setUnit("EUR")

        when: "the average is requested"
        elements.mean()

        then: "an exception is thrown"
        thrown IllegalStateException
    }

    def "adds two time series"() {
        given: "a second time series"
        def other = new TimeSeries().withElements([
                new ValueWithUnit(getDate("2020-11-09"), 42.0, "USD"),
                new ValueWithUnit(getDate("2020-11-10"), 3.14, "USD")
        ])

        when: "the two series are added"
        def result = elements.add(other)

        then: "they contain the correct data"
        result.getElements().every { it.unit == "USD" }
        result.getElements().collect { it.value } == [43.0d, -96.672341324]
    }

    def "adds two time series if one is empty"() {
        given: "a second empty time series"
        def other = new TimeSeries()

        when: "the two series are added"
        def result = other.add(elements)

        then: "they contain the correct data"
        result.getElements().every { it.unit == "USD" }
        result.getElements().collect { it.value } == [1.0d, -99.812341324]
    }

    @Unroll
    def "extrapolates to a future date"() {
        given: "a time series"
        def series = new TimeSeries().withElements([
                new ValueWithUnit(getDate("2020-11-09"), value1, "USD"),
                new ValueWithUnit(getDate("2020-11-10"), getDate("2020-11-11"), value2, "USD"),
                new ValueWithUnit(getDate("2020-11-12"), value3, "USD")
        ])

        when: "the extrapolated value is requested"
        def extrapolated = series.extrapolate(futureDate)

        then: "the value is correct (and possibly with some round-off errors)"
        Math.round(extrapolated.value) as double == result

        and: "the unit and date is correct"
        extrapolated.unit == "USD"
        extrapolated.dateFrom == extrapolated.dateTo
        extrapolated.date == getDate(futureDate)

        where:
        value1 | value2 | value3 | futureDate   | result
        15.0d  | 15.0d  | 15.0d  | "2020-12-24" | 15.0d
        10.0d  | 20.0d  | 10.0d  | "2020-12-24" | 15.0d
        10.0d  | 25.0d  | 40.0d  | "2020-11-12" | 40.0d
    }

    LocalDate getDate(String date) {
        LocalDate.parse(date, ValueWithUnit.DATE_FORMATTER)
    }
}
