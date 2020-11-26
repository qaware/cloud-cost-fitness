package de.qaware.cce;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class ValueWithUnit implements Comparable<ValueWithUnit> {
    private static final int NUMBER_DIGITS_CURRENCY = 2;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate dateFrom;
    private LocalDate dateTo;
    private double value;
    private String unit;

    public ValueWithUnit(LocalDate date, double value, String unit) {
        this(date, date, value, unit);
    }

    public ValueWithUnit(LocalDate dateFrom, LocalDate dateTo, double value, String unit) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.value = value;
        this.unit = unit;
    }

    /**
     * Sets the date and the date interval to one day
     *
     * @param date the date of the day
     */
    public void setDate(LocalDate date) {
        this.dateFrom = date;
        this.dateTo = date;
    }

    /**
     * Returns the date if the time interval corresponds to one day
     *
     * @return the date of the day
     * @throws IllegalStateException if the time interval is longer than one day
     */
    public LocalDate getDate() {
        if (dateFrom == null || dateTo == null) {
            return dateFrom;
        }
        if (dateFrom == dateTo) {
            return dateFrom;
        }
        throw new IllegalStateException("The value is associated with more than one day");
    }

    /**
     * Returns the beginning of the time interval
     *
     * @return date of the beginning
     */
    public LocalDate getDateFrom() {
        return dateFrom;
    }

    /**
     * Returns the end of the time interval
     *
     * @return date of the end
     */
    public LocalDate getDateTo() {
        return dateTo;
    }

    /**
     * Returns the length of the time intervals in days
     *
     * @return number of days
     */
    public int getNumDays() {
        return Period.between(dateFrom, dateTo).getDays() + 1;
    }

    /**
     * Sets the value
     *
     * @param value the value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the unit
     *
     * @param unit the unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets the unit
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Adds to values if they have the same unit and time interval
     *
     * @param other the other instance
     * @return a new instance with added values
     * @throws ArithmeticException if the units are different
     *                             if the date intervals are different
     */
    public ValueWithUnit add(ValueWithUnit other) {
        if (!hasSameUnit(other)) {
            throw new ArithmeticException("Units do not match");
        }
        if (!hasSameDateInterval(other)) {
            throw new ArithmeticException("Date intervals do not match");
        }
        return new ValueWithUnit(dateFrom, dateTo, value + other.getValue(), unit);
    }

    /**
     * Implementation of Comparable interface
     *
     * @param other the other instance
     * @return compared values
     * @throws ArithmeticException if the units are different
     */
    @Override
    public int compareTo(ValueWithUnit other) {
        if (hasSameUnit(other)) {
            return Double.compare(this.getValue(), other.getValue());
        }
        throw new ArithmeticException("Units do not match");
    }

    /**
     * Pretty-prints the time interval, the value, and the unit
     *
     * @return pretty-printed output
     */
    public String toString() {
        if (dateFrom == null || dateTo == null) {
            return valueToString();
        }

        if (dateFrom == dateTo) {
            return dateFrom.format(FORMATTER) + " : " + valueToString();
        }
        return dateFrom.format(FORMATTER) + " to " + dateTo.format(FORMATTER) + " : " + valueToString();
    }

    private String valueToString() {
        if (isCurrency()) {
            double factor = Math.pow(10.0, NUMBER_DIGITS_CURRENCY);
            return Math.round(value * factor) / factor + " " + unit;
        }
        return value + " " + unit;
    }

    private boolean isCurrency() {
        return Arrays.asList("EUR", "USD").contains(unit);
    }

    private boolean hasSameUnit(ValueWithUnit other) {
        return (this.getUnit() == null && other.getUnit() == null) || Objects.equals(this.getUnit(), other.getUnit());
    }

    private boolean hasSameDateInterval(ValueWithUnit other) {
        boolean hasSameStart = (this.getDateFrom() == null && other.getDateFrom() == null) || Objects.equals(this.getDateFrom(), other.getDateFrom());
        boolean hasSameEnd = (this.getDateTo() == null && other.getDateTo() == null) || Objects.equals(this.getDateTo(), other.getDateTo());
        return hasSameStart && hasSameEnd;
    }
}
