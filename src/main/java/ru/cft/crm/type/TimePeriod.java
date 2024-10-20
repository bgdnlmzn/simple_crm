package ru.cft.crm.type;

import ru.cft.crm.exception.InvalidTimePeriodException;

public enum TimePeriod {
    DAY,
    MONTH,
    WEEK,
    QUARTER,
    YEAR;

    public static TimePeriod getTimePeriod(String period) {
        try {
            return TimePeriod.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTimePeriodException("Некорректный временной промежуток: " + period);
        }
    }
}
