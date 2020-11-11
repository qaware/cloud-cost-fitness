package de.qaware.cce.aws.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeSeries {
    private List<ValueWithUnit> elements = new ArrayList<>();

    /**
     * Insert elements into the time series
     *
     * @param elements list of elements to be inserted
     * @return the time series
     */
    public TimeSeries withElements(List<ValueWithUnit> elements) {
        this.elements = elements;
        return this;
    }

    /**
     * Returns the elements of the time series
     *
     * @return list of elements
     */
    public List<ValueWithUnit> getElements() {
        return elements;
    }

    /**
     * Returns the number of elements in the time series
     *
     * @return number of elements
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Extracts the element with the maximum value
     *
     * @return the element with the maximum value
     * @throws NoSuchElementException if the time series is empty
     */
    public ValueWithUnit max() {
        return elements.stream()
                .max(ValueWithUnit::compareTo)
                .orElseThrow(NoSuchElementException::new);
    }


    /**
     * Extracts the element with the minimum value
     *
     * @return the element with the minimum value
     * @throws NoSuchElementException if the time series is empty
     */
    public ValueWithUnit min() {
        return elements.stream()
                .min(ValueWithUnit::compareTo)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Computes the average value
     *
     * @return the average value with the approriate time interval of the time series
     * @throws NoSuchElementException if the time series is empty
     * @throws IllegalStateException  if the units of the elements do not match
     */
    public ValueWithUnit mean() {
        ValueWithUnit sum = sum();
        sum.setValue(sum().getValue() / elements.size());
        return sum;
    }

    /**
     * Computes the sum of all values
     *
     * @return the sum with the appropriate time interval of the time series
     * @throws NoSuchElementException if the time series is empty
     * @throws IllegalStateException  if the units of the elements do not match
     */
    public ValueWithUnit sum() {
        if (elements.size() == 0) {
            throw new NoSuchElementException();
        } else if (elements.size() == 1) {
            return elements.get(0);
        }

        if (!haveAllSameUnit()) {
            throw new IllegalStateException("Units do not match");
        }

        double sum = elements.stream()
                .map(ValueWithUnit::getValue)
                .reduce(Double::sum)
                .orElseThrow(NoSuchElementException::new);

        LocalDate dateFrom = elements.get(0).getDate();
        LocalDate dateTo = elements.get(elements.size() - 1).getDate();
        return new ValueWithUnit(dateFrom, dateTo, sum, elements.get(0).getUnit());
    }

    /**
     * Zips two time series and adds all the values
     *
     * @param other the other time series
     * @return a new time series with added values
     * @throws ArithmeticException if the time series have different number of elements or
     *                             if the units do not match or
     *                             if the dates of the elements do not match
     */
    public TimeSeries add(TimeSeries other) {
        if (elements.size() != other.getSize()) {
            throw new ArithmeticException("Time series lengths do not match");
        }

        TimeSeries sum = new TimeSeries();
        return sum.withElements(IntStream.range(0, elements.size())
                .mapToObj(index -> elements.get(index).add(other.getElements().get(index)))
                .collect(Collectors.toList()));
    }

    /**
     * Pretty-prints the time series
     *
     * @return pretty-printed output
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        elements.stream()
                .map(ValueWithUnit::toString)
                .forEach(item -> stringBuilder.append(item)
                        .append("\n"));
        return stringBuilder.toString();
    }

    private boolean haveAllSameUnit() {
        return elements.stream().map(ValueWithUnit::getUnit).collect(Collectors.toSet()).size() == 1;
    }
}