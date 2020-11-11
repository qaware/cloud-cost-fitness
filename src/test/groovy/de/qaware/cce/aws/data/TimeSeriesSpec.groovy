package de.qaware.cce.aws.data

import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeSeriesSpec extends Specification {
    TimeSeries values

    def setup() {
        values = new TimeSeries().withValues([
                new ValueWithUnit(getDate("2020-11-09"), 1.0, "USD"),
                new ValueWithUnit(getDate("2020-11-10"), -99.812341324, "USD")
        ])
    }

    def "converts values nicely to String"() {
        when: "the values are converted to String"
        def result = values.toString()

        then: "it looks nice"
        result == "2020-11-09 : 1.0 USD\n2020-11-10 : -99.81 USD\n"
    }

    def "determines the minimum"() {
        expect: "the minimum is computed correctly"
        values.min() == values.getElements()[1]
    }

    def "determines the maximum"() {
        expect: "the maximum is computed correctly"
        values.max() == values.getElements()[0]
    }

    def "determines the average"() {
        when: "the average is computed"
        def result = values.mean()

        then: "the average is computed correctly"
        result.getValue() == -49.406170662d
        result.getDateFrom() == getDate("2020-11-09")
        result.getDateTo() == getDate("2020-11-10")
        result.getUnit() == "USD"
    }

    def "determines the average if one date is null"() {
        when: "one date is null"
        values.getElements().get(0).setDate(null)

        and: "the average is computed"
        def result = values.mean()

        then: "the average is computed correctly"
        result.getValue() == -49.406170662d
        result.getDate() == null
        result.getUnit() == "USD"
    }

    def "throws an exception if the value list is empty when requesting the minimum"() {
        when: "the minimum is requested"
        values.withValues([]).min()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the value list is empty when requesting the maximum"() {
        when: "the maximum is requested"
        values.withValues([]).max()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the value list is empty when requesting the average"() {
        when: "the average is requested"
        values.withValues([]).mean()

        then: "an exception is thrown"
        thrown NoSuchElementException
    }

    def "throws an exception if the units do not match when requesting the minimum"() {
        given: "different units"
        values.getElements().get(1).setUnit("EUR")

        when: "the minimum is requested"
        values.min()

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "throws an exception if the units do not match when requesting the maximum"() {
        given: "different units"
        values.getElements().get(1).setUnit("EUR")

        when: "the maximum is requested"
        values.max()

        then: "an exception is thrown"
        thrown ArithmeticException
    }

    def "throws an exception if the units do not match when requesting the average"() {
        given: "different units"
        values.getElements().get(1).setUnit("EUR")

        when: "the average is requested"
        values.mean()

        then: "an exception is thrown"
        thrown IllegalStateException
    }

    def "adds two time series"() {
        given: "a second time series"
        def other = new TimeSeries().withValues([
                new ValueWithUnit(getDate("2020-11-09"), 42.0, "USD"),
                new ValueWithUnit(getDate("2020-11-10"), 3.14, "USD")
        ])

        when: "the two series are added"
        def result = values.add(other)

        then: "they contain the correct data"
        result.getElements().every { it.unit == "USD" }
        result.getElements().collect { it.value } == [43.0d, -96.672341324]
    }

    LocalDate getDate(String date) {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
